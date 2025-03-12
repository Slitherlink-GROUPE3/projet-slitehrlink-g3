package com.menu;

import javafx.scene.shape.StrokeLineCap;
import java.util.HashSet;
import java.util.Set;
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
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.geometry.Insets;

import com.menu.javafx.PauseMenu;
import com.menu.javafx.TopBar;

public class GameScene {
    // Constantes de couleurs du Menu
    private static final String MAIN_COLOR = "#3A7D44"; // Vert principal
    private static final String SECONDARY_COLOR = "#F2E8CF"; // Beige clair
    private static final String ACCENT_COLOR = "#BC4749"; // Rouge-brique
    private static final String DARK_COLOR = "#386641"; // Vert foncé
    private static final String LIGHT_COLOR = "#A7C957"; // Vert clair

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

    private static int[][] gridNumbers;
    private static int gridRows;
    private static int gridCols;

    private static Map<String, Line> gridLines = new HashMap<>();

    private static class Move {
        private final Line line;
        private final Object action;
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

                    if (row > maxRow)
                        maxRow = row;
                    if (col > maxCol)
                        maxCol = col;
                }

                // Création dynamique de la grille avec la bonne taille
                gridRows = maxRow + 1;
                gridCols = maxCol + 1;
                int[][] numbers = new int[gridRows][gridCols];
                for (int[] row : numbers) {
                    java.util.Arrays.fill(row, -1);
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

        return new int[0][0];
    }

    private static void navigateHistory(int direction) {
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

    private static void undoMove(Move move) {
        if (move == null)
            return;

        switch (move.getAction().toString()) {
            case "line":
                if (move.getLine() != null) {
                    move.getLine().setStroke(Color.TRANSPARENT);
                }
                break;
            case "cross":
                slitherlinkGrid.getChildren()
                        .removeIf(node -> node instanceof Line && node.getUserData() == move.getLine());
                break;
            case "remove_cross":
                createCross(move.getLine(), false);
                break;
            case "hypothesis_confirm":
                for (Map.Entry<Line, Color> entry : originalLineStates.entrySet()) {
                    entry.getKey().setStroke(entry.getValue());
                }
                slitherlinkGrid.getChildren()
                        .removeIf(node -> node instanceof Line && "hypothesis".equals(((Line) node).getUserData()));
                break;
        }
    }

    private static void redoMove(Move move) {
        if (move == null)
            return;

        switch (move.getAction().toString()) {
            case "line":
                if (move.getLine() != null) {
                    move.getLine().setStroke(Color.web(DARK_COLOR));

                    if (!isHypothesisActive) {
                        checkGridAutomatically();
                    }
                }
                break;
            case "cross":
                createCross(move.getLine(), false);
                break;
            case "remove_cross":
                slitherlinkGrid.getChildren()
                        .removeIf(node -> node instanceof Line && node.getUserData() == move.getLine());
                break;
            case "hypothesis_confirm":
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
        mainLayer.setStyle("-fx-padding: 0; -fx-background-color: " + SECONDARY_COLOR + ";");

        TopBar topBar = new TopBar(primaryStage, "Jacoboni", "5", "Facile");

        java.util.Timer timer = new java.util.Timer();
        final int[] secondsElapsed = { 0 };
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                // Vérifier si le jeu est en pause
                if (!PauseMenu.isGamePaused()) {
                    secondsElapsed[0]++;
                    int minutes = secondsElapsed[0] / 60;
                    int seconds = secondsElapsed[0] % 60;

                    javafx.application.Platform.runLater(() -> {
                        topBar.updateChronometer(minutes, seconds);
                    });
                }
                // Si en pause, ne rien faire - le temps ne s'incrémente pas
            }
        }, 0, 1000);

        // Configuration du callback de réinitialisation du chronomètre
        topBar.setChronoResetCallback(() -> {
            // Réinitialiser le compteur de secondes
            secondsElapsed[0] = 0;

            // Réinitialiser l'affichage du chronomètre (optionnel car sera mis à jour au
            // prochain tick)
            javafx.application.Platform.runLater(() -> {
                topBar.updateChronometer(0, 0);
            });
        });

        root = new HBox();
        slitherlinkGrid = new Pane();
        gridContainer = new StackPane(slitherlinkGrid);

        gridContainer.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.9);" +
                        "-fx-background-radius: 15;" +
                        "-fx-padding: 20;");

        DropShadow gridShadow = new DropShadow();
        gridShadow.setColor(Color.web("#000000", 0.2));
        gridShadow.setRadius(10);
        gridShadow.setOffsetY(5);
        gridContainer.setEffect(gridShadow);

        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(30));
        buttonBox.setMaxWidth(350);
        buttonBox.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.8);" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 5);");

        Label controlsTitle = new Label("Contrôles");
        controlsTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));
        controlsTitle.setTextFill(Color.web(DARK_COLOR));

        Button helpButton = createStyledButton("   AIDE   ?  ", false);
        helpButton.setOnAction(e -> {
            animateButtonClick(helpButton);
        });

        Button checkButton = createStyledButton("Vérifier", true);

        Text checkCount = new Text(String.valueOf(checkCounter));
        checkCount.setFont(Font.font("Montserrat", FontWeight.BOLD, 22));
        checkCount.setFill(Color.web(DARK_COLOR));

        StackPane countContainer = new StackPane(checkCount);
        countContainer.setMinSize(40, 40);
        countContainer.setMaxSize(40, 40);
        countContainer.setStyle(
                "-fx-background-color: " + SECONDARY_COLOR + ";" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: " + MAIN_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 20;");

        checkButton.setOnAction(e -> {
            animateButtonClick(checkButton);
            if (checkCounter > 0) {
                checkCounter--;
                checkCount.setText(String.valueOf(checkCounter));
                if (checkCounter == 0) {
                    checkButton.setDisable(true);
                    checkButton.setOpacity(0.7);
                }
                handleCheckButton();
            }
        });

        HBox checkContainer = new HBox(15, checkButton, countContainer);
        checkContainer.setAlignment(Pos.CENTER);

        Button hypothesisButton = createStyledButton("Hypothèse", false);
        hypothesisButton.setOnAction(e -> {
            animateButtonClick(hypothesisButton);
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
                hypothesisButton.setStyle(
                        "-fx-background-color: " + ACCENT_COLOR + ";" +
                                "-fx-background-radius: 30;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 16px;" +
                                "-fx-padding: 10 20;" +
                                "-fx-cursor: hand;");
            } else {
                Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);

                Label dialogTitle = new Label("Confirmer l'hypothèse");
                dialogTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 18));
                dialogTitle.setTextFill(Color.web(DARK_COLOR));

                Button confirmButton = createStyledButton("Confirmer", true);
                Button cancelButton = createStyledButton("Annuler", false);

                VBox dialogVBox = new VBox(20);
                dialogVBox.setAlignment(Pos.CENTER);
                dialogVBox.setPadding(new Insets(30));
                dialogVBox.setStyle("-fx-background-color: " + SECONDARY_COLOR + ";");

                HBox buttonContainer = new HBox(20, cancelButton, confirmButton);
                buttonContainer.setAlignment(Pos.CENTER);

                dialogVBox.getChildren().addAll(dialogTitle, buttonContainer);

                Scene dialogScene = new Scene(dialogVBox, 350, 180);
                dialog.setScene(dialogScene);
                dialog.setTitle("Hypothèse");

                dialogVBox.setOpacity(0);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), dialogVBox);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();

                confirmButton.setOnAction(event -> {
                    animateButtonClick(confirmButton);
                    boolean anyChanges = false;

                    for (Line line : originalLineStates.keySet()) {
                        // Vérifier si la ligne est en vert clair (hypothèse) et la transformer en vert
                        // foncé (confirmée)
                        if (isColorEqual((Color) line.getStroke(), Color.web(LIGHT_COLOR))) {
                            line.setStroke(Color.web(DARK_COLOR));
                            anyChanges = true;
                        }
                    }

                    for (Node node : slitherlinkGrid.getChildren()) {
                        if (node instanceof Line && "hypothesis".equals(node.getUserData())) {
                            Line crossLine = (Line) node;
                            crossLine.setStroke(Color.web(ACCENT_COLOR));
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
                    hypothesisButton.setStyle(
                            "-fx-background-color: " + SECONDARY_COLOR + ";" +
                                    "-fx-background-radius: 30;" +
                                    "-fx-border-color: " + MAIN_COLOR + ";" +
                                    "-fx-border-width: 2;" +
                                    "-fx-border-radius: 30;" +
                                    "-fx-text-fill: " + DARK_COLOR + ";" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 16px;" +
                                    "-fx-padding: 10 20;" +
                                    "-fx-cursor: hand;");

                    originalLineStates.clear();

                    FadeTransition fadeOut = new FadeTransition(Duration.millis(300), dialogVBox);
                    fadeOut.setFromValue(1);
                    fadeOut.setToValue(0);
                    fadeOut.setOnFinished(ev -> dialog.close());
                    fadeOut.play();

                    // Vérifier automatiquement après confirmation d'hypothèse
                    checkGridAutomatically();
                });

                cancelButton.setOnAction(event -> {
                    animateButtonClick(cancelButton);

                    for (Map.Entry<Line, Color> entry : originalLineStates.entrySet()) {
                        entry.getKey().setStroke(entry.getValue());
                    }
                    slitherlinkGrid.getChildren()
                            .removeIf(node -> node instanceof Line && "hypothesis".equals(((Line) node).getUserData()));
                    isHypothesisActive = false;
                    hypothesisButton.setText("Hypothèse");
                    hypothesisButton.setStyle(
                            "-fx-background-color: " + SECONDARY_COLOR + ";" +
                                    "-fx-background-radius: 30;" +
                                    "-fx-border-color: " + MAIN_COLOR + ";" +
                                    "-fx-border-width: 2;" +
                                    "-fx-border-radius: 30;" +
                                    "-fx-text-fill: " + DARK_COLOR + ";" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 16px;" +
                                    "-fx-padding: 10 20;" +
                                    "-fx-cursor: hand;");
                    originalLineStates.clear();

                    FadeTransition fadeOut = new FadeTransition(Duration.millis(300), dialogVBox);
                    fadeOut.setFromValue(1);
                    fadeOut.setToValue(0);
                    fadeOut.setOnFinished(ev -> dialog.close());
                    fadeOut.play();
                });

                dialog.show();
            }
        });

        prevButton = createNavigationButton("←");
        prevButton.setDisable(true);
        prevButton.setOnAction(e -> {
            animateButtonClick(prevButton);
            navigateHistory(-1);
        });

        nextButton = createNavigationButton("→");
        nextButton.setDisable(true);
        nextButton.setOnAction(e -> {
            animateButtonClick(nextButton);
            navigateHistory(1);
        });

        // Conteneur pour les boutons de navigation
        HBox historyContainer = new HBox(15, prevButton, nextButton);
        historyContainer.setAlignment(Pos.CENTER);

        buttonBox.getChildren().addAll(controlsTitle, createSeparator(), helpButton, checkContainer, hypothesisButton,
                createSeparator(), historyContainer);

        gridContainer.setPadding(new Insets(20));
        gridContainer.prefWidthProperty().bind(root.widthProperty().multiply(0.65));

        root.setSpacing(30);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(gridContainer, buttonBox);

        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + SECONDARY_COLOR + ", " + LIGHT_COLOR
                        + " 70%);" +
                        "-fx-background-radius: 0;" +
                        "-fx-padding: 20px;");

        mainLayer.getChildren().add(root);
        Scene scene = new Scene(mainLayer, Screen.getPrimary().getVisualBounds().getWidth(),
                Screen.getPrimary().getVisualBounds().getHeight());
        HBox topBarComponent = topBar.createTopBar(scene);
        mainLayer.getChildren().add(0, topBarComponent);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Slitherlink Game");
        primaryStage.setMaximized(true);

        root.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateGrid(scene.getWidth(), scene.getHeight()));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updateGrid(scene.getWidth(), scene.getHeight()));
        primaryStage.maximizedProperty()
                .addListener((obs, oldVal, isMaximized) -> updateGrid(scene.getWidth(), scene.getHeight()));
        primaryStage.fullScreenProperty()
                .addListener((obs, oldVal, isFullScreen) -> updateGrid(scene.getWidth(), scene.getHeight()));

        primaryStage.show();
        updateGrid(scene.getWidth(), scene.getHeight());
    }

    private static boolean isColorEqual(Color c1, Color c2) {
        if (c1 == null || c2 == null)
            return false;
        return Math.abs(c1.getRed() - c2.getRed()) < 0.01 &&
                Math.abs(c1.getGreen() - c2.getGreen()) < 0.01 &&
                Math.abs(c1.getBlue() - c2.getBlue()) < 0.01;
    }

    private static Node createSeparator() {
        Rectangle separator = new Rectangle();
        separator.setHeight(2);
        separator.setWidth(250);
        separator.setFill(Color.web(LIGHT_COLOR, 0.5));
        separator.setArcWidth(2);
        separator.setArcHeight(2);

        VBox separatorContainer = new VBox(separator);
        separatorContainer.setPadding(new Insets(5, 0, 5, 0));
        separatorContainer.setAlignment(Pos.CENTER);

        return separatorContainer;
    }

    // Méthode pour créer des boutons de navigation
    private static Button createNavigationButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Montserrat", FontWeight.BOLD, 20));
        button.setTextFill(Color.web(DARK_COLOR));
        button.setMinSize(50, 50);
        button.setMaxSize(50, 50);

        button.setStyle(
                "-fx-background-color: " + SECONDARY_COLOR + ";" +
                        "-fx-background-radius: 25;" +
                        "-fx-border-color: " + MAIN_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 25;" +
                        "-fx-cursor: hand;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.2));
        shadow.setRadius(3);
        shadow.setOffsetY(2);
        button.setEffect(shadow);

        // Animations au survol
        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: " + MAIN_COLOR + ";" +
                            "-fx-background-radius: 25;" +
                            "-fx-border-color: " + DARK_COLOR + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 25;" +
                            "-fx-cursor: hand;");
            button.setTextFill(Color.WHITE);

            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
        });

        button.setOnMouseExited(e -> {
            if (!button.isDisabled()) {
                button.setStyle(
                        "-fx-background-color: " + SECONDARY_COLOR + ";" +
                                "-fx-background-radius: 25;" +
                                "-fx-border-color: " + MAIN_COLOR + ";" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 25;" +
                                "-fx-cursor: hand;");
                button.setTextFill(Color.web(DARK_COLOR));
            }

            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });

        return button;
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

    // Méthode pour sauvegarder l'état de la grille
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

    // Méthode pour restaurer l'état de la grille
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

    // Méthode pour reconstruire l'historique avec les nouvelles instances de Line
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
                        moveHistory.add(new Move(line, "cross", Color.web(ACCENT_COLOR)));
                    }
                    currentMoveIndex++;
                    updateHistoryButtons();
                }
            }
        }
    }

    /**
     * Méthode à appeler lors d'un clic sur le bouton "Check"
     */
    private static void handleCheckButton() {
        boolean isCorrect = checkGrid();

        // Créer une fenêtre personnalisée au lieu d'une Alert standard
        Stage checkStage = new Stage();
        checkStage.initModality(Modality.APPLICATION_MODAL);

        // Titre stylisé
        Label checkTitle = new Label(isCorrect ? "Félicitations !" : "Solution incorrecte");
        checkTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));

        // Couleur du titre selon le résultat
        checkTitle.setTextFill(isCorrect ? Color.web(MAIN_COLOR) : Color.web(ACCENT_COLOR));

        // Effet d'ombre pour le titre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.3));
        shadow.setRadius(3);
        shadow.setOffsetY(2);
        checkTitle.setEffect(shadow);

        // Message
        Label messageLabel;
        if (isCorrect) {
            messageLabel = new Label("Votre solution est correcte !");
            messageLabel.setTextFill(Color.web(DARK_COLOR));
            messageLabel.setFont(Font.font("Calibri", 16));
        } else {
            // Utiliser un VBox pour formater les points de vérification
            VBox errorDetails = new VBox(8);
            errorDetails.setAlignment(Pos.CENTER_LEFT);

            Label mainError = new Label("Votre solution ne respecte pas les règles du Slitherlink.");
            mainError.setTextFill(Color.web(DARK_COLOR));
            mainError.setFont(Font.font("Calibri", FontWeight.BOLD, 16));

            Label check1 = new Label("• Les chiffres doivent correspondre exactement au nombre de segments adjacents");
            Label check2 = new Label("• Toutes les lignes doivent former un circuit unique fermé");
            Label check3 = new Label("• Il ne doit pas y avoir de branches ou d'intersections");

            check1.setTextFill(Color.web(DARK_COLOR));
            check2.setTextFill(Color.web(DARK_COLOR));
            check3.setTextFill(Color.web(DARK_COLOR));

            errorDetails.getChildren().addAll(mainError, check1, check2, check3);

            checkStage.setTitle("Erreur de vérification");

            // Créer un conteneur pour tout le contenu
            VBox contentBox = new VBox(20, checkTitle, errorDetails);
            contentBox.setAlignment(Pos.CENTER);
            contentBox.setPadding(new Insets(30));
            contentBox.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, " + SECONDARY_COLOR + ", white);" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-color: " + ACCENT_COLOR + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 15;");

            // Bouton OK
            Button okButton = createStyledButton("Continuer", false);
            okButton.setPrefWidth(120);

            okButton.setOnAction(e -> {
                animateButtonClick(okButton);

                // Animation de fermeture
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), contentBox);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(event -> checkStage.close());
                fadeOut.play();
            });

            contentBox.getChildren().add(okButton);

            Scene checkScene = new Scene(contentBox, 500, 350);
            checkStage.setScene(checkScene);

            // Animation d'entrée
            contentBox.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), contentBox);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            checkStage.show();
            return;
        }

        checkStage.setTitle("Vérification réussie");

        // Bouton OK
        Button okButton = createStyledButton("Super !", true);
        okButton.setPrefWidth(120);

        okButton.setOnAction(e -> {
            animateButtonClick(okButton);

            // Animation de fermeture
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), checkStage.getScene().getRoot());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> checkStage.close());
            fadeOut.play();
        });

        // Organisation des éléments
        VBox checkBox = new VBox(20, checkTitle, messageLabel, okButton);
        checkBox.setAlignment(Pos.CENTER);
        checkBox.setPadding(new Insets(30));
        checkBox.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " + SECONDARY_COLOR + ", " + LIGHT_COLOR + " 90%);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: " + MAIN_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;");

        Scene checkScene = new Scene(checkBox, 400, 250);
        checkStage.setScene(checkScene);

        // Animation d'entrée
        checkBox.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), checkBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        checkStage.show();
    }

    /*
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
            if (!(obj instanceof Point))
                return false;
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
    private static void dfs(Point current, Point parent, Map<Point, Set<Point>> adjacencyList, Set<Point> visited) {
        visited.add(current);

        for (Point neighbor : adjacencyList.get(current)) {
            if (!neighbor.equals(parent) && !visited.contains(neighbor)) {
                dfs(neighbor, current, adjacencyList, visited);
            }
        }
    }

    private static Button createStyledButton(String text, boolean isPrimary) {
        Button button = new Button(text);
        button.setFont(Font.font("Montserrat", FontWeight.BOLD, 16));

        if (isPrimary) {
            button.setTextFill(Color.WHITE);
            button.setStyle(
                    "-fx-background-color: " + MAIN_COLOR + ";" +
                            "-fx-background-radius: 30;" +
                            "-fx-border-color: " + DARK_COLOR + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 30;" +
                            "-fx-padding: 10 20;" +
                            "-fx-cursor: hand;");
        } else {
            button.setTextFill(Color.web(DARK_COLOR));
            button.setStyle(
                    "-fx-background-color: " + SECONDARY_COLOR + ";" +
                            "-fx-background-radius: 30;" +
                            "-fx-border-color: " + MAIN_COLOR + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 30;" +
                            "-fx-padding: 10 20;" +
                            "-fx-cursor: hand;");
        }

        // Effet d'ombre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.2));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        button.setEffect(shadow);

        // Animations au survol
        button.setOnMouseEntered(e -> {
            if (isPrimary) {
                button.setStyle(
                        "-fx-background-color: " + DARK_COLOR + ";" +
                                "-fx-background-radius: 30;" +
                                "-fx-border-color: " + MAIN_COLOR + ";" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 30;" +
                                "-fx-padding: 10 20;" +
                                "-fx-cursor: hand;");
            } else {
                button.setStyle(
                        "-fx-background-color: " + MAIN_COLOR + ";" +
                                "-fx-background-radius: 30;" +
                                "-fx-border-color: " + DARK_COLOR + ";" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 30;" +
                                "-fx-padding: 10 20;" +
                                "-fx-cursor: hand;");
                button.setTextFill(Color.WHITE);
            }

            // Animation de mise à l'échelle
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
        });

        button.setOnMouseExited(e -> {
            if (!button.isDisabled()) {
                if (isPrimary) {
                    button.setStyle(
                            "-fx-background-color: " + MAIN_COLOR + ";" +
                                    "-fx-background-radius: 30;" +
                                    "-fx-border-color: " + DARK_COLOR + ";" +
                                    "-fx-border-width: 2;" +
                                    "-fx-border-radius: 30;" +
                                    "-fx-padding: 10 20;" +
                                    "-fx-cursor: hand;");
                    button.setTextFill(Color.WHITE);
                } else {
                    button.setStyle(
                            "-fx-background-color: " + SECONDARY_COLOR + ";" +
                                    "-fx-background-radius: 30;" +
                                    "-fx-border-color: " + MAIN_COLOR + ";" +
                                    "-fx-border-width: 2;" +
                                    "-fx-border-radius: 30;" +
                                    "-fx-padding: 10 20;" +
                                    "-fx-cursor: hand;");
                    button.setTextFill(Color.web(DARK_COLOR));
                }
            }

            // Animation de retour à l'échelle normale
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });

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
            if (line.getStroke() == Color.web(DARK_COLOR) || line.getStroke() == Color.web(LIGHT_COLOR)) {
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
        if (topLine != null
                && (topLine.getStroke() == Color.web(DARK_COLOR) || topLine.getStroke() == Color.web(LIGHT_COLOR))) {
            count++;
        }

        // Vérifier la ligne du bas
        String bottomLineKey = "H_" + (row + 1) + "_" + col;
        Line bottomLine = gridLines.get(bottomLineKey);
        if (bottomLine != null && (bottomLine.getStroke() == Color.web(DARK_COLOR)
                || bottomLine.getStroke() == Color.web(LIGHT_COLOR))) {
            count++;
        }

        // Vérifier la ligne de gauche
        String leftLineKey = "V_" + row + "_" + col;
        Line leftLine = gridLines.get(leftLineKey);
        if (leftLine != null
                && (leftLine.getStroke() == Color.web(DARK_COLOR) || leftLine.getStroke() == Color.web(LIGHT_COLOR))) {
            count++;
        }

        // Vérifier la ligne de droite
        String rightLineKey = "V_" + row + "_" + (col + 1);
        Line rightLine = gridLines.get(rightLineKey);
        if (rightLine != null && (rightLine.getStroke() == Color.web(DARK_COLOR)
                || rightLine.getStroke() == Color.web(LIGHT_COLOR))) {
            count++;
        }

        return count;
    }

    private static boolean hasCross(Line line) {
        return slitherlinkGrid.getChildren().stream()
                .anyMatch(node -> node instanceof Line && node.getUserData() == line);
    }

    private static void toggleLine(Line line) {
        if (isHypothesisActive) {
            line.setStroke(line.getStroke().equals(Color.TRANSPARENT) ? Color.web(LIGHT_COLOR) : Color.TRANSPARENT);
        } else {
            line.setStroke(line.getStroke().equals(Color.TRANSPARENT) ? Color.web(DARK_COLOR) : Color.TRANSPARENT);
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
                    line.getEndX() + 10, line.getEndY() - 20);
            cross2 = new Line(
                    line.getStartX() - 10, line.getEndY() - 20,
                    line.getEndX() + 10, line.getStartY() + 20);
        } else { // Ligne horizontale
            cross1 = new Line(
                    line.getStartX() + 20, line.getStartY() - 10,
                    line.getEndX() - 20, line.getEndY() + 10);
            cross2 = new Line(
                    line.getStartX() + 20, line.getEndY() + 10,
                    line.getEndX() - 20, line.getStartY() - 10);
        }
        cross1.setStrokeWidth(3);
        cross1.setUserData(line);

        cross2.setStrokeWidth(3);
        cross2.setUserData(line);

        // Ajouter un effet visuel aux croix
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.3));
        shadow.setRadius(2);
        shadow.setOffsetY(1);
        cross1.setEffect(shadow);
        cross2.setEffect(shadow);

        // Ajouter une grande hitbox autour de la croix pour la rendre plus facile à
        // cliquer
        Rectangle hitbox = new Rectangle(
                Math.min(cross1.getStartX(), cross2.getStartX()) - padding,
                Math.min(cross1.getStartY(), cross2.getStartY()) - padding,
                Math.abs(cross1.getEndX() - cross1.getStartX()) + 2 * padding,
                Math.abs(cross1.getEndY() - cross1.getStartY()) + 2 * padding);
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
            cross1.setStroke(Color.web(LIGHT_COLOR));
            cross2.setStroke(Color.web(LIGHT_COLOR));
            cross1.setUserData("hypothesis");
            cross2.setUserData("hypothesis");
        } else {
            cross1.setStroke(Color.web(ACCENT_COLOR));
            cross2.setStroke(Color.web(ACCENT_COLOR));
        }
    }

    // Animation pour les clics de bouton
    private static void animateButtonClick(Button button) {
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), button);
        scaleDown.setToX(0.95);
        scaleDown.setToY(0.95);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), button);
        scaleUp.setToX(1.0);
        scaleUp.setToY(1.0);

        scaleDown.setOnFinished(e -> scaleUp.play());
        scaleDown.play();
    }

    private static void checkGridAutomatically() {
        if (!isHypothesisActive) { // Ne pas vérifier pendant le mode hypothèse
            boolean isCorrect = checkGrid();
            if (isCorrect) {
                showWinMessage();
            }
        }
    }

    private static void showWinMessage() {
        // Créer une fenêtre personnalisée au lieu d'une Alert standard
        Stage winStage = new Stage();
        winStage.initModality(Modality.APPLICATION_MODAL);
        winStage.setTitle("Victoire !");

        // Titre stylisé
        Label winTitle = new Label("FÉLICITATIONS !");
        winTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 28));

        // Effet de dégradé pour le texte
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(MAIN_COLOR)),
                new Stop(0.5, Color.web(DARK_COLOR)),
                new Stop(1, Color.web(MAIN_COLOR)));
        winTitle.setTextFill(gradient);

        // Ajouter des effets visuels au titre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(DARK_COLOR, 0.5));
        shadow.setRadius(5);
        shadow.setOffsetY(2);

        Glow glow = new Glow(0.6);
        glow.setInput(shadow);
        winTitle.setEffect(glow);

        // Message
        Label winMessage = new Label("Vous avez correctement résolu le puzzle Slitherlink !");
        winMessage.setFont(Font.font("Calibri", 18));
        winMessage.setTextFill(Color.web(DARK_COLOR));

        // Bouton OK
        Button okButton = createStyledButton("OK", true);
        okButton.setPrefWidth(120);

        okButton.setOnAction(e -> {
            animateButtonClick(okButton);

            // Animation de fermeture
            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), winStage.getScene().getRoot());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> winStage.close());
            fadeOut.play();
        });

        // Organisation des éléments
        VBox winBox = new VBox(20, winTitle, winMessage, okButton);
        winBox.setAlignment(Pos.CENTER);
        winBox.setPadding(new Insets(30));
        winBox.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " + SECONDARY_COLOR + ", " + LIGHT_COLOR + " 90%);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: " + MAIN_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;");

        Scene winScene = new Scene(winBox, 450, 300);
        winStage.setScene(winScene);

        // Animation d'entrée
        winBox.setOpacity(0);

        // Définir une échelle initiale pour l'animation
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(400), winBox);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), winBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Jouer les animations simultanément
        fadeIn.play();
        scaleIn.play();

        winStage.show();
    }

    private static boolean isLineActive(Line line) {
        Color lineColor = (Color) line.getStroke();
        return lineColor != null && !lineColor.equals(Color.TRANSPARENT);
    }
}
