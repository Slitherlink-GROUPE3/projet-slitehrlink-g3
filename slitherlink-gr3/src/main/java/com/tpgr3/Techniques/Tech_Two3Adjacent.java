package com.tpgr3.Techniques;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

// Classe représentant une technique spécifique pour résoudre une grille de Slitherlink
/**
 * La classe Tech_Two3Adjacent implémente une technique spécifique pour résoudre des grilles
 * de Slitherlink. Cette technique s'applique lorsque deux cases adjacentes de valeur 3
 * sont détectées dans la grille. L'objectif est de marquer certains slots (emplacements)
 * avec des bâtons pour respecter les règles du jeu.
 *
 * <p>Explication de la technique :
 * - Deux cases adjacentes de valeur 3 sont identifiées dans la grille.
 * - Les positions des slots à vérifier sont calculées :
 *   1. Le slot situé entre les deux cases de valeur 3.
 *   2. Le slot opposé à la première case.
 *   3. Le slot opposé à la deuxième case.
 * - Si tous ces slots ne sont pas déjà marqués par des bâtons, la technique est applicable.
 * - Une fois appliquée, la technique marque ces slots avec des bâtons pour avancer dans la résolution.
 *
 * <p>Cette technique repose sur le fait que les cases de valeur 3 doivent être entourées
 * exactement par trois bâtons, ce qui impose des contraintes spécifiques sur les slots
 * adjacents à ces cases.
 *
 * <p>Méthodes principales :
 * - estApplicable(Grille grille) : Vérifie si la technique peut être appliquée à la grille.
 * - appliquer(Grille grille) : Applique la technique en marquant les slots nécessaires.
 */
public class Tech_Two3Adjacent implements Techniques {

    // Positions des deux cases adjacentes de valeur 3
    private int[] positionA = null;
    private int[] positionB = null;

    @Override
    public boolean estApplicable(Grille grille) {
        // Parcourt chaque cellule de la grille
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cellule = grille.getCellule(x, y);

                // Vérifie si la cellule est une case avec la valeur 3
                if (cellule instanceof Case && ((Case) cellule).getValeur() == 3) {
                    // Directions possibles pour trouver une autre case de valeur 3
                    int[][] directions = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
                    for (int[] dir : directions) {
                        int xB = x + dir[0];
                        int yB = y + dir[1];

                        // Vérifie si la position voisine est valide
                        if (grille.estValide(xB, yB)) {
                            Cellule voisin = grille.getCellule(xB, yB);
                            // Vérifie si le voisin est une case avec la valeur 3
                            if (voisin instanceof Case && ((Case) voisin).getValeur() == 3) {
                                // Calcule les positions des slots à vérifier
                                int dx = (xB - x) / 2;
                                int dy = (yB - y) / 2;

                                int[][] slotsToCheck = {
                                    {x + dx, y + dy},  // Slot entre A et B
                                    {x - dx, y - dy},  // Slot opposé de A
                                    {xB + dx, yB + dy} // Slot opposé de B
                                };

                                boolean tousBatons = true;

                                // Vérifie si tous les slots sont marqués par des bâtons
                                for (int[] coord : slotsToCheck) {
                                    int sx = coord[0];
                                    int sy = coord[1];
                                    if (grille.estValide(sx, sy)) {
                                        Cellule c = grille.getCellule(sx, sy);
                                        if (c instanceof Slot) {
                                            if (!( ((Slot) c).getMarque() instanceof Baton )) {
                                                tousBatons = false;
                                                break;
                                            }
                                        } else {
                                            tousBatons = false;
                                            break;
                                        }
                                    } else {
                                        tousBatons = false;
                                        break;
                                    }
                                }

                                // Si tous les slots ne sont pas marqués, la technique est applicable
                                if (!tousBatons) {
                                    positionA = new int[]{x, y};
                                    positionB = new int[]{xB, yB};
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false; // Technique non applicable
    }

    public void appliquer(Grille grille) {
        // Vérifie si la technique est applicable avant de l'appliquer
        if (!estApplicable(grille)) return;

        // Récupère les positions des deux cases de valeur 3
        int xA = positionA[0];
        int yA = positionA[1];
        int xB = positionB[0];
        int yB = positionB[1];

        // Calcule les positions des slots à marquer
        int dx = (xB - xA) / 2;
        int dy = (yB - yA) / 2;

        int[][] batonCoords = {
            {xA + dx, yA + dy},  // entre A et B
            {xA - dx, yA - dy},  // opposé A
            {xB + dx, yB + dy}   // opposé B
        };

        // Marque les slots avec des bâtons
        for (int[] coord : batonCoords) {
            int sx = coord[0];
            int sy = coord[1];
            if (grille.estValide(sx, sy)) {
                Cellule c = grille.getCellule(sx, sy);
                if (c instanceof Slot) {
                    ((Slot) c).setMarque(new Baton());
                }
            }
        }
    }
}