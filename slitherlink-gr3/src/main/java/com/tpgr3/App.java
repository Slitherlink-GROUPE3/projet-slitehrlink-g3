package com.tpgr3;

import java.util.List;

import com.tpgr3.Techniques.Technique0et3;
import com.tpgr3.Techniques.Technique0et3Diag;
import com.tpgr3.Techniques.Techniques3diag;

public class App {
    public static void main(String[] args) {
        
        /*Initialisation des valeurs de la grille */
        int[][] valeurs = {
            {3, 0, 3},
            {3, 3, 2},
            {0, 1, 0}
        };

        
        /*Creation de la grille */
        Grille grille = new Grille(valeurs);
        grille.afficher();


        
        Technique0et3Diag technique = new Technique0et3Diag();
        //Techniques3diag technique = new Techniques3diag();

        System.out.println("Technique 0 et 3 : ");
        System.out.println(technique.estApplicable(grille));
    
    }
}