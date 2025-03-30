package com.menu.javafx;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Gestionnaire des utilisateurs pour le jeu Slitherlink
 */
public class UserManager {
    private static final String USER_DIRECTORY = "users";
    private static final String PROGRESS_FILE = "progression.json";
    private static final String SAVES_DIRECTORY = "saves";
    private static String currentUsername = null;
    
    /**
     * Vérifie si un utilisateur existe déjà
     * @param username Le pseudo à vérifier
     * @return true si l'utilisateur existe, false sinon
     */
    public static boolean userExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Vérifier si le dossier utilisateur existe avec le fichier de progression
            File userDir = new File(USER_DIRECTORY, username);
            File progressFile = new File(userDir, PROGRESS_FILE);
            return userDir.exists() && progressFile.exists();
        } catch (Exception e) {
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
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Créer le dossier principal users s'il n'existe pas
            File baseDir = new File(USER_DIRECTORY);
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }
            
            // Créer le dossier spécifique à l'utilisateur
            File userDir = new File(USER_DIRECTORY, username);
            if (!userDir.exists()) {
                userDir.mkdirs();
            }
            
            // Créer le dossier de sauvegardes pour cet utilisateur
            File savesDir = new File(userDir, SAVES_DIRECTORY);
            if (!savesDir.exists()) {
                savesDir.mkdirs();
            }
            
            // Créer le fichier JSON pour cet utilisateur
            File progressFile = new File(userDir, PROGRESS_FILE);
            
            // Créer l'objet JSON avec les données initiales
            JSONObject userData = new JSONObject();
            userData.put("username", username);
            
            // Ajouter les informations des grilles avec le bon format (001, 002, etc.)
            JSONObject grids = new JSONObject();
            
            // Initialiser les 15 premiers niveaux
            for (int i = 1; i <= 15; i++) {
                String gridId = String.format("%03d", i);
                JSONObject grid = new JSONObject();
                grid.put("completed", false);
                grids.put(gridId, grid);
            }
            
            userData.put("grids", grids);
            
            // Écrire dans le fichier
            try (FileWriter fileWriter = new FileWriter(progressFile)) {
                fileWriter.write(userData.toJSONString());
            }
            
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

    /**
     * Crée le dossier utilisateur s'il n'existe pas
     */
    public static void createUserFile() {
        try {
            Path userDirectory = Paths.get(USER_DIRECTORY);
            if (!Files.exists(userDirectory)) {
                Files.createDirectory(userDirectory);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la création du dossier utilisateur: " + e.getMessage());
        }
    }

    /**
     * Marque une grille comme complétée pour l'utilisateur actuel
     * @param gridId L'ID de la grille (format numérique, comme 1, 2, etc.)
     */
    public static void setGridCompleted(int gridId) {
        if (currentUsername == null) {
            return;
        }
        
        try {
            // Formater l'ID de grille (001, 002, etc.)
            String formattedGridId = String.format("%03d", gridId);
            
            File userDir = new File(USER_DIRECTORY, currentUsername);
            File progressFile = new File(userDir, PROGRESS_FILE);
            
            if (!progressFile.exists()) {
                return;
            }
            
            JSONParser parser = new JSONParser();
            JSONObject userData = (JSONObject) parser.parse(new FileReader(progressFile));
            
            JSONObject grids = (JSONObject) userData.get("grids");
            JSONObject grid = (JSONObject) grids.get(formattedGridId);
            
            if (grid == null) {
                grid = new JSONObject();
                grids.put(formattedGridId, grid);
            }
            
            grid.put("completed", true);
            
            // Écrire dans le fichier
            try (FileWriter fileWriter = new FileWriter(progressFile)) {
                fileWriter.write(userData.toJSONString());
            }
            
        } catch (IOException | ParseException e) {
            System.err.println("Erreur lors de la mise à jour de la grille: " + e.getMessage());
        }
    }
    
    /**
     * Marque une grille comme complétée pour l'utilisateur actuel
     * @param gridId L'ID de la grille (format chaîne, comme "001", "grid-001")
     */
    public static void setGridCompleted(String gridId) {
        if (currentUsername == null || gridId == null) {
            return;
        }
        
        try {
            // Extraire l'ID numérique de la chaîne
            String formattedGridId = gridId;
            if (gridId.startsWith("grid-")) {
                formattedGridId = gridId.substring(5);
            }
            
            File userDir = new File(USER_DIRECTORY, currentUsername);
            File progressFile = new File(userDir, PROGRESS_FILE);
            
            if (!progressFile.exists()) {
                return;
            }
            
            JSONParser parser = new JSONParser();
            JSONObject userData = (JSONObject) parser.parse(new FileReader(progressFile));
            
            JSONObject grids = (JSONObject) userData.get("grids");
            JSONObject grid = (JSONObject) grids.get(formattedGridId);
            
            if (grid == null) {
                grid = new JSONObject();
                grids.put(formattedGridId, grid);
            }
            
            grid.put("completed", true);
            
            // Écrire dans le fichier
            try (FileWriter fileWriter = new FileWriter(progressFile)) {
                fileWriter.write(userData.toJSONString());
            }
            
        } catch (IOException | ParseException e) {
            System.err.println("Erreur lors de la mise à jour de la grille: " + e.getMessage());
        }
    }
    
    /**
     * Vérifie si une grille est complétée pour l'utilisateur actuel
     * @param gridId L'ID de la grille (format numérique)
     * @return true si la grille est complétée, false sinon
     */
    public static boolean isGridCompleted(int gridId) {
        // Formater l'ID de grille (001, 002, etc.)
        String formattedGridId = String.format("%03d", gridId);
        return isGridCompleted(formattedGridId);
    }
    
    /**
     * Vérifie si une grille est complétée pour l'utilisateur actuel
     * @param gridId L'ID de la grille (format chaîne, comme "001", "grid-001")
     * @return true si la grille est complétée, false sinon
     */
    public static boolean isGridCompleted(String gridId) {
        if (currentUsername == null || gridId == null) {
            return false;
        }
        
        try {
            // Extraire l'ID numérique de la chaîne
            String formattedGridId = gridId;
            if (gridId.startsWith("grid-")) {
                formattedGridId = gridId.substring(5);
            }
            
            File userDir = new File(USER_DIRECTORY, currentUsername);
            File progressFile = new File(userDir, PROGRESS_FILE);
            
            if (!progressFile.exists()) {
                return false;
            }
            
            JSONParser parser = new JSONParser();
            JSONObject userData = (JSONObject) parser.parse(new FileReader(progressFile));
            
            JSONObject grids = (JSONObject) userData.get("grids");
            JSONObject grid = (JSONObject) grids.get(formattedGridId);
            
            if (grid == null) {
                return false;
            }
            
            return (Boolean) grid.get("completed");
            
        } catch (IOException | ParseException e) {
            System.err.println("Erreur lors de la vérification de la grille: " + e.getMessage());
            return false;
        }
    }
}