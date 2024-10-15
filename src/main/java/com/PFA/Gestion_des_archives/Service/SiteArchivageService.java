package com.PFA.Gestion_des_archives.Service;

import com.PFA.Gestion_des_archives.Dto.CategorieArchiveCountDto;
import com.PFA.Gestion_des_archives.Dto.SiteArchivageDto;
import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Repository.*;
import com.PFA.Gestion_des_archives.Exception.ResourceNotFoundException;
import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Repository.ArchiveRepository;
import com.PFA.Gestion_des_archives.Repository.CategorieArchiveRepository;
import com.PFA.Gestion_des_archives.Repository.ConteneurRepository;
import com.PFA.Gestion_des_archives.Repository.SiteArchivageRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SiteArchivageService {

    private static final Logger logger = LoggerFactory.getLogger(SiteArchivageService.class);

    @Autowired
    private SiteArchivageRepository siteArchivageRepository;

    @Autowired
    private ConteneurRepository conteneurRepository;

    @Autowired
    private HistoriqueActionService historiqueActionService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private ArchiveRepository archiveRepository;

    @Autowired
    private CategorieArchiveRepository categorieArchiveRepository;

    public List<SiteArchivage> getAllSites() {
        return siteArchivageRepository.findAll();
    }

    public SiteArchivage getSiteById(Long id) {
        return siteArchivageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SiteArchivage not found for this id :: " + id));
    }

    public SiteArchivage addSite(SiteArchivage site) {
        // Obtenir l'utilisateur connecté à partir de la gestion des sessions
        Utilisateur utilisateur = SessionContext.getCurrentUser();

        // Vérifier les permissions de l'utilisateur
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));
        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanAddSite()) {
            // Créer et sauvegarder un nouveau site d'archivage
            SiteArchivage newSite = siteArchivageRepository.save(site);

            // Ajouter une action à l'historique
            String titre = "Création du site d'archivage " + newSite.getNom();
            historiqueActionService.addHistoriqueActionForSiteArchivage(Action.ajout, newSite, titre);

            return newSite;
        } else {
            throw new SecurityException("L'utilisateur n'a pas la permission d'ajouter un site d'archivage.");
        }
    }



    public SiteArchivage updateSite(Long id, SiteArchivage site) {
        // Obtenir l'utilisateur connecté à partir de la gestion des sessions
        Utilisateur utilisateur = SessionContext.getCurrentUser();

        // Vérifier les permissions de l'utilisateur
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));
        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanUpdateSite()) {
            // Obtenir le site existant
            SiteArchivage existingSite = getSiteById(id);

            // Mettre à jour les attributs du site
            existingSite.setNom(site.getNom());
            existingSite.setAdresse(site.getAdresse());
            existingSite.setLocalisation(site.getLocalisation());
            existingSite.setSurface(site.getSurface());
            existingSite.setArchiviste(site.getArchiviste());
            existingSite.setPersonnel(site.getPersonnel());
            existingSite.setNombreEmplacements(site.getNombreEmplacements());

            // Sauvegarder les modifications
            SiteArchivage updatedSite = siteArchivageRepository.save(existingSite);

            // Ajouter une action à l'historique
            String titre = "Mise à jour du site d'archivage " + updatedSite.getNom();
            historiqueActionService.addHistoriqueActionForSiteArchivage(Action.modification, updatedSite, titre);

            return updatedSite;
        } else {
            throw new SecurityException("L'utilisateur n'a pas la permission de mettre à jour le site d'archivage.");
        }
    }



    public void deleteSite(Long id) {
        // Obtenir l'utilisateur connecté à partir de la gestion des sessions
        Utilisateur utilisateur = SessionContext.getCurrentUser();
   SiteArchivage siteArchivage = getSiteById(id);

            // Supprimer le site
            siteArchivageRepository.delete(siteArchivage);

            // Ajouter une action à l'historique
            String titre = "Suppression du site d'archivage " + siteArchivage.getNom();
            historiqueActionService.addHistoriqueActionForSiteArchivage(Action.suppression, siteArchivage, titre);
    }



    public List<Conteneur> getConteneursBySiteId(Long id) {
        SiteArchivage siteArchivage = getSiteById(id);
        return siteArchivage.getConteneurs();
    }

    public void importSiteArchivageFromExcel(MultipartFile file) throws IOException {
        // Obtenir l'utilisateur connecté à partir de la gestion des sessions
        Utilisateur utilisateur = SessionContext.getCurrentUser();

        // Vérifier les permissions de l'utilisateur
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));
        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanImportSiteArchivageFromExcel()) {
            List<SiteArchivage> siteArchivages = new ArrayList<>();

            // Lire le fichier Excel
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Skip header row
                }

                SiteArchivage siteArchivage = new SiteArchivage();
                siteArchivage.setNom(row.getCell(0).getStringCellValue());
                siteArchivage.setAdresse(row.getCell(1).getStringCellValue());
                siteArchivage.setLocalisation(row.getCell(2).getStringCellValue());
                siteArchivage.setSurface(row.getCell(3).getStringCellValue());
                siteArchivage.setArchiviste(row.getCell(4).getStringCellValue());
                siteArchivage.setPersonnel(row.getCell(5).getStringCellValue());
                siteArchivage.setNombreEmplacements((int) row.getCell(6).getNumericCellValue());
                siteArchivages.add(siteArchivage);
            }

            // Sauvegarder tous les sites importés
            siteArchivageRepository.saveAll(siteArchivages);
            workbook.close();

            // Ajouter une action à l'historique
            String titre = "Importation de sites d'archivage depuis un fichier Excel";
            historiqueActionService.addHistoriqueActionForSiteArchivage(Action.importation, null, titre);
        } else {
            throw new SecurityException("L'utilisateur n'a pas la permission d'importer des sites d'archivage.");
        }
    }



    public byte[] exportSiteArchivageToExcel() throws IOException {
        // Obtenir l'utilisateur connecté à partir de la gestion des sessions
        Utilisateur utilisateur = SessionContext.getCurrentUser();

        // Vérifier les permissions de l'utilisateur
        Optional<Permission> optionalPermissions = permissionService.getPermissionsByUtilisateur(Optional.of(utilisateur));
        if (optionalPermissions.isPresent() && optionalPermissions.get().isCanExportSiteArchivageToExcel()) {
            List<SiteArchivage> siteArchivages = getAllSites();

            // Créer le fichier Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("SiteArchivage");

            // Créer l'en-tête
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Nom");
            headerRow.createCell(1).setCellValue("Adresse");
            headerRow.createCell(2).setCellValue("Localisation");
            headerRow.createCell(3).setCellValue("Surface");
            headerRow.createCell(4).setCellValue("Archiviste");
            headerRow.createCell(5).setCellValue("Personnel");
            headerRow.createCell(6).setCellValue("Nombre de Conteneurs");

            // Remplir les données
            int rowNum = 1;
            for (SiteArchivage siteArchivage : siteArchivages) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(siteArchivage.getNom());
                row.createCell(1).setCellValue(siteArchivage.getAdresse());
                row.createCell(2).setCellValue(siteArchivage.getLocalisation());
                row.createCell(3).setCellValue(siteArchivage.getSurface());
                row.createCell(4).setCellValue(siteArchivage.getArchiviste());
                row.createCell(5).setCellValue(siteArchivage.getPersonnel());
                row.createCell(6).setCellValue(siteArchivage.getNombreEmplacements());
            }

            // Écrire le fichier dans un flux de sortie
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            // Ajouter une action à l'historique
            String titre = "Exportation de sites d'archivage vers un fichier Excel";
            historiqueActionService.addHistoriqueActionForSiteArchivage(Action.exportation, null, titre);

            return outputStream.toByteArray();
        } else {
            throw new SecurityException("L'utilisateur n'a pas la permission d'exporter des sites d'archivage.");
        }
    }



    public long getCountOfSites() {
        return siteArchivageRepository.count();
    }

    public long countConteneursByStatus(Long siteId, String status) {
        return conteneurRepository.countBySiteArchivageIdAndStatus(siteId, ConteneurStatus.valueOf(status));
    }

    public List<Conteneur> findConteneursByStatus(Long siteId, String status) {
        return conteneurRepository.findBySiteArchivageIdAndStatus(siteId, ConteneurStatus.valueOf(status));
    }



    public Integer getNombreEmplacements(Long siteArchivageId) {
        Optional<SiteArchivage> optionalSiteArchivage = siteArchivageRepository.findById(siteArchivageId);
        if (optionalSiteArchivage.isPresent()) {
            return optionalSiteArchivage.get().getNombreEmplacements();
        } else {
            throw new ResourceNotFoundException("Site d'archivage non trouvé avec l'ID: " + siteArchivageId);
        }
    }


    public Integer getNombreEmplacementsCharges(Long siteArchivageId) {
        Optional<SiteArchivage> optionalSiteArchivage = siteArchivageRepository.findById(siteArchivageId);
        if (optionalSiteArchivage.isPresent()) {
            return optionalSiteArchivage.get().getNombreEmplacementsCharges();
        } else {
            throw new ResourceNotFoundException("Site d'archivage non trouvé avec l'ID: " + siteArchivageId);
        }
    }


    public Integer getNombreEmplacementsVides(Long siteArchivageId) {
        Optional<SiteArchivage> optionalSiteArchivage = siteArchivageRepository.findById(siteArchivageId);
        if (optionalSiteArchivage.isPresent()) {
            return optionalSiteArchivage.get().getNombreEmplacementsVides();
        } else {
            throw new ResourceNotFoundException("Site d'archivage non trouvé avec l'ID: " + siteArchivageId);
        }
    }



    public List<SiteArchivageDto> getAllSiteArchivagesWithCategoryCount() {
        List<SiteArchivage> siteArchivages = siteArchivageRepository.findAll();
        List<SiteArchivageDto> siteArchivageDtos = new ArrayList<>();

        for (SiteArchivage siteArchivage : siteArchivages) {
            SiteArchivageDto dto = new SiteArchivageDto();
            dto.setSiteArchivage(siteArchivage);

            List<CategorieArchive> categories = categorieArchiveRepository.findAll();
            List<CategorieArchiveCountDto> categoryCounts = new ArrayList<>();

            for (CategorieArchive category : categories) {
                long count = archiveRepository.countBySiteArchivageAndCategorieArchive(siteArchivage, category);
                CategorieArchiveCountDto categoryCountDto = new CategorieArchiveCountDto();
                categoryCountDto.setCategorieArchive(category);
                categoryCountDto.setCount(count);
                categoryCounts.add(categoryCountDto);
            }

            dto.setCategorieArchiveCounts(categoryCounts);
            siteArchivageDtos.add(dto);
        }

        return siteArchivageDtos;
    }
}

