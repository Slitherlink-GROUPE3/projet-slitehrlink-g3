package com.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

// Importation local
import com.menu.javafx.TopBar;

public class GameScene {
    private static final int GRID_SIZE = 10;
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
        
        public Color getColor() {
            return color;
        }
    }

    public static void show(Stage primaryStage) {
        
        mainLayer = new VBox();
        mainLayer.setStyle("-fx-padding: 0; -fx-background-color: #E5D5B0;"); // Supprime le padding et définit la couleur de fond

        //     TopBar(Stage primaryStage, String pseudoJoueur, String niveau, String difficulte) {


        /**
         * 
         *  PARTIE LEO : TOPBAR
         */


        TopBar topBar = new TopBar(primaryStage, "Jacoboni", "5", "Facile");
        // Créer un minuteur qui met à jour le chronomètre chaque seconde
        java.util.Timer timer = new java.util.Timer();
        final int[] secondsElapsed = {0};
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                secondsElapsed[0]++;
                int minutes = secondsElapsed[0] / 60;
                int seconds = secondsElapsed[0] % 60;
                
                // Mettre à jour l'interface utilisateur sur le thread JavaFX
                javafx.application.Platform.runLater(() -> {
                    topBar.updateChronometer(minutes, seconds);
                });
            }
        }, 0, 1000); // Démarrer tout de suite, mettre à jour chaque seconde


        /**
         * 
         *  FIN PARTIE LEO : TOPBAR
         */


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
        hypothesisButton.setOnAction(e -> {
            if (!isHypothesisActive) {
                // Sauvegarde l'état initial
                originalLineStates.clear();
                for (Node node : slitherlinkGrid.getChildren()) {
                    if (node instanceof Line) {
                        Line line = (Line) node;
                        if (line.getUserData() == null) { // Ne sauvegarde que les lignes principales
                            originalLineStates.put(line, (Color) line.getStroke());
                        }
                    }
                }
                
                isHypothesisActive = true;
                hypothesisButton.setText("Terminer Hypothèse");
            } else {
                // Affiche la boîte de dialogue de confirmation
                Stage dialog = new Stage();
                VBox dialogVBox = new VBox(10);
                dialogVBox.setAlignment(Pos.CENTER);
                dialogVBox.setStyle("-fx-background-color: #E5D5B0; -fx-padding: 20;");
                Button confirmButton = createStyledButton("Confirmer");
                Button cancelButton = createStyledButton("Annuler");
                
                confirmButton.setOnAction(event -> {
                    // Enregistrer toutes les actions d'hypothèse comme un seul grand mouvement dans l'historique
                    boolean anyChanges = false;
                    
                    // Traite les lignes
                    for (Line line : originalLineStates.keySet()) {
                        if (line.getStroke() == Color.BLUE) {
                            line.setStroke(Color.BLACK); // Valide les modifications
                            anyChanges = true;
                        }
                    }
                    
                    // Traite les croix d'hypothèse
                    for (Node node : slitherlinkGrid.getChildren()) {
                        if (node instanceof Line && "hypothesis".equals(node.getUserData())) {
                            Line crossLine = (Line) node;
                            crossLine.setStroke(Color.RED);
                            crossLine.setUserData(null); // Ou une autre valeur qui indique que ce n'est plus une hypothèse
                            anyChanges = true;
                        }
                    }
                    
                    // Si des changements ont été effectués, on les ajoute à l'historique
                    if (anyChanges) {
                        // Supprime les mouvements futurs si on était revenu en arrière
                        if (currentMoveIndex < moveHistory.size() - 1) {
                            moveHistory = new ArrayList<>(moveHistory.subList(0, currentMoveIndex + 1));
                        }
                        
                        // Ajoute le mouvement d'hypothèse complète à l'historique
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
                    for (Map.Entry<Line, Color> entry : originalLineStates.entrySet()) {
                        entry.getKey().setStroke(entry.getValue()); // Annule les modifications
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
        
        // Boutons d'historique
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
        mainLayer.getChildren().add(0, topBarComponent); // Ajout au début (index 0)

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
    
    // Méthode pour naviguer dans l'historique
    private static void navigateHistory(int direction) {
        // Désactive le mode hypothèse s'il est actif
        if (isHypothesisActive) {
            isHypothesisActive = false;
            // Annule toutes les modifications d'hypothèse
            for (Map.Entry<Line, Color> entry : originalLineStates.entrySet()) {
                entry.getKey().setStroke(entry.getValue());
            }
            originalLineStates.clear();
        }
        
        if (direction < 0) { // Précédent
            if (currentMoveIndex >= 0) {
                Move move = moveHistory.get(currentMoveIndex);
                undoMove(move);
                currentMoveIndex--;
            }
        } else { // Suivant
            if (currentMoveIndex < moveHistory.size() - 1) {
                currentMoveIndex++;
                Move move = moveHistory.get(currentMoveIndex);
                redoMove(move);
            }
        }
        
        updateHistoryButtons();
    }
    
    // Méthode pour annuler un mouvement
    private static void undoMove(Move move) {
        if (move.getAction().equals("line")) {
            move.getLine().setStroke(Color.TRANSPARENT);
        } else if (move.getAction().equals("cross")) {
            // Supprime la croix
            slitherlinkGrid.getChildren().removeIf(node -> 
                node instanceof Line && node.getUserData() == move.getLine()
            );
            slitherlinkGrid.getChildren().removeIf(node -> 
                node instanceof Rectangle && node.getUserData() == move.getLine()
            );
        } else if (move.getAction().equals("remove_cross")) {
            // Recrée la croix
            createCross(move.getLine(), false);
        } else if (move.getAction().equals("hypothesis_confirm")) {
            // Pour l'instant, on ne peut pas annuler une confirmation d'hypothèse
            // C'est un cas complexe qui nécessiterait de stocker plus d'informations
        }
    }
    
    // Méthode pour refaire un mouvement
    private static void redoMove(Move move) {
        if (move.getAction().equals("line")) {
            move.getLine().setStroke(move.getColor());
        } else if (move.getAction().equals("cross")) {
            createCross(move.getLine(), false);
        } else if (move.getAction().equals("remove_cross")) {
            // Ne rien faire, la croix est déjà supprimée
        } else if (move.getAction().equals("hypothesis_confirm")) {
            // Pour l'instant, rien de spécial à faire pour confirmer l'hypothèse
            // Les changements ont déjà été appliqués
        }
    }
    
    // Méthode pour mettre à jour l'état des boutons d'historique
    private static void updateHistoryButtons() {
        prevButton.setDisable(currentMoveIndex < 0);
        nextButton.setDisable(currentMoveIndex >= moveHistory.size() - 1);
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