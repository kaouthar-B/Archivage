package com.PFA.Gestion_des_archives.Model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
public class Conteneur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numero;
    private String type;
    private String emplacement;
    private LocalDateTime dateAffectation;


    @Enumerated(EnumType.STRING)
    private ConteneurStatus status;

    @Enumerated(EnumType.STRING)
    private TypeAffectation typeAffectation; // Nouveau champ pour gérer le type d'affectation

    private LocalDateTime dateConfirmation; // Date de confirmation de l'affectation provisoire

    @ManyToOne
    @JoinColumn(name = "site_archivage_id", nullable = false)
    private SiteArchivage siteArchivage;

    @OneToMany(mappedBy = "conteneur", cascade = CascadeType.ALL)
    private List<HistoriqueAction> historiqueActions;

    @OneToMany(mappedBy = "conteneur", cascade = CascadeType.ALL)
    private List<Archive> archives;


    // provisoire forcé, compteur(statistique)

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;


    // Constructeurs, getters et setters
    public Conteneur() {
    }

    public Conteneur(String numero, String type, String emplacement, ConteneurStatus status, TypeAffectation typeAffectation, SiteArchivage siteArchivage) {
        this.numero = numero;
        this.type = type;
        this.emplacement = emplacement;
        this.status = status;
        this.typeAffectation = typeAffectation;
        this.siteArchivage = siteArchivage;
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {         //Elle prend un seul paramètre de type “String” appelé “numero”.
        this.numero = numero;               //affecte la valeur du paramètre “numero” à la variable d’instance “numero” (en mauve) de l’objet actuel.
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public ConteneurStatus getStatus() {
        return status;
    }

    public void setStatus(ConteneurStatus status) {
        this.status = status;
    }

    public TypeAffectation getTypeAffectation() {
        return typeAffectation;
    }

    public void setTypeAffectation(TypeAffectation typeAffectation) {
        this.typeAffectation = typeAffectation;
    }

    public LocalDateTime getDateConfirmation() {
        return dateConfirmation;
    }

    public void setDateConfirmation(LocalDateTime dateConfirmation) {
        this.dateConfirmation = dateConfirmation;
    }


    public SiteArchivage getSiteArchivage() {
        return siteArchivage;
    }

    public void setSiteArchivage(SiteArchivage siteArchivage) {
        this.siteArchivage = siteArchivage;
    }

    public List<HistoriqueAction> getHistoriqueActions() {
        return historiqueActions;
    }

    public void setHistoriqueActions(List<HistoriqueAction> historiqueActions) {
        this.historiqueActions = historiqueActions;
    }

    public List<Archive> getArchivesConteneur() {
        return archives;
    }

    public void setArchivesConteneur(List<Archive> archives) {
        this.archives = archives;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public LocalDateTime getDateAffectation() {
        return dateAffectation;
    }

    public void setDateAffectation(LocalDateTime dateAffectation) {
        this.dateAffectation = dateAffectation;
    }
}
