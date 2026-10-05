package com.app.controller;

import com.app.model.Mvt;
import com.framework.annotation.APIREST;
import com.framework.annotation.Controller;
import com.framework.annotation.UrlMapping;

import java.util.List;

@Controller("webapi")
public class WebApiController {

    /** String deja en JSON. */
    @APIREST(alreadyJson = true)
    @UrlMapping("webapi/hello")
    public String hello() {
        return "{\"message\": \"Hello APIREST\"}";
    }

    /** Liste d'objets : convertie en tableau JSON par le framework. */
    @APIREST
    @UrlMapping("webapi/list")
    public List<Mvt> list() {
        return List.of(
                new Mvt(1, "Salaire", "ENTREE", 1500000),
                new Mvt(2, "Loyer", "SORTIE", 400000),
                new Mvt(3, "Courses", "SORTIE", 120000));
    }
}
