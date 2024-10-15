package com.PFA.Gestion_des_archives.Service;

import com.PFA.Gestion_des_archives.Exception.ResourceNotFoundException;
import com.PFA.Gestion_des_archives.Exception.UnauthorizedException;
import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Repository.*;
import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Repository.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.Date;
import java.util.List;
import java.util.Optional;



@Service
public class ArchiveService {

    @Autowired
    private ArchiveRepository archiveRepository;

    @Autowired
    private ConteneurRepository conteneurRepository;

    @Autowired
    private HistoriqueActionService historiqueActionService;

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private EntiteRattacheeRepository entiteRattacheeRepository;
    @Autowired
    private CategorieArchiveRepository categorieArchiveRepository;
    @Autowired
    private AgenceRepository agenceRepository;
    // EntiteRattachee.java

    @Autowired
    private SessionContext sessionContext;

    @OneToMany(mappedBy = "entiteRattachee")
    private List<CategorieArchive> categories;

    // EntiteRattachee.java
    @OneToMany(mappedBy = "entiteRattachee")
    private List<Agence> agences;


    public List<Archive> getAllArchives() {
        return archiveRepository.findAll();
    }

    public Archive getArchiveByNom(String nom) {
        List<Archive> archives = archiveRepository.findByNom(nom);
        if (archives.isEmpty()) {
            throw new ResourceNotFoundException("Archive non trouvée.");
        }
        // Vous pouvez choisir d'utiliser le premier élément si vous vous attendez à une seule archive.
        return archives.get(0);
    }



    public Archive afficheArchive(Long id, Optional<Utilisateur> utilisateur) {
        return archiveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Archive non trouvée."));

    }

    public List<Archive> searchArchives(String entite, String refTransfert, String localOrigine,
                                        String correspondant, Date dateTransfert, String numeroConteneur,
                                        String emplacement, String refEnt, String code, String agenceUnite,
                                        String nature, Year anneeCreation, Month mois, Integer jour,
                                        String observations) {
        // Rechercher les archives selon les critères fournis
        return archiveRepository.searchArchives(entite, refTransfert, localOrigine, correspondant, dateTransfert,
                numeroConteneur, emplacement, refEnt, code, agenceUnite, nature,
                anneeCreation, mois, jour, observations);
    }


    public Archive addArchive(Archive archive, String titreHistorique) {

        Utilisateur utilisateur = sessionContext.getCurrentUser();
        // Récupération des permissions de l'utilisateur
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        // Vérification si l'utilisateur a la permission d'ajouter une archive
        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanAddArchive()) {
            // Assigner les valeurs automatiques basées sur l'EntiteRattachee sélectionnée
            EntiteRattachee entiteRattachee = archive.getEntiteRattachee();
            archive.setRefEnt(entiteRattachee.getRef());
            archive.setCodeEntite(entiteRattachee.getCodeEntite());
            archive.setCategorieArchive(archive.getCategorieArchive()); // Déjà sélectionné par l'utilisateur
            archive.setAgence(archive.getAgence()); // Déjà sélectionné par l'utilisateur

            // Sauvegarde de l'archive
            Archive savedArchive = archiveRepository.save(archive);


            // Ajout d'une entrée dans l'historique d'actions pour cette archive
            historiqueActionService.addHistoriqueActionForArchive(Action.ajout, savedArchive, titreHistorique);

            return savedArchive;
        } else {
            // Levée d'une exception si l'utilisateur n'a pas la permission
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour ajouter une archive.");
        }
    }


    public Archive updateArchive(Long id, Archive updatedArchive) {
        Utilisateur utilisateur = sessionContext.getCurrentUser(); // Utiliser SessionContext pour obtenir l'utilisateur connecté

        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent()) {
            Permission permissions = optionalPermissions.get();

            if (permissions.isCanUpdateArchive()) {
                Archive existingArchive = archiveRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Archive non trouvée."));

                existingArchive.setRefTransfert(updatedArchive.getRefTransfert());
                existingArchive.setCorrespondant(updatedArchive.getCorrespondant());
                existingArchive.setDateDeTransfert(updatedArchive.getDateDeTransfert());
                existingArchive.setNumeroConteneur(updatedArchive.getNumeroConteneur());
                existingArchive.setLocalOrigine(updatedArchive.getLocalOrigine());
                existingArchive.setRefEnt(updatedArchive.getRefEnt());
                existingArchive.setCodeEntite(updatedArchive.getCodeEntite());
                existingArchive.setNature(updatedArchive.getNature());
                existingArchive.setNomAgence(updatedArchive.getNomAgence());
                existingArchive.setAdresseAgence(updatedArchive.getAdresseAgence());
                existingArchive.setEmplacement(updatedArchive.getEmplacement());
                existingArchive.setObservations(updatedArchive.getObservations());

                Archive savedArchive = archiveRepository.save(existingArchive);

                String titreHistorique = "Modification dans l'archive " + savedArchive.getId();

                historiqueActionService.addHistoriqueActionForArchive(Action.modification, savedArchive, titreHistorique);

                return savedArchive;
            } else {
                throw new UnauthorizedException("Vous n'avez pas l'habilitation pour mettre à jour cette archive.");
            }
        } else {
            throw new UnauthorizedException("Permissions introuvables pour cet utilisateur.");
        }
    }



    public void deleteArchive(Long id) {
        Utilisateur utilisateur = sessionContext.getCurrentUser(); // Utiliser SessionContext pour obtenir l'utilisateur connecté

        if ("ADMINISTRATEUR".equals(utilisateur.getRole())) {
            Archive archive = archiveRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Archive non trouvée."));

            archiveRepository.delete(archive);

            String titreHistorique = "Suppression de l'archive " + archive.getId();

            historiqueActionService.addHistoriqueActionForArchive(Action.suppression, archive, titreHistorique);
        } else {
            throw new UnauthorizedException("Seul un ADMINISTRATEUR peut supprimer cette archive.");
        }
    }


    public void importArchivesFromExcel(MultipartFile file) throws IOException {
        Utilisateur utilisateur = sessionContext.getCurrentUser(); // Utiliser SessionContext pour obtenir l'utilisateur connecté

        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanImportArchiveFromExcel()) {
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;

                    Archive archive = new Archive();

                    String nomEntite = row.getCell(0).getStringCellValue();
                    EntiteRattachee entiteRattachee = entiteRattacheeRepository.findByNom(nomEntite);
                    if (entiteRattachee == null) {
                        entiteRattachee = new EntiteRattachee();
                        entiteRattachee.setNom(nomEntite);
                        entiteRattacheeRepository.save(entiteRattachee);
                    }
                    archive.setEntiteRattachee(entiteRattachee);

                    archive.setRefTransfert(row.getCell(1).getStringCellValue());
                    archive.setLocalOrigine(row.getCell(2).getStringCellValue());
                    archive.setCorrespondant(row.getCell(3).getStringCellValue());
                    archive.setDateDeTransfert(row.getCell(4).getDateCellValue());
                    archive.setNumeroConteneur(row.getCell(5).getStringCellValue());
                    archive.setEmplacement(row.getCell(6).getStringCellValue());
                    archive.setRefEnt(row.getCell(7).getStringCellValue());
                    archive.setCodeEntite(row.getCell(8).getStringCellValue());
                    archive.setNomAgence(row.getCell(9).getStringCellValue());
                    archive.setNature(row.getCell(10).getStringCellValue());
                    archive.setAnneeCreation(Year.of((int) row.getCell(11).getNumericCellValue()));
                    archive.setMoisCreation(Month.of((int) row.getCell(12).getNumericCellValue()));
                    archive.setJourCreation((int) row.getCell(13).getNumericCellValue());
                    archive.setObservations(row.getCell(14).getStringCellValue());

                    archiveRepository.save(archive);

                    String titreHistorique = "Importation de l'archive " + archive.getId();

                    historiqueActionService.addHistoriqueActionForArchive(Action.ajout, archive, titreHistorique);
                }
            }
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'habilitation pour importer des archives.");
        }
    }



    public void exportArchivesToExcel(List<Long> ids, HttpServletResponse response) throws IOException {
        Utilisateur utilisateur = sessionContext.getCurrentUser(); // Obtenez l'utilisateur connecté via SessionContext
        if (utilisateur == null) {
            throw new RuntimeException("Utilisateur non connecté");
        }

        // Vérifiez les permissions de l'utilisateur
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));

        if (optionalPermissions.isPresent()) {
            Permission permissions = optionalPermissions.get();
            if (!permissions.isCanExportArchiveToExcel()) {
                throw new UnauthorizedException("Vous n'avez pas l'habilitation pour exporter des archives.");
            }

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Archives");

                // Créer l'en-tête
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Entité");
                header.createCell(1).setCellValue("Ref.Transfert");
                header.createCell(2).setCellValue("Local d'origine");
                header.createCell(3).setCellValue("Correspondant");
                header.createCell(4).setCellValue("Date de transfert");
                header.createCell(5).setCellValue("N°conteneur");
                header.createCell(6).setCellValue("Emplacement");
                header.createCell(7).setCellValue("REF Ent");
                header.createCell(8).setCellValue("CODE");
                header.createCell(9).setCellValue("Agence/Unité");
                header.createCell(10).setCellValue("Nature");
                header.createCell(11).setCellValue("Année de création");
                header.createCell(12).setCellValue("Mois");
                header.createCell(13).setCellValue("Jour");
                header.createCell(14).setCellValue("Observations");

                // Remplir le fichier avec les données des archives
                List<Archive> archives = archiveRepository.findAllById(ids);
                int rowIdx = 1;
                for (Archive archive : archives) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(archive.getEntiteRattachee().getNom());
                    row.createCell(1).setCellValue(archive.getRefTransfert());
                    row.createCell(2).setCellValue(archive.getLocalOrigine());
                    row.createCell(3).setCellValue(archive.getCorrespondant());
                    row.createCell(4).setCellValue(archive.getDateDeTransfert().toString()); // Convertir la date en chaîne
                    row.createCell(5).setCellValue(archive.getNumeroConteneur());
                    row.createCell(6).setCellValue(archive.getEmplacement());
                    row.createCell(7).setCellValue(archive.getRefEnt());
                    row.createCell(8).setCellValue(archive.getCodeEntite());
                    row.createCell(9).setCellValue(archive.getNomAgence());
                    row.createCell(10).setCellValue(archive.getNature());

                    // Conversion des types Year et Month en String
                    row.createCell(11).setCellValue(archive.getAnneeCreation().toString()); // Convertir Year en String
                    row.createCell(12).setCellValue(archive.getMoisCreation().name()); // Convertir Month en String
                    row.createCell(13).setCellValue(archive.getJourCreation());
                    row.createCell(14).setCellValue(archive.getObservations());

                    // Génération automatique du titre pour l'historique
                    String titreHistorique = "Exportation de l'archive " + archive.getId();

                    // Ajout d'une entrée dans l'historique d'actions pour cette exportation
                    historiqueActionService.addHistoriqueActionForArchive(
                            Action.exportation,
                            archive,
                            titreHistorique
                    );
                }

                // Écrire le fichier Excel dans la réponse HTTP
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=archives.xlsx");
                workbook.write(response.getOutputStream());
            }
        } else {
            throw new UnauthorizedException("Permissions introuvables pour cet utilisateur.");
        }
    }

    public EntiteRattachee getEntiteRattacheeDetails(Long id) {
        return entiteRattacheeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("EntiteRattachee not found"));
    }

    public List<CategorieArchive> getCategoriesByEntiteRattachee(Long entiteRattacheeId) {
        EntiteRattachee entiteRattachee = entiteRattacheeRepository.findById(entiteRattacheeId)
                .orElseThrow(() -> new EntityNotFoundException("Entité Rattachée introuvable"));
        return entiteRattachee.getCategoriesArchives();
    }

    public List<Agence> getAgencesByEntiteRattachee(Long entiteRattacheeId) {
        EntiteRattachee entiteRattachee = entiteRattacheeRepository.findById(entiteRattacheeId)
                .orElseThrow(() -> new EntityNotFoundException("Entité Rattachée introuvable"));
        return entiteRattachee.getAgences();
    }


}



