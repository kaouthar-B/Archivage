package com.PFA.Gestion_des_archives.Service;

import com.PFA.Gestion_des_archives.Exception.UnauthorizedException;
import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Repository.*;
import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Repository.ArchiveRepository;
import com.PFA.Gestion_des_archives.Repository.ConteneurRepository;
import com.PFA.Gestion_des_archives.Repository.SiteArchivageRepository;
import com.PFA.Gestion_des_archives.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.transaction.Transactional;

@Service
public class ConteneurService {

    private static final Logger logger = LoggerFactory.getLogger(ConteneurService.class);

    @Autowired
    private ConteneurRepository conteneurRepository;

    @Autowired
    private SiteArchivageRepository siteArchivageRepository;

    @Autowired
    private ArchiveRepository archiveRepository;

    @Autowired
    private HistoriqueActionService historiqueActionService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private JavaMailSender mailSender;

    public List<Conteneur> getAllConteneur() {
        return conteneurRepository.findAll();
    }

    public Conteneur getConteneurById(Long id) {
        return conteneurRepository.findById(id).orElse(null);
    }

    public Conteneur addConteneur(Conteneur conteneur) {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        // Vérification si l'utilisateur a la permission d'ajouter une archive
        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanAddConteneur()) {

            Conteneur newConteneur = conteneurRepository.save(conteneur);
            String titre = "Ajout du conteneur '" + conteneur.getNumero() + "'";
            historiqueActionService.addHistoriqueActionForConteneur(Action.ajout, newConteneur, titre);
            return newConteneur;
        } else {
            // Levée d'une exception si l'utilisateur n'a pas la permission
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour ajouter un conteneur.");
        }
    }

    public Conteneur updateConteneur(Long id, Conteneur conteneur) {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        // Vérification si l'utilisateur a la permission de mettre à jour un conteneur
        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanUpdateConteneur()) {

            Conteneur existingConteneur = conteneurRepository.findById(id).orElse(null);
            if (existingConteneur != null) {
                existingConteneur.setNumero(conteneur.getNumero());
                existingConteneur.setType(conteneur.getType());
                existingConteneur.setEmplacement(conteneur.getEmplacement());
                existingConteneur.setStatus(conteneur.getStatus());
                existingConteneur.setSiteArchivage(conteneur.getSiteArchivage());
                Conteneur updatedConteneur = conteneurRepository.save(existingConteneur);
                String titre = "Modification de conteneur '" + conteneur.getNumero() + "'";
                historiqueActionService.addHistoriqueActionForConteneur(Action.modification, updatedConteneur, titre);
                return updatedConteneur;
            }
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour modifier un conteneur.");
        }
        return null;
    }

    public void deleteConteneur(Long id) {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        // Vérification si l'utilisateur a la permission de supprimer un conteneur
        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanDeleteConteneur()) {

            Conteneur conteneur = conteneurRepository.findById(id).orElse(null);
            if (conteneur != null) {
                // Récupération du SiteArchivage associé au conteneur
                SiteArchivage siteArchivage = conteneur.getSiteArchivage();
                if (siteArchivage != null) {
                    // Mise à jour des attributs nombreEmplacementsChargés et nombreEmplacementsVides
                    siteArchivage.setNombreEmplacementsChargés(siteArchivage.getNombreEmplacementsCharges() - 1);
                    siteArchivage.setNombreEmplacementsVides(siteArchivage.getNombreEmplacementsVides() + 1);

                    // Sauvegarde des modifications apportées à SiteArchivage
                    siteArchivageRepository.save(siteArchivage);
                }

                // Suppression des archives associées au conteneur
                List<Archive> archives = archiveRepository.findByConteneurId(id);
                for (Archive archive : archives) {
                    archiveRepository.delete(archive);
                }

                // Suppression du conteneur
                conteneurRepository.deleteById(id);

                // Ajout d'une entrée dans HistoriqueAction
                String titre = "Suppression de conteneur '" + conteneur.getNumero() + "'";
                historiqueActionService.addHistoriqueActionForConteneur(Action.suppression, conteneur, titre);
            } else {
                throw new IllegalArgumentException("Conteneur non trouvé.");
            }
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour supprimer un conteneur.");
        }
    }


    public long countConteneursByStatus(Long siteId, ConteneurStatus status) {
        return conteneurRepository.countBySiteArchivageIdAndStatus(siteId, status);
    }

    public List<Conteneur> findConteneursByStatus(Long siteId, ConteneurStatus status) {
        return conteneurRepository.findBySiteArchivageIdAndStatus(siteId, status);
    }

    public List<Archive> getArchivesByConteneur(Long conteneurId) {
        return archiveRepository.findByConteneurId(conteneurId);
    }

    public void importConteneursFromExcel(MultipartFile file) throws IOException {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        // Vérification si l'utilisateur a la permission d'importer des conteneurs depuis un fichier Excel
        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanImportConteneursFromExcel()) {

            List<Conteneur> conteneurs = new ArrayList<>();
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            // Compteur pour le nombre de conteneurs importés
            int nombreConteneursImportes = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Skip header row
                }

                Conteneur conteneur = new Conteneur();
                conteneur.setNumero(row.getCell(0).getStringCellValue());
                conteneur.setType(row.getCell(1).getStringCellValue());
                conteneur.setEmplacement(row.getCell(2).getStringCellValue());
                conteneur.setStatus(ConteneurStatus.valueOf(row.getCell(3).getStringCellValue()));

                // Ajouter le conteneur à la liste
                conteneurs.add(conteneur);
                nombreConteneursImportes++;
            }

            // Sauvegarder tous les conteneurs dans la base de données
            conteneurRepository.saveAll(conteneurs);

            // Mettre à jour les historiques d'action pour chaque conteneur importé
            for (Conteneur conteneur : conteneurs) {
                String titre = "Ajout de conteneur '" + conteneur.getNumero() + "' par importation Excel";
                historiqueActionService.addHistoriqueActionForConteneur(Action.importation, conteneur, titre);
            }

            // Mise à jour des attributs de SiteArchivage
            if (!conteneurs.isEmpty()) {
                // Assumer que tous les conteneurs importés ont le même SiteArchivage
                SiteArchivage siteArchivage = conteneurs.get(0).getSiteArchivage();
                if (siteArchivage != null) {
                    int nombreEmplacementsChargés = siteArchivage.getNombreEmplacementsCharges();
                    int nombreEmplacementsVides = siteArchivage.getNombreEmplacementsVides();
                    int nombreEmplacementsTotal = siteArchivage.getNombreEmplacements();

                    siteArchivage.setNombreEmplacementsChargés(nombreEmplacementsChargés + nombreConteneursImportes);
                    siteArchivage.setNombreEmplacementsVides(nombreEmplacementsTotal - (nombreEmplacementsChargés + nombreConteneursImportes));

                    siteArchivageRepository.save(siteArchivage);
                }
            }

            workbook.close();
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour importer des conteneurs depuis un fichier Excel.");
        }
    }


    public ByteArrayInputStream exportConteneursToExcel() throws IOException {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        // Vérification si l'utilisateur a la permission d'exporter des conteneurs vers un fichier Excel
        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanExportConteneursToExcel()) {

            List<Conteneur> conteneurs = getAllConteneur();
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Sheet sheet = workbook.createSheet("Conteneurs");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Reference");
            headerRow.createCell(1).setCellValue("Type");
            headerRow.createCell(2).setCellValue("Emplacement");
            headerRow.createCell(3).setCellValue("Status");

            int rowIndex = 1;
            for (Conteneur conteneur : conteneurs) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(conteneur.getNumero());
                row.createCell(1).setCellValue(conteneur.getType());
                row.createCell(2).setCellValue(conteneur.getEmplacement());
                row.createCell(3).setCellValue(conteneur.getStatus().name());
            }

            workbook.write(out);
            workbook.close();

            // Record the export action in the history
            for (Conteneur conteneur : conteneurs) {
                String titre = "Export de conteneur '" + conteneur.getNumero() + "' vers Excel";
                historiqueActionService.addHistoriqueActionForConteneur(Action.exportation, conteneur, titre);
            }

            return new ByteArrayInputStream(out.toByteArray());
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour exporter des conteneurs vers un fichier Excel.");
        }
    }


    // Méthode pour ajouter une affectation provisoire
    @Transactional
    public Conteneur addAffectationProvisoire(Long siteId, Conteneur conteneurData) {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        // Vérification si l'utilisateur a la permission d'ajouter une affectation provisoire
        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanAffecterProvisoire()) {

            SiteArchivage siteArchivage = siteArchivageRepository.findById(siteId).orElse(null);
            if (siteArchivage != null) {
                Conteneur conteneur = new Conteneur();
                conteneur.setSiteArchivage(siteArchivage);
                conteneur.setEmplacement(conteneurData.getEmplacement());
                conteneur.setStatus(conteneurData.getStatus());
                conteneur.setTypeAffectation(TypeAffectation.PROVISOIRE);
                conteneur.setDateConfirmation(LocalDateTime.now());
                conteneur.setUtilisateur(utilisateur);

                Conteneur newConteneur = conteneurRepository.save(conteneur);

                // Mise à jour des attributs de SiteArchivage
                siteArchivage.setNombreEmplacementsChargés(siteArchivage.getNombreEmplacementsCharges() + 1);
                siteArchivage.setNombreEmplacementsVides(siteArchivage.getNombreEmplacementsVides() - 1);
                siteArchivageRepository.save(siteArchivage);

                String titre = "Affectation provisoire du conteneur '" + conteneur.getNumero() + "'";
                historiqueActionService.addHistoriqueActionForConteneur(Action.ajout, newConteneur, titre);
                return newConteneur;
            }
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour ajouter une affectation provisoire.");
        }
        return null;
    }

    // Méthode pour ajouter une affectation définitive
    @Transactional
    public Conteneur addAffectationDefinitive(Long siteId, Long conteneurId, Conteneur conteneurData) {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        // Vérification si l'utilisateur a la permission d'ajouter une affectation définitive
        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanAffecterDefinitive()) {
            Conteneur conteneur = conteneurRepository.findById(conteneurId).orElse(null);
            if (conteneur != null) {
                conteneur.setEmplacement(conteneurData.getEmplacement());
                conteneur.setStatus(conteneurData.getStatus());
                conteneur.setTypeAffectation(TypeAffectation.DEFINITIVE);
                conteneur.setDateConfirmation(LocalDateTime.now());
                conteneur.setUtilisateur(utilisateur);

                Conteneur updatedConteneur = conteneurRepository.save(conteneur);

                SiteArchivage siteArchivage = siteArchivageRepository.findById(siteId).orElse(null);
                if (siteArchivage != null) {
                    // Mise à jour des attributs de SiteArchivage
                    siteArchivage.setNombreEmplacementsChargés(siteArchivage.getNombreEmplacementsCharges() + 1);
                    siteArchivage.setNombreEmplacementsVides(siteArchivage.getNombreEmplacementsVides() - 1);
                    siteArchivageRepository.save(siteArchivage);
                }

                String titre = "Affectation définitive du conteneur '" + conteneur.getNumero() + "'";
                historiqueActionService.addHistoriqueActionForConteneur(Action.affectationDéfinitive, updatedConteneur, titre);
                return updatedConteneur;
            }
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour ajouter une affectation définitive.");
        }
        return null;
    }

    public void checkAndConvertProvisoireToDefinitive() {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        // Vérification si l'utilisateur a la permission de convertir les affectations provisoires en définitives
        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanAffecterDefinitive()) {
            List<Conteneur> provisoireConteneurs = conteneurRepository.findByTypeAffectation(TypeAffectation.PROVISOIRE);
            for (Conteneur conteneur : provisoireConteneurs) {
                if (conteneur.getDateConfirmation().plusDays(7).isBefore(LocalDateTime.now())) {
                    conteneur.setTypeAffectation(TypeAffectation.DEFINITIVE);
                    conteneurRepository.save(conteneur);
                    String titre = "Changement de l'affectation provisoire à une affectation définitive du conteneur '" + conteneur.getNumero() + "'";
                    historiqueActionService.addHistoriqueActionForConteneur(Action.modificationTypeAffectation, conteneur, titre);
                }
            }
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour convertir les affectations provisoires en définitives.");
        }
    }


    /*public void checkAndNotifyForProvisoireAffectations() {
        Utilisateur utilisateur = SessionContext.getCurrentUser();
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        // Vérification si l'utilisateur a la permission de notifier pour les affectations provisoires
        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanAffecterProvisoire()) {
            List<Conteneur> provisoireConteneurs = conteneurRepository.findProvisoireAffectationsOlderThan(7);
            for (Conteneur conteneur : provisoireConteneurs) {
                Utilisateur conteneurUtilisateur = conteneur.getUtilisateur();
                sendNotification(conteneurUtilisateur, conteneur);
                String titre = "Notification pour le changement de l'affectation provisoire du conteneur '" + conteneur.getNumero() + "'";
                historiqueActionService.addHistoriqueActionForConteneur(Action.notification, conteneur, titre, LocalDateTime.now());
            }
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour notifier les affectations provisoires.");
        }
    }


    private void sendNotification(Utilisateur utilisateur, Conteneur conteneur) {
        // Ensure utilisateur is not null before proceeding
        if (utilisateur == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(utilisateur.getEmail());
        message.setSubject("Changement d'emplacement requis");
        message.setText("Il faut changer l'emplacement du conteneur '" + conteneur.getNumero() + "'");
        mailSender.send(message);
    }*/

}



