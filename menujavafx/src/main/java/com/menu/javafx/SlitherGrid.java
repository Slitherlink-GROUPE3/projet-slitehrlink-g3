package com.menu.javafx;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlitherGrid {

    public static String MAIN_COLOR;
    public static String SECONDARY_COLOR;
    public static String ACCENT_COLOR;
    public static String DARK_COLOR;
    public static String LIGHT_COLOR;

    private static double CELL_SIZE;

    private Pane slitherlinkGrid;
    private List<Move> moveHistory = new ArrayList<>();
    private int currentMoveIndex = -1;
    private boolean isHypothesisActive = false;

    private Map<Line, Color> originalLineStates = new HashMap<>();
    private static Map<String, Color> savedLineStates = new HashMap<>();
    private static Map<String, Boolean> savedCrossStates = new HashMap<>();

    private Button prevButton;
    private Button nextButton;

    protected Map<String, Line> gridLines = new HashMap<>();
    protected int[][] gridNumbers;

    protected int gridCols;
    protected int gridRows;

    private SlitherGridChecker slitherGridChecker = new SlitherGridChecker(this);
    private GameMatrix gameMatrix;

    public SlitherGrid(int[][] gridNumbers) {
        this.gridNumbers = gridNumbers;
        gridRows = gridNumbers.length;
        gridCols = gridNumbers[0].length;
        prevButton = Util.createNavigationButton("←", MAIN_COLOR, DARK_COLOR, SECONDARY_COLOR);
        prevButton.setDisable(true);
        prevButton.setOnAction(e -> {
            Util.animateButtonClick(prevButton);
            navigateHistory(-1);
        });

        nextButton = Util.createNavigationButton("→", MAIN_COLOR, DARK_COLOR, SECONDARY_COLOR);
        nextButton.setDisable(true);
        nextButton.setOnAction(e -> {
            Util.animateButtonClick(nextButton);
            navigateHistory(1);
        });

        gameMatrix = new GameMatrix(gridRows, gridCols, gridNumbers, this);

    }

    private void undoMove(Move move) {
        if (move == null)
            return;

        move.undoMove();
    }

    private static void redoMove(Move move) {
        if (move == null)
            return;
        move.redoMove();
    }

    public void addMove(Move move) {
        if (currentMoveIndex < moveHistory.size() - 1) {
            moveHistory = new ArrayList<>(moveHistory.subList(0, currentMoveIndex + 1));
        }

        moveHistory.add(move);
        currentMoveIndex++;
        updateHistoryButtons();
    }

    public void reset() {
        slitherlinkGrid.getChildren().clear();
        originalLineStates.clear();
        savedLineStates.clear();
        savedCrossStates.clear();

        // Réinitialise le mode hypothèse
        isHypothesisActive = false;

        // Réinitialiser l'historique des mouvements
        moveHistory.clear();
        currentMoveIndex = -1;
    }

    public void updateHistoryButtons() {
        prevButton.setDisable(currentMoveIndex == -1);
        nextButton.setDisable(currentMoveIndex >= moveHistory.size() - 1);
    }

    public void prepareHypothesis() {
        originalLineStates.clear();
        for (Node node : slitherlinkGrid.getChildren()) {
            if (node instanceof Line line) {
                if (line.getUserData() == null) {
                    originalLineStates.put(line, (Color) line.getStroke());
                }
            }
        }

        isHypothesisActive = true;
    }

    private void removeHypothesisFromHistory(){
        moveHistory = moveHistory
                .stream()
                .map(move -> {
                    if( move instanceof LineMove && isColorEqual(move.getColor(), Color.web(LIGHT_COLOR)) ) currentMoveIndex--;
                    return move;
                } )
                .filter( move -> ( move instanceof LineMove && !isColorEqual(move.getColor(), Color.web(LIGHT_COLOR)) ) )
                .peek(System.out::println)
                .collect(Collectors.toList());
    }

    public void cancelHypothesis(){
        removeHypothesisFromHistory();
        System.out.println("moveHistory = " + moveHistory);
        for (Map.Entry<Line, Color> entry : originalLineStates.entrySet()) {
            entry.getKey().setStroke(entry.getValue());
        }
        isHypothesisActive = false;
        originalLineStates.clear();
    }

    public void confirmerHypothesis() {
        boolean anyChanges = false;
        int counter = 0;
        for (Line line : originalLineStates.keySet()) {

            // Vérifier si la ligne est en vert clair (hypothèse) et la transformer en vert
            // foncé (confirmée)
            if (isColorEqual( (Color) line.getStroke(), Color.web(LIGHT_COLOR))) {
                line.setStroke(Color.web(DARK_COLOR));
                anyChanges = true;
            }

            this.moveHistory = this.moveHistory
                    .stream()
                    .map(
                            move -> {
                                if(move instanceof LineMove && isColorEqual(move.getColor(), Color.web(LIGHT_COLOR)))
                                    move.setColor( Color.web(DARK_COLOR) );
                                return move;
                            }
                    )
                    .collect(Collectors.toList());

        }

/*
        for (Node node : slitherlinkGrid.getChildren()) {
            if (node instanceof Line && "hypothesis".equals(node.getUserData())) {
                Line crossLine = (Line) node;
                crossLine.setStroke(Color.web(ACCENT_COLOR));
                crossLine.setUserData(null);
                anyChanges = true;
            }
        }
*/

        if (anyChanges) {
            addMove(new HypothesisConfirmMove(null, "hypothesis_confirm", null, this));
        }
        isHypothesisActive = false;
        originalLineStates.clear();

        // Vérifier automatiquement après confirmation d'hypothèse
        slitherGridChecker.checkGridAutomatically();
    }

    public void handleLineClick(MouseEvent e, Line line) {
        if (e.getButton() == MouseButton.PRIMARY) {
            if (!hasCross(line)) { // Vérifie s'il y a une croix
                toggleLine(line);

                // Si ce n'est pas en mode hypothèse, enregistre le mouvement dans l'historique
                if (isHypothesisInactive() && !line.getStroke().equals(Color.TRANSPARENT)) {

                    // Vérifier automatiquement si la grille est correcte
                    slitherGridChecker.checkGridAutomatically();
                }
                addMove(new LineMove(line, "line", (Color) line.getStroke(), this));
            }
        } else if (e.getButton() == MouseButton.SECONDARY) {
            if (!isLineActive(line)) { // Vérifie si une ligne est déjà tracée
                // Vérifie s'il y a déjà une croix
                boolean hasCrossAlready = hasCross(line);

                // Si ce n'est pas en mode hypothèse, enregistre le mouvement dans l'historique
                if (this.isHypothesisInactive()) {
                    // Supprime les mouvements futurs si on était revenu en arrière
                    if (currentMoveIndex < moveHistory.size() - 1) {
                        moveHistory = new ArrayList<>(moveHistory.subList(0, currentMoveIndex + 1));
                    }

                    // Ajoute le nouveau mouvement
                    if (hasCrossAlready) {
                        moveHistory.add(new RemoveCrossMove(line, "remove_cross", null, this));
                    } else {
                        moveHistory.add(new CreateCrossMove(line, "cross", Color.web(ACCENT_COLOR), this));
                    }
                    currentMoveIndex++;
                    updateHistoryButtons();
                }
            }
        }

    }

    public void navigateHistory(int direction) {
        if (direction < 0 && currentMoveIndex > -1) {
            Move moveToUndo = moveHistory.get(currentMoveIndex);
            undoMove(moveToUndo);
            currentMoveIndex--;
        } else if (direction > 0 && currentMoveIndex < moveHistory.size() - 1) {
            currentMoveIndex++;
            Move moveToRedo = moveHistory.get(currentMoveIndex);
            redoMove(moveToRedo);
        }

        updateHistoryButtons();
    }

    // Méthode pour reconstruire l'historique avec les nouvelles instances de Line
    private void rebuildHistory(List<Move> oldHistory, int oldIndex) {
        moveHistory.clear();

        // Pour chaque mouvement dans l'ancien historique
        for (Move oldMove : oldHistory) {
            if (oldMove.line() != null) {
                // Trouver la nouvelle instance de la ligne correspondante
                Line oldLine = oldMove.line();
                String lineId = oldLine.getId();

                if (lineId != null && gridLines.containsKey(lineId)) {
                    Line newLine = gridLines.get(lineId);
                    // Créer un nouveau mouvement avec la nouvelle instance de la ligne

                    oldMove.setLine(newLine);
                    // Move newMove = new Move(newLine, oldMove.action(), (Color) oldMove.color());
                }
            }
            moveHistory.add(oldMove);
        }

        // Restaurer l'index de l'historique
        currentMoveIndex = Math.min(oldIndex, moveHistory.size() - 1);
    }

    // Méthode pour restaurer l'état de la grille
    private void restoreGridState() {
        // Restaurer les lignes
        for (Map.Entry<String, Color> entry : savedLineStates.entrySet()) {
            String lineKey = entry.getKey();
            Line line = gridLines.get(lineKey);
            if (line != null) {
                line.setStroke(entry.getValue());
            }
        }

        // Restaurer les croix
        for (Map.Entry<String, Boolean> entry : savedCrossStates.entrySet()) {
            String lineKey = entry.getKey();
            Line line = gridLines.get(lineKey);
            if (line != null && entry.getValue()) {
                moveHistory.add(new CreateCrossMove(line, "cross", Color.web(ACCENT_COLOR), this));
            }
        }
    }

    // Méthode pour sauvegarder l'état de la grille
    private void saveGridState() {
        savedLineStates.clear();
        savedCrossStates.clear();

        // Sauvegarder l'état des lignes
        for (Map.Entry<String, Line> entry : gridLines.entrySet()) {
            String lineKey = entry.getKey();
            Line line = entry.getValue();
            if (line.getStroke() != Color.TRANSPARENT) {
                savedLineStates.put(lineKey, (Color) line.getStroke());
            }

            // Vérifier si cette ligne a une croix
            boolean hasCross = slitherlinkGrid.getChildren().stream()
                    .anyMatch(node -> node instanceof Line && node.getUserData() == line);
            if (hasCross) {
                savedCrossStates.put(lineKey, true);
            }
        }
    }

    public void updateGrid(double width, double height) {
        // Sauvegarder l'état des lignes existantes avant de reconstruire la grille
        if (!gridLines.isEmpty()) {
            saveGridState();
        }

        // Sauvegarder l'état de l'historique pour le reconstruire ensuite
        List<Move> oldMoveHistory = new ArrayList<>(moveHistory);
        int oldMoveIndex = currentMoveIndex;

        slitherlinkGrid.getChildren().clear();
        gridLines.clear();

        double availableWidth = width * 0.55;
        double availableHeight = height * 0.7;
        double cellWidth = availableWidth / (gridCols + 1);
        double cellHeight = availableHeight / (gridRows + 1);
        CELL_SIZE = Math.min(cellWidth, cellHeight);

        double marginTop = height * 0.05;
        double offsetX = width * 0.05;

        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridCols; j++) {
                if (gridNumbers[i][j] != -1) {
                    Text numberText = new Text(String.valueOf(gridNumbers[i][j]));
                    numberText.setFont(Font.font("Montserrat", FontWeight.BOLD, 28));
                    numberText.setFill(Color.web(DARK_COLOR));

                    double textX = (j + 0.50) * CELL_SIZE + offsetX;
                    double textY = (i + 0.55) * CELL_SIZE + marginTop;
                    numberText.setX(textX - numberText.getLayoutBounds().getWidth() / 2);
                    numberText.setY(textY + numberText.getLayoutBounds().getHeight() / 4);

                    slitherlinkGrid.getChildren().add(numberText);
                }
            }
        }

        for (int i = 0; i <= gridRows; i++) {
            for (int j = 0; j <= gridCols; j++) {
                if (j < gridCols) {
                    Line horizontalLine = new Line(
                            j * CELL_SIZE + offsetX + CELL_SIZE * 0.1,
                            i * CELL_SIZE + marginTop,
                            (j + 1) * CELL_SIZE + offsetX - CELL_SIZE * 0.1,
                            i * CELL_SIZE + marginTop);
                    horizontalLine.setStroke(Color.TRANSPARENT);
                    horizontalLine.setStrokeWidth(5);
                    horizontalLine.setStrokeLineCap(StrokeLineCap.ROUND);

                    String lineKey = "H_" + i + "_" + j;
                    horizontalLine.setId(lineKey);
                    gridLines.put(lineKey, horizontalLine);

                    // Créer la ligne de prévisualisation (effet de survol)
                    Line previewLine = new Line(
                            horizontalLine.getStartX(),
                            horizontalLine.getStartY(),
                            horizontalLine.getEndX(),
                            horizontalLine.getEndY());
                    previewLine.setStroke(Color.TRANSPARENT);
                    previewLine.setStrokeWidth(5);
                    previewLine.setStrokeLineCap(StrokeLineCap.ROUND);
                    previewLine.setOpacity(0.4);
                    previewLine.setUserData("preview_" + lineKey);
                    slitherlinkGrid.getChildren().add(previewLine);

                    Rectangle hitbox = new Rectangle(
                            j * CELL_SIZE + offsetX, i * CELL_SIZE + marginTop - 10,
                            CELL_SIZE, 20);
                    hitbox.setFill(Color.TRANSPARENT);
                    hitbox.setOnMouseClicked(e -> handleLineClick(e, horizontalLine));

                    // Ajouter les événements de survol
                    final Line finalPreviewLine = previewLine;
                    hitbox.setOnMouseEntered(e -> {
                        if (!isLineActive(horizontalLine) && !hasCross(horizontalLine)) {
                            finalPreviewLine
                                    .setStroke(isHypothesisActive ? Color.web(LIGHT_COLOR) : Color.web(DARK_COLOR));
                        }
                    });
                    hitbox.setOnMouseExited(e -> {
                        finalPreviewLine.setStroke(Color.TRANSPARENT);
                    });

                    slitherlinkGrid.getChildren().addAll(horizontalLine, hitbox);
                }

                if (i < gridRows) {
                    Line verticalLine = new Line(
                            j * CELL_SIZE + offsetX,
                            i * CELL_SIZE + marginTop + CELL_SIZE * 0.1,
                            j * CELL_SIZE + offsetX,
                            (i + 1) * CELL_SIZE + marginTop - CELL_SIZE * 0.1);
                    verticalLine.setStroke(Color.TRANSPARENT);
                    verticalLine.setStrokeWidth(5);
                    verticalLine.setStrokeLineCap(StrokeLineCap.ROUND);

                    String lineKey = "V_" + i + "_" + j;
                    verticalLine.setId(lineKey);
                    gridLines.put(lineKey, verticalLine);

                    // Créer la ligne de prévisualisation
                    Line previewLine = new Line(
                            verticalLine.getStartX(),
                            verticalLine.getStartY(),
                            verticalLine.getEndX(),
                            verticalLine.getEndY());
                    previewLine.setStroke(Color.TRANSPARENT);
                    previewLine.setStrokeWidth(5);
                    previewLine.setStrokeLineCap(StrokeLineCap.ROUND);
                    previewLine.setOpacity(0.4);
                    previewLine.setUserData("preview_" + lineKey);
                    slitherlinkGrid.getChildren().add(previewLine);

                    Rectangle hitbox = new Rectangle(
                            j * CELL_SIZE + offsetX - 10, i * CELL_SIZE + marginTop,
                            20, CELL_SIZE);
                    hitbox.setFill(Color.TRANSPARENT);
                    hitbox.setOnMouseClicked(e -> handleLineClick(e, verticalLine));

                    // Ajouter les événements de survol
                    final Line finalPreviewLine = previewLine;
                    hitbox.setOnMouseEntered(e -> {
                        if (!isLineActive(verticalLine) && !hasCross(verticalLine)) {
                            finalPreviewLine
                                    .setStroke(isHypothesisActive ? Color.web(LIGHT_COLOR) : Color.web(DARK_COLOR));
                        }
                    });
                    hitbox.setOnMouseExited(e -> {
                        finalPreviewLine.setStroke(Color.TRANSPARENT);
                    });

                    slitherlinkGrid.getChildren().addAll(verticalLine, hitbox);
                }
            }
        }

        for (int i = 0; i <= gridRows; i++) {
            for (int j = 0; j <= gridCols; j++) {
                Circle dot = new Circle(j * CELL_SIZE + offsetX, i * CELL_SIZE + marginTop, 5, Color.web(ACCENT_COLOR));

                Glow glow = new Glow(0.3);
                dot.setEffect(glow);

                slitherlinkGrid.getChildren().add(dot);
            }
        }

        // Si ce n'est pas le premier chargement de la grille, restaurer l'état
        // précédent
        if (!savedLineStates.isEmpty()) {
            restoreGridState();

            // Reconstruire l'historique avec les nouvelles instances de Line
            if (!oldMoveHistory.isEmpty()) {
                rebuildHistory(oldMoveHistory, oldMoveIndex);
            }
        } else {
            moveHistory.clear();
            currentMoveIndex = -1;
        }

        updateHistoryButtons();
    }

    public boolean hasCross(Line line) {
        return slitherlinkGrid.getChildren().stream()
                .anyMatch(node -> node instanceof Line && node.getUserData() == line);
    }


    public void toggleLine(Line line) {
        if (isHypothesisActive) {
            line.setStroke(line.getStroke().equals(Color.TRANSPARENT) ? Color.web(LIGHT_COLOR) : Color.TRANSPARENT);
        } else {
            line.setStroke(line.getStroke().equals(Color.TRANSPARENT) ? Color.web(DARK_COLOR) : Color.TRANSPARENT);
        }
    }

    public boolean isColorEqual(Color c1, Color c2) {
        if (c1 == null || c2 == null)
            return false;
        return Math.abs(c1.getRed() - c2.getRed()) < 0.01 &&
                Math.abs(c1.getGreen() - c2.getGreen()) < 0.01 &&
                Math.abs(c1.getBlue() - c2.getBlue()) < 0.01;
    }

    public boolean isLineActive(Line line) {
        Color lineColor = (Color) line.getStroke();
        return lineColor != null && !lineColor.equals(Color.TRANSPARENT);
    }

    public boolean checkGrid() {
        return slitherGridChecker.checkGrid();
    }

    /*
     * Getters & Setters
     */

    public boolean isHypothesisInactive() {
        return !isHypothesisActive;
    }

    public Pane getSlitherlinkGrid() {
        return slitherlinkGrid;
    }

    public void setSlitherlinkGrid(Pane slitherlinkGrid) {
        this.slitherlinkGrid = slitherlinkGrid;
    }

    public Button getNextButton() {
        return nextButton;
    }

    public Button getPrevButton() {
        return prevButton;
    }

    public int getGridCols() {
        return gridCols;
    }

    public int getGridRows() {
        return gridRows;
    }

    public int[][] getGridNumbers() {
        return gridNumbers;
    }

    public GameMatrix getGameMatrix() {
        this.gameMatrix.updateGameMatrix(gridLines);
        return gameMatrix;
    }

    /**
     * Retourne une représentation simplifiée de la matrice de jeu
     * 
     * @return Matrice 3D avec l'état de chaque segment
     */
    public int[][][] getSimplifiedGameMatrix() {
        // gameMatrix est l'instance de GameMatrix
        // gridLines est la Map<String, Line> nécessaire
        return gameMatrix.getSimplifiedGameMatrix(gridLines);
    }

    /**
     * Returns a map of all grid lines with their ID as key
     * 
     * @return Map containing all grid lines
     */
    public Map<String, Line> getGridLines() {
        return gridLines;
    }

    /**
     * Returns the cell size used for grid rendering
     * @return The current cell size
     */
    public static double getCellSize() {
        return CELL_SIZE;
    }

    /**
     * Returns the current move history
     * @return List of moves in history
     */
    public List<Move> getMoveHistory() {
        return moveHistory;
    }

    /**
     * Returns the current index in the move history
     * @return Current move index
     */
    public int getCurrentMoveIndex() {
        return currentMoveIndex;
    }

    /**
     * Checks if hypothesis mode is currently active
     * @return true if hypothesis mode is active, false otherwise
     */
    public boolean isHypothesisActive() {
        return isHypothesisActive;
    }

    /**
     * Gets the SlitherGridChecker instance
     * @return The checker object for this grid
     */
    public SlitherGridChecker getSlitherGridChecker() {
        return slitherGridChecker;
    }

}
