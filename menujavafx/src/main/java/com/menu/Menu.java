package com.menu;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Stack;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Reflection;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Tooltip;

public class Menu extends Application {
    private static final Stack<Scene> sceneHistory = new Stack<>();
    private static final String MAIN_COLOR = "#3A7D44"; // Vert principal
    private static final String SECONDARY_COLOR = "#F2E8CF"; // Beige clair
    private static final String ACCENT_COLOR = "#BC4749"; // Rouge-brique
    private static final String DARK_COLOR = "#386641"; // Vert foncé
    private static final String LIGHT_COLOR = "#A7C957"; // Vert clair

    @Override
    public void start(Stage primaryStage) {
        // Configuration de l'icône
        try {
            Image icon = new Image(getClass().getResourceAsStream("/resources/amir.jpeg"));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Impossible de charger l'icône: " + e.getMessage());
        }
        
        // Configuration générale de la fenêtre
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(700);
        primaryStage.setTitle("Slither Link - Puzzle Game");
        
        show(primaryStage);
    }

    public static void show(Stage primaryStage) {
        // Vérification de l'icône
        if (primaryStage.getIcons().isEmpty()) {
            try {
                Image icon = new Image(Menu.class.getResourceAsStream("/resources/amir.jpeg"));
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                System.out.println("Impossible de charger l'icône: " + e.getMessage());
            }
        }
        
        // --- En-tête stylisé ---
        // Titre avec effets
        Label title = new Label("SLITHER LINK");
        title.setFont(Font.font("Montserrat", FontWeight.BOLD, 42));
        
        // Effet de dégradé pour le texte
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web(DARK_COLOR)),
            new Stop(0.5, Color.web(MAIN_COLOR)),
            new Stop(1, Color.web(DARK_COLOR))
        );
        title.setTextFill(gradient);
        
        // Ajouter des effets visuels au titre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(DARK_COLOR, 0.5));
        shadow.setRadius(10);
        shadow.setOffsetY(3);
        
        Glow glow = new Glow(0.6);
        glow.setInput(shadow);
        title.setEffect(glow);
        
        // Sous-titre
        Label subtitle = new Label("Connect the dots, solve the puzzle");
        subtitle.setFont(Font.font("Calibri", FontWeight.LIGHT, 18));
        subtitle.setTextFill(Color.web(DARK_COLOR));
        
        VBox titleBox = new VBox(8, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);
        
        // Logo ou image décorative (en supposant que vous avez une image)
        ImageView logoView = null;
        try {
            Image logo = new Image(Menu.class.getResourceAsStream("/resources/logo.png"));
            logoView = new ImageView(logo);
            logoView.setFitHeight(60);
            logoView.setPreserveRatio(true);
        } catch (Exception e) {
            // Créer un logo simple si l'image n'est pas disponible
            Rectangle logoPlaceholder = new Rectangle(60, 60);
            logoPlaceholder.setFill(Color.web(ACCENT_COLOR));
            logoPlaceholder.setArcWidth(15);
            logoPlaceholder.setArcHeight(15);
            logoView = new ImageView();
        }
        
        HBox headerBox = new HBox(20, logoView, titleBox);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(25));
        headerBox.setStyle(
            "-fx-background-color: linear-gradient(to bottom, " + SECONDARY_COLOR + ", " + SECONDARY_COLOR + " 90%, rgba(0,0,0,0.05));" +
            "-fx-border-color: transparent transparent " + LIGHT_COLOR + " transparent;" +
            "-fx-border-width: 0 0 2 0;"
        );
        
        // --- Création des boutons avec animation ---
        Button adventureButton = createAnimatedButton("Mode Aventure", "🧩");
        Button freeModeButton = createAnimatedButton("Mode Libre", "🔓");
        Button settingsButton = createAnimatedButton("Paramètres", "⚙️");
        Button tutorialButton = createAnimatedButton("Tutoriel", "📖");
        Button exitButton = createAnimatedButton("Quitter", "🚪");

        //Explique l'utilité du boutton lorsqu'on met la souris dessus
        Tooltip tooltipTutorialButton = new Tooltip("Affiche le tutoriel !");
        Tooltip.install(tutorialButton,tooltipTutorialButton);
        



        
        // Actions des boutons
        adventureButton.setOnAction(e -> {
            animateButtonClick(adventureButton);
            GameScene.show(primaryStage);
        });
        
        freeModeButton.setOnAction(e -> {
            animateButtonClick(freeModeButton);
            // Implémentez votre action ici
        });
        
        settingsButton.setOnAction(e -> {
            animateButtonClick(settingsButton);
            SettingScene.show(primaryStage);
        });
        
        tutorialButton.setOnAction(e -> {
            animateButtonClick(tutorialButton);
            TechniquesScene.show(primaryStage);
        });
        
        exitButton.setOnAction(e -> {
            animateButtonClick(exitButton);
            // Ajout d'un effet de fondu avant de fermer
            FadeTransition fadeOut = new FadeTransition(Duration.millis(600), primaryStage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> primaryStage.close());
            fadeOut.play();
        });
        
        // --- Organisation des boutons dans une VBox stylisée ---
        VBox menuBox = new VBox(18, 
            adventureButton, 
            freeModeButton, 
            settingsButton, 
            tutorialButton, 
            exitButton
        );
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(40, 20, 40, 20));
        menuBox.setMaxWidth(400);
        menuBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.8);" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 5);"
        );
        
        // Arrière-plan décoratif
        StackPane centerPane = new StackPane(menuBox);
        centerPane.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + SECONDARY_COLOR + ", " + LIGHT_COLOR + " 70%);" +
            "-fx-background-radius: 0;" +
            "-fx-padding: 20px;" +
            // Motif géométrique subtil en arrière-plan
            "-fx-background-image: url('pattern.png');" +
            "-fx-background-repeat: repeat;" +
            "-fx-background-opacity: 0.1;"
        );
        
        // --- Pied de page ---
        Label footerLabel = new Label("© 2025 Slither Link - Tous droits réservés");
        footerLabel.setTextFill(Color.web(DARK_COLOR, 0.7));
        footerLabel.setFont(Font.font("Calibri", 12));
        
        StackPane footer = new StackPane(footerLabel);
        footer.setPadding(new Insets(10));
        footer.setStyle("-fx-background-color: " + SECONDARY_COLOR + ";");
        
        // --- Layout principal ---
        BorderPane root = new BorderPane();
        root.setTop(headerBox);
        root.setCenter(centerPane);
        root.setBottom(footer);
        
        // Ajouter un effet de transition à l'ouverture
        root.setOpacity(0);
        
        // --- Création de la scène ---
        Scene scene = new Scene(root, 900, 700);
        
        // Ajout d'une feuille de style CSS (optionnelle)
        //scene.getStylesheets().add(Menu.class.getResource("/styles/menu-style.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Animation d'entrée
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private static Button createAnimatedButton(String text, String icon) {
        // Création du bouton avec icône et texte
        Button button = new Button(icon + " " + text);
        button.setFont(Font.font("Calibri", FontWeight.BOLD, 16));
        button.setTextFill(Color.web(DARK_COLOR));
        button.setPrefWidth(300);
        button.setPrefHeight(55);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(10, 20, 10, 20));
        
        // Style visuel du bouton
        button.setStyle(
            "-fx-background-color: " + SECONDARY_COLOR + ";" +
            "-fx-background-radius: 30;" +
            "-fx-border-color: " + MAIN_COLOR + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 30;" +
            "-fx-cursor: hand;"
        );
        
        // Effet d'ombre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.2));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        button.setEffect(shadow);
        
        // Animations au survol
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
            
            // Animation de mise à l'échelle
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
                "-fx-cursor: hand;"
            );
            button.setTextFill(Color.web(DARK_COLOR));
            
            // Animation de retour à l'échelle normale
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
        
        return button;
    }
    
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

    public static void changeScene(Stage primaryStage, Scene newScene) {
        if (primaryStage.getScene() != null) {
            sceneHistory.push(primaryStage.getScene());
        }
        
        // Animation de transition entre scenes
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), primaryStage.getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        fadeOut.setOnFinished(e -> {
            primaryStage.setScene(newScene);
            
            // Animation d'entrée pour la nouvelle scène
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newScene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        
        fadeOut.play();
    }

    public static void goBack(Stage primaryStage) {
        if (!sceneHistory.isEmpty()) {
            Scene previousScene = sceneHistory.pop();
            
            // Animation de transition pour le retour
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), primaryStage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            
            fadeOut.setOnFinished(e -> {
                primaryStage.setScene(previousScene);
                
                // Animation d'entrée pour la scène précédente
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), previousScene.getRoot());
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            
            fadeOut.play();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}