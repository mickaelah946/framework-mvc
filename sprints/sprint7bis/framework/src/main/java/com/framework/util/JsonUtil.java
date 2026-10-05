package com.framework.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/** Conversion d'un objet Java en JSON, sans bibliotheque externe (fonction toJson du framework). */
public class JsonUtil {

    /** Convertit null, nombres, booleens, textes, enums, tableaux, listes, maps et objets en JSON. */
    public static String toJson(Object object) {
        StringBuilder sb = new StringBuilder();
        write(sb, object, new IdentityHashMap<>());
        return sb.toString();
    }

    private static void write(StringBuilder sb, Object value, IdentityHashMap<Object, Boolean> path) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof CharSequence || value instanceof Character || value instanceof Enum) {
            quote(sb, value instanceof Enum ? ((Enum<?>) value).name() : value.toString());
        } else if (value instanceof Boolean) {
            sb.append(value);
        } else if (value instanceof Number) {
            writeNumber(sb, (Number) value);
        } else if (value.getClass().getName().startsWith("java.")) {
            writeJavaType(sb, value, path);
        } else {
            writeObject(sb, value, path);
        }
    }

    private static void writeNumber(StringBuilder sb, Number n) {
        double d = n.doubleValue();
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            sb.append("null");
        } else {
            sb.append(n);
        }
    }

    /** Classes du JDK : collections, maps, tableaux ; le reste (dates...) est ecrit comme un texte. */
    private static void writeJavaType(StringBuilder sb, Object value, IdentityHashMap<Object, Boolean> path) {
        enter(value, path);
        if (value instanceof Map) {
            sb.append('{');
            boolean first = true;
            for (Map.Entry<?, ?> e : ((Map<?, ?>) value).entrySet()) {
                if (!first) {
                    sb.append(',');
                }
                first = false;
                quote(sb, String.valueOf(e.getKey()));
                sb.append(':');
                write(sb, e.getValue(), path);
            }
            sb.append('}');
        } else if (value instanceof Iterable) {
            sb.append('[');
            boolean first = true;
            for (Object item : (Iterable<?>) value) {
                if (!first) {
                    sb.append(',');
                }
                first = false;
                write(sb, item, path);
            }
            sb.append(']');
        } else if (value.getClass().isArray()) {
            writeArray(sb, value, path);
        } else {
            quote(sb, value.toString());
        }
        path.remove(value);
    }

    private static void writeArray(StringBuilder sb, Object array, IdentityHashMap<Object, Boolean> path) {
        sb.append('[');
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            write(sb, Array.get(array, i), path);
        }
        sb.append(']');
    }

    /** Objet du projet : un champ JSON par attribut (prives compris), parents compris. */
    private static void writeObject(StringBuilder sb, Object value, IdentityHashMap<Object, Boolean> path) {
        if (value.getClass().isArray()) {
            enter(value, path);
            writeArray(sb, value, path);
            path.remove(value);
            return;
        }
        enter(value, path);
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = value.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            List<Field> own = new ArrayList<>();
            for (Field f : c.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())
                        && !f.isSynthetic()) {
                    own.add(f);
                }
            }
            fields.addAll(0, own);
        }
        sb.append('{');
        boolean first = true;
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (!first) {
                    sb.append(',');
                }
                first = false;
                quote(sb, field.getName());
                sb.append(':');
                write(sb, field.get(value), path);
            } catch (ReflectiveOperationException | RuntimeException e) {
                throw new IllegalStateException("Lecture impossible du champ " + field.getName(), e);
            }
        }
        sb.append('}');
        path.remove(value);
    }

    private static void enter(Object value, IdentityHashMap<Object, Boolean> path) {
        if (path.put(value, Boolean.TRUE) != null) {
            throw new IllegalStateException("Reference circulaire : impossible de convertir en JSON");
        }
    }

    private static void quote(StringBuilder sb, String text) {
        sb.append('"');
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }
}
