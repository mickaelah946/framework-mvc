package com.app.controller;

import com.app.model.Employe;
import com.framework.annotation.APIREST;
import com.framework.annotation.Controller;
import com.framework.annotation.HttpMethod;
import com.framework.annotation.Param;
import com.framework.annotation.UrlMapping;
import com.framework.model.ModelAndView;

import java.util.LinkedHashMap;
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
    public ModelAndView save(String nom, String prenom, int age) {
        return new ModelAndView("employe/result")
                .addAttribut("methode", "save(String nom, String prenom, int age)")
                .addAttribut("nom", nom)
                .addAttribut("prenom", prenom)
                .addAttribut("age", age);
    }

    /** GET : /employe/hello?nom=Mimi (un seul argument ; les autres champs ne sont pas lus). */
    @UrlMapping("employe/hello")
    public ModelAndView hello(String nom) {
        return new ModelAndView("employe/result")
                .addAttribut("methode", "hello(String nom)")
                .addAttribut("nom", nom);
    }

    /** Meme chose avec une reponse JSON (@APIREST du sprint 6) : /employe/json?nom=Mimi&age=30 */
    @APIREST
    @UrlMapping("employe/json")
    public Map<String, Object> json(String nom, int age) {
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
                .addAttribut("nom", lastName)
                .addAttribut("prenom", firstName);
    }

    /** Argument objet : le framework cree l'Employe et remplit nom, prenom, age avec les champs du formulaire. */
    @UrlMapping(value = "employe/saveObjet", method = HttpMethod.POST)
    public ModelAndView saveObjet(Employe employe) {
        return new ModelAndView("employe/result")
                .addAttribut("methode", "saveObjet(Employe employe)")
                .addAttribut("nom", employe.getNom())
                .addAttribut("prenom", employe.getPrenom())
                .addAttribut("age", employe.getAge());
    }

    /** Objet en entree et en sortie (JSON) : /employe/objet?nom=Mimi&prenom=R&age=30 */
    @APIREST
    @UrlMapping("employe/objet")
    public Employe objet(Employe employe) {
        return employe;
    }
}
