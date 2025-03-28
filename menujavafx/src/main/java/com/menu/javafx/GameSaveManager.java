package com.menu.javafx;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Gestionnaire des sauvegardes de parties de Slitherlink.
 * Permet de sauvegarder et charger l'état d'une grille pour chaque joueur.
 */
public class GameSaveManager {

    private static final String SAVE_DIRECTORY = "saves";
    private static final String USER_SAVES_FORMAT = SAVE_DIRECTORY + "/%s"; // %s = nom d'utilisateur

    /**
     * Vérifie si une sauvegarde existe pour l'ID de grille spécifié
     * 
     * @param gridId L'ID de la grille (au format "grid-XXX")
     * @return true si une sauvegarde existe, false sinon
     */
    public static boolean hasSavedGame(String gridId) {
        String username = UserManager.getCurrentUser();
        if (username == null || username.isEmpty()) {
            return false;
        }

        // Vérifier les sauvegardes manuelles
        File saveDir = new File(SAVE_DIRECTORY + File.separator + username);
        if (!saveDir.exists()) {
            return false;
        }

        // Vérifier les sauvegardes pour cette grille spécifique
        File[] saves = saveDir.listFiles((dir, name) -> name.contains(gridId) && name.endsWith(".json"));
        return saves != null && saves.length > 0;
    }

    /**
     * Sauvegarde l'état actuel du jeu pour l'utilisateur courant.
     * 
     * @param gridId       Identifiant de la grille (numéro dans le mode aventure)
     * @param seconds      Temps écoulé en secondes
     * @param checkCounter Nombre de vérifications restantes
     * @param saveAuto     True si sauvegarde automatique, false si manuelle
     * @return True si la sauvegarde a réussi, false sinon
     */
    public static boolean saveGame(String gridId, int seconds, int checkCounter, boolean saveAuto) {
        // 1. Vérifier si un utilisateur est connecté
        String username = UserManager.getCurrentUser();
        if (username == null || username.isEmpty()) {
            System.err.println("Impossible de sauvegarder sans utilisateur connecté");
            return false;
        }

        // 2. Vérifier si la grille est initialisée
        int[][][] gameState = GameScene.getSimplifiedGameMatrix();
        if (gameState == null || gameState.length == 0) {
            System.err.println("Impossible de sauvegarder: grille non initialisée");
            return false;
        }

        try {
            // 3. Créer les répertoires avec gestion d'erreur explicite
            if (!createSaveDirectoriesSafely(username)) {
                System.err.println("Impossible de créer les répertoires de sauvegarde");
                return false;
            }

            // 4. Créer l'objet JSON pour la sauvegarde
            JSONObject saveData = new JSONObject();

            // Informations de base
            saveData.put("username", username);
            String actualGridId = gridId;
            if (gridId.startsWith("grid-")) {
                actualGridId = gridId.substring(5); // Enlever "grid-"
            }
            saveData.put("gridId", actualGridId);
            saveData.put("timestamp", System.currentTimeMillis());
            saveData.put("elapsedTime", seconds);
            saveData.put("remainingChecks", checkCounter);
            saveData.put("autoSave", saveAuto);

            // Date formatée pour affichage
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            saveData.put("saveDate", sdf.format(new Date()));

            // 5. Conversion de l'état du jeu en JSON avec vérifications
            JSONArray gridStateArray = new JSONArray();

            for (int i = 0; i < gameState.length; i++) {
                JSONArray rowArray = new JSONArray();
                for (int j = 0; j < gameState[i].length; j++) {
                    JSONArray cellArray = new JSONArray();
                    for (int k = 0; k < gameState[i][j].length; k++) {
                        cellArray.add(gameState[i][j][k]);
                    }
                    rowArray.add(cellArray);
                }
                gridStateArray.add(rowArray);
            }

            saveData.put("gridState", gridStateArray);

            // 6. Générer un nom de fichier unique et utiliser File.separator pour
            // compatibilité
            String fileName;
            if (saveAuto) {
                fileName = "auto_" + gridId + ".json";
            } else {
                fileName = gridId + "_" + System.currentTimeMillis() + ".json";
            }

            // 7. Chemin complet du fichier avec File.separator pour compatibilité
            // cross-platform
            String userSaveDir = SAVE_DIRECTORY + File.separator + username;
            File saveFile = new File(userSaveDir, fileName);

            // 8. Écrire les données JSON dans le fichier avec vérification explicite
            try (FileWriter writer = new FileWriter(saveFile)) {
                writer.write(saveData.toJSONString());
                writer.flush(); // Assurer que les données sont bien écrites
            }

            // 9. Vérifier que le fichier a bien été créé
            if (!saveFile.exists() || saveFile.length() == 0) {
                System.err.println("Échec de création du fichier de sauvegarde");
                return false;
            }

            System.out.println("Partie sauvegardée dans: " + saveFile.getAbsolutePath());
            return true;

        } catch (IOException e) {
            System.err.println("Erreur I/O lors de la sauvegarde: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de la sauvegarde: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createSaveDirectoriesSafely(String username) {
        try {
            // Créer le répertoire principal des sauvegardes
            File mainDir = new File(SAVE_DIRECTORY);
            if (!mainDir.exists() && !mainDir.mkdir()) {
                System.err.println("Impossible de créer le répertoire: " + SAVE_DIRECTORY);
                return false;
            }

            // Créer le répertoire spécifique à l'utilisateur
            File userDir = new File(SAVE_DIRECTORY + File.separator + username);
            if (!userDir.exists() && !userDir.mkdir()) {
                System.err.println("Impossible de créer le répertoire: " + userDir.getPath());
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la création des répertoires: " + e.getMessage());
            return false;
        }
    }

    /**
     * Charge une sauvegarde et restaure l'état du jeu.
     * 
     * @param saveFilePath Chemin du fichier de sauvegarde
     * @param primaryStage Stage principal pour réafficher le jeu
     * @return True si le chargement a réussi, false sinon
     */
    public static boolean loadGame(String saveFilePath, Stage primaryStage) {
        try {
            System.out.println("Chargement de la sauvegarde: " + saveFilePath);

            JSONParser parser = new JSONParser();
            JSONObject saveData = (JSONObject) parser.parse(new FileReader(saveFilePath));

            // Extraire les informations de base
            String username = (String) saveData.get("username");
            String gridId = (String) saveData.get("gridId");
            long elapsedTime = (Long) saveData.get("elapsedTime");
            long remainingChecks = (Long) saveData.get("remainingChecks");

            // Debug logs for save data
            System.out.println("=== Debug: Game Loading Details ===");
            System.out.println("- Username: " + username);
            System.out.println("- Grid ID: " + gridId);
            System.out.println("- Elapsed Time: " + elapsedTime + " seconds");
            System.out.println("- Remaining Checks: " + remainingChecks);
            System.out.println("- Current user: " + UserManager.getCurrentUser());

            // Debug the grid state before parsing
            JSONArray gridStateArray = (JSONArray) saveData.get("gridState");
            System.out.println("- Grid state found: " + (gridStateArray != null));
            if (gridStateArray != null) {
                System.out.println("- Grid dimensions: " + gridStateArray.size() + " rows");
                if (gridStateArray.size() > 0) {
                    System.out.println("- First row size: " + ((JSONArray) gridStateArray.get(0)).size() + " columns");
                    if (((JSONArray) gridStateArray.get(0)).size() > 0) {
                        System.out.println(
                                "- Cell depth: " + ((JSONArray) ((JSONArray) gridStateArray.get(0)).get(0)).size());
                    }
                }
            }

            System.out.println("Sauvegarde trouvée - Grille: " + gridId + ", Temps: " + elapsedTime + "s");

            // Vérifier si l'utilisateur actuel correspond à la sauvegarde
            if (!username.equals(UserManager.getCurrentUser())) {
                System.err.println("Cette sauvegarde appartient à un autre utilisateur");
                return false;
            }

            System.out.println("Chargement avec l'ID de grille: " + gridId);

            // Assurez-vous que le gridId est au format attendu
            if (!gridId.startsWith("grid-")) {
                gridId = "grid-" + gridId;
            }

            // Charger l'état de la grille
            int[][][] gridState = parseGridState(gridStateArray);

            // Utiliser SaveGameLoader pour charger la partie sauvegardée
            SaveGameLoader.loadFromSave(primaryStage, gridId, (int) elapsedTime, (int) remainingChecks, gridState);

            return true;

        } catch (IOException | ParseException e) {
            System.err.println("Erreur lors du chargement: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
 * Charge une sauvegarde et lance le jeu avec cet état
 * 
 * @param primaryStage Le stage principal
 * @param gridId L'ID de la grille à charger (format "grid-XXX")
 */
public static void loadGame(Stage primaryStage, String gridId) {
    String username = UserManager.getCurrentUser();
    if (username == null || username.isEmpty()) {
        // Aucun utilisateur connecté, lancer un nouveau jeu
        String actualGridId = gridId.startsWith("grid-") ? gridId.substring(5) : gridId;
        GameScene.show(primaryStage, actualGridId);
        return;
    }
    
    try {
        // Trouver le répertoire des sauvegardes de l'utilisateur
        File saveDir = new File(SAVE_DIRECTORY + File.separator + username);
        if (!saveDir.exists() || !saveDir.isDirectory()) {
            // Répertoire de sauvegarde introuvable, lancer un nouveau jeu
            String actualGridId = gridId.startsWith("grid-") ? gridId.substring(5) : gridId;
            GameScene.show(primaryStage, actualGridId);
            return;
        }
        
        // Trouver toutes les sauvegardes pour cette grille
        final String gridIdSearch = gridId; // Version finale pour le lambda
        File[] saveFiles = saveDir.listFiles((dir, name) -> name.contains(gridIdSearch) && name.endsWith(".json"));
        
        if (saveFiles == null || saveFiles.length == 0) {
            // Aucune sauvegarde trouvée, lancer un nouveau jeu
            String actualGridId = gridId.startsWith("grid-") ? gridId.substring(5) : gridId;
            GameScene.show(primaryStage, actualGridId);
            return;
        }
        
        // Trier les sauvegardes par date de dernière modification (la plus récente en premier)
        java.util.Arrays.sort(saveFiles, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
        
        // Charger la sauvegarde la plus récente
        File mostRecentSave = saveFiles[0];
        System.out.println("Chargement de la sauvegarde: " + mostRecentSave.getAbsolutePath());
        
        // Utiliser la méthode existante pour charger le fichier de sauvegarde
        boolean loadSuccess = loadGame(mostRecentSave.getAbsolutePath(), primaryStage);
        
        if (!loadSuccess) {
            // Échec du chargement, lancer un nouveau jeu
            System.err.println("Échec du chargement de la sauvegarde, lancement d'un nouveau jeu");
            String actualGridId = gridId.startsWith("grid-") ? gridId.substring(5) : gridId;
            GameScene.show(primaryStage, actualGridId);
        }
        
    } catch (Exception e) {
        System.err.println("Erreur lors de la recherche de sauvegardes: " + e.getMessage());
        e.printStackTrace();
        
        // En cas d'erreur, lancer un nouveau jeu
        String actualGridId = gridId.startsWith("grid-") ? gridId.substring(5) : gridId;
        GameScene.show(primaryStage, actualGridId);
    }
}

    /**
     * Liste toutes les sauvegardes disponibles pour l'utilisateur actuel.
     * 
     * @return Liste des métadonnées de sauvegarde
     */
    public static List<SaveMetadata> listSaves() {
        String username = UserManager.getCurrentUser();
        if (username == null || username.isEmpty()) {
            return new ArrayList<>();
        }

        List<SaveMetadata> saveList = new ArrayList<>();
        String userSaveDir = String.format(USER_SAVES_FORMAT, username);
        File saveFolder = new File(userSaveDir);

        if (!saveFolder.exists() || !saveFolder.isDirectory()) {
            return saveList;
        }

        File[] saveFiles = saveFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (saveFiles == null) {
            return saveList;
        }

        for (File saveFile : saveFiles) {
            try {
                JSONParser parser = new JSONParser();
                JSONObject saveData = (JSONObject) parser.parse(new FileReader(saveFile));

                SaveMetadata metadata = new SaveMetadata();
                metadata.setFilePath(saveFile.getAbsolutePath());
                metadata.setGridId((String) saveData.get("gridId"));
                metadata.setTimestamp((Long) saveData.get("timestamp"));
                metadata.setElapsedTime((Long) saveData.get("elapsedTime"));
                metadata.setSaveDate((String) saveData.get("saveDate"));
                metadata.setAutoSave(saveData.containsKey("autoSave") ? (Boolean) saveData.get("autoSave") : false);

                saveList.add(metadata);

            } catch (IOException | ParseException e) {
                System.err
                        .println("Erreur lors de la lecture du fichier " + saveFile.getName() + ": " + e.getMessage());
            }
        }

        // Trier les sauvegardes par date (plus récentes en premier)
        saveList.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));

        return saveList;
    }

    /**
     * Crée les répertoires nécessaires pour les sauvegardes.
     */
    private static void createSaveDirectories(String username) {
        try {
            // Créer le répertoire principal des sauvegardes
            Path mainDir = Paths.get(SAVE_DIRECTORY);
            if (!Files.exists(mainDir)) {
                Files.createDirectory(mainDir);
            }

            // Créer le répertoire spécifique à l'utilisateur
            Path userDir = Paths.get(String.format(USER_SAVES_FORMAT, username));
            if (!Files.exists(userDir)) {
                Files.createDirectory(userDir);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la création des répertoires de sauvegarde: " + e.getMessage());
        }
    }

    /**
     * Supprime une sauvegarde.
     * 
     * @param filePath Chemin du fichier de sauvegarde
     * @return True si la suppression a réussi, false sinon
     */
    public static boolean deleteSave(String filePath) {
        try {
            File saveFile = new File(filePath);

            // Vérifier que le fichier appartient bien à l'utilisateur actuel
            String username = UserManager.getCurrentUser();
            if (username == null || !saveFile.getAbsolutePath().contains(username)) {
                return false;
            }

            return saveFile.delete();
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
            return false;
        }
    }

    /**
     * Parse l'état de la grille à partir des données JSON.
     */
    private static int[][][] parseGridState(JSONArray gridStateArray) {
        int rows = gridStateArray.size();
        int cols = ((JSONArray) gridStateArray.get(0)).size();
        int depth = ((JSONArray) ((JSONArray) gridStateArray.get(0)).get(0)).size();

        int[][][] gridState = new int[rows][cols][depth];

        for (int i = 0; i < rows; i++) {
            JSONArray rowArray = (JSONArray) gridStateArray.get(i);
            for (int j = 0; j < cols; j++) {
                JSONArray cellArray = (JSONArray) rowArray.get(j);
                for (int k = 0; k < depth; k++) {
                    gridState[i][j][k] = ((Long) cellArray.get(k)).intValue();
                }
            }
        }

        return gridState;
    }

    /**
     * Affiche une notification de sauvegarde réussie.
     * 
     * @param pane Panneau où afficher la notification
     */
    public static void showSaveNotification(Pane pane) {
        Platform.runLater(() -> {
            Label saveLabel = new Label("Partie sauvegardée!");
            saveLabel.setTextFill(Color.WHITE);
            saveLabel.setStyle("-fx-background-color: #3A7D44;" +
                    "-fx-padding: 10 20;" +
                    "-fx-background-radius: 5;");
            saveLabel.setOpacity(0);

            // Positionner en haut à droite
            saveLabel.setLayoutX(pane.getWidth() - 200);
            saveLabel.setLayoutY(20);

            pane.getChildren().add(saveLabel);

            // Animation d'apparition/disparition
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), saveLabel);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), saveLabel);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setDelay(Duration.seconds(2));

            fadeIn.setOnFinished(e -> fadeOut.play());
            fadeOut.setOnFinished(e -> pane.getChildren().remove(saveLabel));

            fadeIn.play();
        });
    }

    /**
     * Classe pour stocker les métadonnées d'une sauvegarde.
     */
    public static class SaveMetadata {
        private String filePath;
        private String gridId;
        private long timestamp;
        private long elapsedTime;
        private String saveDate;
        private boolean isAutoSave;

        // Getters et setters
        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getGridId() {
            return gridId;
        }

        public void setGridId(String gridId) {
            this.gridId = gridId;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public long getElapsedTime() {
            return elapsedTime;
        }

        public void setElapsedTime(long elapsedTime) {
            this.elapsedTime = elapsedTime;
        }

        public String getSaveDate() {
            return saveDate;
        }

        public void setSaveDate(String saveDate) {
            this.saveDate = saveDate;
        }

        public boolean isAutoSave() {
            return isAutoSave;
        }

        public void setAutoSave(boolean autoSave) {
            isAutoSave = autoSave;
        }

        public String getFormattedTime() {
            int minutes = (int) (elapsedTime / 60);
            int seconds = (int) (elapsedTime % 60);
            return String.format("%02d:%02d", minutes, seconds);
        }

        @Override
        public String toString() {
            return "Grille " + gridId + " - " + saveDate +
                    " (" + getFormattedTime() + ")" +
                    (isAutoSave ? " [Auto]" : "");
        }
    }
}