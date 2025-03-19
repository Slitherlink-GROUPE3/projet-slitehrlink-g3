// Common imports for Technique classes
package com.tpgr3.Techniques;

import java.util.Map;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

import java.util.List;

public class Tech_0Rule implements Techniques {

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
    public Tech_0Rule(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
    }

    @Override
    public boolean estApplicable(Grille grille) {
        for (int i = 0; i < gridNumbers.length; i++) {
            for (int j = 0; j < gridNumbers[i].length; j++) {
                if (gridNumbers[i][j] == 0) {
                    // Vérifie si tous les segments autour du 0 ont des croix
                    boolean allSegmentsHaveCrosses = true;
                    boolean atLeastOneSegmentNeutral = false;
                    
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
                                // Une ligne existe - la règle du 0 est violée
                                allSegmentsHaveCrosses = false;
                                break;
                            }
                            
                            // Vérifie si le segment n'a pas de croix
                            boolean hasCross = false;
                            for (javafx.scene.Node node : slitherlinkGrid.getChildren()) {
                                if (node instanceof Line && node.getUserData() == segment) {
                                    hasCross = true;
                                    break;
                                }
                            }
                            
                            if (!hasCross) {
                                atLeastOneSegmentNeutral = true;
                            }
                        }
                    }
                    
                    // Si tous les segments sont neutres ou ont des croix, et au moins un est neutre, la règle est applicable
                    if (allSegmentsHaveCrosses && atLeastOneSegmentNeutral) {
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