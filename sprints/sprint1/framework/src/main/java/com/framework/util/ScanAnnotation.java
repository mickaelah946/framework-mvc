package com.framework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/** Trouve les classes et les methodes qui portent une annotation donnee. */
public class ScanAnnotation {

    /** Parmi les classes données, celles qui portent l'annotation. */
    public static List<Class<?>> getClassesWithAnnotation(List<Class<?>> classes,
                                                          Class<? extends Annotation> annotation) {
        List<Class<?>> result = new ArrayList<>();
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(annotation)) {
                result.add(clazz);
            }
        }
        return result;
    }

    /** Toutes les methodes de la classe qui portent l'annotation (ordre stable par nom). */
    public static List<Method> getMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Method> result = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                result.add(method);
            }
        }
        result.sort(Comparator.comparing(Method::getName)
                .thenComparing(m -> Arrays.toString(m.getParameterTypes())));
        return result;
    }
}
