package com.PFA.Gestion_des_archives.Model;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "utilisateur")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "utilisateur")
    private List<HistoriqueAction> historiqueActions;

    // Constructeur par défaut
    public Utilisateur() {
    }

    // Constructeur avec tous les champs
    public Utilisateur(String nom, String prenom, String email, String motDePasse, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }


    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public Role getRole() {
        return role;
    }

    // Setters

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public void setRole(Role role) {
        this.role = role;
    }



    // Méthodes
    public void seConnecter() {
        // Implémentation
    }

    public void seDeconnecter() {
        // Implémentation
    }

    public void modifierProfil() {
        // Implémentation
    }

    public void afficherProfil() {
        // Implémentation
    }

    public void gererParametres() {
        // Implémentation
    }

    public class UnauthorizedException extends Exception {
        public UnauthorizedException(String message) {
            super(message);
        }

    }
}


