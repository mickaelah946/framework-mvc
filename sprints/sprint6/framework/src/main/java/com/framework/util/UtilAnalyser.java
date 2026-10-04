package com.framework.util;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Cherche toutes les classes d'un projet, sans bibliotheque externe.
 * Parcourt les dossiers (WEB-INF/classes) et les jars du ClassLoader.
 */
public class UtilAnalyser {

    /**
     * Toutes les classes du package donne (sous-packages compris).
     * Si basePackage est vide ou null : toutes les classes des dossiers du classpath.
     */
    public static List<Class<?>> getClasses(String basePackage) {
        String pkg = basePackage == null ? "" : basePackage.trim();
        String path = pkg.replace('.', '/');
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = UtilAnalyser.class.getClassLoader();
        }

        TreeSet<String> names = new TreeSet<>();
        try {
            Enumeration<URL> roots = loader.getResources(path);
            while (roots.hasMoreElements()) {
                URL url = roots.nextElement();
                if ("file".equals(url.getProtocol())) {
                    File dir = new File(URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8));
                    collectFromDirectory(dir, pkg, names);
                } else if ("jar".equals(url.getProtocol())) {
                    collectFromJar(url, path, names);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de parcourir le package '" + pkg + "' : " + e, e);
        }

        List<Class<?>> classes = new ArrayList<>();
        for (String name : names) {
            try {
                classes.add(Class.forName(name, false, loader));
            } catch (Throwable ignored) {
                // classe illisible (dependance absente...) : on l'ignore
            }
        }
        return classes;
    }

    private static void collectFromDirectory(File dir, String pkg, TreeSet<String> names) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            String prefix = pkg.isEmpty() ? "" : pkg + ".";
            if (file.isDirectory()) {
                collectFromDirectory(file, prefix + file.getName(), names);
            } else if (file.getName().endsWith(".class") && !file.getName().contains("$")) {
                String simple = file.getName().substring(0, file.getName().length() - ".class".length());
                names.add(prefix + simple);
            }
        }
    }

    private static void collectFromJar(URL url, String path, TreeSet<String> names) throws Exception {
        URLConnection connection = url.openConnection();
        if (!(connection instanceof JarURLConnection)) {
            return;
        }
        JarFile jar = ((JarURLConnection) connection).getJarFile();
        String prefix = path.isEmpty() ? "" : path + "/";
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            String entry = entries.nextElement().getName();
            if (entry.startsWith(prefix) && entry.endsWith(".class") && !entry.contains("$")
                    && !entry.equals("module-info.class")) {
                names.add(entry.substring(0, entry.length() - ".class".length()).replace('/', '.'));
            }
        }
    }
}
