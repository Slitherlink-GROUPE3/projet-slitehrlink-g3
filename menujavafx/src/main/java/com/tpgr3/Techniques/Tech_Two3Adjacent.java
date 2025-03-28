// Common imports for Technique classes
package com.tpgr3.Techniques;

import java.util.Map;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

public class Tech_Two3Adjacent implements Techniques {

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
    public Tech_Two3Adjacent(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
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
                    // Vérifie s'il y a un autre 3 adjacent
                    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Gauche, Droite, Haut, Bas
                    
                    for (int[] dir : directions) {
                        int xB = i + dir[0];
                        int yB = j + dir[1];
                        
                        // Vérifie si la position voisine est valide
                        if (xB >= 0 && xB < gridNumbers.length && 
                            yB >= 0 && yB < gridNumbers[i].length) {
                            // Vérifie si le voisin est une case avec la valeur 3
                            if (gridNumbers[xB][yB] == 3) {
                                // Calcule les positions des slots à vérifier
                                int dx = (xB - i) / 2;
                                int dy = (yB - j) / 2;

                                // Détermine le segment entre les deux 3
                                String segmentBetweenThrees = "";
                                if (dx != 0) { // Horizontalement
                                    segmentBetweenThrees = (dx > 0) 
                                        ? "V_" + i + "_" + (j+1)  // Droite
                                        : "V_" + i + "_" + j;    // Gauche
                                } else { // Verticalement
                                    segmentBetweenThrees = (dy > 0) 
                                        ? "H_" + (i+1) + "_" + j  // Bas
                                        : "H_" + i + "_" + j;    // Haut
                                }
                                
                                Line segmentBetween = gridLines.get(segmentBetweenThrees);
                                
                                // Vérifie si le segment entre les deux 3 est neutre (pas encore décidé)
                                if (segmentBetween != null && segmentBetween.getStroke() == Color.TRANSPARENT) {
                                    boolean hasCross = false;
                                    for (javafx.scene.Node node : slitherlinkGrid.getChildren()) {
                                        if (node instanceof Line && node.getUserData() == segmentBetween) {
                                            hasCross = true;
                                            break;
                                        }
                                    }
                                    
                                    if (!hasCross) {
                                        // Le segment entre les deux 3 est neutre, la technique est applicable
                                        return true;
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
        for (int y = 0; y < gridNumbers.length; y++) {
            for (int x = 0; x < gridNumbers[y].length; x++) {
                // Cherche un 3 dans la grille
                if (gridNumbers[y][x] == 3) {
                    // Vérifie s'il y a un autre 3 adjacent
                    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Gauche, Droite, Haut, Bas
                    
                    for (int[] dir : directions) {
                        int xB = y + dir[0];
                        int yB = x + dir[1];
                        
                        // Vérifie si la position voisine est valide
                        if (xB >= 0 && xB < gridNumbers.length && 
                            yB >= 0 && yB < gridNumbers[y].length) {
                            // Vérifie si le voisin est une case avec la valeur 3
                            if (gridNumbers[xB][yB] == 3) {
                                // Calcule les positions des slots à vérifier
                                int dx = (xB - y) / 2;
                                int dy = (yB - x) / 2;

                                // Déterminer les segments à marquer
                                String[] batonCoords = new String[3];
                                
                                // Segment entre les deux 3
                                if (dx != 0) { // Horizontalement
                                    batonCoords[0] = (dx > 0) 
                                        ? "V_" + y + "_" + (x+1)  // Droite
                                        : "V_" + y + "_" + x;    // Gauche
                                } else { // Verticalement
                                    batonCoords[0] = (dy > 0) 
                                        ? "H_" + (y+1) + "_" + x  // Bas
                                        : "H_" + y + "_" + x;    // Haut
                                }
                                
                                // Segments opposés de chaque case 3
                                String[] oppositeSegments = {
                                    (dx != 0) ? 
                                        ((dx > 0) ? "V_" + y + "_" + x : "V_" + y + "_" + (x+1)) :
                                        ((dy > 0) ? "H_" + y + "_" + x : "H_" + (y+1) + "_" + x)
                                };
                                
                                // Ajouter les segments opposés
                                batonCoords[1] = oppositeSegments[0];
                                batonCoords[2] = (dx != 0) ? 
                                    ((dx > 0) ? "V_" + xB + "_" + yB : "V_" + xB + "_" + (yB+1)) :
                                    ((dy > 0) ? "H_" + xB + "_" + x : "H_" + (xB+1) + "_" + x);

                                // Marquer les segments appropriés
                                for (String segmentKey : batonCoords) {
                                    Line segment = gridLines.get(segmentKey);
                                    if (segment != null) {
                                        segment.setStroke(Color.BLACK); // Marquer comme ligne
                                    }
                                }

                                return; // Une seule application par détection
                            }
                        }
                    }
                }
            }
        }
    }
}