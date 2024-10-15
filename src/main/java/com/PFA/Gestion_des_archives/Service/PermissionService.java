package com.PFA.Gestion_des_archives.Service;

import com.PFA.Gestion_des_archives.Model.Permission;
import com.PFA.Gestion_des_archives.Model.SessionContext;
import com.PFA.Gestion_des_archives.Model.Utilisateur;
import com.PFA.Gestion_des_archives.Repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private SessionContext sessionContext;

    // Créer ou mettre à jour les permissions pour l'utilisateur connecté
    public Permission createOrUpdatePermissions(Permission newPermissions) {
        Utilisateur utilisateur = sessionContext.getCurrentUser();
        if (utilisateur == null) {
            throw new RuntimeException("Utilisateur non connecté");
        }

        Optional<Permission> existingPermission = permissionRepository.findByUtilisateur(utilisateur);

        if (existingPermission.isPresent()) {
            Permission permission = existingPermission.get();
            permission.setCanAddArchive(newPermissions.isCanAddArchive());
            permission.setCanGetArchiveById(newPermissions.isCanGetArchiveById());
            permission.setCanUpdateArchive(newPermissions.isCanUpdateArchive());
            permission.setCanDeleteArchive(newPermissions.isCanDeleteArchive());
            permission.setCanGetCopieNumerique(newPermissions.isCanGetCopieNumerique());
            permission.setCanDownloadCopieNumerique(newPermissions.isCanDownloadCopieNumerique());
            permission.setCanImportArchiveFromExcel(newPermissions.isCanImportArchiveFromExcel());
            permission.setCanExportArchiveToExcel(newPermissions.isCanExportArchiveToExcel());
            permission.setCanAddConteneur(newPermissions.isCanAddConteneur());
            permission.setCanUpdateConteneur(newPermissions.isCanUpdateConteneur());
            permission.setCanDeleteConteneur(newPermissions.isCanDeleteConteneur());
            permission.setCanAffecterProvisoire(newPermissions.isCanAffecterProvisoire());
            permission.setCanAffecterDefinitive(newPermissions.isCanAffecterDefinitive());
            permission.setCanImportConteneursFromExcel(newPermissions.isCanImportConteneursFromExcel());
            permission.setCanExportConteneursToExcel(newPermissions.isCanExportConteneursToExcel());
            permission.setCanAddSite(newPermissions.isCanAddSite());
            permission.setCanUpdateSite(newPermissions.isCanUpdateSite());
            permission.setCanDeleteSite(newPermissions.isCanDeleteSite());
            permission.setCanImportSiteArchivageFromExcel(newPermissions.isCanImportSiteArchivageFromExcel());
            permission.setCanExportSiteArchivageToExcel(newPermissions.isCanExportSiteArchivageToExcel());
            return permissionRepository.save(permission);
        } else {
            newPermissions.setUtilisateur(utilisateur);
            return permissionRepository.save(newPermissions);
        }
    }

    // Récupérer les permissions d'un utilisateur
    public Optional<Permission> getPermissionsByUtilisateur(Optional<Utilisateur> utilisateur) {
        Utilisateur user = utilisateur.orElseThrow(() -> new RuntimeException("User not found"));
        return permissionRepository.findByUtilisateur(user);
    }


}
