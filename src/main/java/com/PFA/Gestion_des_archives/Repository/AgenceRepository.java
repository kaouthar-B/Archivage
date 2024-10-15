package com.PFA.Gestion_des_archives.Repository;

import com.PFA.Gestion_des_archives.Model.Agence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AgenceRepository extends JpaRepository<Agence, Long> {
    // Méthode pour trouver les agences par ID de l'EntiteRattachee
    List<Agence> findByEntiteRattacheeId(Long entiteRattacheeId);
}

