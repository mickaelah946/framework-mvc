package com.framework.servlet;

import com.framework.util.ClasseUtilitaire;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

public class FrontControllerServlet extends HttpServlet {

    private static final String PACKAGE_PARAM = "controllerPackage";
    private static final String DEFAULT_PACKAGE = "com.app.controller";

    private String controllerPackage;
    private Map<String, String> controllers = new TreeMap<>();

    @Override
    public void init() throws ServletException {
        controllerPackage = getInitParameter(PACKAGE_PARAM);
        if (controllerPackage == null || controllerPackage.isBlank()) {
            controllerPackage = DEFAULT_PACKAGE;
        }
        controllers = ClasseUtilitaire.getControllers(controllerPackage);
        log("Controllers detectes dans " + controllerPackage + " : " + controllers.size());
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
        if (path.isEmpty()) {
            path = "/";
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<h1>FrontControllerServlet</h1>");
        out.println("<p>URI demandee : " + esc(uri) + "</p>");
        out.println("<p>URL demandee : " + esc(path) + "</p>");
        out.println("<h2>Controllers detectes dans " + esc(controllerPackage) + " (" + controllers.size() + ")</h2>");

        if (controllers.isEmpty()) {
            out.println("<p>Aucun controller trouve.</p>");
        } else {
            out.println("<ul>");
            for (Map.Entry<String, String> entry : controllers.entrySet()) {
                out.println("<li><b>" + esc(entry.getValue()) + "</b> : " + esc(entry.getKey()) + "</li>");
            }
            out.println("</ul>");
        }
        out.flush();
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}