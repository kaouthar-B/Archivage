package com.PFA.Gestion_des_archives.Repository;

import com.PFA.Gestion_des_archives.Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findByNom(String nom);
    Utilisateur findById(Long utilisateurId);

    Utilisateur findByEmail(String email);

}
