package com.menu;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Gestionnaire des utilisateurs pour le jeu Slitherlink
 */
public class UserManager {
    private static final String USER_FILE = "slitherlink_users.txt";
    private static String currentUsername = null;
    
    /**
     * Vérifie si un utilisateur existe déjà
     * @param username Le pseudo à vérifier
     * @return true si l'utilisateur existe, false sinon
     */
    public static boolean userExists(String username) {
        try {
            File file = new File(USER_FILE);
            if (!file.exists()) {
                return false;
            }
            
            List<String> users = Files.readAllLines(Paths.get(USER_FILE));
            return users.stream().anyMatch(user -> user.equalsIgnoreCase(username));
        } catch (IOException e) {
            System.err.println("Erreur lors de la vérification du pseudo: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Enregistre un nouvel utilisateur
     * @param username Le pseudo à enregistrer
     * @return true si l'enregistrement a réussi, false sinon
     */
    public static boolean registerUser(String username) {
        try {
            FileWriter fileWriter = new FileWriter(USER_FILE, true); // Append mode
            BufferedWriter writer = new BufferedWriter(fileWriter);
            
            writer.write(username);
            writer.newLine();
            writer.close();
            
            currentUsername = username;
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de l'enregistrement du pseudo: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Définit l'utilisateur actuel
     * @param username Le pseudo de l'utilisateur actuel
     */
    public static void setCurrentUser(String username) {
        currentUsername = username;
    }
    
    /**
     * Retourne le pseudo de l'utilisateur actuel
     * @return Le pseudo de l'utilisateur actuel
     */
    public static String getCurrentUser() {
        return currentUsername;
    }
}