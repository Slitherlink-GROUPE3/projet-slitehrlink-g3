package com.menu.javafx;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.*;

public class SlitherGridChecker {

    private SlitherGrid slitherGrid;
    
    SlitherGridChecker(SlitherGrid slitherGrid){
        this.slitherGrid = slitherGrid;
    }

    public void checkGridAutomatically() {
        if (slitherGrid.isHypothesisInactive()) {
            boolean isCorrect = checkGrid();
            if (isCorrect) {
                Util.showWinMessage();
                UserManager.setGridCompleted(GameScene.getCurrentGridId());
            }
        }
    }

    /**
     * Vérifie si la grille actuelle est correcte.
     * Retourne true si la solution est valide, false sinon.
     */
    public boolean checkGrid() {
        // Structure pour stocker toutes les lignes actives (segments)
        List<Line> activeLines = new ArrayList<>();

        // Récupérer toutes les lignes actives
        for (Line line : slitherGrid.gridLines.values()) {
            // Modification ici : vérifier si la ligne n'est pas transparente
            if (line.getStroke() != null && !line.getStroke().equals(Color.TRANSPARENT)) {
                activeLines.add(line);
            }
        }

        // Debug: Imprimer le nombre de lignes actives et leurs couleurs
        System.out.println("Nombre de lignes actives : " + activeLines.size());
        for (Line line : activeLines) {
            System.out.println("Ligne active : " + line.getId() + " - Couleur : " + line.getStroke());
        }

        // Vérifier les nombres de cellules
        boolean numbersCheck = checkNumbers();
        System.out.println("Vérification des nombres : " + numbersCheck);

        // Vérifier le circuit fermé
        boolean singleLoopCheck = checkSingleClosedLoop(activeLines);
        System.out.println("Vérification du circuit fermé : " + singleLoopCheck);

        // Vérifier la connexité des cellules
        boolean cellsCheck = areCellsFullyConnected(activeLines);
        System.out.println("Vérification des cellules : " + cellsCheck);

        // Debug: Vérifier chaque cellule individuellement
        for (int i = 0; i < slitherGrid.gridRows; i++) {
            for (int j = 0; j < slitherGrid.gridCols; j++) {
                int number = slitherGrid.gridNumbers[i][j];
                if (number != -1) {
                    int lineCount = countAdjacentLines(i, j);
                    System.out.println("Cellule [" + i + "," + j + "] : nombre attendu = " + number + ", lignes actuelles = " + lineCount);
                }
            }
        }

        // Retourne true seulement si toutes les conditions sont remplies
        return numbersCheck && singleLoopCheck && cellsCheck;
    }

    private boolean checkNumbers() {
        for (int i = 0; i < slitherGrid.gridRows; i++) {
            for (int j = 0; j < slitherGrid.gridCols; j++) {
                int number = slitherGrid.gridNumbers[i][j];
                if (number != -1) {
                    int lineCount = countAdjacentLines(i, j);
                    if (lineCount != number) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Compte le nombre de segments de ligne entourant une cellule spécifique.
     * Utilise les identifiants des lignes pour un accès direct.
     */
    private int countAdjacentLines(int row, int col) {
        int count = 0;

        // Vérifier la ligne du haut
        String topLineKey = "H_" + row + "_" + col;
        Line topLine = slitherGrid.gridLines.get(topLineKey);
        if (topLine != null && topLine.getStroke() != null && !topLine.getStroke().equals(Color.TRANSPARENT)) {
            count++;
            System.out.println("Ligne du haut [" + topLineKey + "] est active");
        }

        // Vérifier la ligne du bas
        String bottomLineKey = "H_" + (row + 1) + "_" + col;
        Line bottomLine = slitherGrid.gridLines.get(bottomLineKey);
        if (bottomLine != null && bottomLine.getStroke() != null && !bottomLine.getStroke().equals(Color.TRANSPARENT)) {
            count++;
            System.out.println("Ligne du bas [" + bottomLineKey + "] est active");
        }

        // Vérifier la ligne de gauche
        String leftLineKey = "V_" + row + "_" + col;
        Line leftLine = slitherGrid.gridLines.get(leftLineKey);
        if (leftLine != null && leftLine.getStroke() != null && !leftLine.getStroke().equals(Color.TRANSPARENT)) {
            count++;
            System.out.println("Ligne de gauche [" + leftLineKey + "] est active");
        }

        // Vérifier la ligne de droite
        String rightLineKey = "V_" + row + "_" + (col + 1);
        Line rightLine = slitherGrid.gridLines.get(rightLineKey);
        if (rightLine != null && rightLine.getStroke() != null && !rightLine.getStroke().equals(Color.TRANSPARENT)) {
            count++;
            System.out.println("Ligne de droite [" + rightLineKey + "] est active");
        }

        return count;
    }

    private boolean checkSingleClosedLoop(List<Line> activeLines) {
        if (activeLines.isEmpty()) {
            return false; // Pas de lignes tracées
        }

        // Crée une map des points de la grille et leurs connexions
        Map<Point, Set<Point>> adjacencyList = new HashMap<>();

        // Traite chaque ligne active et l'ajoute à la liste d'adjacence
        for (Line line : activeLines) {
            String lineId = line.getId();
            if (lineId == null)
                continue;

            String[] parts = lineId.split("_");
            if (parts.length != 3)
                continue;

            char type = parts[0].charAt(0);
            int i = Integer.parseInt(parts[1]);
            int j = Integer.parseInt(parts[2]);

            Point p1, p2;

            if (type == 'H') { // Ligne horizontale
                p1 = new Point(j, i);
                p2 = new Point(j + 1, i);
            } else { // Ligne verticale
                p1 = new Point(j, i);
                p2 = new Point(j, i + 1);
            }

            // Ajouter les connexions dans les deux sens
            adjacencyList.computeIfAbsent(p1, k -> new HashSet<>()).add(p2);
            adjacencyList.computeIfAbsent(p2, k -> new HashSet<>()).add(p1);
        }

        // Vérifier que chaque point a exactement 2 voisins (condition pour un circuit)
        for (Point p : adjacencyList.keySet()) {
            if (adjacencyList.get(p).size() != 2) {
                return false; // Un point a trop ou pas assez de connexions
            }
        }

        // Vérifier qu'il y a un seul circuit (composante connexe)
        if (adjacencyList.isEmpty()) {
            return false;
        }

        Set<Point> visited = new HashSet<>();
        Point start = adjacencyList.keySet().iterator().next(); // Prendre un point quelconque

        dfs(start, null, adjacencyList, visited);

        // Si tous les points n'ont pas été visités, il y a plusieurs circuits
        return visited.size() == adjacencyList.size();
    }

    /**
     * Parcours en profondeur du graphe pour vérifier la connexité
     */
    private void dfs(Point current, Point parent, Map<Point, Set<Point>> adjacencyList, Set<Point> visited) {
        visited.add(current);

        for (Point neighbor : adjacencyList.get(current)) {
            if (!neighbor.equals(parent) && !visited.contains(neighbor)) {
                dfs(neighbor, current, adjacencyList, visited);
            }
        }
    }

    private boolean areCellsFullyConnected(List<Line> activeLines) {
        for (int i = 0; i < slitherGrid.gridRows; i++) {
            for (int j = 0; j < slitherGrid.gridCols; j++) {
                int number = slitherGrid.gridNumbers[i][j];
                if (number != -1) {
                    int lineCount = countAdjacentLines(i, j);
                    if (lineCount != number) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /*
     * Getters & Setters
     */


}
