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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SettingScene {

    // Variable pour suivre le mode actuel
    private static boolean isDarkMode = false;
    
    // Constantes de couleurs du Mode Clair
    private static final String LIGHT_MAIN_COLOR = "#3A7D44";     // Vert principal
    private static final String LIGHT_SECONDARY_COLOR = "#F2E8CF"; // Beige clair
    private static final String LIGHT_ACCENT_COLOR = "#BC4749";    // Rouge-brique
    private static final String LIGHT_DARK_COLOR = "#386641";      // Vert foncé
    private static final String LIGHT_LIGHT_COLOR = "#A7C957";     // Vert clair
    
    // Constantes de couleurs du Mode Sombre
    private static final String DARK_MAIN_COLOR = "#4c8b5d";      // Vert principal (plus lumineux)
    private static final String DARK_SECONDARY_COLOR = "#1a1a1a";  // Gris très foncé
    private static final String DARK_ACCENT_COLOR = "#e05d5f";     // Rouge-brique (plus lumineux)
    private static final String DARK_DARK_COLOR = "#2d3b2d";       // Vert très foncé
    private static final String DARK_LIGHT_COLOR = "#6a8844";      // Vert clair adapté
    
    // Couleurs actuelles (selon le mode)
    private static String MAIN_COLOR = LIGHT_MAIN_COLOR;
    private static String SECONDARY_COLOR = LIGHT_SECONDARY_COLOR;
    private static String ACCENT_COLOR = LIGHT_ACCENT_COLOR;
    private static String DARK_COLOR = LIGHT_DARK_COLOR;
    private static String LIGHT_COLOR = LIGHT_LIGHT_COLOR;

    public static void show(Stage primaryStage) {
        // Appliquer le thème actuel
        applyTheme(isDarkMode);
        
        // Conteneur principal
        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background-color: " + SECONDARY_COLOR + ";");
        mainContainer.setAlignment(Pos.TOP_CENTER);
        
        // Titre principal
        Label settingsTitle = new Label("Paramètres");
        settingsTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 36));
        
        // Effet de dégradé pour le texte
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(MAIN_COLOR)),
                new Stop(0.5, Color.web(DARK_COLOR)),
                new Stop(1, Color.web(MAIN_COLOR)));
        settingsTitle.setTextFill(gradient);
        
        // Ajouter des effets visuels au titre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(DARK_COLOR, 0.5));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        
        Glow glow = new Glow(0.4);
        glow.setInput(shadow);
        settingsTitle.setEffect(glow);
        
        // Conteneur de titre avec padding
        StackPane titleContainer = new StackPane(settingsTitle);
        titleContainer.setPadding(new Insets(40, 0, 30, 0));
        
        // Conteneur principal pour les boutons
        VBox settingsBox = new VBox(15);
        settingsBox.setAlignment(Pos.CENTER);
        settingsBox.setPadding(new Insets(30));
        settingsBox.setMaxWidth(500);
        
        // Style du conteneur selon le mode
        String boxBackground = isDarkMode ? "rgba(40, 40, 40, 0.8)" : "rgba(255, 255, 255, 0.8)";
        settingsBox.setStyle(
                "-fx-background-color: " + boxBackground + ";" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 5);");
        
        // Créer les boutons de paramètres
        Button mainMenuButton = Util.createStyledButton("Menu principal", false, MAIN_COLOR, DARK_COLOR, SECONDARY_COLOR);
        Button saveButton = Util.createStyledButton("Sauvegarder", true, MAIN_COLOR, DARK_COLOR, SECONDARY_COLOR);
        Button changeAccountButton = Util.createStyledButton("Changer de compte", false, MAIN_COLOR, DARK_COLOR, SECONDARY_COLOR);
        
        // Boutons de mode avec état actif/inactif selon le mode courant
        //Button darkModeButton = Util.createStyledButton("Mode sombre", isDarkMode, MAIN_COLOR, DARK_COLOR, SECONDARY_COLOR);
        //Button lightModeButton = Util.createStyledButton("Mode clair", !isDarkMode, MAIN_COLOR, DARK_COLOR, SECONDARY_COLOR);
        
        // Désactiver le bouton du mode actif
        /*if (isDarkMode) {
            darkModeButton.setDisable(true);
        } else {
            lightModeButton.setDisable(true);
        }*/
        
        Button quitButton = Util.createStyledButton("Quitter", true, MAIN_COLOR, DARK_COLOR, SECONDARY_COLOR);
        
        // Configurer les actions des boutons
        mainMenuButton.setOnAction(e -> {
            Util.animateButtonClick(mainMenuButton);
            Menu.show(primaryStage);
        });
        
        saveButton.setOnAction(e -> {
            Util.animateButtonClick(saveButton);
            // Logique de sauvegarde à implémenter

        });
        
       
        
        // Dans la section où les actions des boutons sont configurées
        // Modifier l'action du bouton "Changer de compte" :
        changeAccountButton.setOnAction(e -> {
            Util.animateButtonClick(changeAccountButton);
            
            // Afficher l'écran de connexion pour changer de compte
            LoginScene.show(primaryStage);
            
            // Vous pouvez également ajouter un message optionnel pour indiquer
            // à LoginScene qu'il s'agit d'un changement de compte et non d'une
            // première connexion, par exemple :
            // LoginScene.show(primaryStage, true); // true indique un changement de compte
        });

        /* 
        // Action pour activer le mode sombre
        darkModeButton.setOnAction(e -> {
            Util.animateButtonClick(darkModeButton);
            isDarkMode = true;
            saveThemePreference(true); // Sauvegarde la préférence
            
            // Update any open scenes
            GameScene.updateTheme(primaryStage);
            
            // Refresh the settings scene
            show(primaryStage);
        });
        
        // Action pour activer le mode clair
        lightModeButton.setOnAction(e -> {
            Util.animateButtonClick(lightModeButton);
            isDarkMode = false;
            saveThemePreference(false); // Sauvegarde la préférence
            show(primaryStage); // Rafraîchit l'interface avec le nouveau thème
        });*/
        
        quitButton.setOnAction(e -> {
            Util.animateButtonClick(quitButton);
            primaryStage.close();
        });
        
        // Ajouter des séparateurs entre certains boutons
        settingsBox.getChildren().addAll(
                mainMenuButton,
                createSeparator(),
                saveButton,
                changeAccountButton,
                //createSeparator(),
                //darkModeButton,
                //lightModeButton,
                createSeparator(),
                quitButton
        );
        
        // Conteneur pour centrer la boîte de paramètres
        StackPane centerPane = new StackPane(settingsBox);
        centerPane.setPadding(new Insets(0, 0, 40, 0));
        
        // Assembler l'interface
        mainContainer.getChildren().addAll(titleContainer, centerPane);
        
        // Arrière-plan avec dégradé adapté au mode
        String gradientStyle = isDarkMode 
            ? "linear-gradient(to bottom right, " + DARK_SECONDARY_COLOR + ", " + DARK_DARK_COLOR + " 70%);"
            : "linear-gradient(to bottom right, " + SECONDARY_COLOR + ", " + LIGHT_COLOR + " 70%);";
            
        mainContainer.setStyle(
                "-fx-background-color: " + gradientStyle +
                "-fx-background-radius: 0;" +
                "-fx-padding: 20px;");

        // Créer la scène
        Scene scene = new Scene(mainContainer, Screen.getPrimary().getVisualBounds().getWidth(), 
                Screen.getPrimary().getVisualBounds().getHeight());
        
        // Animation d'entrée
        mainContainer.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Slitherlink - Paramètres");
        primaryStage.setMaximized(true);
        primaryStage.show();
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
     * Sauvegarde la préférence du thème (pourrait être étendu pour utiliser des préférences persistantes)
     * @param darkMode true pour le mode sombre, false pour le mode clair
     */
    private static void saveThemePreference(boolean darkMode) {
        isDarkMode = darkMode;
        // Ici, vous pourriez implémenter une sauvegarde persistante
        // Par exemple avec les Preferences API de Java
        System.out.println("Thème " + (darkMode ? "sombre" : "clair") + " activé");
    }

    /**
     * Crée un séparateur visuel pour les groupes de boutons
     */
    private static Rectangle createSeparator() {
        Rectangle separator = new Rectangle();
        separator.setHeight(2);
        separator.setWidth(450);
        separator.setFill(Color.web(LIGHT_COLOR, 0.5));
        separator.setArcWidth(2);
        separator.setArcHeight(2);
        return separator;
    }
    
    /**
     * Vérifie si le mode sombre est actif (peut être appelé par d'autres classes)
     * @return true si le mode sombre est actif, false sinon
     */
    public static boolean isDarkModeEnabled() {
        return isDarkMode;
    }
    
    /**
     * Configure le mode à utiliser (peut être appelé par d'autres classes)
     * @param darkMode true pour activer le mode sombre, false pour le mode clair
     */
    public static void setDarkMode(boolean darkMode) {
        isDarkMode = darkMode;
        applyTheme(darkMode);
    }
}