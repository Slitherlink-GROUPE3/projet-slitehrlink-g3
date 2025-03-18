package com.tpgr3.Techniques;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

import java.util.List;

public class Tech_0And3Adjacent implements Techniques {

    // Attribut pour stocker la position du 0 par rapport au 3
    private int[] position = null; // dx, dy entre 3 et 0

    @Override
    public boolean estApplicable(Grille grille) {
        // Recherche d'une Case 3 avec une Case 0 adjacente
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cellule = grille.getCellule(x, y);

                if (cellule instanceof Case) {
                    Case case3 = (Case) cellule;
                    if (case3.getValeur() == 3) {
                        List<Case> casesAdj = grille.getCasesAdjacentes(x, y);
                        for (Case caseAdj : casesAdj) {
                            if (caseAdj.getValeur() == 0) {
                                // Stocker la position du 0 par rapport au 3
                                int dx = caseAdj.getX() - x;
                                int dy = caseAdj.getY() - y;
                                position = new int[] {dx, dy};
                                System.out.println("Position du 0 par rapport au 3 : " + dx + ", " + dy);
                                return true;
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

        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cellule = grille.getCellule(x, y);

                if (cellule instanceof Case) {
                    Case case3 = (Case) cellule;
                    if (case3.getValeur() == 3) {
                        int x0 = x + position[0];
                        int y0 = y + position[1];

                        if (grille.estValide(x0, y0)) {
                            Cellule cellule0 = grille.getCellule(x0, y0);
                            if (cellule0 instanceof Case && ((Case)cellule0).getValeur() == 0) {
                                // Cas valide pour appliquer la technique

                                // Placer les croix autour du 0
                                List<Slot> slots0 = grille.getSlotsAdjacentsCase(x0, y0);
                                for (Slot slot : slots0) {
                                    slot.setMarque(new Croix());
                                }

                                // Détermination des directions orthogonales
                                int dx0 = position[0];
                                int dy0 = position[1];

                                // Les slots à marquer autour du 3 (hors côté 0)
                                int[][] slotDirs;
                                int[][] extensionDirs;
                                int[][] croixCoords;

                                // Déterminer les directions en fonction de la position du 0 par rapport au 3
                                if (dx0 == 2) { // 0 est à droite du 3
                                    slotDirs = new int[][] {{-1, 0}, {0, -1}, {0, 1}};
                                    extensionDirs = new int[][] {{+1,+2},{1,-2}};
                                    croixCoords = new int[][] {{-1, -1}, {-1, 1}};
                                } else if (dx0 == -2) { // 0 est à gauche du 3
                                    slotDirs = new int[][] {{1, 0}, {0, -1}, {0, 1}};
                                    extensionDirs = new int[][] {{-1,+2},{-1,-2}};
                                    croixCoords = new int[][] {{1, -1}, {1, 1}};
                                } else if (dy0 == 2) { // 0 est en bas du 3
                                    slotDirs = new int[][] {{0, -1}, {-1, 0}, {1, 0}};
                                    extensionDirs = new int[][] {{-2,1}, {2, 1}};
                                    croixCoords = new int[][] {{-1, -1}, {1, -1}};
                                } else { // 0 est en haut du 3
                                    slotDirs = new int[][] {{0, 1}, {-1, 0}, {1, 0}};
                                    extensionDirs = new int[][] {{-2,-1}, {2, -1}};
                                    croixCoords = new int[][] {{-1, 1}, {1, 1}};
                                }


                                // Placer bâtons autour du 3 sauf côté 0
                                for (int[] dir : slotDirs) {
                                    int sx = x + dir[0];
                                    int sy = y + dir[1];
                                    if (grille.estValide(sx, sy)) {
                                        Cellule c = grille.getCellule(sx, sy);
                                        if (c instanceof Slot) {
                                            ((Slot) c).setMarque(new Baton());
                                        }
                                    }
                                }

                                // Étendre la boucle avec des bâtons
                                for (int[] dir : extensionDirs) {
                                    int sx = x + dir[0];
                                    int sy = y + dir[1];
                                    if (grille.estValide(sx, sy)) {
                                        Cellule c = grille.getCellule(sx, sy);
                                        if (c instanceof Slot) {
                                            ((Slot) c).setMarque(new Baton());
                                        }
                                    }
                                }

                                // Placer les croix aux coins
                                for (int[] dir : croixCoords) {
                                    int cx = x + dir[0];
                                    int cy = y + dir[1];
                                    if (grille.estValide(cx, cy)) {
                                        Cellule c = grille.getCellule(cx, cy);
                                        if (c instanceof Slot) {
                                            ((Slot) c).setMarque(new Croix());
                                        }
                                    }
                                }

                                return; // Une seule application par détection
                            }
                        }
                    }
                }
            }
        }
    }
}