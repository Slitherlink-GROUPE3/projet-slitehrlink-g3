package com.menu.javafx;

import com.menu.Menu;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
/*import javafx.beans.binding.Bindings;
import java.nio.file.Paths;*/
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;

public class hintScene {
    public String TitreIndice = "Le titre de mon indice";
    public static String TexteIndice = "Le texte de mon indice je ne sais pas ce que je veux ecrire mais je veux faire un indice plus long";
    public ImageView imageView;
    public ImageView bottomImageView;
    /*private String bottomImagePath = "03Res.png"; // Juste le nom du fichier*/
    
    public hintScene() {
        // Image à côté du texte
        try {
            // Obtenir le chemin absolu vers le projet
            File projectDir = new File(System.getProperty("user.dir"));
            String basePath = projectDir.getAbsolutePath();
            
            // Construire le chemin vers le dossier src/main/ressources
            File ressourcesDir = new File(basePath, "src/main/ressources");
            File imageFile = new File(ressourcesDir, "bot.png");
            
            if (imageFile.exists()) {
                Image uneImage = new Image(new FileInputStream(imageFile));
                imageView = new ImageView();
                imageView.setImage(uneImage);
            } else {
                System.err.println("Fichier image non trouvé: " + imageFile.getAbsolutePath());
                imageView = new ImageView(); // Créer un ImageView vide
            }
        } catch (FileNotFoundException e) {
            System.err.println("Erreur lors du chargement de l'image: bot.png");
            e.printStackTrace();
            imageView = new ImageView(); // Créer un ImageView vide pour éviter NullPointerException
        }
        
        // Image en bas
        loadBottomImage("03Res.png"); // Utiliser directement le nom du fichier
    }
    
    // Méthode pour charger l'image en bas avec un chemin spécifique
    public void loadBottomImage(String imagePath) {
        try {
            // Obtenir le chemin absolu vers le projet
            File projectDir = new File(System.getProperty("user.dir"));
            String basePath = projectDir.getAbsolutePath();
            
            // Construire le chemin vers le dossier src/main/ressources
            File ressourcesDir = new File(basePath, "src/main/ressources");
            
            // Construire le chemin du fichier image
            File imageFile;
            if (imagePath.startsWith("/")) {
                imageFile = new File(ressourcesDir, imagePath);
            } else {
                imageFile = new File(ressourcesDir, imagePath);
            }
            
            if (imageFile.exists()) {
                Image bottomImage = new Image(new FileInputStream(imageFile));
                bottomImageView = new ImageView();
                bottomImageView.setImage(bottomImage);
                bottomImageView.setPreserveRatio(true);
                bottomImageView.setFitWidth(300); // Largeur par défaut
            } else {
                System.err.println("Fichier image non trouvé: " + imageFile.getAbsolutePath());
                bottomImageView = new ImageView(); // Créer un ImageView vide
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image: " + imagePath);
            e.printStackTrace();
            bottomImageView = new ImageView(); // Créer un ImageView vide
        }
    }

    public static void show(Stage primaryStage) {
        show(primaryStage, "03Res.png"); // Simplifier le nom du fichier
    }


    public static void show(Stage primaryStage, String bottomImagePath) {
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

        // VBox pour la boîte de texte globale
        VBox hintBox = new VBox(10);
        
        // Définir une largeur préférée
        hintBox.setPrefWidth(300);
        hintBox.setMinWidth(200);  // Définir une largeur minimale
        
        // Lier la largeur maximale à 1/3 de la largeur de la scène
        hintBox.maxWidthProperty().bind(primaryStage.widthProperty().divide(3));
        
        hintBox.setStyle("-fx-background-color: lightgrey; -fx-padding: 10;");
        
        // Permettre à hintBox de se redimensionner avec la fenêtre
        HBox.setHgrow(hintBox, Priority.ALWAYS);

        // Instanciation de hintScene
        hintScene hint = new hintScene();
        
        // Si un chemin d'image est spécifié, charger cette image
        if (bottomImagePath != null && !bottomImagePath.isEmpty()) {
            hint.loadBottomImage(bottomImagePath);
        }

        // Texte à afficher dans la boîte de texte (titre)
        Text hintTitle = new Text(hint.TitreIndice);
        hintTitle.setFont(Font.font("Arial", 20));
        hintTitle.setFill(Color.BLACK);
        
        // Centre le titre
        VBox titleBox = new VBox(hintTitle);
        titleBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Texte de l'indice
        Text hintText = new Text(TexteIndice);
        hintText.setFont(Font.font("Arial", 16));
        hintText.setFill(Color.BLACK);

        // Conteneur pour le texte uniquement
        VBox hintTextBox = new VBox();
        hintTextBox.getChildren().add(hintText);
        hintTextBox.setStyle("-fx-background-color: red; -fx-padding: 10;");
        
        // Définir des contraintes pour hintTextBox
        hintTextBox.setPrefWidth(200);
        hintTextBox.setMinWidth(100);
        hintTextBox.setMaxWidth(Double.MAX_VALUE);
        
        // Configuration de l'image avec taille fixe
        hint.imageView.setFitWidth(100);
        hint.imageView.setFitHeight(100);
        hint.imageView.setPreserveRatio(true);

        // HBox pour placer l'image et le conteneur de texte sur la même ligne
        HBox topHorizontalBox = new HBox(10); // 10px d'espacement entre l'image et le texte
        topHorizontalBox.getChildren().addAll(hint.imageView, hintTextBox);
        topHorizontalBox.setStyle("-fx-padding: 10;");
        
        // Important: Permettre à horizontalBox de se redimensionner
        topHorizontalBox.setMaxWidth(Double.MAX_VALUE);
        
        // Important: Assurer que hintTextBox prend tout l'espace disponible
        HBox.setHgrow(hintTextBox, Priority.ALWAYS);
        
        // Configurer le wrapping du texte pour qu'il s'adapte dynamiquement
        hintText.wrappingWidthProperty().bind(
            hintTextBox.widthProperty().subtract(20)
        );
        
        // Configuration de l'image du bas
        // Lier la largeur maximale de l'image en bas à la largeur de hintBox moins padding
        hint.bottomImageView.fitWidthProperty().bind(
            hintBox.widthProperty().subtract(20)
        );
        
        // Centrer l'image
        HBox bottomImageContainer = new HBox();
        bottomImageContainer.setAlignment(javafx.geometry.Pos.CENTER);
        bottomImageContainer.getChildren().add(hint.bottomImageView);
        
        // Ajout du titre, du HBox supérieur et de l'image du bas à la VBox principale
        hintBox.getChildren().addAll(titleBox, topHorizontalBox, bottomImageContainer);

        // Ajouter hintBox et buttonBox au conteneur principal
        mainLayout.getChildren().addAll(buttonBox, hintBox);

        // Afficher la scène
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Indice");
    }
}