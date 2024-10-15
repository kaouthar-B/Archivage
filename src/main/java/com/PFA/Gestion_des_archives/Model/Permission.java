package com.PFA.Gestion_des_archives.Model;

import javax.persistence.*;

@Entity
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    private boolean canAddArchive;
    private boolean canGetArchiveById;
    private boolean canUpdateArchive;
    private boolean canDeleteArchive;
    private boolean canGetCopieNumerique;
    private boolean canDownloadCopieNumerique;
    private boolean canImportArchiveFromExcel;
    private boolean canExportArchiveToExcel;
    private boolean canAddConteneur;
    private boolean canUpdateConteneur;
    private boolean canDeleteConteneur;
    private boolean canAffecterProvisoire;
    private boolean canAffecterDefinitive;
    private boolean canImportConteneursFromExcel;
    private boolean canExportConteneursToExcel;
    private boolean canAddSite;
    private boolean canUpdateSite;
    private boolean canDeleteSite;
    private boolean canImportSiteArchivageFromExcel;
    private boolean canExportSiteArchivageToExcel;

    // Constructeurs
    public Permission() {
    }

    public Permission(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public boolean isCanAddArchive() {
        return canAddArchive;
    }

    public void setCanAddArchive(boolean canAddArchive) {
        this.canAddArchive = canAddArchive;
    }

    public boolean isCanGetArchiveById() {
        return canGetArchiveById;
    }

    public void setCanGetArchiveById(boolean canGetArchiveById) {
        this.canGetArchiveById = canGetArchiveById;
    }

    public boolean isCanUpdateArchive() {
        return canUpdateArchive;
    }

    public void setCanUpdateArchive(boolean canUpdateArchive) {
        this.canUpdateArchive = canUpdateArchive;
    }

    public boolean isCanDeleteArchive() {
        return canDeleteArchive;
    }

    public void setCanDeleteArchive(boolean canDeleteArchive) {
        this.canDeleteArchive = canDeleteArchive;
    }

    public boolean isCanGetCopieNumerique() {
        return canGetCopieNumerique;
    }

    public void setCanGetCopieNumerique(boolean canGetCopieNumerique) {
        this.canGetCopieNumerique = canGetCopieNumerique;
    }

    public boolean isCanDownloadCopieNumerique() {
        return canDownloadCopieNumerique;
    }

    public void setCanDownloadCopieNumerique(boolean canDownloadCopieNumerique) {
        this.canDownloadCopieNumerique = canDownloadCopieNumerique;
    }


    public void setCanImportArchiveFromExcel(boolean canImportArchiveFromExcel) {
        this.canImportArchiveFromExcel = canImportArchiveFromExcel;
    }
    public boolean isCanImportArchiveFromExcel() {
        return canImportArchiveFromExcel;
    }


    public void setCanExportArchiveToExcel(boolean canExportArchiveToExcel) {
        this.canExportArchiveToExcel = canExportArchiveToExcel;
    }
    public boolean isCanExportArchiveToExcel() {
        return canExportArchiveToExcel;
    }

    public boolean isCanAddConteneur() {
        return canAddConteneur;
    }

    public void setCanAddConteneur(boolean canAddConteneur) {
        this.canAddConteneur = canAddConteneur;
    }

    public boolean isCanUpdateConteneur() {
        return canUpdateConteneur;
    }

    public void setCanUpdateConteneur(boolean canUpdateConteneur) {
        this.canUpdateConteneur = canUpdateConteneur;
    }

    public boolean isCanDeleteConteneur() {
        return canDeleteConteneur;
    }

    public void setCanDeleteConteneur(boolean canDeleteConteneur) {
        this.canDeleteConteneur = canDeleteConteneur;
    }

    public boolean isCanAffecterProvisoire() {
        return canAffecterProvisoire;
    }

    public void setCanAffecterProvisoire(boolean canAffecterProvisoire) {
        this.canAffecterProvisoire = canAffecterProvisoire;
    }

    public boolean isCanAffecterDefinitive() {
        return canAffecterDefinitive;
    }

    public void setCanAffecterDefinitive(boolean canAffecterDefinitive) {
        this.canAffecterDefinitive = canAffecterDefinitive;
    }

    public boolean isCanImportConteneursFromExcel() {
        return canImportConteneursFromExcel;
    }

    public void setCanImportConteneursFromExcel(boolean canImportConteneursFromExcel) {
        this.canImportConteneursFromExcel = canImportConteneursFromExcel;
    }

    public boolean isCanExportConteneursToExcel() {
        return canExportConteneursToExcel;
    }

    public void setCanExportConteneursToExcel(boolean canExportConteneursToExcel) {
        this.canExportConteneursToExcel = canExportConteneursToExcel;
    }

    public boolean isCanAddSite() {
        return canAddSite;
    }

    public void setCanAddSite(boolean canAddSite) {
        this.canAddSite = canAddSite;
    }

    public boolean isCanUpdateSite() {
        return canUpdateSite;
    }

    public void setCanUpdateSite(boolean canUpdateSite) {
        this.canUpdateSite = canUpdateSite;
    }

    public boolean isCanDeleteSite() {
        return canDeleteSite;
    }

    public void setCanDeleteSite(boolean canDeleteSite) {
        this.canDeleteSite = canDeleteSite;
    }

    public boolean isCanImportSiteArchivageFromExcel() {
        return canImportSiteArchivageFromExcel;
    }

    public void setCanImportSiteArchivageFromExcel(boolean canImportSiteArchivageFromExcel) {
        this.canImportSiteArchivageFromExcel = canImportSiteArchivageFromExcel;
    }

    public boolean isCanExportSiteArchivageToExcel() {
        return canExportSiteArchivageToExcel;
    }

    public void setCanExportSiteArchivageToExcel(boolean canExportSiteArchivageToExcel) {
        this.canExportSiteArchivageToExcel = canExportSiteArchivageToExcel;
    }
}
