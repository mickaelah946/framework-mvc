package com.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methode de controller qui retourne du JSON (application/json) au lieu d'une vue.
 * alreadyJson = true : la methode retourne deja une String JSON, renvoyee telle quelle.
 * alreadyJson = false (defaut) : le framework convertit le retour avec JsonUtil.toJson.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface APIREST {
    boolean alreadyJson() default false;
}
