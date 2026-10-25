package com.app.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.UrlMapping;
import com.framework.model.ModelAndView;

@Controller("emp")
public class EmpController {

    @UrlMapping("emp/list")
    public ModelAndView list() {
        return new ModelAndView("page")
                .addAttribut("titre", "Liste des employes")
                .addAttribut("controller", "emp")
                .addAttribut("methode", "list")
                .addAttribut("httpMethod", "GET");
    }

    @UrlMapping("emp/create")
    public ModelAndView create() {
        return new ModelAndView("page")
                .addAttribut("titre", "Creation employe")
                .addAttribut("controller", "emp")
                .addAttribut("methode", "create")
                .addAttribut("httpMethod", "GET");
    }
}
