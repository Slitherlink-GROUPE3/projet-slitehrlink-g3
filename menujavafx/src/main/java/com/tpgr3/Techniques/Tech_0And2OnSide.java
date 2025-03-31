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
 * Implémentation de la technique des 0 et 2 sur un côté de la grille.
 * Il faut placer un bâton à l'opposé du bord et un autre à l'opposé du 0,
 * puis ajouter un bâton à chaque extrémité.
 */
public class Tech_0And2OnSide implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsToHighlight;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_0And2OnSide() {
        segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param gridNumbers La grille de nombres
     * @param gridLines Les segments de la grille
     * @param slitherlinkGrid Le panneau contenant la grille
     */
    public Tech_0And2OnSide(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Vérifie si la technique des 0 et 2 sur un côté est applicable sur la grille actuelle.
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
        
        // Vérifier les paires 0-2 horizontalement sur les bords
        
        // Bord supérieur
        for (int j = 0; j < cols - 1; j++) {
            if ((gridNumbers[0][j] == 2 && gridNumbers[0][j+1] == 0) ||
                (gridNumbers[0][j] == 0 && gridNumbers[0][j+1] == 2)) {
                
                int index2 = gridNumbers[0][j] == 2 ? j : j+1;
                
                // Segments à marquer comme bâtons autour du 2
                String[] batonSegments;
                
                if (gridNumbers[0][j] == 2) {
                    // Le 2 est à gauche du 0
                    batonSegments = new String[]{
                        "H_1_" + j,        // Segment du bas du 2
                        "V_0_" + j,        // Segment de gauche du 2 (opposé au 0)
                        "H_0_" + (j-1),    // Segment à l'extrémité gauche
                        "V_1_" + (j+1)         // Segment en bas à gauche
                    };
                } else {
                    // Le 2 est à droite du 0
                    batonSegments = new String[]{
                        "H_1_" + (j+1),     // Segment du bas du 2
                        "V_0_" + (j+2),     // Segment de droite du 2 (opposé au 0)
                        "H_0_" + (j+2),     // Segment à l'extrémité droite
                        "V_1_" + (j+1)      // Segment en bas à droite
                    };
                }
                
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
            if ((gridNumbers[rows-1][j] == 2 && gridNumbers[rows-1][j+1] == 0) ||
                (gridNumbers[rows-1][j] == 0 && gridNumbers[rows-1][j+1] == 2)) {
                
                int index2 = gridNumbers[rows-1][j] == 2 ? j : j+1;
                
                // Segments à marquer comme bâtons autour du 2
                String[] batonSegments;
                
                if (gridNumbers[rows-1][j] == 2) {
                    // Le 2 est à gauche du 0
                    batonSegments = new String[]{
                        "H_" + (rows-1) + "_" + j,    // Segment du haut du 2
                        "V_" + (rows-1) + "_" + j,    // Segment de gauche du 2 (opposé au 0)
                        "H_" + rows + "_" + (j-1),// Segment à l'extrémité gauche
                        "V_" + (rows-2) + "_" + (j+1)     // Segment en haut à gauche
                    };
                } else {
                    // Le 2 est à droite du 0
                    batonSegments = new String[]{
                        "H_" + (rows-1) + "_" + (j+1),     // Segment du haut du 2
                        "V_" + (rows-1) + "_" + (j+2),     // Segment de droite du 2 (opposé au 0)
                        "H_" + rows + "_" + (j+2),     // Segment à l'extrémité droite
                        "V_" + (rows-2) + "_" + (j+1)      // Segment en haut à droite
                    };
                }
                
                for (String batonKey : batonSegments) {
                    Line segment = gridLines.get(batonKey);
                    if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                        segmentsToHighlight.add(batonKey + ":baton");
                        applicationPossible = true;
                    }
                }
            }
        }
        
        // Vérifier les paires 0-2 verticalement sur les bords
        
        // Bord gauche
        for (int i = 0; i < rows - 1; i++) {
            if ((gridNumbers[i][0] == 2 && gridNumbers[i+1][0] == 0) ||
                (gridNumbers[i][0] == 0 && gridNumbers[i+1][0] == 2)) {
                
                int index2 = gridNumbers[i][0] == 2 ? i : i+1;
                
                // Segments à marquer comme bâtons autour du 2
                String[] batonSegments;
                
                if (gridNumbers[i][0] == 2) {
                    // Le 2 est en haut du 0
                    batonSegments = new String[]{
                        "V_" + i + "_1",        // Segment de droite du 2
                        "H_" + i + "_0",        // Segment du haut du 2 (opposé au 0)
                        "V_" + (i-1) + "_0",    // Segment à l'extrémité haute
                        "H_" + (i+1) + "_1"     // Segment en haut à droite
                    };
                } else {
                    // Le 2 est en bas du 0
                    batonSegments = new String[]{
                        "V_" + (i+1) + "_1",     // Segment de droite du 2
                        "H_" + (i+2) + "_0",     // Segment du bas du 2 (opposé au 0)
                        "V_" + (i+2) + "_0",     // Segment à l'extrémité basse
                        "H_" + (i+1) + "_1"      // Segment en bas à droite
                    };
                }
                
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
            if ((gridNumbers[i][cols-1] == 2 && gridNumbers[i+1][cols-1] == 0) ||
                (gridNumbers[i][cols-1] == 0 && gridNumbers[i+1][cols-1] == 2)) {
                
                int index2 = gridNumbers[i][cols-1] == 2 ? i : i+1;
                
                // Segments à marquer comme bâtons autour du 2
                String[] batonSegments;
                
                if (gridNumbers[i][cols-1] == 2) {
                    // Le 2 est en haut du 0
                    batonSegments = new String[]{
                        "V_" + i + "_" + (cols-1),    // Segment de gauche du 2
                        "H_" + i + "_" + (cols-1),    // Segment du haut du 2 (opposé au 0)
                        "V_" + (i-1) + "_" + cols,// Segment à l'extrémité haute
                        "H_" + (i+1) + "_" + (cols-2) // Segment en haut à gauche
                    };
                } else {
                    // Le 2 est en bas du 0
                    batonSegments = new String[]{
                        "V_" + (i+1) + "_" + (cols-1),     // Segment de gauche du 2
                        "H_" + (i+2) + "_" + (cols-1),     // Segment du bas du 2 (opposé au 0)
                        "V_" + (i+2) + "_" + cols,     // Segment à l'extrémité basse
                        "H_" + (i+1) + "_" + (cols-2)      // Segment en bas à gauche
                    };
                }
                
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
    public static String getDescription() {
        return "0 et 2 sur un côté : Placez un bâton à l'opposé du bord et un autre à l'opposé du 0, " +
                "puis ajoutez deux bâtons aux extrémités.";
    }
}