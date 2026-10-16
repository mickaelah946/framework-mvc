package com.framework.model;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Sprint 8 : un fichier envoye par un formulaire (enctype="multipart/form-data").
 * Le framework en cree un pour chaque champ fichier recu : nom du fichier, extension et octets.
 *
 * Exemple : "Cin-Recto.PNG" donne nom = "Cin-Recto", extension = "png" (en minuscules, sans le point).
 * Les octets et le dossier d'upload ne sont pas affiches dans le JSON (champs transient).
 */
public class MapFile {

    private static final Pattern EXTENSION = Pattern.compile("[a-z0-9]{1,10}");

    private final String nom;
    private final String extension;
    private final String contentType;
    private final long taille;
    private final transient byte[] bytes;
    private final transient Path uploadDir;

    /**
     * @param nomOriginal nom du fichier envoye par le navigateur (chemin eventuel ignore)
     * @param uploadDir   dossier du context-param upload-dir (null si non configure)
     */
    public MapFile(String nomOriginal, String contentType, byte[] bytes, Path uploadDir) {
        String base = lastSegment(nomOriginal == null ? "" : nomOriginal);
        int dot = base.lastIndexOf('.');
        String ext = (dot > 0) ? base.substring(dot + 1).toLowerCase(Locale.ROOT) : "";
        if (EXTENSION.matcher(ext).matches()) {
            this.nom = base.substring(0, dot);
            this.extension = ext;
        } else {
            this.nom = base;
            this.extension = "";
        }
        this.contentType = contentType;
        this.bytes = bytes == null ? new byte[0] : bytes;
        this.taille = this.bytes.length;
        this.uploadDir = uploadDir;
    }

    /** Nom du fichier sans l'extension ("Cin-Recto"). */
    public String getNom() { return nom; }

    /** Extension en minuscules, sans le point ("png") ; chaine vide si le fichier n'en a pas. */
    public String getExtension() { return extension; }

    /** Nom complet tel qu'envoye, extension en minuscules ("Cin-Recto.png"). */
    public String getNomComplet() { return extension.isEmpty() ? nom : nom + "." + extension; }

    /** Type MIME annonce par le navigateur (ex. "image/png"), peut etre null. */
    public String getContentType() { return contentType; }

    /** Nombre d'octets du fichier. */
    public long getTaille() { return taille; }

    /** Contenu du fichier (le tableau lui-meme, sans copie). */
    public byte[] getBytes() { return bytes; }

    public boolean isEmpty() { return bytes.length == 0; }

    /**
     * Enregistre le fichier dans le dossier upload-dir sous son nom d'origine.
     * Un fichier du meme nom n'est jamais ecrase : "cin.png" devient "cin-1.png", "cin-2.png"...
     *
     * @return le chemin du fichier enregistre
     */
    public Path save() throws IOException {
        return save(getNomComplet());
    }

    /**
     * Enregistre le fichier dans upload-dir sous le nom donne (sans ecraser un fichier existant).
     * Le nom est nettoye : pas de chemin (../), seuls lettres, chiffres, point, tiret et underscore sont gardes.
     */
    public Path save(String nomComplet) throws IOException {
        if (uploadDir == null) {
            throw new IllegalStateException("upload-dir non configure : ajouter le context-param \"upload-dir\" dans web.xml");
        }
        Path dir = uploadDir.toAbsolutePath().normalize();
        Files.createDirectories(dir);
        String safe = sanitize(nomComplet);
        int dot = safe.lastIndexOf('.');
        String stem = dot > 0 ? safe.substring(0, dot) : safe;
        String end = dot > 0 ? safe.substring(dot) : "";
        for (int i = 0; i < 10000; i++) {
            String candidate = i == 0 ? safe : stem + "-" + i + end;
            Path target;
            try {
                target = dir.resolve(candidate).normalize();
            } catch (InvalidPathException e) {
                // systeme de fichiers qui n'accepte pas les accents (encodage non UTF-8) : lettres ASCII seulement
                target = dir.resolve(candidate.replaceAll("[^A-Za-z0-9._-]", "_")).normalize();
            }
            if (!target.startsWith(dir)) {
                throw new IOException("nom de fichier refuse : " + nomComplet);
            }
            try {
                Files.write(target, bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                return target;
            } catch (FileAlreadyExistsException e) {
                // essayer le nom suivant
            }
        }
        throw new IOException("trop de fichiers portent deja le nom " + safe);
    }

    /** Dernier element d'un chemin envoye par le client ("C:\\dossier\\cin.png" donne "cin.png"). */
    private static String lastSegment(String name) {
        int cut = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        return name.substring(cut + 1);
    }

    /** Nom de fichier sur : sans chemin, sans point au debut, caracteres speciaux remplaces par "_". */
    static String sanitize(String name) {
        String s = lastSegment(name == null ? "" : name).replaceAll("[^\\p{L}\\p{N}._-]", "_");
        while (s.startsWith(".")) {
            s = s.substring(1);
        }
        if (s.length() > 150) {
            s = s.substring(s.length() - 150);
        }
        return s.isEmpty() ? "fichier" : s;
    }

    @Override
    public String toString() {
        return getNomComplet() + " (" + taille + " octets)";
    }
}
