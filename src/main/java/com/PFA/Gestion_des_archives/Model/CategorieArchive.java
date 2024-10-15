package com.PFA.Gestion_des_archives.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class CategorieArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String code;


    @ManyToOne
    @JoinColumn(name = "plan_classification_id")
    private PlanClassification planClassification;

    @ManyToOne
    @JoinColumn(name = "entite_rattachee_id", nullable = false)
    private EntiteRattachee entiteRattachee;

    @ManyToOne
    @JoinColumn(name = "agence_id", nullable = true)
    private Agence agence;

    @OneToMany(mappedBy = "categorieArchive", cascade = CascadeType.ALL)
    private List<Archive> archive;

    // Constructeurs, getters et setters
    public CategorieArchive() {
    }

    public CategorieArchive(String nom, String code) {
        this.nom = nom;
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public EntiteRattachee getEntiteRattachee() {
        return entiteRattachee;
    }

    public void setEntiteRattachee(EntiteRattachee entiteRattachee) {
        this.entiteRattachee = entiteRattachee;
    }

    public List<Archive> getArchives() {
        return archive;
    }

    public void setArchives(List<Archive> archives) {
        this.archive = archive;
    }

    public void setAgence(Agence agence) {
        this.agence = agence;
    }
}
