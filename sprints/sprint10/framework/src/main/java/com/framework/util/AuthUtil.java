package com.framework.util;

import com.framework.annotation.Auth;

/** Sprint 10 : recherche de l'annotation @Auth qui s'applique a une route. */
public class AuthUtil {

    /** @Auth de la methode, sinon celle de la classe du controller, sinon null (route publique). */
    public static Auth find(Mapping route) {
        Auth auth = route.getMethod().getAnnotation(Auth.class);
        if (auth == null) {
            auth = route.getControllerClass().getAnnotation(Auth.class);
        }
        return auth;
    }

    /** Vrai si au moins une route du controller est protegee par @Auth. */
    public static boolean anyProtected(java.util.Collection<Mapping> routes) {
        for (Mapping route : routes) {
            if (find(route) != null) {
                return true;
            }
        }
        return false;
    }
}
