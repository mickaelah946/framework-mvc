package com.app.controller;

import com.app.model.Mvt;
import com.framework.annotation.Controller;
import com.framework.annotation.UrlMapping;
import com.framework.model.ModelAndView;

import java.util.List;

@Controller("mvt")
public class MvtController {

    /** Liste ecrite en dur (le Sprint 5 de la promo : liste + vue). */
    @UrlMapping("mvt/list")
    public ModelAndView list() {
        List<Mvt> mouvements = List.of(
                new Mvt(1, "Salaire", "ENTREE", 1500000),
                new Mvt(2, "Loyer", "SORTIE", 400000),
                new Mvt(3, "Courses", "SORTIE", 120000));
        return new ModelAndView("mvt/list").addAttribut("mouvements", mouvements);
    }
}
