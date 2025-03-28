// Common imports for Technique classes
package com.tpgr3.Techniques;

import java.util.Map;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

import java.util.List;

public class Tech_0And3Adjacent implements Techniques {

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
    public Tech_0And3Adjacent(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
    }

    @Override
    public boolean estApplicable(Grille grille) {
        for (int i = 0; i < gridNumbers.length; i++) {
            for (int j = 0; j < gridNumbers[i].length; j++) {
                // Cherche un 3 dans la grille
                if (gridNumbers[i][j] == 3) {
                    // Vérifie s'il y a un 0 adjacent
                    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Gauche, Droite, Haut, Bas
                    
                    for (int[] dir : directions) {
                        int ni = i + dir[0];
                        int nj = j + dir[1];
                        
                        // Vérifie si les indices sont valides
                        if (ni >= 0 && ni < gridNumbers.length && nj >= 0 && nj < gridNumbers[i].length) {
                            // Vérifie s'il y a un 0 à cette position
                            if (gridNumbers[ni][nj] == 0) {
                                // Détermine la direction du 0 par rapport au 3
                                String directionFromThree = "";
                                if (dir[0] == -1) directionFromThree = "Gauche";
                                else if (dir[0] == 1) directionFromThree = "Droite";
                                else if (dir[1] == -1) directionFromThree = "Haut";
                                else directionFromThree = "Bas";
                                
                                // Vérifie si les segments autour du 3 sont neutres
                                int lineCount = 0;
                                int neutralCount = 0;
                                
                                // Vérifie les segments autour du 3
                                String[] segmentsAroundThree = {
                                    "H_" + i + "_" + j,       // Haut
                                    "H_" + (i+1) + "_" + j,   // Bas
                                    "V_" + i + "_" + j,       // Gauche
                                    "V_" + i + "_" + (j+1)    // Droite
                                };
                                
                                // Exclut le segment entre le 3 et le 0
                                String segmentBetweenThreeAndZero = "";
                                if (directionFromThree.equals("Haut")) segmentBetweenThreeAndZero = "H_" + i + "_" + j;
                                else if (directionFromThree.equals("Bas")) segmentBetweenThreeAndZero = "H_" + (i+1) + "_" + j;
                                else if (directionFromThree.equals("Gauche")) segmentBetweenThreeAndZero = "V_" + i + "_" + j;
                                else segmentBetweenThreeAndZero = "V_" + i + "_" + (j+1);
                                
                                for (String segmentKey : segmentsAroundThree) {
                                    if (!segmentKey.equals(segmentBetweenThreeAndZero)) {
                                        Line segment = gridLines.get(segmentKey);
                                        if (segment != null) {
                                            if (segment.getStroke() != Color.TRANSPARENT) {
                                                lineCount++;
                                            } else {
                                                // Vérifie si le segment a une croix
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
                                }
                                
                                // Si au moins un segment autour du 3 ne sont pas encore des lignes,
                                // la technique est applicable
                                if (neutralCount > 0) {
                                    return true;
                                }
                            }
                        }
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