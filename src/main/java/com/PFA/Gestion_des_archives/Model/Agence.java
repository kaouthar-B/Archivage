package com.PFA.Gestion_des_archives.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Ou une autre stratégie
    private Long id;
    private String nom;
    private String adresse;
    private String numeroTel;
    private String mail;


    @ManyToOne
    @JoinColumn(name = "plan_classification_id")
    private PlanClassification planClassification;

    @ManyToOne
    @JoinColumn(name = "entite_rattachee_id")
    private EntiteRattachee entiteRattachee;

    @OneToMany
    @JoinColumn(name = "agence_id")
    private List<CategorieArchive> categoriesArchive;

    public Agence() {
    }


    public Agence(String nom, String adresse, EntiteRattachee entiteRattachee, String numeroTel, String mail) {
        this.nom = nom;
        this.adresse = adresse;
        this.entiteRattachee = entiteRattachee;
        this.numeroTel = numeroTel;
        this.mail = mail;
        entiteRattachee.addAgence(this);
    }

    // Getters
    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public EntiteRattachee getEntiteRattachee() {
        return entiteRattachee;
    }

    public String getNumeroTel() {
        return numeroTel;
    }

    public String getMail() {
        return mail;
    }

    // Setters
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setEntiteRattachee(EntiteRattachee entiteRattachee) {
        this.entiteRattachee = entiteRattachee;
    }

    public void setNumeroTel(String numeroTel) {
        this.numeroTel = numeroTel;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public List<CategorieArchive> getCategoriesArchive() {
        return categoriesArchive;


    }

    // Méthode pour ajouter une catégorie d'archive
    public void addCategorieArchive(CategorieArchive categorie) {
        if (!categoriesArchive.contains(categorie)) {
            categoriesArchive.add(categorie);
            categorie.setAgence(this);
        }
    }


}