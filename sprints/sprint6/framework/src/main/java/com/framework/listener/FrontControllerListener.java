package com.framework.listener;

import com.framework.util.ClasseUtilitaire;
import com.framework.util.Mapping;
import com.framework.util.UrlMethod;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.util.Map;

/**
 * Scanne les controllers UNE SEULE FOIS au demarrage de l'application
 * et stocke le resultat dans le ServletContext.
 */
public class FrontControllerListener implements ServletContextListener {

    public static final String PARAM_PACKAGE = "controllerPackage";
    public static final String DEFAULT_PACKAGE = "com.app.controller";

    public static final String ATTR_PACKAGE = "framework.controllerPackage";
    public static final String ATTR_CONTROLLERS = "framework.controllers";
    public static final String ATTR_ROUTES = "framework.routes";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        String controllerPackage = context.getInitParameter(PARAM_PACKAGE);
        if (controllerPackage == null || controllerPackage.isBlank()) {
            controllerPackage = DEFAULT_PACKAGE;
        }

        // Une route en double leve une IllegalStateException : l'application ne demarre pas.
        Map<String, String> controllers = ClasseUtilitaire.getControllers(controllerPackage);
        Map<UrlMethod, Mapping> routes = ClasseUtilitaire.getRoutes(controllerPackage);

        context.setAttribute(ATTR_PACKAGE, controllerPackage);
        context.setAttribute(ATTR_CONTROLLERS, controllers);
        context.setAttribute(ATTR_ROUTES, routes);

        context.log("FrontControllerListener : " + controllers.size() + " controller(s), "
                + routes.size() + " route(s) dans " + controllerPackage);
        for (Mapping route : routes.values()) {
            context.log("Route : " + route.getUrlMethod() + " -> " + route.label());
        }
    }
}