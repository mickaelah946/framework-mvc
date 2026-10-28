package com.framework.model;

import jakarta.servlet.http.HttpSession;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Copie modifiable d'une session HTTP, synchronisee apres l'appel du controleur. */
public class MaSession {

    private String id;
    private final Map<String, Object> attributes = new LinkedHashMap<>();
    private final Set<String> initialKeys = new LinkedHashSet<>();

    public MaSession() {
    }

    /** Copie l'identifiant et les attributs de la session HTTP. */
    public static MaSession from(HttpSession httpSession) {
        Objects.requireNonNull(httpSession, "httpSession ne doit pas etre null");
        MaSession copy = new MaSession();
        copy.id = httpSession.getId();
        Enumeration<String> names = httpSession.getAttributeNames();
        names.asIterator().forEachRemaining(name -> {
            copy.attributes.put(name, httpSession.getAttribute(name));
            copy.initialKeys.add(name);
        });
        return copy;
    }

    /** Recopie les suppressions et valeurs modifiees vers la session HTTP. */
    public void flushTo(HttpSession httpSession) {
        Objects.requireNonNull(httpSession, "httpSession ne doit pas etre null");
        for (String key : initialKeys) {
            if (!attributes.containsKey(key)) {
                httpSession.removeAttribute(key);
            }
        }
        attributes.forEach(httpSession::setAttribute);
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /** Comme HttpSession.setAttribute, une valeur null supprime l'attribut. */
    public void setAttribute(String name, Object value) {
        Objects.requireNonNull(name, "name ne doit pas etre null");
        if (value == null) {
            removeAttribute(name);
        } else {
            attributes.put(name, value);
        }
    }

    public void removeAttribute(String name) {
        Objects.requireNonNull(name, "name ne doit pas etre null");
        attributes.remove(name);
    }
}
