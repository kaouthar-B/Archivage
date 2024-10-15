package com.PFA.Gestion_des_archives.Controller;

import com.PFA.Gestion_des_archives.Exception.UnauthorizedException;
import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Service.ConteneurService;
import com.PFA.Gestion_des_archives.Service.HistoriqueActionService;
import com.PFA.Gestion_des_archives.Service.PermissionService;
import com.PFA.Gestion_des_archives.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/conteneur")
public class ConteneurController {

    @Autowired
    private ConteneurService conteneurService;

    @Autowired
    private HistoriqueActionService historiqueActionService;
    @Autowired
    private PermissionService permissionService;

    @GetMapping("/all")
    public ResponseEntity<List<Conteneur>> getAllConteneurs() {
        List<Conteneur> conteneurs = conteneurService.getAllConteneur();
        return new ResponseEntity<>(conteneurs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conteneur> getConteneurById(@PathVariable Long id) {
        Conteneur conteneur = conteneurService.getConteneurById(id);
        return new ResponseEntity<>(conteneur, HttpStatus.OK);
    }

    @PostMapping("/conteneurs")
    public ResponseEntity<Conteneur> addConteneur(@RequestBody Conteneur conteneur) {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanAddConteneur()) {
            Conteneur newConteneur = conteneurService.addConteneur(conteneur);
            return ResponseEntity.ok(newConteneur);
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour ajouter un conteneur.");
        }
    }


    @PutMapping("/conteneurs/{id}")
    public ResponseEntity<Conteneur> updateConteneur(@PathVariable Long id, @RequestBody Conteneur conteneur) {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanUpdateConteneur()) {
            Conteneur updatedConteneur = conteneurService.updateConteneur(id, conteneur);
            return ResponseEntity.ok(updatedConteneur);
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour modifier un conteneur.");
        }
    }


    @DeleteMapping("/conteneurs/{id}")
    public ResponseEntity<Void> deleteConteneur(@PathVariable Long id) {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanDeleteConteneur()) {
            conteneurService.deleteConteneur(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour supprimer un conteneur.");
        }
    }


    @GetMapping("/count/{siteId}/{status}")
    public ResponseEntity<Long> countConteneursByStatus(@PathVariable Long siteId, @PathVariable ConteneurStatus status) {
        long count = conteneurService.countConteneursByStatus(siteId, status);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/status/{siteId}/{status}")
    public ResponseEntity<List<Conteneur>> findConteneursByStatus(@PathVariable Long siteId, @PathVariable ConteneurStatus status) {
        List<Conteneur> conteneurs = conteneurService.findConteneursByStatus(siteId, status);
        return new ResponseEntity<>(conteneurs, HttpStatus.OK);
    }

    @GetMapping("/archives/{conteneurId}")
    public ResponseEntity<List<Archive>> getArchivesByConteneur(@PathVariable Long conteneurId) {
        List<Archive> archives = conteneurService.getArchivesByConteneur(conteneurId);
        return new ResponseEntity<>(archives, HttpStatus.OK);
    }

    @PostMapping("/conteneurs/import")
    public ResponseEntity<Void> importConteneursFromExcel(@RequestParam("file") MultipartFile file) throws IOException {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanImportConteneursFromExcel()) {
            conteneurService.importConteneursFromExcel(file);
            return ResponseEntity.ok().build();
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour importer des conteneurs depuis un fichier Excel.");
        }
    }


    @GetMapping("/conteneurs/export")
    public ResponseEntity<ByteArrayInputStream> exportConteneursToExcel() throws IOException {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanExportConteneursToExcel()) {
            ByteArrayInputStream in = conteneurService.exportConteneursToExcel();
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(in);
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour exporter des conteneurs vers un fichier Excel.");
        }
    }


    @PostMapping("/conteneurs/{siteId}/affectation-provisoire")
    public ResponseEntity<Conteneur> addAffectationProvisoire(@PathVariable Long siteId, @RequestBody Conteneur conteneurData) {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanAffecterProvisoire()) {
            Conteneur conteneur = conteneurService.addAffectationProvisoire(siteId, conteneurData);
            return ResponseEntity.ok(conteneur);
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour ajouter une affectation provisoire.");
        }
    }


    @PostMapping("/conteneurs/{siteId}/{conteneurId}/affectation-definitive")
    public ResponseEntity<Conteneur> addAffectationDefinitive(@PathVariable Long siteId, @PathVariable Long conteneurId, @RequestBody Conteneur conteneurData) {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanAffecterDefinitive()) {
            Conteneur conteneur = conteneurService.addAffectationDefinitive(siteId, conteneurId, conteneurData);
            return ResponseEntity.ok(conteneur);
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour ajouter une affectation définitive.");
        }
    }

}
