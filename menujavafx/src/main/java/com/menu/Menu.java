package com.menu;

import com.menu.SettingScene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Stack;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;
import javafx.scene.image.Image; // Importez cette classe pour l'icône

public class Menu extends Application {
private static final Stack<Scene> sceneHistory = new Stack<>();

@Override
public void start(Stage primaryStage) {
    // Ajoutez l'icône à votre application ici
    try {
        Image icon = new Image(getClass().getResourceAsStream("/resources/")); // Ajustez le chemin
        primaryStage.getIcons().add(icon);
    } catch (Exception e) {
        System.out.println("Impossible de charger l'icône: " + e.getMessage());
    }
    
    show(primaryStage);
}

public static void show(Stage primaryStage) {
    // Si l'icône n'est pas encore définie et que c'est une nouvelle fenêtre
    if (primaryStage.getIcons().isEmpty()) {
        try {
            Image icon = new Image(Menu.class.getResourceAsStream("/resources/icon.png")); // Ajustez le chemin
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Impossible de charger l'icône: " + e.getMessage());
        }
    }
    
    // Le reste de votre code reste inchangé
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
    Button tutorialButton = ButtonFactory.createStyledButton("Tutorial");
    Button exitButton = ButtonFactory.createStyledButton("Exit");
    // Actions des boutons
    adventureButton.setOnAction(e -> GameScene.show(primaryStage));
    settingsButton.setOnAction(e -> SettingScene.show(primaryStage));
    tutorialButton.setOnAction(e -> TechniquesScene.show(primaryStage));
    exitButton.setOnAction(e -> primaryStage.close());
    // --- Organisation des boutons dans une VBox ---
    VBox menuBox = new VBox(15, adventureButton, freeModeButton, settingsButton, tutorialButton, exitButton);
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
    Scene scene = new Scene(root, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Slither Link Menu");
    primaryStage.show();
}

// Le reste de votre code reste inchangé
public static void changeScene(Stage primaryStage, Scene newScene) {
    if (primaryStage.getScene() != null) {
        sceneHistory.push(primaryStage.getScene());
    }
    primaryStage.setScene(newScene);
}

public static void goBack(Stage primaryStage) {
    if (!sceneHistory.isEmpty()) {
        primaryStage.setScene(sceneHistory.pop());
    }
}

public static void main(String[] args) {
    launch(args);
}
}