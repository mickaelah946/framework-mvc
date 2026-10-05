package com.app.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.HttpMethod;
import com.framework.annotation.UrlMapping;

@Controller("test")
public class TestController {

    @UrlMapping(value = "/list", method = HttpMethod.GET)
    public String listGet() {
        System.out.println("GET /list -> TestController.listGet()");
        return "test->list (GET)";
    }

    @UrlMapping(value = "/list", method = HttpMethod.POST)
    public String listPost() {
        System.out.println("POST /list -> TestController.listPost()");
        return "test->list (POST)";
    }
}