package com.menu.javafx;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PauseMenu {
    // Variables statiques pour stocker l'état du jeu
    private static Scene previousGameScene;
    private static int currentMinutes;
    private static int currentSeconds;
    private static TopBar topBar;
    private static boolean isGamePaused = false;
    private static Button choixGrilleButton;

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
        // Déterminer le thème actuel
        boolean isDarkMode = SettingScene.isDarkModeEnabled();
        
        // Conteneur principal avec fond dynamique
        StackPane root = new StackPane();
        createModernBackground(root, isDarkMode);
        
        // Conteneur principal pour le menu de pause avec effet de carte
        VBox pauseMenu = new VBox(25);
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setPadding(new Insets(40, 30, 40, 30));
        pauseMenu.setMaxWidth(450);
        
        // Style du conteneur en mode carte moderne avec fond translucide
        String boxBackground = isDarkMode ? 
                "rgba(30, 35, 45, 0.9)" : 
                "rgba(255, 255, 255, 0.95)";
        
        String borderColor = isDarkMode ? 
                "rgba(80, 180, 150, 0.3)" : 
                "rgba(54, 132, 109, 0.2)";
        
        pauseMenu.setStyle(
                "-fx-background-color: " + boxBackground + ";" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-width: 1.5px;" +
                "-fx-border-radius: 20;");
        
        // Ajouter un effet d'ombre portée élégant
        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.web(isDarkMode ? "#000000" : "#333333", 0.25));
        cardShadow.setRadius(20);
        cardShadow.setSpread(0.05);
        cardShadow.setOffsetY(6);
        pauseMenu.setEffect(cardShadow);

        // Titre "PAUSE" avec effet 3D
        Text pauseTitle = new Text("PAUSE");
        pauseTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 48));
        
        // Dégradé pour le texte selon le thème
        LinearGradient textGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(isDarkMode ? "#42b996" : "#36846d", 0.9)),
                new Stop(0.5, Color.web(isDarkMode ? "#74c99e" : "#255a4b", 1.0)),
                new Stop(1, Color.web(isDarkMode ? "#42b996" : "#36846d", 0.9))
        );
        pauseTitle.setFill(textGradient);
        
        // Effet 3D avancé pour le titre
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.web(isDarkMode ? "#000000" : "#333333", 0.3));
        dropShadow.setRadius(6);
        dropShadow.setOffsetX(2);
        dropShadow.setOffsetY(2);
        
        Reflection reflection = new Reflection();
        reflection.setFraction(0.2);
        reflection.setTopOpacity(0.5);
        reflection.setBottomOpacity(0);
        
        Glow glow = new Glow(0.4);
        
        // Combiner les effets
        dropShadow.setInput(reflection);
        glow.setInput(dropShadow);
        pauseTitle.setEffect(glow);
        
        // Créer une ligne décorative sous le titre
        Line decorativeLine = new Line(-100, 0, 100, 0);
        decorativeLine.setStroke(Color.web(isDarkMode ? "#74c99e" : "#36846d", 0.7));
        decorativeLine.setStrokeWidth(3);
        decorativeLine.setStrokeLineCap(StrokeLineCap.ROUND);
        
        // Animation pour la ligne décorative
        decorativeLine.setScaleX(0);
        Timeline lineAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(decorativeLine.scaleXProperty(), 0)),
            new KeyFrame(Duration.millis(800), new KeyValue(decorativeLine.scaleXProperty(), 1, 
                    Interpolator.EASE_OUT))
        );
        
        // Groupe pour le titre et sa décoration
        VBox titleGroup = new VBox(15);
        titleGroup.setAlignment(Pos.CENTER);
        titleGroup.getChildren().addAll(pauseTitle, decorativeLine);
        titleGroup.setPadding(new Insets(0, 0, 20, 0));

        // Bouton "Reprendre" avec style moderne
        Button resumeButton = createModernButton("Reprendre", true, isDarkMode);
        resumeButton.setOnAction(e -> {
            // Animation de clic
            animateButtonClick(resumeButton);
            
            // Animation de fermeture
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(evt -> {
                // Désactiver l'état de pause avant de revenir au jeu
                isGamePaused = false;
                
                // Restaure la scène précédente
                if (previousGameScene != null) {
                    primaryStage.setScene(previousGameScene);
                    primaryStage.setTitle("Slitherlink");
                    
                    // Restaurer l'affichage du chronomètre si nécessaire
                    if (topBar != null) {
                        topBar.updateChronometer(currentMinutes, currentSeconds);
                    }
                } else {
                    GameScene.show(primaryStage, GameScene.getCurrentGridId());
                }
            });
            fadeOut.play();
        });

        // Bouton Choix Grille
        if(GameState.choixScene == 0)
            choixGrilleButton = createModernButton("Choix Niveau", false, isDarkMode);
        else
            choixGrilleButton = createModernButton("Choix Difficulté", false, isDarkMode);
            
        choixGrilleButton.setOnAction(e -> {
            animateButtonClick(choixGrilleButton);
            
            // Animation de sortie
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(evt -> {
                if(GameState.choixScene == 0)
                    GridScene.show(primaryStage);
                else
                    FreeModeScene.show(primaryStage);
            });
            fadeOut.play();
        });

        // Bouton "Tutoriel"
        Button tutorialButton = createModernButton("Tutoriel", false, isDarkMode);
        tutorialButton.setOnAction(e -> {
            animateButtonClick(tutorialButton);
            
            // Animation de sortie
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(evt -> {
                TechniquesScene.show(primaryStage);
            });
            fadeOut.play();
        });

        // Bouton "Paramètres"
        Button settingsButton = createModernButton("Paramètres", false, isDarkMode);
        settingsButton.setOnAction(e -> {
            animateButtonClick(settingsButton);
            
            // Animation de sortie
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(evt -> {
                SettingScene.show(primaryStage);
            });
            fadeOut.play();
        });

        // Bouton "Quitter la partie"
        Button quitButton = createModernButton("Quitter la partie", true, isDarkMode);
        quitButton.setStyle(quitButton.getStyle() + 
                "-fx-background-color: " + (isDarkMode ? "#f2777a" : "#d65c5c") + ";");
        quitButton.setOnAction(e -> {
            animateButtonClick(quitButton);
            
            // Animation de sortie avec rotation légère
            ScaleTransition scale = new ScaleTransition(Duration.millis(300), root);
            scale.setToX(0.9);
            scale.setToY(0.9);
            
            FadeTransition fade = new FadeTransition(Duration.millis(300), root);
            fade.setToValue(0);
            
            ParallelTransition transition = new ParallelTransition(scale, fade);
            transition.setOnFinished(evt -> {
                // Nettoyer le timer et revenir au menu
                GameScene.cleanup();
                Menu.show(primaryStage);
            });
            transition.play();
        });

        // Ajouter les éléments au menu
        pauseMenu.getChildren().addAll(
                titleGroup, 
                resumeButton, 
                choixGrilleButton,
                settingsButton, 
                tutorialButton, 
                quitButton
        );

        // Centrer le menu dans la scène
        root.getChildren().add(pauseMenu);
        
        // Créer la scène
        Scene pauseScene = new Scene(root, 600, 650);
        
        // Animation d'entrée
        pauseMenu.setScaleX(0.9);
        pauseMenu.setScaleY(0.9);
        pauseMenu.setOpacity(0);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), pauseMenu);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(400), pauseMenu);
        scaleIn.setFromX(0.9);
        scaleIn.setFromY(0.9);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        scaleIn.setInterpolator(Interpolator.EASE_OUT);
        
        // Lancer les animations d'entrée et la ligne décorative
        ParallelTransition entryAnimation = new ParallelTransition(fadeIn, scaleIn);
        entryAnimation.play();
        lineAnimation.play();
        
        primaryStage.setScene(pauseScene);
        primaryStage.setTitle("Pause Menu");
        primaryStage.show();
    }
    
    /**
     * Crée un fond animé moderne
     */
    private static void createModernBackground(StackPane root, boolean isDarkMode) {
        // Fond avec dégradé adapté au mode
        Rectangle background = new Rectangle();
        background.widthProperty().bind(root.widthProperty());
        background.heightProperty().bind(root.heightProperty());
        
        // Créer un dégradé radial
        Color color1 = Color.web(isDarkMode ? "#141921" : "#f5f8fa");
        Color color2 = Color.web(isDarkMode ? "#192730" : "#a9d8bd", 0.7);
        Color accentColor = Color.web(isDarkMode ? "#42b996" : "#36846d", 0.1);
        
        RadialGradient gradient = new RadialGradient(
                0, 0, 0.5, 0.3, 1.0, true, CycleMethod.NO_CYCLE,
                new Stop(0, color1),
                new Stop(0.8, color2),
                new Stop(1, accentColor)
        );
        
        background.setFill(gradient);
        
        // Ajouter des motifs subtils au fond (limité à quelques éléments pour éviter les lags)
        if (isDarkMode) {
            // Points brillants subtils pour le mode sombre (réduit à 10)
            for (int i = 0; i < 10; i++) {
                double x = Math.random() * 600;
                double y = Math.random() * 650;
                double size = 1 + Math.random() * 2;
                
                Circle dot = new Circle(size);
                dot.setFill(Color.web("#42b996", 0.2 + Math.random() * 0.2));
                dot.setCenterX(x);
                dot.setCenterY(y);
                
                // Animation de pulsation sur seulement quelques points
                if (i % 3 == 0) {
                    Timeline pulse = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(dot.opacityProperty(), 0.1)),
                        new KeyFrame(Duration.seconds(4), new KeyValue(dot.opacityProperty(), 0.3))
                    );
                    pulse.setAutoReverse(true);
                    pulse.setCycleCount(Timeline.INDEFINITE);
                    pulse.play();
                }
                
                root.getChildren().add(dot);
            }
        } else {
            // Formes organiques subtiles pour le mode clair (réduit à 5)
            for (int i = 0; i < 5; i++) {
                double x = Math.random() * 600;
                double y = Math.random() * 650;
                double size = 100 + Math.random() * 150;
                
                Circle shape = new Circle(size);
                shape.setCenterX(x);
                shape.setCenterY(y);
                shape.setFill(Color.web("#36846d", 0.03 + Math.random() * 0.04));
                
                root.getChildren().add(shape);
            }
        }
        
        // Ajouter le fond principal en premier
        root.getChildren().add(0, background);
    }

    /**
     * Crée un bouton moderne avec effets de survol
     */
    private static Button createModernButton(String text, boolean isPrimary, boolean isDarkMode) {
        Button button = new Button(text);
        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        button.setPrefHeight(50);
        button.setPrefWidth(320);
        button.setMaxWidth(350);
        
        // Couleurs basées sur le thème et le type de bouton
        String buttonColor = isPrimary ? 
                (isDarkMode ? "#42b996" : "#36846d") : 
                (isDarkMode ? "rgba(60, 65, 75, 0.7)" : "rgba(240, 240, 240, 0.9)");
        
        String textColor = isPrimary ? 
                "white" : 
                (isDarkMode ? "#74c99e" : "#255a4b");
        
        String hoverColor = isPrimary ? 
                (isDarkMode ? "#74c99e" : "#255a4b") : 
                (isDarkMode ? "rgba(70, 75, 85, 0.8)" : "rgba(230, 230, 230, 0.95)");
        
        // Style de base du bouton
        button.setStyle(
                "-fx-background-color: " + buttonColor + ";" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-background-radius: 12px;" +
                "-fx-padding: 12px 20px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);" +
                "-fx-border-width: 0;");
        
        // Effets de survol
        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: " + hoverColor + ";" +
                    "-fx-text-fill: " + textColor + ";" +
                    "-fx-background-radius: 12px;" +
                    "-fx-padding: 12px 20px;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 4);" +
                    "-fx-border-width: 0;");
            
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), button);
            scaleUp.setToX(1.03);
            scaleUp.setToY(1.03);
            scaleUp.play();
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: " + buttonColor + ";" +
                    "-fx-text-fill: " + textColor + ";" +
                    "-fx-background-radius: 12px;" +
                    "-fx-padding: 12px 20px;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);" +
                    "-fx-border-width: 0;");
            
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), button);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();
        });
        
        return button;
    }
    
    /**
     * Ajoute un effet de clic au bouton
     */
    private static void animateButtonClick(Button button) {
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), button);
        scaleDown.setToX(0.95);
        scaleDown.setToY(0.95);
        
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), button);
        scaleUp.setToX(1.0);
        scaleUp.setToY(1.0);
        
        scaleDown.setOnFinished(e -> scaleUp.play());
        scaleDown.play();
    }

    public static void setGamePaused(boolean paused) {
        isGamePaused = paused;
    }
}