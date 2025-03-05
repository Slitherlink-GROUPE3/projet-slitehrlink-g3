package com.menu;

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
    
    // Historique des mouvements
    private static List<Move> moveHistory = new ArrayList<>();
    private static int currentMoveIndex = -1;
    private static Button prevButton;
    private static Button nextButton;
    
    // Grid numbers from JSON
    private static int[][] gridNumbers;
    private static int gridRows; // Added to store grid dimensions
    private static int gridCols;

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
        slitherlinkGrid.getChildren().clear();
        
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
        
        // Réinitialisation de l'historique après un nouveau chargement de la grille
        moveHistory.clear();
        currentMoveIndex = -1;
        updateHistoryButtons();
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
                }
            }
        }
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
}