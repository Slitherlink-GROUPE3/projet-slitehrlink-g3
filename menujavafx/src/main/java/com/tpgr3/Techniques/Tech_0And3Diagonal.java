package com.tpgr3.Techniques;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Détecte tous les (3,0) diagonaux (±2,±2).
 * Pour le premier couple non déjà marqué,
 * place 2 bâtons communs autour du 3
 * et des croix autour du 0.
 */
public class Tech_0And3Diagonal implements Techniques {

    // Le couple retenu pour l'application
    private int[] position3 = null;
    private int[] position0 = null;

    private final List<int[]> slotsBaton = new ArrayList<>();
    private final List<int[]> slotsCroix = new ArrayList<>();

    @Override
    public boolean estApplicable(Grille grille) {
        // Réinitialise
        reset();

        // 1. Trouver tous les couples (3,0) diagonaux
        List<int[][]> couples = detecterTousLesCouples3_0(grille);
        if (couples.isEmpty()) return false;

        // 2. Parcourir chaque couple
        for (int[][] pair : couples) {
            position3 = pair[0];
            position0 = pair[1];

            buildCoordinates(grille);

            // 3. Vérif si déjà marqué
            if (verifSlots(grille)) {
                System.out.printf("Couple (3@[%d,%d],0@[%d,%d]) déjà appliqué, on ignore%n",
                                  position3[0], position3[1], position0[0], position0[1]);
                clearSlotsOnly();
                continue;
            }
            // Pas déjà fait => on garde ce couple
            System.out.printf("Couple (3@[%d,%d],0@[%d,%d]) => technique 0And3Diagonal applicable%n",
                              position3[0], position3[1], position0[0], position0[1]);
            return true; // On s'arrête au premier couple non marqué
        }

        // Tous les couples sont déjà appliqués
        clearSlotsOnly();
        return false;
    }

    @Override
    public void appliquer(Grille grille) {
        if (!estApplicable(grille)) return;

        // Pose des bâtons
        for (int[] coord : slotsBaton) {
            Cellule c = grille.getCellule(coord[0], coord[1]);
            if (c instanceof Slot s) {
                s.setMarque(new Baton());
            }
        }
        // Pose des croix
        for (int[] coord : slotsCroix) {
            Cellule c = grille.getCellule(coord[0], coord[1]);
            if (c instanceof Slot s) {
                s.setMarque(new Croix());
            }
        }

        // Reset complet
        reset();
    }

    /**
     * Cherche tous les couples (3,0) en diagonale (±2,±2).
     * Retourne liste de pairs : { {x3,y3}, {x0,y0} }.
     */
    private List<int[][]> detecterTousLesCouples3_0(Grille grille) {
        List<int[][]> couples = new ArrayList<>();
        // 4 diagonales possibles
        int[][] diagonales = {{2, 2}, {-2, 2}, {-2, -2}, {2, -2}};

        // Parcours global
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cell = grille.getCellule(x, y);
                if (cell instanceof Case c3 && c3.getValeur() == 3) {
                    // Vérifie chaque diagonale
                    for (int[] diag : diagonales) {
                        int x0 = x + diag[0];
                        int y0 = y + diag[1];
                        if (grille.estValide(x0, y0)) {
                            Cellule cell0 = grille.getCellule(x0, y0);
                            if (cell0 instanceof Case c0 && c0.getValeur() == 0) {
                                // Vérifie qu'il n'y a pas déjà un bâton adjacent au 3
                                if (!batonAutourDu3(grille, x, y)) {
                                    // On ajoute ce couple
                                    couples.add(new int[][]{
                                        {x, y},   // position3
                                        {x0, y0}  // position0
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
     * Construit les slotsBaton + slotsCroix pour un couple (3,0).
     */
    private void buildCoordinates(Grille grille) {
        slotsBaton.clear();
        slotsCroix.clear();

        int x3 = position3[0], y3 = position3[1];
        int x0 = position0[0], y0 = position0[1];
        int dx = x0 - x3, dy = y0 - y3;

        // Bâtons communs
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

        // Ajoute si c’est un Slot
        for (int[] coord : batonSlots) {
            if (grille.estValide(coord[0], coord[1]) &&
                grille.getCellule(coord[0], coord[1]) instanceof Slot) {
                slotsBaton.add(coord);
            }
        }

        // Croix autour du 0
        for (Slot s : grille.getSlotsAdjacentsCase(x0, y0)) {
            slotsCroix.add(new int[]{s.getX(), s.getY()});
        }
    }

    /**
     * Vérifie si tous ces slots sont déjà marqués correctement (Baton/Croix).
     */
    private boolean verifSlots(Grille grille) {
        System.out.println("\n-- Vérification BATONS (0And3Diagonal) --");
        boolean correct = true;

        for (int[] c : slotsBaton) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (cell instanceof Slot s) {
                String type = s.getMarque().getClass().getSimpleName();
                System.out.printf("Baton attendu (%d,%d) - actuel : %s%n", c[0], c[1], type);
                if (!(s.getMarque() instanceof Baton)) {
                    correct = false;
                }
            } else {
                System.out.printf("Erreur : (%d,%d) pas un Slot%n", c[0], c[1]);
                correct = false;
            }
        }

        System.out.println("-- Vérification CROIX (0And3Diagonal) --");
        for (int[] c : slotsCroix) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (cell instanceof Slot s) {
                String type = s.getMarque().getClass().getSimpleName();
                System.out.printf("Croix attendue (%d,%d) - actuel : %s%n", c[0], c[1], type);
                if (!(s.getMarque() instanceof Croix)) {
                    correct = false;
                }
            } else {
                System.out.printf("Erreur : (%d,%d) pas un Slot%n", c[0], c[1]);
                correct = false;
            }
        }

        return correct;
    }

    /**
     * Vérifie s'il y a déjà un bâton parmi les slots autour du 3.
     */
    private boolean batonAutourDu3(Grille grille, int x3, int y3) {
        for (Slot s : grille.getSlotsAdjacentsCase(x3, y3)) {
            if (s.getMarque() instanceof Baton) {
                return true;
            }
        }
        return false;
    }

    /**
     * Nettoie toutes les variables globales.
     */
    private void reset() {
        position3 = null;
        position0 = null;
        slotsBaton.clear();
        slotsCroix.clear();
    }

    /*
     * Nettoie les listes de slots
     */
    private void clearSlotsOnly() {
        slotsBaton.clear();
        slotsCroix.clear();
    }
}
