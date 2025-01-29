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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class hintScene {
    public String TitreIndice = "Le titre de mon indice";
    public static String TexteIndice = "Le texte de mon indice";
    public ImageView imageView;

    public hintScene() {
        Image uneImage = new Image(getClass().getResourceAsStream("/bot.png"));
        imageView = new ImageView();
        imageView.setImage(uneImage);
    }

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
        VBox hintBox = new VBox();
        // Largeur responsive
        hintBox.prefWidthProperty().bind(mainLayout.widthProperty().divide(3));
        hintBox.setStyle("-fx-background-color: lightgrey; -fx-padding: 10;");

        // Texte à afficher dans la boîte de texte
        Text hintTitle = new Text(new hintScene().TitreIndice);
        hintTitle.setFont(Font.font("Arial", 20));
        hintTitle.setFill(Color.BLACK);
        hintTitle.setStyle("-fx-alignment: center;");


        // Texte à afficher dans la boîte de texte
        Text hintText = new Text(TexteIndice);
        hintText.setFont(Font.font("Arial", 16));
        hintText.setFill(Color.BLACK);

        // Ajouter le titre et le texte à la VBox de la boîte de texte
        hintBox.getChildren().addAll(hintTitle, hintText);

        // Ajouter l'image à la VBox de la boîte de texte
        hintScene hint = new hintScene();
        hint.imageView.setFitWidth(100); // Définir la largeur de l'image
        hint.imageView.setFitHeight(100); // Définir la hauteur de l'image

        HBox textHintBox = new HBox();
        textHintBox.getChildren().addAll(hintText);
        textHintBox.prefWidthProperty().bind(hintBox.widthProperty().divide(1.5));
        textHintBox.setStyle("-fx-background-color: red; -fx-padding: 10;");

        hintBox.getChildren().add(hint.imageView);

        hintBox.getChildren().add(textHintBox);
        // Ajouter les VBox au HBox principal
        mainLayout.getChildren().addAll(buttonBox, hintBox);

        // Afficher la scène
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
    }
}