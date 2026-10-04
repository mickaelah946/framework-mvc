package com.framework.util;

import com.framework.annotation.HttpMethod;

import java.lang.reflect.Method;

/** Lien entre une cle (URL + methode HTTP), un controller et une methode. */
public class RouteMapping {

    private final UrlMethod urlMethod;
    private final String controllerName;
    private final Class<?> controllerClass;
    private final Method method;

    public RouteMapping(UrlMethod urlMethod, String controllerName, Class<?> controllerClass, Method method) {
        this.urlMethod = urlMethod;
        this.controllerName = controllerName;
        this.controllerClass = controllerClass;
        this.method = method;
    }

    public UrlMethod getUrlMethod() { return urlMethod; }
    public String getUrl() { return urlMethod.getUrl(); }
    public HttpMethod getHttpMethod() { return urlMethod.getMethod(); }
    public String getControllerName() { return controllerName; }
    public Class<?> getControllerClass() { return controllerClass; }
    public Method getMethod() { return method; }

    /** Forme affichee : controller->methode (ex : test->listGet). */
    public String label() {
        return controllerName + "->" + method.getName();
    }
}