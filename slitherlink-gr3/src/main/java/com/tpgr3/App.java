package com.tpgr3;

import com.tpgr3.Grille;

import java.util.concurrent.TimeUnit;

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
        // Test de la méthode actionner() sur une case
        // On va poser un baton 

        for(int i = 0; i < 5; i++){

            // Test de la méthode actionner() sur une case
            // On va poser un baton 
            System.out.println("Actionner la case (0, 5)");
            // sleep 
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    
            grille.actionnerCelule(1, 1);
            grille.afficher();
        }
    }
}
