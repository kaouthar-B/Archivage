package com.PFA.Gestion_des_archives.Service;

import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Repository.*;
import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Service
public class HistoriqueActionService {

    private static final Logger logger = LoggerFactory.getLogger(HistoriqueActionService.class);

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


    public void addHistoriqueActionForArchive(Action action, Archive archive, String titre) {
        Utilisateur utilisateur = SessionContext.getCurrentUser(); // Obtenir l'utilisateur connecté
        LocalDateTime dateTime = LocalDateTime.now(); // Enregistrer la date et l'heure actuelle
        HistoriqueAction historiqueAction = new HistoriqueAction(TypeActionHistorique.ARCHIVE, action, archive, null, null, null, utilisateur, titre);
        historiqueActionRepository.save(historiqueAction);
        String message = action + " d'archive " + archive.getId();
        logger.info(message);
    }

    public void addHistoriqueActionForSiteArchivage(Action action, SiteArchivage siteArchivage, String titre) {
        Utilisateur utilisateur = SessionContext.getCurrentUser(); // Obtenir l'utilisateur connecté
        LocalDateTime dateTime = LocalDateTime.now(); // Enregistrer la date et l'heure actuelle
        HistoriqueAction historiqueAction = new HistoriqueAction(TypeActionHistorique.SITE_ARCHIVAGE, action, null, null, siteArchivage, null, utilisateur, titre);
        historiqueActionRepository.save(historiqueAction);
        String message = action + " de site d'archivage " + siteArchivage.getNom();
        logger.info(message);
    }

    public void addHistoriqueActionForConteneur(Action action, Conteneur conteneur, String titre) {
        Utilisateur utilisateur = SessionContext.getCurrentUser(); // Obtenir l'utilisateur connecté
        LocalDateTime dateTime = LocalDateTime.now(); // Enregistrer la date et l'heure actuelle
        HistoriqueAction historiqueAction = new HistoriqueAction(TypeActionHistorique.CONTENEUR, action, null, conteneur, null, null, utilisateur, titre);
        historiqueActionRepository.save(historiqueAction);
        String message = action + " de conteneur " + conteneur.getNumero();
        logger.info(message);
    }

    public void addHistoriqueActionForPlanClassification(Action action, PlanClassification planClassification, String titre) {
        Utilisateur utilisateur = SessionContext.getCurrentUser(); // Obtenir l'utilisateur connecté
        LocalDateTime dateTime = LocalDateTime.now(); // Enregistrer la date et l'heure actuelle
        HistoriqueAction historiqueAction = new HistoriqueAction(TypeActionHistorique.PLAN_CLASSIFICATION, action, null, null, null, planClassification, utilisateur, titre);
        historiqueActionRepository.save(historiqueAction);
        String message = action + " dans le plan de classification ";
        logger.info(message);
    }

    public void addHistoriqueActionForUtilisateur(Action action, Utilisateur utilisateurEnRequete, String titre) {
        Utilisateur utilisateur = SessionContext.getCurrentUser(); // Obtenir l'utilisateur connecté
        LocalDateTime dateTime = LocalDateTime.now(); // Enregistrer la date et l'heure actuelle
        HistoriqueAction historiqueAction = new HistoriqueAction(TypeActionHistorique.UTILISATEUR, action, null, null, null, null, utilisateur, titre);
        historiqueActionRepository.save(historiqueAction);
        String message = action + " de l'utilisateur " + utilisateurEnRequete.getNom();
        logger.info(message);
    }



    public void afficherInstancesRemplies(String titre) {
            List<HistoriqueAction> historiquesActions = historiqueActionRepository.findByTitre(titre);
            if (!historiquesActions.isEmpty()) {
                logger.info("Instances remplies pour HistoriqueAction avec titre : " + titre);
                for (HistoriqueAction historiqueAction : historiquesActions) {
                    if (historiqueAction.getArchive() != null) {
                        logger.info("Archive: " + historiqueAction.getArchive().getId());
                    }
                    if (historiqueAction.getConteneur() != null) {
                        logger.info("Conteneur: " + historiqueAction.getConteneur().getNumero());
                    }
                    if (historiqueAction.getSiteArchivage() != null) {
                        logger.info("Site d'archivage: " + historiqueAction.getSiteArchivage().getNom());
                    }
                    if (historiqueAction.getPlanClassification() != null) {
                        logger.info("Plan de classification: ");
                    }
                    if (historiqueAction.getUtilisateur() != null) {
                        logger.info("Utilisateur: " + historiqueAction.getUtilisateur().getNom());
                    }
                    logger.info("Catégorie d'action: " + historiqueAction.getTypeAction());
                    logger.info("Action: " + historiqueAction.getAction());
                    logger.info("Date et heure d'action: " + historiqueAction.getDateHeure());
                    logger.info("Titre: " + historiqueAction.getTitre());
                }
            } else {
                logger.warn("Aucune action historique trouvée avec le titre : " + titre);
            }
        }




   /* public List<HistoriqueAction> searchHistoriqueActions(TypeActionHistorique typeAction, Action action, Archive archive,
                                                          Conteneur conteneur, SiteArchivage siteArchivage,
                                                          PlanClassification planClassification, Utilisateur utilisateur,
                                                          Date dateAction, String titre) {
        return historiqueActionRepository.searchHistoriqueActions(typeAction, action, utilisateur,
                dateAction);
    }*/



public List<HistoriqueAction> getAllHistoriqueActions() {
        return historiqueActionRepository.findAll();
    }

    public List<HistoriqueAction> getHistoriqueActionsBySiteArchivage(SiteArchivage siteArchivage) {
        return historiqueActionRepository.findBySiteArchivage(siteArchivage);
    }

    public List<HistoriqueAction> getHistoriqueActionsByConteneur(Conteneur conteneur) {
        return historiqueActionRepository.findByConteneur(conteneur);
    }

    public List<HistoriqueAction> getHistoriqueActionsByArchive(Archive archive) {
        return historiqueActionRepository.findByArchive(archive);
    }

    public List<HistoriqueAction> getHistoriqueActionsByUtilisateur(Utilisateur utilisateur){
        return historiqueActionRepository.findByUtilisateur(utilisateur);
    }

}
