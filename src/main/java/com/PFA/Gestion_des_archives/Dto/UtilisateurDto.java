package com.PFA.Gestion_des_archives.Dto;

import com.PFA.Gestion_des_archives.Model.Role;

public class UtilisateurDto {
    private String nom;
    private String prenom;
    private String email;
    private Role role;

    public UtilisateurDto(String nom, String prenom, String email, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
