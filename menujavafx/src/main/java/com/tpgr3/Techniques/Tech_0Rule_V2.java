package com.tpgr3.Techniques;

import com.menu.javafx.CreateCrossMove;
import com.menu.javafx.SlitherGrid;
import com.tpgr3.Grille;
import javafx.animation.FadeTransition;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implémentation de la technique de la règle des 0 dans Slitherlink.
 * Quand une cellule contient un 0, aucun segment ne doit être tracé autour de cette cellule.
 * Cette version place des croix définitives sur la grille.
 */
public class Tech_0Rule_V2 implements Techniques {
    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsAMarquer;
    private SlitherGrid slitherGrid;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_0Rule_V2() {
        segmentsAMarquer = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param gridNumbers La grille de nombres
     * @param gridLines Les segments de la grille
     * @param slitherlinkGrid Le panneau contenant la grille
     * @param slitherGrid La grille de jeu
     */
    public Tech_0Rule_V2(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid, SlitherGrid slitherGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        this.slitherGrid = slitherGrid;
        this.segmentsAMarquer = new ArrayList<>();
    }

    /**
     * Vérifie si la technique de la règle des 0 est applicable sur la grille actuelle.
     *
     * @param grille La grille de jeu Slitherlink
     * @return true si la technique est applicable, false sinon
     */
    @Override
    public boolean estApplicable(Grille grille) {
        if (gridNumbers == null) {
            // Si la classe a été instanciée sans paramètres, on utilise les données de la grille
            gridNumbers = grille.valeurs;
        }
        
        segmentsAMarquer.clear();
        
        int rows = gridNumbers.length;
        int cols = gridNumbers[0].length;
        
        boolean applicationPossible = false;
        
        // Parcourir toutes les cellules de la grille
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Si la cellule contient un 0
                if (gridNumbers[i][j] == 0) {
                    // Vérifier les 4 segments autour de la cellule
                    if (verifierSegment("H_" + i + "_" + j)) {       // Segment du haut
                        applicationPossible = true;
                    }
                    if (verifierSegment("H_" + (i + 1) + "_" + j)) { // Segment du bas
                        applicationPossible = true;
                    }
                    if (verifierSegment("V_" + i + "_" + j)) {       // Segment de gauche
                        applicationPossible = true;
                    }
                    if (verifierSegment("V_" + i + "_" + (j + 1))) { // Segment de droite
                        applicationPossible = true;
                    }
                }
            }
        }
        
        return applicationPossible;
    }

    /**
     * Vérifie si un segment peut être marqué d'une croix.
     * Un segment peut être marqué si:
     * - Il existe dans la grille
     * - Il n'a pas déjà une ligne ou une croix
     *
     * @param lineId L'identifiant du segment
     * @return true si le segment peut être marqué, false sinon
     */
    private boolean verifierSegment(String lineId) {
        Line line = gridLines.get(lineId);
        if (line == null) {
            return false;
        }
        
        // Si le segment est vide (ni ligne ni croix)
        if (line.getStroke() == Color.TRANSPARENT && !aCroix(line)) {
            segmentsAMarquer.add(lineId);
            return true;
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

    @Override
    public void appliquer(Grille grille) {
        // Si la technique n'est pas applicable, ne rien faire
        if (!estApplicable(grille)) {
            return;
        }
        
        // Pour chaque segment à marquer
        for (String lineId : segmentsAMarquer) {
            Line line = gridLines.get(lineId);
            if (line != null) {
                // Utiliser CreateCrossMove pour créer une croix définitive qui peut être retirée
                new CreateCrossMove(line, "tech_0_rule", Color.BLACK, slitherGrid);
                
                // Trouver les croix nouvellement créées pour ajouter l'animation
                slitherlinkGrid.getChildren().stream()
                    .filter(node -> node instanceof Line && node.getUserData() == line)
                    .forEach(cross -> {
                        // Appliquer un effet temporaire pour mettre en évidence
                        Glow glow = new Glow(0.8);
                        cross.setEffect(glow);
                        
                        // Animation fade in/out de l'effet
                        FadeTransition fadeEffect = new FadeTransition(Duration.millis(500), cross);
                        fadeEffect.setFromValue(0.3);
                        fadeEffect.setToValue(1.0);
                        fadeEffect.setCycleCount(3);
                        fadeEffect.setAutoReverse(true);
                        fadeEffect.play();
                        
                        // Supprimer uniquement l'effet après l'animation
                        fadeEffect.setOnFinished(event -> {
                            cross.setEffect(null);
                        });
                    });
            }
        }
    }
    public static String getDescription() {
        return "Règle du 0 : Aucun segment ne doit entourer une case contenant un 0. " +
               "Marquez tous les segments adjacents avec une croix.";
    }
}