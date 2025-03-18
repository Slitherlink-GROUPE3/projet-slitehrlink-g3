package com.tpgr3.Techniques;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

import java.util.List;

public class Tech_0Rule implements Techniques {

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
            