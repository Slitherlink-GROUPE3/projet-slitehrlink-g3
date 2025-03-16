package com.tpgr3.Techniques;

import com.tpgr3.Grille;

import java.util.List;

import com.tpgr3.Case;
import com.tpgr3.Slot;

public class Tech_1T implements Techniques {

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 1; y < grille.getHauteur(); y += 2) {
            for (int x = 1; x < grille.getLargeur(); x += 2) {
                Case c = (Case) grille.getCellule(x, y);
                if (c != null && c.getValeur() == 1 && estConfigurationT(grille, x, y)) {
                    System.out.printf("[Tech_1T] Case (x=%d,y=%d) en configuration T.%n", x, y);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean estConfigurationT(Grille grille, int x, int y) {
        List<Slot> slots = grille.getSlotsAdjacentsCase(x, y);
        return slots.size() == 3; // Un "T" a 3 slots adjacents
    }
}