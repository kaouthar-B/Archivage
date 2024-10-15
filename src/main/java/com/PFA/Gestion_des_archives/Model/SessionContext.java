package com.PFA.Gestion_des_archives.Model;

import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Component
public class SessionContext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use the appropriate strategy
    private Long id;
    private static ThreadLocal<Utilisateur> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(Utilisateur utilisateur) {
        currentUser.set(utilisateur);
    }

    public static Utilisateur getCurrentUser() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}

