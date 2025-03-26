package com.menu.javafx;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.Map;

public class GameMatrix {

    private SlitherGrid slitherGrid;
    private int maxRows;
    private int maxCols;

    private int[][] gameMatrix; // Matrice représentant l'état complet du jeu
    private final int EMPTY = 0;      // Cellule vide ou segment vide
    private final int LINE = 1;       // Segment avec une ligne
    private final int CROSS = 2;      // Segment avec une croix
    private final int HYPOTHESIS = 3; // Segment avec une ligne en hypothèse
    private final int DOT = -1;       // Point de connexion


    public GameMatrix(int maxRows, int maxCols, int[][] gridNumbers, SlitherGrid slitherGrid){
        this.slitherGrid = slitherGrid;
        this.maxRows = maxRows;
        this.maxCols = maxCols;
        this.initializeGameMatrix( maxRows, maxCols, gridNumbers );
    }

    /*
     * Initialise la matrice du jeu avec une taille basée sur la grille actuelle
     * et la remplit avec les valeurs initiales.
     */
    private void initializeGameMatrix( int maxRows, int maxCols, int[][] gridNumbers ) {
        // Pour une grille avec gridRows x gridCols de cellules, on a besoin d'une matrice
        // de taille (2*gridRows+1) x (2*gridCols+1)
        int matrixRows = 2 * maxRows + 1;
        int matrixCols = 2 * maxCols + 1;
        gameMatrix = new int[matrixRows][matrixCols];

        // Initialisation de la matrice
        for (int i = 0; i < matrixRows; i++) {
            for (int j = 0; j < matrixCols; j++) {
                // Positions des points (coordonnées paires)
                if (i % 2 == 0 && j % 2 == 0) {
                    gameMatrix[i][j] = DOT;
                }
                // Positions des chiffres (coordonnées impaires)
                else if (i % 2 == 1 && j % 2 == 1) {
                    int gridRow = i / 2;
                    int gridCol = j / 2;
                    if (gridRow < maxRows && gridCol < maxCols) {
                        gameMatrix[i][j] = gridNumbers[gridRow][gridCol];
                    } else {
                        gameMatrix[i][j] = EMPTY;
                    }
                }
                // Positions des segments (une coordonnée paire, une impaire)
                else {
                    gameMatrix[i][j] = EMPTY;
                }
            }
        }
    }

    /**
     * Met à jour la matrice du jeu avec l'état actuel des lignes et des croix
     */
    public void updateGameMatrix( Map<String, Line> gridLines ) {
        // Parcourir toutes les lignes et mettre à jour la matrice
        for (Map.Entry<String, Line> entry : gridLines.entrySet()) {
            String lineKey = entry.getKey();
            Line line = entry.getValue();

            // Extraire les coordonnées et le type de la ligne
            String[] parts = lineKey.split("_");
            char type = parts[0].charAt(0);
            int i = Integer.parseInt(parts[1]);
            int j = Integer.parseInt(parts[2]);

            // Calculer la position dans la matrice
            int matrixRow = (type == 'H') ? 2 * i : 2 * i + 1;
            int matrixCol = (type == 'H') ? 2 * j + 1 : 2 * j;

            // Déterminer l'état de la ligne
            if (slitherGrid.isColorEqual((Color)line.getStroke(), Color.web(SlitherGrid.DARK_COLOR))) {
                gameMatrix[matrixRow][matrixCol] = LINE;
            } else if (slitherGrid.isColorEqual((Color)line.getStroke(), Color.web(SlitherGrid.LIGHT_COLOR))) {
                gameMatrix[matrixRow][matrixCol] = HYPOTHESIS;
            } else if (slitherGrid.hasCross(line)) {
                gameMatrix[matrixRow][matrixCol] = CROSS;
            } else {
                gameMatrix[matrixRow][matrixCol] = EMPTY;
            }
        }
    }

    /**
     * Crée et retourne une matrice simplifiée qui contient uniquement les chiffres
     * et l'état des segments autour de chaque cellule
     *
     * @return Une matrice 3D de taille gridRows x gridCols x 5 où chaque cellule contient:
     *         [0]: la valeur du chiffre dans la case (-1 si pas de chiffre)
     *         [1]: l'état du segment du haut
     *         [2]: l'état du segment de droite
     *         [3]: l'état du segment du bas
     *         [4]: l'état du segment de gauche
     *         où les états des segments sont:
     *           0: pas de segment
     *           1: segment (ligne)
     *           2: croix
     *           3: segment en mode hypothèse
     */
    public int[][][] getSimplifiedGameMatrix( Map<String, Line> gridLines ) {
        // Mise à jour de la matrice complète
        updateGameMatrix(gridLines);

        // Création de la matrice simplifiée
        int[][][] simplifiedMatrix = new int[maxRows][maxCols][5];

        // Parcourir chaque cellule de la grille
        for (int row = 0; row < maxRows; row++) {
            for (int col = 0; col < maxCols; col++) {
                // Position dans la matrice complète (coordonnées impaires pour les chiffres)
                int i = 2 * row + 1;
                int j = 2 * col + 1;

                // Récupérer le chiffre
                simplifiedMatrix[row][col][0] = gameMatrix[i][j];

                // État des segments autour
                simplifiedMatrix[row][col][1] = gameMatrix[i-1][j];    // segment du haut
                simplifiedMatrix[row][col][2] = gameMatrix[i][j+1];    // segment de droite
                simplifiedMatrix[row][col][3] = gameMatrix[i+1][j];    // segment du bas
                simplifiedMatrix[row][col][4] = gameMatrix[i][j-1];    // segment de gauche
            }
        }

        return simplifiedMatrix;
    }

    /**
     * Affiche la matrice simplifiée dans la console de manière visuelle
     */
    private void printSimplifiedGameMatrix( Map<String, Line> gridLines ) {
        int[][][] simplifiedMatrix = getSimplifiedGameMatrix(gridLines);

        System.out.println("\n==== ÉTAT ACTUEL DE LA GRILLE ====");
        System.out.println("Légende: [ ] vide, [─] ligne, [X] croix, [~] hypothèse, [·] valeur non définie");
        System.out.println();

        // Afficher la bordure supérieure
        System.out.print("    ");
        for (int j = 0; j < maxCols; j++) {
            System.out.print("+-----");
        }
        System.out.println("+");

        for (int i = 0; i < maxRows; i++) {
            // Ligne du haut avec les segments horizontaux supérieurs
            System.out.print("    ");
            for (int j = 0; j < maxCols; j++) {
                System.out.print("|  ");
                printSegment(simplifiedMatrix[i][j][1]); // segment du haut
                System.out.print("  ");
            }
            System.out.println("|");

            // Ligne du milieu avec les segments verticaux et la valeur
            System.out.printf(" %2d ", i);
            for (int j = 0; j < maxCols; j++) {
                // Segment gauche
                System.out.print(j == 0 ? "|" : " ");
                printSegment(simplifiedMatrix[i][j][4]); // segment de gauche

                // Valeur centrale
                int value = simplifiedMatrix[i][j][0];
                if (value == -1) {
                    System.out.print(" · ");
                } else {
                    System.out.printf(" %d ", value);
                }

                // Segment droit
                printSegment(simplifiedMatrix[i][j][2]); // segment de droite
            }
            System.out.println("|");

            // Ligne du bas avec les segments horizontaux inférieurs
            System.out.print("    ");
            for (int j = 0; j < maxCols; j++) {
                System.out.print("|  ");
                printSegment(simplifiedMatrix[i][j][3]); // segment du bas
                System.out.print("  ");
            }
            System.out.println("|");

            // Séparateur horizontal entre les lignes
            System.out.print("    ");
            for (int j = 0; j < maxCols; j++) {
                System.out.print("+-----");
            }
            System.out.println("+");
        }

        System.out.println();
    }

    /**
     * Affiche un caractère représentant l'état d'un segment
     */
    private void printSegment(int segmentState) {
        switch (segmentState) {
            case EMPTY:
                System.out.print(" "); // Espace pour segment vide
                break;
            case LINE:
                System.out.print("─"); // Tiret pour ligne
                break;
            case CROSS:
                System.out.print("X"); // X pour croix
                break;
            case HYPOTHESIS:
                System.out.print("~"); // Tilde pour hypothèse
                break;
            default:
                System.out.print("?"); // Point d'interrogation pour valeur inattendue
        }
    }

}
