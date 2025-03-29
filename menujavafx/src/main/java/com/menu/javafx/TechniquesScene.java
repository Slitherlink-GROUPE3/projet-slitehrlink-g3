package com.menu.javafx;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class TechniquesScene {

    // Constantes de couleurs du Menu (cohérence avec GameScene)
    private static final String MAIN_COLOR = "#3A7D44"; // Vert principal
    private static final String SECONDARY_COLOR = "#F2E8CF"; // Beige clair
    private static final String ACCENT_COLOR = "#BC4749"; // Rouge-brique
    private static final String DARK_COLOR = "#386641"; // Vert foncé
    private static final String LIGHT_COLOR = "#A7C957"; // Vert clair

    // Variables pour gérer le tutoriel
    private static int currentStep = 0;
    private static Map<String, Line> gridLines = new HashMap<>();
    private static Pane tutorialGrid;
    private static VBox instructionsPanel;
    private static TextFlow instructionsText;
    private static Button nextStepButton;
    private static Text stepTitle;
    private static double CELL_SIZE = 60;

    // Structure statique d'une petite grille pour le tutoriel
    private static final int[][] tutorialNumbers = {
            {3, 2, 2},
            {1, 3, 1},
            {0, 2, 3}
    };

    private static Stage primaryStage;

    public static void show(Stage primaryStage) {
        BorderPane root = new BorderPane();
        TechniquesScene.currentStep = 0; // Réinitialiser l'étape
        root.setStyle("-fx-background-color: " + SECONDARY_COLOR + ";");
        TechniquesScene.primaryStage = primaryStage; // Stocker la référence au stage

        // Titre du tutoriel
        Text title = new Text("Tutoriel Slitherlink");
        title.setFont(Font.font("Montserrat", FontWeight.BOLD, 32));
        title.setFill(Color.web(DARK_COLOR));

        // Bouton Retour
        Button backButton = createStyledButton("Retour au menu", false);
        Stage finalPrimaryStage = primaryStage;
        backButton.setOnAction(e -> Menu.show(finalPrimaryStage));

        // En-tête avec titre et bouton retour
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(30, 20, 30, 20));
        header.getChildren().addAll(title, backButton);

        // CORRECTION: Créer d'abord le panel d'instructions
        instructionsPanel = createInstructionsPanel();

        // Créer la grille de tutoriel - AJUSTEMENT TAILLE
        tutorialGrid = new Pane();
        tutorialGrid.setMinSize(400, 400);
        StackPane gridContainer = new StackPane(tutorialGrid);
        gridContainer.setPadding(new Insets(30));
        gridContainer.setMinSize(450, 450);
        gridContainer.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.9);" +
                        "-fx-background-radius: 15;" +
                        "-fx-padding: 30;"
        );

        DropShadow gridShadow = new DropShadow();
        gridShadow.setColor(Color.web("#000000", 0.2));
        gridShadow.setRadius(10);
        gridShadow.setOffsetY(5);
        gridContainer.setEffect(gridShadow);

        // Initialiser la grille
        initializeGrid();

        // Conteneur principal pour la grille et les instructions
        HBox mainContent = new HBox(40);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(20, 30, 30, 30));
        mainContent.getChildren().addAll(gridContainer, instructionsPanel);

        // Assembler le tout
        root.setTop(header);
        root.setCenter(mainContent);

        // Créer et configurer la scène
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tutoriel Slitherlink");

        // Afficher l'étape initiale
        showStep(0);

        // Afficher la scène
        primaryStage.show();
    }
    
    private static VBox createInstructionsPanel() {
        VBox panel = new VBox(24); // Augmentation de l'espacement vertical
        panel.setPadding(new Insets(30)); // Plus de padding
        panel.setMinWidth(420); // Augmentation largeur minimale
        panel.setPrefWidth(450); // Taille préférée plus grande
        panel.setMaxWidth(500); // Limiter la largeur maximale
        panel.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" + // Plus opaque
            "-fx-background-radius: 15;" +
            "-fx-padding: 25;" // Plus de padding
        );
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.2));
        shadow.setRadius(10);
        shadow.setOffsetY(5);
        panel.setEffect(shadow);
        
        // Titre de l'étape
        stepTitle = new Text("Bienvenue au tutoriel Slitherlink");
        stepTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 22)); // Plus grand
        stepTitle.setFill(Color.web(DARK_COLOR));
        
        // Zone de texte pour les instructions - AMÉLIORATION DU TEXTFLOW
        instructionsText = new TextFlow();
        instructionsText.setLineSpacing(8); // Plus d'espacement entre lignes
        instructionsText.setPrefWidth(panel.getPrefWidth() - 60); // Largeur fixe
        instructionsText.setTextAlignment(TextAlignment.LEFT);
        
        // ScrollPane pour le texte long
        ScrollPane scrollPane = new ScrollPane(instructionsText);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350); // Hauteur suffisante
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Pas de barre horizontale
        
        // Bouton pour passer à l'étape suivante
        nextStepButton = createStyledButton("Suivant", true);
        nextStepButton.setPrefWidth(200); // Largeur fixe pour le bouton
        nextStepButton.setOnAction(e -> {
            animateButtonClick(nextStepButton);
            currentStep++;
            showStep(currentStep);
        });
        
        panel.getChildren().addAll(stepTitle, scrollPane, nextStepButton);
        return panel;
    }
    
    private static void initializeGrid() {
        tutorialGrid.getChildren().clear();
        gridLines.clear();
        
        int gridRows = tutorialNumbers.length;
        int gridCols = tutorialNumbers[0].length;
        double gridWidth = (gridCols + 1) * CELL_SIZE;
        double gridHeight = (gridRows + 1) * CELL_SIZE;
        
        // Créer les chiffres
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridCols; j++) {
                int value = tutorialNumbers[i][j];
                if (value > 0) {
                    Text numberText = new Text(String.valueOf(value));
                    numberText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
                    numberText.setFill(Color.web(DARK_COLOR));
                    numberText.setX((j + 1) * CELL_SIZE - 6);
                    numberText.setY((i + 1) * CELL_SIZE + 8);
                    tutorialGrid.getChildren().add(numberText);
                }
            }
        }
        
        // Créer les points
        for (int i = 0; i <= gridRows; i++) {
            for (int j = 0; j <= gridCols; j++) {
                Circle dot = new Circle((j + 0.5) * CELL_SIZE, (i + 0.5) * CELL_SIZE, 4);
                dot.setFill(Color.web(DARK_COLOR));
                tutorialGrid.getChildren().add(dot);
            }
        }
        
        // Créer les lignes horizontales
        for (int i = 0; i <= gridRows; i++) {
            for (int j = 0; j < gridCols; j++) {
                Line line = new Line(
                    (j + 0.5) * CELL_SIZE, (i + 0.5) * CELL_SIZE,
                    (j + 1.5) * CELL_SIZE, (i + 0.5) * CELL_SIZE
                );
                line.setStrokeWidth(3);
                line.setStroke(Color.TRANSPARENT);
                line.setId("H_" + i + "_" + j);
                
                // Ajouter une hitbox plus large pour faciliter le clic
                Rectangle hitbox = new Rectangle(
                    (j + 0.5) * CELL_SIZE - 5, (i + 0.5) * CELL_SIZE - 10,
                    CELL_SIZE + 10, 20
                );
                hitbox.setFill(Color.TRANSPARENT);
                hitbox.setUserData(line);
                
                hitbox.setOnMouseClicked(e -> handleLineClick(e, line));
                
                gridLines.put(line.getId(), line);
                tutorialGrid.getChildren().addAll(line, hitbox);
            }
        }
        
        // Créer les lignes verticales
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j <= gridCols; j++) {
                Line line = new Line(
                    (j + 0.5) * CELL_SIZE, (i + 0.5) * CELL_SIZE,
                    (j + 0.5) * CELL_SIZE, (i + 1.5) * CELL_SIZE
                );
                line.setStrokeWidth(3);
                line.setStroke(Color.TRANSPARENT);
                line.setId("V_" + i + "_" + j);
                
                // Ajouter une hitbox plus large
                Rectangle hitbox = new Rectangle(
                    (j + 0.5) * CELL_SIZE - 10, (i + 0.5) * CELL_SIZE - 5,
                    20, CELL_SIZE + 10
                );
                hitbox.setFill(Color.TRANSPARENT);
                hitbox.setUserData(line);
                
                hitbox.setOnMouseClicked(e -> handleLineClick(e, line));
                
                gridLines.put(line.getId(), line);
                tutorialGrid.getChildren().addAll(line, hitbox);
            }
        }
    }
    
    private static void handleLineClick(MouseEvent e, Line line) {
        // Gérer différemment selon l'étape du tutoriel
        if (currentStep == 1) {
            // Étape 1: Tracer une ligne - accepter n'importe quelle ligne
            if (e.getButton() == MouseButton.PRIMARY) {
                line.setStroke(Color.web(DARK_COLOR));
                // Validation plus flexible - n'importe quelle ligne tracée est acceptée
                nextStepButton.setDisable(false);
                highlightInstructions("Parfait ! Vous avez tracé une ligne. Cliquez sur \"Suivant\" pour continuer.", Color.web(MAIN_COLOR));
            }
        }
        else if (currentStep == 2) {
            // Étape 2: Tracer une croix - accepter n'importe quelle croix
            if (e.getButton() == MouseButton.SECONDARY) {
                createCross(line, false);
                // Validation plus flexible - n'importe quelle croix placée est acceptée
                nextStepButton.setDisable(false);
                highlightInstructions("Excellent ! Vous avez placé une croix. Cliquez sur \"Suivant\" pour continuer.", Color.web(MAIN_COLOR));
            }
        }
        else if (currentStep == 3) {
            // Étape 3: Application de la règle du 3
            if (e.getButton() == MouseButton.PRIMARY) {
                line.setStroke(Color.web(DARK_COLOR));
                
                // Compter combien de lignes sont tracées autour du chiffre 3 en [0,0]
                int count = 0;
                if (!gridLines.get("H_0_0").getStroke().equals(Color.TRANSPARENT)) count++;
                if (!gridLines.get("V_0_0").getStroke().equals(Color.TRANSPARENT)) count++;
                if (!gridLines.get("H_1_0").getStroke().equals(Color.TRANSPARENT)) count++;
                if (!gridLines.get("V_0_1").getStroke().equals(Color.TRANSPARENT)) count++;
                
                if (count >= 3) { // Modifier pour accepter 3 ou plus segments
                    nextStepButton.setDisable(false);
                    highlightInstructions("Parfait ! Vous avez entouré le chiffre 3 avec au moins 3 segments. Cliquez sur \"Suivant\" pour continuer.", Color.web(MAIN_COLOR));
                }
            }
        }
    }
    
    private static void createCross(Line line, boolean isPreview) {
        // Création des croix selon l'orientation de la ligne
        Line cross1, cross2;
        
        if (line.getStartX() == line.getEndX()) { // Ligne verticale
            cross1 = new Line(
                line.getStartX() - 8, line.getStartY() + 8,
                line.getEndX() + 8, line.getEndY() - 8
            );
            cross2 = new Line(
                line.getStartX() - 8, line.getEndY() - 8,
                line.getEndX() + 8, line.getStartY() + 8
            );
        } else { // Ligne horizontale
            cross1 = new Line(
                line.getStartX() + 8, line.getStartY() - 8,
                line.getEndX() - 8, line.getEndY() + 8
            );
            cross2 = new Line(
                line.getStartX() + 8, line.getEndY() + 8,
                line.getEndX() - 8, line.getStartY() - 8
            );
        }
        
        cross1.setStrokeWidth(3);
        cross1.setUserData(line);
        cross2.setStrokeWidth(3);
        cross2.setUserData(line);
        
        if (isPreview) {
            // Pour la prévisualisation: croix semi-transparente
            cross1.setStroke(Color.web(ACCENT_COLOR, 0.4)); // 40% opacité
            cross2.setStroke(Color.web(ACCENT_COLOR, 0.4));
            
            // Animation de pulsation pour la prévisualisation
            FadeTransition fade1 = new FadeTransition(Duration.millis(800), cross1);
            fade1.setFromValue(0.2);
            fade1.setToValue(0.6);
            fade1.setCycleCount(-1);
            fade1.setAutoReverse(true);
            fade1.play();
            
            FadeTransition fade2 = new FadeTransition(Duration.millis(800), cross2);
            fade2.setFromValue(0.2);
            fade2.setToValue(0.6);
            fade2.setCycleCount(-1);
            fade2.setAutoReverse(true);
            fade2.play();
        } else {
            // Pour une vraie croix: couleur normale
            cross1.setStroke(Color.web(ACCENT_COLOR));
            cross2.setStroke(Color.web(ACCENT_COLOR));
        }
        
        tutorialGrid.getChildren().addAll(cross1, cross2);
    }
    
    private static void showStep(int step) {
        // Réinitialiser les éléments
        nextStepButton.setDisable(true);
        instructionsText.getChildren().clear();
        
        switch (step) {
            case 0:
                // Introduction
                stepTitle.setText("Bienvenue au tutoriel Slitherlink");
                addInstructionParagraph("Le Slitherlink est un jeu de puzzle où vous devez tracer un chemin fermé en suivant les indices numériques.");
                addInstructionParagraph("Ce tutoriel vous guidera à travers les règles de base pour résoudre un Slitherlink.");
                addInstructionParagraph("Cliquez sur \"Suivant\" pour commencer l'apprentissage.");
                nextStepButton.setDisable(false);
                break;
                
            case 1:
                // Tracer une ligne
                stepTitle.setText("Étape 1: Tracer une ligne");
                addInstructionParagraph("Pour commencer, apprenons à tracer une ligne:");
                addInstructionParagraph("• Cliquez avec le bouton gauche de la souris sur n'importe quel segment pour y tracer une ligne.");
                addInstructionHighlight("Essayez maintenant de tracer une ligne!");
                
                // Mettre en évidence la ligne à tracer
                Line highlightLine = gridLines.get("H_0_1");
                highlightLine.setStroke(Color.LIGHTGRAY);
                FadeTransition fadeTransition = new FadeTransition(Duration.millis(800), highlightLine);
                fadeTransition.setFromValue(0.3);
                fadeTransition.setToValue(0.7);
                fadeTransition.setCycleCount(-1);
                fadeTransition.setAutoReverse(true);
                fadeTransition.play();
                break;
                
            case 2:
                // Placer une croix
                stepTitle.setText("Étape 2: Placer une croix");
                addInstructionParagraph("Parfois, vous savez qu'un segment ne doit PAS faire partie de la solution:");
                addInstructionParagraph("• Cliquez avec le bouton droit de la souris sur un segment pour y placer une croix.");
                addInstructionParagraph("• Les croix vous aident à vous rappeler où vous avez déterminé qu'il ne doit pas y avoir de ligne.");
                addInstructionHighlight("Placez une croix sur n'importe quel segment.");
                
                // Montrer une prévisualisation de croix au lieu d'une ligne
                Line crossLine = gridLines.get("V_0_2");
                // Plutôt que de mettre un trait gris, nous allons créer une croix transparente
                createCross(crossLine, true); // true pour indiquer que c'est une prévisualisation
                break;
                
            case 3:
                // Règle de base: le chiffre 3
                stepTitle.setText("Étape 3: La règle du chiffre 3");
                addInstructionParagraph("La règle fondamentale de Slitherlink:");
                addInstructionParagraph("• Chaque chiffre indique le nombre exact de segments qui doivent être tracés autour de cette cellule.");
                addInstructionParagraph("• Une cellule avec le chiffre 3 doit avoir exactement 3 segments tracés autour d'elle.");
                addInstructionHighlight("Tracez au moins 3 segments autour du chiffre 3 en haut à gauche.");
                
                // Réinitialiser les lignes autour du 3
                gridLines.get("H_0_0").setStroke(Color.TRANSPARENT);
                gridLines.get("V_0_0").setStroke(Color.TRANSPARENT);
                gridLines.get("H_1_0").setStroke(Color.TRANSPARENT);
                gridLines.get("V_0_1").setStroke(Color.TRANSPARENT);
                break;
                
            case 4: // Directement l'ancienne étape 6 (félicitations)
                // Félicitations et fin
                stepTitle.setText("Félicitations!");
                addInstructionParagraph("Vous avez terminé le tutoriel de base de Slitherlink!");
                addInstructionParagraph("Vous pouvez maintenant appliquer ces règles pour résoudre des puzzles Slitherlink.");
                addInstructionParagraph("N'oubliez pas de vérifier votre solution en vous assurant que:");
                addInstructionParagraph("• Tous les chiffres sont respectés");
                
                Button startGameButton = createStyledButton("Commencer à jouer", true);
                startGameButton.setOnAction(e -> GameScene.show(primaryStage));
            
                instructionsText.getChildren().add(new Text("\n\n"));
                instructionsText.getChildren().add(startGameButton);
                
                nextStepButton.setText("Retour au menu");
                nextStepButton.setOnAction(e -> Menu.show(primaryStage));
                nextStepButton.setDisable(false);
                break;
                
            default:
                // Retour au menu si on dépasse les étapes
                Menu.show(primaryStage);
                break;
        }
    }
    
    private static void addInstructionParagraph(String text) {
        Text paragraph = new Text(text + "\n\n");
        paragraph.setFont(Font.font("Arial", 16)); // Plus grand
        paragraph.setFill(Color.BLACK);
        paragraph.setWrappingWidth(instructionsText.getPrefWidth() - 20); // Wrapping pour éviter débordement
        instructionsText.getChildren().add(paragraph);
    }
    
    private static void addInstructionHighlight(String text) {
        Text highlight = new Text(text + "\n\n");
        highlight.setFont(Font.font("Arial", FontWeight.BOLD, 16)); // Plus grand
        highlight.setFill(Color.web(MAIN_COLOR));
        highlight.setWrappingWidth(instructionsText.getPrefWidth() - 20); // Wrapping pour éviter débordement
        instructionsText.getChildren().add(highlight);
    }
    
    private static void highlightInstructions(String text, Color color) {
        Text highlight = new Text(text);
        highlight.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        highlight.setFill(color);
        highlight.setWrappingWidth(instructionsText.getPrefWidth() - 20); // Éviter débordement
        
        instructionsText.getChildren().clear();
        instructionsText.getChildren().add(highlight);
    }
    
    private static Button createStyledButton(String text, boolean isPrimary) {
        Button button = new Button(text);
        button.setFont(Font.font("Montserrat", FontWeight.BOLD, 14));
        
        if (isPrimary) {
            button.setTextFill(Color.WHITE);
            button.setStyle(
                "-fx-background-color: " + MAIN_COLOR + ";" +
                "-fx-background-radius: 30;" +
                "-fx-border-color: " + DARK_COLOR + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 30;" +
                "-fx-padding: 8 16;" +
                "-fx-cursor: hand;"
            );
        } else {
            button.setTextFill(Color.web(DARK_COLOR));
            button.setStyle(
                "-fx-background-color: " + SECONDARY_COLOR + ";" +
                "-fx-background-radius: 30;" +
                "-fx-border-color: " + MAIN_COLOR + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 30;" +
                "-fx-padding: 8 16;" +
                "-fx-cursor: hand;"
            );
        }
        
        return button;
    }
    
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
}

