package com.tpgr3.Techniques;

import com.tpgr3.Grille;
import com.tpgr3.Case;
import com.tpgr3.Cellule;

/*
 * Technique n°4 (cf cahie des charges)
 * 
 */

public class Techniques3diag implements Techniques {

    @Override
    public boolean estApplicable(Grille grille) {
        int hauteur = grille.getHauteur();
        int largeur = grille.getLargeur();
        Cellule[][] CellTab = grille.getMatrice();
        

        for (int x = 0; x < hauteur; x++) {
            for (int y = 0; y < largeur; y++) {
                   System.out.println("x : " + x + "y : " + y + "v :" + CellTab[x][y].getValeur());
            }
        }
        return false;
    }
}