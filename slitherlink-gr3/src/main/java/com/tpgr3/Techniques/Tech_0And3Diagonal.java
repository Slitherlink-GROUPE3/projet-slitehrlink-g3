package com.tpgr3.Techniques;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

import java.util.List;

public class Tech_0And3Diagonal implements Techniques {

    // Stocke les positions (x, y) de la case 3 et du 0
    private int[] position3 = null;
    private int[] position0 = null;

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cellule = grille.getCellule(x, y);

                if (cellule instanceof Case) {
                    Case case3 = (Case) cellule;
                    if (case3.getValeur() == 3) {
                        // Vérifie les 4 diagonales
                        int[][] diagonales = {{2, 2}, {-2, 2}, {-2, -2}, {2, -2}};
                        for (int[] diag : diagonales) {
                            int x0 = x + diag[0];
                            int y0 = y + diag[1];

                            if (grille.estValide(x0, y0)) {
                                Cellule cellule0 = grille.getCellule(x0, y0);
                                if (cellule0 instanceof Case && ((Case)cellule0).getValeur() == 0) {

                                    // Vérifie qu'il n'y a pas déjà de bâtons autour du 3
                                    List<Slot> slots3 = grille.getSlotsAdjacentsCase(x, y);
                                    boolean batonTrouve = false;
                                    for (Slot slot : slots3) {
                                        if (slot.getMarque() instanceof Baton) {
                                            batonTrouve = true;
                                            break;
                                        }
                                    }

                                    if (!batonTrouve) {
                                        position3 = new int[] {x, y};
                                        position0 = new int[] {x0, y0};
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void appliquer(Grille grille) {
        if (!estApplicable(grille)) return;

        int x3 = position3[0];
        int y3 = position3[1];
        int x0 = position0[0];
        int y0 = position0[1];

        int dx = x0 - x3;
        int dy = y0 - y3;

        // Détermination des 2 bâtons communs aux deux solutions possibles
        int[][] batonSlots;

        if (dx == 2 && dy == 2) {
            batonSlots = new int[][] {{x3 + 1, y3}, {x3, y3 + 1}};
        } else if (dx == -2 && dy == 2) {
            batonSlots = new int[][] {{x3 - 1, y3}, {x3, y3 + 1}};
        } else if (dx == -2 && dy == -2) {
            batonSlots = new int[][] {{x3 - 1, y3}, {x3, y3 - 1}};
        } else { // dx == 2 && dy == -2
            batonSlots = new int[][] {{x3 + 1, y3}, {x3, y3 - 1}};
        }

        for (int[] coord : batonSlots) {
            int sx = coord[0];
            int sy = coord[1];
            if (grille.estValide(sx, sy)) {
                Cellule c = grille.getCellule(sx, sy);
                if (c instanceof Slot) {
                    ((Slot) c).setMarque(new Baton());
                }
            }
        }

        // Placer croix autour de la case 0
        List<Slot> slots0 = grille.getSlotsAdjacentsCase(x0, y0);
        for (Slot slot : slots0) {
            slot.setMarque(new Croix());
        }
    }
}