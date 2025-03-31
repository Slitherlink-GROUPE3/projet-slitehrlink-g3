package com.menu.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import java.util.Random;

public class FreeModeScene {

    // Constantes de couleurs pour maintenir la cohérence
    private static final String MAIN_COLOR = "#3A7D44"; // Vert principal
    private static final String SECONDARY_COLOR = "#F2E8CF"; // Beige clair
    private static final String ACCENT_COLOR = "#BC4749"; // Rouge-brique
    private static final String DARK_COLOR = "#386641"; // Vert foncé
    private static final String LIGHT_COLOR = "#A7C957"; // Vert clair

    public static void show(Stage primaryStage) {
        // Conteneur principal
        GameState.choixScene = 1;
        VBox root = new VBox(40); // Augmenté l'espacement pour un meilleur aspect visuel
        root.setAlignment(Pos.CENTER);
        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + SECONDARY_COLOR + ", " + LIGHT_COLOR
                        + " 70%);" +
                        "-fx-background-radius: 0;" +
                        "-fx-padding: 20px;");

        // Titre de la page
        Label titleLabel = new Label("MODE LIBRE");
        titleLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 42));

        // Gradient pour le titre
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(DARK_COLOR)),
                new Stop(0.5, Color.web(MAIN_COLOR)),
                new Stop(1, Color.web(DARK_COLOR)));
        titleLabel.setTextFill(gradient);

        // Effet de glow et d'ombre pour le titre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(DARK_COLOR, 0.5));
        shadow.setRadius(10);
        shadow.setOffsetY(3);

        Glow glow = new Glow(0.6);
        glow.setInput(shadow);
        titleLabel.setEffect(glow);

        // Conteneur pour le titre
        HBox titleContainer = new HBox(titleLabel);
        titleContainer.setAlignment(Pos.CENTER);

        // Créer les trois boutons de difficulté
        VBox difficultyButtons = createDifficultyButtons(primaryStage);

        // Bouton de retour
        Button backButton = createAnimatedButton("Retour");
        backButton.setOnAction(e -> Menu.show(primaryStage));
        
        HBox backButtonContainer = new HBox(backButton);
        backButtonContainer.setAlignment(Pos.CENTER);

        // Assembler tous les éléments
        root.getChildren().addAll(titleContainer, difficultyButtons, backButtonContainer);

        // Créer la scène
        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Slitherlink - Mode Libre");
    }

    private static VBox createDifficultyButtons(Stage primaryStage) {
        VBox buttonsBox = new VBox(20); // Espacement de 20 entre les boutons
        buttonsBox.setAlignment(Pos.CENTER);
        
        // Créer les trois boutons de difficulté
        Button easyButton = createAnimatedButton("Facile");
        Button mediumButton = createAnimatedButton("Moyen");
        Button hardButton = createAnimatedButton("Difficile");
        
        // Ajouter les actions pour chaque bouton
        easyButton.setOnAction(e -> {
            // Sélectionner une grille aléatoire entre 1 et 5
            int gridNumber = getRandomGridNumber(1, 5);
            String gridId = String.format("grid-%03d", gridNumber);
            loadGameGrid(primaryStage, gridId);
        });
        
        mediumButton.setOnAction(e -> {
            // Sélectionner une grille aléatoire entre 6 et 10
            int gridNumber = getRandomGridNumber(6, 10);
            String gridId = String.format("grid-%03d", gridNumber);
            loadGameGrid(primaryStage, gridId);
        });
        
        hardButton.setOnAction(e -> {
            // Sélectionner une grille aléatoire entre 11 et 15
            int gridNumber = getRandomGridNumber(11, 15);
            String gridId = String.format("grid-%03d", gridNumber);
            loadGameGrid(primaryStage, gridId);
        });
        
        // Ajouter les boutons au conteneur
        buttonsBox.getChildren().addAll(easyButton, mediumButton, hardButton);
        
        return buttonsBox;
    }

    // Méthode pour créer un bouton animé similaire à ceux du menu principal
    /**
     * Génère un nombre aléatoire entre min et max (inclus)
     */
    private static int getRandomGridNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
    
    /**
     * Charge une grille de jeu spécifique
     */
    private static void loadGameGrid(Stage primaryStage, String gridId) {
        //System.out.println("Chargement de la grille: " + gridId);
        
        // Vérifier si une sauvegarde existe pour cette grille
        if (GameSaveManager.hasSavedGame(gridId)) {
            // Charger la sauvegarde existante
            GameSaveManager.loadGame(primaryStage, gridId);
        } else {
            // Démarrer une nouvelle partie avec cette grille
            GameScene.show(primaryStage, gridId);
        }
    }
    
    private static Button createAnimatedButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Calibri", FontWeight.BOLD, 16));
        button.setTextFill(Color.web(DARK_COLOR));
        button.setPrefWidth(300);
        button.setPrefHeight(55);
        button.setAlignment(Pos.CENTER);
        button.setPadding(new Insets(10, 20, 10, 20));

        button.setStyle(
                "-fx-background-color: " + SECONDARY_COLOR + ";" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-color: " + MAIN_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 30;" +
                        "-fx-cursor: hand;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.2));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        button.setEffect(shadow);

        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: " + MAIN_COLOR + ";" +
                            "-fx-background-radius: 30;" +
                            "-fx-border-color: " + DARK_COLOR + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 30;" +
                            "-fx-cursor: hand;");
            button.setTextFill(Color.WHITE);

            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: " + SECONDARY_COLOR + ";" +
                            "-fx-background-radius: 30;" +
                            "-fx-border-color: " + MAIN_COLOR + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 30;" +
                            "-fx-cursor: hand;");
            button.setTextFill(Color.web(DARK_COLOR));

            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });

        return button;
    }
}