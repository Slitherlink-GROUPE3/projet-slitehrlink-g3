package com.menu.javafx;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;


public class LoginScene {
    private static final String MAIN_COLOR = "#3A7D44";    // Vert principal
    private static final String SECONDARY_COLOR = "#F2E8CF"; // Beige clair
    private static final String ACCENT_COLOR = "#BC4749";   // Rouge-brique
    private static final String DARK_COLOR = "#386641";     // Vert foncé
    private static final String LIGHT_COLOR = "#A7C957";    // Vert clair


    public static void gestionUtilisateur(TextField usernameField, Label statusLabel ){

        String username = usernameField.getText().trim();
            
            if (username.isEmpty()) {
                statusLabel.setText("Veuillez entrer un pseudo");
                statusLabel.setTextFill(Color.web(ACCENT_COLOR));
                return;
            }
            
            if (username.length() < 3) {
                statusLabel.setText("Le pseudo doit contenir au moins 3 caractères");
                statusLabel.setTextFill(Color.web(ACCENT_COLOR));
                return;
            }
            
            boolean isExistingUser = UserManager.userExists(username);
            
            if (isExistingUser) {
                statusLabel.setText("Bon retour parmi nous, " + username + " !");
                statusLabel.setTextFill(Color.web(MAIN_COLOR));
            } else {
                if (UserManager.registerUser(username)) {
                    statusLabel.setText("Bienvenue, " + username + " !");
                    statusLabel.setTextFill(Color.web(MAIN_COLOR));
                } else {
                    statusLabel.setText("Erreur lors de l'enregistrement du pseudo");
                    statusLabel.setTextFill(Color.web(ACCENT_COLOR));
                    return;
                }
            }
            
            // Enregistrer l'utilisateur courant
            UserManager.setCurrentUser(username);

    }

    public static void show(Stage primaryStage) {
        // Conteneur principal
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + SECONDARY_COLOR + ", " + LIGHT_COLOR + " 70%);" +
            "-fx-padding: 40px;"
        );
        
        // Titre
        Label title = new Label("SLITHERLINK");
        title.setFont(Font.font("Montserrat", FontWeight.BOLD, 42));
        
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web(DARK_COLOR)),
            new Stop(0.5, Color.web(MAIN_COLOR)),
            new Stop(1, Color.web(DARK_COLOR))
        );
        title.setTextFill(gradient);
        
        // Effet d'ombre pour le titre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(DARK_COLOR, 0.5));
        shadow.setRadius(10);
        shadow.setOffsetY(3);
        
        Glow glow = new Glow(0.6);
        glow.setInput(shadow);
        title.setEffect(glow);
        
        // Conteneur pour le formulaire de connexion
        VBox loginBox = new VBox(15);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(30));
        loginBox.setMaxWidth(450);
        loginBox.setMinHeight(300);
        loginBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.8);" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 5);"
        );
        
        // Message de bienvenue
        Label welcomeLabel = new Label("BIENVENUE");
        welcomeLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 28));
        welcomeLabel.setTextFill(Color.web(MAIN_COLOR));
        
        Label subtitleLabel = new Label("Entrez votre pseudo pour commencer");
        subtitleLabel.setFont(Font.font("Calibri", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(Color.web(DARK_COLOR));
        
        // Champ de texte pour le pseudo
        TextField usernameField = new TextField();
        usernameField.setPromptText("Votre pseudo");
        usernameField.setMaxWidth(350);
        usernameField.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + LIGHT_COLOR + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-padding: 10;"
        );
        
        // Message d'état (pour les erreurs ou confirmations)
        Label statusLabel = new Label("");
        statusLabel.setFont(Font.font("Calibri", FontWeight.NORMAL, 14));
        statusLabel.setTextFill(Color.web(ACCENT_COLOR));
        statusLabel.setMinHeight(20);
        
        // Bouton de connexion
        Button loginButton = new Button("Continuer");
        loginButton.setFont(Font.font("Montserrat", FontWeight.BOLD, 16));
        loginButton.setPrefWidth(200);
        loginButton.setPrefHeight(45);
        loginButton.setTextFill(Color.WHITE);
        loginButton.setStyle(
            "-fx-background-color: " + MAIN_COLOR + ";" +
            "-fx-background-radius: 30;" +
            "-fx-border-color: " + DARK_COLOR + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 30;" +
            "-fx-cursor: hand;"
        );
        
        // Effet d'ombre pour le bouton
        DropShadow buttonShadow = new DropShadow();
        buttonShadow.setColor(Color.web("#000000", 0.2));
        buttonShadow.setRadius(5);
        buttonShadow.setOffsetY(2);
        loginButton.setEffect(buttonShadow);
        
        // Action du bouton
        loginButton.setOnAction(e -> {

            gestionUtilisateur(usernameField, statusLabel);
            
            // Transition vers le menu principal après un court délai
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(event -> Menu.show(primaryStage));
            pause.play();
        });
        
        // Animations de survol pour le bouton
        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle(
                "-fx-background-color: " + DARK_COLOR + ";" +
                "-fx-background-radius: 30;" +
                "-fx-border-color: " + MAIN_COLOR + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 30;" +
                "-fx-cursor: hand;"
            );
        });
        
        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle(
                "-fx-background-color: " + MAIN_COLOR + ";" +
                "-fx-background-radius: 30;" +
                "-fx-border-color: " + DARK_COLOR + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 30;" +
                "-fx-cursor: hand;"
            );
        });

        usernameField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginButton.fire(); // Déclenche l'action du bouton de login
            }
        });
        
        // Assembler les composants
        loginBox.getChildren().addAll(welcomeLabel, subtitleLabel, usernameField, statusLabel, loginButton);
        
        // Centrer le formulaire de login
        StackPane centerPane = new StackPane(loginBox);
        centerPane.setPadding(new Insets(20, 0, 20, 0));
        
        // Assembler la page
        root.getChildren().addAll(title, centerPane);
        
        // Créer la scène
        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Slitherlink - Connexion");
        primaryStage.show();
        
        // Animation de transition
        root.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}