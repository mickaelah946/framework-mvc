package com.framework.util;

import com.framework.annotation.Controller;

import java.util.Map;
import java.util.List;
import java.util.TreeMap;

/** Outils de scan des classes annotees (UtilAnalyser + ScanAnnotation, sans bibliotheque externe). */
public class ClasseUtilitaire {

    /**
     * Cherche dans le package donne les classes annotees @Controller.
     *
     * @return map : nom complet de la classe -> nom du controller
     */
    public static Map<String, String> getControllers(String basePackage) {
        List<Class<?>> classes = ScanAnnotation.getClassesWithAnnotation(
                UtilAnalyser.getClasses(basePackage), Controller.class);

        Map<String, String> result = new TreeMap<>();
        for (Class<?> clazz : classes) {
            Controller annotation = clazz.getAnnotation(Controller.class);
            if (annotation == null) {
                continue;
            }
            String name = annotation.value().isEmpty() ? clazz.getSimpleName() : annotation.value();
            result.put(clazz.getName(), name);
        }
        return result;
    }
}