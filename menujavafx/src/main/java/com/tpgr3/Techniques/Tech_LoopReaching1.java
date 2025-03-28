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
 * Implémentation de la technique de détection de boucle pour les cases contenant un 1.
 * Lorsqu'un bâton est déjà placé à côté d'un 1, les autres segments autour doivent être marqués d'une croix.
 */
public class Tech_LoopReaching1 implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsToHighlight;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_LoopReaching1() {
        segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param gridNumbers La grille de nombres
     * @param gridLines Les segments de la grille
     * @param slitherlinkGrid Le panneau contenant la grille
     */
    public Tech_LoopReaching1(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Vérifie si la technique de détection de boucle pour les cases contenant un 1 est applicable.
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
                // Si la cellule contient un 1
                if (gridNumbers[i][j] == 1) {
                    // Identifier les segments autour de la cellule
                    String[] adjacentSegments = {
                        "H_" + i + "_" + j,        // Haut
                        "H_" + (i+1) + "_" + j,    // Bas
                        "V_" + i + "_" + j,        // Gauche
                        "V_" + i + "_" + (j+1)     // Droite
                    };
                    
                    // Compter le nombre de bâtons déjà placés
                    int batonCount = 0;
                    String segmentWithBaton = null;
                    
                    for (String segmentKey : adjacentSegments) {
                        Line segment = gridLines.get(segmentKey);
                        if (segment != null && segment.getStroke() != Color.TRANSPARENT && !aCroix(segment)) {
                            batonCount++;
                            segmentWithBaton = segmentKey;
                        }
                    }
                    
                    // Si exactement un bâton est déjà placé
                    if (batonCount == 1) {
                        // Marquer les autres segments avec des croix
                        for (String segmentKey : adjacentSegments) {
                            if (!segmentKey.equals(segmentWithBaton)) {
                                Line segment = gridLines.get(segmentKey);
                                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                                    segmentsToHighlight.add(segmentKey + ":croix");
                                    applicationPossible = true;
                                }
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