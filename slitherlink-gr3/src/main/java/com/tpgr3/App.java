package com.tpgr3;

import com.tpgr3.Grille;
import java.util.concurrent.TimeUnit;
import com.tpgr3.Case;
import com.tpgr3.Slot;
import com.tpgr3.Cellule;
import com.tpgr3.Techniques.Techniques3_3Cote; 

public class App extends MySleep{
    public static void main(String[] args) {
        int[][] valeurs = {
            {3, 2, 3},
            {1, 3, 2},
            {2, 1, 0}
        };

        

        Grille grille = new Grille(valeurs);
        grille.afficher();

        for (int j = 3 ; j >= 0 ; j--) {
            for(int y = 0; y < grille.getHauteur(); y++) {
                for(int x = 0; x < grille.getLargeur(); x++) {                
                    grille.actionnerCelule(x, y);
                    grille.afficher();
                    //sleep(1);
                }
            }
        }

        // Création d'une instance de votre technique
        Techniques3_3Cote technique = new Techniques3_3Cote();
        
        // Test de la technique
        boolean estApplicable = technique.estApplicable(grille);
        
        // Affichage du résultat
        System.out.println("La technique des cases 3 adjacentes est applicable: " + estApplicable);
    }
}