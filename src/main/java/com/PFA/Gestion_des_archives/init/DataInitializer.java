package com.PFA.Gestion_des_archives.init;

import com.PFA.Gestion_des_archives.Model.Role;
import com.PFA.Gestion_des_archives.Model.Utilisateur;
import com.PFA.Gestion_des_archives.Service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UtilisateurService utilisateurService;

    @Override
    public void run(String... args) throws Exception {
        // Vérifier si l'utilisateur existe déjà
        Optional<Utilisateur> existingAdmin = utilisateurService.getUserByNom("Admin");
        if (existingAdmin.isEmpty()) {
            Utilisateur admin = new Utilisateur();
            admin.setNom("Admin");
            admin.setPrenom("Admin");
            admin.setEmail("bencheikhk178@gmail.com");
            admin.setMotDePasse("Admin1234@");
            admin.setRole(Role.ADMINISTRATEUR);

            utilisateurService.creerUtilisateur(admin.getNom(), admin.getPrenom(), admin.getEmail(), admin.getMotDePasse(), admin.getRole());

            System.out.println("Compte administrateur créé avec succès !");
        } else {
            System.out.println("Le compte administrateur existe déjà.");
        }
    }

}
