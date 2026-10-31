package com.framework.listener;

import com.framework.util.AuthUtil;
import com.framework.util.ClasseUtilitaire;
import com.framework.util.Mapping;
import com.framework.util.UploadUtil;
import com.framework.util.UrlMethod;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    /** Sprint 8 : dossier ou MapFile.save() enregistre les fichiers recus (context-param upload-dir). */
    public static final String PARAM_UPLOAD_DIR = "upload-dir";
    public static final String ATTR_UPLOAD_DIR = "framework.uploadDir";

    /** Sprint 10 : nom de l'attribut de session qui contient l'utilisateur connecte (context-param de web.xml). */
    public static final String PARAM_SESSION_USER = "session-user-name";
    /** Autre nom accepte pour le meme context-param. */
    public static final String PARAM_SESSION_LOG = "session-log-name";
    public static final String ATTR_SESSION_USER = "framework.sessionUserName";

    /** Sprint 10bis : nom de l'attribut de session qui contient le ou les roles de l'utilisateur. */
    public static final String PARAM_SESSION_ROLE = "session-role-name";
    public static final String ATTR_SESSION_ROLE = "framework.sessionRoleName";

    /** Sprint 11 : instances des controllers (classe -> instance), creees a la premiere requete puis reutilisees. */
    public static final String ATTR_CONTROLLERS_INSTANCE = "controllersInstance";

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

        // Sprint 8 : dossier d'upload (${variable} acceptee, ex. ${catalina.base}/uploads) ; une variable inconnue empeche le demarrage
        String uploadDir = context.getInitParameter(PARAM_UPLOAD_DIR);
        if (uploadDir != null && !uploadDir.isBlank()) {
            String resolved = UploadUtil.resolveDir(uploadDir);
            context.setAttribute(ATTR_UPLOAD_DIR, resolved);
            context.log("Dossier d'upload : " + resolved);
        } else {
            context.log("Aucun upload-dir dans web.xml : MapFile.save() sera refuse");
        }

        // Sprint 10 : cle de session de l'utilisateur connecte ; obligatoire des qu'une route porte @Auth
        String userKey = context.getInitParameter(PARAM_SESSION_USER);
        if (userKey == null || userKey.isBlank()) {
            userKey = context.getInitParameter(PARAM_SESSION_LOG);
        }
        if (userKey != null && !userKey.isBlank()) {
            context.setAttribute(ATTR_SESSION_USER, userKey.trim());
            context.log("Attribut de session de l'utilisateur : " + userKey.trim());
        } else if (AuthUtil.anyProtected(routes.values())) {
            throw new IllegalStateException("@Auth est utilise mais le context-param \"" + PARAM_SESSION_USER
                    + "\" est absent de web.xml (nom de l'attribut de session qui contient l'utilisateur connecte)");
        }

        // Sprint 10bis : cle de session des roles ; obligatoire des qu'un @Auth(roles = ...) existe
        String roleKey = context.getInitParameter(PARAM_SESSION_ROLE);
        if (roleKey != null && !roleKey.isBlank()) {
            context.setAttribute(ATTR_SESSION_ROLE, roleKey.trim());
            context.log("Attribut de session des roles : " + roleKey.trim());
        } else if (AuthUtil.anyRoles(routes.values())) {
            throw new IllegalStateException("@Auth(roles = ...) est utilise mais le context-param \"" + PARAM_SESSION_ROLE
                    + "\" est absent de web.xml (nom de l'attribut de session qui contient les roles)");
        }

        // Sprint 11 : une seule instance par controller. La map est creee ici (vide) et remplie par le
        // FrontControllerServlet ; ConcurrentHashMap car plusieurs requetes peuvent la lire en meme temps.
        context.setAttribute(ATTR_CONTROLLERS_INSTANCE, new ConcurrentHashMap<Class<?>, Object>());

        context.log("FrontControllerListener : " + controllers.size() + " controller(s), "
                + routes.size() + " route(s) dans " + controllerPackage);
        for (Mapping route : routes.values()) {
            context.log("Route : " + route.getUrlMethod() + " -> " + route.label());
        }
    }
}