package com.PFA.Gestion_des_archives.Dto;

import java.util.ArrayList;
import java.util.List;

public class PlanClassificationDto {
    private String ref;
    private String code;
    private List<String> proprietairesDesArchives = new ArrayList<>();
    private List<String> entitesRattachees = new ArrayList<>();
    private List<String> codeEntite = new ArrayList<>();
    private List<String> refEntite = new ArrayList<>();
    private List<String> agences = new ArrayList<>();
    private List<String> categoriesArchive = new ArrayList<>();

    // Getters and Setters
    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getProprietairesDesArchives() {
        return proprietairesDesArchives;
    }

    public void addProprietaireDesArchives(String proprietaire) {
        this.proprietairesDesArchives.add(proprietaire);
    }

    public List<String> getEntitesRattachees() {
        return entitesRattachees;
    }

    public void addEntitesRattachees(String entite) {
        this.entitesRattachees.add(entite);
    }

    public List<String> getCodeEntite() {
        return codeEntite;
    }

    public void addCodeEntite(String codeEntite) {
        this.codeEntite.add(codeEntite);
    }

    public List<String> getRefEntite() {
        return refEntite;
    }

    public void addRef(String refEntite) {
        this.refEntite.add(refEntite);
    }

    public List<String> getAgences() {
        return agences;
    }

    public void addAgences(String agence) {
        this.agences.add(agence);
    }

    public List<String> getCategoriesArchive() {
        return categoriesArchive;
    }

    public void addCategoriesArchive(String categorie) {
        this.categoriesArchive.add(categorie);
    }
}

