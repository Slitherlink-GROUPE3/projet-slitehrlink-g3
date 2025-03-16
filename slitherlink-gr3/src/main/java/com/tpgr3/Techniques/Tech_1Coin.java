package com.tpgr3.Techniques;

import com.tpgr3.Grille;
import com.tpgr3.Case;
import com.tpgr3.Slot;
import java.util.List;

public class Tech_1Coin implements Techniques {

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 1; y < grille.getHauteur(); y += 2) {
            for (int x = 1; x < grille.getLargeur(); x += 2) {
                Case c = (Case) grille.getCellule(x, y);
                if (c != null && c.getValeur() == 1 && estDansUnCoin(x, y, grille)) {
                    List<Slot> slots = grille.getSlotsAdjacentsCase(x, y);
                    System.out.printf("[Tech_1Coin] Case (x=%d, y=%d) dans un coin. Slots adjacents: %d%n", x, y, slots.size());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean estDansUnCoin(int x, int y, Grille grille) {
        int maxX = grille.getLargeur() - 2; // Dernière case impaire
        int maxY = grille.getHauteur() - 2;
        return (x == 1 && y == 1) || (x == maxX && y == 1)
            || (x == 1 && y == maxY) || (x == maxX && y == maxY);
    }
}