package com.menu.javafx;

import java.util.Map;

import com.tpgr3.Grille;
import com.tpgr3.Techniques.Tech_MaxValueCheck;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class LineMove extends Move {

    private SlitherGrid slitherGrid;
    private SlitherGridChecker slitherGridChecker;
    
    // Identifiant du segment (utilisé pour vérifier les cellules adjacentes)
    private String lineId;

    public LineMove(Line line, Object action, Color color, SlitherGrid slitherGrid) {
        super(line, action, color);
        this.slitherGrid = slitherGrid;
        this.slitherGridChecker = new SlitherGridChecker(slitherGrid);
        this.lineId = (String) action; // Récupérer l'identifiant du segment
    }

    @Override
    public void undoMove() {
        if (this.line() != null) {
            this.line().setStroke(Color.TRANSPARENT);
        }
    }

    @Override
    public void redoMove() {
        if (this.line() != null) {
            // Colorer le trait
            this.line().setStroke(Color.web(SlitherGrid.DARK_COLOR));
            
            // Si le mode des croix automatiques est activé, vérifier et placer des croix
            if (AutoCrossButton.isAutoCrossEnabled()) {
                // Placer automatiquement des croix aux endroits où c'est nécessaire
                placerCroixAutomatiquement();
            }

            // Vérification automatique seulement si on n'est pas en mode hypothèse
            if (!slitherGrid.isHypothesisInactive()) {
                slitherGridChecker.checkGridAutomatically();
            }
        }
    }
    
    /**
     * Place automatiquement des croix après l'ajout d'un trait.
     * Vérifie les cellules adjacentes et place des croix si nécessaire.
     */
    private void placerCroixAutomatiquement() {
        if (lineId == null || lineId.isEmpty()) {
            return;
        }
        
        // Coordonnées du segment actuel
        String[] parts = lineId.split("_");
        if (parts.length != 3) {
            return;
        }
        
        try {
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            
            // 1. D'abord, vérifier les cellules adjacentes pour les valeurs complétées
            if (lineId.startsWith("H_")) {
                // Segment horizontal - vérifier les cellules au-dessus et en-dessous
                if (row > 0) verifierEtPlacerCroixCellule(row-1, col);
                if (row < slitherGrid.getGridRows()) verifierEtPlacerCroixCellule(row, col);
            } else if (lineId.startsWith("V_")) {
                // Segment vertical - vérifier les cellules à gauche et à droite
                if (col > 0) verifierEtPlacerCroixCellule(row, col-1);
                if (col < slitherGrid.getGridCols()) verifierEtPlacerCroixCellule(row, col);
            }
            
            // 2. Ensuite, vérifier les règles spéciales (éviter les boucles prématurées, etc.)
            verifierEtPlacerCroixReglesSpeciales(row, col);
            
        } catch (NumberFormatException e) {
            // Ignorer les erreurs de parsing
        }
    }
    
    /**
     * Vérifie si une cellule a atteint sa valeur et place des croix si nécessaire.
     */
    private void verifierEtPlacerCroixCellule(int row, int col) {
        // Vérifier si la cellule est valide
        if (row < 0 || row >= slitherGrid.getGridRows() || col < 0 || col >= slitherGrid.getGridCols()) {
            return;
        }
        
        // Obtenir la valeur de la cellule
        int[][] gridNumbers = slitherGrid.getGridNumbers();
        int valeurCellule = gridNumbers[row][col];
        if (valeurCellule <= 0) {
            return; // Ignorer les cellules sans valeur ou avec 0
        }
        
        // Compter les traits autour de la cellule
        int nbTraits = compterTraitsAutourCellule(row, col);
        
        // Si la cellule a atteint exactement sa valeur, placer des croix sur les segments vides
        if (nbTraits == valeurCellule) {
            placerCroixAutourCellule(row, col);
        }
    }
    
    /**
     * Place des croix autour d'une cellule sur tous les segments sans trait.
     */
    private void placerCroixAutourCellule(int row, int col) {
        // Les 4 segments autour de la cellule
        String[] segments = {
            "H_" + row + "_" + col,           // Segment du haut
            "H_" + (row + 1) + "_" + col,     // Segment du bas
            "V_" + row + "_" + col,           // Segment de gauche
            "V_" + row + "_" + (col + 1)      // Segment de droite
        };
        
        Map<String, Line> gridLines = slitherGrid.getGridLines();
        
        for (String segmentId : segments) {
            Line line = gridLines.get(segmentId);
            if (line != null && line.getStroke() == Color.TRANSPARENT && !slitherGrid.hasCross(line)) {
                //System.out.println("Placement automatique d'une croix sur: " + segmentId);
                
                // Créer une croix
                CreateCrossMove crossMove = new CreateCrossMove(line, "auto_cross", Color.BLACK, slitherGrid);
                
                // Ajouter le mouvement à l'historique
                slitherGrid.addMove(crossMove);
            }
        }
    }
    
    /**
     * Compte le nombre de traits autour d'une cellule.
     */
    private int compterTraitsAutourCellule(int row, int col) {
        int count = 0;
        Map<String, Line> gridLines = slitherGrid.getGridLines();
        
        // Vérifier les 4 segments autour de la cellule
        if (aTrait(gridLines, "H_" + row + "_" + col)) count++; // Segment du haut
        if (aTrait(gridLines, "H_" + (row + 1) + "_" + col)) count++; // Segment du bas
        if (aTrait(gridLines, "V_" + row + "_" + col)) count++; // Segment de gauche
        if (aTrait(gridLines, "V_" + row + "_" + (col + 1))) count++; // Segment de droite
        
        return count;
    }
    
    /**
     * Vérifie des règles spéciales pour éviter des structures invalides (boucles prématurées, etc.)
     * et place des croix aux endroits appropriés.
     */
    private void verifierEtPlacerCroixReglesSpeciales(int row, int col) {
        // Récupérer les lignes adjacentes au segment actuel
        Map<String, Line> gridLines = slitherGrid.getGridLines();
        
        // Si c'est un segment horizontal
        if (lineId.startsWith("H_")) {
            // Vérifier si les segments forment un "L" (risque de boucle prématurée)
            verifierEtPlacerCroixPourLHorizontal(row, col, gridLines);
        } 
        // Si c'est un segment vertical
        else if (lineId.startsWith("V_")) {
            // Vérifier si les segments forment un "L" (risque de boucle prématurée)
            verifierEtPlacerCroixPourLVertical(row, col, gridLines);
        }
    }
    
    /**
     * Vérifie et place des croix pour éviter des "L" horizontaux qui pourraient former des boucles prématurées.
     */
    private void verifierEtPlacerCroixPourLHorizontal(int row, int col, Map<String, Line> gridLines) {
        // Exemple de règle: Si on a un "L" formé par un segment horizontal et un segment vertical
        // à gauche, placer une croix sur le segment horizontal à gauche pour éviter une boucle prématurée
        
        // Vérifier le segment vertical à gauche
        if (aTrait(gridLines, "V_" + row + "_" + col)) {
            // Si ce segment a un trait, vérifier les segments horizontaux à gauche et à droite
            // qui pourraient former un "L"
            
            // Segment horizontal à gauche du point (col-1)
            if (col > 0 && !aTrait(gridLines, "H_" + row + "_" + (col-1)) && !slitherGrid.hasCross(gridLines.get("H_" + row + "_" + (col-1)))) {
                Line line = gridLines.get("H_" + row + "_" + (col-1));
                if (line != null) {
                    //System.out.println("Placement automatique d'une croix pour éviter un L: H_" + row + "_" + (col-1));
                    CreateCrossMove crossMove = new CreateCrossMove(line, "auto_cross_L", Color.BLACK, slitherGrid);
                    slitherGrid.addMove(crossMove);
                }
            }
        }
        
        // Vérifier le segment vertical à droite
        if (aTrait(gridLines, "V_" + row + "_" + (col+1))) {
            // Segment horizontal à droite du point (col+1)
            if (col < slitherGrid.getGridCols()-1 && !aTrait(gridLines, "H_" + row + "_" + (col+1)) && !slitherGrid.hasCross(gridLines.get("H_" + row + "_" + (col+1)))) {
                Line line = gridLines.get("H_" + row + "_" + (col+1));
                if (line != null) {
                    //System.out.println("Placement automatique d'une croix pour éviter un L: H_" + row + "_" + (col+1));
                    CreateCrossMove crossMove = new CreateCrossMove(line, "auto_cross_L", Color.BLACK, slitherGrid);
                    slitherGrid.addMove(crossMove);
                }
            }
        }
    }
    
    /**
     * Vérifie et place des croix pour éviter des "L" verticaux qui pourraient former des boucles prématurées.
     */
    private void verifierEtPlacerCroixPourLVertical(int row, int col, Map<String, Line> gridLines) {
        // Exemple de règle similaire pour les segments verticaux
        
        // Vérifier le segment horizontal en haut
        if (aTrait(gridLines, "H_" + row + "_" + col)) {
            // Segment vertical au-dessus du point (row-1)
            if (row > 0 && !aTrait(gridLines, "V_" + (row-1) + "_" + col) && !slitherGrid.hasCross(gridLines.get("V_" + (row-1) + "_" + col))) {
                Line line = gridLines.get("V_" + (row-1) + "_" + col);
                if (line != null) {
                    //System.out.println("Placement automatique d'une croix pour éviter un L: V_" + (row-1) + "_" + col);
                    CreateCrossMove crossMove = new CreateCrossMove(line, "auto_cross_L", Color.BLACK, slitherGrid);
                    slitherGrid.addMove(crossMove);
                }
            }
        }
        
        // Vérifier le segment horizontal en bas
        if (aTrait(gridLines, "H_" + (row+1) + "_" + col)) {
            // Segment vertical en-dessous du point (row+1)
            if (row < slitherGrid.getGridRows()-1 && !aTrait(gridLines, "V_" + (row+1) + "_" + col) && !slitherGrid.hasCross(gridLines.get("V_" + (row+1) + "_" + col))) {
                Line line = gridLines.get("V_" + (row+1) + "_" + col);
                if (line != null) {
                    //System.out.println("Placement automatique d'une croix pour éviter un L: V_" + (row+1) + "_" + col);
                    CreateCrossMove crossMove = new CreateCrossMove(line, "auto_cross_L", Color.BLACK, slitherGrid);
                    slitherGrid.addMove(crossMove);
                }
            }
        }
    }
    
    /**
     * Vérifie si un segment a un trait.
     */
    private boolean aTrait(Map<String, Line> gridLines, String lineId) {
        Line line = gridLines.get(lineId);
        if (line == null) return false;
        
        return line.getStroke() != Color.TRANSPARENT;
    }
}