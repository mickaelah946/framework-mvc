package com.app.controller;

import com.framework.annotation.APIREST;
import com.framework.annotation.Controller;
import com.framework.annotation.HttpMethod;
import com.framework.annotation.UrlMapping;
import com.framework.model.ModelAndView;

import java.util.LinkedHashMap;
import java.util.Map;

/** Sprint 7 : les arguments des methodes sont remplis avec les champs du formulaire (meme nom). */
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
}
