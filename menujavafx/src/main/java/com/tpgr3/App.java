package com.tpgr3;

import com.tpgr3.Techniques.*;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // Initialisation des valeurs de la grille de test
        int[][] valeurs = 
        {
            { -1, -1 , -1 , -1 , -1},
            { -1, -1 , -1 , -1 ,-1},
            { -1, -1 , -1 , -1 ,-1},
            { -1, -1 ,  0 ,  3 ,-1},
            { -1 , -1, 1 ,  -1 ,-1 }
        };

        // Création de la grille
        Grille grille = new Grille(valeurs);
        //ajouter un baton sur la case 6,5
       // grille.getCellule(6, 5).actionner();

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
            Techniques technique = new Tech_0And3Adjacent();
            //Techniques technique = new Tech_0And3Diagonal();
            //Techniques technique = new Tech_0Rule();
            //Techniques technique = new Tech_1Et0Coin();
            //Techniques technique = new Tech_1Et2Coin();
            //Techniques technique = new Tech_CornerRule();
            //Techniques technique = new Tech_3Et0Cote();
            
            //Techniques technique = new Tech_LoopReaching1();
            //Techniques technique = new Tech_LoopReaching3();
            //Techniques technique = new Tech_Two3Adjacent();
            //Techniques technique = new Tech_Two3Diagonal();
            
            // probleme avec cette technique
            //Techniques technique = new Tech_1Cote();

            // Listes des techniques à tester
            List<Techniques> techniques = new ArrayList<>();
            techniques.add(technique);
            techniques.add(technique);
            techniques.add(technique);
            techniques.add(technique);
            techniques.add(technique);


            grille.afficher();
            
            // Tester toutes les techniques
            for (Techniques t : techniques) {
                System.out.println("Technique " + t.getClass().getSimpleName() + " applicable ? " + t.estApplicable(grille));
                t.appliquer(grille);
                grille.afficher();
                try {
                    Thread.sleep(2000); // Pause for 2 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    System.err.println("Thread was interrupted: " + e.getMessage());
                }
            }
            
        }

        if (activerTestTitouan) {
            System.out.println("Test Titouan : ");
            Technique0et3Diag technique = new Technique0et3Diag();
            System.out.println("Technique 0 et 3 : ");
            System.out.println(technique.estApplicable(grille));
        }
    }
}