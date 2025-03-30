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
 * Implémentation de la technique des chiffres 2 et 1 dans un angle de la grille.
 * Avec l'astuce du chiffre 2 dans l'angle de la grille, on place deux bâtons.
 * Il n'y a plus qu'une seule façon de positionner les bâtons autour du 2, 
 * puis on place un dernier bâton à côté du 1.
 */
public class Tech_2And1InCorner implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsToHighlight;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_2And1InCorner() {
        segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param gridNumbers La grille de nombres
     * @param gridLines Les segments de la grille
     * @param slitherlinkGrid Le panneau contenant la grille
     */
    public Tech_2And1InCorner(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Vérifie si la technique des 2 et 1 dans un angle est applicable sur la grille actuelle.
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
        
        // Coin supérieur gauche : 2 dans le coin et 1 à droite
        if (rows > 0 && cols > 1 && gridNumbers[0][0] == 2 && gridNumbers[0][1] == 1)  {
            
            // Déterminer les segments à marquer comme bâtons
            String[] segmentsForBaton = {
                "V_0_0",        
                "H_0_0",     
                "H_0_2"      
            };
            
            for (String segmentKey : segmentsForBaton) {
                Line segment = gridLines.get(segmentKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(segmentKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin supérieur gauche : 2 dans le coin et 1 en bas
        else if (rows > 0 && cols > 1 && gridNumbers[0][0] == 2 && gridNumbers[1][0] == 1) {
            // Déterminer les segments à marquer comme bâtons
            String[] segmentsForBaton = {
                "H_0_0",        
                "V_0_0",     
                "V_2_0"        
            };
            
            for (String segmentKey : segmentsForBaton) {
                Line segment = gridLines.get(segmentKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(segmentKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin supérieur droit : 2 dans le coin et 1 à gauche
        else if (rows > 0 && cols > 1 && gridNumbers[0][cols-1] == 2 && gridNumbers[0][cols-2] == 1)  {
            // Déterminer les segments à marquer comme bâtons
            String[] segmentsForBaton = {
                "V_0_" + cols,        
                "H_0_" + (cols-1),     
                "H_0_" + (cols-3)          
            };
            
            for (String segmentKey : segmentsForBaton) {
                Line segment = gridLines.get(segmentKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(segmentKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin supérieur droit : 2 dans le coin et 1 en bas
        else if (rows > 0 && cols > 1 && gridNumbers[0][cols-1] == 2 && gridNumbers[1][cols-1] == 1)  {
            // Déterminer les segments à marquer comme bâtons
            String[] segmentsForBaton = {
                "V_0_" + cols,        
                "H_0_" + (cols-1),     
                "V_2_" + cols     
            };
            
            for (String segmentKey : segmentsForBaton) {
                Line segment = gridLines.get(segmentKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(segmentKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin inférieur gauche : 2 dans le coin et 1 à droite
        else if (rows > 1 && cols > 1 && gridNumbers[rows-1][0] == 2 && gridNumbers[rows-1][1] == 1) {
            // Déterminer les segments à marquer comme bâtons
            String[] segmentsForBaton = {
                "H_" + rows + "_0",  
                "V_" + (rows-1) + "_0",
                "H_" + rows + "_2"
            };
            
            for (String segmentKey : segmentsForBaton) {
                Line segment = gridLines.get(segmentKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(segmentKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin inférieur gauche : 2 dans le coin et 1 en haut
        else if (rows > 1 && cols > 1 && gridNumbers[rows-1][0] == 2 && gridNumbers[rows-2][0] == 1) {
            // Déterminer les segments à marquer comme bâtons
            String[] segmentsForBaton = {
                "H_" + rows + "_0",
                "V_" + (rows-1) + "_0",
                "V_" + (rows-3) + "_0"
            };
            
            for (String segmentKey : segmentsForBaton) {
                Line segment = gridLines.get(segmentKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(segmentKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin inférieur droit : 2 dans le coin et 1 à gauche
        else if (rows > 1 && cols > 1 && gridNumbers[rows-1][cols-1] == 2 && gridNumbers[rows-1][cols-2] == 1) {
            // Déterminer les segments à marquer comme bâtons
            String[] segmentsForBaton = {
                "H_" + rows + "_" + (cols-1),  
                "V_" + (rows-1) + "_" + cols,
                "H_" + rows + "_" + (cols-3)    
            };
            
            for (String segmentKey : segmentsForBaton) {
                Line segment = gridLines.get(segmentKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(segmentKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin inférieur droit : 2 dans le coin et 1 en haut
        else if (rows > 1 && cols > 1 && gridNumbers[rows-1][cols-1] == 2 && gridNumbers[rows-2][cols-1] == 1) {
            // Déterminer les segments à marquer comme bâtons
            String[] segmentsForBaton = {
                "H_" + rows + "_" + (cols-1),  
                "V_" + (rows-1) + "_" + cols,
                "V_" + (rows-3) + "_" + cols     
            };
            
            for (String segmentKey : segmentsForBaton) {
                Line segment = gridLines.get(segmentKey);
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