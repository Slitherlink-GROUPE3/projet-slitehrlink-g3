// Common imports for Technique classes
package com.tpgr3.Techniques;

import java.util.Map;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

import java.util.ArrayList;
import java.util.List;

public class Tech_CornerRule implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;

    /**
     * Constructeur avec les informations nécessaires pour l'analyse.
     * 
     * @param gridNumbers Tableau 2D représentant les chiffres de la grille
     * @param gridLines Map des lignes de la grille
     * @param slitherlinkGrid Pane contenant la grille de Slitherlink
     */
    public Tech_CornerRule(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
    }

    @Override
    public boolean estApplicable(Grille grille) {
        // Récupère les dimensions de la grille
        int rows = gridNumbers.length;
        int cols = gridNumbers[0].length;
        
        // Vérifie les 4 coins de la grille
        int[][] cornerCoords = {
            {0, 0},             // Coin supérieur gauche
            {0, cols - 1},      // Coin supérieur droit
            {rows - 1, 0},      // Coin inférieur gauche
            {rows - 1, cols - 1} // Coin inférieur droit
        };
        
        for (int[] corner : cornerCoords) {
            int i = corner[0];
            int j = corner[1];
            
            if (gridNumbers[i][j] != -1) { // -1 indique une case vide
                int value = gridNumbers[i][j];
                
                // Pour les coins, les règles sont spécifiques selon la valeur
                if (value == 0 || value == 1 || value == 2) {
                    // Identifie les segments adjacents au coin
                    String[] adjacentSegments = new String[2];
                    
                    if (i == 0 && j == 0) { // Coin supérieur gauche
                        adjacentSegments[0] = "H_0_0"; // Haut
                        adjacentSegments[1] = "V_0_0"; // Gauche
                    } else if (i == 0 && j == cols - 1) { // Coin supérieur droit
                        adjacentSegments[0] = "H_0_" + j; // Haut
                        adjacentSegments[1] = "V_0_" + (j+1); // Droite
                    } else if (i == rows - 1 && j == 0) { // Coin inférieur gauche
                        adjacentSegments[0] = "H_" + (i+1) + "_0"; // Bas
                        adjacentSegments[1] = "V_" + i + "_0"; // Gauche
                    } else if(i == rows - 1 && j == cols - 1){ // Coin inférieur droit
                        adjacentSegments[0] = "H_" + (i+1) + "_" + j; // Bas
                        adjacentSegments[1] = "V_0_" + (j+1); // Droite
                    }
                    
                    // Vérifie si au moins un segment est neutre
                    int lineCount = 0;
                    int neutralCount = 0;
                    
                    for (String segmentKey : adjacentSegments) {
                        Line segment = gridLines.get(segmentKey);
                        if (segment != null) {
                            if (segment.getStroke() != Color.TRANSPARENT) {
                                lineCount++;
                            } else {
                                boolean hasCross = false;
                                for (javafx.scene.Node node : slitherlinkGrid.getChildren()) {
                                    if (node instanceof Line && node.getUserData() == segment) {
                                        hasCross = true;
                                        break;
                                    }
                                }
                                
                                if (!hasCross) {
                                    neutralCount++;
                                }
                            }
                        }
                    }
                    
                    // Les règles spécifiques pour chaque valeur de coin
                    if (value == 0 && neutralCount > 0) {
                        return true; // Pour un 0 dans un coin, tous les segments doivent être des croix
                    } else if (value == 1 && lineCount < 1 && neutralCount > 0) {
                        return true; // Pour un 1 dans un coin, exactement un segment doit être une ligne
                    } else if (value == 2 && lineCount < 2 && neutralCount > 0) {
                        return true; // Pour un 2 dans un coin, les deux segments doivent être des lignes
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void appliquer(Grille grille) {
        
    }
}