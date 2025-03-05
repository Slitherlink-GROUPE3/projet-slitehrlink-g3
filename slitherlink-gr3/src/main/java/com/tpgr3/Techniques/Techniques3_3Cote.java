package com.tpgr3.Techniques;

import com.tpgr3.Grille;
import com.tpgr3.Case;

public class Techniques3_3Cote {

    /*
     * Rappel fontionement de la matrice 
     */

     public boolean estApplicable(Grille grille) {
        int hauteur = grille.getHauteur();
        int largeur = grille.getLargeur();
        
        // Parcourir uniquement les positions où se trouvent les chiffres (x et y impairs)
        for (int y = 1; y < hauteur; y += 2) {
            for (int x = 1; x < largeur; x += 2) {
                // Vérifier si la cellule actuelle est une Case avec valeur 3
                if (grille.getCellule(x, y) instanceof Case) {
                    Case celluleActuelle = (Case) grille.getCellule(x, y);
                    
                    if (celluleActuelle.getValeur() == 3) {
                        // Vérifier la case à droite (x+2)
                        if (x + 2 < largeur && grille.getCellule(x + 2, y) instanceof Case) {
                            Case celluleDroite = (Case) grille.getCellule(x + 2, y);
                            if (celluleDroite.getValeur() == 3) {
                                return true;
                            }
                        }
                        
                        // Vérifier la case en bas (y+2)
                        if (y + 2 < hauteur && grille.getCellule(x, y + 2) instanceof Case) {
                            Case celluleBas = (Case) grille.getCellule(x, y + 2);
                            if (celluleBas.getValeur() == 3) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}