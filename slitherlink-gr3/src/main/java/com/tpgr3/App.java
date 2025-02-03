package com.tpgr3;

import com.tpgr3.Grille;
import com.tpgr3.Case;
import com.tpgr3.Slot;
import com.tpgr3.Cellule;



public class App 
{
    public static void main(String[] args) {
        int[][] valeurs = {
            {3, 2, 3},
            {1, 3, 2},
            {2, 1, 3}
        };

        Grille grille = new Grille(7, 7, valeurs);
        grille.afficher();
    }
}
