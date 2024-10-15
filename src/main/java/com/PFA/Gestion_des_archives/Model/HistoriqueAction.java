package com.PFA.Gestion_des_archives.Model;
import com.PFA.Gestion_des_archives.Model.TypeActionHistorique;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class HistoriqueAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeActionHistorique typeAction;

    @Enumerated(EnumType.STRING)
    private Action action;

    private LocalDateTime dateHeure;

    private String titre;

    @ManyToOne
    @JoinColumn(name = "archive_id", nullable = true)
    private Archive archive;

    @ManyToOne
    @JoinColumn(name = "conteneur_id", nullable = true)
    private Conteneur conteneur;

    @ManyToOne
    @JoinColumn(name = "site_archivage_id", nullable = true)
    private SiteArchivage siteArchivage;

    @ManyToOne
    @JoinColumn(name = "plan_classification_id", nullable = true)
    private PlanClassification planClassification;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = true)
    private Utilisateur utilisateur;

    // Constructors
    public HistoriqueAction() {
    }

    public HistoriqueAction(TypeActionHistorique typeAction, Action action, Archive archive,
                            Conteneur conteneur, SiteArchivage siteArchivage,
                            PlanClassification planClassification, Utilisateur utilisateur, String titre) {
        this.typeAction = typeAction;
        this.action = action;
        this.dateHeure = LocalDateTime.now();
        this.archive = archive;
        this.conteneur = conteneur;
        this.siteArchivage = siteArchivage;
        this.planClassification = planClassification;
        this.utilisateur = utilisateur;
        this.titre = titre;
    }


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeActionHistorique getTypeAction() {
        return typeAction;
    }

    public void setTypeAction(TypeActionHistorique typeAction) {
        this.typeAction = typeAction;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public Archive getArchive() {
        return archive;
    }

    public void setArchive(Archive archive) {
        this.archive = archive;
    }

    public Conteneur getConteneur() {
        return conteneur;
    }

    public void setConteneur(Conteneur conteneur) {
        this.conteneur = conteneur;
    }

    public SiteArchivage getSiteArchivage() {
        return siteArchivage;
    }

    public void setSiteArchivage(SiteArchivage siteArchivage) {
        this.siteArchivage = siteArchivage;
    }

    public PlanClassification getPlanClassification() {
        return planClassification;
    }

    public void setPlanClassification(PlanClassification planClassification) {
        this.planClassification = planClassification;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }
}
