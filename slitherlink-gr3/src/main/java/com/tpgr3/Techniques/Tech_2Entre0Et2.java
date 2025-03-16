package com.tpgr3.Techniques;

import com.tpgr3.Grille;
import com.tpgr3.Case;
import java.util.List;

public class Tech_2Entre0Et2 implements Techniques {

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 1; y < grille.hauteur; y += 2) {
            for (int x = 1; x < grille.largeur; x += 2) {
                Case c = (Case) grille.getCellule(x, y);
                if (c != null && c.getValeur() == 2) {
                    List<Case> voisins = grille.getCasesAdjacentes(x, y);
                    if (contientValeurs(voisins, 0, 2)) {
                        System.out.printf("[OK] Tech_2Entre0Et2: Case (x=%d,y=%d) voisins=%s%n", 
                            x, y, formatVoisins(voisins));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean contientValeurs(List<Case> voisins, int... valeurs) {
        boolean[] trouve = new boolean[valeurs.length];
        for (Case v : voisins) {
            for (int i = 0; i < valeurs.length; i++) {
                if (v.getValeur() == valeurs[i]) trouve[i] = true;
            }
        }
        for (boolean t : trouve) if (!t) return false;
        return true;
    }

    private String formatVoisins(List<Case> voisins) {
        StringBuilder sb = new StringBuilder();
        for (Case v : voisins) sb.append(v.getValeur()).append(" ");
        return sb.toString().trim();
    }
}