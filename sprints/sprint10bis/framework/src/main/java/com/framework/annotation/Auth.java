package com.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sprint 10 : reserve une methode (ou tout un controller) aux utilisateurs authentifies.
 * L'utilisateur est authentifie quand la session HTTP contient l'attribut dont le nom est donne par
 * le context-param session-user-name de web.xml. Sinon le framework repond 401 sans appeler la methode.
 *
 * Sprint 10bis : roles() limite l'acces a certains roles. Vide (valeur par defaut), il suffit d'etre connecte ;
 * sinon l'utilisateur doit avoir au moins un des roles listes (attribut de session session-role-name), sinon 403.
 *
 * Si la methode et la classe portent chacune @Auth, celle de la methode est la seule utilisee (roles compris).
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {

    /** Roles autorises ; vide = tout utilisateur connecte. */
    String[] roles() default {};
}
