package com.tpgr3;

import com.tpgr3.Grille;

import java.util.List;
import java.util.concurrent.TimeUnit;
import com.tpgr3.Case;
import com.tpgr3.Slot;
import com.tpgr3.Cellule;
import com.tpgr3.Techniques.Techniques3diag; 

public class App extends MySleep{
    public static void main(String[] args) {
        
        
        /*Initialisation des valeurs de la grille */
        int[][] valeurs = {
            {3, 2, 3},
            {1, 3, 2},
            {2, 1, 0}
        };

        
        /*Creation de la grille */
        Grille grille = new Grille(valeurs);
        grille.afficher();

        /*Iteration de la grille , boucle 4 fois pour tester la methode actionner de toutes les celllules */
        for (int j = 3 ; j >= 0 ; j--) {
            for(int y = 0; y < grille.getHauteur(); y++) {
                for(int x = 0; x < grille.getLargeur(); x++) {                
                    grille.actionnerCelule(x, y);
                    grille.afficher();
                    //sleep(1);
                }
            }
        }

        // Afficher les voisins d'une cellule
        Cellule c = grille.getCellule(1, 1);
        System.out.println("Voisins de la cellule en (3, 3):");

        List<Case> voisins = grille.getVoisin(c); // Correction ici

        // Afficher les voisins
        for (Case voisin : voisins) {
            System.out.println("Voisin en (" + voisin.getX() + ", " + voisin.getY() + ")");
        }





        // Création d'une instance de votre technique
        Techniques3diag technique = new Techniques3diag();
        
        // Test de la technique
        boolean estApplicable = technique.estApplicable(grille);
        
        // Affichage du résultat
        System.out.println("La technique des cases 3 adjacentes est applicable: " + estApplicable);
    }
}