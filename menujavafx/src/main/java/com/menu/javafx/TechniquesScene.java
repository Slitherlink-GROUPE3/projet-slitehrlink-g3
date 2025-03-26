package com.menu.javafx;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.geometry.Pos;

public class TechniquesScene {
    public static void show(Stage primaryStage) {
        VBox root = new VBox(20); // 20px d'espacement entre les éléments
        root.setAlignment(Pos.TOP_CENTER); // Aligner en haut au centre
        root.setStyle("-fx-padding: 20;"); // Ajouter un peu d'espace en haut

        // Texte principal
        Text label = new Text("Techniques Scene");
        label.setFont(Font.font("Arial", 24));
        label.setFill(Color.DARKBLUE);
        label.setStyle("-fx-font-weight: bold;");

        // Texte de description du jeu
        Text description = new Text(
            "Slitherlink (aussi connu comme Clôtures et Loop de Loop) est un casse-tête logique avec des règles simples et des solutions complexes.\n\n"
            + "Les règles sont simples :\n"
            + "- Tracez des lignes entre les points pour former une boucle unique.\n"
            + "- La boucle ne doit ni croiser ni faire de branche.\n"
            + "- Les nombres indiquent combien de lignes entourent chaque case.\n"
            + "- Cliquez gauche pour tracer une ligne, cliquez droit pour marquer un X."
        );
        description.setFont(Font.font("Arial", 14));
        description.setWrappingWidth(600); // Limite la largeur du texte pour éviter qu'il soit trop large

        // Bouton "Retour"
        Button backButton = new Button("Retour au menu");
        backButton.setOnAction(e -> Menu.show(primaryStage));

        // Bouton "Tutoriel"
        Button tutorialButton = new Button("Voir le tutoriel");
        
        // Ajout du Tooltip au bouton
        Tooltip tutorialTooltip = new Tooltip("Cliquez pour voir un tutoriel sur Slitherlink !");
        tutorialButton.setTooltip(tutorialTooltip);

        // Ajout d'une action au bouton (Exemple : Afficher une autre scène ou une alerte)
        tutorialButton.setOnAction(e -> {
            Text tutorialText = new Text("Le tutoriel détaillé sera ajouté ici...");
            tutorialText.setFont(Font.font("Arial", 16));
            tutorialText.setFill(Color.GREEN);

            VBox tutorialRoot = new VBox(20, tutorialText, backButton);
            tutorialRoot.setAlignment(Pos.CENTER);
            Scene tutorialScene = new Scene(tutorialRoot, 800, 600);
            primaryStage.setScene(tutorialScene);
        });

        // Ajouter les éléments à la VBox
        root.getChildren().addAll(label, description, tutorialButton, backButton);

        // Afficher la scène
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
    }
}