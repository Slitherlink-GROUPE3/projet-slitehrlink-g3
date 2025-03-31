package com.menu.javafx;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SettingScene {

    // Variable pour suivre le mode actuel
    private static boolean isDarkMode = false;
    
    // Constantes de couleurs du Mode Clair - Version moderne
    private static final String LIGHT_MAIN_COLOR = "#36846d";     // Vert turquoise
    private static final String LIGHT_SECONDARY_COLOR = "#f5f8fa"; // Blanc cassé
    private static final String LIGHT_ACCENT_COLOR = "#d65c5c";    // Rouge corail
    private static final String LIGHT_DARK_COLOR = "#255a4b";      // Vert foncé
    private static final String LIGHT_LIGHT_COLOR = "#a9d8bd";     // Vert clair menthe
    
    // Constantes de couleurs du Mode Sombre - Avec meilleur contraste
    private static final String DARK_MAIN_COLOR = "#42b996";      // Vert émeraude lumineux
    private static final String DARK_SECONDARY_COLOR = "#141921";  // Bleu-gris très foncé
    private static final String DARK_ACCENT_COLOR = "#f2777a";     // Rouge-saumon vif
    private static final String DARK_DARK_COLOR = "#192730";       // Bleu-gris profond
    private static final String DARK_LIGHT_COLOR = "#74c99e";      // Vert menthe saturé
    
    // Couleurs actuelles (selon le mode)
    private static String MAIN_COLOR = LIGHT_MAIN_COLOR;
    private static String SECONDARY_COLOR = LIGHT_SECONDARY_COLOR;
    private static String ACCENT_COLOR = LIGHT_ACCENT_COLOR;
    private static String DARK_COLOR = LIGHT_DARK_COLOR;
    private static String LIGHT_COLOR = LIGHT_LIGHT_COLOR;

    public static void show(Stage primaryStage) {
        // Appliquer le thème actuel
        applyTheme(isDarkMode);
        
        // Création du conteneur principal avec fond dynamique
        StackPane root = new StackPane();
        
        // Créer un fond animé moderne
        createModernBackground(root);
        
        // Conteneur principal pour le contenu
        VBox mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(40, 20, 40, 20));
        mainContainer.setMaxWidth(900);
        
        // Création de l'en-tête avec effet 3D
        createModernHeader(mainContainer);
        
        // Conteneur principal pour les boutons avec effet de carte
        VBox settingsBox = new VBox(20);
        settingsBox.setAlignment(Pos.CENTER);
        settingsBox.setPadding(new Insets(40, 50, 40, 50));
        settingsBox.setMaxWidth(600);
        
        // Style du conteneur en mode carte moderne avec fond translucide
        String boxBackground = isDarkMode ? 
                "rgba(30, 35, 45, 0.85)" : 
                "rgba(255, 255, 255, 0.9)";
        
        String borderColor = isDarkMode ? 
                "rgba(80, 180, 150, 0.3)" : 
                "rgba(54, 132, 109, 0.2)";
        
        settingsBox.setStyle(
                "-fx-background-color: " + boxBackground + ";" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-width: 1.5px;" +
                "-fx-border-radius: 20;");
        
        // Ajouter un effet d'ombre portée élégant
        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.web(isDarkMode ? "#000000" : "#333333", 0.25));
        cardShadow.setRadius(20);
        cardShadow.setSpread(0.05);
        cardShadow.setOffsetY(6);
        settingsBox.setEffect(cardShadow);
        
        // Utiliser notre propre création de bouton modernisée
        Button mainMenuButton = createModernButton("Menu principal", false);
        Button saveButton = createModernButton("Sauvegarder", true);
        Button changeAccountButton = createModernButton("Changer de compte", false);
        
        // Création des boutons de thème modernes en style toggle
        HBox themeToggleContainer = createThemeToggleButtons();
        
        Button quitButton = createModernButton("Quitter", true);
        quitButton.setStyle(quitButton.getStyle() + 
                "-fx-background-color: " + (isDarkMode ? DARK_ACCENT_COLOR : LIGHT_ACCENT_COLOR) + ";");
        
        // Configurer les actions des boutons
        mainMenuButton.setOnAction(e -> {
            addClickEffectToButton(mainMenuButton);
            fadeTransitionToScene(() -> Menu.show(primaryStage), root);
        });
        
        saveButton.setOnAction(e -> {
            addClickEffectToButton(saveButton);
            // Logique de sauvegarde à implémenter
            showFloatingNotification(root, "Paramètres sauvegardés !");
        });
        
        changeAccountButton.setOnAction(e -> {
            addClickEffectToButton(changeAccountButton);
            fadeTransitionToScene(() -> LoginScene.show(primaryStage), root);
        });
        
        quitButton.setOnAction(e -> {
            addClickEffectToButton(quitButton);
            animateClose(primaryStage, root);
        });
        
        // Créer des sections avec des titres et icônes pour organiser les paramètres
        VBox generalSection = createSettingsSection("Général", mainMenuButton, saveButton);
        VBox accountSection = createSettingsSection("Compte", changeAccountButton);
        VBox appearanceSection = createSettingsSection("Apparence", themeToggleContainer);
        VBox exitSection = createSettingsSection("Application", quitButton);
        
        // Ajouter les sections à la boîte de paramètres
        settingsBox.getChildren().addAll(
                generalSection,
                createModernSeparator(),
                accountSection,
                createModernSeparator(),
                appearanceSection,
                createModernSeparator(),
                exitSection
        );
        
        // Conteneur pour centrer la boîte de paramètres
        StackPane centerPane = new StackPane(settingsBox);
        centerPane.setPadding(new Insets(0, 0, 60, 0));
        
        // Ajouter les éléments au conteneur principal
        mainContainer.getChildren().add(centerPane);
        
        // Ajouter le conteneur principal à la racine
        root.getChildren().add(mainContainer);
        
        // Créer la scène
        Scene scene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth(), 
                Screen.getPrimary().getVisualBounds().getHeight());
        
        // Ajouter styles CSS globaux (polices, etc.)
        scene.getStylesheets().add(SettingScene.class.getResource("/styles/modern-styles.css") != null ? 
                SettingScene.class.getResource("/styles/modern-styles.css").toExternalForm() : "");
        
        // Animation d'entrée
        animateEntrance(mainContainer, settingsBox);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Slitherlink - Paramètres");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    
    /**
     * Crée un fond animé moderne avec subtil mouvement de particules ou gradient
     */
    private static void createModernBackground(StackPane root) {
        // Fond avec dégradé adapté au mode
        Rectangle background = new Rectangle();
        background.widthProperty().bind(root.widthProperty());
        background.heightProperty().bind(root.heightProperty());
        
        // Créer un dégradé radial plus moderne
        Color color1 = Color.web(isDarkMode ? DARK_SECONDARY_COLOR : LIGHT_SECONDARY_COLOR);
        Color color2 = Color.web(isDarkMode ? DARK_DARK_COLOR : LIGHT_LIGHT_COLOR, 0.7);
        Color accentColor = Color.web(isDarkMode ? DARK_MAIN_COLOR : LIGHT_MAIN_COLOR, 0.1);
        
        RadialGradient gradient = new RadialGradient(
                0, 0, 0.5, 0.3, 1.0, true, CycleMethod.NO_CYCLE,
                new Stop(0, color1),
                new Stop(0.8, color2),
                new Stop(1, accentColor)
        );
        
        background.setFill(gradient);
        
        if (isDarkMode) {
            // Réduire le nombre de points (de 50 à 15)
            for (int i = 0; i < 15; i++) {
                double x = Math.random();
                double y = Math.random();
                double size = 1 + Math.random() * 2;
                
                Circle dot = new Circle(size);
                dot.setFill(Color.web(DARK_MAIN_COLOR, 0.2 + Math.random() * 0.2));
                dot.setCenterX(x * Screen.getPrimary().getVisualBounds().getWidth());
                dot.setCenterY(y * Screen.getPrimary().getVisualBounds().getHeight());
                
                // Animation plus simple et moins fréquente
                if (i % 3 == 0) { // Animer seulement 1/3 des points
                    Timeline pulse = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(dot.opacityProperty(), 0.1)),
                        new KeyFrame(Duration.seconds(4), new KeyValue(dot.opacityProperty(), 0.3))
                    );
                    pulse.setAutoReverse(true);
                    pulse.setCycleCount(Timeline.INDEFINITE);
                    pulse.play();
                }
                
                root.getChildren().add(dot);
            }
        } else {
            // Formes organiques subtiles pour le mode clair
            for (int i = 0; i < 10; i++) {
                double x = Math.random() * Screen.getPrimary().getVisualBounds().getWidth();
                double y = Math.random() * Screen.getPrimary().getVisualBounds().getHeight();
                double size = 100 + Math.random() * 200;
                
                Circle shape = new Circle(size);
                shape.setCenterX(x);
                shape.setCenterY(y);
                shape.setFill(Color.web(LIGHT_MAIN_COLOR, 0.03 + Math.random() * 0.04));
                
                root.getChildren().add(shape);
            }
        }
        
        // Ajouter le fond principal en premier
        root.getChildren().add(0, background);
    }
    
    /**
     * Crée un en-tête moderne avec effet 3D et animations
     */
    private static void createModernHeader(VBox container) {
        StackPane headerContainer = new StackPane();
        headerContainer.setPadding(new Insets(20, 0, 40, 0));
        
        // Titre avec effet 3D
        Text settingsTitle = new Text("Paramètres");
        settingsTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 42));
        settingsTitle.setTextAlignment(TextAlignment.CENTER);
        
        // Couleur du texte avec dégradé
        LinearGradient textGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(isDarkMode ? DARK_MAIN_COLOR : LIGHT_MAIN_COLOR, 0.9)),
                new Stop(0.5, Color.web(isDarkMode ? DARK_LIGHT_COLOR : LIGHT_DARK_COLOR, 1.0)),
                new Stop(1, Color.web(isDarkMode ? DARK_MAIN_COLOR : LIGHT_MAIN_COLOR, 0.9))
        );
        settingsTitle.setFill(textGradient);
        
        // Effet 3D avancé
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.web(isDarkMode ? "#000000" : "#333333", 0.3));
        dropShadow.setRadius(5);
        dropShadow.setOffsetX(2);
        dropShadow.setOffsetY(2);
        
        Reflection reflection = new Reflection();
        reflection.setFraction(0.2);
        reflection.setTopOpacity(0.5);
        reflection.setBottomOpacity(0);
        
        Glow glow = new Glow(0.4);
        
        // Combiner les effets
        dropShadow.setInput(reflection);
        glow.setInput(dropShadow);
        settingsTitle.setEffect(glow);
        
        // Créer une ligne décorative sous le titre
        Line decorativeLine = new Line(-100, 0, 100, 0);
        decorativeLine.setStroke(Color.web(isDarkMode ? DARK_LIGHT_COLOR : LIGHT_MAIN_COLOR, 0.7));
        decorativeLine.setStrokeWidth(3);
        decorativeLine.setStrokeLineCap(StrokeLineCap.ROUND);
        
        // Animation pour la ligne décorative
        decorativeLine.setScaleX(0);
        Timeline lineAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(decorativeLine.scaleXProperty(), 0)),
            new KeyFrame(Duration.millis(800), new KeyValue(decorativeLine.scaleXProperty(), 1, 
                    Interpolator.EASE_OUT))
        );
        
        // Ajouter à l'interface et jouer l'animation
        VBox titleBox = new VBox(15, settingsTitle, decorativeLine);
        titleBox.setAlignment(Pos.CENTER);
        headerContainer.getChildren().add(titleBox);
        container.getChildren().add(headerContainer);
        
        lineAnimation.play();
    }
    
    /**
     * Crée un bouton moderne avec effets de survol avancés
     */
    private static Button createModernButton(String text, boolean isPrimary) {
        Button button = new Button(text);
        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        button.setPrefHeight(45);
        button.setMinWidth(200);
        button.setMaxWidth(300);
        
        // Couleurs basées sur le thème et le type de bouton
        String buttonColor = isPrimary ? 
                (isDarkMode ? DARK_MAIN_COLOR : LIGHT_MAIN_COLOR) : 
                (isDarkMode ? "rgba(60, 65, 75, 0.7)" : "rgba(240, 240, 240, 0.9)");
        
        String textColor = isPrimary ? 
                "white" : 
                (isDarkMode ? DARK_LIGHT_COLOR : LIGHT_DARK_COLOR);
        
        String hoverColor = isPrimary ? 
                (isDarkMode ? DARK_LIGHT_COLOR : LIGHT_DARK_COLOR) : 
                (isDarkMode ? "rgba(70, 75, 85, 0.8)" : "rgba(230, 230, 230, 0.95)");
        
        // Style de base du bouton
        button.setStyle(
                "-fx-background-color: " + buttonColor + ";" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-background-radius: 10px;" +
                "-fx-padding: 10px 20px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);" +
                "-fx-border-width: 0;");
        
        // Effets de survol avancés
        button.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), button);
            scaleUp.setToX(1.05);
            scaleUp.setToY(1.05);
            scaleUp.play();
            
            button.setStyle(
                    "-fx-background-color: " + hoverColor + ";" +
                    "-fx-text-fill: " + textColor + ";" +
                    "-fx-background-radius: 10px;" +
                    "-fx-padding: 10px 20px;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 3);" +
                    "-fx-border-width: 0;");
        });
        
        button.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), button);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();
            
            button.setStyle(
                    "-fx-background-color: " + buttonColor + ";" +
                    "-fx-text-fill: " + textColor + ";" +
                    "-fx-background-radius: 10px;" +
                    "-fx-padding: 10px 20px;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);" +
                    "-fx-border-width: 0;");
        });
        
        return button;
    }
    
    /**
     * Ajoute un effet de clic au bouton
     */
    private static void addClickEffectToButton(Button button) {
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), button);
        scaleDown.setToX(0.95);
        scaleDown.setToY(0.95);
        scaleDown.play();
        
        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), button);
            scaleUp.setToX(1.0);
            scaleUp.setToY(1.0);
            scaleUp.play();
        });
        pause.play();
    }
    
    /**
     * Crée un toggle button moderne pour le choix de thème
     */
    private static HBox createThemeToggleButtons() {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER);
        
        // Créer un conteneur de type switch
        StackPane switchContainer = new StackPane();
        switchContainer.setPrefWidth(200);
        switchContainer.setPrefHeight(40);
        
        // Fond du switch
        Rectangle switchBackground = new Rectangle(200, 40);
        switchBackground.setArcWidth(40);
        switchBackground.setArcHeight(40);
        switchBackground.setFill(Color.web(isDarkMode ? DARK_DARK_COLOR : LIGHT_SECONDARY_COLOR, 0.8));
        switchBackground.setStroke(Color.web(isDarkMode ? DARK_MAIN_COLOR : LIGHT_MAIN_COLOR, 0.3));
        switchBackground.setStrokeWidth(1.5);
        
        // Curseur du switch
        Circle switchThumb = new Circle(20);
        switchThumb.setFill(Color.web(isDarkMode ? DARK_MAIN_COLOR : LIGHT_DARK_COLOR));
        switchThumb.setTranslateX(isDarkMode ? 70 : -70);
        
        // Textes pour les options
        Text lightText = new Text("Clair");
        lightText.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        lightText.setFill(Color.web(isDarkMode ? DARK_LIGHT_COLOR : LIGHT_DARK_COLOR, isDarkMode ? 0.6 : 1.0));
        lightText.setTranslateX(-60);
        
        Text darkText = new Text("Sombre");
        darkText.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        darkText.setFill(Color.web(isDarkMode ? DARK_LIGHT_COLOR : LIGHT_DARK_COLOR, isDarkMode ? 1.0 : 0.6));
        darkText.setTranslateX(60);
        
        // Icônes pour les thèmes
        Text lightIcon = new Text("☀️");
        lightIcon.setFont(Font.font("Arial", 16));
        lightIcon.setTranslateX(-40);
        
        Text darkIcon = new Text("🌙");
        darkIcon.setFont(Font.font("Arial", 16));
        darkIcon.setTranslateX(40);
        
        switchContainer.getChildren().addAll(switchBackground, lightText, darkText, lightIcon, darkIcon, switchThumb);
        
        // Gestionnaire d'événements pour le switch
        switchContainer.setOnMouseClicked(e -> {
            // Animer le curseur et changer le thème
            TranslateTransition thumbTransition = new TranslateTransition(Duration.millis(250), switchThumb);
            
            if (isDarkMode) {
                // Passage au mode clair
                thumbTransition.setToX(-70);
                thumbTransition.setOnFinished(event -> {
                    isDarkMode = false;
                    saveThemePreference(false);
                    show((Stage) ((Scene)switchContainer.getScene()).getWindow());
                });
            } else {
                // Passage au mode sombre
                thumbTransition.setToX(70);
                thumbTransition.setOnFinished(event -> {
                    isDarkMode = true;
                    saveThemePreference(true);
                    show((Stage) ((Scene)switchContainer.getScene()).getWindow());
                });
            }
            
            thumbTransition.play();
        });
        
        switchContainer.setCursor(javafx.scene.Cursor.HAND);
        container.getChildren().add(switchContainer);
        
        return container;
    }
    
    /**
     * Crée une section de paramètres avec titre et icône
     */
    private static VBox createSettingsSection(String title, Node... content) {
        VBox section = new VBox(15);
        section.setAlignment(Pos.CENTER);
        
        // Titre de section
        Label sectionTitle = new Label(title);
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        sectionTitle.setTextFill(Color.web(isDarkMode ? DARK_LIGHT_COLOR : LIGHT_DARK_COLOR));
        
        // Conteneur pour les éléments de contenu
        VBox contentBox = new VBox(12);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(content);
        
        section.getChildren().addAll(sectionTitle, contentBox);
        return section;
    }
    
    /**
     * Crée un séparateur moderne et élégant
     */
    private static Node createModernSeparator() {
        Rectangle separator = new Rectangle();
        separator.setHeight(1);
        separator.setWidth(450);
        
        // Dégradé horizontal pour un effet plus moderne
        LinearGradient sepGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(isDarkMode ? DARK_MAIN_COLOR : LIGHT_MAIN_COLOR, 0.0)),
                new Stop(0.5, Color.web(isDarkMode ? DARK_LIGHT_COLOR : LIGHT_MAIN_COLOR, 0.5)),
                new Stop(1, Color.web(isDarkMode ? DARK_MAIN_COLOR : LIGHT_MAIN_COLOR, 0.0))
        );
        separator.setFill(sepGradient);
        
        StackPane separatorContainer = new StackPane(separator);
        separatorContainer.setPadding(new Insets(15, 0, 15, 0));
        
        return separatorContainer;
    }
    
    /**
     * Anime l'entrée des éléments de l'interface
     */
    private static void animateEntrance(VBox mainContainer, VBox settingsBox) {
        // Animation principale du conteneur
        mainContainer.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        // Animation de la carte de paramètres
        settingsBox.setScaleX(0.9);
        settingsBox.setScaleY(0.9);
        settingsBox.setOpacity(0);
        
        // Animations parallèles
        FadeTransition cardFade = new FadeTransition(Duration.millis(600), settingsBox);
        cardFade.setFromValue(0);
        cardFade.setToValue(1);
        cardFade.setDelay(Duration.millis(300));
        
        ScaleTransition cardScale = new ScaleTransition(Duration.millis(600), settingsBox);
        cardScale.setFromX(0.9);
        cardScale.setFromY(0.9);
        cardScale.setToX(1.0);
        cardScale.setToY(1.0);
        cardScale.setDelay(Duration.millis(300));
        cardScale.setInterpolator(Interpolator.EASE_OUT);
        
        // Démarrer les animations
        cardFade.play();
        cardScale.play();
    }
    
    /**
     * Anime la fermeture de l'application
     */
    private static void animateClose(Stage stage, StackPane root) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), root);
        scale.setToX(0.9);
        scale.setToY(0.9);
        
        FadeTransition fade = new FadeTransition(Duration.millis(300), root);
        fade.setToValue(0);
        
        ParallelTransition transition = new ParallelTransition(scale, fade);
        transition.setOnFinished(e -> stage.close());
        transition.play();
    }
    
    /**
     * Effectue une transition en fondu vers une autre scène
     */
    private static void fadeTransitionToScene(Runnable changeSceneAction, StackPane root) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> changeSceneAction.run());
        fadeOut.play();
    }
    
    /**
     * Affiche une notification flottante élégante
     */
    private static void showFloatingNotification(StackPane root, String message) {
        // Créer la notification
        HBox notification = new HBox(10);
        notification.setAlignment(Pos.CENTER);
        notification.setPadding(new Insets(15, 25, 15, 25));
        notification.setStyle(
                "-fx-background-color: " + (isDarkMode ? "rgba(40, 40, 45, 0.9)" : "rgba(255, 255, 255, 0.95)") + ";" +
                "-fx-background-radius: 10px;" +
                "-fx-border-radius: 10px;" +
                "-fx-border-color: " + (isDarkMode ? DARK_MAIN_COLOR : LIGHT_MAIN_COLOR) + ";" +
                "-fx-border-width: 1px;");
        
        // Icône de succès
        Text icon = new Text("✓");
        icon.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        icon.setFill(Color.web(isDarkMode ? DARK_MAIN_COLOR : LIGHT_MAIN_COLOR));
        
        // Message
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Segoe UI", 14));
        messageLabel.setTextFill(Color.web(isDarkMode ? DARK_LIGHT_COLOR : LIGHT_DARK_COLOR));
        
        notification.getChildren().addAll(icon, messageLabel);
        
        // Effet d'ombre
        DropShadow notifShadow = new DropShadow();
        notifShadow.setColor(Color.web(isDarkMode ? "#000000" : "#333333", 0.3));
        notifShadow.setRadius(10);
        notifShadow.setOffsetY(3);
        notification.setEffect(notifShadow);
        
        // Positionner en bas de l'écran
        notification.setTranslateY(200);
        notification.setOpacity(0);
        
        // Ajouter à l'interface
        root.getChildren().add(notification);
        
        // Animations
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), notification);
        slideIn.setFromY(200);
        slideIn.setToY(0);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), notification);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        // Animation de sortie avec délai
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(400), notification);
        slideOut.setFromY(0);
        slideOut.setToY(-50);
        slideOut.setDelay(Duration.seconds(2));
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), notification);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2));
        fadeOut.setOnFinished(e -> root.getChildren().remove(notification));
        
        // Jouer les animations
        new ParallelTransition(slideIn, fadeIn).play();
        new ParallelTransition(slideOut, fadeOut).play();
    }
    
    /**
     * Applique le thème approprié (clair ou sombre)
     * @param darkMode true pour le mode sombre, false pour le mode clair
     */
    public static void applyTheme(boolean darkMode) {
        if (darkMode) {
            // Appliquer les couleurs du mode sombre
            MAIN_COLOR = DARK_MAIN_COLOR;
            SECONDARY_COLOR = DARK_SECONDARY_COLOR;
            ACCENT_COLOR = DARK_ACCENT_COLOR;
            DARK_COLOR = DARK_DARK_COLOR;
            LIGHT_COLOR = DARK_LIGHT_COLOR;
        } else {
            // Appliquer les couleurs du mode clair
            MAIN_COLOR = LIGHT_MAIN_COLOR;
            SECONDARY_COLOR = LIGHT_SECONDARY_COLOR;
            ACCENT_COLOR = LIGHT_ACCENT_COLOR;
            DARK_COLOR = LIGHT_DARK_COLOR;
            LIGHT_COLOR = LIGHT_LIGHT_COLOR;
        }
    }
    
    /**
     * Sauvegarde la préférence du thème
     */
    private static void saveThemePreference(boolean darkMode) {
        isDarkMode = darkMode;
        // Message de confirmation du changement de thème
        System.out.println("Thème " + (darkMode ? "sombre" : "clair") + " activé");
    }

    /**
     * Vérifie si le mode sombre est actif
     */
    public static boolean isDarkModeEnabled() {
        return isDarkMode;
    }
    
    /**
     * Configure le mode à utiliser
     */
    public static void setDarkMode(boolean darkMode) {
        isDarkMode = darkMode;
        applyTheme(darkMode);
    }
}