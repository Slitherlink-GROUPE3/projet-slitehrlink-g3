package com.tpgr3.Techniques;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

import java.util.ArrayList;
import java.util.List;

public class Tech_CornerRule implements Techniques {

    private int[] positionCase = null;
    private List<int[]> coordsSlotsCible = new ArrayList<>();
    private Marque marqueCible = null;

    private String getTypeDeCoin(int x, int y, Grille grille) {
        int[] dim = grille.getDimensionsLogiques();
        if (x == 1 && y == 1) return "hautGauche";
        if (x == dim[0] * 2 - 1 && y == 1) return "hautDroit";
        if (x == 1 && y == dim[1] * 2 - 1) return "basGauche";
        if (x == dim[0] * 2 - 1 && y == dim[1] * 2 - 1) return "basDroit";
        return "aucun";
    }

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cellule = grille.getCellule(x, y);
                if (cellule instanceof Case) {
                    Case c = (Case) cellule;
                    int val = c.getValeur();

                    String coin = getTypeDeCoin(x, y, grille);
                    if (!coin.equals("aucun") && val >= 0 && val <= 3) {
                        List<int[]> coordsCibleTemp = genererCoords(x, y, val, coin);
                        Marque marque = (val == 0 || val == 1) ? new Croix() : new Baton();

                        boolean auMoinsUnIncorrect = false;
                        for (int[] coord : coordsCibleTemp) {
                            int sx = coord[0], sy = coord[1];
                            if (grille.estValide(sx, sy)) {
                                Cellule cSlot = grille.getCellule(sx, sy);
                                if (cSlot instanceof Slot) {
                                    Marque m = ((Slot) cSlot).getMarque();
                                    if (!m.getClass().equals(marque.getClass())) {
                                        auMoinsUnIncorrect = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (auMoinsUnIncorrect) {
                            positionCase = new int[]{x, y};
                            coordsSlotsCible = coordsCibleTemp;
                            marqueCible = marque;
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
        if (!estApplicable(grille)) return;

        for (int[] coord : coordsSlotsCible) {
            int x = coord[0], y = coord[1];
            if (grille.estValide(x, y)) {
                Cellule c = grille.getCellule(x, y);
                if (c instanceof Slot) {
                    ((Slot) c).setMarque(marqueCible);
                }
            }
        }

        positionCase = null;
        coordsSlotsCible.clear();
        marqueCible = null;
    }

    private List<int[]> genererCoords(int x, int y, int val, String coin) {
        List<int[]> coords = new ArrayList<>();
        switch (val) {
            case 0:
                if (coin.equals("hautGauche")) {
                    coords.add(new int[]{x - 1, y});       // Gauche
                    coords.add(new int[]{x, y - 1});       // Haut
                    coords.add(new int[]{x + 1, y});       // Droite
                    coords.add(new int[]{x, y + 1});       // Bas
                    coords.add(new int[]{x -1 , y + 2});   // Croix éloignée gauche-bas
                    coords.add(new int[]{x + 2 , y -1});   // Croix éloignée droite haut
                } else if (coin.equals("hautDroit")) {
                    coords.add(new int[]{x + 1, y});       // Droite
                    coords.add(new int[]{x, y - 1});       // Haut
                    coords.add(new int[]{x - 1, y});       // Gauche
                    coords.add(new int[]{x, y + 1});       // Bas
                    coords.add(new int[]{x + 1, y + 2});   // Croix éloignée droite-bas
                    coords.add(new int[]{x - 2, y - 1});   // Croix éloignée gauche-haut
                } else if (coin.equals("basGauche")) {
                    coords.add(new int[]{x - 1, y});       // Gauche
                    coords.add(new int[]{x, y + 1});       // Bas
                    coords.add(new int[]{x + 1, y});       // Droite
                    coords.add(new int[]{x, y - 1});       // Haut
                    coords.add(new int[]{x + 2, y + 1});   // Croix éloignée droite-bas
                    coords.add(new int[]{x - 1, y - 2});   // Croix éloignée gauche-haut
                } else if (coin.equals("basDroit")) {
                    coords.add(new int[]{x + 1, y});       // Droite
                    coords.add(new int[]{x, y + 1});       // Bas
                    coords.add(new int[]{x, y - 1});       // Haut
                    coords.add(new int[]{x - 1, y});       // Gauche
                    coords.add(new int[]{x + 1, y - 2});   // Croix éloignée droite-haut
                    coords.add(new int[]{x - 2, y + 1});   // Croix éloignée gauche-bas
                }
                break;
            case 1:
                // Inchangé
                break;
            case 2:
                // Inchangé
                break;
            case 3:
                // Inchangé
                break;
        }
        return coords;
    }
}
