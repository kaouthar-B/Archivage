package com.PFA.Gestion_des_archives.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SiteArchivage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String adresse;
    private String localisation;
    private String surface;
    private String archiviste;
    private String personnel;
    private int nombreEmplacements;
    private int nombreEmplacementsCharges;
    private int nombreEmplacementsVides;

    @OneToMany(mappedBy = "siteArchivage", cascade = CascadeType.ALL)
    private List<Conteneur> conteneurs = new ArrayList<>();;

    @OneToMany(mappedBy = "siteArchivage", cascade = CascadeType.ALL)
    private List<HistoriqueAction> historiqueActions = new ArrayList<>();

    // Constructeurs, getters et setters

    public SiteArchivage() {}

    public SiteArchivage(String nom, String adresse, String localisation, String surface, String archiviste, String personnel, int nombreEmplacements, int nombreEmplacementsChargés, int nombreEmplacementsVides) {
        this.nom = nom;
        this.adresse = adresse;
        this.localisation = localisation;
        this.surface = surface;
        this.archiviste = archiviste;
        this.personnel = personnel;
        this.nombreEmplacements = nombreEmplacements;
        this.nombreEmplacementsCharges = nombreEmplacementsChargés;
        this.nombreEmplacementsVides = nombreEmplacementsVides;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public String getArchiviste() {
        return archiviste;
    }

    public void setArchiviste(String archiviste) {
        this.archiviste = archiviste;
    }

    public String getPersonnel() {
        return personnel;
    }

    public void setPersonnel(String personnel) {
        this.personnel = personnel;
    }

    public int getNombreEmplacements() {
        return nombreEmplacements;
    }

    public void setNombreEmplacements(int nombreEmplacements) {
        this.nombreEmplacements = nombreEmplacements;
    }

    public int getNombreEmplacementsCharges() {
        return nombreEmplacementsCharges;
    }

    public void setNombreEmplacementsChargés(int nombreEmplacementsChargés) {
        this.nombreEmplacementsCharges = nombreEmplacementsChargés;
    }

    public int getNombreEmplacementsVides() {
        return nombreEmplacementsVides;
    }

    public void setNombreEmplacementsVides(int nombreEmplacementsVides) {
        this.nombreEmplacementsVides = nombreEmplacementsVides;
    }

    public List<Conteneur> getConteneurs() {
        return conteneurs;
    }

    public void setConteneurs(List<Conteneur> conteneurs) {
        this.conteneurs = conteneurs;
    }

    public List<HistoriqueAction> getHistoriqueActions() {
        return historiqueActions;
    }

    public void setHistoriqueActions(List<HistoriqueAction> historiqueActions) {
        this.historiqueActions = historiqueActions;
    }
}
