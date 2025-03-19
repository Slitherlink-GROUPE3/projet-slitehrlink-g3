package com.tpgr3.Techniques;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

import java.util.ArrayList;
import java.util.List;

public class Tech_1Et0Coin implements Techniques {

    private int[] position1 = null; // Position de la case 1
    private int[] position0 = null; // Position de la case 0

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cellule = grille.getCellule(x, y);

                if (cellule instanceof Case && ((Case) cellule).getValeur() == 1 && grille.estDansUnCoin(x, y)) {
                    String coin = getTypeDeCoin(x, y, grille);

                    int[][] directions = getDirectionsPourZero(coin);

                    for (int[] dir : directions) {
                        int x0 = x + dir[0];
                        int y0 = y + dir[1];
                        if (grille.estValide(x0, y0)) {
                            Cellule c0 = grille.getCellule(x0, y0);
                            if (c0 instanceof Case && ((Case) c0).getValeur() == 0) {
                            if (slotsSontNeutres(grille, x, y) && slotsSontNeutres(grille, x0, y0)) {
                                System.out.println("0 trouvé à (" + x0 + "," + y0 + ") par rapport à 1 à (" + x + "," + y + ")");
                                System.out.println("dx = " + (x0 - x) + " , dy = " + (y0 - y));
                                position1 = new int[]{x, y};
                                position0 = new int[]{x0, y0};
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

    @Override
    public void appliquer(Grille grille) {
        if (!estApplicable(grille)) return;
    
        int x1 = position1[0];
        int y1 = position1[1];
        int x0 = position0[0];
        int y0 = position0[1];
    
        String coin = getTypeDeCoin(x1, y1, grille);
        System.out.println("Tech_1Et0Coin - Case 1 en coin : " + coin);
        System.out.println("Position 1 : (" + x1 + "," + y1 + ")");
        System.out.println("Position 0 : (" + x0 + "," + y0 + ")");
    
        // 🔻 Croix autour du 0
        List<Slot> slots0 = grille.getSlotsAdjacentsCase(x0, y0);
        for (Slot slot : slots0) {
            slot.setMarque(new Croix());
        }
    
        // 🔻 Déduire direction du 0 par rapport au 1
        int dx = x0 - x1;
        int dy = y0 - y1;
    
        // 🔻 Coords des croix à poser autour du 1
        int[][] croixCoords = new int[2][2];
    
        if (coin.equals("hautGauche")) {
            croixCoords[0] = new int[]{x1 - 1, y1}; // gauche
            croixCoords[1] = new int[]{x1, y1 - 1}; // haut
        } else if (coin.equals("hautDroit")) {
            croixCoords[0] = new int[]{x1 + 1, y1}; // droite
            croixCoords[1] = new int[]{x1, y1 - 1}; // haut
        } else if (coin.equals("basGauche")) {
            croixCoords[0] = new int[]{x1 - 1, y1}; // gauche
            croixCoords[1] = new int[]{x1, y1 + 1}; // bas
        } else if (coin.equals("basDroit")) {
            croixCoords[0] = new int[]{x1 + 1, y1}; // droite
            croixCoords[1] = new int[]{x1, y1 + 1}; // bas
        }
    
        // 🔻 Poser croix autour du 1
        for (int[] coord : croixCoords) {
            int cx = coord[0], cy = coord[1];
            if (grille.estValide(cx, cy)) {
                Cellule c = grille.getCellule(cx, cy);
                if (c instanceof Slot) {
                    ((Slot) c).setMarque(new Croix());
                }
            }
        }
    
        position1 = null;
        position0 = null;
    }
    

    // Vérifie que tous les slots autour sont neutres
    private boolean slotsSontNeutres(Grille grille, int x, int y) {
        List<Slot> slots = grille.getSlotsAdjacentsCase(x, y);
        for (Slot slot : slots) {
            if (!(slot.getMarque() instanceof Neutre)) return false;
        }
        return true;
    }

    // Détermine le type de coin selon position
    private String getTypeDeCoin(int x, int y, Grille grille) {
        int[] dim = grille.getDimensionsLogiques();
        if (x == 1 && y == 1) return "hautGauche";
        if (x == dim[0] * 2 - 1 && y == 1) return "hautDroit";
        if (x == 1 && y == dim[1] * 2 - 1) return "basGauche";
        if (x == dim[0] * 2 - 1 && y == dim[1] * 2 - 1) return "basDroit";
        return "aucun";
    }

    // Positions possibles du 0 autour du 1 selon le coin
    private int[][] getDirectionsPourZero(String coin) {
        switch (coin) {
            case "hautGauche": return new int[][]{{2, 0}, {0, 2}};   // droite ou bas
            case "hautDroit":  return new int[][]{{-2, 0}, {0, 2}};  // gauche ou bas
            case "basGauche":  return new int[][]{{2, 0}, {0, -2}};  // droite ou haut
            case "basDroit":   return new int[][]{{-2, 0}, {0, -2}}; // gauche ou haut
        }
        return new int[0][0];
    }

    // Donne les directions pour croix selon position du 0 par rapport au 1
    private List<int[]> getCroixDirections(int dx, int dy) {
        List<int[]> dirs = new ArrayList<>();
        if (dx == 2) { // 0 à droite
            dirs.add(new int[]{0, -1}); // haut
            dirs.add(new int[]{0, 1});  // bas
        } else if (dx == -2) { // 0 à gauche
            dirs.add(new int[]{0, -1});
            dirs.add(new int[]{0, 1});
        } else if (dy == 2) { // 0 en bas
            dirs.add(new int[]{-1, 0});
            dirs.add(new int[]{1, 0});
        } else if (dy == -2) { // 0 en haut
            dirs.add(new int[]{-1, 0});
            dirs.add(new int[]{1, 0});
        }
        return dirs;
    }
}
