package com.tpgr3.Techniques;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import com.tpgr3.Grille;

/**
 * Implémentation de la technique des 3 adjacents en diagonal dans Slitherlink.
 * Quand deux cases contenant un 3 sont adjacentes, il y a une configuration 
 * spécifique obligatoire pour éviter les conflits.
 */
public class Tech_Two3Diagonal implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsToHighlight;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_Two3Diagonal() {
        segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Constructeur avec les informations nécessaires pour l'analyse.
     * 
     * @param gridNumbers Tableau 2D représentant les chiffres de la grille
     * @param gridLines Map des lignes de la grille
     * @param slitherlinkGrid Pane contenant la grille de Slitherlink
     */
    public Tech_Two3Diagonal(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Vérifie si la technique des 3 adjacents en diagonale est applicable sur la grille actuelle.
     *
     * @param grille La grille de jeu Slitherlink
     * @return true si la technique est applicable, false sinon
     */
    @Override
    public boolean estApplicable(Grille grille) {

        if (gridNumbers == null) {
            gridNumbers = grille.valeurs;
        }
        
        segmentsToHighlight.clear();
        
        boolean applicationPossible = false;
        for (int i = 0; i < gridNumbers.length-1; i++) {
            for (int j = 0; j < gridNumbers[i].length-1; j++) {
                if (gridNumbers[i][j] == 3 && gridNumbers[i+1][j+1] == 3) {
                    // Deux 3 adjacents verticalement
                    // Déterminer les segments à marquer comme bâtons
                    String[] segmentsForBaton = {
                        "H_" + i + "_" + j,         
                        "H_" + (i+2) + "_" + (j+1),      
                        "V_" + i + "_" + j,
                        "V_" + (i+1) + "_" + (j+2)         
                    };
                    // Vérifier si les segments sont disponibles pour être marqués
                    for (String segmentKey : segmentsForBaton) {
                        Line segment = gridLines.get(segmentKey);
                        if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                            segmentsToHighlight.add(segmentKey + ":baton");
                            applicationPossible = true;
                        }
                    }
                    return applicationPossible;
                }       
            }
        }
        for (int i = 0; i < gridNumbers.length-1; i++) {
            for (int j = 1; j < gridNumbers[i].length-1; j++) {
                if(gridNumbers[i][j] == 3 && gridNumbers[i+1][j-1] == 3){
                    // Deux 3 adjacents verticalement
                    // Déterminer les segments à marquer comme bâtons
                    String[] segmentsForBaton = {
                        "H_" + i + "_" + j,         
                        "H_" + (i+2) + "_" + (j-1),      
                        "V_" + i + "_" + (j+1),
                        "V_" + (i+1) + "_" + (j-1)         
                    };
                    
                    // Vérifier si les segments sont disponibles pour être marqués
                    for (String segmentKey : segmentsForBaton) {
                        Line segment = gridLines.get(segmentKey);
                        if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                            segmentsToHighlight.add(segmentKey + ":baton");
                            applicationPossible = true;
                        }
                    }
                    return applicationPossible;
                }
            }
        }
        return applicationPossible;
    }

    
    /**
     * Vérifie si un segment a déjà une croix.
     *
     * @param line Le segment à vérifier
     * @return true si le segment a une croix, false sinon
     */
    private boolean aCroix(Line line) {
        if (line == null || slitherlinkGrid == null) return false;
        
        for (javafx.scene.Node node : slitherlinkGrid.getChildren()) {
            if (node instanceof Line && node.getUserData() == line) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Applique la technique en mettant en surbrillance les segments qui doivent être marqués.
     *
     * @param grille La grille de jeu Slitherlink
     */
    @Override
    public void appliquer(Grille grille) {
        if (!estApplicable(grille)) {
            return;
        }
        
        for (String segment : segmentsToHighlight) {
            String[] parts = segment.split(":");
            String lineId = parts[0];
            
            Line line = gridLines.get(lineId);
            if (line != null) {
                Glow glow = new Glow(0.8);
                line.setEffect(glow);
                line.setStroke(Color.GREEN);
                line.setOpacity(0.7);
                
                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), line);
                fadeIn.setFromValue(0.3);
                fadeIn.setToValue(0.9);
                fadeIn.setCycleCount(3);
                fadeIn.setAutoReverse(true);
                fadeIn.play();
                
                fadeIn.setOnFinished(event -> {
                    line.setEffect(null);
                    line.setStroke(Color.TRANSPARENT);
                    line.setOpacity(1.0);
                });
            }
        }
    }
}