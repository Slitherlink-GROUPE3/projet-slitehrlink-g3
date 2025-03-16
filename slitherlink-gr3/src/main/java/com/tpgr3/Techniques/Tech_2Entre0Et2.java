package com.tpgr3.Techniques;

import com.tpgr3.Grille;
import com.tpgr3.Case;
import java.util.List;

public class Tech_2Entre0Et2 implements Techniques {

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 1; y < grille.getHauteur(); y += 2) { // Parcours des Cases
            for (int x = 1; x < grille.getLargeur(); x += 2) {
                Case c = (Case) grille.getCellule(x, y);
                if (c.getValeur() == 2) {
                    List<Case> voisins = grille.getCasesAdjacentes(x, y);
                    boolean a0 = false, a2 = false;
                    for (Case v : voisins) {
                        if (v.getValeur() == 0) a0 = true;
                        if (v.getValeur() == 2) a2 = true;
                    }
                    if (a0 && a2) return true; // Technique applicable
                }
            }
        }
        return false;
    }
}