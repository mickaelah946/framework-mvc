package com.app.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.HttpMethod;
import com.framework.annotation.UrlMapping;
import com.framework.model.ModelAndView;

@Controller("test")
public class TestController {

    @UrlMapping(value = "/list", method = HttpMethod.GET)
    public ModelAndView listGet() {
        return new ModelAndView("page")
                .addAttribut("titre", "GET /list")
                .addAttribut("controller", "test")
                .addAttribut("methode", "listGet")
                .addAttribut("httpMethod", "GET");
    }

    @UrlMapping(value = "/list", method = HttpMethod.POST)
    public ModelAndView listPost() {
        return new ModelAndView("page")
                .addAttribut("titre", "POST /list")
                .addAttribut("controller", "test")
                .addAttribut("methode", "listPost")
                .addAttribut("httpMethod", "POST");
    }
}
