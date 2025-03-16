package com.tpgr3.Techniques;

import com.tpgr3.Grille;
import com.tpgr3.Case;
import com.tpgr3.Slot;
import java.util.List;

public class Tech_0Coin implements Techniques {

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 1; y < grille.hauteur; y += 2) { // Parcours des cases
            for (int x = 1; x < grille.largeur; x += 2) {
                Case c = (Case) grille.getCellule(x, y);
                if (c != null && c.getValeur() == 0 && estDansUnCoin(x, y, grille)) {
                    List<Slot> slots = grille.getSlotsAdjacentsCase(x, y);
                    System.out.printf("[OK] Tech_0Coin: Case (x=%d,y=%d) dans un coin. Slots adjacents: %d%n", x, y, slots.size());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean estDansUnCoin(int x, int y, Grille grille) {
        int maxX = grille.largeur - 2; // Dernière case impaire
        int maxY = grille.hauteur - 2;
        return (x == 1 && y == 1) || (x == maxX && y == 1)
            || (x == 1 && y == maxY) || (x == maxX && y == maxY);
    }
}