package com.tpgr3.backend.Techniques;
import com.tpgr3.backend.Marque.*;

import java.util.ArrayList;
import java.util.List;

import com.tpgr3.backend.Case;
import com.tpgr3.backend.Cellule;
import com.tpgr3.backend.Grille;
import com.tpgr3.backend.Slot;


public class Tech_Two3Diagonal implements Techniques {

    private int[] positionA = null; // Position du premier 3
    private int[] positionB = null; // Position du deuxième 3

    /**
     * Vérifie si un slot aux coordonnées données est un bâton.
     */
    private boolean estBaton(Grille grille, int x, int y) {
        if (grille.estValide(x, y)) {
            Cellule c = grille.getCellule(x, y);
            return c instanceof Slot && ((Slot) c).getMarque() instanceof Baton;
        }
        return false;
    }

    /**
     * Place une marque (Bâton ou Croix) sur une liste de slots donnés.
     */
    private void placerMarque(Grille grille, int[][] coords, Marque marque) {
        for (int[] coord : coords) {
            int x = coord[0];
            int y = coord[1];
            if (grille.estValide(x, y)) {
                Cellule c = grille.getCellule(x, y);
                if (c instanceof Slot) {
                    ((Slot) c).setMarque(marque);
                }
            }
        }
    }

    /**
     * Détection : Deux 3 en diagonale, avec au moins un slot cible non bâton.
     */
    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cellule = grille.getCellule(x, y);

                if (cellule instanceof Case && ((Case) cellule).getValeur() == 3) {
                    int[][] diagonales = {{2, 2}, {-2, 2}, {-2, -2}, {2, -2}};
                    for (int[] dir : diagonales) {
                        int xB = x + dir[0];
                        int yB = y + dir[1];

                        if (grille.estValide(xB, yB)) {
                            Cellule voisin = grille.getCellule(xB, yB);
                            if (voisin instanceof Case && ((Case) voisin).getValeur() == 3) {

                                int dx = dir[0] / 2;
                                int dy = dir[1] / 2;

                                // Vérifie les deux slots clés entre les 3
                                boolean baton1 = estBaton(grille, x + dx, y);
                                boolean baton2 = estBaton(grille, x, y + dy);

                                if (!(baton1 && baton2)) {
                                    // Stocker les positions
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
        return false;
    }

    /**
     * Applique la technique : pose bâtons et croix si la configuration est valide.
     */
    public void appliquer(Grille grille) {
        if (!estApplicable(grille)) return;
    
        int xA = positionA[0];
        int yA = positionA[1];
        int xB = positionB[0];
        int yB = positionB[1];
    
        int dx = (xB - xA) / 2;
        int dy = (yB - yA) / 2;
    
        int[][] batonCoords = {
            {xA + dx, yA},
            {xA, yA + dy},
            {xB - dx, yB},
            {xB, yB - dy}
        };
        placerMarque(grille, batonCoords, new Baton());
    
        int[][] croixCoords = {
            {xA - dx, yA},
            {xA, yA - dy},
            {xB + dx, yB},
            {xB, yB + dy}
        };
        placerMarque(grille, croixCoords, new Croix());
    }

    @Override
    public List<int[]> getSlotsToMark(Grille grille) {
        // Sauvegarder l'état
        int[] savePosA = positionA;
        int[] savePosB = positionB;
        
        // Vérifier applicabilité
        if (!estApplicable(grille)) {
            // Restaurer et retourner vide
            positionA = savePosA;
            positionB = savePosB;
            return new ArrayList<>();
        }
        
        // Générer les coordonnées
        List<int[]> resultat = new ArrayList<>();
        
        int xA = positionA[0];
        int yA = positionA[1];
        int xB = positionB[0];
        int yB = positionB[1];

        int dx = (xB - xA) / 2;
        int dy = (yB - yA) / 2;

        int[][] batonCoords = {
            {xA + dx, yA},
            {xA, yA + dy},
            {xB - dx, yB},
            {xB, yB - dy}
        };
        
        int[][] croixCoords = {
            {xA - dx, yA},
            {xA, yA - dy},
            {xB + dx, yB},
            {xB, yB + dy}
        };

        // Ajouter les coordonnées des bâtons
        for (int[] coord : batonCoords) {
            int sx = coord[0];
            int sy = coord[1];
            if (grille.estValide(sx, sy) && grille.getCellule(sx, sy) instanceof Slot) {
                resultat.add(new int[]{sx, sy});
            }
        }

        // Ajouter les coordonnées des croix
        for (int[] coord : croixCoords) {
            int sx = coord[0];
            int sy = coord[1];
            if (grille.estValide(sx, sy) && grille.getCellule(sx, sy) instanceof Slot) {
                resultat.add(new int[]{sx, sy});
            }
        }
        
        // Restaurer l'état
        positionA = savePosA;
        positionB = savePosB;
        
        return resultat;
    }
    
}
