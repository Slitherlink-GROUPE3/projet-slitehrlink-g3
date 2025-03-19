package com.tpgr3.Techniques;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

import java.util.List;
import java.util.Map;

public class Tech_1Et0Coin implements Techniques {

    private int[] position1 = null;
    private int[] position0 = null;

    // === Détection : 1 en coin + 0 adjacent ===
    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cellule = grille.getCellule(x, y);
                if (cellule instanceof Case c1 && c1.getValeur() == 1 && grille.estDansUnCoin(x, y)) {
                    String coin = getCoin(x, y, grille);
                    for (int[] dir : getZeroDirections(coin)) {
                        int x0 = x + dir[0], y0 = y + dir[1];
                        if (grille.estValide(x0, y0)) {
                            Cellule c0 = grille.getCellule(x0, y0);
                            if (c0 instanceof Case c && c.getValeur() == 0) {
                                if (slotsNeutres(grille.getSlotsAdjacentsCase(x, y)) &&
                                    slotsNeutres(grille.getSlotsAdjacentsCase(x0, y0))) {
                                    position1 = new int[]{x, y};
                                    position0 = new int[]{x0, y0};
                                    System.out.println("1 en coin " + coin + ", 0 en (" + x0 + "," + y0 + ")");
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

    // === Application : croix autour du 0 et autour du 1 ===
    @Override
    public void appliquer(Grille grille) {
        if (!estApplicable(grille)) return;

        int x1 = position1[0], y1 = position1[1];
        int x0 = position0[0], y0 = position0[1];

        // Croix autour du 0
        for (Slot s : grille.getSlotsAdjacentsCase(x0, y0)) {
            s.setMarque(new Croix());
        }

        // Croix autour du 1 selon son coin
        Map<String, int[][]> croixOffsets = Map.of(
            "hautGauche", new int[][]{{-1, 0}, {0, -1}},
            "hautDroit",  new int[][]{{1, 0}, {0, -1}},
            "basGauche", new int[][]{{-1, 0}, {0, 1}},
            "basDroit",  new int[][]{{1, 0}, {0, 1}}
        );

        String coin = getCoin(x1, y1, grille);
        for (int[] offset : croixOffsets.get(coin)) {
            int cx = x1 + offset[0], cy = y1 + offset[1];
            ((Slot) grille.getCellule(cx, cy)).setMarque(new Croix());
        }

        position1 = null;
        position0 = null;
    }

    // === Vérifie que tous les slots sont neutres ===
    private boolean slotsNeutres(List<Slot> slots) {
        for (Slot s : slots) {
            if (!(s.getMarque() instanceof Neutre)) return false;
        }
        return true;
    }

    // === Donne le coin d'une case ===
    private String getCoin(int x, int y, Grille grille) {
        int[] d = grille.getDimensionsLogiques();
        if (x == 1 && y == 1) return "hautGauche";
        if (x == d[0] * 2 - 1 && y == 1) return "hautDroit";
        if (x == 1 && y == d[1] * 2 - 1) return "basGauche";
        if (x == d[0] * 2 - 1 && y == d[1] * 2 - 1) return "basDroit";
        return "aucun";
    }

    // === Donne les directions possibles du 0 selon coin ===
    private int[][] getZeroDirections(String coin) {
        return switch (coin) {
            case "hautGauche" -> new int[][]{{2, 0}, {0, 2}};
            case "hautDroit"  -> new int[][]{{-2, 0}, {0, 2}};
            case "basGauche"  -> new int[][]{{2, 0}, {0, -2}};
            case "basDroit"   -> new int[][]{{-2, 0}, {0, -2}};
            default -> new int[0][0];
        };
    }
}
