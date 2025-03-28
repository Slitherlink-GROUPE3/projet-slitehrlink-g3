package com.tpgr3.Techniques;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

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
    public boolean estApplicable(Grille grille) {
        // Récupère les dimensions de la grille
        int rows = gridNumbers.length;
        int cols = gridNumbers[0].length;
        
        // Vérifie les 4 coins de la grille
        int[][] cornerCoords = {
            {0, 0},             // Coin supérieur gauche
            {0, cols - 1},      // Coin supérieur droit
            {rows - 1, 0},      // Coin inférieur gauche
            {rows - 1, cols - 1} // Coin inférieur droit
        };
        
        for (int[] corner : cornerCoords) {
            int i = corner[0];
            int j = corner[1];
            
            if (gridNumbers[i][j] != -1) { // -1 indique une case vide
                int value = gridNumbers[i][j];
                
                // Pour les coins, les règles sont spécifiques selon la valeur
                if (value == 0 || value == 1 || value == 2) {
                    // Identifie les segments adjacents au coin
                    String[] adjacentSegments = new String[2];
                    
                    if (i == 0 && j == 0) { // Coin supérieur gauche
                        adjacentSegments[0] = "H_0_0"; // Haut
                        adjacentSegments[1] = "V_0_0"; // Gauche
                    } else if (i == 0 && j == cols - 1) { // Coin supérieur droit
                        adjacentSegments[0] = "H_0_" + j; // Haut
                        adjacentSegments[1] = "V_0_" + (j+1); // Droite
                    } else if (i == rows - 1 && j == 0) { // Coin inférieur gauche
                        adjacentSegments[0] = "H_" + (i+1) + "_0"; // Bas
                        adjacentSegments[1] = "V_" + i + "_0"; // Gauche
                    } else if(i == rows - 1 && j == cols - 1){ // Coin inférieur droit
                        adjacentSegments[0] = "H_" + (i+1) + "_" + j; // Bas
                        adjacentSegments[1] = "V_0_" + (j+1); // Droite
                    }
                    
                    // Vérifie si au moins un segment est neutre
                    int lineCount = 0;
                    int neutralCount = 0;
                    
                    for (String segmentKey : adjacentSegments) {
                        Line segment = gridLines.get(segmentKey);
                        if (segment != null) {
                            if (segment.getStroke() != Color.TRANSPARENT) {
                                lineCount++;
                            } else {
                                boolean hasCross = false;
                                for (javafx.scene.Node node : slitherlinkGrid.getChildren()) {
                                    if (node instanceof Line && node.getUserData() == segment) {
                                        hasCross = true;
                                        break;
                                    }
                                }
                                
                                if (!hasCross) {
                                    neutralCount++;
                                }
                            }
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
}
