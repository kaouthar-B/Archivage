package com.PFA.Gestion_des_archives.Model;

import javax.persistence.*;
import java.time.Month;
import java.time.Year;
import java.util.Date;

@Entity
public class Archive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refTransfert;
    private String correspondant;
    private Date dateDeTransfert;
    private String numeroConteneur;

    // Champs provenant de EntiteRattachee
    private String localOrigine;
    private String refEnt;
    private String codeEntite;

    // Champs provenant de CategorieArchive
    private String nature;

    // Champs provenant de Agence
    private String nomAgence;
    private String adresseAgence;

    // Champs provenant de Conteneur
    private String emplacement;

    // Champs générés automatiquement
    private Year anneeCreation; // Utilisation de Year
    private Month moisCreation;  // Utilisation de Month
    private Integer jourCreation;     // Utilisation d'un int pour le jour

    private String observations;

    @ManyToOne
    @JoinColumn(name = "entite_rattachee_id", nullable = false)
    private EntiteRattachee entiteRattachee;

    @ManyToOne
    @JoinColumn(name = "conteneur_id", nullable = false)
    private Conteneur conteneur;

    @ManyToOne
    @JoinColumn(name = "categorie_archive_id", nullable = false)
    private CategorieArchive categorieArchive;

    @ManyToOne
    @JoinColumn(name = "agence_id", nullable = false)
    private Agence agence;

    @ManyToOne
    @JoinColumn(name = "site_archivage_id", nullable = false)
    private SiteArchivage siteArchivage;
    private String nom;

    // Constructeurs
    public Archive() {
        // Initialisation par défaut
    }

    public Archive(String refTransfert, String correspondant, Date dateDeTransfert, String numeroConteneur,
                   EntiteRattachee entiteRattachee, CategorieArchive categorieArchive, Agence agence, Conteneur conteneur,
                   String observations, Year anneeCreation, Month moisCreation, Integer jourCreation) {
        this.refTransfert = refTransfert;
        this.correspondant = correspondant;
        this.dateDeTransfert = dateDeTransfert;
        this.numeroConteneur = numeroConteneur;
        this.entiteRattachee = entiteRattachee;
        this.localOrigine = entiteRattachee.getLocalOrigine();
        this.refEnt = entiteRattachee.getRef();
        this.codeEntite = entiteRattachee.getCodeEntite();
        this.categorieArchive = categorieArchive;
        this.nature = categorieArchive.getNom();
        this.agence = agence;
        this.nomAgence = agence.getNom();
        this.adresseAgence = agence.getAdresse();
        this.conteneur = conteneur;
        this.emplacement = conteneur.getEmplacement();
        this.observations = observations;
        this.anneeCreation = anneeCreation; // Assignation de l'année
        this.moisCreation = moisCreation;   // Assignation du mois
        this.jourCreation = jourCreation;   // Assignation du jour
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRefTransfert() {
        return refTransfert;
    }

    public void setRefTransfert(String refTransfert) {
        this.refTransfert = refTransfert;
    }

    public String getCorrespondant() {
        return correspondant;
    }

    public void setCorrespondant(String correspondant) {
        this.correspondant = correspondant;
    }

    public Date getDateDeTransfert() {
        return dateDeTransfert;
    }

    public void setDateDeTransfert(Date dateDeTransfert) {
        this.dateDeTransfert = dateDeTransfert;
    }

    public String getNumeroConteneur() {
        return numeroConteneur;
    }

    public void setNumeroConteneur(String numeroConteneur) {
        this.numeroConteneur = numeroConteneur;
    }

    public String getLocalOrigine() {
        return localOrigine;
    }

    public void setLocalOrigine(String localOrigine) {
        this.localOrigine = localOrigine;
    }

    public String getRefEnt() {
        return refEnt;
    }

    public void setRefEnt(String refEnt) {
        this.refEnt = refEnt;
    }

    public String getCodeEntite() {
        return codeEntite;
    }

    public void setCodeEntite(String codeEntite) {
        this.codeEntite = codeEntite;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getNomAgence() {
        return nomAgence;
    }

    public void setNomAgence(String nomAgence) {
        this.nomAgence = nomAgence;
    }

    public String getAdresseAgence() {
        return adresseAgence;
    }

    public void setAdresseAgence(String adresseAgence) {
        this.adresseAgence = adresseAgence;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public Year getAnneeCreation() {
        return anneeCreation; // Renvoie Year
    }

    public void setAnneeCreation(Year anneeCreation) {
        this.anneeCreation = anneeCreation; // Assignation de Year
    }

    public Month getMoisCreation() {
        return moisCreation; // Renvoie Month
    }

    public void setMoisCreation(Month moisCreation) {
        this.moisCreation = moisCreation; // Assignation de Month
    }

    public Integer getJourCreation() {
        return jourCreation; // Renvoie int
    }

    public void setJourCreation(Integer jourCreation) {
        this.jourCreation = jourCreation; // Assignation de jour
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public EntiteRattachee getEntiteRattachee() {
        return entiteRattachee;
    }

    public void setEntiteRattachee(EntiteRattachee entiteRattachee) {
        this.entiteRattachee = entiteRattachee;
        this.localOrigine = entiteRattachee.getLocalOrigine();
        this.refEnt = entiteRattachee.getRef();
        this.codeEntite = entiteRattachee.getCodeEntite();
    }

    public Conteneur getConteneur() {
        return conteneur;
    }

    public void setConteneur(Conteneur conteneur) {
        this.conteneur = conteneur;
        this.emplacement = conteneur.getEmplacement();
    }

    public CategorieArchive getCategorieArchive() {
        return categorieArchive;
    }

    public void setCategorieArchive(CategorieArchive categorieArchive) {
        this.categorieArchive = categorieArchive;
        this.nature = categorieArchive.getNom();
    }

    public Agence getAgence() {
        return agence;
    }

    public void setAgence(Agence agence) {
        this.agence = agence;
        this.nomAgence = agence.getNom();
        this.adresseAgence = agence.getAdresse();
    }


    // Getters and Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
