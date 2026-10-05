package com.app.model;

public class Mvt {

    private final int id;
    private final String libelle;
    private final String type;
    private final double montant;

    public Mvt(int id, String libelle, String type, double montant) {
        this.id = id;
        this.libelle = libelle;
        this.type = type;
        this.montant = montant;
    }

    public int getId() { return id; }
    public String getLibelle() { return libelle; }
    public String getType() { return type; }
    public double getMontant() { return montant; }
}