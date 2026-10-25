package com.app.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.UrlMapping;
import com.framework.model.ModelAndView;

@Controller("user")
public class UserController {

    @UrlMapping("user/list")
    public ModelAndView list() {
        return new ModelAndView("page")
                .addAttribut("titre", "Liste des utilisateurs")
                .addAttribut("controller", "user")
                .addAttribut("methode", "list")
                .addAttribut("httpMethod", "GET");
    }
}
