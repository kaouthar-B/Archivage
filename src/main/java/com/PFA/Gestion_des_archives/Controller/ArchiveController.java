package com.PFA.Gestion_des_archives.Controller;


import com.PFA.Gestion_des_archives.Exception.ResourceNotFoundException;
import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Exception.UnauthorizedException;
import com.PFA.Gestion_des_archives.Repository.AgenceRepository;
import com.PFA.Gestion_des_archives.Repository.ArchiveRepository;
import com.PFA.Gestion_des_archives.Repository.CategorieArchiveRepository;
import com.PFA.Gestion_des_archives.Repository.EntiteRattacheeRepository;
import com.PFA.Gestion_des_archives.Service.ArchiveService;
import com.PFA.Gestion_des_archives.Service.HistoriqueActionService;
import com.PFA.Gestion_des_archives.Service.PermissionService;
import com.PFA.Gestion_des_archives.Service.UtilisateurService;
import com.PFA.Gestion_des_archives.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/archives")
public class ArchiveController {

    @Autowired
    private ArchiveService archiveService;
    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private EntiteRattacheeRepository entiteRattacheeRepository;
    @Autowired
    private CategorieArchiveRepository categorieArchiveRepository;
    @Autowired
    private AgenceRepository agenceRepository;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private SessionContext sessionContext;
    @Autowired
    private ArchiveRepository archiveRepository;
    @Autowired
    private HistoriqueActionService historiqueActionService;




    @GetMapping
    public ResponseEntity<List<Archive>> getAllArchives() {
        List<Archive> archives = archiveService.getAllArchives();
        return ResponseEntity.ok(archives);
    }


    @GetMapping("/archives/nom/{nom}")
    public Archive getArchiveByNom(@PathVariable String nom) {
        List<Archive> archives = archiveRepository.findByNom(nom);
        if (archives.isEmpty()) {
            throw new ResourceNotFoundException("Archive non trouvée.");
        }
        return archives.get(0);
    }


    // Endpoint pour afficher une archive spécifique par ID, en vérifiant l'utilisateur
    @GetMapping("/{id}/affiche")
    public ResponseEntity<Archive> afficheArchive(@PathVariable Long id, @RequestParam("userId") int userId) {
        // Ici, vous récupérez l'utilisateur par son ID depuis le service Utilisateur
        Optional<Utilisateur> utilisateur = utilisateurService.getUserById(userId);
        Archive archive = archiveService.afficheArchive(id, utilisateur);
        return new ResponseEntity<>(archive, HttpStatus.OK);
    }

    // Endpoint pour rechercher des archives avec des critères spécifiques
    @GetMapping("/search")
    public ResponseEntity<List<Archive>> searchArchives(
            @RequestParam(required = false) String entite,
            @RequestParam(required = false) String refTransfert,
            @RequestParam(required = false) String localOrigine,
            @RequestParam(required = false) String correspondant,
            @RequestParam(required = false) Date dateTransfert,
            @RequestParam(required = false) String numeroConteneur,
            @RequestParam(required = false) String emplacement,
            @RequestParam(required = false) String refEnt,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String agenceUnite,
            @RequestParam(required = false) String nature,
            @RequestParam(required = false) Year anneeCreation,
            @RequestParam(required = false) Month mois,
            @RequestParam(required = false) Integer jour,
            @RequestParam(required = false) String observations) {

        List<Archive> archives = archiveService.searchArchives(entite, refTransfert, localOrigine, correspondant, dateTransfert,
                numeroConteneur, emplacement, refEnt, code, agenceUnite, nature,
                anneeCreation, mois, jour, observations);
        return new ResponseEntity<>(archives, HttpStatus.OK);
    }


    // Endpoint pour obtenir les détails d'une EntiteRattachee par ID
    @GetMapping("/entite-rattachee/{id}")
    public ResponseEntity<EntiteRattachee> getEntiteRattacheeDetails(@PathVariable Long id) {
        EntiteRattachee entiteRattachee = archiveService.getEntiteRattacheeDetails(id);
        return new ResponseEntity<>(entiteRattachee, HttpStatus.OK);
    }

    // Endpoint pour obtenir les catégories d'archives par ID de l'entité rattachée
    @GetMapping("/entite-rattachee/{id}/categories")
    public ResponseEntity<List<CategorieArchive>> getCategoriesByEntiteRattachee(@PathVariable Long id) {
        List<CategorieArchive> categories = archiveService.getCategoriesByEntiteRattachee(id);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Endpoint pour obtenir les agences par ID de l'entité rattachée
    @GetMapping("/entite-rattachee/{id}/agences")
    public ResponseEntity<List<Agence>> getAgencesByEntiteRattachee(@PathVariable Long id) {
        List<Agence> agences = archiveService.getAgencesByEntiteRattachee(id);
        return new ResponseEntity<>(agences, HttpStatus.OK);
    }



    //Admin

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArchive(@PathVariable Long id) {
        Utilisateur utilisateur = sessionContext.getCurrentUser(); // Utiliser SessionContext pour obtenir l'utilisateur connecté

        if ("ADMINISTRATEUR".equals(utilisateur.getRole())) {
            Archive archive = archiveRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Archive non trouvée."));

            archiveRepository.delete(archive);

            String titreHistorique = "Suppression de l'archive " + archive.getId();
            historiqueActionService.addHistoriqueActionForArchive(Action.suppression, archive, titreHistorique);

            return ResponseEntity.noContent().build(); // Réponse 204 No Content pour indiquer la réussite
        } else {
            throw new UnauthorizedException("Seul un ADMINISTRATEUR peut supprimer cette archive.");
        }
    }




    // Endpoint pour ajouter une nouvelle archive avec validation utilisateur
    @PostMapping("/add")
    public ResponseEntity<Archive> addArchive(
            @RequestBody Archive archive,
            @RequestParam("userId") int userId,
            @RequestParam("entiteRattacheeId") Long entiteRattacheeId,
            @RequestParam("categorieArchiveId") Long categorieArchiveId,
            @RequestParam("agenceId") Long agenceId,
            @RequestParam("titreHistorique") String titreHistorique) {



        Utilisateur utilisateur = sessionContext.getCurrentUser();
        if (utilisateur == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Récupérer l'EntiteRattachee sélectionnée par son ID
        EntiteRattachee entiteRattachee = entiteRattacheeRepository.getById(entiteRattacheeId);
        archive.setEntiteRattachee(entiteRattachee);

        // Assigner refEnt et codeEntite basés sur l'EntiteRattachee sélectionnée
        archive.setRefEnt(entiteRattachee.getRef());
        archive.setCodeEntite(entiteRattachee.getCodeEntite());

        // Récupérer la CategorieArchive sélectionnée par son ID et vérifier qu'elle appartient à l'EntiteRattachee
        CategorieArchive categorieArchive = categorieArchiveRepository.getById(categorieArchiveId);
        if (!categorieArchive.getEntiteRattachee().getId().equals(entiteRattacheeId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        archive.setCategorieArchive(categorieArchive);

        // Récupérer l'Agence sélectionnée par son ID et vérifier qu'elle appartient à l'EntiteRattachee
        Agence agence = agenceRepository.getById(agenceId);
        if (!agence.getEntiteRattachee().getId().equals(entiteRattacheeId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        archive.setAgence(agence);

        // Vérifier les permissions de l'utilisateur
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));
        if (!optionalPermissions.isPresent() || !optionalPermissions.get().isCanAddArchive()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Appeler le service pour ajouter l'archive avec les informations complètes
        try {
            Archive savedArchive = archiveService.addArchive(archive, titreHistorique);
            return new ResponseEntity<>(savedArchive, HttpStatus.CREATED);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Archive> updateArchive(
            @PathVariable("id") Long id,
            @RequestBody Archive updatedArchive) {  // Correction : Ajout de la parenthèse fermante ici

        Utilisateur utilisateur = sessionContext.getCurrentUser();
        if (utilisateur == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Appeler le service pour mettre à jour l'archive
        try {
            Archive updated = archiveService.updateArchive(id, updatedArchive);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }




    @PostMapping("/import")
    public ResponseEntity<String> importArchives(@RequestParam("file") MultipartFile file) {
        Utilisateur utilisateur = sessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanImportArchiveFromExcel()) {
            try {
                archiveService.importArchivesFromExcel(file);
                return ResponseEntity.ok("Archives importées avec succès.");
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'importation des archives.");
            }
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour importer des archives.");
        }
    }

    @GetMapping("/export")
    public void exportArchives(@RequestParam("ids") List<Long> ids, HttpServletResponse response) throws IOException {
        Utilisateur utilisateur = sessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanExportArchiveToExcel()) {
            archiveService.exportArchivesToExcel(ids, response);
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour exporter des archives.");
        }
    }

}
