package com.framework.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Sprint 7 : conversion d'un parametre de requete (texte) vers le type d'un argument de methode. */
public class ParamUtil {

    /**
     * Convertit le texte recu vers le type demande.
     * Parametre absent (null) : null pour un objet, 0 / false pour un type primitif.
     * Champ vide : meme chose, sauf pour String qui garde la chaine vide.
     * Types pris en charge : String, int, long, double, float, short, boolean (et leurs classes enveloppes), LocalDate.
     */
    public static Object convert(String raw, Class<?> type) {
        if (type == String.class) {
            return raw;
        }
        if (raw == null || raw.trim().isEmpty()) {
            return defaultValue(type);
        }
        String text = raw.trim();
        try {
            if (type == int.class || type == Integer.class) {
                return Integer.valueOf(text);
            }
            if (type == long.class || type == Long.class) {
                return Long.valueOf(text);
            }
            if (type == double.class || type == Double.class) {
                return Double.valueOf(text.replace(',', '.'));
            }
            if (type == float.class || type == Float.class) {
                return Float.valueOf(text.replace(',', '.'));
            }
            if (type == short.class || type == Short.class) {
                return Short.valueOf(text);
            }
            if (type == boolean.class || type == Boolean.class) {
                return isTrue(text);
            }
            if (type == LocalDate.class) {
                return LocalDate.parse(text);
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new IllegalArgumentException("valeur \"" + raw + "\" invalide pour le type " + type.getSimpleName());
        }
        throw new IllegalArgumentException("type d'argument non supporte : " + type.getName());
    }

    /** "true", "on" (case a cocher), "1", "oui", "yes". */
    private static boolean isTrue(String text) {
        return text.equalsIgnoreCase("true") || text.equalsIgnoreCase("on") || text.equals("1")
                || text.equalsIgnoreCase("oui") || text.equalsIgnoreCase("yes");
    }

    private static Object defaultValue(Class<?> type) {
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == double.class) return 0.0;
        if (type == float.class) return 0f;
        if (type == short.class) return (short) 0;
        if (type == boolean.class) return false;
        return null;
    }
}
