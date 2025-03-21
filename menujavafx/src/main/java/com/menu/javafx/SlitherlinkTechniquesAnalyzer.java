package com.menu.javafx;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;

import com.tpgr3.Techniques.*;
import com.tpgr3.Grille;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlitherlinkTechniquesAnalyzer {
    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private Map<Class<? extends Techniques>, Techniques> techniquesMap;

    public SlitherlinkTechniquesAnalyzer(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        
        // Initialiser les techniques
        initializeTechniques();
    }

    /**
     * Initialise les techniques en utilisant la réflexion pour créer des instances
     */
    private void initializeTechniques() {
        techniquesMap = new HashMap<>();
        
        for (Class<? extends Techniques> techniqueClass : TechniquesPriority.PRIORITY_ORDER) {
            try {
                // Trouver un constructeur qui prend gridNumbers, gridLines, slitherlinkGrid
                Constructor<?> constructor = techniqueClass.getConstructor(int[][].class, Map.class, Pane.class);
                Techniques technique = (Techniques) constructor.newInstance(gridNumbers, gridLines, slitherlinkGrid);
                techniquesMap.put(techniqueClass, technique);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'initialisation de la technique " + techniqueClass.getSimpleName());
                e.printStackTrace();
            }
        }
    }

    /**
     * Analyse et suggère la technique à appliquer
     * @param primaryStage La fenêtre principale
     */
    public void analyzeAndSuggestTechnique(Stage primaryStage) {
        Stage analysisStage = new Stage();
        analysisStage.initModality(Modality.APPLICATION_MODAL);
        analysisStage.initOwner(primaryStage);
        analysisStage.setTitle("Analyse des Techniques de Résolution");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("techniques-analysis-popup");

        Label titleLabel = new Label("Analyse des Techniques de Résolution");
        titleLabel.getStyleClass().add("techniques-title");
        content.getChildren().add(titleLabel);

        Techniques recommendedTechnique = null;

        // Parcourir les techniques dans l'ordre de priorité
        for (Class<? extends Techniques> techniqueClass : TechniquesPriority.PRIORITY_ORDER) {
            Techniques technique = techniquesMap.get(techniqueClass);
            
            try {
                boolean isApplicable = technique.estApplicable(null);
                
                Label techniqueLabel = new Label(
                    techniqueClass.getSimpleName() + 
                    (isApplicable ? " : Applicable" : " : Non applicable")
                );
                
                // Styliser le label selon son applicabilité
                techniqueLabel.getStyleClass().add("technique-item");
                techniqueLabel.getStyleClass().add(
                    isApplicable ? "technique-applicable" : "technique-not-applicable"
                );
                
                content.getChildren().add(techniqueLabel);
                
                // La première technique applicable devient la technique recommandée
                if (isApplicable && recommendedTechnique == null) {
                    recommendedTechnique = technique;
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de l'analyse de " + techniqueClass.getSimpleName());
            }
        }

        // Afficher la technique recommandée
        if (recommendedTechnique != null) {
            Label recommendedLabel = new Label("Technique recommandée : " + 
                recommendedTechnique.getClass().getSimpleName());
            recommendedLabel.getStyleClass().add("technique-recommended");
            content.getChildren().add(recommendedLabel);
        }

        // Bouton pour fermer
        Button closeButton = new Button("Fermer");
        closeButton.setOnAction(e -> analysisStage.close());
        content.getChildren().add(closeButton);

        Scene scene = new Scene(content, 400, 500);
        
        // Charger le CSS
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        analysisStage.setScene(scene);
        analysisStage.show();
    }
}