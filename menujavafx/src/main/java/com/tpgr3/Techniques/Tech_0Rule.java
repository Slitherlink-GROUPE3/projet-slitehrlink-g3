package com.tpgr3.Techniques;

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
 */
public class Tech_0Rule implements Techniques {
    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private List<String> segmentsAMarquer;

    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_0Rule() {
        segmentsAMarquer = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param gridNumbers La grille de nombres
     * @param gridLines Les segments de la grille
     * @param slitherlinkGrid Le panneau contenant la grille
     */
    public Tech_0Rule(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
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
        
        // Liste pour suivre les croix créées (pour les animations et la suppression)
        List<Line> allCrossLines = new ArrayList<>();
        
        // Pour chaque segment à marquer
        for (String lineId : segmentsAMarquer) {
            Line line = gridLines.get(lineId);
            if (line != null) {
                // Créer des croix rouges (couleur spécifique pour les techniques)
                Line cross1, cross2;
                
                if (line.getStartX() == line.getEndX()) { // Ligne verticale
                    cross1 = new Line(
                            line.getStartX() - 10, line.getStartY() + 20,
                            line.getEndX() + 10, line.getEndY() - 20);
                    cross2 = new Line(
                            line.getStartX() - 10, line.getEndY() - 20,
                            line.getEndX() + 10, line.getStartY() + 20);
                } else { // Ligne horizontale
                    cross1 = new Line(
                            line.getStartX() + 20, line.getStartY() - 10,
                            line.getEndX() - 20, line.getEndY() + 10);
                    cross2 = new Line(
                            line.getStartX() + 20, line.getEndY() + 10,
                            line.getEndX() - 20, line.getStartY() - 10);
                }
                
                // Configuration des croix
                cross1.setStrokeWidth(3);
                cross1.setUserData(line);
                cross1.setStroke(Color.RED); // Rouge pour la technique
                
                cross2.setStrokeWidth(3);
                cross2.setUserData(line);
                cross2.setStroke(Color.RED); // Rouge pour la technique
                
                // Ajouter un effet visuel
                Glow glow = new Glow(0.8);
                cross1.setEffect(glow);
                cross2.setEffect(glow);
                
                // Ajouter les croix à la grille
                slitherlinkGrid.getChildren().addAll(cross1, cross2);
                
                // Garder une référence pour l'animation
                allCrossLines.add(cross1);
                allCrossLines.add(cross2);
            }
        }
        
        // Animation de mise en évidence pour toutes les croix créées
        for (Line crossLine : allCrossLines) {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), crossLine);
            fadeIn.setFromValue(0.3);
            fadeIn.setToValue(0.9);
            fadeIn.setCycleCount(3);
            fadeIn.setAutoReverse(true);
            fadeIn.play();
            
            // Supprimer la croix après l'animation
            fadeIn.setOnFinished(event -> {
                slitherlinkGrid.getChildren().remove(crossLine);
            });
        }
    }
}