package com.PFA.Gestion_des_archives.Repository;

import com.PFA.Gestion_des_archives.Model.Archive;
import com.PFA.Gestion_des_archives.Model.CategorieArchive;
import com.PFA.Gestion_des_archives.Model.ConteneurStatus;
import com.PFA.Gestion_des_archives.Model.SiteArchivage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Date;
import java.util.List;

@Repository
public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    // Basic query methods for archives
    List<Archive> findByNom(String nom);
    List<Archive> findByConteneurId(Long conteneurId);

    // Custom query for dynamic searching across multiple fields
    @Query("SELECT a FROM Archive a WHERE "
            + "(:entite IS NULL OR a.entiteRattachee.localOrigine = :entite) AND "
            + "(:refTransfert IS NULL OR a.refTransfert = :refTransfert) AND "
            + "(:localOrigine IS NULL OR a.localOrigine = :localOrigine) AND "
            + "(:correspondant IS NULL OR a.correspondant = :correspondant) AND "
            + "(:dateTransfert IS NULL OR a.dateDeTransfert = :dateTransfert) AND "
            + "(:numeroConteneur IS NULL OR a.numeroConteneur = :numeroConteneur) AND "
            + "(:emplacement IS NULL OR a.emplacement = :emplacement) AND "
            + "(:refEnt IS NULL OR a.refEnt = :refEnt) AND "
            + "(:code IS NULL OR a.codeEntite = :code) AND "
            + "(:agenceUnite IS NULL OR a.nomAgence = :agenceUnite) AND "
            + "(:nature IS NULL OR a.nature = :nature) AND "
            + "(:anneeCreation IS NULL OR a.anneeCreation = :anneeCreation) AND "
            + "(:moisCreation IS NULL OR a.moisCreation = :moisCreation) AND "
            + "(:jour IS NULL OR a.jourCreation = :jour) AND "
            + "(:observations IS NULL OR a.observations LIKE %:observations%)")
    List<Archive> searchArchives(@Param("entite") String entite,
                                 @Param("refTransfert") String refTransfert,
                                 @Param("localOrigine") String localOrigine,
                                 @Param("correspondant") String correspondant,
                                 @Param("dateTransfert") Date dateTransfert,
                                 @Param("numeroConteneur") String numeroConteneur,
                                 @Param("emplacement") String emplacement,
                                 @Param("refEnt") String refEnt,
                                 @Param("code") String code,
                                 @Param("agenceUnite") String agenceUnite,
                                 @Param("nature") String nature,
                                 @Param("anneeCreation") Year anneeCreation,
                                 @Param("moisCreation") Month moisCreation,
                                 @Param("jour") Integer jour,  // Change to Integer
                                 @Param("observations") String observations);


    // Count archives by site and category
    @Query("SELECT COUNT(a) FROM Archive a WHERE a.siteArchivage = :siteArchivage AND a.categorieArchive = :categorieArchive")
    long countBySiteArchivageAndCategorieArchive(@Param("siteArchivage") SiteArchivage siteArchivage, @Param("categorieArchive") CategorieArchive categorieArchive);
}