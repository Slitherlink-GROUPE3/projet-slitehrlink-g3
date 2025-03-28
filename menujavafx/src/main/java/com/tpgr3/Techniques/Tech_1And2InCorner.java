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
 * Implémentation de la technique des chiffres 1 et 2 dans un angle de la grille.
 * Avec le chiffre 1 dans l'angle de la grille à côté d'un 2, 
 * on doit placer des croix dans l'angle de la grille.
 */
public class Tech_1And2InCorner implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsToHighlight;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_1And2InCorner() {
        segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param gridNumbers La grille de nombres
     * @param gridLines Les segments de la grille
     * @param slitherlinkGrid Le panneau contenant la grille
     */
    public Tech_1And2InCorner(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Vérifie si la technique des 1 et 2 dans un angle est applicable sur la grille actuelle.
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
        if (rows >= 1 && cols >= 2 && gridNumbers[0][0] == 1 && gridNumbers[0][1] == 2) {
            // 1 dans le coin supérieur gauche et 2 à droite
            // Segments à marquer comme croix
            String[] croixSegments = {
                "H_0_0",    // Segment du haut (angle)
                "V_0_0"     // Segment de gauche (angle)
            };
            
            for (String croixKey : croixSegments) {
                Line segment = gridLines.get(croixKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(croixKey + ":croix");
                    applicationPossible = true;
                }
            }
            
            // Segments à marquer comme bâtons
            String[] batonSegments = {
                "H_1_0",    // Segment du bas du 1
                "H_0_1",    // Segment du haut du 2
                "H_1_1",    // Segment du bas du 2
                "V_0_2"     // Segment de droite du 2
            };
            
            for (String batonKey : batonSegments) {
                Line segment = gridLines.get(batonKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(batonKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Vérifier le coin supérieur gauche (variante : 2 à gauche, 1 à droite)
        if (rows >= 1 && cols >= 2 && gridNumbers[0][0] == 2 && gridNumbers[0][1] == 1) {
            // Segments à marquer comme bâtons
            String[] batonSegments = {
                "H_0_0",    // Segment du haut du 2
                "V_0_0",    // Segment de gauche du 2
                "H_1_0",    // Segment du bas du 2
                "V_0_1"     // Segment entre le 2 et le 1
            };
            
            for (String batonKey : batonSegments) {
                Line segment = gridLines.get(batonKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(batonKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Vérifier les autres combinaisons dans les autres coins de manière similaire
        // Coin supérieur droit (1 et 2)
        if (rows >= 1 && cols >= 2 && gridNumbers[0][cols-1] == 1 && gridNumbers[0][cols-2] == 2) {
            // 1 dans le coin supérieur droit et 2 à gauche
            // Segments à marquer comme croix
            String[] croixSegments = {
                "H_0_" + (cols-1),    // Segment du haut (angle)
                "V_0_" + cols         // Segment de droite (angle)
            };
            
            for (String croixKey : croixSegments) {
                Line segment = gridLines.get(croixKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(croixKey + ":croix");
                    applicationPossible = true;
                }
            }
            
            // Segments à marquer comme bâtons
            String[] batonSegments = {
                "H_1_" + (cols-1),    // Segment du bas du 1
                "H_0_" + (cols-2),    // Segment du haut du 2
                "H_1_" + (cols-2),    // Segment du bas du 2
                "V_0_" + (cols-2)     // Segment de gauche du 2
            };
            
            for (String batonKey : batonSegments) {
                Line segment = gridLines.get(batonKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(batonKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin inférieur gauche (1 et 2)
        if (rows >= 2 && cols >= 2 && gridNumbers[rows-1][0] == 1 && gridNumbers[rows-1][1] == 2) {
            // 1 dans le coin inférieur gauche et 2 à droite
            // Segments à marquer comme croix
            String[] croixSegments = {
                "H_" + rows + "_0",       // Segment du bas (angle)
                "V_" + (rows-1) + "_0"    // Segment de gauche (angle)
            };
            
            for (String croixKey : croixSegments) {
                Line segment = gridLines.get(croixKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(croixKey + ":croix");
                    applicationPossible = true;
                }
            }
            
            // Segments à marquer comme bâtons
            String[] batonSegments = {
                "H_" + (rows-1) + "_0",    // Segment du haut du 1
                "H_" + (rows-1) + "_1",    // Segment du haut du 2
                "H_" + rows + "_1",        // Segment du bas du 2
                "V_" + (rows-1) + "_2"     // Segment de droite du 2
            };
            
            for (String batonKey : batonSegments) {
                Line segment = gridLines.get(batonKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(batonKey + ":baton");
                    applicationPossible = true;
                }
            }
        }
        
        // Coin inférieur droit (1 et 2)
        if (rows >= 2 && cols >= 2 && gridNumbers[rows-1][cols-1] == 1 && gridNumbers[rows-1][cols-2] == 2) {
            // 1 dans le coin inférieur droit et 2 à gauche
            // Segments à marquer comme croix
            String[] croixSegments = {
                "H_" + rows + "_" + (cols-1),       // Segment du bas (angle)
                "V_" + (rows-1) + "_" + cols        // Segment de droite (angle)
            };
            
            for (String croixKey : croixSegments) {
                Line segment = gridLines.get(croixKey);
                if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                    segmentsToHighlight.add(croixKey + ":croix");
                    applicationPossible = true;
                }
            }
            
            // Segments à marquer comme bâtons
            String[] batonSegments = {
                "H_" + (rows-1) + "_" + (cols-1),    // Segment du haut du 1
                "H_" + (rows-1) + "_" + (cols-2),    // Segment du haut du 2
                "H_" + rows + "_" + (cols-2),        // Segment du bas du 2
                "V_" + (rows-1) + "_" + (cols-2)     // Segment de gauche du 2
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
}