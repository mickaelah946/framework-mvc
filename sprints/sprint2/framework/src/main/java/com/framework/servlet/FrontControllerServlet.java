package com.framework.servlet;

import com.framework.util.ClasseUtilitaire;
import com.framework.util.Mapping;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

public class FrontControllerServlet extends HttpServlet {

    private static final String PACKAGE_PARAM = "controllerPackage";
    private static final String DEFAULT_PACKAGE = "com.app.controller";

    private String controllerPackage;
    private Map<String, String> controllers = new TreeMap<>();
    private Map<String, Mapping> routes = new TreeMap<>();

    @Override
    public void init() throws ServletException {
        controllerPackage = getInitParameter(PACKAGE_PARAM);
        if (controllerPackage == null || controllerPackage.isBlank()) {
            controllerPackage = DEFAULT_PACKAGE;
        }
        controllers = ClasseUtilitaire.getControllers(controllerPackage);
        try {
            routes = ClasseUtilitaire.getRoutes(controllerPackage);
        } catch (IllegalStateException e) {
            throw new ServletException(e.getMessage(), e);
        }
        log("Controllers : " + controllers.size() + ", URLs : " + routes.size());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String path = uri.substring(req.getContextPath().length());
        String key = ClasseUtilitaire.normalizeUrl(path);

        // Accueil : liste de tous les controllers et de leurs URLs
        if (key.isEmpty()) {
            resp.setContentType("text/html;charset=UTF-8");
            PrintWriter out = resp.getWriter();
            out.println("<h1>FrontControllerServlet</h1>");
            out.println("<h2>Controllers et URLs disponibles</h2>");
            out.println(listAll());
            out.flush();
            return;
        }

        Mapping route = routes.get(key);
        if (route == null) {
            throw new ServletException("Aucune methode pour l'URL /" + key
                    + ". URLs disponibles : " + describeRoutes());
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<h1>" + esc(route.label()) + "</h1>");
        out.println("<p>URL : /" + esc(route.getUrl()) + "</p>");
        out.println("<p>Classe : " + esc(route.getControllerClass().getName()) + "</p>");
        out.flush();
    }

    /** Liste HTML : chaque controller avec ses URLs. */
    private String listAll() {
        if (controllers.isEmpty()) {
            return "<p>Aucun controller trouve.</p>";
        }
        StringBuilder sb = new StringBuilder("<ul>");
        for (Map.Entry<String, String> c : controllers.entrySet()) {
            sb.append("<li><b>").append(esc(c.getValue())).append("</b> : ").append(esc(c.getKey())).append("<ul>");
            boolean any = false;
            for (Mapping r : routes.values()) {
                if (r.getControllerClass().getName().equals(c.getKey())) {
                    any = true;
                    sb.append("<li>/").append(esc(r.getUrl())).append(" &rarr; ").append(esc(r.label())).append("</li>");
                }
            }
            if (!any) {
                sb.append("<li>Aucune methode @UrlMapping</li>");
            }
            sb.append("</ul></li>");
        }
        return sb.append("</ul>").toString();
    }

    /** Texte : /emp/list (emp->list), /emp/create (emp->create), ... */
    private String describeRoutes() {
        if (routes.isEmpty()) {
            return "aucune";
        }
        StringJoiner joiner = new StringJoiner(", ");
        for (Mapping r : routes.values()) {
            joiner.add("/" + r.getUrl() + " (" + r.label() + ")");
        }
        return joiner.toString();
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}