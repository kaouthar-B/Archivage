package com.PFA.Gestion_des_archives.Service;

import com.PFA.Gestion_des_archives.Dto.UtilisateurDto;
import com.PFA.Gestion_des_archives.Exception.UnauthorizedException;
import com.PFA.Gestion_des_archives.Model.*;
import com.PFA.Gestion_des_archives.Model.SessionContext;
import com.PFA.Gestion_des_archives.Model.Action;
import com.PFA.Gestion_des_archives.Model.Role;
import com.PFA.Gestion_des_archives.Model.Utilisateur;
import com.PFA.Gestion_des_archives.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private SessionContext sessionContext;
    @Autowired
    private HistoriqueActionService historiqueActionService;

    public List<Utilisateur> getAllUsers() {
        return utilisateurRepository.findAll();
    }

    public Optional<Utilisateur> getUserById(int id) {
        return utilisateurRepository.findById(id);
    }

    public Optional<Utilisateur> getUserByNom(String nomUtilisateur) {
        return utilisateurRepository.findByNom(nomUtilisateur);
    }


    // Vérifie si le mot de passe est robuste
    public boolean isPasswordStrong(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password.matches(passwordPattern);
    }


    public Utilisateur creerUtilisateur(String nom, String prenom, String email, String motDePasse, Role role) {
        if (!isPasswordStrong(motDePasse)) {
            throw new IllegalArgumentException("Le mot de passe n'est pas assez robuste.");
        }

        // Création de l'utilisateur si le mot de passe est robuste
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(motDePasse);
        utilisateur.setRole(role);
        Utilisateur nouvelUtilisateur = utilisateurRepository.save(utilisateur);

        // Ajouter une action à l'historique
        Utilisateur utilisateurConnecte = SessionContext.getCurrentUser();
        historiqueActionService.addHistoriqueActionForUtilisateur(Action.créationNouveauCompte, nouvelUtilisateur,
                "Création d'un nouveau compte pour " + nouvelUtilisateur.getNom());

        return nouvelUtilisateur;
    }


    public boolean authenticate(String email, String password) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email);

        if (utilisateur != null && utilisateur.getMotDePasse().equals(password)) {
            // Authentification réussie, définir l'utilisateur dans le contexte de session
            SessionContext.setCurrentUser(utilisateur);

            // Ajouter une action à l'historique
            historiqueActionService.addHistoriqueActionForUtilisateur(Action.connexion, utilisateur,
                    "Connexion réussie pour " + utilisateur.getNom());

            return true;
        }

        // Authentification échouée
        return false;
    }


    public void logout() {
        Utilisateur utilisateurConnecte = SessionContext.getCurrentUser();

        // Nettoyer l'utilisateur connecté du contexte
        SessionContext.clear();

        // Ajouter une action à l'historique
        if (utilisateurConnecte != null) {
            historiqueActionService.addHistoriqueActionForUtilisateur(Action.déconnection, utilisateurConnecte,
                    "Déconnexion de l'utilisateur " + utilisateurConnecte.getNom());
        }
    }




    // Méthode pour obtenir l'utilisateur actuel depuis le contexte de session
    private Utilisateur getUtilisateurActuel() {
        return SessionContext.getCurrentUser(); // Assurez-vous que SessionContext est correctement implémenté
    }

    // Méthode pour afficher tous les utilisateurs si l'utilisateur actuel est ADMINISTRATEUR
    public List<Utilisateur> afficherTousUtilisateurs() throws UnauthorizedException {
        Utilisateur utilisateurActuel = getUtilisateurActuel();

        // Vérifier que l'utilisateur actuel est ADMINISTRATEUR
        if (utilisateurActuel.getRole() == Role.ADMINISTRATEUR) {
            return utilisateurRepository.findAll(); // Retourne tous les utilisateurs
        } else {
            throw new UnauthorizedException("Vous n'avez pas l'autorisation d'afficher tous les utilisateurs.");
        }
    }


    public Utilisateur modifierUtilisateur(int idUtilisateur, String nouveauNom, String nouveauPrenom, String nouvelEmail, Role nouveauRole) throws UnauthorizedException {
        Utilisateur utilisateurCourant = SessionContext.getCurrentUser();

        // Vérifier si l'utilisateur courant est un administrateur
        if (utilisateurCourant.getRole() != Role.ADMINISTRATEUR) {
            throw new UnauthorizedException("Vous n'avez pas les droits nécessaires pour modifier un utilisateur.");
        }

        // Rechercher l'utilisateur à modifier
        Utilisateur utilisateurAModifier = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        // Mettre à jour les attributs
        utilisateurAModifier.setNom(nouveauNom);
        utilisateurAModifier.setPrenom(nouveauPrenom);
        utilisateurAModifier.setEmail(nouvelEmail);
        utilisateurAModifier.setRole(nouveauRole);

        // Sauvegarder les modifications
        Utilisateur utilisateurModifie = utilisateurRepository.save(utilisateurAModifier);

        // Ajouter une action à l'historique
        historiqueActionService.addHistoriqueActionForUtilisateur(Action.modification, utilisateurModifie,
                "Modification des informations de l'utilisateur " + utilisateurModifie.getNom());

        return utilisateurModifie;
    }






    // Méthode pour changer le mot de passe
    public Utilisateur changerMotDePasse(String ancienMotDePasse, String nouveauMotDePasse) throws UnauthorizedException {
        Utilisateur utilisateurCourant = SessionContext.getCurrentUser();

        // Vérifier si le mot de passe actuel est correct
        if (!utilisateurCourant.getMotDePasse().equals(ancienMotDePasse)) {
            throw new UnauthorizedException("L'ancien mot de passe est incorrect.");
        }

        // Vérifier si le nouveau mot de passe est robuste
        if (!isPasswordStrong(nouveauMotDePasse)) {
            throw new IllegalArgumentException("Le nouveau mot de passe n'est pas assez robuste.");
        }

        // Mettre à jour le mot de passe de l'utilisateur
        utilisateurCourant.setMotDePasse(nouveauMotDePasse);

        // Sauvegarder les modifications
        Utilisateur utilisateurModifie = utilisateurRepository.save(utilisateurCourant);

        // Ajouter une action à l'historique
        historiqueActionService.addHistoriqueActionForUtilisateur(Action.changerMotDePasse, utilisateurCourant,
                "Changement du mot de passe pour l'utilisateur " + utilisateurCourant.getNom());

        return utilisateurModifie;
    }

    public UtilisateurDto getUtilisateurInfo() throws UnauthorizedException {
        Utilisateur currentUser = SessionContext.getCurrentUser();

        if (currentUser == null) {
            throw new UnauthorizedException("Utilisateur non connecté.");
        }

        return new UtilisateurDto(currentUser.getNom(), currentUser.getPrenom(), currentUser.getEmail(), currentUser.getRole());
    }

}
