package com.app.controller;

import com.app.model.Departement;
import com.app.model.Employe;
import com.framework.annotation.APIREST;
import com.framework.annotation.Controller;
import com.framework.annotation.HttpMethod;
import com.framework.annotation.Param;
import com.framework.annotation.UrlMapping;
import com.framework.model.ModelAndView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Sprint 7 et 7bis : les arguments des methodes sont remplis avec les champs du formulaire (meme nom). */
@Controller("employe")
public class EmployeController {

    /** Affiche le formulaire (champs nom, prenom, age). */
    @UrlMapping("employe/form")
    public ModelAndView form() {
        return new ModelAndView("employe/form");
    }

    /** POST du formulaire : "nom", "prenom" et "age" sont reconnus automatiquement. */
    @UrlMapping(value = "employe/save", method = HttpMethod.POST)
    public ModelAndView save(String nom, String prenom, Integer age) {
        return new ModelAndView("employe/result")
                .addAttribut("methode", "save(String nom, String prenom, Integer age)")
                .addAttribut("nom", String.valueOf(nom))
                .addAttribut("prenom", String.valueOf(prenom))
                .addAttribut("age", String.valueOf(age));
    }

    /** GET : /employe/hello?nom=Mimi (un seul argument ; les autres champs ne sont pas lus). */
    @UrlMapping("employe/hello")
    public ModelAndView hello(String nom) {
        return new ModelAndView("employe/result")
                .addAttribut("methode", "hello(String nom)")
                .addAttribut("nom", String.valueOf(nom));
    }

    /** Meme chose avec une reponse JSON (@APIREST du sprint 6) : /employe/json?nom=Mimi&age=30 */
    @APIREST
    @UrlMapping("employe/json")
    public Map<String, Object> json(String nom, Integer age) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("nom", nom);
        m.put("age", age);
        return m;
    }

    // ---------- Sprint 7bis ----------

    /** @Param : le champ "nom" du formulaire arrive dans lastName, "prenom" dans firstName. */
    @UrlMapping(value = "employe/saveParam", method = HttpMethod.POST)
    public ModelAndView saveParam(@Param("nom") String lastName, @Param("prenom") String firstName) {
        return new ModelAndView("employe/result")
                .addAttribut("methode", "saveParam(@Param(\"nom\") String lastName, @Param(\"prenom\") String firstName)")
                .addAttribut("nom", String.valueOf(lastName))
                .addAttribut("prenom", String.valueOf(firstName));
    }

    /** Argument objet : le framework cree l'Employe et remplit nom, prenom, age avec les champs du formulaire. */
    @UrlMapping(value = "employe/saveObjet", method = HttpMethod.POST)
    public ModelAndView saveObjet(Employe employe) {
        return new ModelAndView("employe/result")
                .addAttribut("methode", "saveObjet(Employe employe)")
                .addAttribut("nom", String.valueOf(employe.getNom()))
                .addAttribut("prenom", String.valueOf(employe.getPrenom()))
                .addAttribut("age", String.valueOf(employe.getAge()));
    }

    /** Objet en entree et en sortie (JSON) : /employe/objet?nom=Mimi&prenom=R&age=30 */
    @APIREST
    @UrlMapping("employe/objet")
    public Employe objet(Employe employe) {
        return employe;
    }

    // ---------- Sprint 7bis : objet dans un objet, liste d'objets, deux objets ----------

    /** Objet dans l'objet : champs nom, prenom, age, adresse.rue, adresse.ville, langues[0], langues[1]. */
    @UrlMapping(value = "employe/saveImbrique", method = HttpMethod.POST)
    public ModelAndView saveImbrique(Employe employe) {
        List<String> lignes = new ArrayList<>();
        lignes.add("employe = " + employe);
        lignes.add("adresse = " + employe.getAdresse() + (employe.getAdresse() == null ? " (aucun champ adresse.*)" : ""));
        return new ModelAndView("employe/binding")
                .addAttribut("methode", "saveImbrique(Employe employe)")
                .addAttribut("lignes", lignes);
    }

    /** Liste d'objets : employes[0].nom, employes[0].age, employes[1].nom, ... */
    @UrlMapping(value = "employe/saveListe", method = HttpMethod.POST)
    public ModelAndView saveListe(List<Employe> employes) {
        List<String> lignes = new ArrayList<>();
        for (int i = 0; i < employes.size(); i++) {
            lignes.add("employes[" + i + "] = " + employes.get(i));
        }
        if (lignes.isEmpty()) {
            lignes.add("(liste vide : aucun champ employes[i].xxx)");
        }
        return new ModelAndView("employe/binding")
                .addAttribut("methode", "saveListe(List<Employe> employes)")
                .addAttribut("lignes", lignes);
    }

    /** Deux objets : employe.nom et departement.nom ne se melangent pas. */
    @UrlMapping(value = "employe/saveDeux", method = HttpMethod.POST)
    public ModelAndView saveDeux(Employe employe, Departement departement) {
        List<String> lignes = new ArrayList<>();
        lignes.add("employe = " + employe);
        lignes.add("departement = " + departement);
        return new ModelAndView("employe/binding")
                .addAttribut("methode", "saveDeux(Employe employe, Departement departement)")
                .addAttribut("lignes", lignes);
    }

    /** JSON : /employe/imbrique?nom=Mimi&adresse.rue=Lalana&adresse.ville=Tana&langues[0]=fr&langues[1]=mg */
    @APIREST
    @UrlMapping("employe/imbrique")
    public Employe imbrique(Employe employe) {
        return employe;
    }

    /** JSON : /employe/liste?employes[0].nom=A&employes[1].nom=B */
    @APIREST
    @UrlMapping("employe/liste")
    public List<Employe> liste(List<Employe> employes) {
        return employes;
    }
}
