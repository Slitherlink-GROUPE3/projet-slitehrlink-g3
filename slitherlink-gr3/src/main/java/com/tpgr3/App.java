package com.tpgr3;

import com.tpgr3.Techniques.Techniques;
import com.tpgr3.Marque.Baton;
import com.tpgr3.Techniques.Tech_0And3Adjacent;
import com.tpgr3.Techniques.Tech_0And3Diagonal;
import com.tpgr3.Techniques.Tech_0Rule;
import com.tpgr3.Techniques.Tech_CornerRule;
import com.tpgr3.Techniques.Tech_LoopReaching1;
import com.tpgr3.Techniques.Tech_LoopReaching3;
import com.tpgr3.Techniques.Tech_Two3Adjacent;
import com.tpgr3.Techniques.Tech_Two3Diagonal;
import com.tpgr3.Techniques.Technique0et3Diag;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // Initialisation des valeurs de la grille de test
        int[][] valeurs = {
            { -1, -1, -1, -1, -1},
            {-1, -1,  1, -1,-1},
            {-1,  -1, -1, -1,-1},
            {-1, -1, -1, -1,-1},
            { -1, -1, -1, -1, -1},
        };

        // Création de la grille
        Grille grille = new Grille(valeurs);
        //ajouter un baton sur la case 6,5
        grille.getCellule(6, 5).actionner();

        // Affichage de la grille
        //System.out.println("Affichage de la grille :");
        //grille.afficher();

        // Pour activer les tests
        boolean activerTestAntho = true;
        boolean activerTestTitouan = false;


        // Test Anthony
        if (activerTestAntho) {
            System.out.println("Test Technique Antho: ");

            // on ajoute une croix sur la case 6,5

            // technique a tester 
            Techniques t = new Tech_LoopReaching1();
            grille.afficher();
            


            System.out.println("Technique applicable ? " + t.estApplicable(grille));
            t.appliquer(grille);
            
            grille.afficher();


        }

        if (activerTestTitouan) {
            System.out.println("Test Titouan : ");
            Technique0et3Diag technique = new Technique0et3Diag();
            System.out.println("Technique 0 et 3 : ");
            System.out.println(technique.estApplicable(grille));
        }
    }
}