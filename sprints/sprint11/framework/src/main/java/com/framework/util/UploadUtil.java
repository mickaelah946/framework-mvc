package com.framework.util;

import com.framework.listener.FrontControllerListener;
import com.framework.model.MapFile;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sprint 8 : lecture des fichiers d'un formulaire multipart (une requete sans fichier ne pose aucun probleme :
 * il n'y a alors aucun MapFile). La requete doit etre envoyee par un formulaire enctype="multipart/form-data",
 * et le servlet doit declarer un multipart-config dans web.xml.
 */
public class UploadUtil {

    private static final Pattern VARIABLE = Pattern.compile("\\$\\{([^}]+)}");

    /** Vrai si la requete est un envoi multipart/form-data. */
    public static boolean isMultipart(HttpServletRequest req) {
        String type = req.getContentType();
        return type != null && type.toLowerCase().startsWith("multipart/");
    }

    /** Le fichier envoye par le champ "name", ou null (pas de multipart, champ absent ou aucun fichier choisi). */
    public static MapFile file(HttpServletRequest req, String name) {
        for (Part part : fileParts(req)) {
            if (name.equals(part.getName())) {
                return toMapFile(req, part);
            }
        }
        return null;
    }

    /** Tous les fichiers envoyes par le champ "name" (champ file avec l'attribut multiple), liste vide sinon. */
    public static List<MapFile> files(HttpServletRequest req, String name) {
        List<MapFile> list = new ArrayList<>();
        for (Part part : fileParts(req)) {
            if (name.equals(part.getName())) {
                list.add(toMapFile(req, part));
            }
        }
        return list;
    }

    /** Octets du fichier envoye par le champ "name", ou null. */
    public static byte[] bytes(HttpServletRequest req, String name) {
        MapFile file = file(req, name);
        return file == null ? null : file.getBytes();
    }

    /**
     * Tous les fichiers de la requete : nom du champ de formulaire -> fichier (le premier si un champ en envoie plusieurs).
     * Pour un argument Map&lt;String, MapFile&gt; ou Map&lt;String, byte[]&gt;.
     */
    public static Object fileMap(HttpServletRequest req, Class<?> mapType, Type genericType) {
        boolean asBytes;
        Type value = null;
        if (genericType instanceof ParameterizedType) {
            Type[] args = ((ParameterizedType) genericType).getActualTypeArguments();
            if (args.length == 2 && args[0] == String.class) {
                value = args[1];
            }
        }
        if (value == MapFile.class) {
            asBytes = false;
        } else if (value == byte[].class) {
            asBytes = true;
        } else {
            throw new IllegalArgumentException("argument Map : seuls Map<String, MapFile> et Map<String, byte[]> sont pris en charge");
        }
        if (!mapType.isAssignableFrom(LinkedHashMap.class)) {
            throw new IllegalArgumentException("argument Map : utiliser le type Map (ou LinkedHashMap), pas " + mapType.getSimpleName());
        }
        Map<String, Object> map = new LinkedHashMap<>();
        for (Part part : fileParts(req)) {
            if (!map.containsKey(part.getName())) {
                MapFile file = toMapFile(req, part);
                map.put(part.getName(), asBytes ? file.getBytes() : file);
            }
        }
        return map;
    }

    /** Vrai si un champ fichier rempli a un nom qui commence par start (ex. "employe." pour employe.rectoCIN). */
    public static boolean hasFileStartingWith(HttpServletRequest req, String start) {
        for (Part part : fileParts(req)) {
            if (part.getName() != null && part.getName().startsWith(start)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Valeur du context-param upload-dir : les ${variable} sont remplacees par la propriete systeme
     * (ou la variable d'environnement) du meme nom, par exemple ${catalina.base}/uploads.
     */
    public static String resolveDir(String value) {
        Matcher m = VARIABLE.matcher(value.trim());
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String key = m.group(1);
            String replacement = System.getProperty(key, System.getenv(key));
            if (replacement == null) {
                throw new IllegalStateException("upload-dir : la variable ${" + key + "} est inconnue (valeur : " + value + ")");
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        try {
            Paths.get(sb.toString());
        } catch (InvalidPathException e) {
            throw new IllegalStateException("upload-dir : chemin invalide (" + sb + ")", e);
        }
        return sb.toString();
    }

    // ---------- interne ----------

    /** Les parties de la requete qui sont des fichiers choisis (un champ file laisse vide est ignore). */
    private static List<Part> fileParts(HttpServletRequest req) {
        List<Part> files = new ArrayList<>();
        if (!isMultipart(req)) {
            return files;
        }
        try {
            for (Part part : req.getParts()) {
                String submitted = part.getSubmittedFileName();
                if (submitted != null && !submitted.isEmpty()) {
                    files.add(part);
                }
            }
        } catch (IllegalStateException e) {
            // Tomcat : multipart-config absent, ou fichier plus gros que max-file-size / max-request-size
            throw new IllegalArgumentException("envoi de fichier impossible : verifier que le servlet a un <multipart-config> "
                    + "dans web.xml et que les fichiers ne depassent pas max-file-size / max-request-size (" + e.getMessage() + ")");
        } catch (IOException | ServletException e) {
            throw new IllegalArgumentException("lecture de la requete multipart impossible : " + e.getMessage());
        }
        return files;
    }

    private static MapFile toMapFile(HttpServletRequest req, Part part) {
        try (InputStream in = part.getInputStream()) {
            return new MapFile(part.getSubmittedFileName(), part.getContentType(), in.readAllBytes(), uploadDir(req));
        } catch (IOException e) {
            throw new IllegalArgumentException("lecture du fichier \"" + part.getName() + "\" impossible : " + e.getMessage());
        }
    }

    private static Path uploadDir(HttpServletRequest req) {
        Object dir = req.getServletContext().getAttribute(FrontControllerListener.ATTR_UPLOAD_DIR);
        return dir == null ? null : Paths.get(dir.toString());
    }
}
