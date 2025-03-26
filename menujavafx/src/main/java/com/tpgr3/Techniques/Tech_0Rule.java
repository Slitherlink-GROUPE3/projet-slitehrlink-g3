<<<<<<< HEAD
package com.tpgr3.Techniques;

=======
// Common imports for Technique classes
package com.tpgr3.Techniques;

import java.util.Map;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

>>>>>>> 12cd25160dfc23eed9267d004e81170d606aac35
import com.tpgr3.*;
import com.tpgr3.Marque.*;

import java.util.List;

public class Tech_0Rule implements Techniques {

<<<<<<< HEAD
    @Override
    public boolean estApplicable(Grille grille) {
        // Parcours de la grille
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cellule = grille.getCellule(x, y);

                // On cible uniquement les cases avec valeur 0
                if (cellule instanceof Case) {
                    Case c = (Case) cellule;
                    if (c.getValeur() == 0) {
                        List<Slot> slotsAdj = grille.getSlotsAdjacentsCase(x, y);

                        boolean slotAPlacer = false;

                        for (Slot slot : slotsAdj) {
                            Marque marque = slot.getMarque();
                            if (marque instanceof Baton) {
                                // Si un slot est un segment => la technique échoue
                                return false;
                            } else if (marque instanceof Neutre) {
                                slotAPlacer = true;
                            }
                        }

                        if (slotAPlacer) {
                            return true; // La technique peut s’appliquer (croix à placer)
                        }
                    }
                }
            }
        }
        // Aucune case 0 où la technique peut s’appliquer
        return false;
    }

    // Applique la technique : place des croix autour des cases 0
    @Override
    public void appliquer(Grille grille) {

        if (estApplicable(grille)) { // Vérifier si la technique est applicable
            for (int y = 0; y < grille.getHauteur(); y++) {
                for (int x = 0; x < grille.getLargeur(); x++) {
                    Cellule cellule = grille.getCellule(x, y);

                    // On cible uniquement les cases avec valeur 0
                    if (cellule instanceof Case) {
                        Case c = (Case) cellule;
                        if (c.getValeur() == 0) {
                            List<Slot> slotsAdj = grille.getSlotsAdjacentsCase(x, y);

                            for (Slot slot : slotsAdj) {
                                slot.setMarque(new Croix());
                            }
                        }
                    }
                }
            }
        }
        else {
            System.out.println("La technique n'est pas applicable");
        }
    }
}
            
=======
    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;

    /**
     * Constructeur avec les informations nécessaires pour l'analyse.
     * 
     * @param gridNumbers Tableau 2D représentant les chiffres de la grille
     * @param gridLines Map des lignes de la grille
     * @param slitherlinkGrid Pane contenant la grille de Slitherlink
     */
    public Tech_0Rule(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
    }

    @Override
    public boolean estApplicable(Grille grille) {
        for (int i = 0; i < gridNumbers.length; i++) {
            for (int j = 0; j < gridNumbers[i].length; j++) {
                if (gridNumbers[i][j] == 0) {
                    // Vérifie si tous les segments autour du 0 ont des croix
                    boolean allSegmentsHaveCrosses = true;
                    boolean atLeastOneSegmentNeutral = false;
                    
                    // Vérifier les 4 segments adjacents
                    String[] adjacentSegments = {
                        "H_" + i + "_" + j,       // Haut
                        "H_" + (i+1) + "_" + j,   // Bas
                        "V_" + i + "_" + j,       // Gauche
                        "V_" + i + "_" + (j+1)    // Droite
                    };
                    
                    for (String segmentKey : adjacentSegments) {
                        Line segment = gridLines.get(segmentKey);
                        if (segment != null) {
                            if (segment.getStroke() != Color.TRANSPARENT) {
                                // Une ligne existe - la règle du 0 est violée
                                allSegmentsHaveCrosses = false;
                                break;
                            }
                            
                            // Vérifie si le segment n'a pas de croix
                            boolean hasCross = false;
                            for (javafx.scene.Node node : slitherlinkGrid.getChildren()) {
                                if (node instanceof Line && node.getUserData() == segment) {
                                    hasCross = true;
                                    break;
                                }
                            }
                            
                            if (!hasCross) {
                                atLeastOneSegmentNeutral = true;
                            }
                        }
                    }
                    
                    // Si tous les segments sont neutres ou ont des croix, et au moins un est neutre, la règle est applicable
                    if (allSegmentsHaveCrosses && atLeastOneSegmentNeutral) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    
    @Override
    public void appliquer(Grille grille) {
        
    }
}
>>>>>>> 12cd25160dfc23eed9267d004e81170d606aac35
