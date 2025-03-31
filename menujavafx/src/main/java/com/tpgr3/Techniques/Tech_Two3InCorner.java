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
 * Implémentation de la technique des deux 3 dans un angle de la grille.
 * En utilisant les techniques des 3 côte à côte et du 3 dans l'angle,
 * on peut déduire le placement de segments obligatoires.
 */
public class Tech_Two3InCorner implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsToHighlight;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_Two3InCorner() {
        segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param gridNumbers La grille de nombres
     * @param gridLines Les segments de la grille
     * @param slitherlinkGrid Le panneau contenant la grille
     */
    public Tech_Two3InCorner(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Vérifie si la technique des deux 3 dans un angle est applicable sur la grille actuelle.
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
        
        // Vérifier le coin supérieur gauche
        if (rows >= 2 && cols >= 2 && gridNumbers[0][0] == 3 && gridNumbers[0][1] == 3) {
            // Deux 3 dans le coin supérieur gauche mais en diagonale
            // Segments à marquer comme bâtons
            String[] batonSegments = {
                "H_1_1",    
                "V_1_0",   
                "H_0_2"
            };
            
            for (String batonKey : batonSegments) {
                Line segment = gridLines.get(batonKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(batonKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        else if (rows >= 2 && cols >= 2 && gridNumbers[0][0] == 3 && gridNumbers[1][0] == 3) {
            // Deux 3 dans le coin supérieur gauche mais en diagonale
            // Segments à marquer comme bâtons
            String[] batonSegments = {
                "V_1_1",    
                "H_0_1",   
                "V_2_0"
            };
            
            for (String batonKey : batonSegments) {
                Line segment = gridLines.get(batonKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(batonKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Vérifier le coin supérieur droit
        else if (rows >= 2 && cols >= 2 && gridNumbers[0][cols-1] == 3 && gridNumbers[0][cols-2] == 3) {
            // Deux 3 dans le coin supérieur droit mais en diagonale
            // Segments à marquer comme bâtons
            String[] batonSegments = {
                "V_1_" + cols,    
                "H_0_" + (cols-3),   
                "H_1_" + (cols-2)
            };
            
            for (String batonKey : batonSegments) {
                Line segment = gridLines.get(batonKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(batonKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        // Vérifier le coin supérieur droit
        else if (rows >= 2 && cols >= 2 && gridNumbers[0][cols-1] == 3 && gridNumbers[1][cols-1] == 3) {
            // Deux 3 dans le coin supérieur droit mais en diagonale
            // Segments à marquer comme bâtons
            String[] batonSegments = {
                "V_1_" + (cols-1),    
                "H_0_" + (cols-2),   
                "V_2_" + cols
            };
            
            for (String batonKey : batonSegments) {
                Line segment = gridLines.get(batonKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(batonKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Vérifier le coin inférieur gauche
        else if (rows >= 2 && cols >= 2 && gridNumbers[rows-1][0] == 3 && gridNumbers[rows-2][0] == 3) {
            // Deux 3 dans le coin inférieur gauche mais en diagonale
            // Segments à marquer comme bâtons
            String[] batonSegments = {
                "H_" + rows + "_1",
                "V_" + (rows-3) + "_0",
                "V_" + (rows-2) + "_1"
            };
            
            for (String batonKey : batonSegments) {
                Line segment = gridLines.get(batonKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(batonKey + ":baton");
                    applicationPossible = true;
                }
            }
        }

        else if (rows >= 2 && cols >= 2 && gridNumbers[rows-1][0] == 3 && gridNumbers[rows-1][1] == 3) {
            // Deux 3 dans le coin inférieur gauche mais en diagonale
            // Segments à marquer comme bâtons
            String[] batonSegments = {
                "H_" + (rows-1) + "_1",
                "V_" + (rows-2) + "_0",
                "H_" + rows + "_2"
            };
            
            for (String batonKey : batonSegments) {
                Line segment = gridLines.get(batonKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(batonKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Vérifier le coin inférieur droit
        else if (rows >= 2 && cols >= 2 && gridNumbers[rows-1][cols-1] == 3 && gridNumbers[rows-1][cols-2] == 3) {
            // Deux 3 dans le coin inférieur droit mais en diagonale
            // Segments à marquer comme bâtons
            String[] batonSegments = {
                "H_" + (rows-1) + "_" + (cols-2),
                "V_" + (rows-2) + "_" + cols,
                "H_" + rows + "_" + (cols-3)
            };
            
            for (String batonKey : batonSegments) {
                Line segment = gridLines.get(batonKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(batonKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        else if (rows >= 2 && cols >= 2 && gridNumbers[rows-1][cols-1] == 3 && gridNumbers[rows-2][cols-1] == 3) {
            // Deux 3 dans le coin inférieur droit mais en diagonale
            // Segments à marquer comme bâtons
            String[] batonSegments = {
                "V_" + (rows-2) + "_" + (cols-1),
                "H_" + rows + "_" + (cols-2),
                "V_" + (rows-3) + "_" + cols
            };
            
            for (String batonKey : batonSegments) {
                Line segment = gridLines.get(batonKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(batonKey + ":baton");
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

    public static String getDescription() {
        return "Deux 3 dans un angle : Combinez les techniques des 3 côte à côte et du 3 dans l'angle " +
                "pour déduire des placements obligatoires.";
    }
}