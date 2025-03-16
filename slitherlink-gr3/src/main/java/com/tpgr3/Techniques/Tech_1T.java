package com.tpgr3.Techniques;

import com.tpgr3.Grille;
import com.tpgr3.Case;
import com.tpgr3.Slot;
import java.util.List;

public class Tech_1T implements Techniques {

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 1; y < grille.hauteur; y += 2) {
            for (int x = 1; x < grille.largeur; x += 2) {
                Case c = (Case) grille.getCellule(x, y);
                if (c != null && c.getValeur() == 1 && estConfigurationT(grille, x, y)) {
                    System.out.printf("[OK] Tech_1T: Case (x=%d,y=%d)%n", x, y);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean estConfigurationT(Grille grille, int x, int y) {
        List<Slot> slots = grille.getSlotsAdjacentsCase(x, y);
        return slots.stream().filter(s -> s.getValeur() == Slot.BATON).count() == 3;
    }
}