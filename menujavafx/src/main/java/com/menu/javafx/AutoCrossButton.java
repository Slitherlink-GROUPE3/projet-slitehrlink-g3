package com.menu.javafx;

import com.tpgr3.Grille;
import com.tpgr3.Techniques.Tech_0Rule;
import com.tpgr3.Techniques.Tech_0Rule_V2;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import java.util.Map;

/**
 * Classe qui gère le bouton "Croix Auto" pour appliquer automatiquement
 * la technique des 0 et la règle des valeurs maximales dans Slitherlink.
 */
public class AutoCrossButton {
    // Variable statique pour suivre l'état d'activation des croix automatiques
    // Désactivé par défaut (false)
    private static boolean autoCrossEnabled = false;

    /**
     * Crée un bouton qui applique automatiquement la technique des 0.
     * 
     * @param slitherGrid La grille de jeu
     * @param mainLayer Le conteneur principal de l'interface
     * @param gridContainer Le conteneur de la grille
     * @return Le bouton configuré
     */
    public static Button createAutoCrossButton(SlitherGrid slitherGrid, VBox mainLayer, StackPane gridContainer) {
        // Initialisation avec l'état "OFF" puisque autoCrossEnabled est false par défaut
        Button crossAutoButton = Util.createStyledButton("Croix Auto: ON", false, SlitherGrid.ACCENT_COLOR,
                SlitherGrid.ACCENT_COLOR, SlitherGrid.SECONDARY_COLOR);

        // Style initial pour le bouton OFF
        crossAutoButton.setStyle(
            "-fx-background-color: " + SlitherGrid.SECONDARY_COLOR + ";" +
            "-fx-background-radius: 30;" +
            "-fx-border-color: " + SlitherGrid.ACCENT_COLOR + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 30;" +
            "-fx-text-fill: " + SlitherGrid.ACCENT_COLOR + ";" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 16px;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;"
        );
        
        crossAutoButton.setOnAction(e -> {
            Util.animateButtonClick(crossAutoButton);
            
            if (autoCrossEnabled) {
                // Désactiver les croix automatiques
                autoCrossEnabled = false;
                crossAutoButton.setText("Croix Auto: ON");
                crossAutoButton.setStyle(
                    "-fx-background-color: " + SlitherGrid.SECONDARY_COLOR + ";" +
                    "-fx-background-radius: 30;" +
                    "-fx-border-color: " + SlitherGrid.ACCENT_COLOR + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 30;" +
                    "-fx-text-fill: " + SlitherGrid.ACCENT_COLOR + ";" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 16px;" +
                    "-fx-padding: 10 20;" +
                    "-fx-cursor: hand;"
                );
                
                showNotification(mainLayer, gridContainer, "Mode croix automatiques activé", 
                        SlitherGrid.ACCENT_COLOR);
            } else {
                // Réactiver les croix automatiques
                autoCrossEnabled = true;
                crossAutoButton.setText("Croix Auto: OFF");
                crossAutoButton.setStyle(
                    "-fx-background-color: " + SlitherGrid.SECONDARY_COLOR + ";" +
                    "-fx-background-radius: 30;" +
                    "-fx-border-color: " + SlitherGrid.MAIN_COLOR + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 30;" +
                    "-fx-text-fill: " + SlitherGrid.DARK_COLOR + ";" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 16px;" +
                    "-fx-padding: 10 20;" +
                    "-fx-cursor: hand;"
                );
                
                // Appliquer les techniques immédiatement
                applyTech0Rule(slitherGrid, mainLayer, gridContainer);
                applyMaxValueRule(slitherGrid, mainLayer, gridContainer);
                
                showNotification(mainLayer, gridContainer, "Mode croix automatiques désactivé", 
                        SlitherGrid.MAIN_COLOR);
            }
        });
        
        return crossAutoButton;
    }
    
    /**
     * Vérifie si le mode de croix automatiques est activé.
     * Cette méthode sera appelée depuis SlitherGrid pour savoir
     * si une croix doit être placée automatiquement lors d'un clic.
     * 
     * @return true si le mode est activé, false sinon
     */
    public static boolean isAutoCrossEnabled() {
        return autoCrossEnabled;
    }
    
    /**
     * Applique la technique des 0 sur la grille.
     */
    private static void applyTech0Rule(SlitherGrid slitherGrid, VBox mainLayer, StackPane gridContainer) {
        // Créer une instance de Tech_0Rule avec les données actuelles de la grille
        Tech_0Rule_V2 tech0Rule = new Tech_0Rule_V2(
            slitherGrid.getGridNumbers(),
            slitherGrid.gridLines,
            slitherGrid.getSlitherlinkGrid(),
            slitherGrid  // La référence à SlitherGrid est nécessaire
        );
        
        // Créer une instance de Grille à partir des données de SlitherGrid
        Grille grille = new Grille(slitherGrid.getGridNumbers());
        
        // Vérifier si la technique est applicable avant de l'appliquer
        if (tech0Rule.estApplicable(grille)) {
            // Appliquer la technique
            tech0Rule.appliquer(grille);
            
            // Notification visuelle que la technique a été appliquée
            showNotification(mainLayer, gridContainer, "Technique des 0 appliquée", 
                    SlitherGrid.MAIN_COLOR);
        }
    }
    
    /**
     * Applique la règle des valeurs maximales sur la grille.
     * Pour chaque cellule qui a déjà atteint sa valeur, place des croix sur
     * les segments restants.
     */
    private static void applyMaxValueRule(SlitherGrid slitherGrid, VBox mainLayer, StackPane gridContainer) {
        int[][] gridNumbers = slitherGrid.getGridNumbers();
        Map<String, Line> gridLines = slitherGrid.gridLines;
        boolean croixPlacees = false;
        
        // Parcourir toutes les cellules
        for (int row = 0; row < gridNumbers.length; row++) {
            for (int col = 0; col < gridNumbers[0].length; col++) {
                int valeurCellule = gridNumbers[row][col];
                if (valeurCellule <= 0) continue;  // Ignorer les cellules sans valeur ou avec 0
                
                // Compter les traits autour de la cellule
                int nbTraits = 0;
                if (aTrait(gridLines, "H_" + row + "_" + col)) nbTraits++;
                if (aTrait(gridLines, "H_" + (row + 1) + "_" + col)) nbTraits++;
                if (aTrait(gridLines, "V_" + row + "_" + col)) nbTraits++;
                if (aTrait(gridLines, "V_" + row + "_" + (col + 1))) nbTraits++;
                
                // Si la cellule a atteint sa valeur, placer des croix sur les segments vides
                if (nbTraits == valeurCellule) {
                    croixPlacees |= placerCroixAutourCellule(slitherGrid, row, col);
                }
            }
        }
        
        // Si des croix ont été placées, afficher une notification
        if (croixPlacees) {
            showNotification(mainLayer, gridContainer, "Croix placées pour les valeurs complètes", 
                    SlitherGrid.MAIN_COLOR);
        }
    }
    
    /**
     * Place des croix autour d'une cellule sur tous les segments sans trait.
     */
    private static boolean placerCroixAutourCellule(SlitherGrid slitherGrid, int row, int col) {
        boolean croixPlacees = false;
        Map<String, Line> gridLines = slitherGrid.gridLines;
        
        // Les 4 segments autour de la cellule
        String[] segments = {
            "H_" + row + "_" + col,         // Segment du haut
            "H_" + (row + 1) + "_" + col,   // Segment du bas
            "V_" + row + "_" + col,         // Segment de gauche
            "V_" + row + "_" + (col + 1)    // Segment de droite
        };
        
        for (String segmentId : segments) {
            Line line = gridLines.get(segmentId);
            if (line != null && line.getStroke() == Color.TRANSPARENT && !hasCross(slitherGrid, line)) {
                // Placer une croix
                new CreateCrossMove(line, "max_value_rule", Color.BLACK, slitherGrid);
                croixPlacees = true;
            }
        }
        
        return croixPlacees;
    }
    
    /**
     * Vérifie si un segment a un trait.
     */
    private static boolean aTrait(Map<String, Line> gridLines, String lineId) {
        Line line = gridLines.get(lineId);
        if (line == null) return false;
        
        return line.getStroke() != Color.TRANSPARENT;
    }
    
    /**
     * Vérifie si un segment a une croix.
     */
    private static boolean hasCross(SlitherGrid slitherGrid, Line line) {
        return slitherGrid.getSlitherlinkGrid().getChildren().stream()
                .anyMatch(node -> node instanceof Line && node.getUserData() == line);
    }
    
    /**
     * Affiche une notification temporaire sur l'écran.
     */
    private static void showNotification(VBox mainLayer, StackPane gridContainer, 
                                        String message, String color) {
        Label notificationLabel = new Label(message);
        notificationLabel.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10;" +
            "-fx-background-radius: 5;"
        );
        
        StackPane notificationPane = new StackPane(notificationLabel);
        notificationPane.setLayoutX(gridContainer.getLayoutX() + gridContainer.getWidth()/2 - 100);
        notificationPane.setLayoutY(gridContainer.getLayoutY() + 20);
        notificationPane.setOpacity(0);
        
        mainLayer.getChildren().add(notificationPane);
        
        // Animation de la notification
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notificationPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        // Disparition après 2 secondes
        PauseTransition pause = new PauseTransition(Duration.millis(2000));
        pause.setOnFinished(event -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), notificationPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(evt -> mainLayer.getChildren().remove(notificationPane));
            fadeOut.play();
        });
        pause.play();
    }
}