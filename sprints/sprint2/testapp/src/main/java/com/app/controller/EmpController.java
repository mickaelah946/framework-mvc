package com.app.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.UrlMapping;

@Controller("emp")
public class EmpController {

    @UrlMapping("emp/list")
    public String list() {
        return "emp->list";
    }

    @UrlMapping("emp/create")
    public String create() {
        return "emp->create";
    }
}