package com.menu.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.animation.ScaleTransition;
import javafx.util.Duration;

public class GridScene {
    // Constantes de couleurs pour maintenir la cohérence
    private static final String MAIN_COLOR = "#3A7D44"; // Vert principal
    private static final String SECONDARY_COLOR = "#F2E8CF"; // Beige clair
    private static final String ACCENT_COLOR = "#BC4749"; // Rouge-brique
    private static final String DARK_COLOR = "#386641"; // Vert foncé
    private static final String LIGHT_COLOR = "#A7C957"; // Vert clair

    public static void show(Stage primaryStage) {
        // Conteneur principal
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + SECONDARY_COLOR + ", " + LIGHT_COLOR
                        + " 70%);" +
                        "-fx-background-radius: 0;" +
                        "-fx-padding: 20px;");

        // Titre de la page
        Label titleLabel = new Label("SELECT LEVEL");
        titleLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 42));

        // Gradient pour le titre
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(DARK_COLOR)),
                new Stop(0.5, Color.web(MAIN_COLOR)),
                new Stop(1, Color.web(DARK_COLOR)));
        titleLabel.setTextFill(gradient);

        // Effet de glow et d'ombre pour le titre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(DARK_COLOR, 0.5));
        shadow.setRadius(10);
        shadow.setOffsetY(3);

        Glow glow = new Glow(0.6);
        glow.setInput(shadow);
        titleLabel.setEffect(glow);

        // Bouton de réinitialisation (en haut à droite)
        HBox titleContainer = new HBox(titleLabel);
        titleContainer.setAlignment(Pos.CENTER);


        // Grille de sélection de niveau
        GridPane gridPane = createLevelGrid(primaryStage);

        // Boutons de navigation
        HBox navigationButtons = createNavigationButtons(primaryStage);

        // Assembler tous les éléments
        root.getChildren().addAll(titleContainer, gridPane, navigationButtons);

        // Créer la scène
        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Slitherlink - Sélection de Niveau");
    }

    private static GridPane createLevelGrid(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setAlignment(Pos.CENTER);

        // Créer 15 boutons de niveau (11 déverrouillés, 4 verrouillés)
        for (int i = 0; i < 15; i++) {
            StackPane levelButton;
            // Determine if the level should be unlocked
            boolean isUnlocked;
            if (i == 0) {
                // First level is always unlocked
                isUnlocked = true;
            } else {
                // Check with UserManager if the previous level is completed
                // For level i+1, we need to check if level i is completed
                isUnlocked = UserManager.isGridCompleted(i);
            }

            if (isUnlocked) {
                // Create unlocked level button
                int difficulty = calculateDifficulty(i + 1);
                levelButton = createLevelButton(String.valueOf(i + 1), difficulty, false);

                // Add click handler to launch the game
                int levelNumber = i + 1;
                String levelString = String.format("%03d", levelNumber);
                levelButton.setOnMouseClicked(e -> {
                    String gridId = "grid-" + levelString;
                    if (GameSaveManager.hasSavedGame(gridId)) {
                        // Load existing save
                        GameSaveManager.loadGame(primaryStage, gridId);
                    } else {
                        // Start new level
                        GameScene.show(primaryStage, "-1");
                    }
                });
            } else {
                // Create locked level button
                levelButton = createLevelButton("", 0, true);
            }

            gridPane.add(levelButton, i % 5, i / 5);
        }

        return gridPane;
    }

    private static StackPane createLevelButton(String level, int difficulty, boolean locked) {
        StackPane buttonContainer = new StackPane();
        buttonContainer.setPrefSize(120, 120);

        // Fond du bouton
        javafx.scene.shape.Rectangle background = new javafx.scene.shape.Rectangle(100, 100);
        background.setArcWidth(20);
        background.setArcHeight(20);
        background.setFill(Color.WHITE); // Fond blanc comme dans le menu principal

        // Style de base
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.2));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        buttonContainer.setEffect(shadow);

        // Conteneur pour le contenu du bouton
        VBox content = new VBox(5);
        content.setAlignment(Pos.CENTER);

        if (locked) {
            // Utiliser une image de cadenas au lieu d'un emoji
            try {
                // Charger l'image du cadenas
                var resourceStream = GridScene.class.getResourceAsStream("/lock.png");
                if (resourceStream == null) {
                    throw new Exception("Resource not found: /lock.png");
                }
                Image lockImage = new Image(resourceStream);
                ImageView lockIcon = new ImageView(lockImage);
                lockIcon.setFitWidth(40);
                lockIcon.setFitHeight(40);
                lockIcon.setPreserveRatio(true);

                // Assombrir le bouton
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setBrightness(-0.5);
                background.setFill(Color.web(SECONDARY_COLOR)); // Couleur de fond légèrement assombrie
                background.setEffect(colorAdjust); // Appliquer l'effet au fond

                // Style du bouton verrouillé
                buttonContainer.setStyle(
                        "-fx-background-color: #d0d0d0;" + // Gris clair
                                "-fx-background-radius: 30;" +
                                "-fx-border-color: " + ACCENT_COLOR + ";" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 30;" +
                                "-fx-opacity: 0.8;");

                content.getChildren().add(lockIcon);
            } catch (Exception e) {
                // En cas d'erreur, utiliser l'emoji comme fallback
                System.err.println("Impossible de charger l'image du cadenas: " + e.getMessage());
                Label lockIcon = new Label("🔒");
                lockIcon.setFont(Font.font(36));
                lockIcon.setTextFill(Color.web(ACCENT_COLOR));
                content.getChildren().add(lockIcon);
            }
        } else {
            // Texte du niveau
            Label levelLabel = new Label(level);
            levelLabel.setFont(Font.font("Calibri", FontWeight.BOLD, 36));
            levelLabel.setTextFill(Color.web(DARK_COLOR));

            // Étoiles de difficulté
            HBox starContainer = new HBox(5);
            starContainer.setAlignment(Pos.CENTER);
            for (int i = 0; i < 5; i++) {
                Label star = new Label(i < difficulty ? "★" : "☆");
                star.setFont(Font.font(20));
                star.setTextFill(Color.web(MAIN_COLOR));

                starContainer.getChildren().add(star);
            }

            content.getChildren().addAll(levelLabel, starContainer);

            // Animations de survol
            buttonContainer.setOnMouseEntered(e -> {
                buttonContainer.setStyle("-fx-background-color: derive(" + MAIN_COLOR + ", 50%);" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-color: " + MAIN_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 30;");
                ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), buttonContainer);
                scaleUp.setToX(1.1);
                scaleUp.setToY(1.1);
                scaleUp.play();
            });

            buttonContainer.setOnMouseExited(e -> {
                buttonContainer.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-background-radius: 30;" +
                                "-fx-border-color: " + MAIN_COLOR + ";" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 30;");
                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), buttonContainer);
                scaleDown.setToX(1.0);
                scaleDown.setToY(1.0);
                scaleDown.play();
            });

            // Style du bouton déverrouillé
            buttonContainer.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-background-radius: 30;" +
                            "-fx-border-color: " + MAIN_COLOR + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 30;");
        }

        buttonContainer.getChildren().addAll(background, content);
        return buttonContainer;
    }

    private static int calculateDifficulty(int level) {
        // Logique simple pour calculer la difficulté
        if (level <= 3)
            return 1;
        if (level <= 6)
            return 2;
        if (level <= 10)
            return 3;
        return 5;
    }

    private static Button createResetButton() {
        Button resetButton = new Button("⟳"); // Utiliser un caractère Unicode de rotation
        resetButton.setFont(Font.font(20)); // Taille de police plus grande

        resetButton.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-color: " + MAIN_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 30;" +
                        "-fx-text-fill: " + MAIN_COLOR + ";" +
                        "-fx-padding: 5 10;");

        // Ajouter des effets de survol
        resetButton.setOnMouseEntered(e -> {
            resetButton.setStyle(
                    "-fx-background-color: " + MAIN_COLOR + ";" +
                            "-fx-background-radius: 30;" +
                            "-fx-border-color: white;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 30;" +
                            "-fx-text-fill: white;" +
                            "-fx-padding: 5 10;");
        });

        resetButton.setOnMouseExited(e -> {
            resetButton.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-background-radius: 30;" +
                            "-fx-border-color: " + MAIN_COLOR + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 30;" +
                            "-fx-text-fill: " + MAIN_COLOR + ";" +
                            "-fx-padding: 5 10;");
        });

        resetButton.setOnAction(e -> {
            // Logique de réinitialisation (à implémenter)
            System.out.println("Réinitialisation des niveaux");
        });

        return resetButton;
    }

    private static HBox createNavigationButtons(Stage primaryStage) {
        HBox navigationBox = new HBox(20);
        navigationBox.setAlignment(Pos.CENTER);

        // Bouton de retour
        Button backButton = createAnimatedButton("Retour");
        backButton.setOnAction(e -> Menu.show(primaryStage));

        navigationBox.getChildren().add(backButton);

        return navigationBox;
    }

    // Méthode pour créer un bouton animé similaire à ceux du menu principal
    private static Button createAnimatedButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Calibri", FontWeight.BOLD, 16));
        button.setTextFill(Color.web(DARK_COLOR));
        button.setPrefWidth(300);
        button.setPrefHeight(55);
        button.setAlignment(Pos.CENTER);
        button.setPadding(new Insets(10, 20, 10, 20));

        button.setStyle(
                "-fx-background-color: " + SECONDARY_COLOR + ";" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-color: " + MAIN_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 30;" +
                        "-fx-cursor: hand;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.2));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        button.setEffect(shadow);

        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: " + MAIN_COLOR + ";" +
                            "-fx-background-radius: 30;" +
                            "-fx-border-color: " + DARK_COLOR + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 30;" +
                            "-fx-cursor: hand;");
            button.setTextFill(Color.WHITE);

            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: " + SECONDARY_COLOR + ";" +
                            "-fx-background-radius: 30;" +
                            "-fx-border-color: " + MAIN_COLOR + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 30;" +
                            "-fx-cursor: hand;");
            button.setTextFill(Color.web(DARK_COLOR));

            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });

        return button;
    }
}