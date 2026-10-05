package com.app.controller;

import com.framework.annotation.APIREST;
import com.framework.annotation.Controller;
import com.framework.annotation.UrlMapping;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller("api")
public class ApiController {

    /** Le developpeur ecrit lui-meme le JSON : alreadyJson = true, la String est renvoyee telle quelle. */
    @APIREST(alreadyJson = true)
    @UrlMapping("api/hello")
    public String hello() {
        return "{\"message\": \"Hello API\"}";
    }

    /** Autre type qu'une String : le framework appelle toJson. */
    @APIREST
    @UrlMapping("api/info")
    public Map<String, Object> info() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("framework", "framework-servlet");
        info.put("sprint", 6);
        info.put("rest", true);
        return info;
    }
}
