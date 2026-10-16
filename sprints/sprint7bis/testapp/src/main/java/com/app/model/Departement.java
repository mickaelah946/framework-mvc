package com.app.model;

/** Deuxieme objet pour tester deux objets dans la meme methode (sprint 7bis). */
public class Departement {
    private String nom;
    private int effectif;

    public Departement() {
    }

    public String getNom() { return nom; }
    public int getEffectif() { return effectif; }

    @Override
    public String toString() {
        return nom + " (" + effectif + " personnes)";
    }
}
