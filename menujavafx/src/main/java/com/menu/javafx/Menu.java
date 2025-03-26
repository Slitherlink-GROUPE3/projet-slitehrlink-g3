package com.menu.javafx;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Stack;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Tooltip;
import java.io.InputStream;

public class Menu extends Application {
    private static final Stack<Scene> sceneHistory = new Stack<>();
    private static final String MAIN_COLOR = "#3A7D44"; // Vert principal
    private static final String SECONDARY_COLOR = "#F2E8CF"; // Beige clair
    private static final String ACCENT_COLOR = "#BC4749"; // Rouge-brique
    private static final String DARK_COLOR = "#386641"; // Vert foncé
    private static final String LIGHT_COLOR = "#A7C957"; // Vert clair

    private static boolean saveCheckPerformed = false;

    private static Image loadImage(String resourcePath) {
        try {
            InputStream inputStream = Menu.class.getResourceAsStream(resourcePath);
            if (inputStream != null) {
                return new Image(inputStream);
            } else {
                System.err.println("Impossible de charger l'image : " + resourcePath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image : " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Image icon = loadImage("/amir.jpeg");
        if (icon != null) {
            primaryStage.getIcons().add(icon);
        } else {
            System.out.println("Impossible de charger l'icône");
        }

        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(700);
        primaryStage.setTitle("Slither Link - Puzzle Game");

        // Démarrer avec l'écran de connexion au lieu du menu principal
        LoginScene.show(primaryStage);
    }

    public static void show(Stage primaryStage) {

        // Récupérer le nom d'utilisateur actuel
        String username = UserManager.getCurrentUser();
        primaryStage.setTitle("Slitherlink - " + (username != null ? username : "Menu Principal"));

        // Only check for saves if we haven't done it already since login
        if (!saveCheckPerformed && username != null) {
            System.out.println("Vérification des sauvegardes pour " + username);
            boolean saveLoaded = com.menu.javafx.SaveGameLoader.loadUserSave(primaryStage);
            System.out.println(
                    "Résultat de la vérification: " + (saveLoaded ? "Sauvegarde chargée" : "Pas de sauvegarde chargée"));
            saveCheckPerformed = true;
        }
        if (primaryStage.getIcons().isEmpty()) {
            Image icon = loadImage("/amir.jpeg");
            if (icon != null) {
                primaryStage.getIcons().add(icon);
            }
        }

        Label title = new Label("SLITHER LINK");
        title.setFont(Font.font("Montserrat", FontWeight.BOLD, 42));

        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(DARK_COLOR)),
                new Stop(0.5, Color.web(MAIN_COLOR)),
                new Stop(1, Color.web(DARK_COLOR)));
        title.setTextFill(gradient);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(DARK_COLOR, 0.5));
        shadow.setRadius(10);
        shadow.setOffsetY(3);

        Glow glow = new Glow(0.6);
        glow.setInput(shadow);
        title.setEffect(glow);

        Label subtitle = new Label("Le SlitherLink préféré de ton SlitherLink préféré");
        subtitle.setFont(Font.font("Calibri", FontWeight.LIGHT, 18));
        subtitle.setTextFill(Color.web(DARK_COLOR));

        VBox titleBox = new VBox(8, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);

        ImageView logoView = null;
        Image logo = loadImage("/amir.jpeg");
        if (logo != null) {
            logoView = new ImageView(logo);
            logoView.setFitHeight(60);
            logoView.setPreserveRatio(true);
        } else {
            Rectangle logoPlaceholder = new Rectangle(60, 60);
            logoPlaceholder.setFill(Color.web(ACCENT_COLOR));
            logoPlaceholder.setArcWidth(15);
            logoPlaceholder.setArcHeight(15);
            logoView = new ImageView();
            System.out.println("Impossible de charger le logo");
        }

        HBox headerBox = new HBox(20, logoView, titleBox);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(25));
        headerBox.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " + SECONDARY_COLOR + ", " + SECONDARY_COLOR
                        + " 90%, rgba(0,0,0,0.05));" +
                        "-fx-border-color: transparent transparent " + LIGHT_COLOR + " transparent;" +
                        "-fx-border-width: 0 0 2 0;");

        Button adventureButton = createAnimatedButton("Mode Aventure", "🧩");
        Button freeModeButton = createAnimatedButton("Mode Libre", "🔓");
        Button settingsButton = createAnimatedButton("Paramètres", "⚙️");
        Button tutorialButton = createAnimatedButton("Tutoriel", "📖");
        Button exitButton = createAnimatedButton("Quitter", "🚪");

        // Explique l'utilité des boutons lorsqu'on met la souris dessus
        Tooltip tooltipAdventureButton = new Tooltip("Commencez une nouvelle aventure !");
        Tooltip.install(adventureButton, tooltipAdventureButton);

        Tooltip tooltipFreeModeButton = new Tooltip("Jouez en mode libre !");
        Tooltip.install(freeModeButton, tooltipFreeModeButton);

        Tooltip tooltipSettingsButton = new Tooltip("Accédez aux paramètres !");
        Tooltip.install(settingsButton, tooltipSettingsButton);

        Tooltip tooltipTutorialButton = new Tooltip("Affiche le tutoriel !");
        Tooltip.install(tutorialButton, tooltipTutorialButton);

        Tooltip tooltipExitButton = new Tooltip("Quittez le jeu !");
        Tooltip.install(exitButton, tooltipExitButton);

        // Actions des boutons
        adventureButton.setOnAction(e -> {
            Util.animateButtonClick(adventureButton);
            // GameScene.show(primaryStage);
            GridScene.show(primaryStage);

        });

        freeModeButton.setOnAction(e -> {
            Util.animateButtonClick(freeModeButton);
        });

        settingsButton.setOnAction(e -> {
            Util.animateButtonClick(settingsButton);
            SettingScene.show(primaryStage);
        });

        tutorialButton.setOnAction(e -> {
            Util.animateButtonClick(tutorialButton);
            TechniquesScene.show(primaryStage);
        });

        exitButton.setOnAction(e -> {
            Util.animateButtonClick(exitButton);
            FadeTransition fadeOut = new FadeTransition(Duration.millis(600), primaryStage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> primaryStage.close());
            fadeOut.play();
        });

        VBox menuBox = new VBox(18,
                adventureButton,
                freeModeButton,
                settingsButton,
                tutorialButton,
                exitButton);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(40, 20, 40, 20));
        menuBox.setMaxWidth(400);
        menuBox.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.8);" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 5);");

        StackPane centerPane = new StackPane(menuBox);
        centerPane.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + SECONDARY_COLOR + ", " + LIGHT_COLOR
                        + " 70%);" +
                        "-fx-background-radius: 0;" +
                        "-fx-padding: 20px;");

        Label footerLabel = new Label("© 2025 Slither Link - Tous droits réservés");
        footerLabel.setTextFill(Color.web(DARK_COLOR, 0.7));
        footerLabel.setFont(Font.font("Calibri", 12));

        StackPane footer = new StackPane(footerLabel);
        footer.setPadding(new Insets(10));
        footer.setStyle("-fx-background-color: " + SECONDARY_COLOR + ";");

        BorderPane root = new BorderPane();
        root.setTop(headerBox);
        root.setCenter(centerPane);
        root.setBottom(footer);

        root.setOpacity(0);

        Scene scene = new Scene(root, 900, 700);

        primaryStage.setScene(scene);
        primaryStage.show();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private static Button createAnimatedButton(String text, String icon) {
        Button button = new Button(icon + " " + text);
        button.setFont(Font.font("Calibri", FontWeight.BOLD, 16));
        button.setTextFill(Color.web(DARK_COLOR));
        button.setPrefWidth(300);
        button.setPrefHeight(55);
        button.setAlignment(Pos.CENTER_LEFT);
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

    public static void changeScene(Stage primaryStage, Scene newScene) {
        if (primaryStage.getScene() != null) {
            sceneHistory.push(primaryStage.getScene());
        }

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), primaryStage.getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> {
            primaryStage.setScene(newScene);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newScene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }

    public static void goBack(Stage primaryStage) {
        if (!sceneHistory.isEmpty()) {
            Scene previousScene = sceneHistory.pop();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), primaryStage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                primaryStage.setScene(previousScene);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), previousScene.getRoot());
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        }
    }

    public static void resetSaveCheckFlag() {
        saveCheckPerformed = false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}