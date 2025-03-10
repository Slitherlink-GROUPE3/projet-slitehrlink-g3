package com.menu;

import java.util.HashSet;
import java.util.Set;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import com.menu.javafx.TopBar;

public class GameScene {
    private static Pane slitherlinkGrid;
    private static double CELL_SIZE;
    private static StackPane gridContainer;
    private static HBox root;
    private static VBox mainLayer;
    private static int checkCounter = 3;
    private static boolean isHypothesisActive = false;
    private static Map<Line, Color> originalLineStates = new HashMap<>();

    private static Map<String, Color> savedLineStates = new HashMap<>();
    private static Map<String, Boolean> savedCrossStates = new HashMap<>();
    
    // Historique des mouvements
    private static List<Move> moveHistory = new ArrayList<>();
    private static int currentMoveIndex = -1;
    private static Button prevButton;
    private static Button nextButton;
    
    // Grid numbers from JSON
    private static int[][] gridNumbers;
    private static int gridRows; // Added to store grid dimensions
    private static int gridCols;
    
    // Stockage des lignes pour faciliter la vérification
    private static Map<String, Line> gridLines = new HashMap<>();

    // Classe pour représenter un mouvement
    private static class Move {
        private final Line line;
        private final Object action; // "line", "cross" ou "remove_cross"
        private final Color color;
        
        public Move(Line line, Object action, Color color) {
            this.line = line;
            this.action = action;
            this.color = color;
        }
        
        public Line getLine() {
            return line;
        }
        
        public Object getAction() {
            return action;
        }
        
    }

    // Méthode pour charger la grille depuis un fichier JSON
    private static int[][] loadGridFromJson(String filePath) {
        JSONParser parser = new JSONParser();
        int maxRow = 0;
        int maxCol = 0;
        
        try (InputStream inputStream = GameScene.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + filePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String jsonContent = reader.lines().collect(Collectors.joining("\n"));
                JSONObject jsonObject = (JSONObject) parser.parse(jsonContent);
                JSONArray numbersArray = (JSONArray) jsonObject.get("numbers");
    
                // Déterminer la taille de la grille en fonction des coordonnées max
                for (Object numberObj : numbersArray) {
                    JSONObject number = (JSONObject) numberObj;
                    int row = ((Long) number.get("row")).intValue();
                    int col = ((Long) number.get("col")).intValue();
                    
                    if (row > maxRow) maxRow = row;
                    if (col > maxCol) maxCol = col;
                }
    
                // Création dynamique de la grille avec la bonne taille
                gridRows = maxRow + 1; // +1 car index 0-based
                gridCols = maxCol + 1;
                int[][] numbers = new int[gridRows][gridCols];
                for (int[] row : numbers) {
                    java.util.Arrays.fill(row, -1); // Initialisation à -1
                }
    
                // Remplissage des valeurs
                for (Object numberObj : numbersArray) {
                    JSONObject number = (JSONObject) numberObj;
                    int row = ((Long) number.get("row")).intValue();
                    int col = ((Long) number.get("col")).intValue();
                    int value = ((Long) number.get("value")).intValue();
                    numbers[row][col] = value;
                }
    
                return numbers;
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.err.println("Failed to load grid from JSON. Using empty grid.");
        }
    
        return new int[0][0]; // Retourne une grille vide en cas d'erreur
    }
    
    // History navigation methods
    private static void navigateHistory(int direction) {
        if (direction < 0 && currentMoveIndex > -1) {
            // Undo move
            Move moveToUndo = moveHistory.get(currentMoveIndex);
            undoMove(moveToUndo);
            currentMoveIndex--;
        } else if (direction > 0 && currentMoveIndex < moveHistory.size() - 1) {
            // Redo move
            currentMoveIndex++;
            Move moveToRedo = moveHistory.get(currentMoveIndex);
            redoMove(moveToRedo);
        }
        
        updateHistoryButtons();
    }

    private static void undoMove(Move move) {
        if (move == null) return;

        switch (move.getAction().toString()) {
            case "line":
                if (move.getLine() != null) {
                    move.getLine().setStroke(Color.TRANSPARENT);
                }
                break;
            case "cross":
                // Remove the cross if it was added
                slitherlinkGrid.getChildren().removeIf(node -> 
                    node instanceof Line && node.getUserData() == move.getLine()
                );
                break;
            case "remove_cross":
                // Recreate the cross
                createCross(move.getLine(), false);
                break;
            case "hypothesis_confirm":
                // Revert hypothesis changes
                for (Map.Entry<Line, Color> entry : originalLineStates.entrySet()) {
                    entry.getKey().setStroke(entry.getValue());
                }
                slitherlinkGrid.getChildren().removeIf(node -> 
                    node instanceof Line && "hypothesis".equals(((Line) node).getUserData())
                );
                break;
        }
    }

    private static void redoMove(Move move) {
        if (move == null) return;
    
        switch (move.getAction().toString()) {
            case "line":
                if (move.getLine() != null) {
                    move.getLine().setStroke(Color.BLACK);
                    
                    // Vérifier si la grille est correcte après avoir refait un trait
                    if (!isHypothesisActive) {
                        checkGridAutomatically();
                    }
                }
                break;
            case "cross":
                createCross(move.getLine(), false);
                break;
            case "remove_cross":
                slitherlinkGrid.getChildren().removeIf(node -> 
                    node instanceof Line && node.getUserData() == move.getLine()
                );
                break;
            case "hypothesis_confirm":
                // Same as hypothesis exit, no specific redo action needed
                
                // Vérifier également après avoir confirmé une hypothèse
                checkGridAutomatically();
                break;
        }
    }

    private static void updateHistoryButtons() {
        prevButton.setDisable(currentMoveIndex == -1);
        nextButton.setDisable(currentMoveIndex >= moveHistory.size() - 1);
    }

    public static void show(Stage primaryStage) {
        // Charger la grille depuis le fichier JSON
        gridNumbers = loadGridFromJson("grid.json");
        
        mainLayer = new VBox();
        mainLayer.setStyle("-fx-padding: 0; -fx-background-color: #E5D5B0;");

        TopBar topBar = new TopBar(primaryStage, "Jacoboni", "5", "Facile");
        
        // Minuteur (code précédent inchangé)
        java.util.Timer timer = new java.util.Timer();
        final int[] secondsElapsed = {0};
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                secondsElapsed[0]++;
                int minutes = secondsElapsed[0] / 60;
                int seconds = secondsElapsed[0] % 60;
                
                javafx.application.Platform.runLater(() -> {
                    topBar.updateChronometer(minutes, seconds);
                });
            }
        }, 0, 1000);

        root = new HBox();
        slitherlinkGrid = new Pane();
        gridContainer = new StackPane(slitherlinkGrid);
        Pane emptyPane = new Pane();

        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setTranslateX(200);
        buttonBox.setStyle("-fx-background-color: #E5D5B0; -fx-padding: 20;");

        // Créer les boutons (code précédent inchangé)
        Button helpButton = createStyledButton("   AIDE   ?  ");
        Button checkButton = createStyledButton("Check");
        checkButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;-fx-padding: 10px 20px; -fx-background-radius: 10;");

        Text checkCount = new Text(String.valueOf(checkCounter));
        checkCount.setFont(Font.font("Arial", 20));
        checkCount.setFill(Color.BLACK);

        checkButton.setOnAction(e -> {
            if (checkCounter > 0) {
                checkCounter--;
                checkCount.setText(String.valueOf(checkCounter));
                if (checkCounter == 0) {
                    checkButton.setDisable(true);
                }
                
                // Appeler la méthode de vérification
                handleCheckButton();
            }
        });

        HBox checkContainer = new HBox(15, checkButton, checkCount);
        checkContainer.setAlignment(Pos.CENTER);

        // Bouton Hypothèse (code précédent inchangé)
        Button hypothesisButton = createStyledButton("Hypothèse");
        hypothesisButton.setOnAction(e -> {
            if (!isHypothesisActive) {
                originalLineStates.clear();
                for (Node node : slitherlinkGrid.getChildren()) {
                    if (node instanceof Line) {
                        Line line = (Line) node;
                        if (line.getUserData() == null) {
                            originalLineStates.put(line, (Color) line.getStroke());
                        }
                    }
                }
                
                isHypothesisActive = true;
                hypothesisButton.setText("Terminer Hypothèse");
            } else {
                // Code de la boîte de dialogue de confirmation (inchangé)
                Stage dialog = new Stage();
                VBox dialogVBox = new VBox(10);
                dialogVBox.setAlignment(Pos.CENTER);
                dialogVBox.setStyle("-fx-background-color: #E5D5B0; -fx-padding: 20;");
                Button confirmButton = createStyledButton("Confirmer");
                Button cancelButton = createStyledButton("Annuler");
                
                // ... (reste du code de la boîte de dialogue inchangé)
                
                confirmButton.setOnAction(event -> {
                    boolean anyChanges = false;
                    
                    for (Line line : originalLineStates.keySet()) {
                        if (line.getStroke() == Color.BLUE) {
                            line.setStroke(Color.BLACK);
                            anyChanges = true;
                        }
                    }
                    
                    for (Node node : slitherlinkGrid.getChildren()) {
                        if (node instanceof Line && "hypothesis".equals(node.getUserData())) {
                            Line crossLine = (Line) node;
                            crossLine.setStroke(Color.RED);
                            crossLine.setUserData(null);
                            anyChanges = true;
                        }
                    }
                    
                    if (anyChanges) {
                        if (currentMoveIndex < moveHistory.size() - 1) {
                            moveHistory = new ArrayList<>(moveHistory.subList(0, currentMoveIndex + 1));
                        }
                        
                        moveHistory.add(new Move(null, "hypothesis_confirm", null));
                        currentMoveIndex++;
                        updateHistoryButtons();
                    }
                    
                    isHypothesisActive = false;
                    hypothesisButton.setText("Hypothèse");
                    originalLineStates.clear();
                    dialog.close();
                    
                    // Vérifier automatiquement après confirmation d'hypothèse
                    checkGridAutomatically();
                });

                cancelButton.setOnAction(event -> {
                    // ... (code d'annulation inchangé)
                    for (Map.Entry<Line, Color> entry : originalLineStates.entrySet()) {
                        entry.getKey().setStroke(entry.getValue());
                    }
                    slitherlinkGrid.getChildren().removeIf(node -> 
                        node instanceof Line && "hypothesis".equals(((Line) node).getUserData())
                    );
                    isHypothesisActive = false;
                    hypothesisButton.setText("Hypothèse");
                    originalLineStates.clear();
                    dialog.close();
                });

                dialogVBox.getChildren().addAll(confirmButton, cancelButton);
                Scene dialogScene = new Scene(dialogVBox, 200, 100);
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setScene(dialogScene);
                dialog.show();
            }
        });
        
        // Boutons d'historique (code précédent inchangé)
        prevButton = createStyledButton("←");
        prevButton.setDisable(true);
        prevButton.setOnAction(e -> navigateHistory(-1));
        
        nextButton = createStyledButton("→");
        nextButton.setDisable(true);
        nextButton.setOnAction(e -> navigateHistory(1));
        
        HBox historyContainer = new HBox(10, prevButton, nextButton);
        historyContainer.setAlignment(Pos.CENTER);

        gridContainer.prefWidthProperty().bind(root.widthProperty().multiply(0.5));
        emptyPane.prefWidthProperty().bind(root.widthProperty().multiply(0.5));

        buttonBox.getChildren().addAll(helpButton, checkContainer, hypothesisButton, historyContainer);
        root.getChildren().addAll(gridContainer, buttonBox);
        
        mainLayer.getChildren().add(root);
        Scene scene = new Scene(mainLayer, Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
        HBox topBarComponent = topBar.createTopBar(scene);
        mainLayer.getChildren().add(0, topBarComponent);

        root.setStyle("-fx-background-color: #E5D5B0;");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Slitherlink Game");
        primaryStage.setMaximized(true);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateGrid(scene.getWidth(), scene.getHeight()));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updateGrid(scene.getWidth(), scene.getHeight()));
        primaryStage.maximizedProperty().addListener((obs, oldVal, isMaximized) -> updateGrid(scene.getWidth(), scene.getHeight()));
        primaryStage.fullScreenProperty().addListener((obs, oldVal, isFullScreen) -> updateGrid(scene.getWidth(), scene.getHeight()));

        primaryStage.show();
        updateGrid(scene.getWidth(), scene.getHeight());
    }
    
    private static void updateGrid(double width, double height) {
        // Sauvegarder l'état des lignes existantes avant de reconstruire la grille
        if (!gridLines.isEmpty()) {
            saveGridState();
        }
        
        // Sauvegarder l'état de l'historique pour le reconstruire ensuite
        List<Move> oldMoveHistory = new ArrayList<>(moveHistory);
        int oldMoveIndex = currentMoveIndex;
        
        slitherlinkGrid.getChildren().clear();
        gridLines.clear(); // Clear stored lines
        
        // Calculate CELL_SIZE based on grid dimensions
        double availableWidth = width / 2;
        double availableHeight = height * 0.8;
        double cellWidth = availableWidth / (gridCols + 1);
        double cellHeight = availableHeight / (gridRows + 1);
        CELL_SIZE = Math.min(cellWidth, cellHeight);
    
        double marginTop = height * 0.15;
        double offsetX = width * 0.1;
    
        // Draw numbers
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridCols; j++) {
                if (gridNumbers[i][j] != -1) {
                    Text numberText = new Text(String.valueOf(gridNumbers[i][j]));
                    numberText.setFont(Font.font(28));
                    numberText.setFill(Color.BLACK);
    
                    double textX = (j + 0.50) * CELL_SIZE + offsetX;
                    double textY = (i + 0.55) * CELL_SIZE + marginTop;
                    numberText.setX(textX - numberText.getLayoutBounds().getWidth() / 2);
                    numberText.setY(textY + numberText.getLayoutBounds().getHeight() / 4);
    
                    slitherlinkGrid.getChildren().add(numberText);
                }
            }
        }
    
        // Draw horizontal and vertical lines
        for (int i = 0; i <= gridRows; i++) {
            for (int j = 0; j <= gridCols; j++) {
                if (j < gridCols) { // Horizontal lines
                    Line horizontalLine = new Line(
                        j * CELL_SIZE + offsetX + CELL_SIZE * 0.1,
                        i * CELL_SIZE + marginTop,
                        (j + 1) * CELL_SIZE + offsetX - CELL_SIZE * 0.1,
                        i * CELL_SIZE + marginTop
                    );
                    horizontalLine.setStroke(Color.TRANSPARENT);
                    horizontalLine.setStrokeWidth(10);
    
                    // Store line with its location
                    String lineKey = "H_" + i + "_" + j;
                    horizontalLine.setId(lineKey);
                    gridLines.put(lineKey, horizontalLine);
    
                    Rectangle hitbox = new Rectangle(
                        j * CELL_SIZE + offsetX, i * CELL_SIZE + marginTop - 10,
                        CELL_SIZE, 20
                    );
                    hitbox.setFill(Color.TRANSPARENT);
                    hitbox.setOnMouseClicked(e -> handleLineClick(e, horizontalLine));
                    
                    slitherlinkGrid.getChildren().addAll(horizontalLine, hitbox);
                }
    
                if (i < gridRows) { // Vertical lines
                    Line verticalLine = new Line(
                        j * CELL_SIZE + offsetX,
                        i * CELL_SIZE + marginTop + CELL_SIZE * 0.1,
                        j * CELL_SIZE + offsetX,
                        (i + 1) * CELL_SIZE + marginTop - CELL_SIZE * 0.1
                    );
                    verticalLine.setStroke(Color.TRANSPARENT);
                    verticalLine.setStrokeWidth(10);
    
                    // Store line with its location
                    String lineKey = "V_" + i + "_" + j;
                    verticalLine.setId(lineKey);
                    gridLines.put(lineKey, verticalLine);
    
                    Rectangle hitbox = new Rectangle(
                        j * CELL_SIZE + offsetX - 10, i * CELL_SIZE + marginTop,
                        20, CELL_SIZE
                    );
                    hitbox.setFill(Color.TRANSPARENT);
                    hitbox.setOnMouseClicked(e -> handleLineClick(e, verticalLine));
                    
                    slitherlinkGrid.getChildren().addAll(verticalLine, hitbox);
                }
            }
        }
    
        // Draw dots
        for (int i = 0; i <= gridRows; i++) {
            for (int j = 0; j <= gridCols; j++) {
                Circle dot = new Circle(j * CELL_SIZE + offsetX, i * CELL_SIZE + marginTop, 7, Color.BLACK);
                slitherlinkGrid.getChildren().add(dot);
            }
        }
        
        // Si ce n'est pas le premier chargement de la grille, restaurer l'état précédent
        if (!savedLineStates.isEmpty()) {
            restoreGridState();
            
            // Reconstruire l'historique avec les nouvelles instances de Line
            if (!oldMoveHistory.isEmpty()) {
                rebuildHistory(oldMoveHistory, oldMoveIndex);
            }
        } else {
            // Réinitialisation de l'historique seulement lors du premier chargement
            moveHistory.clear();
            currentMoveIndex = -1;
        }
        
        updateHistoryButtons();
    }
    
    // 3. Méthode pour sauvegarder l'état de la grille
    private static void saveGridState() {
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
    
    // 4. Méthode pour restaurer l'état de la grille
    private static void restoreGridState() {
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
                createCross(line, false);
            }
        }
    }
    
    // 5. Méthode pour reconstruire l'historique avec les nouvelles instances de Line
    private static void rebuildHistory(List<Move> oldHistory, int oldIndex) {
        moveHistory.clear();
        
        // Pour chaque mouvement dans l'ancien historique
        for (Move oldMove : oldHistory) {
            if (oldMove.getLine() != null) {
                // Trouver la nouvelle instance de la ligne correspondante
                Line oldLine = oldMove.getLine();
                String lineId = oldLine.getId();
                
                if (lineId != null && gridLines.containsKey(lineId)) {
                    Line newLine = gridLines.get(lineId);
                    // Créer un nouveau mouvement avec la nouvelle instance de la ligne
                    Move newMove = new Move(newLine, oldMove.getAction(), (Color) oldMove.color);
                    moveHistory.add(newMove);
                } else {
                    // Si on ne trouve pas de correspondance, on ajoute quand même le mouvement
                    // pour préserver la longueur de l'historique
                    moveHistory.add(oldMove);
                }
            } else {
                // Pour les mouvements sans ligne (comme hypothesis_confirm)
                moveHistory.add(oldMove);
            }
        }
        
        // Restaurer l'index de l'historique
        currentMoveIndex = Math.min(oldIndex, moveHistory.size() - 1);
    }
    

    private static void handleLineClick(MouseEvent e, Line line) {
        if (e.getButton() == MouseButton.PRIMARY) {
            if (!hasCross(line)) { // Vérifie s'il y a une croix
                toggleLine(line);
                
                // Si ce n'est pas en mode hypothèse, enregistre le mouvement dans l'historique
                if (!isHypothesisActive && !line.getStroke().equals(Color.TRANSPARENT)) {
                    // Supprime les mouvements futurs si on était revenu en arrière
                    if (currentMoveIndex < moveHistory.size() - 1) {
                        moveHistory = new ArrayList<>(moveHistory.subList(0, currentMoveIndex + 1));
                    }
                    
                    // Ajoute le nouveau mouvement
                    moveHistory.add(new Move(line, "line", (Color) line.getStroke()));
                    currentMoveIndex++;
                    updateHistoryButtons();
                    
                    // Vérifier automatiquement si la grille est correcte
                    checkGridAutomatically();
                }
            }
        } else if (e.getButton() == MouseButton.SECONDARY) {
            if (!isLineActive(line)) { // Vérifie si une ligne est déjà tracée
                // Vérifie s'il y a déjà une croix
                boolean hasCrossAlready = hasCross(line);
                
                toggleCross(line);
                
                // Si ce n'est pas en mode hypothèse, enregistre le mouvement dans l'historique
                if (!isHypothesisActive) {
                    // Supprime les mouvements futurs si on était revenu en arrière
                    if (currentMoveIndex < moveHistory.size() - 1) {
                        moveHistory = new ArrayList<>(moveHistory.subList(0, currentMoveIndex + 1));
                    }
                    
                    // Ajoute le nouveau mouvement
                    if (hasCrossAlready) {
                        moveHistory.add(new Move(line, "remove_cross", null));
                    } else {
                        moveHistory.add(new Move(line, "cross", Color.RED));
                    }
                    currentMoveIndex++;
                    updateHistoryButtons();
                    
                    // Pas besoin de vérifier pour les croix, car elles n'affectent pas la solution
                }
            }
        }
    }

    private static void checkGridAutomatically() {
        if (!isHypothesisActive) {  // Ne pas vérifier pendant le mode hypothèse
            boolean isCorrect = checkGrid();
            if (isCorrect) {
                showWinMessage();
            }
        }
    }

    private static void showWinMessage() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Félicitations !");
        alert.setHeaderText("Vous avez gagné !");
        alert.setContentText("Vous avez correctement résolu le puzzle Slitherlink !");
        alert.showAndWait();
    }

    private static boolean isLineActive(Line line) {
        return !line.getStroke().equals(Color.TRANSPARENT);
    }

    private static boolean hasCross(Line line) {
        return slitherlinkGrid.getChildren().stream()
            .anyMatch(node -> node instanceof Line && node.getUserData() == line);
    }

    private static void toggleLine(Line line) {
        if (isHypothesisActive) {
            line.setStroke(line.getStroke().equals(Color.TRANSPARENT) ? Color.BLUE : Color.TRANSPARENT);
        } else {
            line.setStroke(line.getStroke().equals(Color.TRANSPARENT) ? Color.BLACK : Color.TRANSPARENT);
        }
    }
    
    private static void toggleCross(Line line) {
        boolean hasCross = slitherlinkGrid.getChildren().stream()
            .anyMatch(node -> node instanceof Line && node.getUserData() == line);

        if (hasCross) {
            // Supprimer la croix si elle existe
            slitherlinkGrid.getChildren().removeIf(node -> node instanceof Line && node.getUserData() == line);
            slitherlinkGrid.getChildren().removeIf(node -> node instanceof Rectangle && node.getUserData() == line);
        } else {
            createCross(line, isHypothesisActive);
        }
    }
    
    private static void createCross(Line line, boolean isHypothesis) {
        // Création des croix selon l'orientation de la ligne
        Line cross1, cross2;
        double padding = 20; // Ajouter un espacement pour la zone cliquable autour de la croix

        if (line.getStartX() == line.getEndX()) { // Ligne verticale
            cross1 = new Line(
                line.getStartX() - 10, line.getStartY() + 20,
                line.getEndX() + 10, line.getEndY() - 20
            );
            cross2 = new Line(
                line.getStartX() - 10, line.getEndY() - 20,
                line.getEndX() + 10, line.getStartY() + 20
            );
        } else { // Ligne horizontale
            cross1 = new Line(
                line.getStartX() + 20, line.getStartY() - 10,
                line.getEndX() - 20, line.getEndY() + 10
            );
            cross2 = new Line(
                line.getStartX() + 20, line.getEndY() + 10,
                line.getEndX() - 20, line.getStartY() - 10
            );
        }
        cross1.setStrokeWidth(2);
        cross1.setUserData(line);
        
        cross2.setStrokeWidth(2);
        cross2.setUserData(line);
        
        // Ajouter une grande hitbox autour de la croix pour la rendre plus facile à cliquer
        Rectangle hitbox = new Rectangle(
            Math.min(cross1.getStartX(), cross2.getStartX()) - padding, 
            Math.min(cross1.getStartY(), cross2.getStartY()) - padding, 
            Math.abs(cross1.getEndX() - cross1.getStartX()) + 2 * padding, 
            Math.abs(cross1.getEndY() - cross1.getStartY()) + 2 * padding
        );
        hitbox.setFill(Color.TRANSPARENT);
        hitbox.setUserData(line); // Lier la hitbox à la ligne de la croix
        hitbox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                toggleCross(line); // Si on clique sur la hitbox, on supprime la croix
                
                // Si ce n'est pas en mode hypothèse, enregistre le mouvement dans l'historique
                if (!isHypothesisActive) {
                    // Supprime les mouvements futurs si on était revenu en arrière
                    if (currentMoveIndex < moveHistory.size() - 1) {
                        moveHistory = new ArrayList<>(moveHistory.subList(0, currentMoveIndex + 1));
                    }
                    
                    // Ajoute le nouveau mouvement (suppression de croix)
                    moveHistory.add(new Move(line, "remove_cross", null));
                    currentMoveIndex++;
                    updateHistoryButtons();
                }
            }
        });
        
        slitherlinkGrid.getChildren().addAll(cross1, cross2, hitbox);
        
        if (isHypothesis) {
            cross1.setStroke(Color.ORANGE);
            cross2.setStroke(Color.ORANGE);
            cross1.setUserData("hypothesis");
            cross2.setUserData("hypothesis");
        } else {
            cross1.setStroke(Color.RED);
            cross2.setStroke(Color.RED);
        }
    }

    private static Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 18));
        button.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 10;");
        return button;
    }

    /**
     * Vérifie si la grille actuelle est correcte.
     * Retourne true si la solution est valide, false sinon.
     */
    private static boolean checkGrid() {
        // Structure pour stocker toutes les lignes actives (segments)
        List<Line> activeLines = new ArrayList<>();
        
        // Récupérer toutes les lignes actives
        for (Line line : gridLines.values()) {
            if (line.getStroke() == Color.BLACK || line.getStroke() == Color.BLUE) {
                activeLines.add(line);
            }
        }
        
        // Si aucune ligne n'est tracée, la solution est invalide
        if (activeLines.isEmpty()) {
            return false;
        }
        
        // Vérifier que les nombres correspondent aux lignes adjacentes
        if (!checkNumbers()) {
            return false;
        }
        
        // Vérifier que les lignes forment un seul circuit fermé
        return checkSingleClosedLoop(activeLines);
    }

    /**
     * Vérifie que chaque nombre dans la grille correspond au nombre exact 
     * de segments de ligne qui l'entourent.
     */
    private static boolean checkNumbers() {
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridCols; j++) {
                int number = gridNumbers[i][j];
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
    private static int countAdjacentLines(int row, int col) {
        int count = 0;
        
        // Vérifier la ligne du haut
        String topLineKey = "H_" + row + "_" + col;
        Line topLine = gridLines.get(topLineKey);
        if (topLine != null && (topLine.getStroke() == Color.BLACK || topLine.getStroke() == Color.BLUE)) {
            count++;
        }
        
        // Vérifier la ligne du bas
        String bottomLineKey = "H_" + (row + 1) + "_" + col;
        Line bottomLine = gridLines.get(bottomLineKey);
        if (bottomLine != null && (bottomLine.getStroke() == Color.BLACK || bottomLine.getStroke() == Color.BLUE)) {
            count++;
        }
        
        // Vérifier la ligne de gauche
        String leftLineKey = "V_" + row + "_" + col;
        Line leftLine = gridLines.get(leftLineKey);
        if (leftLine != null && (leftLine.getStroke() == Color.BLACK || leftLine.getStroke() == Color.BLUE)) {
            count++;
        }
        
        // Vérifier la ligne de droite
        String rightLineKey = "V_" + row + "_" + (col + 1);
        Line rightLine = gridLines.get(rightLineKey);
        if (rightLine != null && (rightLine.getStroke() == Color.BLACK || rightLine.getStroke() == Color.BLUE)) {
            count++;
        }
        
        return count;
    }

    /**
     * Structure pour représenter un point de la grille
     */
    private static class Point {
        final int x, y;
        
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Point)) return false;
            Point other = (Point) obj;
            return this.x == other.x && this.y == other.y;
        }
        
        @Override
        public int hashCode() {
            return x * 31 + y;
        }
    }

    /**
     * Vérifie que toutes les lignes actives forment un unique circuit fermé.
     */
    private static boolean checkSingleClosedLoop(List<Line> activeLines) {
        if (activeLines.isEmpty()) {
            return false; // Pas de lignes tracées
        }
        
        // Crée une map des points de la grille et leurs connexions
        Map<Point, Set<Point>> adjacencyList = new HashMap<>();
        
        // Traite chaque ligne active et l'ajoute à la liste d'adjacence
        for (Line line : activeLines) {
            String lineId = line.getId();
            if (lineId == null) continue;
            
            String[] parts = lineId.split("_");
            if (parts.length != 3) continue;
            
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
    private static void dfs(Point current, Point parent, Map<Point, Set<Point>> adjacencyList, Set<Point> visited) {
        visited.add(current);
        
        for (Point neighbor : adjacencyList.get(current)) {
            if (!neighbor.equals(parent) && !visited.contains(neighbor)) {
                dfs(neighbor, current, adjacencyList, visited);
            }
        }
    }

    /**
     * Méthode à appeler lors d'un clic sur le bouton "Check"
     */
    private static void handleCheckButton() {
        boolean isCorrect = checkGrid();
        
        Alert alert = new Alert(isCorrect ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setTitle("Vérification de la grille");
        
        if (isCorrect) {
            alert.setHeaderText("Félicitations !");
            alert.setContentText("Votre solution est correcte.");
        } else {
            alert.setHeaderText("Solution incorrecte");
            alert.setContentText("Votre solution ne respecte pas les règles du Slitherlink. Vérifiez que :\n"
                    + "- Les chiffres correspondent exactement au nombre de segments adjacents\n"
                    + "- Toutes les lignes forment un circuit unique fermé\n"
                    + "- Il n'y a pas de branches ou d'intersections");
        }
        
        alert.showAndWait();
    }
}