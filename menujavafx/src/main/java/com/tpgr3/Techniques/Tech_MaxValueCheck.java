package com.tpgr3.Techniques;

import java.util.Map;

import com.menu.javafx.CreateCrossMove;
import com.menu.javafx.SlitherGrid;
import com.tpgr3.Grille;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * Technique qui vérifie que le nombre de traits autour d'une cellule
 * ne dépasse pas la valeur indiquée dans la cellule.
 */
public class Tech_MaxValueCheck implements Techniques {
    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private SlitherGrid slitherGrid;
    private String lineId;
    
    /**
     * Constructeur par défaut pour l'instanciation réflexive.
     */
    public Tech_MaxValueCheck() {
    }
    
    /**
     * Constructeur avec paramètres pour le détecteur de techniques.
     */
    public Tech_MaxValueCheck(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
    }
    
    /**
     * Constructeur complet.
     */
    public Tech_MaxValueCheck(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid, SlitherGrid slitherGrid) {
        this(gridNumbers, gridLines, slitherlinkGrid);
        this.slitherGrid = slitherGrid;
    }

    @Override
    public boolean estApplicable(Grille grille) {
        // Cette technique n'est pas applicable automatiquement
        // Elle est appelée directement lors du placement d'un trait
        return false;
    }

    @Override
    public void appliquer(Grille grille) {
        // Cette méthode n'est pas utilisée directement
    }
    
    /**
     * Définit l'identifiant du segment actuel.
     * Utile pour mieux cibler les cellules à vérifier.
     * 
     * @param lineId L'identifiant du segment
     */
    public void setLineId(String lineId) {
        this.lineId = lineId;
    }
    
    /**
     * Vérifie si l'ajout d'un trait dépasserait la valeur maximale d'une cellule.
     * Si c'est le cas, place des croix sur les segments restants.
     * 
     * @param row Ligne où un trait vient d'être placé
     * @param col Colonne où un trait vient d'être placé
     * @return true si des croix ont été placées, false sinon
     */
    public boolean verifierEtAppliquerMaxValue(int row, int col) {
        boolean croixPlacees = false;
        
        if (lineId != null) {
            if (lineId.startsWith("H_")) {
                // Pour un segment horizontal, vérifier les cellules au-dessus et en dessous
                // Cellule au-dessus (si existe)
                if (row > 0) {
                    System.out.println("Vérification cellule au-dessus: [" + (row-1) + "," + col + "]");
                    croixPlacees |= verifierCellule(row-1, col);
                }
                // Cellule en dessous (toujours vérifier, même si c'est la limite de la grille)
                System.out.println("Vérification cellule en dessous: [" + row + "," + col + "]");
                croixPlacees |= verifierCellule(row, col);
            } else if (lineId.startsWith("V_")) {
                // Pour un segment vertical, vérifier les cellules à gauche et à droite
                // Cellule à gauche (si existe)
                if (col > 0) {
                    System.out.println("Vérification cellule à gauche: [" + row + "," + (col-1) + "]");
                    croixPlacees |= verifierCellule(row, col-1);
                }
                // Cellule à droite (toujours vérifier, même si c'est la limite de la grille)
                System.out.println("Vérification cellule à droite: [" + row + "," + col + "]");
                croixPlacees |= verifierCellule(row, col);
            }
        } else {
            // Approche générique si on ne peut pas déterminer le type de segment
            System.out.println("Vérification générique des cellules adjacentes");
            // Vérifier les quatre cellules adjacentes au segment
            if (row > 0 && col > 0) {
                croixPlacees |= verifierCellule(row-1, col-1);
            }
            if (row > 0 && col < gridNumbers[0].length) {
                croixPlacees |= verifierCellule(row-1, col);
            }
            if (row < gridNumbers.length && col > 0) {
                croixPlacees |= verifierCellule(row, col-1);
            }
            if (row < gridNumbers.length && col < gridNumbers[0].length) {
                croixPlacees |= verifierCellule(row, col);
            }
        }
        
        return croixPlacees;
    }
    
    /**
     * Vérifie si une cellule a atteint sa valeur maximale.
     * Si oui, place des croix sur les segments restants.
     * 
     * @param row Ligne de la cellule
     * @param col Colonne de la cellule
     * @return true si des croix ont été placées, false sinon
     */
    private boolean verifierCellule(int row, int col) {
        // Vérifier si la cellule est valide et a une valeur
        if (row < 0 || row >= gridNumbers.length || col < 0 || col >= gridNumbers[0].length) {
            return false;
        }
        
        int valeurCellule = gridNumbers[row][col];
        if (valeurCellule <= 0) {
            return false; // Ignorer les cellules sans valeur ou avec 0
        }
        
        // Compter les traits déjà placés
        int nbTraits = compterTraitsAutourCellule(row, col);
        
        System.out.println("Cellule [" + row + "," + col + "]: valeur=" + valeurCellule + ", traits actuels=" + nbTraits);
        
        // Si le nombre de traits est égal à la valeur de la cellule,
        // placer des croix sur les segments restants
        if (nbTraits == valeurCellule) {
            boolean croixPlacees = false;
            
            // Vérifier et marquer les segments vides autour de la cellule
            String[] segments = {
                "H_" + row + "_" + col,         // Segment du haut
                "H_" + (row + 1) + "_" + col,   // Segment du bas
                "V_" + row + "_" + col,         // Segment de gauche
                "V_" + row + "_" + (col + 1)    // Segment de droite
            };
            
            for (String segmentId : segments) {
                Line line = gridLines.get(segmentId);
                if (line != null && line.getStroke() == Color.TRANSPARENT && !aCroix(line)) {
                    System.out.println("Placement d'une croix sur le segment " + segmentId);
                    // Placer une croix
                    if (slitherGrid != null) {
                        new CreateCrossMove(line, "tech_max_value_check", Color.BLACK, slitherGrid);
                        croixPlacees = true;
                    }
                }
            }
            
            return croixPlacees;
        }
        
        return false;
    }
    
    /**
     * Compte le nombre de traits autour d'une cellule.
     *
     * @param row Ligne de la cellule
     * @param col Colonne de la cellule
     * @return Le nombre de traits autour de la cellule
     */
    private int compterTraitsAutourCellule(int row, int col) {
        int count = 0;
        
        // Vérifier les 4 segments autour de la cellule
        if (aTrait("H_" + row + "_" + col)) count++; // Segment du haut
        if (aTrait("H_" + (row + 1) + "_" + col)) count++; // Segment du bas
        if (aTrait("V_" + row + "_" + col)) count++; // Segment de gauche
        if (aTrait("V_" + row + "_" + (col + 1))) count++; // Segment de droite
        
        return count;
    }
    
    /**
     * Vérifie si un segment a déjà un trait.
     *
     * @param lineId L'identifiant du segment
     * @return true si le segment a un trait, false sinon
     */
    private boolean aTrait(String lineId) {
        Line line = gridLines.get(lineId);
        if (line == null) return false;
        
        // Vérifier si la ligne a une couleur (donc un trait)
        return line.getStroke() != Color.TRANSPARENT;
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

    public static String getDescription() {
        return "";
    }
}