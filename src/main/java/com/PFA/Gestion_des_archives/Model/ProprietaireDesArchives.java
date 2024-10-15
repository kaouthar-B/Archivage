package com.PFA.Gestion_des_archives.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class ProprietaireDesArchives {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;

    @ManyToOne
    @JoinColumn(name = "plan_classification_id", nullable = false)
    private PlanClassification planClassification;

    @OneToMany(mappedBy = "proprietaireDesArchives")
    private List<EntiteRattachee> entitesRattachees;

    // Constructeurs, getters et setters
    public ProprietaireDesArchives() {}

    public ProprietaireDesArchives(String nom) {
        this.nom = nom;
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

    public PlanClassification getPlanClassification() {
        return planClassification;
    }

    public void setPlanClassification(PlanClassification planClassification) {
        this.planClassification = planClassification;
    }

    public List<EntiteRattachee> getEntitesRattachees() {
        return entitesRattachees;
    }

    public void setEntitesRattachees(List<EntiteRattachee> entitesRattachees) {
        this.entitesRattachees = entitesRattachees;
    }
}
