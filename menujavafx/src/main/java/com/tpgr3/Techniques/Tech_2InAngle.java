package com.tpgr3.Techniques;
import com.menu.javafx.GameScene;

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
 * Implémentation de la technique du 2 en angle de chemin dans Slitherlink.
 * Quand il y a un angle de chemin avec un 2 en diagonal, on place deux 
 * bâtons autour du 2 à l'opposé du chemin.
 */
public class Tech_2InAngle implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsToHighlight;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_2InAngle() {
        segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Constructeur avec les informations nécessaires pour l'analyse.
     * 
     * @param gridNumbers Tableau 2D représentant les chiffres de la grille
     * @param gridLines Map des lignes de la grille
     * @param slitherlinkGrid Pane contenant la grille de Slitherlink
     */
    public Tech_2InAngle(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Vérifie si la technique du 2 en angle est applicable sur la grille actuelle.
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
        
        // Parcourir la grille
        for (int i = 1; i < gridNumbers.length-1; i++) {
            for (int j = 1; j < gridNumbers[i].length-1; j++) {
                // Vérifier s'il y a un 2 dans la case actuelle
                if (gridNumbers[i][j] == 2) {
                    // Cas 1: Angle en haut à gauche du 2
                    if (isLinePresent("H_" + i + "_" + (j-1)) && 
                        isLinePresent("V_" + (i-1) + "_" + j)) {
                        // On place des bâtons à droite et en bas du 2
                        String[] batonSegments = {
                            "H_" + (i+1) + "_" + j,  // Bâton en bas
                            "V_" + i + "_" + (j+1)   // Bâton à droite
                        };
                        
                        for (String batonKey : batonSegments) {
                            Line segment = gridLines.get(batonKey);
                            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                                segmentsToHighlight.add(batonKey + ":baton");
                                applicationPossible = true;
                            }
                        }
                        return applicationPossible;
                    }
                    
                    // Cas 2: Angle en haut à droite du 2
                    if (isLinePresent("H_" + i + "_" + (j+1)) && 
                        isLinePresent("V_" + (i-1) + "_" + (j+1))) {
                        // On place des bâtons à gauche et en bas du 2
                        String[] batonSegments = {
                            "H_" + (i+1) + "_" + j,  // Bâton en bas
                            "V_" + i + "_" + j       // Bâton à gauche
                        };
                        
                        for (String batonKey : batonSegments) {
                            Line segment = gridLines.get(batonKey);
                            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                                segmentsToHighlight.add(batonKey + ":baton");
                                applicationPossible = true;
                            }
                        }
                        return applicationPossible;
                    }
                    
                    // Cas 3: Angle en bas à gauche du 2
                    if (isLinePresent("H_" + (i+1) + "_" + (j-1)) && 
                        isLinePresent("V_" + (i+1) + "_" + j)) {
                        // On place des bâtons à droite et en haut du 2
                        String[] batonSegments = {
                            "H_" + i + "_" + j,       // Bâton en haut
                            "V_" + i + "_" + (j+1)    // Bâton à droite
                        };
                        
                        for (String batonKey : batonSegments) {
                            Line segment = gridLines.get(batonKey);
                            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                                segmentsToHighlight.add(batonKey + ":baton");
                                applicationPossible = true;
                            }
                        }
                        return applicationPossible;
                    }
                    
                    // Cas 4: Angle en bas à droite du 2
                    if (isLinePresent("H_" + (i+1) + "_" + (j+1)) && 
                        isLinePresent("V_" + (i+1) + "_" + (j+1))) {
                        // On place des bâtons à gauche et en haut du 2
                        String[] batonSegments = {
                            "H_" + i + "_" + j,      // Bâton en haut
                            "V_" + i + "_" + j       // Bâton à gauche
                        };
                        
                        for (String batonKey : batonSegments) {
                            Line segment = gridLines.get(batonKey);
                            if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                                segmentsToHighlight.add(batonKey + ":baton");
                                applicationPossible = true;
                            }
                        }
                        return applicationPossible;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Vérifie si un segment est présent et de la couleur spécifiée.
     *
     * @param lineId L'identifiant du segment
     * @param color La couleur à vérifier
     * @return true si le segment est présent et de la couleur spécifiée, false sinon
     */
    private boolean isLinePresent(String lineId) {
        Line line = gridLines.get(lineId);
        if (line == null) {
            return false;
        }
    
        // Vérifier si le segment a la couleur DARK_DARK_COLOR ou est vert (Color.GREEN)
        if (line.getStroke() instanceof Color) {
            Color strokeColor = (Color) line.getStroke();
            String strokeHex = String.format("#%02x%02x%02x",
                (int) (strokeColor.getRed() * 255),
                (int) (strokeColor.getGreen() * 255),
                (int) (strokeColor.getBlue() * 255)
            );
    
            // Vérifier si la couleur correspond à LIGHT_DARK_COLOR
            if (strokeHex.equalsIgnoreCase(GameScene.LIGHT_DARK_COLOR)) {
                return true;
            }
            // Vérifier si la couleur correspond à DARK_DARK_COLOR
            if (strokeHex.equalsIgnoreCase(GameScene.DARK_DARK_COLOR)) {
                return true;
            }
        }
    
        return false;
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
            
            Line line = gridLines.get(lineId);
            if (line != null) {
                Glow glow = new Glow(0.8);
                line.setEffect(glow);
                line.setStroke(Color.GREEN);
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