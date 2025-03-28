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
 * Implémentation de la technique des 1 et 0 dans un angle de la grille.
 * Quand on a un 1 dans l'angle à côté d'un 0, on peut placer le bâton uniquement
 * en bas. Puis, on doit placer deux autres bâtons en bas à gauche et à droite.
 */
public class Tech_1And0InCorner implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsToHighlight;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_1And0InCorner() {
        segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param gridNumbers La grille de nombres
     * @param gridLines Les segments de la grille
     * @param slitherlinkGrid Le panneau contenant la grille
     */
    public Tech_1And0InCorner(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Vérifie si la technique des 1 et 0 dans un angle est applicable sur la grille actuelle.
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
        
        // Vérifier les coins de la grille pour un 1 et un 0 adjacent
        
        // Coin supérieur gauche (1) avec un 0 à droite
        if (gridNumbers[0][0] == 1 && gridNumbers[0][1] == 0) {
            // Segment à marquer comme bâton
            String batonSegment = "V_0_0";  // Segment de gauche
            Line segment = gridLines.get(batonSegment);
            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                segmentsToHighlight.add(batonSegment + ":baton");
                applicationPossible = true;
            }
            
            // Segments supplémentaires à marquer comme bâtons
            String[] otherBatonSegments = {
                "H_1_0",   // Segment du bas
                "V_1_0"    // Segment en bas à gauche
            };
            
            for (String segmentKey : otherBatonSegments) {
                segment = gridLines.get(segmentKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(segmentKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin supérieur gauche (1) avec un 0 en dessous
        if (gridNumbers[0][0] == 1 && gridNumbers[1][0] == 0) {
            // Segment à marquer comme bâton
            String batonSegment = "H_0_0";  // Segment du haut
            Line segment = gridLines.get(batonSegment);
            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                segmentsToHighlight.add(batonSegment + ":baton");
                applicationPossible = true;
            }
            
            // Segments supplémentaires à marquer comme bâtons
            String[] otherBatonSegments = {
                "V_0_1",   // Segment de droite
                "H_0_1"    // Segment en haut à droite
            };
            
            for (String segmentKey : otherBatonSegments) {
                segment = gridLines.get(segmentKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(segmentKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin supérieur droit (1) avec un 0 à gauche
        if (gridNumbers[0][cols-1] == 1 && gridNumbers[0][cols-2] == 0) {
            // Segment à marquer comme bâton
            String batonSegment = "V_0_" + cols;  // Segment de droite
            Line segment = gridLines.get(batonSegment);
            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                segmentsToHighlight.add(batonSegment + ":baton");
                applicationPossible = true;
            }
            
            // Segments supplémentaires à marquer comme bâtons
            String[] otherBatonSegments = {
                "H_1_" + (cols-1),   // Segment du bas
                "V_1_" + cols        // Segment en bas à droite
            };
            
            for (String segmentKey : otherBatonSegments) {
                segment = gridLines.get(segmentKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(segmentKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin supérieur droit (1) avec un 0 en dessous
        if (gridNumbers[0][cols-1] == 1 && gridNumbers[1][cols-1] == 0) {
            // Segment à marquer comme bâton
            String batonSegment = "H_0_" + (cols-1);  // Segment du haut
            Line segment = gridLines.get(batonSegment);
            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                segmentsToHighlight.add(batonSegment + ":baton");
                applicationPossible = true;
            }
            
            // Segments supplémentaires à marquer comme bâtons
            String[] otherBatonSegments = {
                "V_0_" + (cols-1),     // Segment de gauche
                "H_0_" + (cols-2)      // Segment en haut à gauche
            };
            
            for (String segmentKey : otherBatonSegments) {
                segment = gridLines.get(segmentKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(segmentKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin inférieur gauche (1) avec un 0 à droite
        if (gridNumbers[rows-1][0] == 1 && gridNumbers[rows-1][1] == 0) {
            // Segment à marquer comme bâton
            String batonSegment = "V_" + (rows-1) + "_0";  // Segment de gauche
            Line segment = gridLines.get(batonSegment);
            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                segmentsToHighlight.add(batonSegment + ":baton");
                applicationPossible = true;
            }
            
            // Segments supplémentaires à marquer comme bâtons
            String[] otherBatonSegments = {
                "H_" + (rows-1) + "_0",   // Segment du haut
                "V_" + (rows-2) + "_0"    // Segment en haut à gauche
            };
            
            for (String segmentKey : otherBatonSegments) {
                segment = gridLines.get(segmentKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(segmentKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin inférieur gauche (1) avec un 0 au-dessus
        if (gridNumbers[rows-1][0] == 1 && gridNumbers[rows-2][0] == 0) {
            // Segment à marquer comme bâton
            String batonSegment = "H_" + rows + "_0";  // Segment du bas
            Line segment = gridLines.get(batonSegment);
            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                segmentsToHighlight.add(batonSegment + ":baton");
                applicationPossible = true;
            }
            
            // Segments supplémentaires à marquer comme bâtons
            String[] otherBatonSegments = {
                "V_" + (rows-1) + "_1",   // Segment de droite
                "H_" + rows + "_1"        // Segment en bas à droite
            };
            
            for (String segmentKey : otherBatonSegments) {
                segment = gridLines.get(segmentKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(segmentKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin inférieur droit (1) avec un 0 à gauche
        if (gridNumbers[rows-1][cols-1] == 1 && gridNumbers[rows-1][cols-2] == 0) {
            // Segment à marquer comme bâton
            String batonSegment = "V_" + (rows-1) + "_" + cols;  // Segment de droite
            Line segment = gridLines.get(batonSegment);
            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                segmentsToHighlight.add(batonSegment + ":baton");
                applicationPossible = true;
            }
            
            // Segments supplémentaires à marquer comme bâtons
            String[] otherBatonSegments = {
                "H_" + (rows-1) + "_" + (cols-1),   // Segment du haut
                "V_" + (rows-2) + "_" + cols        // Segment en haut à droite
            };
            
            for (String segmentKey : otherBatonSegments) {
                segment = gridLines.get(segmentKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(segmentKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin inférieur droit (1) avec un 0 au-dessus
        if (gridNumbers[rows-1][cols-1] == 1 && gridNumbers[rows-2][cols-1] == 0) {
            // Segment à marquer comme bâton
            String batonSegment = "H_" + rows + "_" + (cols-1);  // Segment du bas
            Line segment = gridLines.get(batonSegment);
            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                segmentsToHighlight.add(batonSegment + ":baton");
                applicationPossible = true;
            }
            
            // Segments supplémentaires à marquer comme bâtons
            String[] otherBatonSegments = {
                "V_" + (rows-1) + "_" + (cols-1),   // Segment de gauche
                "H_" + rows + "_" + (cols-2)        // Segment en bas à gauche
            };
            
            for (String segmentKey : otherBatonSegments) {
                segment = gridLines.get(segmentKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(segmentKey + ":baton");
                    applicationPossible = true;
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