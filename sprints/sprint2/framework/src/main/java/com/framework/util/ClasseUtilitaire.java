package com.framework.util;

import com.framework.annotation.Controller;
import com.framework.annotation.UrlMapping;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Outils de scan des classes annotees (UtilAnalyser + ScanAnnotation, sans bibliotheque externe). */
public class ClasseUtilitaire {

    /** Classes annotees @Controller du package donne. */
    private static Set<Class<?>> scanControllerClasses(String basePackage) {
        return new LinkedHashSet<>(ScanAnnotation.getClassesWithAnnotation(
                UtilAnalyser.getClasses(basePackage), Controller.class));
    }

    private static String controllerName(Class<?> clazz) {
        String value = clazz.getAnnotation(Controller.class).value();
        return value.isEmpty() ? clazz.getSimpleName() : value;
    }

    /** Map : nom complet de la classe -> nom du controller. */
    public static Map<String, String> getControllers(String basePackage) {
        Map<String, String> result = new TreeMap<>();
        for (Class<?> clazz : scanControllerClasses(basePackage)) {
            result.put(clazz.getName(), controllerName(clazz));
        }
        return result;
    }

    /**
     * Map : url normalisee -> Mapping, pour chaque methode annotee @UrlMapping
     * d'un controller.
     *
     * @throws IllegalStateException si deux methodes ont la meme URL
     */
    public static Map<String, Mapping> getRoutes(String basePackage) {
        Map<String, Mapping> routes = new TreeMap<>();
        for (Class<?> clazz : scanControllerClasses(basePackage)) {
            String name = controllerName(clazz);
            for (Method method : ScanAnnotation.getMethodsWithAnnotation(clazz, UrlMapping.class)) {
                UrlMapping annotation = method.getAnnotation(UrlMapping.class);
                String url = normalizeUrl(annotation.value());
                Mapping existing = routes.get(url);
                if (existing != null) {
                    throw new IllegalStateException("URL en double : /" + url + " ("
                            + existing.label() + " et " + name + "->" + method.getName() + ")");
                }
                routes.put(url, new Mapping(url, name, clazz, method));
            }
        }
        return routes;
    }

    /** Retire les "/" au debut et a la fin : "/emp/list/" devient "emp/list". */
    public static String normalizeUrl(String url) {
        String result = url == null ? "" : url.trim();
        while (result.startsWith("/")) {
            result = result.substring(1);
        }
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}