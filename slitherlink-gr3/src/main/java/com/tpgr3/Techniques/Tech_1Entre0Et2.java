package com.tpgr3.Techniques;

import com.tpgr3.Grille;
import com.tpgr3.Case;
import java.util.List;

public class Tech_1Entre0Et2 implements Techniques {

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 1; y < grille.getHauteur(); y += 2) {
            for (int x = 1; x < grille.getLargeur(); x += 2) {
                Case c = (Case) grille.getCellule(x, y);
                if (c != null && c.getValeur() == 1) {
                    List<Case> voisins = grille.getCasesAdjacentes(x, y);
                    if (aAuMoinsUn0EtUn2(voisins)) {
                        System.out.printf("[Tech_1Entre0Et2] Case (x=%d, y=%d) a des voisins 0 et 2.%n", x, y);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean aAuMoinsUn0EtUn2(List<Case> voisins) {
        boolean a0 = false, a2 = false;
        for (Case v : voisins) {
            if (v.getValeur() == 0) a0 = true;
            if (v.getValeur() == 2) a2 = true;
        }
        return a0 && a2;
    }
}