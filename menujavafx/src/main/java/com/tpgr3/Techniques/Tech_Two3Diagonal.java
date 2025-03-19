// Common imports for Technique classes
package com.tpgr3.Techniques;

import java.util.Map;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

public class Tech_Two3Diagonal implements Techniques {

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
    public Tech_Two3Diagonal(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
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
                    // Vérifie s'il y a un autre 3 en diagonale
                    int[][] diagonales = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
                    
                    for (int[] diag : diagonales) {
                        int xB = i + diag[0] * 2;
                        int yB = j + diag[1] * 2;

                        // Vérifie si les indices sont valides
                        if (xB >= 0 && xB < gridNumbers.length && 
                            yB >= 0 && yB < gridNumbers[i].length) {
                            
                            // Vérifie s'il y a un 3 à cette position
                            if (gridNumbers[xB][yB] == 3) {
                                // Vérifie les segments intermédiaires
                                String[] segmentsToCheck = {
                                    "H_" + (i + diag[0]) + "_" + j,
                                    "V_" + i + "_" + (j + diag[1])
                                };
                                
                                // Vérifie si au moins un des segments est neutre
                                for (String segmentKey : segmentsToCheck) {
                                    Line segment = gridLines.get(segmentKey);
                                    if (segment != null && segment.getStroke() == Color.TRANSPARENT) {
                                        boolean hasCross = false;
                                        for (javafx.scene.Node node : slitherlinkGrid.getChildren()) {
                                            if (node instanceof Line && node.getUserData() == segment) {
                                                hasCross = true;
                                                break;
                                            }
                                        }
                                        
                                        if (!hasCross) {
                                            return true; // Un segment neutre existe
                                        }
                                    }
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