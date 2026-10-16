package com.app.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.UrlMapping;
import com.framework.model.ModelAndView;

@Controller("home")
public class HomeController {

    @UrlMapping("home/index")
    public ModelAndView index() {
        return new ModelAndView("page")
                .addAttribut("titre", "Accueil")
                .addAttribut("controller", "home")
                .addAttribut("methode", "index")
                .addAttribut("httpMethod", "GET");
    }
}
