package com.PFA.Gestion_des_archives.Model;

public enum ConteneurStatus {
    TOTALEMENT_SATURE("conteneurs totalement saturés"),
    QUASIMENT_SATURE("quasiment saturés"),
    VIDE("vides");

    private String status;

    ConteneurStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return this.status;
    }
}
