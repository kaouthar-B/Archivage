package com.PFA.Gestion_des_archives.Controller;

import com.PFA.Gestion_des_archives.Dto.PlanClassificationDto;
import com.PFA.Gestion_des_archives.Exception.ResourceNotFoundException;
import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Service.PlanClassificationService;
import com.PFA.Gestion_des_archives.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/plan-classification")
public class PlanClassificationController {
    @Autowired
    private PlanClassificationService planClassificationService;
    @Autowired
    private SessionContext sessionContext;



    @GetMapping("/filtre")
    public ResponseEntity<List<Object>> rechercheParFiltre(
            @RequestParam String typeRecherche,
            @RequestParam String searchQuery) {

        PlanClassification planClassification = planClassificationService.getPlanClassification(); // Assuming this method retrieves the PlanClassification object
        List<Object> resultats = planClassificationService.rechercheParFiltre(planClassification, typeRecherche, searchQuery);

        return new ResponseEntity<>(resultats, HttpStatus.OK);
    }

    @GetMapping("/findProprietaire")
    public ResponseEntity<ProprietaireDesArchives> findProprietaire(@RequestParam String proprietaireName) {
        PlanClassification planClassification = planClassificationService.getPlanClassification();
        ProprietaireDesArchives proprietaire = planClassificationService.findProprietaire(planClassification.getProprietairesDesArchives(), proprietaireName);

        if (proprietaire != null) {
            return ResponseEntity.ok(proprietaire);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findEntite")
    public ResponseEntity<EntiteRattachee> findEntite(@RequestParam String entiteName) {
        PlanClassification planClassification = planClassificationService.getPlanClassification();
        EntiteRattachee entite = planClassificationService.findEntite(planClassification.getEntitesRattachees(), entiteName);

        if (entite != null) {
            return ResponseEntity.ok(entite);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findAgence")
    public ResponseEntity<Agence> findAgence(@RequestParam String agenceName) {
        PlanClassification planClassification = planClassificationService.getPlanClassification();
        Agence agence = planClassificationService.findAgence(planClassification.getAgences(), agenceName);

        if (agence != null) {
            return ResponseEntity.ok(agence);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findCategrie")
    public ResponseEntity<CategorieArchive> findCategorie(@RequestParam String categorieName) {
        PlanClassification planClassification = planClassificationService.getPlanClassification();
        CategorieArchive categorie = planClassificationService.findCategorie(planClassification.getCategoriesArchive(), categorieName);

        if (categorie != null) {
            return ResponseEntity.ok(categorie);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/create")
    public ResponseEntity<String> createPlanClassification(@RequestBody PlanClassification planClassification) {
        Utilisateur utilisateur = sessionContext.getCurrentUser();

        // Vérification si l'utilisateur est ADMINISTRATEUR
        Optional.ofNullable(utilisateur)
                .filter(u -> "ADMINISTRATEUR".equals(u.getRole()))
                .orElseThrow(() -> new ResourceNotFoundException("Accès refusé. Seul l'administrateur peut accéder à cette fonctionnalité."));

        planClassificationService.createPlanClassification(planClassification);
        return ResponseEntity.ok("Plan de classification créé avec succès.");
    }


    @PostMapping("/ajouterLigne")
    public ResponseEntity<String> ajouterLigneDansPlanClassification(
            @RequestBody PlanClassification planClassification,
            @RequestParam String nomProprietaire,
            @RequestParam String nomEntite,
            @RequestParam String ref,
            @RequestParam String codeEntite,
            @RequestParam String nomAgence,
            @RequestParam String nomCategorie,
            @RequestParam String codeCategorie) {

        Utilisateur utilisateur = sessionContext.getCurrentUser();

        // Vérification si l'utilisateur est ADMINISTRATEUR
        Optional.ofNullable(utilisateur)
                .filter(u -> "ADMINISTRATEUR".equals(u.getRole()))
                .orElseThrow(() -> new ResourceNotFoundException("Accès refusé. Seul l'administrateur peut accéder à cette fonctionnalité."));

        planClassificationService.ajouterLigneDansPlanClassification(planClassification, nomProprietaire, nomEntite, ref, codeEntite, nomAgence, nomCategorie, codeCategorie);
        return ResponseEntity.ok("Nouvelle ligne ajoutée au plan de classification.");
    }


    @PostMapping("/ajouterAgence")
    public ResponseEntity<String> ajouterNouvelleAgence(
            @RequestParam String nomAgence,
            @RequestParam String nomEntite,
            @RequestParam String refEntite) {

        Utilisateur utilisateur = sessionContext.getCurrentUser();

        // Vérification si l'utilisateur est ADMINISTRATEUR
        Optional.ofNullable(utilisateur)
                .filter(u -> "ADMINISTRATEUR".equals(u.getRole()))
                .orElseThrow(() -> new ResourceNotFoundException("Accès refusé. Seul l'administrateur peut accéder à cette fonctionnalité."));

        planClassificationService.ajouterNouvelleAgence(nomAgence, nomEntite, refEntite);
        return ResponseEntity.ok("Nouvelle agence ajoutée au plan de classification.");
    }


    @PostMapping("/ajouterCategorie")
    public ResponseEntity<String> ajouterNouvelleCategorie(
            @RequestParam String nomCategorie,
            @RequestParam String nomEntite,
            @RequestParam String refEntite,
            @RequestParam String nomAgence) {

        Utilisateur utilisateur = sessionContext.getCurrentUser();

        // Vérification si l'utilisateur est ADMINISTRATEUR
        Optional.ofNullable(utilisateur)
                .filter(u -> "ADMINISTRATEUR".equals(u.getRole()))
                .orElseThrow(() -> new ResourceNotFoundException("Accès refusé. Seul l'administrateur peut accéder à cette fonctionnalité."));

        planClassificationService.ajouterNouvelleCategorie(nomCategorie, nomEntite, refEntite, nomAgence);
        return ResponseEntity.ok("Nouvelle catégorie d'archive ajoutée au plan de classification.");
    }


    @PutMapping("/update")
    public ResponseEntity<String> updatePlanClassification(
            @RequestBody PlanClassification planClassification,
            @RequestParam String oldNomProprietaire,
            @RequestParam String newNomProprietaire,
            @RequestParam String oldNomEntite,
            @RequestParam String newNomEntite,
            @RequestParam String newRef,
            @RequestParam String newCodeEntite,
            @RequestParam String oldNomAgence,
            @RequestParam String newNomAgence,
            @RequestParam String oldNomCategorie,
            @RequestParam String newNomCategorie,
            @RequestParam String newCodeCategorie) {

        Utilisateur utilisateur = sessionContext.getCurrentUser();

        // Vérification si l'utilisateur est ADMINISTRATEUR
        Optional.ofNullable(utilisateur)
                .filter(u -> "ADMINISTRATEUR".equals(u.getRole()))
                .orElseThrow(() -> new ResourceNotFoundException("Accès refusé. Seul l'administrateur peut accéder à cette fonctionnalité."));

        planClassificationService.updatePlanClassification(planClassification, oldNomProprietaire, newNomProprietaire, oldNomEntite, newNomEntite, newRef, newCodeEntite, oldNomAgence, newNomAgence, oldNomCategorie, newNomCategorie, newCodeCategorie);
        return ResponseEntity.ok("Plan de classification mis à jour avec succès.");
    }



    @PostMapping("/import")
    public ResponseEntity<PlanClassification> importPlanClassification(@RequestParam("file") MultipartFile file) {
        Utilisateur currentUser = sessionContext.getCurrentUser();

        if (currentUser != null && "ADMINISTRATEUR".equals(currentUser.getRole())) {
            try (InputStream is = file.getInputStream()) {
                PlanClassification planClassification = planClassificationService.excelToPlanClassification(is);
                return ResponseEntity.ok(planClassification);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null); // Handle the exception properly in real implementation
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportPlanClassification() {
        Utilisateur currentUser = sessionContext.getCurrentUser();

        if (currentUser != null && "ADMINISTRATEUR".equals(currentUser.getRole())) {
            PlanClassification planClassification = planClassificationService.getPlanClassification(); // Assuming this method exists
            ByteArrayInputStream bis = planClassificationService.planClassificationToExcel(planClassification);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=plan_classification.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(bis));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }


        @GetMapping("/table")
        public ResponseEntity<List<PlanClassificationDto>> getPlanClassificationTable() {
            List<PlanClassificationDto> tableData = planClassificationService.getPlanClassificationTable();
            return ResponseEntity.ok(tableData);
        }

}
