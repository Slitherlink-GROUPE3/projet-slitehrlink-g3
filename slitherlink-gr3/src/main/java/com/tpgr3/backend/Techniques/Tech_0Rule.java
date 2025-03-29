package com.tpgr3.backend.Techniques;
import com.tpgr3.backend.Marque.*;
import com.tpgr3.backend.Case;
import com.tpgr3.backend.Cellule;
import com.tpgr3.backend.Grille;
import com.tpgr3.backend.Slot;

import java.util.ArrayList;
import java.util.List;

public class Tech_0Rule implements Techniques {

    @Override
    public boolean estApplicable(Grille grille) {
        // Code existant inchangé...
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cellule = grille.getCellule(x, y);

                if (cellule instanceof Case) {
                    Case c = (Case) cellule;
                    if (c.getValeur() == 0) {
                        List<Slot> slotsAdjacents = grille.getSlotsAdjacentsCase(x, y);
                        
                        int nbCroix = 0;
                        for (Slot s : slotsAdjacents) {
                            if (s.getMarque() instanceof Croix) {
                                nbCroix++;
                            }
                        }
                        
                        if (nbCroix < slotsAdjacents.size()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void appliquer(Grille grille) {
        // Vérifier d'abord si la technique est applicable
        if (!estApplicable(grille)) {
            return; // Rien à faire si la technique n'est pas applicable
        }
        
        // Utiliser getSlotsToMark pour obtenir les slots à marquer
        List<int[]> slotsToMark = getSlotsToMark(grille);
        
        // Marquer les slots identifiés
        for (int[] coords : slotsToMark) {
            int x = coords[0];
            int y = coords[1];
            
            Cellule cellule = grille.getCellule(x, y);
            if (cellule instanceof Slot s) {
                Marque ancienneMarque = s.getMarque();
                s.setMarque(new Croix());
                grille.enregistrerCoup(x, y, ancienneMarque, s.getMarque());
            }
        }
    }
    
    @Override
    public List<int[]> getSlotsToMark(Grille grille) {
        List<int[]> resultat = new ArrayList<>();
        
        // Si la technique n'est pas applicable, retourner une liste vide
        if (!estApplicable(grille)) {
            return resultat;
        }
        
        // Parcours de la grille pour identifier les slots à marquer
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cellule = grille.getCellule(x, y);
                
                if (cellule instanceof Case) {
                    Case c = (Case) cellule;
                    if (c.getValeur() == 0) {
                        // Récupérer les slots adjacents
                        List<Slot> slotsAdjacents = grille.getSlotsAdjacentsCase(x, y);
                        
                        // Ajouter à la liste les slots qui ne sont pas déjà des croix
                        for (Slot s : slotsAdjacents) {
                            if (!(s.getMarque() instanceof Croix)) {
                                resultat.add(new int[]{s.getX(), s.getY()});
                            }
                        }
                    }
                }
            }
        }
        
        return resultat;
    }
}