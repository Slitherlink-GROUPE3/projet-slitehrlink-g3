package com.menu;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Stack;

public class Menu extends Application {
    private static final Stack<Scene> sceneHistory = new Stack<>(); // Stocke l'historique des scènes

    @Override
    public void start(Stage primaryStage) {
        show(primaryStage);
    }

    public static void show(Stage primaryStage) {
        TitleComponent titleComponent = new TitleComponent();
        MenuBoxComponent menuBoxComponent = new MenuBoxComponent();
        GridComponent gridComponent = new GridComponent();

        HBox root = new HBox(20, menuBoxComponent.getMenuBox(), gridComponent.getGridPane());
        root.setStyle("-fx-background-color: #B0C4DE; -fx-padding: 20;");
        root.setAlignment(javafx.geometry.Pos.CENTER);

        VBox mainLayout = new VBox(10, titleComponent.getTitle(), root);
        mainLayout.setAlignment(javafx.geometry.Pos.CENTER);

        Scene scene = new Scene(mainLayout, 800, 600);
        
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
