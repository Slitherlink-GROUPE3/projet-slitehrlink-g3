package com.menu.javafx;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

// Imports manquants
import com.menu.javafx.Menu;
import com.menu.javafx.UserManager;

/**
 * Classe responsable du chargement et de l'application des sauvegardes de jeu
 * Travaille en collaboration avec GameSaveManager
 */
public class SaveGameLoader {

    // Constantes représentant les états possibles des segments
    public static final int EMPTY = 0;
    public static final int LINE = 1;
    public static final int CROSS = 2;
    public static final int HYPOTHESIS = 3;

    private static int[][][] savedGridState;
    private static int savedElapsedTime;

    /**
     * Charge une partie à partir d'une sauvegarde.
     * 
     * @param primaryStage Le stage principal
     * @param gridId       Identifiant de la grille
     * @param elapsedTime  Temps écoulé en secondes
     * @param gridState    État de la grille
     */
    public static void loadFromSave(Stage primaryStage, String gridId, int elapsedTime,
            int[][][] gridState) {
        // Sauvegarder les données de la partie
        savedGridState = gridState;
        //System.out.println("État chargé depuis la sauvegarde: " + (savedGridState != null ? savedGridState.length + "x" + savedGridState[0].length + "x" + savedGridState[0][0].length : "null"));

        // Extraire l'ID réel de la grille (enlever le préfixe "grid-" s'il existe)
        String actualGridId = gridId;
        if (gridId.startsWith("grid-")) {
            actualGridId = gridId.substring(5); // Enlever "grid-"
        }
    
        // Initialiser le compteur et le temps
        savedElapsedTime = elapsedTime;

        // Afficher la scène de jeu avec les données chargées
        //System.out.println("Affichage de la scène de jeu avec la sauvegarde...");
        // Log gridState details
        //System.out.println("Loading grid state with dimensions: " + gridState.length + "x" + gridState[0].length + "x" + gridState[0][0].length);
                         
        // Count different types of elements
        int lines = 0, crosses = 0, hypotheses = 0;
        for (int i = 0; i < gridState.length; i++) {
            for (int j = 0; j < gridState[i].length; j++) {
                for (int k = 1; k < gridState[i][j].length; k++) {
                    switch (gridState[i][j][k]) {
                        case LINE: lines++; break;
                        case CROSS: crosses++; break;
                        case HYPOTHESIS: hypotheses++; break;
                    }
                }
            }
        }
        //System.out.println("Grid contains: " + lines + " lines, " + crosses + " crosses, " + hypotheses + " hypotheses");
        GameScene.showWithSavedState(primaryStage, actualGridId, gridState, elapsedTime);
    }

    /**
 * Applique un état spécifique à un segment de ligne
 * 
 * @param line        La ligne à modifier
 * @param state       L'état à appliquer (EMPTY, LINE, CROSS ou HYPOTHESIS)
 * @param slitherGrid La grille sur laquelle appliquer l'état
 */
public static void applySegmentState(Line line, int state, SlitherGrid slitherGrid) {
    try {
        // Ajouter un log détaillé pour le débogage
        //System.out.println("Application de l'état " + state + " à la ligne: " + line.getId());
        
        // D'abord supprimer les croix si elles existent
        slitherGrid.getSlitherlinkGrid().getChildren()
                .removeIf(node -> node instanceof Line && node.getUserData() == line);

        switch (state) {
            case EMPTY:
                // Mettre la ligne à l'état initial (transparent)
                line.setStroke(Color.TRANSPARENT);
                line.setOpacity(0.0);
                break;
            case LINE:
                // CORRECTION ICI: Utiliser directement toggleLine si la ligne n'est pas active
                if (!slitherGrid.isLineActive(line)) {
                    slitherGrid.toggleLine(line);
                } else {
                    // Forcer la mise à jour si la ligne est déjà active
                    line.setStroke(Color.web(SlitherGrid.DARK_COLOR));
                    line.setOpacity(1.0);
                    line.setStrokeWidth(3.0);
                }
                // Vérifier après l'application
                //System.out.println(" -> Ligne activée: " + slitherGrid.isLineActive(line));
                break;
            case CROSS:
                // Rendre la ligne transparente et ajouter une croix
                line.setStroke(Color.TRANSPARENT);
                line.setOpacity(0.0);
                // Créer la croix en utilisant votre helper
                CrossMoveHelper.createCrossForSave(line, false, slitherGrid);
                break;
            case HYPOTHESIS:
                // Pour les hypothèses, utiliser la couleur claire
                line.setStroke(Color.web(SlitherGrid.LIGHT_COLOR));
                line.setOpacity(1.0);
                line.setStrokeWidth(3.0);
                break;
            default:
                System.err.println("État de segment inconnu: " + state);
                break;
        }
        
        // Forcer une mise à jour du rendu
        line.setVisible(true);
        
    } catch (Exception e) {
        System.err.println("Erreur lors de l'application de l'état " + state + " à une ligne: " + e.getMessage());
        e.printStackTrace();
    }
}

            /**
     * Applique un état sauvegardé à la grille actuelle.
     * 
     * @param state État de la grille à appliquer (tableau 3D avec l'état de chaque segment)
     */
    public static void applyGridState(int[][][] state) {
        SlitherGrid slitherGrid = GameScene.getSlitherGrid();
        int gridRows = slitherGrid.getGridRows();
        int gridCols = slitherGrid.getGridCols();
        Map<String, Line> gridLines = slitherGrid.getGridLines();
        // Vérifier les dimensions
        if (state.length != gridRows || state[0].length != gridCols) {
            System.err.println("Erreur: dimensions de l'état (" + state.length + 
                             "x" + state[0].length + ") ne correspondent pas à la grille (" + 
                             gridRows + "x" + gridCols + ")");
            return;
        }
        
        int foundLines = 0;
        int nonEmptySegments = 0;
        int totalLines = 0;
        
        // Parcourir chaque cellule de la grille
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridCols; j++) {
                totalLines += 4; // 4 segments par cellule
                
                // Compter les segments non vides dans l'état
                if (state[i][j][1] != 0) nonEmptySegments++;
                if (state[i][j][2] != 0) nonEmptySegments++;
                if (state[i][j][3] != 0) nonEmptySegments++;
                if (state[i][j][4] != 0) nonEmptySegments++;
                
                // Segment du haut
                String topLineKey = "H_" + i + "_" + j;
                Line topLine = gridLines.get(topLineKey);
                if (topLine != null) {
                    /*if (state[i][j][1] != 0) {
                        System.out.println("Segment haut trouvé: [" + i + "][" + j + "][1] = " + state[i][j][1]);
                    }*/
                    applySegmentState(topLine, state[i][j][1], slitherGrid);
                    foundLines++;
                }
                
                // Segment de droite
                String rightLineKey = "V_" + i + "_" + (j + 1);
                Line rightLine = gridLines.get(rightLineKey);
                if (rightLine != null) {
                    /*if (state[i][j][2] != 0) {
                        System.out.println("Segment droit trouvé: [" + i + "][" + j + "][2] = " + state[i][j][2]);
                    }*/
                    applySegmentState(rightLine, state[i][j][2], slitherGrid);
                    foundLines++;
                }
                
                // Segment du bas
                String bottomLineKey = "H_" + (i + 1) + "_" + j;
                Line bottomLine = gridLines.get(bottomLineKey);
                if (bottomLine != null) {
                    /*if (state[i][j][3] != 0) {
                        System.out.println("Segment bas trouvé: [" + i + "][" + j + "][3] = " + state[i][j][3]);
                    }*/
                    applySegmentState(bottomLine, state[i][j][3], slitherGrid);
                    foundLines++;
                }
                
                // Segment de gauche
                String leftLineKey = "V_" + i + "_" + j;
                Line leftLine = gridLines.get(leftLineKey);
                if (leftLine != null) {
                    /*if (state[i][j][4] != 0) {
                        System.out.println("Segment gauche trouvé: [" + i + "][" + j + "][4] = " + state[i][j][4]);
                    }*/
                    applySegmentState(leftLine, state[i][j][4], slitherGrid);
                    foundLines++;
                }
            }
        }
        
        //System.out.println("Application de l'état terminée: " + foundLines + " segments trouvés sur " + totalLines);
        //System.out.println("Nombre de segments non vides dans l'état: " + nonEmptySegments);
        
        // Mettre à jour la matrice de jeu après avoir appliqué tous les états
        slitherGrid.getGameMatrix();
    }

    /**
     * Vérifie si l'utilisateur actuel a des sauvegardes et propose de les charger
     * 
     * @param primaryStage Le stage principal
     * @return true si une sauvegarde a été chargée, false sinon
     */
    public static boolean loadUserSave(Stage primaryStage) {
        // Définir les couleurs localement pour éviter les dépendances sur SlitherGrid
        final String MAIN_COLOR = "#3A7D44"; // Vert principal
        final String SECONDARY_COLOR = "#F2E8CF"; // Beige clair
        final String ACCENT_COLOR = "#BC4749"; // Rouge-brique
        final String DARK_COLOR = "#386641"; // Vert foncé

        System.out.println("=== VÉRIFICATION DES SAUVEGARDES ===");
        String username = UserManager.getCurrentUser();
        System.out.println("Utilisateur connecté: " + username);

        if (username == null || username.isEmpty()) {
            System.out.println("Aucun utilisateur connecté, impossible de charger une sauvegarde");
            return false;
        }

        // Récupérer la liste des sauvegardes de l'utilisateur
        List<GameSaveManager.SaveMetadata> saves = GameSaveManager.listSaves();
        System.out.println("Nombre de sauvegardes trouvées: " + saves.size());

        if (saves.isEmpty()) {
            System.out.println("Aucune sauvegarde trouvée pour " + username);
            return false;
        }

        // Trier les sauvegardes par date (la plus récente en premier)
        GameSaveManager.SaveMetadata lastSave = saves.get(0);
        System.out.println("Sauvegarde la plus récente: " + lastSave.getGridId() + " du " + lastSave.getSaveDate());

        final boolean[] saveWasLoaded = {false};

            java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);


        // Exécuter sur le thread JavaFX
        Platform.runLater(() -> {
            // Créer une boîte de dialogue pour demander à l'utilisateur
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Partie sauvegardée");

            // Titre
            Label title = new Label("Partie sauvegardée trouvée !");
            title.setFont(Font.font("Montserrat", FontWeight.BOLD, 22));
            title.setTextFill(Color.web(MAIN_COLOR));

            // Détails de la sauvegarde
            VBox detailsBox = new VBox(8);
            detailsBox.setPadding(new Insets(15));
            detailsBox.setStyle("-fx-background-color: #f8f8f8; -fx-background-radius: 5;");

            Label gridLabel = new Label("Grille: " + lastSave.getGridId());
            Label timeLabel = new Label("Temps: " + lastSave.getFormattedTime());
            Label dateLabel = new Label("Date: " + lastSave.getSaveDate());

            detailsBox.getChildren().addAll(gridLabel, timeLabel, dateLabel);

            // Création des boutons localement sans dépendance externe
            Button loadButton = new Button("Charger la partie");
            loadButton.setFont(Font.font("Montserrat", FontWeight.BOLD, 14));
            loadButton.setStyle("-fx-background-color: " + MAIN_COLOR + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-background-radius: 5;" +
                    "-fx-padding: 10 20;" +
                    "-fx-cursor: hand;");

            Button newGameButton = new Button("Nouvelle partie");
            newGameButton.setFont(Font.font("Montserrat", FontWeight.BOLD, 14));
            newGameButton.setStyle("-fx-background-color: transparent;" +
                    "-fx-text-fill: " + MAIN_COLOR + ";" +
                    "-fx-border-color: " + MAIN_COLOR + ";" +
                    "-fx-border-radius: 5;" +
                    "-fx-background-radius: 5;" +
                    "-fx-padding: 10 20;" +
                    "-fx-cursor: hand;");

            HBox buttonBox = new HBox(20, newGameButton, loadButton);
            buttonBox.setAlignment(Pos.CENTER);

            // Conteneur principal
            VBox root = new VBox(20);
            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(30));
            root.getChildren().addAll(title, detailsBox, buttonBox);
            root.setStyle("-fx-background-color: white;");

            // Actions des boutons avec animation simple intégrée
            loadButton.setOnAction(e -> {
                // Animation simple du bouton
                loadButton.setScaleX(0.95);
                loadButton.setScaleY(0.95);

                // Restaurer après animation
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                        javafx.util.Duration.millis(100));
                pause.setOnFinished(event -> {
                    loadButton.setScaleX(1);
                    loadButton.setScaleY(1);

                    // Fermer la dialogue
                    dialog.close();

                    // Charger la sauvegarde
                    GameSaveManager.loadGame(lastSave.getFilePath(), primaryStage);
                });
                pause.play();
            });

            newGameButton.setOnAction(e -> {
                // Animation simple du bouton
                newGameButton.setScaleX(0.95);
                newGameButton.setScaleY(0.95);
            
                // Restaurer après animation
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                        javafx.util.Duration.millis(100));
                pause.setOnFinished(event -> {
                    newGameButton.setScaleX(1);
                    newGameButton.setScaleY(1);
            
                    // Récupérer le nom d'utilisateur actuel
                    String currentUser = UserManager.getCurrentUser();
                    
                    // Supprimer toutes les sauvegardes de l'utilisateur
                    List<GameSaveManager.SaveMetadata> saves3 = GameSaveManager.listSaves();
                    for (GameSaveManager.SaveMetadata save : saves3) {
                        GameSaveManager.deleteSave(save.getFilePath());
                        System.out.println("Suppression de la sauvegarde: " + save.getFilePath());
                    }
                    
                    // Réinitialiser la progression de l'utilisateur
                    UserManager.resetUserProgress(currentUser);
                    System.out.println("Progression réinitialisée pour l'utilisateur: " + currentUser);
                    
                    // Afficher un message de confirmation
                    System.out.println("Nouvelle partie initialisée avec succès");
            
                    // Fermer la dialogue
                    dialog.close();
            
                    // Afficher le menu principal
                    com.menu.javafx.Menu.show(primaryStage);
                });
                pause.play();
            });

            Scene scene = new Scene(root, 400, 300);
            dialog.setScene(scene);
            dialog.show();
        });

        return false;
    }
}