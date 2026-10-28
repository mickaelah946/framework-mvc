package com.app.model;

import com.framework.model.MapFile;

import java.util.List;

/** Objet rempli automatiquement avec les champs du formulaire (sprint 7bis). */
public class Employe {
    private String nom;
    private String prenom;
    private Integer age;
    private String matricule;
    private Adresse adresse;          // objet dans l'objet : adresse.rue, adresse.ville
    private List<String> langues;     // liste de types simples : langues[0], langues[1]
    private MapFile rectoCIN;         // sprint 8 : fichier du champ rectoCIN (formulaire multipart)
    private MapFile versoCIN;         // sprint 8 : fichier du champ versoCIN

    public Employe() {
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public Adresse getAdresse() { return adresse; }
    public void setAdresse(Adresse adresse) { this.adresse = adresse; }
    public List<String> getLangues() { return langues; }
    public void setLangues(List<String> langues) { this.langues = langues; }
    public MapFile getRectoCIN() { return rectoCIN; }
    public void setRectoCIN(MapFile rectoCIN) { this.rectoCIN = rectoCIN; }
    public MapFile getVersoCIN() { return versoCIN; }
    public void setVersoCIN(MapFile versoCIN) { this.versoCIN = versoCIN; }

    @Override
    public String toString() {
        return nom + " " + prenom + ", " + age + " ans"
                + (adresse != null ? ", adresse : " + adresse : "")
                + (langues != null ? ", langues : " + langues : "");
    }
}
