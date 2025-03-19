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

public class App2 {
    public static void main(String[] args) {
        // Initialisation des valeurs de la grille de test
        int[][] valeurs = 
        {
            {3, 2, 1, -1, -1},
            {-1, -1, -1, 2, 2},
            {3, 1, -1, 2, 3},
            {3, -1, -1, 0, 3},
            {-1, -1, 3, -1, -1}
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

            // Listes des techniques à tester
            List<Techniques> techniques = new ArrayList<>();
            techniques.add(new Tech_0Rule());
            techniques.add(new Tech_0Rule());
            techniques.add(new Tech_0And3Adjacent());
            techniques.add(new Tech_0And3Adjacent());
            techniques.add(new Tech_0And3Adjacent());
            techniques.add(new Tech_0And3Diagonal());
            techniques.add(new Tech_0And3Diagonal());

            
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