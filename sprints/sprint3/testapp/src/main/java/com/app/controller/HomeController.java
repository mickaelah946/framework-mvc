package com.app.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.UrlMapping;

@Controller("home")
public class HomeController {

    @UrlMapping("home/index")
    public String index() {
        return "home->index";
    }
}