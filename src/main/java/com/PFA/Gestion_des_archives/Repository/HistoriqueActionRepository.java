package com.PFA.Gestion_des_archives.Repository;

import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HistoriqueActionRepository extends JpaRepository<HistoriqueAction, Long> {
    List<HistoriqueAction> findBySiteArchivageId(Long siteArchivageId);
    List<HistoriqueAction> findBySiteArchivageNom(String siteArchivageNom);
    List<HistoriqueAction> findBySiteArchivage(SiteArchivage siteArchivage);
    List<HistoriqueAction> findByConteneurId(Long conteneurId);
    List<HistoriqueAction> findByConteneur(Conteneur conteneur);
    List<HistoriqueAction> findByArchiveId(Long archiveId);
    List<HistoriqueAction> findByArchive(Archive archive);

    List<HistoriqueAction> findByUtilisateur(Utilisateur utilisateur);

    List<HistoriqueAction> findByUtilisateurId(Long utilisateurId);
    List<HistoriqueAction> findByTitre(String titre);

   /* @Query("SELECT h FROM HistoriqueAction h WHERE " +
            "(:typeAction IS NULL OR h.typeAction = :typeAction) AND " +
            "(:action IS NULL OR h.action = :action) AND " +
            "(:archive IS NULL OR h.archive = :archive) AND " +
            "(:conteneur IS NULL OR h.conteneur = :conteneur) AND " +
            "(:siteArchivage IS NULL OR h.siteArchivage = :siteArchivage) AND " +
            "(:planClassification IS NULL OR h.planClassification = :planClassification) AND " +
            "(:utilisateur IS NULL OR h.utilisateur = :utilisateur) AND " +
            "(:dateAction IS NULL OR h.dateAction = :dateAction)")
    List<HistoriqueAction> searchHistoriqueActions(@Param("typeAction") TypeActionHistorique typeAction,
                                                   @Param("action") Action action,
                                                   @Param("utilisateur") Utilisateur utilisateur,
                                                   @Param("dateAction") Date dateAction);
*/

}
