package com.framework.util;

import java.lang.reflect.Method;

/** Lien entre une URL, un controller et une methode. */
public class Mapping {

    private final String url;
    private final String controllerName;
    private final Class<?> controllerClass;
    private final Method method;

    public Mapping(String url, String controllerName, Class<?> controllerClass, Method method) {
        this.url = url;
        this.controllerName = controllerName;
        this.controllerClass = controllerClass;
        this.method = method;
    }

    public String getUrl() { return url; }
    public String getControllerName() { return controllerName; }
    public Class<?> getControllerClass() { return controllerClass; }
    public Method getMethod() { return method; }

    /** Forme affichee : controller->methode (ex : emp->list). */
    public String label() {
        return controllerName + "->" + method.getName();
    }
}