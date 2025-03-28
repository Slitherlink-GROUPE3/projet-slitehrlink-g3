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
        
        // Vérifier les configurations possibles dans les coins
        
        // Coin supérieur gauche : 2 dans le coin et 1 à droite
        if (rows > 0 && cols > 1 && gridNumbers[0][0] == 2 && gridNumbers[0][1] == 1) {
            // D'après la règle du 2 dans l'angle, on place deux bâtons à l'opposé
            checkAndAddSegment("V_0_1", "baton");  // Vertical entre 2 et 1
            checkAndAddSegment("H_1_0", "baton");  // Horizontal sous le 2
            
            // Pour le 1, il nous reste un bâton à placer
            checkAndAddSegment("H_1_1", "baton");  // Horizontal sous le 1
            
            if (!segmentsToHighlight.isEmpty()) {
                applicationPossible = true;
            }
        }
        
        // Coin supérieur gauche : 1 dans le coin et 2 à droite
        if (rows > 0 && cols > 1 && gridNumbers[0][0] == 1 && gridNumbers[0][1] == 2) {
            // Placer un bâton pour le 1
            checkAndAddSegment("V_0_1", "baton");  // Vertical entre 1 et 2
            
            // Pour le 2, placer deux bâtons
            checkAndAddSegment("H_1_1", "baton");  // Horizontal sous le 2
            checkAndAddSegment("V_0_2", "baton");  // Vertical à droite du 2
            
            if (!segmentsToHighlight.isEmpty()) {
                applicationPossible = true;
            }
        }
        
        // Coin supérieur droit : 2 dans le coin et 1 à gauche
        if (rows > 0 && cols > 1 && gridNumbers[0][cols-1] == 2 && gridNumbers[0][cols-2] == 1) {
            // D'après la règle du 2 dans l'angle, on place deux bâtons à l'opposé
            checkAndAddSegment("V_0_" + (cols-1), "baton");  // Vertical entre 1 et 2
            checkAndAddSegment("H_1_" + (cols-1), "baton");  // Horizontal sous le 2
            
            // Pour le 1, il nous reste un bâton à placer
            checkAndAddSegment("H_1_" + (cols-2), "baton");  // Horizontal sous le 1
            
            if (!segmentsToHighlight.isEmpty()) {
                applicationPossible = true;
            }
        }
        
        // Coin supérieur droit : 1 dans le coin et 2 à gauche
        if (rows > 0 && cols > 1 && gridNumbers[0][cols-1] == 1 && gridNumbers[0][cols-2] == 2) {
            // Placer un bâton pour le 1
            checkAndAddSegment("V_0_" + (cols-1), "baton");  // Vertical entre 2 et 1
            
            // Pour le 2, placer deux bâtons
            checkAndAddSegment("H_1_" + (cols-2), "baton");  // Horizontal sous le 2
            checkAndAddSegment("V_0_" + (cols-2), "baton");  // Vertical à gauche du 2
            
            if (!segmentsToHighlight.isEmpty()) {
                applicationPossible = true;
            }
        }
        
        // Coin inférieur gauche : 2 dans le coin et 1 à droite
        if (rows > 1 && cols > 1 && gridNumbers[rows-1][0] == 2 && gridNumbers[rows-1][1] == 1) {
            // D'après la règle du 2 dans l'angle, on place deux bâtons à l'opposé
            checkAndAddSegment("V_" + (rows-1) + "_1", "baton");  // Vertical entre 2 et 1
            checkAndAddSegment("H_" + (rows-1) + "_0", "baton");  // Horizontal au-dessus du 2
            
            // Pour le 1, il nous reste un bâton à placer
            checkAndAddSegment("H_" + (rows-1) + "_1", "baton");  // Horizontal au-dessus du 1
            
            if (!segmentsToHighlight.isEmpty()) {
                applicationPossible = true;
            }
        }
        
        // Coin inférieur gauche : 1 dans le coin et 2 à droite
        if (rows > 1 && cols > 1 && gridNumbers[rows-1][0] == 1 && gridNumbers[rows-1][1] == 2) {
            // Placer un bâton pour le 1
            checkAndAddSegment("V_" + (rows-1) + "_1", "baton");  // Vertical entre 1 et 2
            
            // Pour le 2, placer deux bâtons
            checkAndAddSegment("H_" + (rows-1) + "_1", "baton");  // Horizontal au-dessus du 2
            checkAndAddSegment("V_" + (rows-1) + "_2", "baton");  // Vertical à droite du 2
            
            if (!segmentsToHighlight.isEmpty()) {
                applicationPossible = true;
            }
        }
        
        // Coin inférieur droit : 2 dans le coin et 1 à gauche
        if (rows > 1 && cols > 1 && gridNumbers[rows-1][cols-1] == 2 && gridNumbers[rows-1][cols-2] == 1) {
            // D'après la règle du 2 dans l'angle, on place deux bâtons à l'opposé
            checkAndAddSegment("V_" + (rows-1) + "_" + (cols-1), "baton");  // Vertical entre 1 et 2
            checkAndAddSegment("H_" + (rows-1) + "_" + (cols-1), "baton");  // Horizontal au-dessus du 2
            
            // Pour le 1, il nous reste un bâton à placer
            checkAndAddSegment("H_" + (rows-1) + "_" + (cols-2), "baton");  // Horizontal au-dessus du 1
            
            if (!segmentsToHighlight.isEmpty()) {
                applicationPossible = true;
            }
        }
        
        // Coin inférieur droit : 1 dans le coin et 2 à gauche
        if (rows > 1 && cols > 1 && gridNumbers[rows-1][cols-1] == 1 && gridNumbers[rows-1][cols-2] == 2) {
            // Placer un bâton pour le 1
            checkAndAddSegment("V_" + (rows-1) + "_" + (cols-1), "baton");  // Vertical entre 2 et 1
            
            // Pour le 2, placer deux bâtons
            checkAndAddSegment("H_" + (rows-1) + "_" + (cols-2), "baton");  // Horizontal au-dessus du 2
            checkAndAddSegment("V_" + (rows-1) + "_" + (cols-2), "baton");  // Vertical à gauche du 2
            
            if (!segmentsToHighlight.isEmpty()) {
                applicationPossible = true;
            }
        }
        
        return applicationPossible;
    }
    
    /**
     * Vérifie si un segment peut être ajouté à la liste des segments à mettre en évidence.
     * Le segment doit être disponible (ni ligne ni croix).
     *
     * @param lineId L'identifiant du segment
     * @param type Le type de segment à ajouter ("baton" ou "croix")
     */
    private void checkAndAddSegment(String lineId, String type) {
        Line segment = gridLines.get(lineId);
        if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
            segmentsToHighlight.add(lineId + ":" + type);
        }
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