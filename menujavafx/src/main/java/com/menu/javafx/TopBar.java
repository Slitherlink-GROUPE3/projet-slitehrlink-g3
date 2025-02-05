package com.menu.javafx;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.effect.DropShadow;
import javafx.geometry.Pos;
import com.menu.ButtonFactory;


// Classe TopBar
public class TopBar {
    private Stage primaryStage;
    private String pseudoJoueur;
    private String niveau;
    private String difficulte;

    TopBar(Stage primaryStage, String pseudoJoueur, String niveau, String difficulte) {
        this.primaryStage = primaryStage;
        this.pseudoJoueur = pseudoJoueur;
        this.niveau = niveau;
        this.difficulte = difficulte;
    }

    public String getPseudoJoueur(){
        return this.pseudoJoueur;
    }

    public String getNiveau() {
        return this.niveau;
    }

    public String getDifficulte() {
        return this.difficulte;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    // Potentiellement a mettre dans une classe plus général car peut etre utlisé pas seulement pour la topbar
    private void adjustFontSize(Scene scene, Text... texts) {
        double baseWidth = 800;  // Largeur de référence
        double baseFontSize = 10; // Taille de police de base

        scene.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double scaleFactor = newWidth.doubleValue() / baseWidth;
            double newFontSize = baseFontSize * scaleFactor;
            for (Text text : texts) {
                text.setFont(Font.font("Arial", newFontSize));
            }
        });
    }


    public HBox createTopBar(Scene scene) {
        HBox topBar = new HBox();
        topBar.setStyle("-fx-background-color: #4a90e2; -fx-padding: 10px 20px; -fx-background-radius: 20px;");
        topBar.setPrefWidth(this.getPrimaryStage().getHeight() / 10);
        topBar.setTranslateY(primaryStage.getHeight() / 100);
        topBar.setAlignment(Pos.CENTER);

        HBox textInfos = new HBox();
        textInfos.prefWidthProperty().bind(topBar.widthProperty().divide(2));
        textInfos.setAlignment(Pos.CENTER);

        Text textPseudo = new Text("Joueur : " + this.pseudoJoueur);
        Text textNiveau = new Text("Niveau : " + this.niveau);
        Text textDifficulte = new Text("Difficulté : " + this.difficulte);

        HBox textOptions = new HBox();
        textOptions.prefWidthProperty().bind(topBar.widthProperty().subtract(textInfos.prefWidthProperty()));
        textOptions.setAlignment(Pos.CENTER_RIGHT);

        Text textChrono = new Text("00:00");
        textChrono.setFill(Color.WHITE);
        textChrono.setEffect(new DropShadow(10, Color.BLACK));

        // Fonction qui rend responsive la police d'écriture
        adjustFontSize(scene, textPseudo, textNiveau, textDifficulte, textChrono);

        textPseudo.setFill(Color.WHITE);
        textNiveau.setFill(Color.WHITE);
        textDifficulte.setFill(Color.WHITE);

        textPseudo.setEffect(new DropShadow(10, Color.BLACK));
        textNiveau.setEffect(new DropShadow(10, Color.BLACK));
        textDifficulte.setEffect(new DropShadow(10, Color.BLACK));

        

        Button pauseButton = ButtonFactory.createAnimatedButton("Pause");
        pauseButton.setStyle("-fx-background-color:rgb(255, 255, 255)");
        pauseButton.setOnMouseEntered(e -> pauseButton.setStyle("-fx-background-color:rgb(255, 255, 255); -fx-text-fill: black;"));
        pauseButton.setOnMouseExited(e -> pauseButton.setStyle("-fx-background-color:rgb(255, 255, 255); -fx-text-fill: black;"));
        ImageView pauseImage = new ImageView(new Image(getClass().getResource("/assets/pause.png").toExternalForm()));
        pauseImage.setFitHeight(30);
        pauseImage.setFitWidth(30);
        
        Button retryButton = ButtonFactory.createAnimatedButton("Réessayer");
        retryButton.setStyle("-fx-background-color:rgb(255, 255, 255)");
        retryButton.setOnMouseEntered(e -> retryButton.setStyle("-fx-background-color:rgb(255, 255, 255); -fx-text-fill: black;"));
        retryButton.setOnMouseExited(e -> retryButton.setStyle("-fx-background-color:rgb(255, 255, 255); -fx-text-fill: black;"));
        ImageView retryImage = new ImageView(new Image(getClass().getResource("/assets/retry.png").toExternalForm()));
        retryImage.setFitHeight(30);
        retryImage.setFitWidth(30);
        
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        Region spacer3 = new Region();
        Region spacer4 = new Region();
        Region spacer5 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        HBox.setHgrow(spacer3, Priority.ALWAYS);
        HBox.setHgrow(spacer4, Priority.ALWAYS);
        HBox.setHgrow(spacer5, Priority.ALWAYS);

        textInfos.getChildren().addAll(textPseudo, spacer1, textNiveau, spacer2, textDifficulte);
        textOptions.getChildren().addAll(spacer3, textChrono, spacer4, retryButton, spacer5, pauseButton);

        topBar.getChildren().addAll(textInfos, textOptions);

        return topBar;
    }
}