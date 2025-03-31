package com.menu.javafx;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.util.Duration;
import com.tpgr3.Techniques.Techniques;
import com.menu.javafx.TechniqueDescriptions;
import com.menu.javafx.AutoCrossButton;
import javafx.scene.effect.DropShadow;
import javafx.geometry.Insets;
import java.util.Map;
import java.lang.reflect.Constructor;
import javafx.animation.PauseTransition;

public class GameScene {
    // Add at the top of the GameScene class, replacing your current color
    // constants:

    // Light theme constants
    private static final String LIGHT_MAIN_COLOR = "#3A7D44"; // Vert principal
    private static final String LIGHT_SECONDARY_COLOR = "#F2E8CF"; // Beige clair
    private static final String LIGHT_ACCENT_COLOR = "#BC4749"; // Rouge-brique
    public static final String LIGHT_DARK_COLOR = "#386641"; // Vert foncé
    private static final String LIGHT_LIGHT_COLOR = "#A7C957"; // Vert clair

    // Dark theme constants
    private static final String DARK_MAIN_COLOR = "#4c8b5d"; // Vert principal (plus lumineux)
    private static final String DARK_SECONDARY_COLOR = "#1a1a1a"; // Gris très foncé
    private static final String DARK_ACCENT_COLOR = "#e05d5f"; // Rouge-brique (plus lumineux)
    public static final String DARK_DARK_COLOR = "#2d3b2d"; // Vert très foncé
    private static final String DARK_LIGHT_COLOR = "#6a8844";

    private static double CELL_SIZE;
    private static StackPane gridContainer;
    private static HBox root;
    private static VBox mainLayer;

    private static String gridId = "001"; // Valeur par défaut

    // Variables pour gérer les états sauvegardés
    private static int[][][] savedGridState = null;
    private static int savedElapsedTime = 0;
    private static SlitherGrid slitherGrid;
    private static GameMatrix gameMatrix;
    private static int checkCounter;
    private static int techniqueCounter = 3; // Compteur de techniques limité à 3
    private static Text techniqueCountDisplay;

    private static java.util.Timer gameTimer;

    private static String currentGridId = "001"; // Store the current grid ID for saving/loading

    // Add a method to apply the current theme:
    private static void applyTheme() {
        boolean isDarkMode = SettingScene.isDarkModeEnabled();
        if (isDarkMode) {
            SlitherGrid.MAIN_COLOR = DARK_MAIN_COLOR;
            SlitherGrid.SECONDARY_COLOR = DARK_SECONDARY_COLOR;
            SlitherGrid.ACCENT_COLOR = DARK_ACCENT_COLOR;
            SlitherGrid.DARK_COLOR = DARK_DARK_COLOR;
            SlitherGrid.LIGHT_COLOR = DARK_LIGHT_COLOR;
        } else {
            SlitherGrid.MAIN_COLOR = LIGHT_MAIN_COLOR;
            SlitherGrid.SECONDARY_COLOR = LIGHT_SECONDARY_COLOR;
            SlitherGrid.ACCENT_COLOR = LIGHT_ACCENT_COLOR;
            SlitherGrid.DARK_COLOR = LIGHT_DARK_COLOR;
            SlitherGrid.LIGHT_COLOR = LIGHT_LIGHT_COLOR;
        }
    }

    // Méthode pour charger la grille depuis un fichier JSON
    public static int[][] loadGridFromJson(String filePath) {
        JSONParser parser = new JSONParser();
        int maxRow = 0;
        int maxCol = 0;

        try (InputStream inputStream = GameScene.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + filePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String jsonContent = reader.lines().collect(Collectors.joining("\n"));
                JSONObject jsonObject = (JSONObject) parser.parse(jsonContent);
                JSONArray numbersArray = (JSONArray) jsonObject.get("numbers");

                // Déterminer la taille de la grille en fonction des coordonnées max
                for (Object numberObj : numbersArray) {
                    JSONObject number = (JSONObject) numberObj;
                    int row = ((Long) number.get("row")).intValue();
                    int col = ((Long) number.get("col")).intValue();

                    if (row > maxRow)
                        maxRow = row;
                    if (col > maxCol)
                        maxCol = col;
                }

                // Création dynamique de la grille avec la bonne taille
                int[][] numbers = new int[maxRow + 1][maxCol + 1];

                for (int[] row : numbers) {
                    java.util.Arrays.fill(row, -1);
                }

                // Remplissage des valeurs
                for (Object numberObj : numbersArray) {
                    JSONObject number = (JSONObject) numberObj;
                    int row = ((Long) number.get("row")).intValue();
                    int col = ((Long) number.get("col")).intValue();
                    int value = ((Long) number.get("value")).intValue();
                    numbers[row][col] = value;
                }

                return numbers;
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.err.println("Failed to load grid from JSON. Using empty grid.");
        }

        return new int[0][0];
    }

    /**
     * Affiche la scène de jeu avec un état sauvegardé
     * 
     * @param primaryStage Le stage principal
     * @param gridId       L'ID de la grille
     * @param gridState    L'état sauvegardé de la grille
     * @param elapsedTime  Le temps écoulé à restaurer
     */
    public static void showWithSavedState(Stage primaryStage, String gridId, int[][][] gridState, int elapsedTime) {
        System.out.println("Chargement avec état sauvegardé - Grille: " + gridId);
        System.out.println("État: "
                + (gridState != null ? gridState.length + "x" + gridState[0].length + "x" + gridState[0][0].length
                        : "null"));
        System.out.println("Temps écoulé: " + elapsedTime + " secondes");

        // Charger la grille depuis le fichier JSON
        savedGridState = gridState;
        savedElapsedTime = elapsedTime;

        // Afficher la scène de jeu normalement, elle utilisera l'état sauvegardé si
        // disponible
        show(primaryStage, gridId);
    }

    public static void show(Stage primaryStage, String... newGridId) {
        applyTheme();
        PauseMenu.setGamePaused(false);
    
        // Update the current grid ID if provided
        if (newGridId != null && newGridId.length > 0 && newGridId[0] != null) {
            currentGridId = newGridId[0];
            gridId = newGridId[0];
        }
    
        String gridIdForLoading = gridId.startsWith("grid-") ? gridId : "grid-" + gridId;
    
        // Charger la grille depuis le fichier JSON
        int[][] gridNumbers = loadGridFromJson("grids/" + gridIdForLoading + ".json");
        slitherGrid = new SlitherGrid(gridNumbers);
        gameMatrix = slitherGrid.getGameMatrix();
    
        // Créer les composants principaux
        mainLayer = new VBox();
        mainLayer.setStyle("-fx-padding: 0; -fx-background-color: " + SlitherGrid.SECONDARY_COLOR + ";");
    
        root = new HBox();
        slitherGrid.setSlitherlinkGrid(new Pane());
        gridContainer = new StackPane(slitherGrid.getSlitherlinkGrid());
    
        gridContainer.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.9);" +
                "-fx-background-radius: 15;" +
                "-fx-padding: 20;");
    
        DropShadow gridShadow = new DropShadow();
        gridShadow.setColor(Color.web("#000000", 0.2));
        gridShadow.setRadius(10);
        gridShadow.setOffsetY(5);
        gridContainer.setEffect(gridShadow);
    
        // Créer la barre supérieure
        String username = UserManager.getCurrentUser();
        String level = getLevelFromGridId(gridId);
        String initialScore = "0";  // Score initial à 0
        TopBar topBar = new TopBar(primaryStage, username, level, initialScore, slitherGrid);
    
        // Créer les boutons et contrôles
        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(30));
        buttonBox.setMaxWidth(350);
        buttonBox.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.8);" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 5);");
    
        Label controlsTitle = new Label("Contrôles");
        controlsTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));
        controlsTitle.setTextFill(Color.web(SlitherGrid.DARK_COLOR));

        Button crossAutoButton = AutoCrossButton.createAutoCrossButton(slitherGrid, mainLayer, gridContainer);
      
        Button helpButton = Util.createStyledButton("   AIDE   ?  ", false, SlitherGrid.MAIN_COLOR,
                SlitherGrid.DARK_COLOR, SlitherGrid.SECONDARY_COLOR);
    
        Text techniqueCount = new Text(String.valueOf(techniqueCounter));
        techniqueCount.setFont(Font.font("Montserrat", FontWeight.BOLD, 22));
        techniqueCount.setFill(Color.web(SlitherGrid.DARK_COLOR));
        techniqueCountDisplay = techniqueCount;
    
        StackPane techniqueCountContainer = new StackPane(techniqueCount);
        techniqueCountContainer.setMinSize(40, 40);
        techniqueCountContainer.setMaxSize(40, 40);
        techniqueCountContainer.setStyle(
                "-fx-background-color: " + SlitherGrid.SECONDARY_COLOR + ";" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: " + SlitherGrid.MAIN_COLOR + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 20;");
    
                helpButton.setOnAction(e1 -> {
                    Util.animateButtonClick(helpButton);
        
                    SlitherlinkTechniqueDetector detector = new SlitherlinkTechniqueDetector(
                            slitherGrid.getGridNumbers(),
                            slitherGrid.gridLines,
                            slitherGrid.getSlitherlinkGrid());
        
                    // Trouver la première technique applicable
                    Optional<Class<? extends Techniques>> techniqueSuggere = TechniquesPriority.PRIORITY_ORDER.stream()
                            .filter(detector::estTechniqueApplicable)
                            .findFirst();
        
                    Stage suggestionStage = new Stage();
                    suggestionStage.initModality(Modality.APPLICATION_MODAL);
        
                    VBox content = new VBox(20);
                    content.setAlignment(Pos.CENTER);
                    content.setPadding(new Insets(30));
                    content.setStyle(
                            "-fx-background-color: linear-gradient(to bottom, " + SlitherGrid.SECONDARY_COLOR + ", "
                                    + SlitherGrid.LIGHT_COLOR + " 90%);" +
                                    "-fx-background-radius: 15;" +
                                    "-fx-border-color: " + SlitherGrid.MAIN_COLOR + ";" +
                                    "-fx-border-width: 2;" +
                                    "-fx-border-radius: 15;");
        
                    Label titre = new Label(techniqueSuggere.isPresent()
                            ? "Technique de Résolution"
                            : "Aucune Technique Disponible");
                    titre.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));
                    titre.setTextFill(Color.web(SlitherGrid.MAIN_COLOR));
                    
                    // Afficher le compteur de techniques restantes
                    Label counterLabel = new Label("Aides restantes : " + techniqueCounter);
                    counterLabel.setTextFill(Color.web(SlitherGrid.DARK_COLOR));
                    counterLabel.setFont(Font.font("Calibri", FontWeight.BOLD, 14));
        
                    VBox details = new VBox(10);
                    details.setAlignment(Pos.CENTER_LEFT);
        
                    // Bouton Appliquer
                    Button applyButton = Util.createStyledButton("Appliquer", true,
                            SlitherGrid.ACCENT_COLOR, SlitherGrid.ACCENT_COLOR, SlitherGrid.SECONDARY_COLOR);
                    applyButton.setPrefWidth(120);
                    
                    // Désactiver le bouton Appliquer si pas de techniques disponibles OU compteur à zéro
                    applyButton.setDisable(!techniqueSuggere.isPresent() || techniqueCounter <= 0);
        
                    if (techniqueSuggere.isPresent()) {
                        String nomTechnique = techniqueSuggere.get().getSimpleName();
        
                        Label description = new Label(TechniqueDescriptions.getDescription(nomTechnique));
                        description.setTextFill(Color.web(SlitherGrid.DARK_COLOR));
                        description.setWrapText(true);
        
                        details.getChildren().add(description);
                    }
        
                    applyButton.setOnAction(event -> {
                        Util.animateButtonClick(applyButton);
                        
                        if (techniqueSuggere.isPresent() && techniqueCounter > 0) {
                            try {
                                // Décrémenter le compteur lorsqu'une technique est appliquée
                                techniqueCounter--;
                                techniqueCount.setText(String.valueOf(techniqueCounter));
                                
                                // Mettre à jour l'affichage du compteur
                                counterLabel.setText("Aides restantes : " + techniqueCounter);
                                
                                // Créer une instance de la technique trouvée
                                Techniques technique = techniqueSuggere.get().getDeclaredConstructor(
                                    int[][].class, 
                                    Map.class, 
                                    Pane.class
                                ).newInstance(
                                    slitherGrid.getGridNumbers(),
                                    slitherGrid.gridLines,
                                    slitherGrid.getSlitherlinkGrid()
                                );
                                
                                // Créer une instance de Grille à partir des données de SlitherGrid
                                com.tpgr3.Grille grille = new com.tpgr3.Grille(slitherGrid.getGridNumbers());
                                
                                // Appliquer la technique
                                technique.appliquer(grille);
                                
                                // Afficher une confirmation
                                Label confirmLabel = new Label("Technique appliquée avec succès !");
                                confirmLabel.setTextFill(Color.web(SlitherGrid.MAIN_COLOR));
                                confirmLabel.setFont(Font.font("Calibri", FontWeight.BOLD, 16));
                                
                                // Désactiver le bouton Appliquer si plus de techniques disponibles
                                if (techniqueCounter <= 0) {
                                    applyButton.setDisable(true);
                                    
                                    Label warningLabel = new Label("Vous avez utilisé toutes vos aides disponibles.");
                                    warningLabel.setTextFill(Color.web(SlitherGrid.ACCENT_COLOR));
                                    warningLabel.setFont(Font.font("Calibri", FontWeight.BOLD, 14));
                                    
                                    details.getChildren().clear();
                                    details.getChildren().addAll(confirmLabel, warningLabel);
                                } else {
                                    details.getChildren().clear();
                                    details.getChildren().add(confirmLabel);
                                }
                            } catch (Exception ex) {
                                System.err.println("Erreur lors de l'application de la technique: " + ex.getMessage());
                                ex.printStackTrace();
                                
                                // Restaurer le compteur en cas d'erreur
                                techniqueCounter++;
                                techniqueCount.setText(String.valueOf(techniqueCounter));
                                counterLabel.setText("Aides restantes : " + techniqueCounter);
                                
                                // Afficher une alerte en cas d'erreur
                                Label errorLabel = new Label("Erreur lors de l'application de la technique");
                                errorLabel.setTextFill(Color.RED);
                                details.getChildren().add(errorLabel);
                            }
                        }
                    });
        
                    Button okButton = Util.createStyledButton("Compris", true,
                            SlitherGrid.MAIN_COLOR, SlitherGrid.DARK_COLOR, SlitherGrid.SECONDARY_COLOR);
                    okButton.setPrefWidth(120);
                    okButton.setOnAction(event -> {
                        Util.animateButtonClick(okButton);
                        suggestionStage.close();
                    });
                    
                    // Mettre les boutons côte à côte dans un HBox
                    HBox buttonsBox = new HBox(15, okButton, applyButton);
                    buttonsBox.setAlignment(Pos.CENTER);
        
                    content.getChildren().addAll(titre, counterLabel, details, buttonsBox);
        
                    Scene scene = new Scene(content, 400, 250);
                    suggestionStage.setScene(scene);
                    suggestionStage.show();
                });
                
                // Container pour le bouton d'aide et son compteur
                HBox helpContainer = new HBox(15, helpButton, techniqueCountContainer);
                helpContainer.setAlignment(Pos.CENTER);
                
                Button checkButton = Util.createStyledButton("Vérifier", true, SlitherGrid.MAIN_COLOR, SlitherGrid.DARK_COLOR,
                        SlitherGrid.SECONDARY_COLOR);
        
                Text checkCount = new Text(String.valueOf(checkCounter));
                checkCount.setFont(Font.font("Montserrat", FontWeight.BOLD, 22));
                checkCount.setFill(Color.web(SlitherGrid.DARK_COLOR));
        
                StackPane countContainer = new StackPane(checkCount);
                countContainer.setMinSize(40, 40);
                countContainer.setMaxSize(40, 40);
                countContainer.setStyle(
                        "-fx-background-color: " + SlitherGrid.SECONDARY_COLOR + ";" +
                                "-fx-background-radius: 20;" +
                                "-fx-border-color: " + SlitherGrid.MAIN_COLOR + ";" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 20;");
        
                checkButton.setOnAction(e -> {
                    Util.animateButtonClick(checkButton);
                    if (checkCounter > 0) {
                        checkCounter--;
                        checkCount.setText(String.valueOf(checkCounter));
                        if (checkCounter == 0) {
                            checkButton.setDisable(true);
                            checkButton.setOpacity(0.7);
                        }
                        handleCheckButton();
                    }
                });
        
                HBox checkContainer = new HBox(15, checkButton, countContainer);
                checkContainer.setAlignment(Pos.CENTER);
        
                Button hypothesisButton = Util.createStyledButton("Hypothèse", false, SlitherGrid.MAIN_COLOR,
                        SlitherGrid.DARK_COLOR, SlitherGrid.SECONDARY_COLOR);
                hypothesisButton.setOnAction(e -> {
                    Util.animateButtonClick(hypothesisButton);
                    if (slitherGrid.isHypothesisInactive()) {
                        slitherGrid.prepareHypothesis();
                        hypothesisButton.setText("Terminer Hypothèse");
                        hypothesisButton.setStyle(
                                "-fx-background-color: " + SlitherGrid.ACCENT_COLOR + ";" +
                                        "-fx-background-radius: 30;" +
                                        "-fx-text-fill: white;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-size: 16px;" +
                                        "-fx-padding: 10 20;" +
                                        "-fx-cursor: hand;");
                    } else {
                        Stage dialog = new Stage();
                        dialog.initModality(Modality.APPLICATION_MODAL);
        
                        Label dialogTitle = new Label("Confirmer l'hypothèse");
                        dialogTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 18));
                        dialogTitle.setTextFill(Color.web(SlitherGrid.DARK_COLOR));
        
                        Button confirmButton = Util.createStyledButton("Confirmer", true, SlitherGrid.MAIN_COLOR,
                                SlitherGrid.DARK_COLOR, SlitherGrid.SECONDARY_COLOR);
                        Button cancelButton = Util.createStyledButton("Annuler", false, SlitherGrid.MAIN_COLOR,
                                SlitherGrid.DARK_COLOR, SlitherGrid.SECONDARY_COLOR);
        
                        VBox dialogVBox = new VBox(20);
                        dialogVBox.setAlignment(Pos.CENTER);
                        dialogVBox.setPadding(new Insets(30));
                        dialogVBox.setStyle("-fx-background-color: " + SlitherGrid.SECONDARY_COLOR + ";");
        
                        HBox buttonContainer = new HBox(20, cancelButton, confirmButton);
                        buttonContainer.setAlignment(Pos.CENTER);
        
                        dialogVBox.getChildren().addAll(dialogTitle, buttonContainer);
        
                        Scene dialogScene = new Scene(dialogVBox, 350, 180);
                        dialog.setScene(dialogScene);
                        dialog.setTitle("Hypothèse");
        
                        dialogVBox.setOpacity(0);
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), dialogVBox);
                        fadeIn.setFromValue(0);
                        fadeIn.setToValue(1);
                        fadeIn.play();
        
                        confirmButton.setOnAction(event -> {
                            Util.animateButtonClick(confirmButton);
        
                            slitherGrid.confirmerHypothesis();
        
                            hypothesisButton.setText("Hypothèse");
                            hypothesisButton.setStyle(
                                    "-fx-background-color: " + SlitherGrid.SECONDARY_COLOR + ";" +
                                            "-fx-background-radius: 30;" +
                                            "-fx-border-color: " + SlitherGrid.MAIN_COLOR + ";" +
                                            "-fx-border-width: 2;" +
                                            "-fx-border-radius: 30;" +
                                            "-fx-text-fill: " + SlitherGrid.DARK_COLOR + ";" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 16px;" +
                                            "-fx-padding: 10 20;" +
                                            "-fx-cursor: hand;");
        
                            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), dialogVBox);
                            fadeOut.setFromValue(1);
                            fadeOut.setToValue(0);
                            fadeOut.setOnFinished(ev -> dialog.close());
                            fadeOut.play();
        
                        });
        
                        cancelButton.setOnAction(event -> {
                            Util.animateButtonClick(cancelButton);
        
                            slitherGrid.cancelHypothesis();
        
                            hypothesisButton.setText("Hypothèse");
                            hypothesisButton.setStyle(
                                    "-fx-background-color: " + SlitherGrid.SECONDARY_COLOR + ";" +
                                            "-fx-background-radius: 30;" +
                                            "-fx-border-color: " + SlitherGrid.MAIN_COLOR + ";" +
                                            "-fx-border-width: 2;" +
                                            "-fx-border-radius: 30;" +
                                            "-fx-text-fill: " + SlitherGrid.DARK_COLOR + ";" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 16px;" +
                                            "-fx-padding: 10 20;" +
                                            "-fx-cursor: hand;");
        
                            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), dialogVBox);
                            fadeOut.setFromValue(1);
                            fadeOut.setToValue(0);
                            fadeOut.setOnFinished(ev -> dialog.close());
                            fadeOut.play();
                        });
        
                        dialog.show();
                    }
                });
    
        Button saveButton = Util.createStyledButton("Sauvegarder", false, SlitherGrid.MAIN_COLOR,
                SlitherGrid.DARK_COLOR, SlitherGrid.SECONDARY_COLOR);
        saveButton.setOnAction(e -> {
            Util.animateButtonClick(saveButton);
            System.out.println("Nombre de techniqueCounter" + techniqueCounter);
            if (GameSaveManager.saveGame("grid-" + gridId, savedElapsedTime > 0 ? savedElapsedTime : 0, techniqueCounter, false)) {
                GameSaveManager.showSaveNotification(slitherGrid.getSlitherlinkGrid());
            }
        });
    
        // Conteneur pour les boutons de navigation
        HBox historyContainer = new HBox(15, slitherGrid.getPrevButton(), slitherGrid.getNextButton());
        historyContainer.setAlignment(Pos.CENTER);

        buttonBox.getChildren().addAll(controlsTitle, createSeparator(), helpContainer, checkContainer, hypothesisButton,
                crossAutoButton, saveButton, createSeparator(), historyContainer);

        gridContainer.setPadding(new Insets(20));
        gridContainer.prefWidthProperty().bind(root.widthProperty().multiply(0.65));
    
        root.setSpacing(30);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(gridContainer, buttonBox);
    
        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + SlitherGrid.SECONDARY_COLOR + ", "
                        + SlitherGrid.LIGHT_COLOR
                        + " 70%);" +
                "-fx-background-radius: 0;" +
                "-fx-padding: 20px;");
    
        mainLayer.getChildren().add(root);
    
        // Créer la scène APRÈS avoir configuré tous les composants principaux
        Scene scene = new Scene(mainLayer, Screen.getPrimary().getVisualBounds().getWidth(),
                Screen.getPrimary().getVisualBounds().getHeight());
    
        // Créer la barre supérieure APRÈS avoir créé la scène
        HBox topBarComponent = topBar.createTopBar(scene);
        mainLayer.getChildren().add(0, topBarComponent);
    
        // Timer et callback APRÈS avoir configuré la topBar
        gameTimer = new java.util.Timer();
        final int[] secondsElapsed = { savedElapsedTime > 0 ? savedElapsedTime : 0 };
    
        gameTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                // Vérifier si le jeu est en pause
                if (!PauseMenu.isGamePaused()) {
                    secondsElapsed[0]++;
                    int minutes = secondsElapsed[0] / 60;
                    int seconds = secondsElapsed[0] % 60;
                    
                    // Calculer le score (temps en secondes * 2)
                    int scoreValue = secondsElapsed[0] * 2;
    
                    javafx.application.Platform.runLater(() -> {
                        topBar.updateChronometer(minutes, seconds);
                        topBar.updateScore(scoreValue);  // Mettre à jour le score
                    });
                }
                // Si en pause, ne rien faire - le temps ne s'incrémente pas
            }
        }, 0, 1000);
    
        // Configuration du callback de réinitialisation du chronomètre
        topBar.setChronoResetCallback(() -> {
            // Réinitialiser le compteur de secondes
            secondsElapsed[0] = 0;
    
            // Réinitialiser l'affichage du chronomètre
            javafx.application.Platform.runLater(() -> {
                topBar.updateChronometer(0, 0);
            });
        });
    
        // Configuration du callback pour réinitialiser la grille
        topBar.setGridResetCallback(() -> {
            savedGridState = null;
    
            // Recréer la grille de jeu
            slitherGrid.getSlitherlinkGrid().getChildren()
                    .removeIf(node -> node instanceof Line);
            System.out.println(slitherGrid.getSlitherlinkGrid());
    
            // Reconstruire la grille
            slitherGrid.updateGrid(scene.getWidth(), scene.getHeight());
    
            // Mettre à jour les boutons d'historique
            slitherGrid.updateHistoryButtons();
    
            javafx.application.Platform.runLater(() -> {
                topBar.updateScore(0);
            });
        });
    
        // Si un état sauvegardé est disponible, l'appliquer à la grille
        if (savedGridState != null) {
            Platform.runLater(() -> {
                try {
                    System.out.println("Applying saved state from GameScene.show()");
                    SaveGameLoader.applyGridState(savedGridState);
                    savedGridState = null; // Clear the saved state to avoid reapplying
                } catch (Exception e) {
                    System.err.println("Error applying saved state: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    
        // Mettre à jour le chronomètre si un temps sauvegardé est disponible
        if (savedElapsedTime > 0) {
            int minutes = savedElapsedTime / 60;
            int seconds = savedElapsedTime % 60;
            topBar.updateChronometer(minutes, seconds);
        }
    
        // Configuration finale de la scène et du stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Slitherlink Game");
        primaryStage.setMaximized(true);
    
        // Animation d'entrée
        root.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    
        // Écouteurs pour la mise à jour de la grille
        scene.widthProperty()
                .addListener((obs, oldVal, newVal) -> slitherGrid.updateGrid(scene.getWidth(), scene.getHeight()));
        scene.heightProperty()
                .addListener((obs, oldVal, newVal) -> slitherGrid.updateGrid(scene.getWidth(), scene.getHeight()));
        primaryStage.maximizedProperty()
                .addListener((obs, oldVal, isMaximized) -> slitherGrid.updateGrid(scene.getWidth(), scene.getHeight()));
        primaryStage.fullScreenProperty()
                .addListener((obs, oldVal, isFullScreen) -> slitherGrid.updateGrid(scene.getWidth(), scene.getHeight()));
    
        primaryStage.show();
        slitherGrid.updateGrid(scene.getWidth(), scene.getHeight());
    }
    
    
    private static Node createSeparator() {
        Rectangle separator = new Rectangle();
        separator.setHeight(2);
        separator.setWidth(250);
        separator.setFill(Color.web(SlitherGrid.LIGHT_COLOR, 0.5));
        separator.setArcWidth(2);
        separator.setArcHeight(2);

        VBox separatorContainer = new VBox(separator);
        separatorContainer.setPadding(new Insets(5, 0, 5, 0));
        separatorContainer.setAlignment(Pos.CENTER);

        return separatorContainer;
    }

    /**
     * Updates the UI with the current theme settings
     * Can be called from SettingScene when theme changes
     */
    public static void updateTheme(Stage primaryStage) {
        applyTheme();

        // If the game is already displayed, refresh it
        if (root != null) {
            // Update background colors
            String gradientStyle = SettingScene.isDarkModeEnabled()
                    ? "linear-gradient(to bottom right, " + SlitherGrid.SECONDARY_COLOR + ", " + SlitherGrid.DARK_COLOR
                            + " 70%);"
                    : "linear-gradient(to bottom right, " + SlitherGrid.SECONDARY_COLOR + ", " + SlitherGrid.LIGHT_COLOR
                            + " 70%);";

            root.setStyle(
                    "-fx-background-color: " + gradientStyle +
                            "-fx-background-radius: 0;" +
                            "-fx-padding: 20px;");

            // Refresh the grid
            slitherGrid.updateGrid(root.getScene().getWidth(), root.getScene().getHeight());
        }
    }

    /**
     * Retourne une représentation simplifiée de la matrice de jeu actuelle.
     * Cette méthode est utilisée par GameSaveManager pour créer des sauvegardes.
     * 
     * @return Une matrice 3D représentant l'état du jeu
     */
    public static int[][][] getSimplifiedGameMatrix() {
        // Si nous avons une SlitherGrid active
        if (slitherGrid != null) {
            return slitherGrid.getSimplifiedGameMatrix();
        }
        return null;
    }

    /**
     * Met à jour l'affichage du compteur de techniques
     */
    public static void updateTechniqueCountDisplay() {
        if (techniqueCountDisplay != null) {
            Platform.runLater(() -> {
                techniqueCountDisplay.setText(String.valueOf(techniqueCounter));
            });
        }
    }

    // Modifier la méthode resetTechniqueCounter pour mettre à jour l'affichage
    public static void resetTechniqueCounter() {
        techniqueCounter = 3;
        updateTechniqueCountDisplay();
    }

    /**
     * Obtient le nombre de techniques restantes
     * 
     * @return Le nombre de techniques restantes
     */
    public static int getTechniqueCounter() {
        return techniqueCounter;
    }

    /**
     * Définit le nombre de techniques restantes
     * 
     * @param count Le nombre de techniques
     */
    public static void setTechniqueCounter(int count) {
        techniqueCounter = count;
    }

    public static String getCurrentGridId() {
        return currentGridId;
    }

    /**
     * Returns the current SlitherGrid instance
     * 
     * @return The current SlitherGrid instance
     */
    public static SlitherGrid getSlitherGrid() {
        return slitherGrid;
    }

    /**
     * Extrait le numéro de niveau depuis l'ID de la grille
     * 
     * @param gridId L'ID de la grille (ex: "002" ou "grid-002")
     * @return Le numéro de niveau sous forme de chaîne (ex: "2")
     */
    private static String getLevelFromGridId(String gridId) {
        // Supprimer le préfixe "grid-" s'il est présent
        String cleanId = gridId.startsWith("grid-") ? gridId.substring(5) : gridId;

        // Convertir en nombre en supprimant les zéros au début
        try {
            int levelNumber = Integer.parseInt(cleanId);
            return String.valueOf(levelNumber);
        } catch (NumberFormatException e) {
            // Si l'ID ne peut pas être analysé comme un nombre, le retourner tel quel
            return cleanId;
        }
    }

    private static void handleCheckButton() {
        boolean isCorrect = slitherGrid.checkGrid();
    
        // Créer une fenêtre personnalisée au lieu d'une Alert standard
        Stage checkStage = new Stage();
        checkStage.initModality(Modality.APPLICATION_MODAL);
    
        // Titre stylisé
        Label checkTitle = new Label(isCorrect ? "Félicitations !" : "Solution incorrecte");
        checkTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));
    
        // Couleur du titre selon le résultat
        checkTitle.setTextFill(isCorrect ? Color.web(SlitherGrid.MAIN_COLOR) : Color.web(SlitherGrid.ACCENT_COLOR));
    
        // Effet d'ombre pour le titre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.3));
        shadow.setRadius(3);
        shadow.setOffsetY(2);
        checkTitle.setEffect(shadow);
    
        // Message
        Label messageLabel;
        if (isCorrect) {
            messageLabel = new Label("Votre solution est correcte !");
            messageLabel.setTextFill(Color.web(SlitherGrid.DARK_COLOR));
            messageLabel.setFont(Font.font("Calibri", 16));
        } else {
            // Utiliser un VBox pour formater les points de vérification
            VBox errorDetails = new VBox(8);
            errorDetails.setAlignment(Pos.CENTER_LEFT);
    
            Label mainError = new Label("Votre solution ne respecte pas les règles du Slitherlink.");
            mainError.setTextFill(Color.web(SlitherGrid.DARK_COLOR));
            mainError.setFont(Font.font("Calibri", FontWeight.BOLD, 16));
    
            Label check1 = new Label("• Les chiffres doivent correspondre exactement au nombre de segments adjacents");
            Label check2 = new Label("• Toutes les lignes doivent former un circuit unique fermé");
            Label check3 = new Label("• Il ne doit pas y avoir de branches ou d'intersections");
    
            check1.setTextFill(Color.web(SlitherGrid.DARK_COLOR));
            check2.setTextFill(Color.web(SlitherGrid.DARK_COLOR));
            check3.setTextFill(Color.web(SlitherGrid.DARK_COLOR));
    
            errorDetails.getChildren().addAll(mainError, check1, check2, check3);
    
            checkStage.setTitle("Erreur de vérification");
    
            // Créer un conteneur pour tout le contenu
            VBox contentBox = new VBox(20, checkTitle, errorDetails);
            contentBox.setAlignment(Pos.CENTER);
            contentBox.setPadding(new Insets(30));
            contentBox.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, " + SlitherGrid.SECONDARY_COLOR + ", white);" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-color: " + SlitherGrid.ACCENT_COLOR + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 15;");
    
            // Bouton OK
            Button okButton = Util.createStyledButton("Continuer", false, SlitherGrid.MAIN_COLOR,
                    SlitherGrid.DARK_COLOR, SlitherGrid.SECONDARY_COLOR);
            okButton.setPrefWidth(120);
    
            okButton.setOnAction(e -> {
                Util.animateButtonClick(okButton);
    
                // Animation de fermeture
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), contentBox);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(event -> checkStage.close());
                fadeOut.play();
            });
    
            contentBox.getChildren().add(okButton);
    
            Scene checkScene = new Scene(contentBox, 500, 350);
            checkStage.setScene(checkScene);
    
            // Animation d'entrée
            contentBox.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), contentBox);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
    
            checkStage.show();
            return;
        }
    
        checkStage.setTitle("Vérification réussie");
    
        // Bouton OK
        Button okButton = Util.createStyledButton("Super !", true, SlitherGrid.MAIN_COLOR, SlitherGrid.DARK_COLOR,
                SlitherGrid.SECONDARY_COLOR);
        okButton.setPrefWidth(120);
    
        okButton.setOnAction(e -> {
            Util.animateButtonClick(okButton);
    
            // Animation de fermeture
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), checkStage.getScene().getRoot());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> checkStage.close());
            fadeOut.play();
        });
    
        // Organisation des éléments
        VBox checkBox = new VBox(20, checkTitle, messageLabel, okButton);
        checkBox.setAlignment(Pos.CENTER);
        checkBox.setPadding(new Insets(30));
        checkBox.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " + SlitherGrid.SECONDARY_COLOR + ", "
                        + SlitherGrid.LIGHT_COLOR + " 90%);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: " + SlitherGrid.MAIN_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;");
    
        Scene checkScene = new Scene(checkBox, 400, 250);
        checkStage.setScene(checkScene);
    
        // Animation d'entrée
        checkBox.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), checkBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    
        checkStage.show();
    }

    /**
     * Détermine la difficulté en fonction du niveau
     * 
     * @param level Le niveau sous forme de chaîne
     * @return Une description de la difficulté
     */
    private static String getDifficultyFromLevel(String level) {
        try {
            int levelNum = Integer.parseInt(level);
            if (levelNum <= 5)
                return "Facile";
            if (levelNum <= 10)
                return "Moyen";
            if (levelNum <= 15)
                return "Difficile";
            return "Expert";
        } catch (NumberFormatException e) {
            return "Facile"; // Par défaut
        }
    }

    /**
     * Nettoie les ressources du jeu (à appeler avant de quitter)
     */
    public static void cleanup() {
        if (gameTimer != null) {
            try {
                gameTimer.cancel();
                gameTimer.purge();
            } catch (Exception e) {
                System.err.println("Erreur lors du nettoyage du timer: " + e.getMessage());
            } finally {
                gameTimer = null;
            }
        }
        // Réinitialiser l'état de pause
        try {
            PauseMenu.setGamePaused(false);
        } catch (Exception e) {
            System.err.println("Erreur lors de la réinitialisation de l'état de pause: " + e.getMessage());
        }

        // Réinitialiser l'état sauvegardé
        savedElapsedTime = 0;
    }

  
}