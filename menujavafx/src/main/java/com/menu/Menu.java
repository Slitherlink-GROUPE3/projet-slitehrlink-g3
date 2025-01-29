package com.menu;
import com.menu.SettingScene;
import com.menu.javafx.hintScene;

import javafx.scene.control.Label;  // Pour le titre "Mode aventure"
import javafx.scene.text.Font;       // Pour changer la police du texte
import javafx.scene.paint.Color;     // Pour changer la couleur du texte
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Stack;
import javafx.scene.control.Button;  // Pour les boutons
import javafx.scene.layout.VBox;     // Pour organiser les boutons verticalement
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos; // Pour aligner les boutons dans VBox


public class Menu extends Application {
    private static final Stack<Scene> sceneHistory = new Stack<>(); // Stocke l'historique des scènes

    @Override
    public void start(Stage primaryStage) {
        show(primaryStage);
    }

      public static void show(Stage primaryStage) {
        // --- Barre verte en haut ---
        Label title = new Label("Slither Link");
        title.setFont(Font.font("Arial", 28));
        title.setTextFill(Color.BLACK);
        title.setStyle("-fx-font-weight: bold;");

        StackPane topBar = new StackPane(title);
        topBar.setStyle("-fx-background-color: #A5B8A5; -fx-padding: 20px;");
        topBar.setPrefHeight(80);

        // --- Création des boutons ---
        Button adventureButton = ButtonFactory.createStyledButton("Mode Aventure");
        Button freeModeButton = ButtonFactory.createStyledButton("Mode Libre");
        Button settingsButton = ButtonFactory.createStyledButton("Settings");
        Button hintButton = ButtonFactory.createStyledButton("Indice");
        Button exitButton = ButtonFactory.createStyledButton("Exit");

        // Actions des boutons
        adventureButton.setOnAction(e -> GameScene.show(primaryStage));
        settingsButton.setOnAction(e -> SettingScene.show(primaryStage)); 
        hintButton.setOnAction(e -> hintScene.show(primaryStage));
        exitButton.setOnAction(e -> primaryStage.close());

        // --- Organisation des boutons dans une VBox ---
        VBox menuBox = new VBox(15, adventureButton, freeModeButton, settingsButton, hintButton, exitButton);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setStyle("-fx-padding: 20px;");

        StackPane centerPane = new StackPane(menuBox);
        centerPane.setStyle("-fx-background-color: #E5D5B0; -fx-padding: 20px;");
        centerPane.setPrefSize(800, 500);

        // --- Layout principal ---
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(centerPane);

        // --- Création de la scène ---
        Scene scene = new Scene(root, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Slither Link Menu");
        primaryStage.show();
    }


    // Permet de changer de scène en gardant un historique
    public static void changeScene(Stage primaryStage, Scene newScene) {
        if (primaryStage.getScene() != null) {
            sceneHistory.push(primaryStage.getScene());  // Sauvegarde la scène actuelle
        }
        primaryStage.setScene(newScene);
    }

    // Permet de revenir en arrière
    public static void goBack(Stage primaryStage) {
        if (!sceneHistory.isEmpty()) {
            primaryStage.setScene(sceneHistory.pop()); // Charge la scène précédente
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
