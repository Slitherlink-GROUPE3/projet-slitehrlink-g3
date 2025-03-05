package com.menu.javafx;

import com.menu.ButtonFactory;
import com.menu.GameScene;
import com.menu.Menu;
import com.menu.SettingScene;

import javafx.scene.Scene;
import javafx.scene.control.Button; 
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class PauseMenu {

    public static void show(Stage primaryStage) {
        // Conteneur principal pour le menu de pause
        VBox pauseMenu = new VBox(20);
        pauseMenu.setStyle("-fx-background-color: #E5D5B0; -fx-padding: 20;");
        pauseMenu.setAlignment(javafx.geometry.Pos.CENTER);

        // Bouton "Reprendre"
        Button resumeButton = ButtonFactory.createStyledButton("Reprendre");
        resumeButton.setOnAction(e -> {
            // Ferme le menu de pause et revient au jeu
            GameScene.show(primaryStage);  
        });

        // Bouton "Tutoriel"
        Button tutorialButton = ButtonFactory.createStyledButton("Tutoriel");
        tutorialButton.setOnAction(e -> {
            hintScene.show(primaryStage); // Appelle la scène du tutoriel
        });

        // Bouton "Paramètres"
        Button settingsButton = ButtonFactory.createStyledButton("Paramètres");
        settingsButton.setOnAction(e -> {
            SettingScene.show(primaryStage); // Appelle la scène des paramètres
        });

        

        // Bouton "Quitter la partie"
        Button quitButton = ButtonFactory.createStyledButton("Quitter la partie");
        quitButton.setOnAction(e -> {
            // Revient au menu principal sans fermer l'application
            Menu.show(primaryStage); 
        });

        // Ajouter tous les boutons au menu de pause
        pauseMenu.getChildren().addAll(resumeButton,tutorialButton, settingsButton, quitButton);

        // Créer une scène pour le menu de pause
        Scene pauseScene = new Scene(pauseMenu, 400, 400);
        primaryStage.setScene(pauseScene);
        primaryStage.setTitle("Pause Menu");
        primaryStage.show();
    }
}
