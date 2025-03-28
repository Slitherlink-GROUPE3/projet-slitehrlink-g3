// Common imports for Technique classes
package com.tpgr3.Techniques;

import java.util.Map;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import com.tpgr3.*;
import com.tpgr3.Marque.*;

public class Tech_LoopReaching1 implements Techniques {

    private int[] position1 = null; // Position de la case 1
    private int[] directionBaton = null; // Direction du bâton adjacent

    @Override
    public boolean estApplicable(Grille grille) {
        for (int y = 0; y < grille.getHauteur(); y++) {
            for (int x = 0; x < grille.getLargeur(); x++) {
                Cellule cellule = grille.getCellule(x, y);

                if (cellule instanceof Case && ((Case) cellule).getValeur() == 1) {
                    // Directions orthogonales : droite, gauche, bas, haut
                    int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

                    for (int[] dir : directions) {
                        int sx = x + dir[0];
                        int sy = y + dir[1];

                        if (grille.estValide(sx, sy)) {
                            Cellule c = grille.getCellule(sx, sy);
                            if (c instanceof Slot && ((Slot) c).getMarque() instanceof Baton) {
                                position1 = new int[]{x, y};
                                directionBaton = dir; // Enregistre la direction du bâton
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

        int x = position1[0];
        int y = position1[1];
        int dx = directionBaton[0];
        int dy = directionBaton[1];

        // Crois à poser perpendiculairement au bâton détecté
        int[][] croixCoords = new int[2][2];

        if (dx != 0) { // Bâton horizontal -> croix en haut et bas
            croixCoords[0] = new int[]{x, y - 1}; // Haut
            croixCoords[1] = new int[]{x, y + 1}; // Bas
        } else { // Bâton vertical -> croix à gauche et droite
            croixCoords[0] = new int[]{x - 1, y}; // Gauche
            croixCoords[1] = new int[]{x + 1, y}; // Droite
        }

        // Pose des croix
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
        directionBaton = null;
    }
}
