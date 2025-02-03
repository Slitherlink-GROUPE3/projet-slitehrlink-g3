package com.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;



public class GameScene {
    private static final int GRID_SIZE = 10; // Nombre de cellules
    private static Pane slitherlinkGrid;
    private static double CELL_SIZE;
    private static StackPane gridContainer;
    private static HBox root;
    private static int checkCounter = 3; 

    public static void show(Stage primaryStage) {
        // Création du conteneur principal avec redimensionnement automatique
        root = new HBox();
        slitherlinkGrid = new Pane();
        gridContainer = new StackPane(slitherlinkGrid);
        Pane emptyPane = new Pane(); // Partie vide de droite

        // Ajout de la barre latérale pour les boutons 
        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setStyle("-fx-background-color: #E5D5B0; -fx-padding: 20;");

        Button helpButton = createStyledButton("   AIDE   ?  ");
        Button checkButton = createStyledButton("Check");
        checkButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;-fx-padding: 10px 20px; -fx-background-radius: 10;");
        
       

        // Ajout du compteur (Texte à côté de "Check")
        Text checkCount = new Text(String.valueOf(checkCounter));
        checkCount.setFont(Font.font("Arial", 20));
        checkCount.setFill(Color.BLACK);

        checkButton.setOnAction(e -> {
            if (checkCounter > 0) {  // Vérifie si checkCounter est encore positif
                checkCounter--; 
                checkCount.setText(String.valueOf(checkCounter));

                if (checkCounter == 0) {  // Désactive le bouton quand il atteint 0
                    checkButton.setDisable(true);
                }
            }
        });
        

        // Conteneur pour "Check" + compteur
        HBox checkContainer = new HBox(15, checkButton, checkCount);
        checkContainer.setAlignment(Pos.CENTER);


        Button hypothesisButton = createStyledButton("Hypothèse");  // Bouton Hypothèse

        
       


        // Lier les largeurs des panneaux pour maintenir 50/50 lors du redimensionnement
        gridContainer.prefWidthProperty().bind(root.widthProperty().multiply(0.5));
        emptyPane.prefWidthProperty().bind(root.widthProperty().multiply(0.5));

        buttonBox.getChildren().addAll(helpButton, checkContainer, hypothesisButton);

        //root.getChildren().addAll(gridContainer, emptyPane);
        root.getChildren().addAll(gridContainer, buttonBox);

        // Définition de la scène avec un fond beige
        Scene scene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
        root.setStyle("-fx-background-color: #E5D5B0;");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Slitherlink Game");
        primaryStage.setMaximized(true); // Plein écran par défaut

        // Écouteur pour redimensionner la grille dynamiquement, y compris après passage en plein écran
        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateGrid(scene.getWidth(), scene.getHeight()));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updateGrid(scene.getWidth(), scene.getHeight()));
        primaryStage.maximizedProperty().addListener((obs, oldVal, isMaximized) -> updateGrid(scene.getWidth(), scene.getHeight()));
        primaryStage.fullScreenProperty().addListener((obs, oldVal, isFullScreen) -> updateGrid(scene.getWidth(), scene.getHeight()));

        primaryStage.show();
        updateGrid(scene.getWidth(), scene.getHeight());
    }

    private static void updateGrid(double width, double height) {
        slitherlinkGrid.getChildren().clear(); // Réinitialise la grille
        CELL_SIZE = Math.min(width / 2, height * 0.8) / (GRID_SIZE + 1); // Ajuste la taille des cellules et laisse une marge en haut
        double marginTop = height * 0.1; // Définit un espace entre le haut de la fenêtre et la grille

        // Ajout des indices pour le jeu (simulation)
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
                    Text numberText = new Text(j * CELL_SIZE + CELL_SIZE / 3, i * CELL_SIZE + CELL_SIZE / 1.5 + marginTop, String.valueOf(numbers[i][j]));
                    numberText.setFont(Font.font(18));
                    slitherlinkGrid.getChildren().add(numberText);
                }
            }
        }

        // Ajout des lignes interactives
        for (int i = 0; i <= GRID_SIZE; i++) {
            for (int j = 0; j <= GRID_SIZE; j++) {
                if (j < GRID_SIZE) {
                    Line horizontalLine = new Line(j * CELL_SIZE, i * CELL_SIZE + marginTop, (j + 1) * CELL_SIZE, i * CELL_SIZE + marginTop);
                    horizontalLine.setStroke(Color.WHITE);
                    horizontalLine.setStrokeWidth(5);
                    horizontalLine.setOnMouseClicked(GameScene::toggleLine);
                    slitherlinkGrid.getChildren().add(horizontalLine);
                }
                if (i < GRID_SIZE) {
                    Line verticalLine = new Line(j * CELL_SIZE, i * CELL_SIZE + marginTop, j * CELL_SIZE, (i + 1) * CELL_SIZE + marginTop);
                    verticalLine.setStroke(Color.WHITE);
                    verticalLine.setStrokeWidth(5);
                    verticalLine.setOnMouseClicked(GameScene::toggleLine);
                    slitherlinkGrid.getChildren().add(verticalLine);
                }
            }
        }

        // Ajout des points après les lignes
        for (int i = 0; i <= GRID_SIZE; i++) {
            for (int j = 0; j <= GRID_SIZE; j++) {
                Circle dot = new Circle(j * CELL_SIZE, i * CELL_SIZE + marginTop, 3, Color.BLACK);
                slitherlinkGrid.getChildren().add(dot);
            }
        }
    }

    // Fonction pour activer/désactiver une ligne
    private static void toggleLine(MouseEvent e) {
        Line line = (Line) e.getSource();
        if (line.getStroke().equals(Color.WHITE)) {
            line.setStroke(Color.BLACK); // Active la barre
        } else {
            line.setStroke(Color.WHITE); // Désactive la barre
        }
    }

     // Fonction pour créer des boutons stylisés
     private static Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 18));
        button.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 10;");
       
        return button;
    }

   
}
