package com.tpgr3.Techniques;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Détecte un 1 en coin et un 0 adjacent (±2 selon le coin).
 * Place 4 croix autour du 0 et 2 croix autour du 1.
 * Gère potentiellement plusieurs couples (1,0) dans la grille.
 */
public class Tech_1Et0Coin implements Techniques {

    // Stocke le couple retenu pour appliquer la technique
    private int[] position1 = null;
    private int[] position0 = null;

    // Slots à marquer en croix
    private final List<int[]> slotsCroix = new ArrayList<>();

    @Override
    public boolean estApplicable(Grille grille) {
        // Réinitialise au début
        reset();

        // 1. Trouver tous les couples (1 en coin, 0 adjacent)
        List<int[][]> couples = detecterTousLesCouples1_0(grille);
        if (couples.isEmpty()) return false;

        // 2. Parcourt ces couples
        for (int[][] pair : couples) {
            position1 = pair[0];
            position0 = pair[1];

            buildCoordinates(grille); // Construit la liste de slots croix

            // Vérifie s’ils sont déjà marqués (croix)
            if (verifSlots(grille)) {
                System.out.printf("Couple (1@[%d,%d],0@[%d,%d]) déjà appliqué, on ignore%n",
                                  position1[0], position1[1], position0[0], position0[1]);
                slotsCroix.clear();
                continue;
            }
            // Sinon => ce couple n’est pas encore appliqué
            System.out.printf("Couple (1@[%d,%d],0@[%d,%d]) => technique 1Et0Coin applicable%n",
                              position1[0], position1[1], position0[0], position0[1]);
            return true;
        }

        // Si on arrive ici => tous les couples (1,0) sont déjà marqués ou inexistants
        return false;
    }

    @Override
    public void appliquer(Grille grille) {
        // Vérifie d’abord s’il y a un couple applicable
        if (!estApplicable(grille)) return;

        // 1. Pose les croix
        for (int[] c : slotsCroix) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (cell instanceof Slot s) {
                s.setMarque(new Croix());
            }
        }

        // 2. Reset
        reset();
    }

    /**
     * Détecte tous les couples (1 en coin, 0 adjacent) dans la grille.
     * @return liste de pairs { {x1,y1}, {x0,y0} }
     */
    private List<int[][]> detecterTousLesCouples1_0(Grille grille) {
        List<int[][]> couples = new ArrayList<>();
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cell = grille.getCellule(x, y);
                // Cherche un 1 en coin
                if (cell instanceof Case c1 && c1.getValeur() == 1 && grille.estDansUnCoin(x, y)) {
                    String coin = getCoin(x, y, grille);

                    // 2 directions possibles vers un 0
                    for (int[] dir : getZeroDirections(coin)) {
                        int x0 = x + dir[0];
                        int y0 = y + dir[1];
                        if (grille.estValide(x0, y0)) {
                            Cellule c0 = grille.getCellule(x0, y0);
                            // Vérifie si c’est un 0
                            if (c0 instanceof Case case0 && case0.getValeur() == 0) {
                                // Vérifie les slots autour du 1 et du 0 sont Neutres
                                if (slotsNeutres(grille.getSlotsAdjacentsCase(x, y)) &&
                                    slotsNeutres(grille.getSlotsAdjacentsCase(x0, y0))) {
                                    couples.add(new int[][] {
                                        {x, y},      // position1
                                        {x0, y0}     // position0
                                    });
                                }
                            }
                        }
                    }
                }
            }
        }
        return couples;
    }

    /**
     * Construit la liste des slotsCroix pour le couple (1,0).
     * - 4 croix autour du 0
     * - 2 croix autour du 1 selon coin
     */
    private void buildCoordinates(Grille grille) {
        slotsCroix.clear();

        // 4 croix autour du 0
        for (Slot s : grille.getSlotsAdjacentsCase(position0[0], position0[1])) {
            slotsCroix.add(new int[]{s.getX(), s.getY()});
        }

        // 2 croix autour du 1
        String coin = getCoin(position1[0], position1[1], grille);
        Map<String, int[][]> croixOffsets = Map.of(
            "hautGauche", new int[][]{{-1, 0}, {0, -1}},
            "hautDroit",  new int[][]{{1, 0}, {0, -1}},
            "basGauche",  new int[][]{{-1, 0}, {0, 1}},
            "basDroit",   new int[][]{{1, 0}, {0, 1}}
        );

        for (int[] offset : croixOffsets.get(coin)) {
            int cx = position1[0] + offset[0];
            int cy = position1[1] + offset[1];
            if (grille.estValide(cx, cy) && grille.getCellule(cx, cy) instanceof Slot) {
                slotsCroix.add(new int[]{cx, cy});
            }
        }
    }

    /**
     * Vérifie si tous les slots sont déjà marqués en Croix.
     * @return true si tout est correct, false sinon
     */
    private boolean verifSlots(Grille grille) {
        System.out.println("\n-- Vérification CROIX (1Et0Coin) --");
        boolean correct = true;
        for (int[] c : slotsCroix) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (cell instanceof Slot s) {
                String type = s.getMarque().getClass().getSimpleName();
                System.out.printf("Attendu CROIX à (%d,%d) - actuel : %s%n", c[0], c[1], type);
                if (!(s.getMarque() instanceof Croix)) {
                    correct = false;
                }
            } else {
                System.out.printf("Erreur : (%d,%d) n'est pas Slot%n", c[0], c[1]);
                correct = false;
            }
        }
        return correct;
    }

    /**
     * Vérifie si tous les slots de la liste sont Neutres.
     */
    private boolean slotsNeutres(List<Slot> slots) {
        for (Slot s : slots) {
            if (!(s.getMarque() instanceof Neutre)) return false;
        }
        return true;
    }

    /**
     * Identifie le coin d’une case (x,y).
     */
    private String getCoin(int x, int y, Grille grille) {
        int[] d = grille.getDimensionsLogiques();
        if (x == 1 && y == 1) return "hautGauche";
        if (x == d[0] * 2 - 1 && y == 1) return "hautDroit";
        if (x == 1 && y == d[1] * 2 - 1) return "basGauche";
        if (x == d[0] * 2 - 1 && y == d[1] * 2 - 1) return "basDroit";
        return "aucun";
    }

    /**
     * Directions possibles (±2,±0) selon le coin
     */
    private int[][] getZeroDirections(String coin) {
        return switch (coin) {
            case "hautGauche" -> new int[][]{{2, 0}, {0, 2}};
            case "hautDroit"  -> new int[][]{{-2, 0}, {0, 2}};
            case "basGauche"  -> new int[][]{{2, 0}, {0, -2}};
            case "basDroit"   -> new int[][]{{-2, 0}, {0, -2}};
            default -> new int[0][0];
        };
    }

    /**
     * Reset total.
     */
    private void reset() {
        position1 = null;
        position0 = null;
        slotsCroix.clear();
    }
}
