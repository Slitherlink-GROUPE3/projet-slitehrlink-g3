// Common imports for Technique classes
package com.tpgr3.Techniques;

import java.util.Map;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

public class Tech_LoopReaching3 implements Techniques {

    private int[] position3 = null; // Position de la case 3
    private int[] directionDiag = null; // Direction diagonale du bâton trouvé

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cellule = grille.getCellule(x, y);

                if (cellule instanceof Case && ((Case) cellule).getValeur() == 3) {
                    // Diagonales à distance spécifique
                    int[][] diagonales = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

                    for (int[] dir : diagonales) {
                        int sx = x + dir[0];
                        int sy = y + dir[1];

                        if (grille.estValide(sx, sy)) {
                            Cellule c = grille.getCellule(sx, sy);
                            if (c instanceof Slot && ((Slot) c).getMarque() instanceof Baton) {
                                position3 = new int[]{x, y};
                                directionDiag = dir; // Stocke la direction du bâton détecté
                                return true;
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
    
        int x = position3[0];
        int y = position3[1];
        int dx = directionDiag[0];
        int dy = directionDiag[1];
    
        int[][] batonCoords = new int[2][2];
        int[] croixCoord = new int[2];
    
        // 🔍 Déduction des placements selon la diagonale
        if (dx == 1 && dy == 2) { // Bas-Droite
            batonCoords[0] = new int[]{x, y - 1};     // Bas
            batonCoords[1] = new int[]{x - 1, y};     // Gauche
            croixCoord = new int[]{x + 2, y + 1};     // Droite éloignée
        } else if (dx == 1 && dy == -2) { // Haut-Droite
            batonCoords[0] = new int[]{x, y + 1};     // Haut
            batonCoords[1] = new int[]{x - 1, y};     // Gauche
            croixCoord = new int[]{x + 2, y - 1};     // Droite éloignée
        } else if (dx == -1 && dy == 2) { // Bas-Gauche
            batonCoords[0] = new int[]{x, y - 1};     // Bas
            batonCoords[1] = new int[]{x + 1, y};     // Droite
            croixCoord = new int[]{x - 2, y + 1};     // Gauche éloignée
        } else if (dx == -1 && dy == -2) { // Haut-Gauche
            batonCoords[0] = new int[]{x, y + 1};     // Haut
            batonCoords[1] = new int[]{x + 1, y};     // Droite
            croixCoord = new int[]{x - 2, y - 1};     // Gauche éloignée
        }
    
        // ✅ Placer les bâtons
        for (int[] coord : batonCoords) {
            int bx = coord[0], by = coord[1];
            if (grille.estValide(bx, by)) {
                Cellule c = grille.getCellule(bx, by);
                if (c instanceof Slot) {
                    ((Slot) c).setMarque(new Baton());
                }
            }
        }
    
        // ❌ Placer la croix
        int cx = croixCoord[0], cy = croixCoord[1];
        if (grille.estValide(cx, cy)) {
            Cellule c = grille.getCellule(cx, cy);
            if (c instanceof Slot) {
                ((Slot) c).setMarque(new Croix());
            }
        }
    
        // Reset pour prochaine détection
        position3 = null;
        directionDiag = null;
    }
}
