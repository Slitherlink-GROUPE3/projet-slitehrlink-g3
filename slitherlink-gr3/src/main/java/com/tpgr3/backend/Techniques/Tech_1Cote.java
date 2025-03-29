package com.tpgr3.backend.Techniques;
import com.tpgr3.backend.Marque.*;
import com.tpgr3.backend.Case;
import com.tpgr3.backend.Cellule;
import com.tpgr3.backend.Grille;
import com.tpgr3.backend.Slot;

import java.util.ArrayList;
import java.util.List;

/**
 * Technique : 
 * Si un (1) est sur un bord, on place 2 croix et 1 bâton 
 * selon les offsets spécifiés pour ce bord.
 */
public class Tech_1Cote implements Techniques {

    private int[] position1 = null;       // (x,y) du 1 détecté sur le bord
    private String bord = null;           // haut, bas, gauche, droite ?

    // Slots où placer des croix ou un bâton
    private List<int[]> slotsCroix = new ArrayList<>();
    private List<int[]> slotBaton = new ArrayList<>();

    @Override
    public boolean estApplicable(Grille grille) {
        // Réinitialise
        reset();

        // 1. Cherche un (1) sur un bord
        if (!detecterConfiguration(grille)) {
            return false;  // rien trouvé
        }

        // 2. Vérifie si tout est déjà en place
        boolean tousCorrects = true;

        // Vérification des croix
        for (int[] c : slotsCroix) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (!(cell instanceof Slot s) || !(s.getMarque() instanceof Croix)) {
                tousCorrects = false;
                break;
            }
        }
        // Vérification du bâton
        for (int[] c : slotBaton) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (!(cell instanceof Slot s) || !(s.getMarque() instanceof Baton)) {
                tousCorrects = false;
                break;
            }
        }

        if (tousCorrects) {
            System.out.println("Tech_1Cote : déjà appliquée (tout est déjà correct).");
            reset();
            return false;
        }

        return true;
    }

    @Override
    public void appliquer(Grille grille) {
        if (!estApplicable(grille)) return;

        // Place les croix
        for (int[] c : slotsCroix) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (cell instanceof Slot s) {
                s.setMarque(new Croix());
            }
        }

        // Place le bâton
        for (int[] c : slotBaton) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (cell instanceof Slot s) {
                s.setMarque(new Baton());
            }
        }

        reset();
    }

    /**
     * Détecte un 1 sur l'un des bords (haut/bas/gauche/droite).
     * Stocke la position et construit la liste de slots.
     */
    private boolean detecterConfiguration(Grille grille) {
        int w = grille.getLargeur(), h = grille.getHauteur();

        // On scanne la grille pour un Case valant 1
        for (int y = 1; y < h; y += 2) {
            for (int x = 1; x < w; x += 2) {
                Cellule cell = grille.getCellule(x, y);
                if (cell instanceof Case c1 && c1.getValeur() == 1) {
                    // Identifie le bord
                    String b = detectBord(x, y, grille);
                    if (!b.equals("aucun")) {
                        // Trouvé un 1 sur ce bord => on retient
                        position1 = new int[]{x, y};
                        bord = b;

                        // Construit les offsets
                        buildCoordinates();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Selon le bord, on place 2 croix et 1 bâton 
     * d'après les offsets spécifiés dans la question.
     */
    private void buildCoordinates() {
        slotsCroix.clear();
        slotBaton.clear();

        int x1 = position1[0];
        int y1 = position1[1];

        // Offsets précisés dans la question
        // bord haut => cross: (x-1,y), (x,y+1), baton: (x+2,y+2)
        // bord bas => cross: (x-1,y), (x,y-1), baton: (x+2,y-2)
        // bord droit => cross: (x,y-1), (x-1,y), baton: (x+2,y+1)? 
        // bord gauche => cross: (x,y+1), (x+1,y), baton: (x-2,y-1)?

        if (bord.equals("haut")) {
            // 2 croix
            addIfSlot(slotsCroix, x1 - 1, y1);
            addIfSlot(slotsCroix, x1, y1 + 1);
            // 1 bâton
            addIfSlot(slotBaton, x1 + 2, y1 - 1 );

        } else if (bord.equals("bas")) {
            addIfSlot(slotsCroix, x1 - 1, y1);
            addIfSlot(slotsCroix, x1, y1 - 1);
            addIfSlot(slotBaton, x1 + 2, y1 - 1);

        } else if (bord.equals("droite")) {
            addIfSlot(slotsCroix, x1, y1 - 1);
            addIfSlot(slotsCroix, x1 - 1, y1);
            addIfSlot(slotBaton, x1 + 2, y1 + 1);

        } else if (bord.equals("gauche")) {
            addIfSlot(slotsCroix, x1, y1 + 1);
            addIfSlot(slotsCroix, x1 + 1, y1);
            addIfSlot(slotBaton, x1 - 2, y1 + 3);
        }
    }

    /**
     * Détermine le bord (haut, bas, gauche, droite) d'un (x,y).
     */
    private String detectBord(int x, int y, Grille g) {
        int w = g.getLargeur();
        int h = g.getHauteur();

        if (y == 1) return "haut";
        if (y == h - 2) return "bas";
        if (x == 1) return "gauche";
        if (x == w - 2) return "droite";
        return "aucun";
    }

    /**
     * Ajoute la coord si c'est un slot valide.
     */
    private void addIfSlot(List<int[]> list, int xx, int yy) {
        // On ne fait rien si c'est hors grille ou pas un Slot
        // Sinon on ajoute
        list.add(new int[]{xx, yy});
    }

    private void reset() {
        position1 = null;
        bord = null;
        slotsCroix.clear();
        slotBaton.clear();
    }

    @Override
    public List<int[]> getSlotsToMark(Grille grille) {
        // Sauvegarder l'état
        int[] savePos1 = position1;
        String saveBord = bord;
        List<int[]> saveCroix = new ArrayList<>(slotsCroix != null ? slotsCroix : new ArrayList<>());
        List<int[]> saveBaton = new ArrayList<>(slotBaton != null ? slotBaton : new ArrayList<>());
        
        // Vérifier applicabilité
        if (!estApplicable(grille)) {
            // Restaurer et retourner vide
            position1 = savePos1;
            bord = saveBord;
            slotsCroix = new ArrayList<>(saveCroix);
            slotBaton = new ArrayList<>(saveBaton);
            return new ArrayList<>();
        }
        
        // Copier les résultats
        List<int[]> resultat = new ArrayList<>();
        for (int[] coord : slotsCroix) {
            resultat.add(coord.clone());
        }
        for (int[] coord : slotBaton) {
            resultat.add(coord.clone());
        }
        
        // Restaurer l'état
        position1 = savePos1;
        bord = saveBord;
        slotsCroix = new ArrayList<>(saveCroix);
        slotBaton = new ArrayList<>(saveBaton);
        
        return resultat;
    }
}
