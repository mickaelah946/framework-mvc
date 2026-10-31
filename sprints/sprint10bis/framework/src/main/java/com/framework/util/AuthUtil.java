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

    /** Sprint 10bis : vrai si une route exige au moins un role (roles() non vide). */
    public static boolean anyRoles(java.util.Collection<Mapping> routes) {
        for (Mapping route : routes) {
            Auth auth = find(route);
            if (auth != null && auth.roles().length > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sprint 10bis : vrai si les roles de la session contiennent au moins un des roles requis.
     * La session peut contenir un seul role (String) ou plusieurs (Collection, List, tableau).
     */
    public static boolean hasAnyRole(Object sessionRoles, String[] required) {
        if (sessionRoles == null) {
            return false;
        }
        if (sessionRoles instanceof Iterable) {
            for (Object role : (Iterable<?>) sessionRoles) {
                if (matches(role, required)) {
                    return true;
                }
            }
            return false;
        }
        if (sessionRoles.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(sessionRoles);
            for (int i = 0; i < length; i++) {
                if (matches(java.lang.reflect.Array.get(sessionRoles, i), required)) {
                    return true;
                }
            }
            return false;
        }
        return matches(sessionRoles, required);
    }

    private static boolean matches(Object role, String[] required) {
        if (role == null) {
            return false;
        }
        String name = String.valueOf(role);
        for (String wanted : required) {
            if (wanted.equals(name)) {
                return true;
            }
        }
        return false;
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
