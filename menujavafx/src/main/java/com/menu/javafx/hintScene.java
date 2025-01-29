package com.menu.javafx;

import com.menu.Menu;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class hintScene {
    public static void show(Stage primaryStage) {
        // Conteneur principal horizontal
        HBox mainLayout = new HBox(20); // 20px d'espacement entre les éléments
        mainLayout.setStyle("-fx-padding: 20;"); // Ajouter un peu d'espace en haut

        // VBox pour les boutons
        VBox buttonBox = new VBox(20); // 20px d'espacement entre les éléments
        buttonBox.setAlignment(javafx.geometry.Pos.TOP_CENTER); // Aligner en haut au centre


        // Bouton "Retour"
        Button backButton = new Button("Retour au menu");
        backButton.setOnAction(e -> Menu.show(primaryStage));


        // Ajouter les éléments à la VBox des boutons
        buttonBox.getChildren().addAll(backButton);

        /////////////////////////  Partie Importante  /////////////////////////


        // VBox pour la boîte de texte
        VBox textBox = new VBox();
        textBox.setPrefWidth(200); // Largeur préférée
        textBox.setStyle("-fx-background-color: lightgrey; -fx-padding: 10;");

        // Texte à afficher dans la boîte de texte
        Text hintText = new Text("Ceci est un texte d'indice.");
        hintText.setFont(Font.font("Arial", 16));
        hintText.setFill(Color.BLACK);

        // Ajouter le texte à la VBox de la boîte de texte
        textBox.getChildren().add(hintText);

        // Ajouter les VBox au HBox principal
        mainLayout.getChildren().addAll(buttonBox, textBox);

        // Afficher la scène
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
    }
}