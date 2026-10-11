package com.framework.util;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Sprint 7bis : cree un objet et remplit ses attributs avec les parametres de requete du meme nom.
 * Ex. : un champ "nom" du formulaire remplit l'attribut "nom" de l'objet.
 * Un attribut sans parametre correspondant garde sa valeur par defaut. Seuls les attributs de type
 * simple (String, nombres, boolean, LocalDate) sont remplis.
 */
public class ObjectBinder {

    public static Object bind(Class<?> type, HttpServletRequest req) {
        Object object = newInstance(type);
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod) || !ParamUtil.isSimple(field.getType())) {
                    continue;
                }
                String raw = req.getParameter(field.getName());
                if (raw == null) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    field.set(object, ParamUtil.convert(raw, field.getType()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("attribut \"" + field.getName() + "\" : " + e.getMessage());
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("attribut \"" + field.getName() + "\" inaccessible");
                }
            }
        }
        return object;
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
