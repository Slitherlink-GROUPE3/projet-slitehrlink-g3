package com.menu;

import javafx.geometry.Pos;
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
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GameScene {
    private static final int GRID_SIZE = 10;
    private static Pane slitherlinkGrid;
    private static double CELL_SIZE;
    private static StackPane gridContainer;
    private static HBox root;
    private static int checkCounter = 3;

    public static void show(Stage primaryStage) {
        root = new HBox();
        slitherlinkGrid = new Pane();
        gridContainer = new StackPane(slitherlinkGrid);
        Pane emptyPane = new Pane();

        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setTranslateX(200);
        buttonBox.setStyle("-fx-background-color: #E5D5B0; -fx-padding: 20;");

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

        Button hypothesisButton = createStyledButton("Hypothèse");

        gridContainer.prefWidthProperty().bind(root.widthProperty().multiply(0.5));
        emptyPane.prefWidthProperty().bind(root.widthProperty().multiply(0.5));

        buttonBox.getChildren().addAll(helpButton, checkContainer, hypothesisButton);
        root.getChildren().addAll(gridContainer, buttonBox);

        Scene scene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
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
        CELL_SIZE = Math.min(width / 2, height * 0.8) / (GRID_SIZE + 1);

        double marginTop = height * 0.15;
        double offsetX = width * 0.1;

        int[][] numbers = {
            {3, 3, -1, -1, -1, -1, -1, -1, -1, 0},
            {-1, -1, 1, 2, -1, -1, -1, -1, -1, 1},
            {-1, -1, 2, 0, -1, -1, -1, -1, -1, -1},
            {-1, 1, -1, -1, 1, 1, -1, -1, -1, -1},
            {-1, 2, -1, -1, -1, -1, -1, -1, -1, -1},
        };

        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j < numbers[i].length; j++) {
                if (numbers[i][j] != -1) {
                    Text numberText = new Text(String.valueOf(numbers[i][j]));
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

        for (int i = 0; i <= GRID_SIZE; i++) {
            for (int j = 0; j <= GRID_SIZE; j++) {
                if (j < GRID_SIZE) {
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

                if (i < GRID_SIZE) {
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

        for (int i = 0; i <= GRID_SIZE; i++) {
            for (int j = 0; j <= GRID_SIZE; j++) {
                Circle dot = new Circle(j * CELL_SIZE + offsetX, i * CELL_SIZE + marginTop, 7, Color.BLACK);
                slitherlinkGrid.getChildren().add(dot);
            }
        }
    }

    private static void handleLineClick(MouseEvent e, Line line) {
        if (e.getButton() == MouseButton.PRIMARY) {
            if (!hasCross(line)) { // Vérifie s'il y a une croix
                toggleLine(line);
            }
        } else if (e.getButton() == MouseButton.SECONDARY) {
            if (!isLineActive(line)) { // Vérifie si une ligne est déjà tracée
                toggleCross(line);
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
        line.setStroke(line.getStroke().equals(Color.TRANSPARENT) ? Color.BLACK : Color.TRANSPARENT);
    }

    private static void toggleCross(Line line) {
        boolean hasCross = slitherlinkGrid.getChildren().stream()
        .anyMatch(node -> node instanceof Line && node.getUserData() == line);

        if (hasCross) {
            slitherlinkGrid.getChildren().removeIf(node -> node instanceof Line && node.getUserData() == line);
        } else {
            // Création des croix selon l'orientation de la ligne
            Line cross1, cross2;

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
            cross1.setStroke(Color.RED);
            cross1.setStrokeWidth(2);
            cross1.setUserData(line);
            
            cross2.setStroke(Color.RED);
            cross2.setStrokeWidth(2);
            cross2.setUserData(line);
            
            slitherlinkGrid.getChildren().addAll(cross1, cross2);
        }
    }

    private static Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 18));
        button.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 10;");
        return button;
    }
}