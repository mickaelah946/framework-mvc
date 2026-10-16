package com.app.model;

/** Objet dans un objet (sprint 7bis) : champs adresse.rue et adresse.ville du formulaire. */
public class Adresse {
    private String rue;
    private String ville;

    public Adresse() {
    }

    public String getRue() { return rue; }
    public String getVille() { return ville; }

    @Override
    public String toString() {
        return rue + ", " + ville;
    }
}
