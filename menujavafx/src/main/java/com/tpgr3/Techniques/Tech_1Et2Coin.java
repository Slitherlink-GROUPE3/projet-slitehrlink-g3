package com.tpgr3.Techniques;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

import java.util.List;

public class Tech_1Et2Coin implements Techniques {

    private int[] position1 = null;
    private int[] position2 = null;

    // === Détection : cherche un 1 en coin avec un 2 adjacent ===
    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cellule = grille.getCellule(x, y);

                // On cible les cases 1 situées dans un coin
                if (cellule instanceof Case c1 && c1.getValeur() == 1 && grille.estDansUnCoin(x, y)) {
                    List<Case> voisins = grille.getCasesAdjacentes(x, y);

                    // On affiche les voisins pour debug
                    System.out.println("Case 1 (" + x + "," + y + ") voisins :");
                    for (Case v : voisins) {
                        System.out.println("  -> (" + v.getX() + "," + v.getY() + ") = " + v.getValeur());

                        if (v.getValeur() == 2) {
                            // Vérifie que tous les slots autour sont neutres
                            if (slotsNeutres(grille.getSlotsAdjacentsCase(x, y))
                                    && slotsNeutres(grille.getSlotsAdjacentsCase(v.getX(), v.getY()))) {
                                position1 = new int[]{x, y};
                                position2 = new int[]{v.getX(), v.getY()};
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    // === Application : 2 croix autour du 1 + 1 bâton spécifique autour du 2 ===
    @Override
    public void appliquer(Grille grille) {
        if (!estApplicable(grille)) return;

        int x1 = position1[0], y1 = position1[1];
        int x2 = position2[0], y2 = position2[1];
        String coin = getCoin(x1, y1, grille);

        // Croix à poser autour du 1 selon le coin
        int[][] croixOffsets;
        switch (coin) {
            case "hautGauche":
                croixOffsets = new int[][]{{-1, 0}, {0, -1}};
                break;
            case "hautDroit":
                croixOffsets = new int[][]{{1, 0}, {0, -1}};
                break;
            case "basGauche":
                croixOffsets = new int[][]{{-1, 0}, {0, 1}};
                break;
            case "basDroit":
                croixOffsets = new int[][]{{1, 0}, {0, 1}};
                break;
            default:
                croixOffsets = new int[0][0];
                break;
        }

        for (int[] offset : croixOffsets) {
            int cx = x1 + offset[0], cy = y1 + offset[1];
            ((Slot) grille.getCellule(cx, cy)).setMarque(new Croix());
        }

        // Bâton à poser autour du 2 selon coin + position du 2 par rapport au 1
        int dx = x2 - x1, dy = y2 - y1;
        int[] batonOffset = switch (coin) {
            case "hautDroit"  -> dx == -2 ? new int[]{-2, -1} : new int[]{1, 2};
            case "hautGauche" -> dx == 2  ? new int[]{2, -1}  : new int[]{-1, 2};
            case "basDroit"   -> dx == -2 ? new int[]{-2, 1}  : new int[]{1, -2};
            case "basGauche"  -> dx == 2  ? new int[]{2, 1}   : new int[]{-1, -2};
            default -> new int[]{0, 0};
        };

        int bx = x2 + batonOffset[0], by = y2 + batonOffset[1];
        ((Slot) grille.getCellule(bx, by)).setMarque(new Baton());

        position1 = null;
        position2 = null;
    }

    // === Vérifie que tous les slots sont neutres ===
    private boolean slotsNeutres(List<Slot> slots) {
        for (Slot slot : slots) {
            if (!(slot.getMarque() instanceof Neutre)) return false;
        }
        return true;
    }

    // === Déduit le type de coin ===
    private String getCoin(int x, int y, Grille grille) {
        int[] dim = grille.getDimensionsLogiques();
        if (x == 1 && y == 1) return "hautGauche";
        if (x == dim[0] * 2 - 1 && y == 1) return "hautDroit";
        if (x == 1 && y == dim[1] * 2 - 1) return "basGauche";
        if (x == dim[0] * 2 - 1 && y == dim[1] * 2 - 1) return "basDroit";
        return "aucun";
    }
}
