package com.PFA.Gestion_des_archives.Controller;

import com.PFA.Gestion_des_archives.Model.Permission;
import com.PFA.Gestion_des_archives.Model.SessionContext;
import com.PFA.Gestion_des_archives.Model.Utilisateur;
import com.PFA.Gestion_des_archives.Repository.UtilisateurRepository;
import com.PFA.Gestion_des_archives.Service.PermissionService;
import com.PFA.Gestion_des_archives.Service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SessionContext sessionContext; // Assuming SessionContext is a custom class for managing session data
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private UtilisateurService utilisateurService;

    // Endpoint to create or update permissions
    @PostMapping("/createOrUpdate")
    public ResponseEntity<String> createOrUpdatePermissions(@RequestBody Permission newPermissions) {
        Utilisateur utilisateur = sessionContext.getCurrentUser(); // Get the current logged-in user

        // Check if the user has the ADMINISTRATEUR role
        if (!"ADMINISTRATEUR".equals(utilisateur.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Only ADMINISTRATEUR can perform this action.");
        }

        // If the user is an ADMINISTRATEUR, proceed with the operation
        Permission permission = permissionService.createOrUpdatePermissions(newPermissions);
        return ResponseEntity.ok("Permissions updated successfully.");
    }

    // Endpoint to get permissions by utilisateur
    @GetMapping("/getByUtilisateur")
    public ResponseEntity<?> getPermissionsByUtilisateur(@RequestParam("utilisateurId") Long utilisateurId) {
        Utilisateur utilisateur = sessionContext.getCurrentUser(); // Get the current logged-in user

        // Check if the user has the ADMINISTRATEUR role
        if (!"ADMINISTRATEUR".equals(utilisateur.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Only ADMINISTRATEUR can perform this action.");
        }

        // If the user is an ADMINISTRATEUR, proceed with the operation
        Optional<Utilisateur> targetUtilisateur = utilisateurService.getUserById(Math.toIntExact(utilisateurId));
        if (targetUtilisateur.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        Optional<Permission> permission = permissionService.getPermissionsByUtilisateur(targetUtilisateur);

        // Handle the case where permission is found or not found
        return permission.isPresent() ? ResponseEntity.ok(utilisateur) : ResponseEntity.notFound().build();
    }

}

