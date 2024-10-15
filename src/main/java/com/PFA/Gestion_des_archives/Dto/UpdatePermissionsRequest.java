package com.PFA.Gestion_des_archives.Dto;

import java.util.Map;

public class UpdatePermissionsRequest {

    private int idUtilisateur;
    private Map<String, Boolean> permissions;

    // Constructeurs
    public UpdatePermissionsRequest() {}

    public UpdatePermissionsRequest(int idUtilisateur, Map<String, Boolean> permissions) {
        this.idUtilisateur = idUtilisateur;
        this.permissions = permissions;
    }

    // Getters et Setters
    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, Boolean> permissions) {
        this.permissions = permissions;
    }
}

