package com.menu.javafx;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PauseMenu {

    // les couleurs, identiques à celles du menu principal
    private static final String MAIN_COLOR = "#3A7D44"; // Vert principal
    private static final String SECONDARY_COLOR = "#F2E8CF"; // Beige clair
    private static final String ACCENT_COLOR = "#BC4749"; // Rouge-brique
    private static final String DARK_COLOR = "#386641"; // Vert foncé
    private static final String LIGHT_COLOR = "#A7C957"; // Vert clair
  // Variables statiques pour stocker l'état du jeu
    private static Scene previousGameScene;
    private static int currentMinutes;
    private static int currentSeconds;
    private static TopBar topBar;
    private static boolean isGamePaused = false;

    // Méthode pour sauvegarder l'état actuel
    public static void saveGameState(Scene gameScene, int minutes, int seconds, TopBar gameTopBar) {
        previousGameScene = gameScene;
        currentMinutes = minutes;
        currentSeconds = seconds;
        topBar = gameTopBar;
        isGamePaused = true; // Définir comme en pause quand on entre dans le menu
    }

    // Méthode pour vérifier si le jeu est en pause
    public static boolean isGamePaused() {
        return isGamePaused;
    }

    public static void show(Stage primaryStage) {
        // Conteneur principal pour le menu de pause
        VBox pauseMenu = new VBox(20);
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; -fx-padding: 20;");

        // "PAUSE"
        Label pauseTitle = new Label("PAUSE");
        pauseTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 42));
        pauseTitle.setTextFill(Color.web(DARK_COLOR));
        pauseTitle.setStyle("-fx-padding: 20;");

        // Bouton "Reprendre"
        Button resumeButton = createStyledButton("Reprendre");
        resumeButton.setOnAction(e -> {
            // Désactiver l'état de pause avant de revenir au jeu
            isGamePaused = false;
            
            // Restaure la scène précédente au lieu d'en créer une nouvelle
            if (previousGameScene != null) {
                primaryStage.setScene(previousGameScene);
                primaryStage.setTitle("Slitherlink");
                
                // Restaurer l'affichage du chronomètre si nécessaire
                if (topBar != null) {
                    topBar.updateChronometer(currentMinutes, currentSeconds);
                }
            } else {
                // Si pas de scène sauvegardée (cas improbable), revenir à une nouvelle
                GameScene.show(primaryStage, GameScene.getCurrentGridId());
            }
        });

        // Bouton Choix Grille
        Button choixGrilleButton = createStyledButton("Choix Grille");
        choixGrilleButton.setTooltip(new javafx.scene.control.Tooltip("Reprendre le jeu"));
        choixGrilleButton.setOnAction(e -> {
            Util.animateButtonClick(choixGrilleButton);
            GridScene.show(primaryStage);
        });

        // Bouton "Tutoriel"
        Button tutorialButton = createStyledButton("Tutoriel");
        tutorialButton.setTooltip(new javafx.scene.control.Tooltip("Voir le tutoriel"));
        tutorialButton.setOnAction(e -> {
            Util.animateButtonClick(tutorialButton);
            TechniquesScene.show(primaryStage);
        });

        // Bouton "Paramètres"
        Button settingsButton = createStyledButton("Paramètres");
        settingsButton.setTooltip(new javafx.scene.control.Tooltip("Modifier les paramètres"));
        settingsButton.setOnAction(e -> {
            SettingScene.show(primaryStage);
        });

        // Bouton "Quitter la partie"
        Button quitButton = createStyledButton("Quitter la partie");
        quitButton.setTooltip(new javafx.scene.control.Tooltip("Quitter la partie et revenir au menu principal"));
        quitButton.setOnAction(e -> {
            // Revient au menu principal sans fermer l'application
            Menu.show(primaryStage);
        });

        // Ajouter le titre et les boutons au menu de pause
        pauseMenu.getChildren().addAll(pauseTitle, resumeButton, choixGrilleButton,settingsButton, tutorialButton, quitButton);

        // Créer une scène pour le menu de pause avec des transitions et des styles
        Scene pauseScene = new Scene(pauseMenu, 400, 400);
        primaryStage.setScene(pauseScene);
        primaryStage.setTitle("Pause Menu");

        // Animation de transition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), pauseMenu);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        primaryStage.show();
    }

    private static Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(javafx.scene.text.Font.font("Calibri", 16));
        button.setPrefWidth(300);
        button.setPrefHeight(55);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new javafx.geometry.Insets(10, 20, 10, 20));
        
        button.setStyle(
            "-fx-background-color: " + SECONDARY_COLOR + ";" +
            "-fx-background-radius: 30;" +
            "-fx-border-color: " + MAIN_COLOR + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 30;" +
            "-fx-cursor: hand;"
        );
        
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
                "-fx-cursor: hand;"
            );
            button.setTextFill(Color.WHITE);
            
            FadeTransition scaleTransition = new FadeTransition(Duration.millis(200), button);
            scaleTransition.setToValue(1.05);
            scaleTransition.setToValue(1.05);
            scaleTransition.play();
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: " + SECONDARY_COLOR + ";" +
                "-fx-background-radius: 30;" +
                "-fx-border-color: " + MAIN_COLOR + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 30;" +
                "-fx-cursor: hand;"
            );
            button.setTextFill(Color.web(DARK_COLOR));
            
            FadeTransition scaleTransition = new FadeTransition(Duration.millis(200), button);
            scaleTransition.setToValue(1.0);
            scaleTransition.setToValue(1.0);
            scaleTransition.play();
        });
        
        return button;
    }
}
