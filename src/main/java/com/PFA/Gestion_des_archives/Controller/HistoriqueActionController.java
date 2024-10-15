package com.PFA.Gestion_des_archives.Controller;

import com.PFA.Gestion_des_archives.Exception.ResourceNotFoundException;
import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Repository.*;
import com.PFA.Gestion_des_archives.Service.HistoriqueActionService;
import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/historique-actions")
public class HistoriqueActionController {

    @Autowired
    private HistoriqueActionService historiqueActionService;
    @Autowired
    private HistoriqueActionRepository historiqueActionRepository;

    @Autowired
    private SiteArchivageRepository siteArchivageRepository;

    @Autowired
    private ConteneurRepository conteneurRepository;

    @Autowired
    private ArchiveRepository archiveRepository;

    @Autowired
    private PlanClassificationRepository planClassificationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;


   /* public List<HistoriqueAction> searchHistoriqueActions(TypeActionHistorique typeAction, Action action, Utilisateur utilisateur,
                                                          Date dateAction, String titre) {
        return historiqueActionRepository.searchHistoriqueActions(typeAction, action, utilisateur,
                dateAction);
    }*/

    @GetMapping("/all")
    public ResponseEntity<List<HistoriqueAction>> getAllHistoriqueActions() {
        List<HistoriqueAction> result = historiqueActionService.getAllHistoriqueActions();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/site-archivage/{id}")
    public ResponseEntity<List<HistoriqueAction>> getHistoriqueActionsBySiteArchivage(@PathVariable Long id) {
        SiteArchivage siteArchivage = siteArchivageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site d'archivage not found with id " + id));
        List<HistoriqueAction> result = historiqueActionService.getHistoriqueActionsBySiteArchivage(siteArchivage);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/conteneur/{id}")
    public ResponseEntity<List<HistoriqueAction>> getHistoriqueActionsByConteneur(@PathVariable Long id) {
        Conteneur conteneur = conteneurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conteneur not found with id " + id));
        List<HistoriqueAction> result = historiqueActionService.getHistoriqueActionsByConteneur(conteneur);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/archive/{id}")
    public ResponseEntity<List<HistoriqueAction>> getHistoriqueActionsByArchive(@PathVariable Long id) {
        Archive archive = archiveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Archive not found with id " + id));
        List<HistoriqueAction> result = historiqueActionService.getHistoriqueActionsByArchive(archive);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<HistoriqueAction>> getHistoriqueActionsByUtilisateur(
            @PathVariable Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId);
        List<HistoriqueAction> historiqueActions = historiqueActionService.getHistoriqueActionsByUtilisateur(utilisateur);
        return ResponseEntity.ok(historiqueActions);
    }
}
