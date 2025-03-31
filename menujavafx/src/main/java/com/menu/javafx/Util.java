package com.menu.javafx;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Util {
    public static void animateButtonClick(Button button) {
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), button);
        scaleDown.setToX(0.95);
        scaleDown.setToY(0.95);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), button);
        scaleUp.setToX(1.0);
        scaleUp.setToY(1.0);

        scaleDown.setOnFinished(e -> scaleUp.play());
        scaleDown.play();
    }

    public static Button createStyledButton(String text, boolean isPrimary, String MAIN_COLOR, String DARK_COLOR, String SECONDARY_COLOR ) {
        Button button = new Button(text);
        button.setFont(Font.font("Montserrat", FontWeight.BOLD, 16));

        if (isPrimary) {
            button.setTextFill(Color.WHITE);
            button.setStyle(
                    "-fx-background-color: " + MAIN_COLOR + ";" +
                            "-fx-background-radius: 30;" +
                            "-fx-border-color: " + DARK_COLOR + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 30;" +
                            "-fx-padding: 10 20;" +
                            "-fx-cursor: hand;");
        } else {
            button.setTextFill(Color.web(DARK_COLOR));
            button.setStyle(
                    "-fx-background-color: " + SECONDARY_COLOR + ";" +
                            "-fx-background-radius: 30;" +
                            "-fx-border-color: " + MAIN_COLOR + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 30;" +
                            "-fx-padding: 10 20;" +
                            "-fx-cursor: hand;");
        }

        // Effet d'ombre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.2));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        button.setEffect(shadow);

        // Animations au survol
        button.setOnMouseEntered(e -> {
            if (isPrimary) {
                button.setStyle(
                        "-fx-background-color: " + DARK_COLOR + ";" +
                                "-fx-background-radius: 30;" +
                                "-fx-border-color: " + MAIN_COLOR + ";" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 30;" +
                                "-fx-padding: 10 20;" +
                                "-fx-cursor: hand;");
            } else {
                button.setStyle(
                        "-fx-background-color: " + MAIN_COLOR + ";" +
                                "-fx-background-radius: 30;" +
                                "-fx-border-color: " + DARK_COLOR + ";" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 30;" +
                                "-fx-padding: 10 20;" +
                                "-fx-cursor: hand;");
                button.setTextFill(Color.WHITE);
            }

            // Animation de mise à l'échelle
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
        });

        button.setOnMouseExited(e -> {
            if (!button.isDisabled()) {
                if (isPrimary) {
                    button.setStyle(
                            "-fx-background-color: " + MAIN_COLOR + ";" +
                                    "-fx-background-radius: 30;" +
                                    "-fx-border-color: " + DARK_COLOR + ";" +
                                    "-fx-border-width: 2;" +
                                    "-fx-border-radius: 30;" +
                                    "-fx-padding: 10 20;" +
                                    "-fx-cursor: hand;");
                    button.setTextFill(Color.WHITE);
                } else {
                    button.setStyle(
                            "-fx-background-color: " + SECONDARY_COLOR + ";" +
                                    "-fx-background-radius: 30;" +
                                    "-fx-border-color: " + MAIN_COLOR + ";" +
                                    "-fx-border-width: 2;" +
                                    "-fx-border-radius: 30;" +
                                    "-fx-padding: 10 20;" +
                                    "-fx-cursor: hand;");
                    button.setTextFill(Color.web(DARK_COLOR));
                }
            }

            // Animation de retour à l'échelle normale
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });

        return button;
    }

    public static void showWinMessage(int score, Stage primaryStage) {
        // Créer une fenêtre personnalisée au lieu d'une Alert standard
        Stage winStage = new Stage();
        winStage.initModality(Modality.APPLICATION_MODAL);
        winStage.setTitle("Victoire !");

        // Titre stylisé
        Label winTitle = new Label("FÉLICITATIONS !");
        winTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 28));

        // Effet de dégradé pour le texte
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(SlitherGrid.MAIN_COLOR)),
                new Stop(0.5, Color.web(SlitherGrid.DARK_COLOR)),
                new Stop(1, Color.web(SlitherGrid.MAIN_COLOR)));
        winTitle.setTextFill(gradient);

        // Ajouter des effets visuels au titre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(SlitherGrid.DARK_COLOR, 0.5));
        shadow.setRadius(5);
        shadow.setOffsetY(2);

        Glow glow = new Glow(0.6);
        glow.setInput(shadow);
        winTitle.setEffect(glow);

        // Message
        Label winMessage = new Label("Vous avez correctement résolu le puzzle Slitherlink !");
        winMessage.setFont(Font.font("Calibri", 18));
        winMessage.setTextFill(Color.web(SlitherGrid.DARK_COLOR));

        Label scoreMessage = new Label("Score: " + score);
        scoreMessage.setFont(Font.font("Calibri", 18));
        scoreMessage.setTextFill(Color.web(SlitherGrid.DARK_COLOR));

        // Bouton OK
        Button okButton = Util.createStyledButton("MENU", true, SlitherGrid.MAIN_COLOR, SlitherGrid.DARK_COLOR, SlitherGrid.SECONDARY_COLOR);
        okButton.setPrefWidth(120);



        okButton.setOnAction(e -> {
            Util.animateButtonClick(okButton);
            if(GameState.choixScene == 0){
                GridScene.show(primaryStage);
            } else{
                FreeModeScene.show(primaryStage);
            }

            // Animation de fermeture
            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), winStage.getScene().getRoot());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> winStage.close());
            fadeOut.play();

        });

        // Organisation des éléments
        VBox winBox = new VBox(20, winTitle, winMessage, scoreMessage, okButton);
        winBox.setAlignment(Pos.CENTER);
        winBox.setPadding(new Insets(30));
        winBox.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " + SlitherGrid.SECONDARY_COLOR + ", " + SlitherGrid.LIGHT_COLOR + " 90%);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: " + SlitherGrid.MAIN_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;");

        Scene winScene = new Scene(winBox, 450, 300);
        winStage.setScene(winScene);

        // Animation d'entrée
        winBox.setOpacity(0);

        // Définir une échelle initiale pour l'animation
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(400), winBox);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), winBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Jouer les animations simultanément
        fadeIn.play();
        scaleIn.play();

        winStage.show();
    }

    // Méthode pour créer des boutons de navigation
    public static Button createNavigationButton(String text, String MAIN_COLOR, String DARK_COLOR, String SECONDARY_COLOR) {
        Button button = new Button(text);
        button.setFont(Font.font("Montserrat", FontWeight.BOLD, 20));
        button.setTextFill(Color.web(DARK_COLOR));
        button.setMinSize(50, 50);
        button.setMaxSize(50, 50);

        button.setStyle(
                "-fx-background-color: " + SECONDARY_COLOR + ";" +
                        "-fx-background-radius: 25;" +
                        "-fx-border-color: " + MAIN_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 25;" +
                        "-fx-cursor: hand;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.2));
        shadow.setRadius(3);
        shadow.setOffsetY(2);
        button.setEffect(shadow);

        // Animations au survol
        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: " + MAIN_COLOR + ";" +
                            "-fx-background-radius: 25;" +
                            "-fx-border-color: " + DARK_COLOR + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 25;" +
                            "-fx-cursor: hand;");
            button.setTextFill(Color.WHITE);

            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
        });

        button.setOnMouseExited(e -> {
            if (!button.isDisabled()) {
                button.setStyle(
                        "-fx-background-color: " + SECONDARY_COLOR + ";" +
                                "-fx-background-radius: 25;" +
                                "-fx-border-color: " + MAIN_COLOR + ";" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 25;" +
                                "-fx-cursor: hand;");
                button.setTextFill(Color.web(DARK_COLOR));
            }

            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });

        return button;
    }

}
