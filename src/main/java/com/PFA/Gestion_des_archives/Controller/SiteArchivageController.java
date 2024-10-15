package com.PFA.Gestion_des_archives.Controller;

// Importation des bibliothèques nécessaires
import com.PFA.Gestion_des_archives.Dto.SiteArchivageDto;
import com.PFA.Gestion_des_archives.Exception.ResourceNotFoundException;
import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Service.PermissionService;
import com.PFA.Gestion_des_archives.Service.SiteArchivageService;
import com.PFA.Gestion_des_archives.Service.HistoriqueActionService;
import com.PFA.Gestion_des_archives.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

// Annotation pour indiquer que cette classe est un contrôleur REST
@RestController
// Annotation pour définir la route de base pour ce contrôleur
@RequestMapping("/api/siteArchivage")
public class SiteArchivageController {

    // Injection des services nécessaires
    @Autowired
    private SiteArchivageService siteArchivageService;

    @Autowired
    private HistoriqueActionService historiqueActionService;
    @Autowired
    private PermissionService permissionService;

    // Méthode pour obtenir tous les sites d'archivage
    @GetMapping
    public List<SiteArchivage> getAllSites() {
        return siteArchivageService.getAllSites();
    }

    @GetMapping("/{id}")
    public SiteArchivage getSiteById(@PathVariable Long id) {
        return siteArchivageService.getSiteById(id);
    }

    @PostMapping("/sites")
    public ResponseEntity<SiteArchivage> addSite(@RequestBody SiteArchivage site) {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanAddSite()) {
            SiteArchivage newSite = siteArchivageService.addSite(site);
            return ResponseEntity.ok(newSite);
        } else {
            throw new SecurityException("L'utilisateur n'a pas la permission d'ajouter un site d'archivage.");
        }
    }


    @PutMapping("/sites/{id}")
    public ResponseEntity<SiteArchivage> updateSite(@PathVariable Long id, @RequestBody SiteArchivage site) {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanUpdateSite()) {
            SiteArchivage updatedSite = siteArchivageService.updateSite(id, site);
            return ResponseEntity.ok(updatedSite);
        } else {
            throw new SecurityException("L'utilisateur n'a pas la permission de mettre à jour un site d'archivage.");
        }
    }


    @DeleteMapping("/sites/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
        // Obtenir l'utilisateur connecté à partir de la gestion des sessions
        Utilisateur utilisateur = SessionContext.getCurrentUser();

        // Vérifier si l'utilisateur est un administrateur
        if (utilisateur == null || !utilisateur.getRole().equals("ADMINISTRATEUR")) {
            throw new SecurityException("L'utilisateur n'a pas la permission de supprimer un site d'archivage.");
        }

        // Supprimer le site d'archivage
        siteArchivageService.deleteSite(id);

        // Retourner une réponse HTTP 204 No Content pour indiquer que la suppression a été effectuée
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/conteneurs")
    public List<Conteneur> getConteneursBySiteId(@PathVariable Long id) {
        return siteArchivageService.getConteneursBySiteId(id);
    }

    @PostMapping("/sites/import")
    public ResponseEntity<Void> importSiteArchivageFromExcel(@RequestParam("file") MultipartFile file) throws IOException {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanImportSiteArchivageFromExcel()) {
            siteArchivageService.importSiteArchivageFromExcel(file);
            return ResponseEntity.ok().build();
        } else {
            throw new SecurityException("L'utilisateur n'a pas la permission d'importer des sites d'archivage.");
        }
    }


    @GetMapping("/sites/export")
    public ResponseEntity<byte[]> exportSiteArchivageToExcel() throws IOException {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanExportSiteArchivageToExcel()) {
            byte[] excelData = siteArchivageService.exportSiteArchivageToExcel();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelData);
        } else {
            throw new SecurityException("L'utilisateur n'a pas la permission d'exporter des sites d'archivage.");
        }
    }



    // Méthode pour obtenir le nombre de sites d'archivage
    @GetMapping("/count")
    public long getCountOfSites() {

        return siteArchivageService.getCountOfSites();
    }


    @GetMapping("/{id}/countConteneurs/{status}")
    public long countConteneursByStatus(@PathVariable Long id, @PathVariable String status) {
        return siteArchivageService.countConteneursByStatus(id, status);
    }

    @GetMapping("/{id}/conteneurs/{status}")
    public List<Conteneur> findConteneursByStatus(@PathVariable Long id, @PathVariable String status) {
        return siteArchivageService.findConteneursByStatus(id, status);
    }



    // Agarder cet endpoint :
    @GetMapping("/categories-count")
    public ResponseEntity<List<SiteArchivageDto>> getSiteArchivagesWithCategoryCounts() {
        List<SiteArchivageDto> siteArchivageDtos = siteArchivageService.getAllSiteArchivagesWithCategoryCount();
        return ResponseEntity.ok(siteArchivageDtos);
    }

    @GetMapping("/sites/{id}/emplacements")
    public ResponseEntity<Integer> getNombreEmplacements(@PathVariable("id") Long siteArchivageId) {
        try {
            Integer nombreEmplacements = siteArchivageService.getNombreEmplacements(siteArchivageId);
            return ResponseEntity.ok(nombreEmplacements);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/sites/{id}/emplacements-charges")
    public ResponseEntity<Integer> getNombreEmplacementsCharges(@PathVariable("id") Long siteArchivageId) {
        try {
            Integer nombreEmplacementsCharges = siteArchivageService.getNombreEmplacementsCharges(siteArchivageId);
            return ResponseEntity.ok(nombreEmplacementsCharges);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/sites/{id}/emplacements-vides")
    public ResponseEntity<Integer> getNombreEmplacementsVides(@PathVariable("id") Long siteArchivageId) {
        try {
            Integer nombreEmplacementsVides = siteArchivageService.getNombreEmplacementsVides(siteArchivageId);
            return ResponseEntity.ok(nombreEmplacementsVides);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


}
