package com.tpgr3;

import com.tpgr3.Techniques.Techniques;
import com.tpgr3.Techniques.Tech_0Coin;
import com.tpgr3.Techniques.Tech_3Coin;
import com.tpgr3.Techniques.Tech_1Coin;
import com.tpgr3.Techniques.Tech_2Coin;
import com.tpgr3.Techniques.Tech_3Adjacents;
import com.tpgr3.Techniques.Tech_1Entre0Et2;
import com.tpgr3.Techniques.Tech_2Entre0Et2;
import com.tpgr3.Techniques.Tech_1Entre1Et3;

import java.util.ArrayList;
import java.util.List;

import com.tpgr3.Techniques.Technique0et3;
import com.tpgr3.Techniques.Technique0et3Diag;

public class App {
    public static void main(String[] args) {
        // Initialisation des valeurs de la grille de test
        int[][] valeurs = {
            {3, 2, 3, 2},
            {1, 3, 2, 0},
            {2, 1, 0, 3},
            {3 ,3, 0, 1},

        };

        // Création de la grille
        Grille grille = new Grille(valeurs);

        // Affichage de la grille
        System.out.println("Affichage de la grille :");
        grille.afficher();

        // Liste des techniques disponibles
        List<Techniques> techniques = new ArrayList<>();
        techniques.add(new Tech_0Coin());
        techniques.add(new Tech_3Coin());
        techniques.add(new Tech_1Coin());
        techniques.add(new Tech_2Coin());
        techniques.add(new Tech_3Adjacents());
        techniques.add(new Tech_1Entre0Et2());
        techniques.add(new Tech_2Entre0Et2());
        techniques.add(new Tech_1Entre1Et3());

        // Détection des techniques applicables
        List<String> techniquesApplicables = new ArrayList<>();
        for (Techniques tech : techniques) {
            if (tech.estApplicable(grille)) {
                techniquesApplicables.add(tech.getClass().getSimpleName());
            }
        }

        // Affichage des résultats
        System.out.println("\nTechniques applicables :");
        if (techniquesApplicables.isEmpty()) {
            System.out.println("Aucune technique applicable.");
        } else {
            for (String nomTechnique : techniquesApplicables) {
                System.out.println("- " + nomTechnique);
            }
        }
    }
}