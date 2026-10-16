package com.framework.model;

import java.util.LinkedHashMap;
import java.util.Map;

/** Nom d'une vue (JSP) et donnees a lui transmettre. */
public class ModelAndView {

    private String view;
    private final Map<String, Object> data = new LinkedHashMap<>();

    public ModelAndView(String view) {
        this.view = view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    /** Ajoute une donnee a transmettre a la vue (retourne this pour chainer les appels). */
    public ModelAndView addAttribut(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    /** "mvt/list" devient /WEB-INF/views/mvt/list.jsp */
    public String getViewPath() {
        return "/WEB-INF/views/" + view + ".jsp";
    }
}
