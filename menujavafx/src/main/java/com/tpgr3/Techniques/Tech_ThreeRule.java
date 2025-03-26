package com.tpgr3.Techniques;

 


 

import java.util.Map;
 

import javafx.scene.shape.Line;
 

import javafx.scene.layout.Pane;
 

import javafx.scene.paint.Color;
 


 

import com.tpgr3.*;
 

import com.tpgr3.Marque.*;
 


 

public class Tech_ThreeRule implements Techniques {
 


 

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
 

    public Tech_ThreeRule(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
 

        this.gridNumbers = gridNumbers;
 

        this.gridLines = gridLines;
 

        this.slitherlinkGrid = slitherlinkGrid;
 

    }
 


 

    @Override
 

    public boolean estApplicable(Grille grille) {
 

        for (int i = 0; i < gridNumbers.length; i++) {
 

            for (int j = 0; j < gridNumbers[i].length; j++) {
 

                if (gridNumbers[i][j] == 3) {
 

                    // Compter les lignes et croix autour du 3
 

                    int lineCount = 0;
 

                    int crossCount = 0;
 

                    int neutralCount = 0;
 

                    
 

                    // Vérifier les 4 segments adjacents
 

                    String[] adjacentSegments = {
 

                        "H_" + i + "_" + j,       // Haut
 

                        "H_" + (i+1) + "_" + j,   // Bas
 

                        "V_" + i + "_" + j,       // Gauche
 

                        "V_" + i + "_" + (j+1)    // Droite
 

                    };
 

                    
 

                    for (String segmentKey : adjacentSegments) {
 

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
 

                                        crossCount++;
 

                                        break;
 

                                    }
 

                                }
 

                                
 

                                if (!hasCross) {
 

                                    neutralCount++;
 

                                }
 

                            }
 

                        }
 

                    }
 

                    
 

                    // Cas 1: Si déjà 2 lignes et 1 croix, le dernier segment doit être une ligne
 

                    // Cas 2: Si déjà 2 lignes et 1 neutre, le dernier segment doit être une ligne
 

                    // Cas 3: Si 1 croix et 3 neutres, tous les segments neutres doivent être des lignes
 

                    if ((lineCount == 2 && crossCount == 1 && neutralCount == 1) ||
 

                        (lineCount == 2 && crossCount == 0 && neutralCount == 2) ||
 

                        (lineCount == 0 && crossCount == 1 && neutralCount == 3)) {
 

                        return true;
 

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
