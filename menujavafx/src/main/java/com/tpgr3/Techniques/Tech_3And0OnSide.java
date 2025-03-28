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
 * Implémentation de la technique des 3 et 0 sur un côté de la grille.
 * Il faut tracer deux bâtons (un contre le côté de la grille et du côté du chiffre 0),
 * pour ne pas avoir de lignes qu'on ne peut pas relier.
 */
public class Tech_3And0OnSide implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsToHighlight;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_3And0OnSide() {
        segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param gridNumbers La grille de nombres
     * @param gridLines Les segments de la grille
     * @param slitherlinkGrid Le panneau contenant la grille
     */
    public Tech_3And0OnSide(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Vérifie si la technique des 3 et 0 sur un côté est applicable sur la grille actuelle.
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
        
        int rows = gridNumbers.length;
        int cols = gridNumbers[0].length;
        
        // Vérifier les paires 3-0 horizontalement sur les bords
        
        // Bord supérieur
        for (int j = 0; j < cols - 1; j++) {
            if ((gridNumbers[0][j] == 3 && gridNumbers[0][j+1] == 0) ||
                (gridNumbers[0][j] == 0 && gridNumbers[0][j+1] == 3)) {
                
                int index3 = gridNumbers[0][j] == 3 ? j : j+1;
                
                // Segments à marquer comme bâtons
                String[] batonSegments = {
                    "H_0_" + index3,        // Segment du haut (côté de la grille)
                    "V_0_" + (index3 + (gridNumbers[0][j] == 3 ? 1 : 0))  // Segment opposé au 0
                };
                
                for (String batonKey : batonSegments) {
                    Line segment = gridLines.get(batonKey);
                    if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                        segmentsToHighlight.add(batonKey + ":baton");
                        applicationPossible = true;
                    }
                }
            }
        }
        
        // Bord inférieur
        for (int j = 0; j < cols - 1; j++) {
            if ((gridNumbers[rows-1][j] == 3 && gridNumbers[rows-1][j+1] == 0) ||
                (gridNumbers[rows-1][j] == 0 && gridNumbers[rows-1][j+1] == 3)) {
                
                int index3 = gridNumbers[rows-1][j] == 3 ? j : j+1;
                
                // Segments à marquer comme bâtons
                String[] batonSegments = {
                    "H_" + rows + "_" + index3,   // Segment du bas (côté de la grille)
                    "V_" + (rows-1) + "_" + (index3 + (gridNumbers[rows-1][j] == 3 ? 1 : 0))  // Segment opposé au 0
                };
                
                for (String batonKey : batonSegments) {
                    Line segment = gridLines.get(batonKey);
                    if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                        segmentsToHighlight.add(batonKey + ":baton");
                        applicationPossible = true;
                    }
                }
            }
        }
        
        // Vérifier les paires 3-0 verticalement sur les bords
        
        // Bord gauche
        for (int i = 0; i < rows - 1; i++) {
            if ((gridNumbers[i][0] == 3 && gridNumbers[i+1][0] == 0) ||
                (gridNumbers[i][0] == 0 && gridNumbers[i+1][0] == 3)) {
                
                int index3 = gridNumbers[i][0] == 3 ? i : i+1;
                
                // Segments à marquer comme bâtons
                String[] batonSegments = {
                    "V_" + index3 + "_0",        // Segment de gauche (côté de la grille)
                    "H_" + (index3 + (gridNumbers[i][0] == 3 ? 1 : 0)) + "_0"  // Segment opposé au 0
                };
                
                for (String batonKey : batonSegments) {
                    Line segment = gridLines.get(batonKey);
                    if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                        segmentsToHighlight.add(batonKey + ":baton");
                        applicationPossible = true;
                    }
                }
            }
        }
        
        // Bord droit
        for (int i = 0; i < rows - 1; i++) {
            if ((gridNumbers[i][cols-1] == 3 && gridNumbers[i+1][cols-1] == 0) ||
                (gridNumbers[i][cols-1] == 0 && gridNumbers[i+1][cols-1] == 3)) {
                
                int index3 = gridNumbers[i][cols-1] == 3 ? i : i+1;
                
                // Segments à marquer comme bâtons
                String[] batonSegments = {
                    "V_" + index3 + "_" + cols,   // Segment de droite (côté de la grille)
                    "H_" + (index3 + (gridNumbers[i][cols-1] == 3 ? 1 : 0)) + "_" + (cols-1)  // Segment opposé au 0
                };
                
                for (String batonKey : batonSegments) {
                    Line segment = gridLines.get(batonKey);
                    if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                        segmentsToHighlight.add(batonKey + ":baton");
                        applicationPossible = true;
                    }
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
            boolean isBaton = parts[1].equals("baton");
            
            Line line = gridLines.get(lineId);
            if (line != null) {
                Glow glow = new Glow(0.8);
                line.setEffect(glow);
                line.setStroke(isBaton ? Color.GREEN : Color.RED);
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