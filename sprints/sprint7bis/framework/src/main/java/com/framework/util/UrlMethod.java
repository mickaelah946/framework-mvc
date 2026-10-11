package com.framework.util;

import com.framework.annotation.HttpMethod;

import java.util.Objects;

/** Cle d'une route : URL + methode HTTP. Deux UrlMethod sont egales si les deux sont egaux. */
public class UrlMethod implements Comparable<UrlMethod> {

    private final String url;
    private final HttpMethod method;

    public UrlMethod(String url, HttpMethod method) {
        this.url = ClasseUtilitaire.normalizeUrl(url);
        this.method = method;
    }

    public String getUrl() { return url; }
    public HttpMethod getMethod() { return method; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlMethod that = (UrlMethod) o;
        return Objects.equals(url, that.url) && method == that.method;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }

    @Override
    public int compareTo(UrlMethod other) {
        int c = url.compareTo(other.url);
        return c != 0 ? c : method.compareTo(other.method);
    }

    /** Ex : GET /emp/list */
    @Override
    public String toString() {
        return method + " /" + url;
    }
}