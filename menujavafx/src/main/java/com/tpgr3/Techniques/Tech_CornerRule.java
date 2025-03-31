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
 * Implémentation de la technique de la règle des coins dans Slitherlink.
 * Dans les coins de la grille, le nombre de segments est limité selon la valeur de la case.
 */
public class Tech_CornerRule implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsToHighlight;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_CornerRule() {
        segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param gridNumbers La grille de nombres
     * @param gridLines Les segments de la grille
     * @param slitherlinkGrid Le panneau contenant la grille
     */
    public Tech_CornerRule(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Vérifie si la technique de la règle des coins est applicable sur la grille actuelle.
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
        
        // Vérifier les quatre coins de la grille
        int[][] corners = {
            {0, 0},                 // Coin supérieur gauche
            {0, cols - 1},          // Coin supérieur droit
            {rows - 1, 0},          // Coin inférieur gauche
            {rows - 1, cols - 1}    // Coin inférieur droit
        };
        
        for (int[] corner : corners) {
            int i = corner[0];
            int j = corner[1];
            
            if (gridNumbers[i][j] != -1) { // -1 indique une case vide
                int value = gridNumbers[i][j];
                
                // Identifier les segments adjacents au coin
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
                } else { // Coin inférieur droit
                    adjacentSegments[0] = "H_" + (i+1) + "_" + j; // Bas
                    adjacentSegments[1] = "V_" + i + "_" + (j+1); // Droite
                }
                
                // Analyser l'état actuel des segments
                int lineCount = 0;
                int crossCount = 0;
                int neutralCount = 0;
                
                for (String segmentKey : adjacentSegments) {
                    Line segment = gridLines.get(segmentKey);
                    if (segment != null) {
                        if (segment.getStroke() != Color.TRANSPARENT && !aCroix(segment)) {
                            lineCount++;
                        } else if (aCroix(segment)) {
                            crossCount++;
                        } else {
                            neutralCount++;
                        }
                    }
                }
                
                // Appliquer les règles selon la valeur du coin
                if (value == 0) {
                    // Pour un 0, tous les segments doivent être des croix
                    if (crossCount < 2 && neutralCount > 0) {
                        for (String segmentKey : adjacentSegments) {
                            Line segment = gridLines.get(segmentKey);
                            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                                segmentsToHighlight.add(segmentKey + ":croix");
                                applicationPossible = true;
                            }
                        }
                    }
                } else if (value == 1) {
                    // Pour un 1, exactement un segment doit être un bâton
                    if (lineCount == 1 && neutralCount == 1) {
                        // Si un bâton est déjà placé, l'autre doit être une croix
                        for (String segmentKey : adjacentSegments) {
                            Line segment = gridLines.get(segmentKey);
                            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                                segmentsToHighlight.add(segmentKey + ":croix");
                                applicationPossible = true;
                            }
                        }
                    } else if (lineCount == 0 && crossCount == 1 && neutralCount == 1) {
                        // Si une croix est déjà placée, l'autre doit être un bâton
                        for (String segmentKey : adjacentSegments) {
                            Line segment = gridLines.get(segmentKey);
                            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                                segmentsToHighlight.add(segmentKey + ":baton");
                                applicationPossible = true;
                            }
                        }
                    }
                } else if (value == 2) {
                    // Pour un 2, les deux segments doivent être des bâtons
                    if (lineCount < 2 && neutralCount > 0) {
                        for (String segmentKey : adjacentSegments) {
                            Line segment = gridLines.get(segmentKey);
                            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                                segmentsToHighlight.add(segmentKey + ":baton");
                                applicationPossible = true;
                            }
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
        return "Règle des coins : Dans les coins de la grille, le nombre de segments est limité " +
                "selon la valeur de la case (0, 1, 2 ou 3).";
    }
}