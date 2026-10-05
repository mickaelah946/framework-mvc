package com.app.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.UrlMapping;

@Controller("user")
public class UserController {

    @UrlMapping("user/list")
    public String list() {
        return "user->list";
    }
}