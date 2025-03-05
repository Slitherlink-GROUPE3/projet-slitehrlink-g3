package com.tpgr3;

import com.tpgr3.Grille;
import java.util.concurrent.TimeUnit;
import com.tpgr3.Case;
import com.tpgr3.Slot;
import com.tpgr3.Cellule;



public class App extends MySleep{
    public static void main(String[] args) {
        int[][] valeurs = {
            {3, 2, 3},
            {1, 3, 2},
            {2, 1, 3}
        };

        Grille grille = new Grille(valeurs);
        grille.afficher();


        // Test de la méthode actionner() sur toutes les cases
        for (int j = 3 ; j >= 0 ; j--) {
            for(int y = 0; y < grille.getHauteur(); y++) {
                for(int x = 0; x < grille.getLargeur(); x++) {                
                    grille.actionnerCelule(x, y);
                    grille.afficher();
                    sleep(1);
                }
            }
        }
        
        
    }


}
    