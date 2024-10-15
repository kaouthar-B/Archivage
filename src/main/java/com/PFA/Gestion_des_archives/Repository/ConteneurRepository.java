package com.PFA.Gestion_des_archives.Repository;

import com.PFA.Gestion_des_archives.Model.Conteneur;
import com.PFA.Gestion_des_archives.Model.ConteneurStatus;
import com.PFA.Gestion_des_archives.Model.TypeAffectation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConteneurRepository extends JpaRepository<Conteneur, Long> {

    // Méthodes pour compter les conteneurs par statut dans un site d'archivage spécifique
    long countBySiteArchivageIdAndStatus(Long siteId, ConteneurStatus status);

    // Méthodes pour trouver les conteneurs par statut dans un site d'archivage spécifique
    List<Conteneur> findBySiteArchivageIdAndStatus(Long siteId, ConteneurStatus status);


    List<Conteneur> findByTypeAffectation(TypeAffectation typeAffectation);
}
