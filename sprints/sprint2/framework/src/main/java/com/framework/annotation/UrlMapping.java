package com.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Associe une URL (ex : "emp/list") a une methode d'un controller. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlMapping {
    String value();
}