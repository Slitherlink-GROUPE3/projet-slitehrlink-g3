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
 * Implémentation de la technique de la règle des 3 dans Slitherlink.
 * Exactement trois segments doivent entourer une case contenant un 3.
 */
public class Tech_ThreeRule implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsToHighlight;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_ThreeRule() {
        segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param gridNumbers La grille de nombres
     * @param gridLines Les segments de la grille
     * @param slitherlinkGrid Le panneau contenant la grille
     */
    public Tech_ThreeRule(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Vérifie si la technique de la règle des 3 est applicable sur la grille actuelle.
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
        
        // Parcourir toutes les cellules de la grille
        for (int i = 0; i < gridNumbers.length; i++) {
            for (int j = 0; j < gridNumbers[i].length; j++) {
                // Recherche des cases contenant un 3
                if (gridNumbers[i][j] == 3) {
                    // Compter les lignes et croix autour du 3
                    int lineCount = 0;
                    int crossCount = 0;
                    int neutralCount = 0;
                    
                    // Liste des segments autour de la case
                    String[] adjacentSegments = {
                        "H_" + i + "_" + j,       // Haut
                        "H_" + (i+1) + "_" + j,   // Bas
                        "V_" + i + "_" + j,       // Gauche
                        "V_" + i + "_" + (j+1)    // Droite
                    };
                    
                    // Vérifier l'état de chaque segment
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
                    
                    // Si déjà 3 lignes, le segment restant doit être une croix
                    if (lineCount == 3 && neutralCount == 1) {
                        for (String segmentKey : adjacentSegments) {
                            Line segment = gridLines.get(segmentKey);
                            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                                segmentsToHighlight.add(segmentKey + ":croix");
                                applicationPossible = true;
                            }
                        }
                    }
                    
                    // Si déjà 2 lignes et 1 croix, le dernier segment doit être une ligne
                    if (lineCount == 2 && crossCount == 1 && neutralCount == 1) {
                        for (String segmentKey : adjacentSegments) {
                            Line segment = gridLines.get(segmentKey);
                            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                                segmentsToHighlight.add(segmentKey + ":baton");
                                applicationPossible = true;
                            }
                        }
                    }
                    
                    // Si tous les segments sont neutres, marquez-les comme bâtons potentiels
                    if (lineCount == 0 && crossCount == 0 && neutralCount == 4) {
                        for (String segmentKey : adjacentSegments) {
                            Line segment = gridLines.get(segmentKey);
                            if (segment != null) {
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
}