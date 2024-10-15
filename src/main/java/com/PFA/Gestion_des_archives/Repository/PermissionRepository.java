package com.PFA.Gestion_des_archives.Repository;

import com.PFA.Gestion_des_archives.Model.Permission;
import com.PFA.Gestion_des_archives.Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    // Rechercher les permissions par utilisateur
    Optional<Permission> findByUtilisateur(Utilisateur utilisateur);
}
