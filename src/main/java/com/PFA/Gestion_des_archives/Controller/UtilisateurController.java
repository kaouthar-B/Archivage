package com.PFA.Gestion_des_archives.Controller;

import com.PFA.Gestion_des_archives.Dto.UtilisateurDto;
import com.PFA.Gestion_des_archives.Exception.UnauthorizedException;
import com.PFA.Gestion_des_archives.Model.Role;
import com.PFA.Gestion_des_archives.Model.SessionContext;
import com.PFA.Gestion_des_archives.Model.Utilisateur;
import com.PFA.Gestion_des_archives.Service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    // Endpoint pour créer un utilisateur
    @PostMapping("/utilisateur")
    public ResponseEntity<Utilisateur> createUser(@RequestParam("nom") String nom,
                                                  @RequestParam("prenom") String prenom,
                                                  @RequestParam("email") String email,
                                                  @RequestParam("motDePasse") String motDePasse,
                                                  @RequestParam("role") Role role) {
        Utilisateur utilisateurCourant = SessionContext.getCurrentUser();

        // Vérification du rôle de l'utilisateur courant
        if (utilisateurCourant.getRole() != Role.ADMINISTRATEUR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        try {
            Utilisateur nouvelUtilisateur = utilisateurService.creerUtilisateur(nom, prenom, email, motDePasse, role);
            return ResponseEntity.ok(nouvelUtilisateur);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    // Endpoint pour afficher tous les utilisateurs
    @GetMapping("/afficher")
    public ResponseEntity<List<Utilisateur>> afficherTousUtilisateurs() {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.afficherTousUtilisateurs();
            return ResponseEntity.ok(utilisateurs);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(403).body(null); // Code HTTP 403 pour interdiction
        }
    }

    // Endpoint pour modifier un utilisateur
    @PutMapping("/utilisateur/{id}")
    public ResponseEntity<Utilisateur> modifyUser(@PathVariable("id") int idUtilisateur,
                                                  @RequestParam("nom") String nouveauNom,
                                                  @RequestParam("prenom") String nouveauPrenom,
                                                  @RequestParam("email") String nouvelEmail,
                                                  @RequestParam("role") Role nouveauRole) {
        Utilisateur utilisateurCourant = SessionContext.getCurrentUser();

        // Vérification du rôle de l'utilisateur courant
        if (utilisateurCourant.getRole() != Role.ADMINISTRATEUR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        try {
            Utilisateur utilisateurModifie = utilisateurService.modifierUtilisateur(idUtilisateur, nouveauNom, nouveauPrenom, nouvelEmail, nouveauRole);
            return ResponseEntity.ok(utilisateurModifie);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    // Endpoint pour obtenir tous les utilisateurs
    @GetMapping
    public ResponseEntity<List<Utilisateur>> getAllUsers() {
        List<Utilisateur> utilisateurs = utilisateurService.getAllUsers();
        return ResponseEntity.ok(utilisateurs);
    }

    // Endpoint pour obtenir un utilisateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Utilisateur>> getUserById(@PathVariable int id) {
        Optional<Utilisateur> utilisateur = utilisateurService.getUserById(id);
        return utilisateur.isPresent() ? ResponseEntity.ok(utilisateur) : ResponseEntity.notFound().build();
    }

    // Endpoint pour obtenir un utilisateur par nom
    @GetMapping("/nom/{nom}")
    public ResponseEntity<Optional<Utilisateur>> getUserByNom(@PathVariable String nom) {
        Optional<Utilisateur> utilisateur = utilisateurService.getUserByNom(nom);
        return utilisateur.isPresent() ? ResponseEntity.ok(utilisateur) : ResponseEntity.notFound().build();
    }

    // Endpoint pour authentifier un utilisateur
    @PostMapping("/authentifier")
    public ResponseEntity<Map<String, Object>> authenticate(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        boolean isAuthenticated = utilisateurService.authenticate(email, password);

        if (isAuthenticated) {
            Utilisateur utilisateur = SessionContext.getCurrentUser(); // Récupérer l'utilisateur authentifié
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Authentification réussie");
            response.put("userId", utilisateur.getId());
            response.put("userType", utilisateur.getRole().name()); // Assuming role is an Enum
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(Collections.singletonMap("message", "Échec de l'authentification"));
        }
    }


    // Endpoint pour déconnecter un utilisateur
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        utilisateurService.logout();
        return ResponseEntity.ok("Déconnexion réussie");
    }

    // Endpoint pour changer le mot de passe
    @PutMapping("/changerMotDePasse")
    public ResponseEntity<String> changerMotDePasse(
            @RequestParam String ancienMotDePasse,
            @RequestParam String nouveauMotDePasse) {
        try {
            utilisateurService.changerMotDePasse(ancienMotDePasse, nouveauMotDePasse);
            return ResponseEntity.ok("Mot de passe changé avec succès");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(403).body("Mot de passe actuel incorrect"); // Code HTTP 403 pour interdiction
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Le nouveau mot de passe n'est pas assez robuste"); // Code HTTP 400 pour mauvaise requête
        }
    }


    @GetMapping("/info")
    public ResponseEntity<UtilisateurDto> afficherInformationsPersonnelles() {
        try {
            UtilisateurDto utilisateurInfo = utilisateurService.getUtilisateurInfo();
            return ResponseEntity.ok(utilisateurInfo);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
