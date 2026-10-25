package com.framework.util;

import com.framework.model.MapFile;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
 *   - List, Set et tableaux (Employe[], int[]) se lisent de la meme facon
 *   - deux objets en argument  : employe.nom et departement.nom (chacun garde son prefixe)
 *
 * Sans prefixe dans le formulaire ("nom"), l'attribut est rempli quand meme (sprint 7bis de base),
 * sauf si le formulaire utilise deja le prefixe de l'argument (employe.xxx) : dans ce cas seuls les
 * champs prefixes sont lus. Quand la methode recoit plusieurs objets, le prefixe est obligatoire
 * (aucune valeur sans prefixe n'est partagee entre deux objets : pas d'ambiguite).
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
        return bind(type, name, req, false);
    }

    /**
     * Comme bind ; avec strict = true (methode qui recoit plusieurs objets) le prefixe est obligatoire :
     * les champs sans prefixe ne sont lus pour aucun des objets.
     */
    public static Object bind(Class<?> type, String name, HttpServletRequest req, boolean strict) {
        String prefix = name == null ? "" : name;
        if (strict && prefix.isEmpty()) {
            throw new IllegalArgumentException("la methode recoit plusieurs objets : utiliser @Param(\"...\") "
                    + "ou compiler l'application avec javac -parameters (prefixe " + type.getSimpleName()
                    + ".champ obligatoire)");
        }
        boolean allowBare = !strict && (prefix.isEmpty() || !hasParamStartingWith(req, prefix + "."));
        return bindObject(type, prefix, allowBare, 0, req);
    }

    /**
     * Sprint 8 : type lu dans les fichiers envoyes (MapFile, byte[] ou Map<String, MapFile|byte[]>),
     * et non dans les champs de texte du formulaire.
     */
    public static boolean isFileType(Class<?> type) {
        return type == MapFile.class || type == byte[].class || Map.class.isAssignableFrom(type);
    }

    /** List, Set ou tableau (argument ou attribut) : vrai si bindMultiple sait le lire. */
    public static boolean isMultiple(Class<?> type) {
        return type.isArray() || Collection.class.isAssignableFrom(type);
    }

    /**
     * List&lt;Employe&gt;, Set&lt;String&gt;, Employe[], int[]... : le type des elements vient du type generique
     * (ou du type du tableau). Retourne le conteneur du type demande, vide si le formulaire n'envoie rien.
     */
    public static Object bindMultiple(Type genericType, Class<?> rawType, String name, HttpServletRequest req) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("nom d'argument illisible pour la liste : utiliser @Param(\"...\") "
                    + "ou compiler l'application avec javac -parameters");
        }
        return adapt(rawType, bindList(elementType(genericType, rawType, name), name, 0, req), name);
    }

    /** Liste (List&lt;Employe&gt;, List&lt;String&gt;...) : elementType vient du type generique de l'argument. */
    public static List<Object> bindList(Type listType, String name, HttpServletRequest req) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("nom d'argument illisible pour la liste : utiliser @Param(\"...\") "
                    + "ou compiler l'application avec javac -parameters");
        }
        return bindList(elementType(listType, List.class, name), name, 0, req);
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

        // Sprint 8 : fichier envoye (MapFile) ou ses octets (byte[]) ; rien d'envoye : l'attribut garde sa valeur par defaut
        if (type == MapFile.class) {
            MapFile file = UploadUtil.file(req, key);
            return (file == null && tryBare) ? UploadUtil.file(req, bare) : file;
        }
        if (type == byte[].class) {
            byte[] bytes = UploadUtil.bytes(req, key);
            return (bytes == null && tryBare) ? UploadUtil.bytes(req, bare) : bytes;
        }
        if (ParamUtil.isSimple(type)) {
            String raw = req.getParameter(key);
            if (raw == null && tryBare) {
                raw = req.getParameter(bare);
            }
            return raw == null ? null : convert(raw, type, key);
        }
        if (isMultiple(type)) {
            Class<?> item = elementType(field.getGenericType(), type, key);
            List<Object> list = bindList(item, key, depth + 1, req);
            if (list.isEmpty() && tryBare) {
                list = bindList(item, bare, depth + 1, req);
            }
            return list.isEmpty() ? null : adapt(type, list, key);
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
        // Sprint 8 : List<MapFile> / MapFile[] : tous les fichiers du champ (input file avec l'attribut multiple)
        if (item == MapFile.class) {
            return new ArrayList<Object>(UploadUtil.files(req, key));
        }
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

    /** Vrai si un champ de texte ou un fichier envoye a un nom qui commence par start (ex. "employe."). */
    private static boolean hasParamStartingWith(HttpServletRequest req, String start) {
        for (String name : req.getParameterMap().keySet()) {
            if (name.startsWith(start)) {
                return true;
            }
        }
        return UploadUtil.hasFileStartingWith(req, start);
    }

    /** Type des elements de List&lt;T&gt; / Set&lt;T&gt; ou d'un tableau ; erreur si le type est inconnu (List sans &lt;T&gt;). */
    private static Class<?> elementType(Type genericType, Class<?> rawType, String name) {
        if (rawType.isArray()) {
            return rawType.getComponentType();
        }
        if (genericType instanceof ParameterizedType) {
            Type[] args = ((ParameterizedType) genericType).getActualTypeArguments();
            if (args.length == 1 && args[0] instanceof Class) {
                return (Class<?>) args[0];
            }
        }
        throw new IllegalArgumentException("liste \"" + name + "\" : le type des elements est inconnu "
                + "(ecrire List<Employe> et non List)");
    }

    /** Met les elements dans le conteneur demande : tableau, List/Collection (ArrayList) ou Set (LinkedHashSet). */
    private static Object adapt(Class<?> type, List<Object> items, String name) {
        if (type.isArray()) {
            Object array = Array.newInstance(type.getComponentType(), items.size());
            for (int i = 0; i < items.size(); i++) {
                Array.set(array, i, items.get(i));
            }
            return array;
        }
        if (type.isAssignableFrom(ArrayList.class)) {
            return items;
        }
        if (type.isAssignableFrom(LinkedHashSet.class)) {
            return new LinkedHashSet<>(items);
        }
        throw new IllegalArgumentException("\"" + name + "\" : type de collection non supporte ("
                + type.getSimpleName() + ") ; utiliser List, Set ou un tableau");
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
