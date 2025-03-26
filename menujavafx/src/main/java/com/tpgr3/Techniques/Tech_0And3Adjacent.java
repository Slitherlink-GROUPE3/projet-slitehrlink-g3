package com.tpgr3.Techniques;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Détecte un 3 et un 0 adjacents orthogonalement (±2 en x ou y).
 * Peut gérer plusieurs couples (3,0) dans la grille.
 */
public class Tech_0And3Adjacent implements Techniques {

    // Le couple sur lequel on va appliquer la technique
    private int[] position3 = null;
    private int[] position0 = null;

    // Coordonnées des Slots (pour croix / bâton)
    private final List<int[]> slotsCroix = new ArrayList<>();
    private final List<int[]> slotsBaton = new ArrayList<>();

    @Override
    public boolean estApplicable(Grille grille) {
        // 1. Récupère tous les couples (3,0) possibles
        List<int[][]> couples = detecterTousLesCouples30(grille);

        // 2. Parcourt chacun et vérifie s’il est déjà marqué
        for (int[][] couple : couples) {
            position3 = couple[0];
            position0 = couple[1];

            // Construit la liste des slots
            buildCoordinates(grille);

            // Vérifie s’ils sont déjà marqués
            if (verifSlots(grille)) {
                // Déjà marqué -> on ignore ce couple et on continue
                System.out.printf("Couple (3@[%d,%d],0@[%d,%d]) déjà appliqué, on ignore%n",
                        position3[0], position3[1], position0[0], position0[1]);
                resetSlotsOnly();
                continue;
            }
            // Pas déjà fait => on peut appliquer
            System.out.printf("Couple (3@[%d,%d],0@[%d,%d]) NON marqué => technique applicable%n",
                    position3[0], position3[1], position0[0], position0[1]);
            return true; // On s'arrête au premier couple non marqué
        }

        // 3. Si on n’a trouvé aucun couple non marqué => false
        resetSlotsOnly();
        return false;
    }

    @Override
    public void appliquer(Grille grille) {
        // 1. Vérifie d’abord s’il y a un couple valide
        if (!estApplicable(grille)) return;

        // 2. Applique la technique sur le couple retenu (position3, position0)
        for (int[] c : slotsCroix) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (cell instanceof Slot s) {
                s.setMarque(new Croix());
            }
        }
        for (int[] c : slotsBaton) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (cell instanceof Slot s) {
                s.setMarque(new Baton());
            }
        }

        // 3. Reset
        resetAll();
    }

    /**
     * Retourne la liste de tous les couples (3,0) trouvés dans la grille.
     * Chaque couple est un tableau 2D : {{x3,y3},{x0,y0}}.
     */
    private List<int[][]> detecterTousLesCouples30(Grille grille) {
        List<int[][]> couples = new ArrayList<>();
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cell = grille.getCellule(x, y);
                if (cell instanceof Case c3 && c3.getValeur() == 3) {
                    // On regarde les cases adjacentes (±2)
                    List<Case> voisins = grille.getCasesAdjacentes(x, y);
                    for (Case voisin : voisins) {
                        if (voisin.getValeur() == 0) {
                            // Ajoute le couple (3,0)
                            couples.add(new int[][] {
                                    {x, y}, // position3
                                    {voisin.getX(), voisin.getY()} // position0
                            });
                        }
                    }
                }
            }
        }
        return couples;
    }

    /**
     * Construit toutes les coordonnées de slots en croix ou bâton
     * pour le couple global (position3, position0).
     */
    private void buildCoordinates(Grille grille) {
        slotsCroix.clear();
        slotsBaton.clear();

        int dx0 = position0[0] - position3[0];
        int dy0 = position0[1] - position3[1];

        // Croix autour du 0
        for (Slot s : grille.getSlotsAdjacentsCase(position0[0], position0[1])) {
            slotsCroix.add(new int[]{s.getX(), s.getY()});
        }

        // Bâtons + extension + croix coin autour du 3
        int[][] slotDirs, extensionDirs, croixCoords;

        if (dx0 == 2) { // 0 droite
            slotDirs = new int[][]{{-1, 0}, {0, -1}, {0, 1}};
            extensionDirs = new int[][]{{1, 2}, {1, -2}};
            croixCoords = new int[][]{{-1, -1}, {-1, 1}};
        } else if (dx0 == -2) { // 0 gauche
            slotDirs = new int[][]{{1, 0}, {0, -1}, {0, 1}};
            extensionDirs = new int[][]{{-1, 2}, {-1, -2}};
            croixCoords = new int[][]{{1, -1}, {1, 1}};
        } else if (dy0 == 2) { // 0 bas
            slotDirs = new int[][]{{0, -1}, {-1, 0}, {1, 0}};
            extensionDirs = new int[][]{{-2, 1}, {2, 1}};
            croixCoords = new int[][]{{-1, -1}, {1, -1}};
        } else { // 0 haut
            slotDirs = new int[][]{{0, 1}, {-1, 0}, {1, 0}};
            extensionDirs = new int[][]{{-2, -1}, {2, -1}};
            croixCoords = new int[][]{{-1, 1}, {1, 1}};
        }

        // Bâtons autour du 3
        for (int[] dir : slotDirs) {
            int sx = position3[0] + dir[0];
            int sy = position3[1] + dir[1];
            if (grille.estValide(sx, sy) && grille.getCellule(sx, sy) instanceof Slot) {
                slotsBaton.add(new int[]{sx, sy});
            }
        }
        // Bâtons d’extension
        for (int[] dir : extensionDirs) {
            int sx = position3[0] + dir[0];
            int sy = position3[1] + dir[1];
            if (grille.estValide(sx, sy) && grille.getCellule(sx, sy) instanceof Slot) {
                slotsBaton.add(new int[]{sx, sy});
            }
        }
        // Croix coin
        for (int[] dir : croixCoords) {
            int sx = position3[0] + dir[0];
            int sy = position3[1] + dir[1];
            if (grille.estValide(sx, sy) && grille.getCellule(sx, sy) instanceof Slot) {
                slotsCroix.add(new int[]{sx, sy});
            }
        }
    }

    /**
     * Vérifie si tous les slots sont déjà marqués comme attendu.
     * @return true si déjà tout correct, false sinon
     */
    private boolean verifSlots(Grille grille) {
        System.out.println("\n-- Vérification des CROIX --");
        boolean tousCorrects = true;
        for (int[] c : slotsCroix) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (cell instanceof Slot s) {
                String type = s.getMarque().getClass().getSimpleName();
                System.out.printf("Attendu CROIX à (%d,%d) - actuel : %s%n", c[0], c[1], type);
                if (!(s.getMarque() instanceof Croix)) {
                    tousCorrects = false;
                }
            } else {
                System.out.printf("Erreur : (%d,%d) n'est pas Slot%n", c[0], c[1]);
                tousCorrects = false;
            }
        }

        System.out.println("-- Vérification des BATONS --");
        for (int[] c : slotsBaton) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (cell instanceof Slot s) {
                String type = s.getMarque().getClass().getSimpleName();
                System.out.printf("Attendu BATON à (%d,%d) - actuel : %s%n", c[0], c[1], type);
                if (!(s.getMarque() instanceof Baton)) {
                    tousCorrects = false;
                }
            } else {
                System.out.printf("Erreur : (%d,%d) n'est pas Slot%n", c[0], c[1]);
                tousCorrects = false;
            }
        }
        return tousCorrects;
    }

    /**
     * Réinitialise uniquement les listes de slots.
     * (On garde position3 / position0 pour potentielle utilisation)
     */
    private void resetSlotsOnly() {
        slotsCroix.clear();
        slotsBaton.clear();
    }

    /**
     * Réinitialise toutes les variables (positions et slots).
     */
    private void resetAll() {
        position3 = null;
        position0 = null;
        resetSlotsOnly();
    }
}
