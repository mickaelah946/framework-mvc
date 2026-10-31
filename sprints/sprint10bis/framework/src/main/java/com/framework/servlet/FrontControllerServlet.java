package com.framework.servlet;

import com.framework.annotation.APIREST;
import com.framework.annotation.Auth;
import com.framework.annotation.HttpMethod;
import com.framework.listener.FrontControllerListener;
import com.framework.model.MapFile;
import com.framework.model.MaSession;
import com.framework.model.ModelAndView;
import com.framework.util.AuthUtil;
import com.framework.util.ClasseUtilitaire;
import com.framework.annotation.Param;
import com.framework.util.JsonUtil;
import com.framework.util.ObjectBinder;
import com.framework.util.ParamUtil;
import com.framework.util.Mapping;
import com.framework.util.UploadUtil;
import com.framework.util.UrlMethod;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

public class FrontControllerServlet extends HttpServlet {

    private String controllerPackage;
    private Map<String, String> controllers = new TreeMap<>();
    private Map<UrlMethod, Mapping> routes = new TreeMap<>();
    /** Sprint 10 : nom de l'attribut de session qui contient l'utilisateur connecte. */
    private String sessionUserName;
    /** Sprint 10bis : nom de l'attribut de session qui contient le ou les roles de l'utilisateur. */
    private String sessionRoleName;

    /** Les controllers et les routes sont deja scannes par le FrontControllerListener. */
    @Override
    @SuppressWarnings("unchecked")
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        controllerPackage = (String) context.getAttribute(FrontControllerListener.ATTR_PACKAGE);
        controllers = (Map<String, String>) context.getAttribute(FrontControllerListener.ATTR_CONTROLLERS);
        routes = (Map<UrlMethod, Mapping>) context.getAttribute(FrontControllerListener.ATTR_ROUTES);

        if (controllers == null || routes == null) {
            throw new ServletException("Routes absentes du contexte : declarer "
                    + "com.framework.listener.FrontControllerListener dans web.xml");
        }
        sessionUserName = (String) context.getAttribute(FrontControllerListener.ATTR_SESSION_USER);
        sessionRoleName = (String) context.getAttribute(FrontControllerListener.ATTR_SESSION_ROLE);
        log("Routes recuperees depuis le contexte : " + routes.size());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp, HttpMethod.GET);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp, HttpMethod.POST);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp, HttpMethod httpMethod)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // accents des champs de formulaire (POST)
        String path = req.getServletPath() + (req.getPathInfo() == null ? "" : req.getPathInfo());
        String url = ClasseUtilitaire.normalizeUrl(path);

        // Accueil : liste de tous les controllers et de leurs routes
        if (url.isEmpty()) {
            resp.setContentType("text/html;charset=UTF-8");
            PrintWriter out = resp.getWriter();
            out.println("<h1>FrontControllerServlet</h1>");
            out.println("<h2>Controllers et routes disponibles</h2>");
            out.println(listAll(req.getContextPath()));
            out.flush();
            return;
        }

        UrlMethod wanted = new UrlMethod(url, httpMethod);
        Mapping route = routes.get(wanted);
        if (route == null) {
            throw new ServletException("Aucune methode pour " + wanted + "." + otherMethodsHint(url, httpMethod)
                    + " Routes disponibles : " + describeRoutes());
        }

        // Sprint 10 : @Auth -> 401 si personne n'est connecte ; la methode du controller n'est alors pas appelee
        if (!checkAuthorization(route, req, resp)) {
            return;
        }

        // Sprint 6 : methode annotee @APIREST -> JSON, sans requestDispatcher
        APIREST apiRest = route.getMethod().getAnnotation(APIREST.class);
        if (apiRest != null) {
            Object value = invoke(route, req);
            String json = (apiRest.alreadyJson() && value instanceof String)
                    ? (String) value
                    : JsonUtil.toJson(value);
            resp.setContentType("application/json;charset=UTF-8");
            PrintWriter jsonOut = resp.getWriter();
            jsonOut.print(json);
            jsonOut.flush();
            return;
        }

        // Sinon : ancienne execution (ModelAndView + dispatcher)
        Object result = invoke(route, req);

        if (result instanceof ModelAndView) {
            ModelAndView mv = (ModelAndView) result;
            for (Map.Entry<String, Object> attr : mv.getData().entrySet()) {
                req.setAttribute(attr.getKey(), attr.getValue());
            }
            req.getRequestDispatcher(mv.getViewPath()).forward(req, resp);
            return;
        }

        throw new ServletException("Type de retour invalide pour " + route.label()
                + " : ModelAndView attendu, trouve " + (result == null ? "null" : result.getClass().getName()));
    }

    /**
     * Sprint 10 : vrai si la route peut etre appelee. Une route sans @Auth est publique. Sinon la session doit
     * exister et contenir l'attribut de l'utilisateur (session-user-name), sinon reponse 401 et retour false.
     * Sprint 10bis : si @Auth precise des roles, la session doit en contenir au moins un (un seul role en String,
     * ou plusieurs dans une Collection / List / un tableau), sinon reponse 403 et retour false.
     * @Auth de la methode prime sur celui de la classe.
     */
    protected boolean checkAuthorization(Mapping route, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        Auth auth = AuthUtil.find(route);
        if (auth == null) {
            return true;
        }
        if (sessionUserName == null) {
            throw new ServletException(route.label() + " porte @Auth mais le context-param \""
                    + FrontControllerListener.PARAM_SESSION_USER + "\" n'est pas configure dans web.xml");
        }
        HttpSession session = req.getSession(false); // pas de creation de session pour un visiteur anonyme
        Object user = (session == null) ? null : session.getAttribute(sessionUserName);
        if (user == null) {
            handleUnauthorized(req, resp, route);
            return false;
        }
        String[] roles = auth.roles();
        if (roles.length == 0) {
            return true; // connecte suffit
        }
        if (sessionRoleName == null) {
            throw new ServletException(route.label() + " exige des roles mais le context-param \""
                    + FrontControllerListener.PARAM_SESSION_ROLE + "\" n'est pas configure dans web.xml");
        }
        if (!AuthUtil.hasAnyRole(session.getAttribute(sessionRoleName), roles)) {
            handleForbidden(req, resp, route, roles);
            return false;
        }
        return true;
    }

    /** Sprint 10bis : reponse 403 (connecte mais sans le role requis), en JSON pour une API, en HTML sinon. */
    protected void handleForbidden(HttpServletRequest req, HttpServletResponse resp, Mapping route, String[] roles)
            throws IOException {
        sendError(req, resp, route, HttpServletResponse.SC_FORBIDDEN, "Forbidden",
                "Acces refuse a " + route.getUrlMethod() + " : role requis : " + String.join(", ", roles));
    }

    /** Sprint 10 : reponse 401 (personne n'est connecte), en JSON pour une API, en HTML sinon. */
    protected void handleUnauthorized(HttpServletRequest req, HttpServletResponse resp, Mapping route) throws IOException {
        sendError(req, resp, route, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
                "Authentification requise pour " + route.getUrlMethod());
    }

    /** Reponse d'erreur : JSON si la route est @APIREST ou si le client demande du JSON, page HTML sinon. */
    private void sendError(HttpServletRequest req, HttpServletResponse resp, Mapping route, int status,
                           String error, String message) throws IOException {
        resp.setStatus(status);
        PrintWriter out;
        if (wantsJson(req, route)) {
            resp.setContentType("application/json;charset=UTF-8");
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("status", status);
            body.put("error", error);
            body.put("message", message);
            out = resp.getWriter();
            out.print(JsonUtil.toJson(body));
        } else {
            resp.setContentType("text/html;charset=UTF-8");
            out = resp.getWriter();
            out.println("<h1>" + status + " " + esc(error) + "</h1>");
            out.println("<p>" + esc(message) + "</p>");
        }
        out.flush();
    }

    /** Vrai pour une methode @APIREST, ou quand le client prefere le JSON au HTML (en-tete Accept). */
    private static boolean wantsJson(HttpServletRequest req, Mapping route) {
        if (route.getMethod().isAnnotationPresent(APIREST.class)) {
            return true;
        }
        String accept = req.getHeader("Accept");
        return accept != null && accept.contains("application/json") && !accept.contains("text/html");
    }

    /** Invoque par reflexion la methode du controller, avec les arguments pris dans la requete (Sprint 7). */
    private Object invoke(Mapping route, HttpServletRequest req) throws ServletException {
        Method method = route.getMethod();
        Object[] args = buildArguments(route, req);
        try {
            Object controller = route.getControllerClass().getDeclaredConstructor().newInstance();
            Object result = method.invoke(controller, args);
            flushSessions(args, req);
            return result;
        } catch (InvocationTargetException e) {
            throw new ServletException("Erreur dans " + route.label() + " : " + e.getCause(), e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new ServletException("Impossible d'invoquer " + route.label(), e);
        }
    }

    /**
     * Sprint 7 : un argument nomme "nom" recoit le parametre de requete "nom" (champ de formulaire
     * ou ?nom=... dans l'URL), converti vers le type de l'argument.
     * Sprint 7bis : @Param("nom") choisit le parametre a lire ; un argument de type objet est cree
     * et ses attributs sont remplis avec les parametres du meme nom.
     * Sprint 8 : un argument MapFile (ou byte[]) recoit le fichier du champ du meme nom, un argument
     * Map<String, MapFile> (ou Map<String, byte[]>) recoit tous les fichiers du formulaire.
     * Sprint 9 : un argument MaSession recoit une copie de la session HTTP, puis ses changements
     * sont recopies vers HttpSession apres l'invocation.
     */
    private Object[] buildArguments(Mapping route, HttpServletRequest req) throws ServletException {
        Parameter[] parameters = route.getMethod().getParameters();
        Object[] args = new Object[parameters.length];
        int objectCount = 0; // plusieurs objets : le prefixe (employe.nom) devient obligatoire
        for (Parameter q : parameters) {
            if (q.getType() != MaSession.class
                    && !ParamUtil.isSimple(q.getType()) && !ObjectBinder.isMultiple(q.getType())
                    && !ObjectBinder.isFileType(q.getType())) {
                objectCount++;
            }
        }
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            try {
                Param param = p.getAnnotation(Param.class);
                Class<?> type = p.getType();
                if (type == MaSession.class) {
                    args[i] = MaSession.from(req.getSession(true));
                    continue;
                }
                if (ObjectBinder.isFileType(type)) {
                    // Sprint 8 : fichiers envoyes par un formulaire multipart
                    if (type == java.util.Map.class || java.util.Map.class.isAssignableFrom(type)) {
                        args[i] = UploadUtil.fileMap(req, type, p.getParameterizedType()); // tous les fichiers
                    } else {
                        String field = (param != null) ? param.value() : (p.isNamePresent() ? p.getName() : "");
                        if (field.isEmpty()) {
                            throw new IllegalArgumentException("nom d'argument illisible pour un fichier : utiliser "
                                    + "@Param(\"...\") ou compiler l'application avec javac -parameters");
                        }
                        args[i] = (type == MapFile.class) ? UploadUtil.file(req, field) : UploadUtil.bytes(req, field);
                    }
                    continue;
                }
                if (!ParamUtil.isSimple(p.getType())) {
                    // 7bis : objet (avec objets imbriques), List / Set / tableau, ou plusieurs objets.
                    // Le nom de l'argument (ou @Param) sert de prefixe : employe.nom, employes[0].nom...
                    String name = (param != null) ? param.value() : (p.isNamePresent() ? p.getName() : "");
                    args[i] = ObjectBinder.isMultiple(p.getType())
                            ? ObjectBinder.bindMultiple(p.getParameterizedType(), p.getType(), name, req)
                            : ObjectBinder.bind(p.getType(), name, req, objectCount > 1);
                    continue;
                }
                if (param == null && !p.isNamePresent()) {
                    throw new ServletException("Nom d'argument illisible pour " + route.label()
                            + " : utiliser @Param(\"...\") ou compiler l'application avec javac -parameters");
                }
                String name = (param != null) ? param.value() : p.getName();
                args[i] = ParamUtil.convert(req.getParameter(name), p.getType());
            } catch (IllegalArgumentException e) {
                throw new ServletException("Argument " + (i + 1) + " de " + route.label() + " : " + e.getMessage(), e);
            }
        }
        return args;
    }

    /** Applique les copies MaSession a la session de la requete apres l'action du controleur. */
    private void flushSessions(Object[] args, HttpServletRequest req) throws ServletException {
        jakarta.servlet.http.HttpSession httpSession = req.getSession(false);
        for (Object arg : args) {
            if (arg instanceof MaSession) {
                if (httpSession == null) {
                    throw new ServletException("La session HTTP a disparu avant la synchronisation de MaSession");
                }
                ((MaSession) arg).flushTo(httpSession);
            }
        }
    }

    /** Si l'URL existe avec une autre methode HTTP, le signale. */
    private String otherMethodsHint(String url, HttpMethod requested) {
        StringBuilder sb = new StringBuilder();
        for (HttpMethod other : HttpMethod.values()) {
            if (other != requested && routes.containsKey(new UrlMethod(url, other))) {
                sb.append(" Cette URL existe en ").append(other).append('.');
            }
        }
        return sb.toString();
    }

    /** Liste HTML : chaque controller avec ses routes (lien pour GET, bouton pour POST). */
    private String listAll(String contextPath) {
        if (controllers.isEmpty()) {
            return "<p>Aucun controller trouve dans " + esc(String.valueOf(controllerPackage)) + ".</p>";
        }
        StringBuilder sb = new StringBuilder("<ul>");
        for (Map.Entry<String, String> c : controllers.entrySet()) {
            sb.append("<li><b>").append(esc(c.getValue())).append("</b> : ").append(esc(c.getKey())).append("<ul>");
            boolean any = false;
            for (Mapping r : routes.values()) {
                if (!r.getControllerClass().getName().equals(c.getKey())) {
                    continue;
                }
                any = true;
                String href = esc(contextPath) + "/" + esc(r.getUrl());
                if (r.getHttpMethod() == HttpMethod.GET) {
                    sb.append("<li>GET <a href=\"").append(href).append("\">/").append(esc(r.getUrl()))
                      .append("</a> &rarr; ").append(esc(r.label())).append("</li>");
                } else {
                    sb.append("<li><form method=\"post\" action=\"").append(href)
                      .append("\" style=\"display:inline\"><button type=\"submit\">POST /")
                      .append(esc(r.getUrl())).append("</button></form> &rarr; ")
                      .append(esc(r.label())).append("</li>");
                }
            }
            if (!any) {
                sb.append("<li>Aucune methode @UrlMapping</li>");
            }
            sb.append("</ul></li>");
        }
        return sb.append("</ul>").toString();
    }

    /** Texte : GET /emp/list (emp->list), POST /list (test->listPost), ... */
    private String describeRoutes() {
        if (routes.isEmpty()) {
            return "aucune";
        }
        StringJoiner joiner = new StringJoiner(", ");
        for (Mapping r : routes.values()) {
            joiner.add(r.getUrlMethod() + " (" + r.label() + ")");
        }
        return joiner.toString();
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
