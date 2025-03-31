package com.menu.javafx;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * Classe utilitaire pour marquer les zéros dans une grille de Slitherlink.
 * Cette implémentation indépendante évite les problèmes avec le système d'historique.
 */
public class ZeroMarker {

    /**
     * Marque toutes les croix autour des cellules contenant des zéros
     * @param slitherGrid La grille de jeu
     * @return Le nombre de croix placées
     */
    public static int markZeros(SlitherGrid slitherGrid) {
        int crossesAdded = 0;
        int[][] gridNumbers = slitherGrid.getGridNumbers();
        int gridRows = slitherGrid.getGridRows();
        int gridCols = slitherGrid.getGridCols();
        
        // Pour chaque cellule de la grille
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                // Si la cellule contient un zéro
                if (gridNumbers[row][col] == 0) {
                    // Marquer les lignes adjacentes
                    crossesAdded += markAdjacentLines(row, col, slitherGrid);
                }
            }
        }
        
        return crossesAdded;
    }
    
    /**
     * Marque les lignes adjacentes à une cellule avec des croix
     * @param row Ligne de la cellule
     * @param col Colonne de la cellule
     * @param slitherGrid La grille de jeu
     * @return Le nombre de croix placées
     */
    private static int markAdjacentLines(int row, int col, SlitherGrid slitherGrid) {
        int crossesAdded = 0;
        
        // Définir les clés des lignes adjacentes
        String[] lineKeys = {
            "H_" + row + "_" + col,      // Ligne horizontale du haut
            "H_" + (row + 1) + "_" + col, // Ligne horizontale du bas
            "V_" + row + "_" + col,       // Ligne verticale de gauche
            "V_" + row + "_" + (col + 1)  // Ligne verticale de droite
        };
        
        // Pour chaque ligne adjacente
        for (String lineKey : lineKeys) {
            Line line = slitherGrid.getGridLines().get(lineKey);
            if (line != null && !slitherGrid.isLineActive(line) && !slitherGrid.hasCross(line)) {
                // Mettre la ligne en transparent pour s'assurer qu'elle n'a pas d'état actif
                line.setStroke(Color.TRANSPARENT);
                
                // Créer directement une croix en utilisant le helper (sans passer par le système de mouvements)
                CrossMoveHelper.createCrossForSave(line, false, slitherGrid);
                
                // Ajouter manuellement cet état à la sauvegarde
                crossesAdded++;
            }
        }
        
        return crossesAdded;
    }
}