package com.tpgr3.Techniques;

import com.tpgr3.Grille;
import com.tpgr3.Case;
import com.tpgr3.Slot;
import java.util.List;

public class Tech_0Coin implements Techniques {

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 1; y < grille.getHauteur(); y += 2) { // Parcours des Cases
            for (int x = 1; x < grille.getLargeur(); x += 2) {
                Case c = (Case) grille.getCellule(x, y);
                if (c.getValeur() == 0 && estDansUnCoin(x, y, grille)) {
                    List<Slot> slots = grille.getSlotsAdjacentsCase(x, y);
                    if (slots.size() == 2) return true; // Technique applicable
                }
            }
        }
        return false;
    }

    private boolean estDansUnCoin(int x, int y, Grille grille) {
        int maxX = grille.getLargeur() - 1;
        int maxY = grille.getHauteur() - 1;
        return (x == 1 && y == 1) || (x == maxX - 1 && y == 1)
            || (x == 1 && y == maxY - 1) || (x == maxX - 1 && y == maxY - 1);
    }
}