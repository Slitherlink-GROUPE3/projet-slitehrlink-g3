// Common imports for Technique classes
package com.tpgr3.Techniques;

import java.util.Map;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

public class Tech_0And3Diagonal implements Techniques {

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
    public Tech_0And3Diagonal(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
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
                    // Vérifie s'il y a un 0 en diagonale
                    int[][] diagonales = {{2, 2}, {-2, 2}, {-2, -2}, {2, -2}};
                    
                    for (int[] diag : diagonales) {
                        int x0 = i + diag[0];
                        int y0 = j + diag[1];

                        // Vérifie si les indices sont valides
                        if (x0 >= 0 && x0 < gridNumbers.length && 
                            y0 >= 0 && y0 < gridNumbers[i].length) {
                            
                            // Vérifie s'il y a un 0 à cette position
                            if (gridNumbers[x0][y0] == 0) {
                                // Vérifie si les segments à marquer sont neutres
                                // Les segments dépendent de la direction de la diagonale
                                String[] segmentsToCheck = new String[4];
                                
                                if (diag[0] == 2 && diag[1] == 2) { // Bas-Droite
                                    segmentsToCheck[0] = "H_" + (i+1) + "_" + j;   // Bas du 3
                                    segmentsToCheck[1] = "V_" + i + "_" + (j+1);   // Droite du 3
                                    segmentsToCheck[2] = "H_" + i + "_" + j;       // Haut du 3 (à croiser)
                                    segmentsToCheck[3] = "V_" + i + "_" + j;       // Gauche du 3 (à croiser)
                                } else if (diag[0] == -2 && diag[1] == 2) { // Bas-Gauche
                                    segmentsToCheck[0] = "H_" + (i+1) + "_" + j;   // Bas du 3
                                    segmentsToCheck[1] = "V_" + i + "_" + j;       // Gauche du 3
                                    segmentsToCheck[2] = "H_" + i + "_" + j;       // Haut du 3 (à croiser)
                                    segmentsToCheck[3] = "V_" + i + "_" + (j+1);   // Droite du 3 (à croiser)
                                } else if (diag[0] == -2 && diag[1] == -2) { // Haut-Gauche
                                    segmentsToCheck[0] = "H_" + i + "_" + j;       // Haut du 3
                                    segmentsToCheck[1] = "V_" + i + "_" + j;       // Gauche du 3
                                    segmentsToCheck[2] = "H_" + (i+1) + "_" + j;   // Bas du 3 (à croiser)
                                    segmentsToCheck[3] = "V_" + i + "_" + (j+1);   // Droite du 3 (à croiser)
                                } else { // Haut-Droite
                                    segmentsToCheck[0] = "H_" + i + "_" + j;       // Haut du 3
                                    segmentsToCheck[1] = "V_" + i + "_" + (j+1);   // Droite du 3
                                    segmentsToCheck[2] = "H_" + (i+1) + "_" + j;   // Bas du 3 (à croiser)
                                    segmentsToCheck[3] = "V_" + i + "_" + j;       // Gauche du 3 (à croiser)
                                }
                                
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