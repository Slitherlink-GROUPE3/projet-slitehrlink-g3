package com.tpgr3.Techniques;

import com.tpgr3.Grille;
import com.tpgr3.Case;
import com.tpgr3.Cellule;
import com.tpgr3.Voisins;

/*
 * Technique n°4 (cf cahie des charges)
 * 
 */

public class Techniques3diag implements Techniques {

    @Override
    public boolean estApplicable(Grille grille) {
        
        
        grille.afficher();
        //grille.afficherValeursReeles();

        // Tester la détection des voisins à différents endroits
        /* 
        System.out.println("Test des voisins au centre :");
        Voisins vCentre = new Voisins(grille, 2, 2);
        vCentre.testDetection();
        
        System.out.println("\nTest des voisins en haut à gauche :");
        Voisins vCoin = new Voisins(grille, 1, 1);
        vCoin.testDetection();
        
        System.out.println("\nTest des voisins en bas à droite :");
        Voisins vBasDroite = new Voisins(grille, 3, 3);
        vBasDroite.testDetection();
        */
        return false;
    }
}