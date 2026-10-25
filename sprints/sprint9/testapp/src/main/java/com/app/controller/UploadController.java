package com.app.controller;

import com.app.model.Employe;
import com.framework.annotation.APIREST;
import com.framework.annotation.Controller;
import com.framework.annotation.HttpMethod;
import com.framework.annotation.UrlMapping;
import com.framework.model.MapFile;
import com.framework.model.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sprint 8 : envoi de fichiers (formulaires enctype="multipart/form-data").
 * Le framework donne un MapFile (nom, extension, octets) a chaque argument ou attribut du meme nom que le champ file.
 */
@Controller("upload")
public class UploadController {

    /** Affiche les formulaires d'envoi. */
    @UrlMapping("upload/form")
    public ModelAndView form() {
        return new ModelAndView("upload/form");
    }

    /** Un argument MapFile par champ file (rectoCIN, versoCIN) ; "nom" est un champ de texte du meme formulaire. */
    @UrlMapping(value = "upload/saveFichiers", method = HttpMethod.POST)
    public ModelAndView saveFichiers(String nom, MapFile rectoCIN, MapFile versoCIN) throws IOException {
        List<String> lignes = new ArrayList<>();
        lignes.add("nom = " + nom);
        lignes.add(decrire("rectoCIN", rectoCIN));
        lignes.add(decrire("versoCIN", versoCIN));
        return resultat("saveFichiers(String nom, MapFile rectoCIN, MapFile versoCIN)", lignes);
    }

    /** Objet Employe : les attributs MapFile rectoCIN et versoCIN sont remplis avec les fichiers du meme nom. */
    @UrlMapping(value = "upload/saveObjet", method = HttpMethod.POST)
    public ModelAndView saveObjet(Employe employe) throws IOException {
        List<String> lignes = new ArrayList<>();
        lignes.add("employe = " + employe);
        lignes.add(decrire("employe.rectoCIN", employe.getRectoCIN()));
        lignes.add(decrire("employe.versoCIN", employe.getVersoCIN()));
        return resultat("saveObjet(Employe employe)", lignes);
    }

    /** Map nom du champ -> MapFile : tous les fichiers du formulaire, sans connaitre leurs noms. */
    @UrlMapping(value = "upload/saveMap", method = HttpMethod.POST)
    public ModelAndView saveMap(Map<String, MapFile> fichiers) throws IOException {
        List<String> lignes = new ArrayList<>();
        for (Map.Entry<String, MapFile> e : fichiers.entrySet()) {
            lignes.add(decrire(e.getKey(), e.getValue()));
        }
        if (lignes.isEmpty()) {
            lignes.add("(aucun fichier recu)");
        }
        return resultat("saveMap(Map<String, MapFile> fichiers)", lignes);
    }

    /** Map nom du champ -> octets (tableau de bytes), comme dans le todo. */
    @UrlMapping(value = "upload/saveOctets", method = HttpMethod.POST)
    public ModelAndView saveOctets(Map<String, byte[]> octets) {
        List<String> lignes = new ArrayList<>();
        for (Map.Entry<String, byte[]> e : octets.entrySet()) {
            lignes.add(e.getKey() + " : " + e.getValue().length + " octets");
        }
        if (lignes.isEmpty()) {
            lignes.add("(aucun fichier recu)");
        }
        return resultat("saveOctets(Map<String, byte[]> octets)", lignes);
    }

    /** Plusieurs fichiers dans le meme champ (input file avec l'attribut multiple). */
    @UrlMapping(value = "upload/saveListe", method = HttpMethod.POST)
    public ModelAndView saveListe(String nom, List<MapFile> photos) throws IOException {
        List<String> lignes = new ArrayList<>();
        lignes.add("nom = " + nom);
        for (int i = 0; i < photos.size(); i++) {
            lignes.add(decrire("photos[" + i + "]", photos.get(i)));
        }
        if (photos.isEmpty()) {
            lignes.add("(aucune photo recue)");
        }
        return resultat("saveListe(String nom, List<MapFile> photos)", lignes);
    }

    /** Reponse JSON (@APIREST) : nom, extension, type et taille de chaque fichier ; les octets ne sont pas affiches. */
    @APIREST
    @UrlMapping(value = "upload/info", method = HttpMethod.POST)
    public Map<String, MapFile> info(Map<String, MapFile> fichiers) {
        return fichiers;
    }

    // ---------- outils ----------

    private static ModelAndView resultat(String methode, List<String> lignes) {
        return new ModelAndView("upload/result")
                .addAttribut("methode", methode)
                .addAttribut("lignes", lignes);
    }

    /** Decrit le fichier recu et l'enregistre dans le dossier upload-dir (context-param de web.xml). */
    private static String decrire(String champ, MapFile f) throws IOException {
        if (f == null) {
            return champ + " = null (aucun fichier choisi, ou formulaire sans enctype=\"multipart/form-data\")";
        }
        return champ + " : nom = " + f.getNom() + ", extension = " + f.getExtension() + ", type = " + f.getContentType()
                + ", taille = " + f.getTaille() + " octets, enregistre dans " + f.save();
    }
}
