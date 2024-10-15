package com.PFA.Gestion_des_archives.Service;

import com.PFA.Gestion_des_archives.Dto.PlanClassificationDto;
import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Repository.PlanClassificationRepository;
import com.PFA.Gestion_des_archives.Repository.HistoriqueActionRepository;
import com.PFA.Gestion_des_archives.Model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanClassificationService {
    @Autowired
    private PlanClassificationRepository planClassificationRepository;
    private PlanClassification planClassification;

    @Autowired
    private HistoriqueActionRepository historiqueActionRepository;
    @Autowired
    private HistoriqueActionService historiqueActionService;



    public void createPlanClassification(PlanClassification planClassification) {
        planClassificationRepository.save(planClassification);
        // Enregistrer l'action d'ajout dans l'historique
        historiqueActionService.addHistoriqueActionForPlanClassification(
                Action.ajout,
                planClassification,
                "Ajout d'un nouveau plan de classification"
        );
    }


    public List<Object> rechercheParFiltre(PlanClassification planClassification, String typeRecherche, String searchQuery) {
                List<Object> resultats = new ArrayList<>();

                switch (typeRecherche.toLowerCase()) {
                    case "proprietairedesarchives":
                        // Recherche par nom du propriétaire des archives
                        for (ProprietaireDesArchives proprietaire : planClassification.getProprietairesDesArchives()) {
                            if (proprietaire.getNom().toLowerCase().contains(searchQuery.toLowerCase())) {
                                resultats.add(proprietaire);
                            }
                        }
                        break;

                    case "entiterattachee":
                        for (EntiteRattachee entite : planClassification.getEntitesRattachees()) {
                            // Recherche par nom, ref ou code de l'entité rattachée
                            if (entite.getNom().toLowerCase().contains(searchQuery.toLowerCase()) ||
                                    (entite.getRef() != null && entite.getRef().toLowerCase().contains(searchQuery.toLowerCase())) ||
                                    (entite.getCodeEntite() != null && entite.getCodeEntite().toLowerCase().contains(searchQuery.toLowerCase()))) {
                                resultats.add(entite);
                            }
                        }
                        break;

                    case "agence":
                        for (Agence agence : planClassification.getAgences()) {
                            // Recherche par nom de l'agence
                            if (agence.getNom().toLowerCase().contains(searchQuery.toLowerCase())) {
                                resultats.add(agence);
                            }
                        }
                        break;

            case "categoriearchive":
                for (CategorieArchive categorie : planClassification.getCategoriesArchive()) {
                    // Recherche par nom ou code de la catégorie d'archives
                    if (categorie.getNom().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            (categorie.getCode() != null && categorie.getCode().toLowerCase().contains(searchQuery.toLowerCase()))) {
                        resultats.add(categorie);
                    }
                }
                break;

            default:
                System.out.println("Type de recherche non valide. Veuillez spécifier un type valide : proprietairedesarchives, entiterattachee, agence, categoriearchive.");
        }

        return resultats;
    }



    public void ajouterLigneDansPlanClassification(PlanClassification planClassification,
                                                   String nomProprietaire,
                                                   String nomEntite, String ref, String codeEntite,
                                                   String nomAgence,
                                                   String nomCategorie, String codeCategorie) {
        // Assurez-vous que getProprietairesDesArchives() retourne une liste
        List<ProprietaireDesArchives> proprietairesList = planClassification.getProprietairesDesArchives();
        if (proprietairesList == null) {
            proprietairesList = new ArrayList<>();
            planClassification.setProprietairesDesArchives(proprietairesList);
        }

        // Trouver le propriétaire des archives
        ProprietaireDesArchives proprietaire = findProprietaire(proprietairesList, nomProprietaire);

        // Si le propriétaire n'existe pas, ou si l'utilisateur veut en créer un nouveau malgré son existence
        if (proprietaire == null || userWantsToCreateNewInstance()) {
            proprietaire = createProprietaire(proprietairesList, nomProprietaire);
        }

        // Récupérer ou initialiser la liste des entités rattachées pour le propriétaire
        List<EntiteRattachee> entitesRattachees = planClassification.getEntitesRattachees();
        if (entitesRattachees == null) {
            entitesRattachees = new ArrayList<>();
            planClassification.setEntitesRattachees(entitesRattachees);
        }

        // Trouver l'entité rattachée
        EntiteRattachee entite = findEntite(entitesRattachees, nomEntite);

        // Si l'entité n'existe pas, ou si l'utilisateur veut en créer une nouvelle malgré son existence
        if (entite == null || userWantsToCreateNewInstance()) {
            entite = createEntite(entitesRattachees, nomEntite);
        }

        // Définir les valeurs de ref et de codeEntite si elles sont fournies
        if (ref != null) {
            entite.setRef(ref);
        }
        if (codeEntite != null) {
            entite.setCodeEntite(codeEntite);
        }

        // Récupérer ou initialiser la liste des agences pour l'entité rattachée
        List<Agence> agences = planClassification.getAgences();
        if (agences == null) {
            agences = new ArrayList<>();
            planClassification.setAgences(agences);
        }

        if (nomAgence != null && !nomAgence.isEmpty()) {
            // Trouver l'agence
            Agence agence = findAgence(agences, nomAgence);

            // Si l'agence n'existe pas, ou si l'utilisateur veut en créer une nouvelle malgré son existence
            if (agence == null || userWantsToCreateNewInstance()) {
                agence = createAgence(agences, nomAgence);
            }

            // Récupérer ou initialiser la liste des catégories d'archives pour l'agence
            List<CategorieArchive> categoriesArchive = planClassification.getCategoriesArchive();
            if (categoriesArchive == null) {
                categoriesArchive = new ArrayList<>();
                planClassification.setCategoriesArchive(categoriesArchive);
            }

            if (nomCategorie != null && !nomCategorie.isEmpty()) {
                // Ajouter une nouvelle catégorie d'archives à l'agence
                CategorieArchive categorie = new CategorieArchive();
                categorie.setNom(nomCategorie);
                if (codeCategorie != null) {
                    categorie.setCode(codeCategorie);
                }
                categoriesArchive.add(categorie);
            }
        } else {
            if (nomCategorie != null && !nomCategorie.isEmpty()) {
                // Si aucune agence n'est spécifiée, ajouter la catégorie d'archives à l'entité rattachée
                CategorieArchive categorie = new CategorieArchive();
                categorie.setNom(nomCategorie);
                if (codeCategorie != null) {
                    categorie.setCode(codeCategorie);
                }
                entite.getCategoriesArchives().add(categorie);
            }
        }
        historiqueActionService.addHistoriqueActionForPlanClassification(
                Action.ajout,
                planClassification,
                "Ajout d'une nouvelle ligne dans le plan de classification"
        );
    }

    // Méthode fictive pour représenter la décision de l'utilisateur de créer une nouvelle instance
    private boolean userWantsToCreateNewInstance() {
        // Implémentez la logique pour demander à l'utilisateur s'il souhaite créer une nouvelle instance
        // Exemple : return true si l'utilisateur le souhaite, sinon return false
        return true; // Remplacer par la logique réelle
    }





    public void ajouterNouvelleAgence(String nomAgence, String nomEntite, String refEntite) {
        // Rechercher l'entité rattachée à laquelle l'agence sera ajoutée
        EntiteRattachee entiteRattachee = planClassificationRepository.findAll().stream()
                .flatMap(planClassification -> planClassification.getEntitesRattachees().stream())
                .filter(entite -> entite.getNom().equalsIgnoreCase(nomEntite) && entite.getRef().equalsIgnoreCase(refEntite))
                .findFirst()
                .orElse(null);

        if (entiteRattachee == null) {
            throw new IllegalArgumentException("L'entité rattachée spécifiée n'existe pas.");
        }

        // Créer la nouvelle agence
        Agence nouvelleAgence = new Agence();
        nouvelleAgence.setNom(nomAgence);
        nouvelleAgence.setEntiteRattachee(entiteRattachee);

        // Ajouter la nouvelle agence à l'entité rattachée
        entiteRattachee.addAgence(nouvelleAgence);

        // Sauvegarder les modifications dans PlanClassification
        PlanClassification planClassification = entiteRattachee.getPlanClassification();
        planClassification.getAgences().add(nouvelleAgence);

        planClassificationRepository.save(planClassification);
        historiqueActionService.addHistoriqueActionForPlanClassification(
                Action.ajout,
                planClassification,
                "Ajout d'une nouvelle agence dans le plan de classification"
        );
    }



    public void ajouterNouvelleCategorie(String nomCategorie, String nomEntite, String refEntite, String nomAgence) {
        // Trouver l'entité rattachée
        EntiteRattachee entiteRattachee = planClassificationRepository.findAll().stream()
                .flatMap(planClassification -> planClassification.getEntitesRattachees().stream())
                .filter(entite -> entite.getNom().equalsIgnoreCase(nomEntite) && entite.getRef().equalsIgnoreCase(refEntite))
                .findFirst()
                .orElse(null);

        if (entiteRattachee == null) {
            throw new IllegalArgumentException("L'entité rattachée spécifiée n'existe pas.");
        }

        // Trouver l'agence (optionnelle)
        Agence agence = null;
        if (nomAgence != null && !nomAgence.isEmpty()) {
            agence = entiteRattachee.getAgences().stream()
                    .filter(ag -> ag.getNom().equalsIgnoreCase(nomAgence))
                    .findFirst()
                    .orElse(null);

            if (agence == null) {
                throw new IllegalArgumentException("L'agence spécifiée n'existe pas ou n'est pas liée à l'entité rattachée spécifiée.");
            }
        }

        // Créer la nouvelle catégorie d'archive
        CategorieArchive nouvelleCategorie = new CategorieArchive();
        nouvelleCategorie.setNom(nomCategorie);

        // Associer la nouvelle catégorie à l'entité rattachée et, éventuellement, à l'agence
        entiteRattachee.addCategorieArchive(nouvelleCategorie);
        if (agence != null) {
            agence.addCategorieArchive(nouvelleCategorie);
        }

        // Sauvegarder les modifications dans PlanClassification
        PlanClassification planClassification = entiteRattachee.getPlanClassification();
        planClassification.getCategoriesArchive().add(nouvelleCategorie);

        planClassificationRepository.save(planClassification);
        historiqueActionService.addHistoriqueActionForPlanClassification(
                Action.ajout,
                planClassification,
                "Ajout d'une nouvelle catégorie d'archive dans le plan de classification"
        );
    }




    public void updatePlanClassification(PlanClassification planClassification,
                                         String oldNomProprietaire, String newNomProprietaire,
                                         String oldNomEntite, String newNomEntite, String newRef,
                                         String newCodeEntite, String oldNomAgence, String newNomAgence,
                                         String oldNomCategorie, String newNomCategorie, String newCodeCategorie) {
        // Rechercher l'instance de ProprietaireDesArchives à mettre à jour
        ProprietaireDesArchives proprietaire = findProprietaire(planClassification.getProprietairesDesArchives(), oldNomProprietaire);

        if (proprietaire != null) {
            // Mettre à jour le nom du propriétaire des archives, si un nouveau nom est fourni
            if (newNomProprietaire != null && !newNomProprietaire.isEmpty()) {
                proprietaire.setNom(newNomProprietaire);
            }

            // Rechercher l'instance de EntiteRattachee à mettre à jour
            EntiteRattachee entite = findEntite(planClassification.getEntitesRattachees(), oldNomEntite);

            if (entite != null) {
                // Mettre à jour le nom, le ref ou le code de l'entité rattachée, si de nouvelles valeurs sont fournies
                if (newNomEntite != null && !newNomEntite.isEmpty()) {
                    entite.setNom(newNomEntite);
                }
                if (newRef != null && !newRef.isEmpty()) {
                    entite.setRef(newRef);
                }
                if (newCodeEntite != null && !newCodeEntite.isEmpty()) {
                    entite.setCodeEntite(newCodeEntite);
                }

                // Rechercher l'instance de Agence à mettre à jour
                Agence agence = findAgence(planClassification.getAgences(), oldNomAgence);

                if (agence != null) {
                    // Mettre à jour le nom de l'agence, si un nouveau nom est fourni
                    if (newNomAgence != null && !newNomAgence.isEmpty()) {
                        agence.setNom(newNomAgence);
                    }

                    // Rechercher l'instance de CategorieArchive à mettre à jour
                    CategorieArchive categorie = findCategorie(planClassification.getCategoriesArchive(), oldNomCategorie);

                    if (categorie != null) {
                        // Mettre à jour le nom et le code de la catégorie d'archives, si de nouvelles valeurs sont fournies
                        if (newNomCategorie != null && !newNomCategorie.isEmpty()) {
                            categorie.setNom(newNomCategorie);
                        }
                        if (newCodeCategorie != null && !newCodeCategorie.isEmpty()) {
                            categorie.setCode(newCodeCategorie);
                        }
                    }
                } else {
                    // Si aucune agence n'est spécifiée, mettre à jour directement la catégorie d'archives de l'entité rattachée
                    CategorieArchive categorie = findCategorie(planClassification.getCategoriesArchive(), oldNomCategorie);

                    if (categorie != null) {
                        if (newNomCategorie != null && !newNomCategorie.isEmpty()) {
                            categorie.setNom(newNomCategorie);
                        }
                        if (newCodeCategorie != null && !newCodeCategorie.isEmpty()) {
                            categorie.setCode(newCodeCategorie);
                        }
                    }
                }
            }
        }
        historiqueActionService.addHistoriqueActionForPlanClassification(
                Action.modification,
                planClassification,
                "Modification dans le plan de classification"
        );
    }



    // Les méthodes excelToPlanClassifications et planClassificationsToExcel restent inchangées

    public void deletePlanClassification(Long id) {
        PlanClassification planClassification = planClassificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan de classification introuvable."));
        planClassificationRepository.delete(planClassification);
        // Enregistrer l'action de suppression dans l'historique
        historiqueActionService.addHistoriqueActionForPlanClassification(
                Action.suppression,
                planClassification,
                "Suppression du plan de classification"
        );
    }

    public PlanClassification excelToPlanClassification(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            PlanClassification planClassification = new PlanClassification();
            List<ProprietaireDesArchives> proprietairesList = new ArrayList<>();
            List<EntiteRattachee> entitesList = new ArrayList<>();
            List<CategorieArchive> categoriesList = new ArrayList<>();
            List<Agence> agencesList = new ArrayList<>();

            ProprietaireDesArchives currentProprietaire = null;
            EntiteRattachee currentEntite = null;
            Agence currentAgence = null;

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if (rowNumber == 0) { // Skip header row
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0: // Propriétaire des archives
                            String proprietaireName = currentCell.getStringCellValue();
                            if (proprietaireName != null && !proprietaireName.isEmpty()) {
                                currentProprietaire = findProprietaire(proprietairesList, proprietaireName);
                                if (currentProprietaire == null) {
                                    currentProprietaire = createProprietaire(proprietairesList, proprietaireName);
                                }
                            }
                            break;
                        case 1: // Entité rattachée
                            String entiteName = currentCell.getStringCellValue();
                            if (entiteName != null && !entiteName.isEmpty()) {
                                currentEntite = findEntite(entitesList, entiteName);
                                if (currentEntite == null) {
                                    currentEntite = createEntite(entitesList, entiteName);
                                }
                                currentAgence = null; // Reset current Agence
                            }
                            break;
                        case 2: // Agence
                            String agenceName = currentCell.getStringCellValue();
                            if (agenceName != null && !agenceName.isEmpty()) {
                                currentAgence = findAgence(agencesList, agenceName);
                                if (currentAgence == null) {
                                    currentAgence = createAgence(agencesList, agenceName);
                                }
                            } else {
                                currentAgence = null; // Pas d'agence pour cette ligne
                            }
                            break;
                        case 3: // REF
                            String refValue = currentCell.getStringCellValue();
                            if (currentEntite != null) {
                                currentEntite.setRef(refValue);
                            }
                            break;
                        case 4: // Code Entité
                            String codeEntiteValue = currentCell.getStringCellValue();
                            if (currentEntite != null) {
                                currentEntite.setCodeEntite(codeEntiteValue);
                            }
                            break;
                        case 5: // Catégorie d'archives
                            String categorieName = currentCell.getStringCellValue();
                            if (categorieName != null && !categorieName.isEmpty()) {
                                CategorieArchive categorie = new CategorieArchive();
                                categorie.setNom(categorieName);
                                categoriesList.add(categorie);

                                if (currentAgence != null) {
                                    currentAgence.getCategoriesArchive().add(categorie);
                                } else if (currentEntite != null) {
                                    currentEntite.getCategoriesArchive().add(categorie);
                                }
                            }
                            break;
                        case 6: // Code catégorie
                            String codeCategorieValue = currentCell.getStringCellValue();
                            if (!categoriesList.isEmpty()) {
                                CategorieArchive lastCategorie = categoriesList.get(categoriesList.size() - 1);
                                lastCategorie.setCode(codeCategorieValue);
                            }
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
            }

            planClassification.setProprietairesDesArchives(proprietairesList);
            planClassification.setEntitesRattachees(entitesList);
            planClassification.setCategoriesArchive(categoriesList);
            planClassification.setAgences(agencesList);

            workbook.close();

            // Log the import action
            historiqueActionService.addHistoriqueActionForPlanClassification(Action.importation, planClassification, "Importation du plan de classification");

            return planClassification;

        } catch (IOException e) {
            throw new RuntimeException("Échec de la conversion du fichier Excel en PlanClassification : " + e.getMessage());
        }
    }



    public ByteArrayInputStream planClassificationToExcel(PlanClassification planClassification) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("PlanClassification");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Propriétaire des archives", "Entités rattachées", "Agence", "Ref", "Code Entité", "Catégorie d’archives (CA)", "Code"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (ProprietaireDesArchives proprietaire : planClassification.getProprietairesDesArchives()) {
                boolean isFirstEntiteForProprietaire = true;
                for (EntiteRattachee entite : planClassification.getEntitesRattachees()) {
                    boolean isFirstAgenceForEntite = true;
                    if (!planClassification.getAgences().isEmpty()) {
                        for (Agence agence : planClassification.getAgences()) {
                            boolean isFirstCategorieForAgence = true;
                            for (CategorieArchive categorie : planClassification.getCategoriesArchive()) {
                                Row row = sheet.createRow(rowIdx++);

                                if (isFirstEntiteForProprietaire) {
                                    row.createCell(0).setCellValue(proprietaire.getNom());
                                    isFirstEntiteForProprietaire = false;
                                }

                                if (isFirstAgenceForEntite) {
                                    row.createCell(1).setCellValue(entite.getNom());
                                    isFirstAgenceForEntite = false;
                                }

                                if (isFirstCategorieForAgence) {
                                    row.createCell(2).setCellValue(agence.getNom()); // Nom de l'agence
                                    row.createCell(3).setCellValue(entite.getRef()); // Ref de l'entité rattachée
                                    row.createCell(4).setCellValue(entite.getCodeEntite()); // Code de l'entité rattachée
                                    isFirstCategorieForAgence = false;
                                }

                                row.createCell(5).setCellValue(categorie.getNom());
                                row.createCell(6).setCellValue(categorie.getCode());
                            }
                        }
                    } else {
                        for (CategorieArchive categorie : planClassification.getCategoriesArchive()) {
                            Row row = sheet.createRow(rowIdx++);

                            if (isFirstEntiteForProprietaire) {
                                row.createCell(0).setCellValue(proprietaire.getNom());
                                isFirstEntiteForProprietaire = false;
                            }

                            if (isFirstAgenceForEntite) {
                                row.createCell(1).setCellValue(entite.getNom());
                                row.createCell(3).setCellValue(entite.getRef()); // Ref de l'entité rattachée
                                row.createCell(4).setCellValue(entite.getCodeEntite()); // Code de l'entité rattachée
                                isFirstAgenceForEntite = false;
                            }

                            row.createCell(5).setCellValue(categorie.getNom());
                            row.createCell(6).setCellValue(categorie.getCode());
                        }
                    }
                }
            }

            workbook.write(out);

            // Log the export action
            historiqueActionService.addHistoriqueActionForPlanClassification(Action.exportation, planClassification, "Exportation du plan de classification");

            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Échec de l'exportation des données en Excel : " + e.getMessage());
        }
    }



//* PS : pour la 2eme erreur : il n'ya pas de setRefOrCode, mais j'ai deux setters qui sont setRef et setCodeEntite.
// et je ne veux pas de 'refOrCodeEntite' mais je veux ref toute seule et codeEntie toute seul.

    // Méthode pour trouver ou créer un propriétaire des archives dans PlanClassification
    public ProprietaireDesArchives findProprietaire(List<ProprietaireDesArchives> proprietairesList, String proprietaireName) {
        for (ProprietaireDesArchives proprietaire : proprietairesList) {
            if (proprietaire.getNom().equals(proprietaireName)) {
                return proprietaire;
            }
        }
        return null; // Retourne null si le propriétaire n'est pas trouvé
    }

    public EntiteRattachee findEntite(List<EntiteRattachee> entitesList, String entiteName) {
        for (EntiteRattachee entite : entitesList) {
            if (entite.getNom().equals(entiteName)) {
                return entite;
            }
        }
        return null; // Retourne null si l'entité n'est pas trouvée
    }

    public Agence findAgence(List<Agence> agencesList, String agenceName) {
        for (Agence agence : agencesList) {
            if (agence.getNom().equals(agenceName)) {
                return agence;
            }
        }
        return null; // Retourne null si l'agence n'est pas trouvée
    }

    public CategorieArchive findCategorie(List<CategorieArchive> categoriesList, String categorieName) {
        for (CategorieArchive categorie : categoriesList) {
            if (categorie.getNom().equals(categorieName)) {
                return categorie;
            }
        }
        return null; // Retourne null si categorieArchive n'est pas trouvée
    }


    private ProprietaireDesArchives createProprietaire(List<ProprietaireDesArchives> proprietairesList, String proprietaireName) {
        ProprietaireDesArchives newProprietaire = new ProprietaireDesArchives();
        newProprietaire.setNom(proprietaireName);
        proprietairesList.add(newProprietaire);
        return newProprietaire;
    }

    private EntiteRattachee createEntite(List<EntiteRattachee> entitesList, String entiteName) {
        EntiteRattachee newEntite = new EntiteRattachee();
        newEntite.setNom(entiteName);
        entitesList.add(newEntite);
        return newEntite;
    }

    private Agence createAgence(List<Agence> agencesList, String agenceName) {
        Agence newAgence = new Agence();
        newAgence.setNom(agenceName);
        agencesList.add(newAgence);
        return newAgence;
    }


    // Method to retrieve the PlanClassification object
    public PlanClassification getPlanClassification() {
        // Assuming there is only one PlanClassification or a specific way to identify it
        // For instance, if you're fetching the first one or by a specific ID.
        return planClassificationRepository.findById(1L) // Replace with the appropriate method to retrieve the data
                .orElseThrow(() -> new RuntimeException("PlanClassification not found"));
    }

    public List<PlanClassificationDto> getPlanClassificationTable() {
        List<PlanClassification> planClassifications = planClassificationRepository.findAll();

        return planClassifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PlanClassificationDto convertToDto(PlanClassification planClassification) {
        PlanClassificationDto dto = new PlanClassificationDto();
        dto.setRef(planClassification.getRef());
        dto.setCode(planClassification.getCode());

        List<ProprietaireDesArchives> proprietaires = planClassification.getProprietairesDesArchives();
        List<EntiteRattachee> entites = planClassification.getEntitesRattachees();
        List<CategorieArchive> categories = planClassification.getCategoriesArchive();
        List<Agence> agences = planClassification.getAgences();

        for (ProprietaireDesArchives proprietaire : proprietaires) {
            for (EntiteRattachee entite : entites) {
                if (entite.getProprietaireDesArchives().equals(proprietaire)) {
                    dto.addProprietaireDesArchives(proprietaire.getNom());
                    dto.addEntitesRattachees(entite.getRef() + " - " + entite.getCodeEntite());
                    dto.addCodeEntite(entite.getCodeEntite());
                    dto.addRef(entite.getRef());

                    for (Agence agence : agences) {
                        if (agence.getEntiteRattachee().equals(entite)) {
                            dto.addAgences(agence.getNom());

                            for (CategorieArchive categorie : categories) {
                                if (categorie.getEntiteRattachee().equals(entite)) {
                                    dto.addCategoriesArchive(categorie.getCode());
                                }
                            }
                        }
                    }
                }
            }
        }
        return dto;
    }

}

