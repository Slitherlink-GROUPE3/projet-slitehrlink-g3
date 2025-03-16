package com.tpgr3.Techniques;

import com.tpgr3.Grille;
import com.tpgr3.Case;
import java.util.List;

public class Tech_3Adjacents implements Techniques {
    
    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 1; y < grille.getHauteur(); y += 2) {
            for (int x = 1; x < grille.getLargeur(); x += 2) {
                Case c = (Case) grille.getCellule(x, y);
                if (c != null && c.getValeur() == 3) {
                    List<Case> voisins = grille.getCasesAdjacentes(x, y);
                    for (Case v : voisins) {
                        if (v.getValeur() == 3) {
                            System.out.printf("[Tech_3Adjacents] Cases (x=%d,y=%d) et (x=%d,y=%d) adjacentes.%n", x, y, v.getX(), v.getY());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}