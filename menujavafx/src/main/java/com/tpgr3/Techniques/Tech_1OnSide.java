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
 * Implémentation de la technique du 1 sur un côté de la grille.
 * Si un bâton est positionné sur le bord de la grille et qu'un chiffre 1 se trouve à côté,
 * on ne peut pas positionner de bâtons à l'opposé de celui déjà posé.
 */
public class Tech_1OnSide implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsToHighlight;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_1OnSide() {
        segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param gridNumbers La grille de nombres
     * @param gridLines Les segments de la grille
     * @param slitherlinkGrid Le panneau contenant la grille
     */
    public Tech_1OnSide(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Vérifie si la technique du 1 sur un côté est applicable sur la grille actuelle.
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
        
        // Vérifier les 1 sur les bords de la grille
        
        // Bord supérieur
        for (int j = 0; j < cols; j++) {
            if (gridNumbers[0][j] == 1) {
                String topSegment = "H_0_" + j;
                Line segment = gridLines.get(topSegment);
                
                // Si le segment du haut est déjà tracé
                if (segment != null && segment.getStroke() != Color.TRANSPARENT && !aCroix(segment)) {
                    // Marquer le segment du bas comme croix
                    String bottomSegment = "H_1_" + j;
                    Line bottomLine = gridLines.get(bottomSegment);
                    
                    if (bottomLine != null && bottomLine.getStroke() == Color.TRANSPARENT && !aCroix(bottomLine)) {
                        segmentsToHighlight.add(bottomSegment + ":croix");
                        applicationPossible = true;
                    }
                }
                // Si le segment du bas est déjà tracé
                else {
                    String bottomSegment = "H_1_" + j;
                    Line bottomLine = gridLines.get(bottomSegment);
                    
                    if (bottomLine != null && bottomLine.getStroke() != Color.TRANSPARENT && !aCroix(bottomLine)) {
                        // Marquer le segment du haut comme croix
                        if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                            segmentsToHighlight.add(topSegment + ":croix");
                            applicationPossible = true;
                        }
                    }
                }
            }
        }
        
        // Bord inférieur
        for (int j = 0; j < cols; j++) {
            if (gridNumbers[rows-1][j] == 1) {
                String bottomSegment = "H_" + rows + "_" + j;
                Line segment = gridLines.get(bottomSegment);
                
                // Si le segment du bas est déjà tracé
                if (segment != null && segment.getStroke() != Color.TRANSPARENT && !aCroix(segment)) {
                    // Marquer le segment du haut comme croix
                    String topSegment = "H_" + (rows-1) + "_" + j;
                    Line topLine = gridLines.get(topSegment);
                    
                    if (topLine != null && topLine.getStroke() == Color.TRANSPARENT && !aCroix(topLine)) {
                        segmentsToHighlight.add(topSegment + ":croix");
                        applicationPossible = true;
                    }
                }
                // Si le segment du haut est déjà tracé
                else {
                    String topSegment = "H_" + (rows-1) + "_" + j;
                    Line topLine = gridLines.get(topSegment);
                    
                    if (topLine != null && topLine.getStroke() != Color.TRANSPARENT && !aCroix(topLine)) {
                        // Marquer le segment du bas comme croix
                        if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                            segmentsToHighlight.add(bottomSegment + ":croix");
                            applicationPossible = true;
                        }
                    }
                }
            }
        }
        
        // Bord gauche
        for (int i = 0; i < rows; i++) {
            if (gridNumbers[i][0] == 1) {
                String leftSegment = "V_" + i + "_0";
                Line segment = gridLines.get(leftSegment);
                
                // Si le segment de gauche est déjà tracé
                if (segment != null && segment.getStroke() != Color.TRANSPARENT && !aCroix(segment)) {
                    // Marquer le segment de droite comme croix
                    String rightSegment = "V_" + i + "_1";
                    Line rightLine = gridLines.get(rightSegment);
                    
                    if (rightLine != null && rightLine.getStroke() == Color.TRANSPARENT && !aCroix(rightLine)) {
                        segmentsToHighlight.add(rightSegment + ":croix");
                        applicationPossible = true;
                    }
                }
                // Si le segment de droite est déjà tracé
                else {
                    String rightSegment = "V_" + i + "_1";
                    Line rightLine = gridLines.get(rightSegment);
                    
                    if (rightLine != null && rightLine.getStroke() != Color.TRANSPARENT && !aCroix(rightLine)) {
                        // Marquer le segment de gauche comme croix
                        if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                            segmentsToHighlight.add(leftSegment + ":croix");
                            applicationPossible = true;
                        }
                    }
                }
            }
        }
        
        // Bord droit
        for (int i = 0; i < rows; i++) {
            if (gridNumbers[i][cols-1] == 1) {
                String rightSegment = "V_" + i + "_" + cols;
                Line segment = gridLines.get(rightSegment);
                
                // Si le segment de droite est déjà tracé
                if (segment != null && segment.getStroke() != Color.TRANSPARENT && !aCroix(segment)) {
                    // Marquer le segment de gauche comme croix
                    String leftSegment = "V_" + i + "_" + (cols-1);
                    Line leftLine = gridLines.get(leftSegment);
                    
                    if (leftLine != null && leftLine.getStroke() == Color.TRANSPARENT && !aCroix(leftLine)) {
                        segmentsToHighlight.add(leftSegment + ":croix");
                        applicationPossible = true;
                    }
                }
                // Si le segment de gauche est déjà tracé
                else {
                    String leftSegment = "V_" + i + "_" + (cols-1);
                    Line leftLine = gridLines.get(leftSegment);
                    
                    if (leftLine != null && leftLine.getStroke() != Color.TRANSPARENT && !aCroix(leftLine)) {
                        // Marquer le segment de droite comme croix
                        if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                            segmentsToHighlight.add(rightSegment + ":croix");
                            applicationPossible = true;
                        }
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
    public static String getDescription() {
        return  "1 sur un côté : Si un bâton est déjà placé sur un bord à côté d'un 1, " +
                "le segment opposé doit être marqué d'une croix.";
    }
}