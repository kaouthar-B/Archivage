package com.PFA.Gestion_des_archives.Repository;

import com.PFA.Gestion_des_archives.Model.CategorieArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategorieArchiveRepository extends JpaRepository<CategorieArchive, Long> {
    // Méthode pour trouver les catégories d'archive par ID de l'EntiteRattachee
    List<CategorieArchive> findByEntiteRattacheeId(Long entiteRattacheeId);
}

