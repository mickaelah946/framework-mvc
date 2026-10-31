package com.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sprint 7bis : donne le nom du parametre de requete a lire pour un argument de methode,
 * quand ce nom est different du nom de l'argument (ex. champ "nom" du formulaire dans l'argument lastName).
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    /** Nom du parametre de requete (champ de formulaire ou ?nom=...). */
    String value();
}
