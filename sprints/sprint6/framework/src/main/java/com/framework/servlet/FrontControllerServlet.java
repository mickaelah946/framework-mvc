package com.framework.servlet;

import com.framework.annotation.APIREST;
import com.framework.annotation.HttpMethod;
import com.framework.listener.FrontControllerListener;
import com.framework.model.ModelAndView;
import com.framework.util.ClasseUtilitaire;
import com.framework.util.JsonUtil;
import com.framework.util.RouteMapping;
import com.framework.util.UrlMethod;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

public class FrontControllerServlet extends HttpServlet {

    private String controllerPackage;
    private Map<String, String> controllers = new TreeMap<>();
    private Map<UrlMethod, RouteMapping> routes = new TreeMap<>();

    /** Les controllers et les routes sont deja scannes par le FrontControllerListener. */
    @Override
    @SuppressWarnings("unchecked")
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        controllerPackage = (String) context.getAttribute(FrontControllerListener.ATTR_PACKAGE);
        controllers = (Map<String, String>) context.getAttribute(FrontControllerListener.ATTR_CONTROLLERS);
        routes = (Map<UrlMethod, RouteMapping>) context.getAttribute(FrontControllerListener.ATTR_ROUTES);

        if (controllers == null || routes == null) {
            throw new ServletException("Routes absentes du contexte : declarer "
                    + "com.framework.listener.FrontControllerListener dans web.xml");
        }
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
        RouteMapping route = routes.get(wanted);
        if (route == null) {
            throw new ServletException("Aucune methode pour " + wanted + "." + otherMethodsHint(url, httpMethod)
                    + " Routes disponibles : " + describeRoutes());
        }

        // Sprint 6 : methode annotee @APIREST -> JSON, sans requestDispatcher
        APIREST apiRest = route.getMethod().getAnnotation(APIREST.class);
        if (apiRest != null) {
            Object value = invoke(route);
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
        Object result = invoke(route);

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

    /** Invoque par reflexion la methode du controller (sans parametre pour l'instant). */
    private Object invoke(RouteMapping route) throws ServletException {
        Method method = route.getMethod();
        if (method.getParameterCount() > 0) {
            throw new ServletException("Methode avec parametres non supportee : " + route.label());
        }
        try {
            Object controller = route.getControllerClass().getDeclaredConstructor().newInstance();
            return method.invoke(controller);
        } catch (InvocationTargetException e) {
            throw new ServletException("Erreur dans " + route.label() + " : " + e.getCause(), e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new ServletException("Impossible d'invoquer " + route.label(), e);
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
            for (RouteMapping r : routes.values()) {
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
        for (RouteMapping r : routes.values()) {
            joiner.add(r.getUrlMethod() + " (" + r.label() + ")");
        }
        return joiner.toString();
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}