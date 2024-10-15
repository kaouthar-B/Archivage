package com.PFA.Gestion_des_archives.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class PlanClassification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "planClassification", cascade = CascadeType.ALL)
    private List<ProprietaireDesArchives> proprietairesDesArchives;


    @OneToMany(mappedBy = "planClassification", cascade = CascadeType.ALL)
    private List<EntiteRattachee> entitesRattachees;

    @OneToMany(mappedBy = "planClassification", cascade = CascadeType.ALL)
    private List<CategorieArchive> categoriesArchive;

    @OneToMany(mappedBy = "planClassification", cascade = CascadeType.ALL)
    private List<Agence> agences;

    @Column(nullable = false)
    private String ref;

    @Column(nullable = false)
    private String codeEntite;

    @Column(nullable = false)
    private String code;

    // Constructeurs
    public PlanClassification() {
    }

    public PlanClassification(List<ProprietaireDesArchives> proprietairesDesArchives, List<EntiteRattachee> entitesRattachees,
                              List<CategorieArchive> categoriesArchive, String ref, String codeEntite, List<Agence> agences) {
        this.proprietairesDesArchives = proprietairesDesArchives;
        this.entitesRattachees = entitesRattachees;
        this.categoriesArchive = categoriesArchive;
        this.ref = ref;
        this.codeEntite = codeEntite;
        this.agences = agences;
        this.code = code;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ProprietaireDesArchives> getProprietairesDesArchives() {
        return proprietairesDesArchives;
    }

    public void setProprietairesDesArchives(List<ProprietaireDesArchives> proprietairesDesArchives) {
        this.proprietairesDesArchives = proprietairesDesArchives;
    }

    public List<EntiteRattachee> getEntitesRattachees() {
        return entitesRattachees;
    }

    public void setEntitesRattachees(List<EntiteRattachee> entitesRattachees) {
        this.entitesRattachees = entitesRattachees;
    }

    public List<CategorieArchive> getCategoriesArchive() {
        return categoriesArchive;
    }

    public void setCategoriesArchive(List<CategorieArchive> categoriesArchive) {
        this.categoriesArchive = categoriesArchive;
    }

    public List<Agence> getAgences() {
        return agences;
    }

    public void setAgences(List<Agence> agences) {
        this.agences = agences;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getCodeEntite() {
        return codeEntite;
    }

    public void setCodeEntite(String codeEntite) {
        this.codeEntite = codeEntite;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    // Méthodes pour obtenir des noms extraits des autres classes (pour la première instance dans la liste)
    public String getNomProprietaire() {
        return !this.proprietairesDesArchives.isEmpty() ? this.proprietairesDesArchives.get(0).getNom() : null;
    }

    public String getNomEntiteRattachee() {
        return !this.entitesRattachees.isEmpty() ? this.entitesRattachees.get(0).getRef() + " - " + this.entitesRattachees.get(0).getCodeEntite() : null;
    }

    public String getNomCategorieArchive() {
        return !this.categoriesArchive.isEmpty() ? this.categoriesArchive.get(0).getCode() : null;
    }

    public String getNomAgence() {
        return !this.agences.isEmpty() ? this.agences.get(0).getNom() : null;
    }
}
