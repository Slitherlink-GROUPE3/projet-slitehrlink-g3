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
 * Implémentation de la technique du 3 dans un angle de la grille dans Slitherlink.
 * Quand un 3 est dans un coin de la grille, il faut tracer les deux lignes qui forment l'angle.
 * Le troisième segment peut être placé librement.
 */
public class Tech_3InCorner implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsToHighlight;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_3InCorner() {
        segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param gridNumbers La grille de nombres
     * @param gridLines Les segments de la grille
     * @param slitherlinkGrid Le panneau contenant la grille
     */
    public Tech_3InCorner(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Vérifie si la technique du 3 dans un angle est applicable sur la grille actuelle.
     * Cette technique ne vérifie que les deux segments formant l'angle.
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
            
            if (gridNumbers[i][j] == 3) {
                // Un 3 dans un coin
                
                String[] segmentsInCorner;
                
                if (i == 0 && j == 0) {
                    // Coin supérieur gauche
                    segmentsInCorner = new String[]{
                        "H_0_0",    // Segment du haut
                        "V_0_0"     // Segment de gauche
                    };
                } else if (i == 0 && j == cols - 1) {
                    // Coin supérieur droit
                    segmentsInCorner = new String[]{
                        "H_0_" + j,        // Segment du haut
                        "V_0_" + (j + 1)   // Segment de droite
                    };
                } else if (i == rows - 1 && j == 0) {
                    // Coin inférieur gauche
                    segmentsInCorner = new String[]{
                        "H_" + (i + 1) + "_0",  // Segment du bas
                        "V_" + i + "_0"         // Segment de gauche
                    };
                } else {
                    // Coin inférieur droit
                    segmentsInCorner = new String[]{
                        "H_" + (i + 1) + "_" + j,    // Segment du bas
                        "V_" + i + "_" + (j + 1)     // Segment de droite
                    };
                }
                
                // Vérifier si les segments dans le coin sont disponibles pour être marqués
                for (String segmentKey : segmentsInCorner) {
                    Line segment = gridLines.get(segmentKey);
                    if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                        segmentsToHighlight.add(segmentKey + ":baton");
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
    public static String getDescription() {
        return "3 dans un angle : Lorsqu'un 3 est dans un coin, il faut tracer les deux segments " +
                "dans l'angle plus un troisième.";
    }
}