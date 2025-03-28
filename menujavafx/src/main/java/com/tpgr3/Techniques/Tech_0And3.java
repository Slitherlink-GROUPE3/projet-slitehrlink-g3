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
 * Implémentation de la technique des 0 et 3 dans Slitherlink.
 * Quand un 0 et un 3 sont adjacents, on place une croix au-dessus du 3, 
 * ce qui impose une seule façon de tracer la boucle autour du 3.
 */
public class Tech_0And3 implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsToHighlight;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_0And3() {
        segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param gridNumbers La grille de nombres
     * @param gridLines Les segments de la grille
     * @param slitherlinkGrid Le panneau contenant la grille
     */
    public Tech_0And3(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Vérifie si la technique des 0 et 3 est applicable sur la grille actuelle.
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
        
        // Vérifier les paires 0-3 horizontalement adjacentes
        for (int i = 0; i < gridNumbers.length; i++) {
            for (int j = 0; j < gridNumbers[i].length - 1; j++) {
                if ((gridNumbers[i][j] == 0 && gridNumbers[i][j+1] == 3) ||
                    (gridNumbers[i][j] == 3 && gridNumbers[i][j+1] == 0)) {
                    
                    int index3 = gridNumbers[i][j] == 3 ? j : j+1;
                    int index0 = gridNumbers[i][j] == 0 ? j : j+1;
                    
                    // Segment du haut du 3 (à marquer comme croix)
                    String topSegment = "H_" + i + "_" + index3;
                    Line segment = gridLines.get(topSegment);
                    if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                        segmentsToHighlight.add(topSegment + ":croix");
                        applicationPossible = true;
                    }
                    
                    // Trois segments autour du 3 (à marquer comme bâtons)
                    // Bas, gauche et droite du 3 (sauf le segment partagé avec le 0)
                    String[] batonSegments = new String[3];
                    
                    batonSegments[0] = "H_" + (i+1) + "_" + index3;  // Bas du 3
                    
                    if (index3 < index0) {
                        // Le 3 est à gauche du 0
                        batonSegments[1] = "V_" + i + "_" + index3;       // Gauche du 3
                        batonSegments[2] = "V_" + i + "_" + (index3+1);   // Droite du 3 (partagé avec le 0)
                    } else {
                        // Le 3 est à droite du 0
                        batonSegments[1] = "V_" + i + "_" + index3;       // Gauche du 3 (partagé avec le 0)
                        batonSegments[2] = "V_" + i + "_" + (index3+1);   // Droite du 3
                    }
                    
                    for (String batonKey : batonSegments) {
                        segment = gridLines.get(batonKey);
                        if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                            segmentsToHighlight.add(batonKey + ":baton");
                            applicationPossible = true;
                        }
                    }
                    
                    // Croix autour du 0 (sauf le segment partagé avec le 3)
                    String[] croixAutour0 = new String[3];
                    
                    croixAutour0[0] = "H_" + i + "_" + index0;        // Haut du 0
                    croixAutour0[1] = "H_" + (i+1) + "_" + index0;    // Bas du 0
                    
                    if (index0 < index3) {
                        // Le 0 est à gauche du 3
                        croixAutour0[2] = "V_" + i + "_" + index0;    // Gauche du 0
                    } else {
                        // Le 0 est à droite du 3
                        croixAutour0[2] = "V_" + i + "_" + (index0+1);  // Droite du 0
                    }
                    
                    for (String croixKey : croixAutour0) {
                        segment = gridLines.get(croixKey);
                        if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                            segmentsToHighlight.add(croixKey + ":croix");
                            applicationPossible = true;
                        }
                    }
                }
            }
        }
        
        // Vérifier les paires 0-3 verticalement adjacentes
        for (int i = 0; i < gridNumbers.length - 1; i++) {
            for (int j = 0; j < gridNumbers[i].length; j++) {
                if ((gridNumbers[i][j] == 0 && gridNumbers[i+1][j] == 3) ||
                    (gridNumbers[i][j] == 3 && gridNumbers[i+1][j] == 0)) {
                    
                    int index3 = gridNumbers[i][j] == 3 ? i : i+1;
                    int index0 = gridNumbers[i][j] == 0 ? i : i+1;
                    
                    // Segment à gauche du 3 (à marquer comme croix)
                    String leftSegment = "V_" + index3 + "_" + j;
                    Line segment = gridLines.get(leftSegment);
                    if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                        segmentsToHighlight.add(leftSegment + ":croix");
                        applicationPossible = true;
                    }
                    
                    // Trois segments autour du 3 (à marquer comme bâtons)
                    // Haut, bas et droite du 3 (sauf le segment partagé avec le 0)
                    String[] batonSegments = new String[3];
                    
                    batonSegments[0] = "V_" + index3 + "_" + (j+1);  // Droite du 3
                    
                    if (index3 < index0) {
                        // Le 3 est au-dessus du 0
                        batonSegments[1] = "H_" + index3 + "_" + j;       // Haut du 3
                        batonSegments[2] = "H_" + (index3+1) + "_" + j;   // Bas du 3 (partagé avec le 0)
                    } else {
                        // Le 3 est en-dessous du 0
                        batonSegments[1] = "H_" + index3 + "_" + j;       // Haut du 3 (partagé avec le 0)
                        batonSegments[2] = "H_" + (index3+1) + "_" + j;   // Bas du 3
                    }
                    
                    for (String batonKey : batonSegments) {
                        segment = gridLines.get(batonKey);
                        if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                            segmentsToHighlight.add(batonKey + ":baton");
                            applicationPossible = true;
                        }
                    }
                    
                    // Croix autour du 0 (sauf le segment partagé avec le 3)
                    String[] croixAutour0 = new String[3];
                    
                    croixAutour0[0] = "V_" + index0 + "_" + (j+1);    // Droite du 0
                    croixAutour0[1] = "V_" + index0 + "_" + j;        // Gauche du 0
                    
                    if (index0 < index3) {
                        // Le 0 est au-dessus du 3
                        croixAutour0[2] = "H_" + index0 + "_" + j;    // Haut du 0
                    } else {
                        // Le 0 est en-dessous du 3
                        croixAutour0[2] = "H_" + (index0+1) + "_" + j;  // Bas du 0
                    }
                    
                    for (String croixKey : croixAutour0) {
                        segment = gridLines.get(croixKey);
                        if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                            segmentsToHighlight.add(croixKey + ":croix");
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
}