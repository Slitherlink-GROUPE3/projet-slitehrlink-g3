package com.tpgr3.Techniques;

import com.tpgr3.Grille;
import com.tpgr3.Case;

public class Tech_3Coin implements Techniques {

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 1; y < grille.hauteur; y += 2) {
            for (int x = 1; x < grille.largeur; x += 2) {
                Case c = (Case) grille.getCellule(x, y);
                if (c != null && c.getValeur() == 3 && estDansUnCoin(x, y, grille)) {
                    System.out.printf("[OK] Tech_3Coin: Case (x=%d,y=%d) dans un coin.%n", x, y);
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