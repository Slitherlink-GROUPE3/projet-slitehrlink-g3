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

public class Tech_0And3Diagonal implements Techniques {

    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsToHighlight;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_0And3Diagonal() {
        segmentsToHighlight = new ArrayList<>();
    }

    /**
     * Constructeur avec les informations nécessaires pour l'analyse.
     * 
     * @param gridNumbers Tableau 2D représentant les chiffres de la grille
     * @param gridLines Map des lignes de la grille
     * @param slitherlinkGrid Pane contenant la grille de Slitherlink
     */
    public Tech_0And3Diagonal(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.segmentsToHighlight = new ArrayList<>();
    }

    @Override
    public boolean estApplicable(Grille grille) {
        if (gridNumbers == null) {
            gridNumbers = grille.valeurs;
        }
        
        segmentsToHighlight.clear();
        
        boolean applicationPossible = false;
        
        // Parcourir la grille
        for (int i = 0; i < gridNumbers.length; i++) {
            for (int j = 0; j < gridNumbers[i].length; j++) {
                // Cherche un 3
                if (gridNumbers[i][j] == 3) {
                    // Vérifier diagonales
                    int[][] diags = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
                    
                    for (int[] diag : diags) {
                        int ni = i + diag[0];
                        int nj = j + diag[1];
                        
                        // Vérifier si cette position est valide et contient un 0
                        if (ni >= 0 && ni < gridNumbers.length && nj >= 0 && nj < gridNumbers[i].length) {
                            if (gridNumbers[ni][nj] == 0) {
                                // On a trouvé un 0 en diagonale avec un 3
                                
                                // Déterminer les segments appropriés en fonction de la position relative
                                List<String> segmentsForBaton = new ArrayList<>();
                                
                                // Déterminer la direction diagonale
                                if (ni > i && nj > j) { // Bas-droite
                                    // Segments pour lignes (bâtons) autour du 3
                                    segmentsForBaton.add("H_" + (i+1) + "_" + j); // Bas du 3
                                    segmentsForBaton.add("V_" + i + "_" + (j+1)); // Droite du 3
                                }
                                else if (ni > i && nj < j) { // Bas-gauche
                                    // Segments pour lignes (bâtons) autour du 3
                                    segmentsForBaton.add("H_" + (i+1) + "_" + j); // Bas du 3
                                    segmentsForBaton.add("V_" + i + "_" + j); // Gauche du 3
                                }
                                else if (ni < i && nj > j) { // Haut-droite
                                    // Segments pour lignes (bâtons) autour du 3
                                    segmentsForBaton.add("H_" + i + "_" + j); // Haut du 3
                                    segmentsForBaton.add("V_" + i + "_" + (j+1)); // Droite du 3
                                }
                                else if (ni < i && nj < j) { // Haut-gauche
                                    // Segments pour lignes (bâtons) autour du 3
                                    segmentsForBaton.add("H_" + i + "_" + j); // Haut du 3
                                    segmentsForBaton.add("V_" + i + "_" + j); // Gauche du 3
                                }
                                // Ajouter les segments appropriés à la liste pour la mise en évidence
                                for (String segmentKey : segmentsForBaton) {
                                    Line segment = gridLines.get(segmentKey);
                                    if (segment != null && segment.getStroke() == Color.TRANSPARENT && !aCroix(segment)) {
                                        segmentsToHighlight.add(segmentKey + ":baton");
                                        applicationPossible = true;
                                    }
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
        // Si la ligne ou le slitherlinkGrid est null, retourner false
        if (line == null || slitherlinkGrid == null) return false;
        
        // Parcourir tous les enfants du slitherlinkGrid
        for (javafx.scene.Node node : slitherlinkGrid.getChildren()) {
            // Si un nœud est une ligne et a comme userData notre ligne, c'est une croix
            if (node instanceof Line && node.getUserData() == line) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Applique la technique en mettant en surbrillance les segments concernés.
     *
     * @param grille La grille de jeu Slitherlink
     */
    @Override
    public void appliquer(Grille grille) {
        // Si la technique n'est pas applicable, ne rien faire
        if (!estApplicable(grille)) {
            return;
        }
        
        // Pour chaque segment à mettre en évidence
        for (String segment : segmentsToHighlight) {
            String[] parts = segment.split(":");
            String lineId = parts[0];
            boolean isBaton = parts[1].equals("baton");
            
            Line line = gridLines.get(lineId);
            if (line != null) {
                // Appliquer un effet de surbrillance
                Glow glow = new Glow(0.8);
                line.setEffect(glow);
                line.setStroke(isBaton ? Color.GREEN : Color.RED);
                line.setOpacity(0.7);
                
                // Animation de surbrillance
                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), line);
                fadeIn.setFromValue(0.3);
                fadeIn.setToValue(0.9);
                fadeIn.setCycleCount(3);
                fadeIn.setAutoReverse(true);
                fadeIn.play();
                
                // Remettre l'état original après l'animation
                fadeIn.setOnFinished(event -> {
                    line.setEffect(null);
                    line.setStroke(Color.TRANSPARENT);
                    line.setOpacity(1.0);
                });
            }
        }
    }
}