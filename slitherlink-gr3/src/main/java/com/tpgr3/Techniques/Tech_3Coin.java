package com.tpgr3.Techniques;

import com.tpgr3.Grille;
import com.tpgr3.Cellule;
import com.tpgr3.Case;

public class Tech_3Coin implements Techniques {

    @Override
    public boolean estApplicable(Grille grille) {
        int largeur = grille.getLargeur();
        int hauteur = grille.getHauteur();

        // Parcourir toutes les cellules de la grille
        for (int y = 1; y < hauteur; y += 2) { // Seulement les lignes impaires (cases)
            for (int x = 1; x < largeur; x += 2) { // Seulement les colonnes impaires (cases)
                Cellule cellule = grille.getCellule(x, y);

                // Vérifier si c'est une Case avec valeur 3
                if (cellule instanceof Case && cellule.getValeur() == 3) {
                    // Vérifier si la case est dans un coin
                    if (estDansUnCoin(x, y, grille)) {
                        return true; // Technique applicable
                    }
                }
            }
        }
        return false;
    }

    /**
     * Vérifie si une case est dans un coin de la grille.
     */
    private boolean estDansUnCoin(int x, int y, Grille grille) {
        int maxX = grille.getLargeur() - 2; // Dernière case impaire
        int maxY = grille.getHauteur() - 2; // Dernière case impaire

        // Coins : (1,1), (maxX,1), (1,maxY), (maxX,maxY)
        return (x == 1 && y == 1) || (x == maxX && y == 1) 
            || (x == 1 && y == maxY) || (x == maxX && y == maxY);
    }
}