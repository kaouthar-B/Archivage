package com.PFA.Gestion_des_archives.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EntiteRattachee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String ref;
    private String codeEntite;
    private String localOrigine;

    @OneToMany(mappedBy = "entiteRattachee", cascade = CascadeType.ALL)
    private List<Agence> agences;

    @ManyToOne
    @JoinColumn(name = "proprietaire_id", nullable = false)
    private ProprietaireDesArchives proprietaireDesArchives;

    @OneToMany(mappedBy = "entiteRattachee", cascade = CascadeType.ALL)
    private List<CategorieArchive> categoriesArchives;

    @ManyToOne
    @JoinColumn(name = "planClassification_id", nullable = false)
    private PlanClassification planClassification;

    // Constructeurs, getters et setters
    public EntiteRattachee() {}

    public EntiteRattachee(String nom, String ref, String codeEntite) {
        this.nom = nom;
        this.ref = ref;
        this.codeEntite = codeEntite;
        this.categoriesArchives = new ArrayList<>();
        this.agences = new ArrayList<>();
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


    public PlanClassification getPlanClassification() {
        return planClassification;
    }

    public void setPlanClassification(PlanClassification planClassification) {
        this.planClassification = planClassification;
    }
    public ProprietaireDesArchives getProprietaireDesArchives() {
        return proprietaireDesArchives;
    }

    public void setProprietaireDesArchives(ProprietaireDesArchives proprietaireDesArchives) {
        this.proprietaireDesArchives = proprietaireDesArchives;
    }

    public List<CategorieArchive> getCategoriesArchives() {
        return categoriesArchives;
    }

    public void setCategoriesArchives(List<CategorieArchive> categoriesArchives) {
        this.categoriesArchives = categoriesArchives;
    }

    public String getLocalOrigine() {
        return localOrigine;
    }

    public void setLocalOrigine(String localOrigine) {
        this.localOrigine = localOrigine;
    }

    public void addAgence(Agence agence) {
        this.agences.add(agence);
    }

    public List<Agence> getAgences() {
        return agences;
    }

    // Getter pour obtenir une agence par nom
    public Agence getAgenceByName(String nom) {
        for (Agence agence : agences) {
            if (agence.getNom().equals(nom)) {
                return agence;
            }
        }
        return null; // Retourne null si aucune agence n'est trouvée avec ce nom
    }


    public List<CategorieArchive> getCategoriesArchive() {
        return categoriesArchives;
    }

    // Méthode pour ajouter une catégorie d'archive
    public void addCategorieArchive(CategorieArchive categorie) {
        if (!categoriesArchives.contains(categorie)) {
            categoriesArchives.add(categorie);
            categorie.setEntiteRattachee(this);
        }
    }

}
