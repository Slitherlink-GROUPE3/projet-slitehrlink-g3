package com.tpgr3.Techniques;

import com.tpgr3.Grille;
import com.tpgr3.Case;
import java.util.List;

public class Tech_1Entre1Et3 implements Techniques {

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 1; y < grille.getHauteur(); y += 2) { // Parcours des Cases
            for (int x = 1; x < grille.getLargeur(); x += 2) {
                Case c = (Case) grille.getCellule(x, y);
                if (c.getValeur() == 1) {
                    List<Case> voisins = grille.getCasesAdjacentes(x, y);
                    boolean a1 = false, a3 = false;
                    for (Case v : voisins) {
                        if (v.getValeur() == 1) a1 = true;
                        if (v.getValeur() == 3) a3 = true;
                    }
                    if (a1 && a3) return true; // Technique applicable
                }
            }
        }
        return false;
    }
}