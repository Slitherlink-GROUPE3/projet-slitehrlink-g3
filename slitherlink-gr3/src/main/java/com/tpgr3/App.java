package com.tpgr3;

import com.google.gson.Gson;
import com.tpgr3.backend.Techniques.*;
import com.tpgr3.backend.adapter.GrilleAdapter;
import com.tpgr3.backend.Coup;
import com.tpgr3.backend.Grille;
import com.tpgr3.backend.ValidateurDeChemin;
import com.tpgr3.backend.Marque.Croix;
import com.tpgr3.backend.dto.*;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
            { -1, -1 ,  0 ,  1 ,-1},
            { -1 , -1,  0,  -1 ,-1 }
        };

        // Pour activer les tests
        boolean activerTestAntho =true;
        boolean test2 = false;

        if (activerTestAntho) {

        // Création de la grille
        Grille grille = new Grille(valeurs);

        // On ajoute une croix sur la case 9 , 5
        grille.getCellule(7, 6).actionner();

            System.out.println("Test Technique Antho: ");

            // on ajoute une croix sur la case 6,5

            // technique a tester 
            //Techniques technique = new Tech_0And3Adjacent();
            //Techniques technique = new Tech_0And3Diagonal();
            //Techniques technique = new Tech_0Rule();
            //Techniques technique = new Tech_1Et0Coin();
            //Techniques technique = new Tech_1Et2Coin();
            //Techniques technique = new Tech_CornerRule();
            //Techniques technique = new Tech_3Et0Cote();
            
        Techniques technique = new Tech_LoopReaching1();
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


        /*========================================= */
        // Test 2
        /*========================================= */
        if (test2) {
            
            try {
                // 1. Chargement du fichier JSON unifié via classloader
                InputStream input = App.class.getClassLoader().getResourceAsStream("grille1.json");
                if (input == null) throw new RuntimeException("Fichier grille1.json non trouvé dans resources/");
                InputStreamReader reader = new InputStreamReader(input);
                
                Gson gson = new Gson();
                NiveauDTO niveau = gson.fromJson(reader, NiveauDTO.class);
                reader.close();
                
                // 2. Charger la grille avec les coups déjà joués (sauvegarde partielle)
                Grille grille = GrilleAdapter.chargerDepuisSauvegarde(niveau);
                ValidateurDeChemin validateur = new ValidateurDeChemin(grille);
                
                // 3. Afficher la grille actuelle
                System.out.println("🧩 Grille actuelle :");
                grille.afficher();
                
                // 4. Afficher les coups joués
                System.out.println("⏱️  Historique (taille = " + grille.getHistoriqueSize() + ")");
                for (int i = 0; i <= grille.getHistoriqueIndex(); i++) {
                    Coup coup = grille.getCoup(i);
                    System.out.printf("  • (%d,%d) : %s -> %s%n",
                    coup.getX(), coup.getY(),
                    coup.getAncienne().getClass().getSimpleName(),
                    coup.getNouvelle().getClass().getSimpleName());
                }
                
                // 5. Comparer avec la solution
                List<int[]> solution = GrilleAdapter.getSolutionCoordonnees(niveau);
                System.out.println("🔎 Vérification chemin partiel...");
                int[] erreur = validateur.checkPartielStrict(solution);
                
                if (erreur != null) {
                    System.out.println("❌ Erreur détectée en (" + erreur[0] + "," + erreur[1] + ")");
                    validateur.supprimerDepuis(erreur[0], erreur[1]);
                    System.out.println("🧼 Nettoyage effectué.");
                } else {
                    System.out.println("✅ Aucun problème détecté.");
                }
                
                // 6. Affichage final
                System.out.println("\n📦 Grille après correction :");
                grille.afficher();
                
            } catch (Exception e) {
                System.err.println("Erreur dans App : " + e.getMessage());
            }
            
        }
    }
}
