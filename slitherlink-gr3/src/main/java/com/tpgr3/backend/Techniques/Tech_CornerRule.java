package com.tpgr3.backend.Techniques;
import com.tpgr3.backend.Marque.*;
import com.tpgr3.backend.Case;
import com.tpgr3.backend.Cellule;
import com.tpgr3.backend.Grille;
import com.tpgr3.backend.Slot;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère les coins (valeurs 0..3 dans un coin).
 * Pour 0 ou 1 => place des croix,
 * pour 2 ou 3 => place des bâtons,
 * selon les coordonnées générées.
 * gère TOUS les coins possibles avant de s'arrêter.
 */
public class Tech_CornerRule implements Techniques {

    // Stocke le corner retenu pour appliquer la technique
    private int[] positionCase = null;
    private int caseValue = -1; // Valeur 0..3
    private List<int[]> coordsSlotsCible = new ArrayList<>();
    private Marque marqueCible = null;

    @Override
    public boolean estApplicable(Grille grille) {
        // Réinitialise l'état
        reset();

        // 1. Récupère tous les coins valables (0..3)
        List<int[]> corners = detecterTousLesCoins(grille);
        if (corners.isEmpty()) {
            System.out.println("Tech_CornerRule : Aucun coin (valeur 0..3) trouvé => non applicable.");
            return false;
        }

        // 2. Parcourir chaque coin, générer ses slots, vérifier s’ils sont déjà corrects
        for (int[] c : corners) {
            int x = c[0], y = c[1];
            int val = c[2]; // Valeur de la case

            // On construit la liste + marqueCible
            buildCoordinates(grille, x, y, val);

            // Vérifie si déjà marqués
            if (verifSlots(grille)) {
                // Ce coin est déjà correct => on l’ignore et on reset juste la liste
                coordsSlotsCible.clear();
                marqueCible = null;
                continue;
            }
            // Sinon => technique applicable sur ce coin
            positionCase = new int[]{x, y};
            caseValue = val;
            System.out.printf("Tech_CornerRule : coin (%d,%d) val=%d => technique applicable%n", x, y, val);
            return true; 
        }

        // 3. Si on a fini tous les coins => tous marqués => technique déjà appliquée
        System.out.println("Tech_CornerRule : tous les coins (0..3) sont déjà marqués => technique déjà appliquée.");
        return false;
    }

    @Override
    public void appliquer(Grille grille) {
        // Vérifie la technique
        if (!estApplicable(grille)) return;

        // Pose la marque sur tous les coordsSlotsCible
        for (int[] coord : coordsSlotsCible) {
            if (grille.estValide(coord[0], coord[1])) {
                Cellule cell = grille.getCellule(coord[0], coord[1]);
                if (cell instanceof Slot s) {
                    s.setMarque(marqueCible);
                }
            }
        }

        // Reset complet
        reset();
    }

    /**
     * Détecte tous les coins (x,y) où x,y est un coin logique
     * et la case a valeur dans [0..3].
     * @return liste [ {x,y,valCase} ... ]
     */
    private List<int[]> detecterTousLesCoins(Grille grille) {
        List<int[]> result = new ArrayList<>();
        int w = grille.getLargeur(), h = grille.getHauteur();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Cellule cell = grille.getCellule(x, y);
                if (cell instanceof Case c) {
                    int val = c.getValeur();
                    if (val >= 0 && val <= 3) {
                        String coinType = getTypeDeCoin(x, y, grille);
                        if (!coinType.equals("aucun")) {
                            result.add(new int[]{x, y, val});
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Construit coordsSlotsCible + marqueCible pour un coin (x,y) de valeur val
     */
    private void buildCoordinates(Grille grille, int x, int y, int val) {
        coordsSlotsCible.clear();

        // Détermine la marque cible : (0,1) => croix, (2,3) => bâton
        marqueCible = ((val == 0 || val == 1) ? new Croix() : new Baton());

        // Génére la liste de slots
        coordsSlotsCible.addAll(genererCoords(grille, x, y, val));
    }

    /**
     * Vérifie si tous les slots de coordsSlotsCible ont déjà la bonne marque (marqueCible).
     */
    private boolean verifSlots(Grille grille) {
        boolean allCorrect = true;
        System.out.println("\n-- Vérif Tech_CornerRule --");
        for (int[] c : coordsSlotsCible) {
            Cellule cell = grille.getCellule(c[0], c[1]);
            if (cell instanceof Slot s) {
                String actual = s.getMarque().getClass().getSimpleName();
                String expected = marqueCible.getClass().getSimpleName();
                System.out.printf("Coin : attend %s à (%d,%d) - actuel : %s%n", 
                                  expected, c[0], c[1], actual);
                if (!actual.equals(expected)) {
                    allCorrect = false;
                }
            } else {
                System.out.printf("Coin : (%d,%d) n'est pas un Slot => incorrect%n", c[0], c[1]);
                allCorrect = false;
            }
        }
        return allCorrect;
    }

    /**
     * Retourne la liste de coordonnées des slots à marquer selon la valeur + coin
     */
    private List<int[]> genererCoords(Grille grille, int x, int y, int val) {
        List<int[]> coords = new ArrayList<>();
        String coin = getTypeDeCoin(x, y, grille);

        switch (val) {
            case 0 -> {
                // 0 => place des croix dans 4 directions + 2 coords éloignées
                if (coin.equals("hautGauche")) {
                    coords.add(new int[]{x - 1, y});       // Gauche
                    coords.add(new int[]{x, y - 1});       // Haut
                    coords.add(new int[]{x + 1, y});       // Droite
                    coords.add(new int[]{x, y + 1});       // Bas
                    coords.add(new int[]{x - 1, y + 2});   // Croix éloignée gauche-bas
                    coords.add(new int[]{x + 2, y - 1});   // Croix éloignée droite-haut
                } else if (coin.equals("hautDroit")) {
                    coords.add(new int[]{x + 1, y});       
                    coords.add(new int[]{x, y - 1});       
                    coords.add(new int[]{x - 1, y});       
                    coords.add(new int[]{x, y + 1});       
                    coords.add(new int[]{x + 1, y + 2});   
                    coords.add(new int[]{x - 2, y - 1});   
                } else if (coin.equals("basGauche")) {
                    coords.add(new int[]{x - 1, y});       
                    coords.add(new int[]{x, y + 1});       
                    coords.add(new int[]{x + 1, y});       
                    coords.add(new int[]{x, y - 1});       
                    coords.add(new int[]{x + 2, y + 1});   
                    coords.add(new int[]{x - 1, y - 2});   
                } else if (coin.equals("basDroit")) {
                    coords.add(new int[]{x + 1, y});       
                    coords.add(new int[]{x, y + 1});       
                    coords.add(new int[]{x, y - 1});       
                    coords.add(new int[]{x - 1, y});       
                    coords.add(new int[]{x + 1, y - 2});   
                    coords.add(new int[]{x - 2, y + 1});   
                }
            }
            case 1 -> {
                // 1 => 2 coords
                if (coin.equals("hautGauche")) {
                    coords.add(new int[]{x - 1, y});
                    coords.add(new int[]{x, y - 1});
                } else if (coin.equals("hautDroit")) {
                    coords.add(new int[]{x + 1, y});
                    coords.add(new int[]{x, y - 1});
                } else if (coin.equals("basGauche")) {
                    coords.add(new int[]{x - 1, y});
                    coords.add(new int[]{x, y + 1});
                } else if (coin.equals("basDroit")) {
                    coords.add(new int[]{x + 1, y});
                    coords.add(new int[]{x, y + 1});
                }
            }
            case 2, 3 -> {
                // 2 => pose 2 bâtons / 3 => pose 2 bâtons,
                // Cf. ta logique si tu veux l'étendre
                // Place ici quelques coords comme baton
                if (coin.equals("hautGauche")) {
                    coords.add(new int[]{x + 1, y}); // etc.
                    coords.add(new int[]{x, y + 1});
                } else if (coin.equals("hautDroit")) {
                    coords.add(new int[]{x - 1, y});
                    coords.add(new int[]{x, y + 1});
                } else if (coin.equals("basGauche")) {
                    coords.add(new int[]{x + 1, y});
                    coords.add(new int[]{x, y - 1});
                } else if (coin.equals("basDroit")) {
                    coords.add(new int[]{x - 1, y});
                    coords.add(new int[]{x, y - 1});
                }
            }
        }

        // Filtrer => On ne garde que si c’est un Slot
        List<int[]> result = new ArrayList<>();
        for (int[] co : coords) {
            int sx = co[0], sy = co[1];
            if (grille.estValide(sx, sy) && grille.getCellule(sx, sy) instanceof Slot) {
                result.add(co);
            }
        }
        return result;
    }

    /**
     * Détermine si (x,y) est un coin (hautGauche, hautDroit, basGauche, basDroit).
     */
    private String getTypeDeCoin(int x, int y, Grille grille) {
        int[] dim = grille.getDimensionsLogiques();
        if (x == 1 && y == 1) return "hautGauche";
        if (x == dim[0] * 2 - 1 && y == 1) return "hautDroit";
        if (x == 1 && y == dim[1] * 2 - 1) return "basGauche";
        if (x == dim[0] * 2 - 1 && y == dim[1] * 2 - 1) return "basDroit";
        return "aucun";
    }

    private void reset() {
        positionCase = null;
        caseValue = -1;
        coordsSlotsCible.clear();
        marqueCible = null;
    }

    @Override
    public List<int[]> getSlotsToMark(Grille grille) {
        // Sauvegarder l'état
        int[] savePos = positionCase;
        int saveVal = caseValue;
        List<int[]> saveCoords = new ArrayList<>(coordsSlotsCible != null ? coordsSlotsCible : new ArrayList<>());
        Marque saveMarque = marqueCible;
        
        // Vérifier applicabilité
        if (!estApplicable(grille)) {
            // Restaurer et retourner vide
            positionCase = savePos;
            caseValue = saveVal;
            coordsSlotsCible = new ArrayList<>(saveCoords);
            marqueCible = saveMarque;
            return new ArrayList<>();
        }
        
        // Copier les résultats
        List<int[]> resultat = new ArrayList<>();
        for (int[] coord : coordsSlotsCible) {
            resultat.add(coord.clone());
        }
        
        // Restaurer l'état
        positionCase = savePos;
        caseValue = saveVal;
        coordsSlotsCible = new ArrayList<>(saveCoords);
        marqueCible = saveMarque;
        
        return resultat;
    }
}
