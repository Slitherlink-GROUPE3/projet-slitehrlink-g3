package com.tpgr3.Techniques;

import com.tpgr3.*;
import com.tpgr3.Marque.*;
import java.util.*;

public class Tech_3Et0Cote implements Techniques {

    private int[] position3 = null, position0 = null, positionMilieu = null;
    private String bord = null;
    private boolean inverse = false;

    // Coordonnées des slots affectés
    private List<int[]> slotsCroix = new ArrayList<>();
    private List<int[]> slotsBaton = new ArrayList<>();

    @Override
    public boolean estApplicable(Grille grille) {
        if (!detecterConfiguration(grille)) return false;

        // Vérifie que tous les slots sont déjà bien marqués
        boolean tousCorrects = true;
        for (int[] c : slotsCroix) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (!(cell instanceof Slot s) || !(s.getMarque() instanceof Croix)) {
                tousCorrects = false; break;
            }
        }
        for (int[] c : slotsBaton) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (!(cell instanceof Slot s) || !(s.getMarque() instanceof Baton)) {
                tousCorrects = false; break;
            }
        }

        if (tousCorrects) {
            System.out.println("Technique déjà appliquée, slots déjà marqués.");
            reset(); return false;
        }

        return true;
    }

    @Override
    public void appliquer(Grille grille) {
        if (!estApplicable(grille)) return;

        for (int[] c : slotsCroix) {
            ((Slot) grille.getCellule(c[0], c[1])).setMarque(new Croix());
        }
        for (int[] c : slotsBaton) {
            ((Slot) grille.getCellule(c[0], c[1])).setMarque(new Baton());
        }

        reset();
    }

    /*
     * Detecte la configuration 3 et 0 cote
     */
    private boolean detecterConfiguration(Grille grille) {
        int largeur = grille.getLargeur(), hauteur = grille.getHauteur();
        for (int y = 1; y < hauteur; y += 2) {
            for (int x = 1; x < largeur; x += 2) {
                if (!(grille.getCellule(x, y) instanceof Case)) continue;
                if (x + 4 < largeur && verif(grille, x, y, x + 4, y)) return true;
                if (x - 4 >= 0 && verif(grille, x, y, x - 4, y)) return true;
                if (y + 4 < hauteur && verif(grille, x, y, x, y + 4)) return true;
                if (y - 4 >= 0 && verif(grille, x, y, x, y - 4)) return true;
            }
        }
        return false;
    }

    private boolean verif(Grille g, int xA, int yA, int xB, int yB) {
        slotsCroix.clear(); slotsBaton.clear(); inverse = false;

        Cellule cA = g.getCellule(xA, yA), cB = g.getCellule(xB, yB);
        if (cA instanceof Case ca && ca.getValeur() == 3 &&
            cB instanceof Case cb && cb.getValeur() == 0) {
            position3 = new int[]{xA, yA}; position0 = new int[]{xB, yB};
        } else if (cA instanceof Case ca2 && ca2.getValeur() == 0 &&
                   cB instanceof Case cb2 && cb2.getValeur() == 3) {
            position3 = new int[]{xB, yB}; position0 = new int[]{xA, yA}; inverse = true;
        } else return false;

        int dx = (position0[0] - position3[0]) / 2, dy = (position0[1] - position3[1]) / 2;
        int xMid = position3[0] + dx, yMid = position3[1] + dy;
        Cellule cMid = g.getCellule(xMid, yMid);

        if (!(cMid instanceof Case cm && cm.getValeur() < 0)) return false;

        bord = detectBord(position3[0], position3[1], g);
        positionMilieu = new int[]{xMid, yMid};

        // Slots croix autour du 0
        for (Slot s : g.getSlotsAdjacentsCase(position0[0], position0[1])) {
            slotsCroix.add(new int[]{s.getX(), s.getY()});
        }

        // Coordonnées relative au 3 et milieu
        Map<String, int[][]> coordMap = Map.of(
            "haut",   new int[][]{{-1, 0}, {0, -1}, {0, -1}},
            "bas",    new int[][]{{1, 0}, {0, 1}, {0, 1}},
            "gauche", new int[][]{{0, -1}, {1, 0}, {1, 0}},
            "droite", new int[][]{{0, 1}, {-1, 0}, {-1, 0}}
        );
        int[][] coords = coordMap.getOrDefault(bord, new int[][]{{0,0},{0,0},{0,0}});

        for (int i = 0; i < 2; i++) {
            int dxB = coords[i][0], dyB = coords[i][1];
            if (inverse) { dxB = -dxB; dyB = -dyB; }
            slotsBaton.add(new int[]{position3[0] + dxB, position3[1] + dyB});
        }

        int[] croixInter = {positionMilieu[0] + coords[2][0], positionMilieu[1] + coords[2][1]};
        slotsCroix.add(croixInter);

        return true;
    }

    /*
     * Detecte le bord de la grille 
     * @return le bord détecté
     * 
     */
    private String detectBord(int x, int y, Grille g) {
        int w = g.getLargeur(), h = g.getHauteur();
        if (y == 1) return "haut";
        if (y == h - 2) return "bas";
        if (x == 1) return "gauche";
        if (x == w - 2) return "droite";
        return "aucun";
    }

    private void reset() {
        position3 = null; position0 = null; positionMilieu = null; bord = null; inverse = false;
        slotsCroix.clear(); slotsBaton.clear();
    }
}
