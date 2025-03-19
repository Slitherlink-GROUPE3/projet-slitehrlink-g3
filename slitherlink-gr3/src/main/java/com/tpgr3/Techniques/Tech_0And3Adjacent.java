package com.tpgr3.Techniques;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

import java.util.ArrayList;
import java.util.List;

public class Tech_0And3Adjacent implements Techniques {

    private int[] position3 = null;
    private int[] position0 = null;

    private List<int[]> slotsCroix = new ArrayList<>();
    private List<int[]> slotsBaton = new ArrayList<>();

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cellule = grille.getCellule(x, y);
                if (cellule instanceof Case c3 && c3.getValeur() == 3) {
                    List<Case> voisins = grille.getCasesAdjacentes(x, y);
                    for (Case v : voisins) {
                        if (v.getValeur() == 0) {
                            position3 = new int[]{x, y};
                            position0 = new int[]{v.getX(), v.getY()};
                            genererCoordonnees(grille);

                            if (verifSlots(grille)) {
                                System.out.println("Technique déjà appliquée.");
                                reset();
                                return false;
                            }
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

        reset();
    }

    // === Génère toutes les coordonnées de slots à marquer selon la configuration ===
    private void genererCoordonnees(Grille grille) {
        slotsCroix.clear();
        slotsBaton.clear();
    
        int dx0 = position0[0] - position3[0];
        int dy0 = position0[1] - position3[1];
    
        // Croix autour du 0
        for (Slot s : grille.getSlotsAdjacentsCase(position0[0], position0[1])) {
            slotsCroix.add(new int[]{s.getX(), s.getY()});
        }
    
        int[][] slotDirs, extensionDirs, croixCoords;
    
        if (dx0 == 2) {
            System.out.println("1");
            slotDirs = new int[][]{{-1, 0}, {0, -1}, {0, 1}};
            extensionDirs = new int[][]{{1, 2}, {1, -2}};
            croixCoords = new int[][]{{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}};
        } else if (dx0 == -2) {
            System.out.println("2");
            slotDirs = new int[][]{{1, 0}, {0, -1}, {0, 1}};
            extensionDirs = new int[][]{{-1, 2}, {-1, -2}};
            croixCoords = new int[][]{{2, -1}, {2, 1}, {1, -2}, {1, 2}};
        } else if (dy0 == 2) {
            System.out.println("3");
            slotDirs = new int[][]{{0, -1}, {-1, 0}, {1, 0}};
            extensionDirs = new int[][]{{-2, 1}, {2, 1}};
            croixCoords = new int[][]{{-1, -2}, {1, -2}, {-2, -1}, {2, -1}};
        } else {
            System.out.println("4");
            slotDirs = new int[][]{{0, 1}, {-1, 0}, {1, 0}};
            extensionDirs = new int[][]{{-2, -1}, {2, -1}};
            croixCoords = new int[][]{{-1, 2}, {1, 2}, {-2, 1}, {2, 1}};
        }
    
        // Bâtons autour du 3
        for (int[] dir : slotDirs) {
            int x = position3[0] + dir[0], y = position3[1] + dir[1];
            if (grille.getCellule(x, y) instanceof Slot) {
                slotsBaton.add(new int[]{x, y});
            } else {
                System.out.printf("Ignoré (non Slot) BATON (%d,%d)%n", x, y);
            }
        }
    
        // Bâtons extension
        for (int[] dir : extensionDirs) {
            int x = position3[0] + dir[0], y = position3[1] + dir[1];
            if (grille.getCellule(x, y) instanceof Slot) {
                slotsBaton.add(new int[]{x, y});
            } else {
                System.out.printf("Ignoré (non Slot) BATON EXT (%d,%d)%n", x, y);
            }
        }
    
        // Croix aux coins
        for (int[] dir : croixCoords) {
            int x = position3[0] + dir[0], y = position3[1] + dir[1];
            if (grille.getCellule(x, y) instanceof Slot) {
                slotsCroix.add(new int[]{x, y});
            } else {
                System.out.printf("Ignoré (non Slot) CROIX COIN (%d,%d)%n", x, y);
            }
        }
    }
    
    private boolean verifSlots(Grille grille) {
        boolean tousCorrects = true;
    
        System.out.println("\n-- Vérification des CROIX --");
        for (int[] c : slotsCroix) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (cell instanceof Slot s) {
                String type = s.getMarque().getClass().getSimpleName();
                System.out.printf("Croix attendue à (%d,%d) - Marque actuelle : %s%n", c[0], c[1], type);
                if (!(s.getMarque() instanceof Croix)) {
                    tousCorrects = false;
                }
            } else {
                System.out.printf("Erreur : cellule (%d,%d) n'est pas un Slot%n", c[0], c[1]);
                tousCorrects = false;
            }
        }
    
        System.out.println("-- Vérification des BATONS --");
        for (int[] c : slotsBaton) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (cell instanceof Slot s) {
                String type = s.getMarque().getClass().getSimpleName();
                System.out.printf("Baton attendu à (%d,%d) - Marque actuelle : %s%n", c[0], c[1], type);
                if (!(s.getMarque() instanceof Baton)) {
                    tousCorrects = false;
                }
            } else {
                System.out.printf("Erreur : cellule (%d,%d) n'est pas un Slot%n", c[0], c[1]);
                tousCorrects = false;
            }
        }
    
        return tousCorrects;
    }
    

    // === Réinitialise les variables ===
    private void reset() {
        position3 = null;
        position0 = null;
        slotsCroix.clear();
        slotsBaton.clear();
    }
}
