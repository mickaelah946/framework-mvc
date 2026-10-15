package com.framework.util;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sprint 7bis : cree un objet et remplit ses attributs avec les parametres de requete.
 *
 * Noms de parametres (le prefixe est le nom de l'argument, ou la valeur de @Param) :
 *   - attribut simple          : employe.nom  (ou nom, sans prefixe)
 *   - objet dans l'objet       : employe.adresse.rue
 *   - liste d'objets           : employes[0].nom, employes[1].nom, ...
 *   - liste de types simples   : tags[0], tags[1]  (ou plusieurs champs "tags" de meme nom)
 *   - deux objets en argument  : employe.nom et departement.nom (chacun garde son prefixe)
 *
 * Sans prefixe dans le formulaire ("nom"), l'attribut est rempli quand meme (sprint 7bis de base),
 * sauf si le formulaire utilise deja le prefixe de l'argument (employe.xxx) : dans ce cas seuls les
 * champs prefixes sont lus, pour que deux objets aux attributs de meme nom ne se melangent pas.
 * Un attribut sans parametre correspondant garde sa valeur par defaut.
 */
public class ObjectBinder {

    /** Profondeur maximale d'objets imbriques (protege des classes qui se referencent). */
    private static final int MAX_DEPTH = 5;

    /** Sprint 7bis de base : sans prefixe (champs "nom", "prenom", ...). */
    public static Object bind(Class<?> type, HttpServletRequest req) {
        return bind(type, "", req);
    }

    /** Objet rempli avec le prefixe donne (nom de l'argument ou @Param) ; prefixe vide : champs sans prefixe. */
    public static Object bind(Class<?> type, String name, HttpServletRequest req) {
        String prefix = name == null ? "" : name;
        boolean allowBare = prefix.isEmpty() || !hasParamStartingWith(req, prefix + ".");
        return bindObject(type, prefix, allowBare, 0, req);
    }

    /** Liste (List&lt;Employe&gt;, List&lt;String&gt;...) : elementType vient du type generique de l'argument. */
    public static List<Object> bindList(Type listType, String name, HttpServletRequest req) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("nom d'argument illisible pour la liste : utiliser @Param(\"...\") "
                    + "ou compiler l'application avec javac -parameters");
        }
        return bindList(elementType(listType, name), name, 0, req);
    }

    // ---------- objets ----------

    private static Object bindObject(Class<?> type, String prefix, boolean allowBare, int depth,
                                     HttpServletRequest req) {
        if (depth > MAX_DEPTH) {
            return null;
        }
        Object object = newInstance(type);
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod) || field.isSynthetic()) {
                    continue;
                }
                String key = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();
                Object value = readField(field, key, allowBare, depth, req);
                if (value != null) {
                    set(object, field, value, key);
                }
            }
        }
        return object;
    }

    /** Valeur de l'attribut lue dans la requete, ou null s'il n'y a rien pour lui (il garde alors sa valeur par defaut). */
    private static Object readField(Field field, String key, boolean allowBare, int depth, HttpServletRequest req) {
        Class<?> type = field.getType();
        String bare = field.getName();
        boolean tryBare = allowBare && !bare.equals(key);

        if (ParamUtil.isSimple(type)) {
            String raw = req.getParameter(key);
            if (raw == null && tryBare) {
                raw = req.getParameter(bare);
            }
            return raw == null ? null : convert(raw, type, key);
        }
        if (List.class.isAssignableFrom(type)) {
            Class<?> item = elementType(field.getGenericType(), key);
            List<Object> list = bindList(item, key, depth + 1, req);
            if (list.isEmpty() && tryBare) {
                list = bindList(item, bare, depth + 1, req);
            }
            return list.isEmpty() ? null : list;
        }
        // objet dans l'objet : employe.adresse.rue
        if (hasParamStartingWith(req, key + ".")) {
            return bindObject(type, key, false, depth + 1, req);
        }
        if (tryBare && hasParamStartingWith(req, bare + ".")) {
            return bindObject(type, bare, false, depth + 1, req);
        }
        return null;
    }

    // ---------- listes ----------

    private static final Pattern INDEX = Pattern.compile("\\[(\\d+)\\]");

    private static List<Object> bindList(Class<?> item, String key, int depth, HttpServletRequest req) {
        List<Object> list = new ArrayList<>();
        boolean simple = ParamUtil.isSimple(item);
        for (int index : indexesOf(req, key)) {
            String itemKey = key + "[" + index + "]";
            if (simple) {
                String raw = req.getParameter(itemKey);
                if (raw != null) {
                    list.add(convert(raw, item, itemKey));
                }
            } else if (hasParamStartingWith(req, itemKey + ".")) {
                Object o = bindObject(item, itemKey, false, depth + 1, req);
                if (o != null) {
                    list.add(o);
                }
            }
        }
        // liste de types simples sans indices : plusieurs champs de meme nom (cases a cocher, select multiple)
        if (list.isEmpty() && simple) {
            String[] values = req.getParameterValues(key);
            if (values != null) {
                for (String raw : values) {
                    list.add(convert(raw, item, key));
                }
            }
        }
        return list;
    }

    /** Indices trouves dans les noms de parametres : employes[0].nom, employes[2].nom -> 0, 2 (trous acceptes). */
    private static Set<Integer> indexesOf(HttpServletRequest req, String key) {
        Set<Integer> indexes = new TreeSet<>();
        String start = key + "[";
        for (String name : req.getParameterMap().keySet()) {
            if (name.startsWith(start)) {
                Matcher m = INDEX.matcher(name.substring(key.length()));
                if (m.lookingAt()) {
                    indexes.add(Integer.valueOf(m.group(1)));
                }
            }
        }
        return indexes;
    }

    // ---------- outils ----------

    private static boolean hasParamStartingWith(HttpServletRequest req, String start) {
        for (String name : req.getParameterMap().keySet()) {
            if (name.startsWith(start)) {
                return true;
            }
        }
        return false;
    }

    /** Type des elements de List&lt;T&gt; ; erreur si la liste n'a pas de type (List sans &lt;T&gt;). */
    private static Class<?> elementType(Type listType, String name) {
        if (listType instanceof ParameterizedType) {
            Type[] args = ((ParameterizedType) listType).getActualTypeArguments();
            if (args.length == 1 && args[0] instanceof Class) {
                return (Class<?>) args[0];
            }
        }
        throw new IllegalArgumentException("liste \"" + name + "\" : le type des elements est inconnu "
                + "(ecrire List<Employe> et non List)");
    }

    private static Object convert(String raw, Class<?> type, String key) {
        try {
            return ParamUtil.convert(raw, type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("attribut \"" + key + "\" : " + e.getMessage());
        }
    }

    private static void set(Object object, Field field, Object value, String key) {
        try {
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("attribut \"" + key + "\" inaccessible");
        }
    }

    private static Object newInstance(Class<?> type) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("la classe " + type.getSimpleName() + " doit avoir un constructeur sans argument");
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("impossible de creer un " + type.getSimpleName());
        }
    }
}
