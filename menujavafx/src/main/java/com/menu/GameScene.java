package com.menu;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class GameScene {
    public static void show(Stage primaryStage) {
        VBox root = new VBox(20); // 20px d'espacement entre les éléments
        root.setAlignment(javafx.geometry.Pos.TOP_CENTER); // Aligner en haut au centre
        root.setStyle("-fx-padding: 20;"); // Ajouter un peu d'espace en haut

        // Texte
        Text label = new Text("Game Scene");
        label.setFont(Font.font("Arial", 24));
        label.setFill(Color.DARKBLUE);
        label.setStyle("-fx-font-weight: bold;");

        // Bouton "Retour"
        Button backButton = new Button("Retour au menu");
        backButton.setOnAction(e -> Menu.show(primaryStage));

        // Ajouter les éléments à la VBox
        root.getChildren().addAll(label, backButton);

        // Afficher la scène
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
    }
}
