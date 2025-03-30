package com.menu.javafx;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.application.Platform;

/**
 * Classe responsable de la scène tutorielle pour apprendre les techniques Slitherlink
 */
public class TechniquesScene {
    // Constantes de couleurs (cohérentes avec GameScene et les autres scènes)
    private static final String MAIN_COLOR = "#3A7D44";     // Vert principal
    private static final String SECONDARY_COLOR = "#F2E8CF"; // Beige clair
    private static final String ACCENT_COLOR = "#BC4749";   // Rouge-brique
    private static final String DARK_COLOR = "#386641";     // Vert foncé
    private static final String LIGHT_COLOR = "#A7C957";    // Vert clair

    // Dimensions et tailles
    private static final double CELL_SIZE = 60;
    private static final double DOT_RADIUS = 4;
    private static final double LINE_THICKNESS = 3;
    private static final double PANEL_WIDTH = 450;

    // Variables pour gérer le tutoriel
    private static int currentStep = 0;
    private static Map<String, Line> gridLines = new HashMap<>();
    private static Map<String, List<Line>> gridCrosses = new HashMap<>();
    private static Pane tutorialGrid;
    private static VBox instructionsPanel;
    private static TextFlow instructionsText;
    private static Button nextStepButton;
    private static Text stepTitle;
    private static Stage primaryStage;

    // Structure statique d'une petite grille pour le tutoriel
    private static final int[][] tutorialNumbers = {
        {3, 2, 1},
        {3, -1, -1},
        {3, -1, 3}
    };
    
    /**
     * Affiche la scène du tutoriel technique
     * @param stage La fenêtre principale
     */
    public static void show(Stage stage) {
        // Réinitialiser les variables statiques pour éviter les problèmes avec plusieurs affichages
        currentStep = 0;
        gridLines.clear();
        gridCrosses.clear();
        TechniquesScene.primaryStage = stage;
        
        // Créer le conteneur principal
        BorderPane root = new BorderPane();
        TechniquesScene.currentStep = 0; // Réinitialiser l'étape
        root.setStyle("-fx-background-color: " + SECONDARY_COLOR + ";");
        
        // En-tête avec titre et bouton retour
        HBox header = createHeader();
        root.setTop(header);
        
        // Créer les panneaux d'instructions et la grille
        instructionsPanel = createInstructionsPanel();
        StackPane gridContainer = createGridContainer();
        
        // Zone principale avec grille et instructions
        HBox mainContent = new HBox(40);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(20, 30, 30, 30));
        mainContent.getChildren().addAll(gridContainer, instructionsPanel);
        root.setCenter(mainContent);
        
        // Configurer et afficher la scène
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Slitherlink - Tutoriel Techniques");
        
        // Initialiser et montrer la première étape du tutoriel
        showStep(currentStep);
        
        // Ajouter une animation d'apparition
        root.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        primaryStage.show();
    }
    
    /**
     * Crée l'en-tête avec le titre et le bouton retour
     * @return HBox contenant les éléments d'en-tête
     */
    private static HBox createHeader() {
        // Titre principal
        Text title = new Text("Tutoriel Slitherlink");
        title.setFont(Font.font("Montserrat", FontWeight.BOLD, 32));
        title.setFill(Color.web(DARK_COLOR));
        
        // Effet pour le titre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(DARK_COLOR, 0.5));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        
        Glow glow = new Glow(0.4);
        glow.setInput(shadow);
        title.setEffect(glow);
        
        // Bouton retour
        Button backButton = createStyledButton("Retour au menu", false);
        backButton.setOnAction(e -> {
            animateButtonClick(backButton);
            
            // Animation de sortie avant retour au menu
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), backButton.getScene().getRoot());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(evt -> Menu.show(primaryStage));
            fadeOut.play();
        });
        
        // Assembler l'en-tête
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(30, 20, 30, 20));
        header.getChildren().addAll(title, backButton);
        
        return header;
    }
    
    /**
     * Crée le conteneur pour la grille de jeu
     * @return StackPane contenant la grille
     */
    private static StackPane createGridContainer() {
        // Créer la grille
        tutorialGrid = new Pane();
        tutorialGrid.setMinSize(400, 400);
        
        // Conteneur avec effet d'ombre
        StackPane gridContainer = new StackPane(tutorialGrid);
        gridContainer.setPadding(new Insets(30));
        gridContainer.setMinSize(450, 450);
        gridContainer.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.9);" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 30;"
        );
        
        // Ajouter un effet d'ombre
        DropShadow gridShadow = new DropShadow();
        gridShadow.setColor(Color.web("#000000", 0.2));
        gridShadow.setRadius(10);
        gridShadow.setOffsetY(5);
        gridContainer.setEffect(gridShadow);
        
        // Initialiser le contenu de la grille
        initializeGrid();
        
        return gridContainer;
    }
    
    /**
     * Crée le panneau d'instructions du tutoriel
     * @return VBox contenant les instructions
     */
    private static VBox createInstructionsPanel() {
        // Conteneur principal
        VBox panel = new VBox(24);
        panel.setPadding(new Insets(30));
        panel.setMinWidth(420);
        panel.setPrefWidth(PANEL_WIDTH);
        panel.setMaxWidth(500);
        panel.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 25;"
        );
        
        // Effet d'ombre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.2));
        shadow.setRadius(10);
        shadow.setOffsetY(5);
        panel.setEffect(shadow);
        
        // Titre de l'étape
        stepTitle = new Text("Bienvenue au tutoriel Slitherlink");
        stepTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 22));
        stepTitle.setFill(Color.web(DARK_COLOR));
        
        // Zone de texte défilante pour les instructions
        instructionsText = new TextFlow();
        instructionsText.setLineSpacing(8);
        instructionsText.setPrefWidth(PANEL_WIDTH - 60);
        instructionsText.setTextAlignment(TextAlignment.LEFT);
        
        // ScrollPane pour permettre le défilement
        ScrollPane scrollPane = new ScrollPane(instructionsText);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        
        // Bouton pour passer à l'étape suivante
        nextStepButton = createStyledButton("Suivant", true);
        nextStepButton.setPrefWidth(200);
        nextStepButton.setOnAction(e -> {
            animateButtonClick(nextStepButton);
            currentStep++;
            showStep(currentStep);
        });
        
        // Assembler le panneau
        panel.getChildren().addAll(stepTitle, scrollPane, nextStepButton);
        return panel;
    }
    
    /**
     * Initialise la grille de jeu avec les nombres, points, et lignes potentielles
     */
    private static void initializeGrid() {
        // Vider la grille existante
        tutorialGrid.getChildren().clear();
        gridLines.clear();
        gridCrosses.clear();
        
        // Dimensions de la grille
        int gridRows = tutorialNumbers.length;
        int gridCols = tutorialNumbers[0].length;
        
        // Dessiner les nombres
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridCols; j++) {
                int value = tutorialNumbers[i][j];
                if (value > 0) {
                    // Créer et positionner le texte du nombre
                    Text numberText = new Text(String.valueOf(value));
                    numberText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
                    numberText.setFill(Color.web(DARK_COLOR));
                    numberText.setX((j + 1) * CELL_SIZE - 6);
                    numberText.setY((i + 1) * CELL_SIZE + 8);
                    tutorialGrid.getChildren().add(numberText);
                }
            }
        }
        
        // Dessiner les points aux intersections
        for (int i = 0; i <= gridRows; i++) {
            for (int j = 0; j <= gridCols; j++) {
                Circle dot = new Circle((j + 0.5) * CELL_SIZE, (i + 0.5) * CELL_SIZE, DOT_RADIUS);
                dot.setFill(Color.web(DARK_COLOR));
                tutorialGrid.getChildren().add(dot);
            }
        }
        
        // Créer les segments horizontaux potentiels
        for (int i = 0; i <= gridRows; i++) {
            for (int j = 0; j < gridCols; j++) {
                // Créer la ligne horizontale
                Line line = new Line(
                    (j + 0.5) * CELL_SIZE, (i + 0.5) * CELL_SIZE,
                    (j + 1.5) * CELL_SIZE, (i + 0.5) * CELL_SIZE
                );
                line.setStrokeWidth(LINE_THICKNESS);
                line.setStroke(Color.TRANSPARENT);
                line.setId("H_" + i + "_" + j);
                
                // Hitbox plus grande pour faciliter le clic
                Rectangle hitbox = new Rectangle(
                    (j + 0.5) * CELL_SIZE - 5, (i + 0.5) * CELL_SIZE - 10,
                    CELL_SIZE + 10, 20
                );
                hitbox.setFill(Color.TRANSPARENT);
                hitbox.setUserData(line);
                hitbox.setOnMouseClicked(e -> handleLineClick(e, line));
                
                // SUPPRIMER LES EFFETS AU SURVOL
                // hitbox.setOnMouseEntered et hitbox.setOnMouseExited ont été supprimés
                
                // Ajouter la ligne à la grille et aux références
                gridLines.put(line.getId(), line);
                gridCrosses.put(line.getId(), new ArrayList<>());
                tutorialGrid.getChildren().addAll(line, hitbox);
            }
        }
        
        // Créer les segments verticaux potentiels
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j <= gridCols; j++) {
                // Créer la ligne verticale
                Line line = new Line(
                    (j + 0.5) * CELL_SIZE, (i + 0.5) * CELL_SIZE,
                    (j + 0.5) * CELL_SIZE, (i + 1.5) * CELL_SIZE
                );
                line.setStrokeWidth(LINE_THICKNESS);
                line.setStroke(Color.TRANSPARENT);
                line.setId("V_" + i + "_" + j);
                
                // Hitbox plus grande pour faciliter le clic
                Rectangle hitbox = new Rectangle(
                    (j + 0.5) * CELL_SIZE - 10, (i + 0.5) * CELL_SIZE - 5,
                    20, CELL_SIZE + 10
                );
                hitbox.setFill(Color.TRANSPARENT);
                hitbox.setUserData(line);
                hitbox.setOnMouseClicked(e -> handleLineClick(e, line));
                
                // SUPPRIMER LES EFFETS AU SURVOL
                // hitbox.setOnMouseEntered et hitbox.setOnMouseExited ont été supprimés
                
                // Ajouter la ligne à la grille et aux références
                gridLines.put(line.getId(), line);
                gridCrosses.put(line.getId(), new ArrayList<>());
                tutorialGrid.getChildren().addAll(line, hitbox);
            }
        }
    }

    /**
     * Affiche l'étape spécifiée du tutoriel
     * @param step Numéro de l'étape à afficher
     */
    private static void showStep(int step) {
        // Réinitialiser l'interface
        nextStepButton.setDisable(true);
        instructionsText.getChildren().clear();
        clearHighlights();
        
        // Afficher le contenu selon l'étape
        switch (step) {
            case 0:
                showIntroduction();
                break;
                
            case 1:
                showRules();
                break;
                
            case 2:
                showAdjacentThrees();
                break;
                
            case 3:
                showCornerThrees();
                break;
                
            case 4:
                showBottomRightThree();
                break;
                
            case 5:
                showTwoConstraints();
                break;
                
            case 6:
                showCongratulations();
                break;
                
            default:
                // Retour au menu si on dépasse les étapes
                Menu.show(primaryStage);
                break;
        }
    }
    
    /**
     * Étape 0: Introduction au tutoriel
     */
    private static void showIntroduction() {
        stepTitle.setText("Bienvenue au tutoriel Slitherlink");
        
        addInstructionParagraph("Dans ce tutoriel, vous apprendrez à résoudre des puzzles Slitherlink en utilisant des techniques logiques éprouvées.");
        addInstructionParagraph("Vous découvrirez comment analyser les indices et progresser méthodiquement vers la solution.");
        addInstructionParagraph("Les techniques présentées ici sont utilisables dans n'importe quel puzzle Slitherlink, quel que soit son niveau de difficulté.");
        addInstructionHighlight("Cliquez sur \"Suivant\" pour commencer.");
        
        // Activer le bouton suivant immédiatement
        nextStepButton.setDisable(false);
    }
    
    /**
     * Étape 1: Présentation des règles du jeu
     */
    private static void showRules() {
        stepTitle.setText("Rappel des règles");
        
        addInstructionParagraph("• Le but est de tracer un circuit fermé unique qui ne se croise ni ne se divise.");
        addInstructionParagraph("• Chaque chiffre indique le nombre exact de segments qui doivent être tracés autour de cette case.");
        addInstructionParagraph("• Les cases vides n'imposent aucune contrainte particulière.");
        addInstructionParagraph("• Le circuit doit former une boucle continue sans branches ni intersections.");
        
        addInstructionParagraph("Commandes :");
        addInstructionParagraph("• Cliquez avec le bouton gauche pour tracer une ligne.");
        addInstructionParagraph("• Cliquez avec le bouton droit pour placer une croix (segment interdit).");
        
        // Activer le bouton suivant immédiatement
        nextStepButton.setDisable(false);
    }
    
    /**
     * Étape 2: Technique des 3 adjacents en colonne
     */
    private static void showAdjacentThrees() {
        stepTitle.setText("Technique: 3 adjacents en colonne");
        
        addInstructionParagraph("Quand plusieurs 3 sont alignés en colonne ou en ligne, ils doivent partager certains segments pour former un circuit valide.");
        addInstructionParagraph("Dans notre grille, les trois 3 de la première colonne ne peuvent former un circuit valide que si les segments sont placés de façon spécifique.");
        addInstructionHighlight("Tracez les lignes indiquées en alternant entre les segments comme montré par les segments clignotants.");
        
        // Mettre en évidence les lignes à tracer
        highlightLine("H_0_0"); // Haut du premier 3
        highlightLine("V_0_0"); // Gauche du premier 3
        highlightLine("H_1_0"); // Haut du deuxième 3
        highlightLine("V_1_1"); // Droite du deuxième 3
        highlightLine("H_2_0"); // Droite du troisième 3
        highlightLine("V_2_0"); // Gauche du troisième 3
        highlightLine("H_3_0"); // Bas du troisième 3

        currentStep = 2;

        
        
    }

    /**
     * Étape 4: Technique du 3 en bas à droite
     */
    private static void showBottomRightThree() {
        stepTitle.setText("On fait le 2");
        
        addInstructionParagraph("Le 3 en bas à droite doit également avoir exactement trois segments autour de lui.");
        addInstructionParagraph("Dans un coin, ces segments doivent nécessairement être placés sur les trois côtés disponibles.");
        addInstructionHighlight("Tracez les trois segments autour du 3 en bas à droite.");
        
        // Mettre en évidence les lignes à tracer
        highlightLine("H_0_1"); // Haut du 3 en bas à droite
        highlightLine("V_0_2"); // Droite du 3 en bas à droite
    }
    
    /**
     * Étape 5: Contraintes sur le chiffre 2
     */
    private static void showTwoConstraints() {
        stepTitle.setText("On fini le tracé");
        
        addInstructionParagraph("Examinons maintenant le 2 en haut au centre. Une case avec un 2 doit avoir exactement deux segments autour d'elle.");
        addInstructionParagraph("Ce 2 partage son côté gauche avec le 3 en haut à gauche. Si ce segment était tracé pour les deux cases, cela créerait des configurations impossibles.");
        addInstructionHighlight("Tracez des segments sur les côtés supérieur et droit du 2, et placez une croix sur le segment gauche.");
        
        // Mettre en évidence les actions à effectuer
        highlightLine("V_1_2"); // Haut du 2
        highlightLine("H_2_2"); // Droite du 2
        highlightLine("H_3_1"); 
    }
    
    /**
     * Étape 6: Contraintes sur le chiffre 1
     */
    private static void showOneConstraints() {
        stepTitle.setText("Technique: Contraintes sur un 1");
        
        addInstructionParagraph("Considérons maintenant le 1 en haut à droite. Une case avec un 1 doit avoir exactement un segment autour d'elle.");
        addInstructionParagraph("Ce 1 partage son côté supérieur avec le 2 que nous venons de traiter. Ce segment doit être le seul tracé pour le 1.");
        addInstructionHighlight("Placez des croix sur les trois autres côtés du 1 en haut à droite pour indiquer qu'aucun segment ne doit y être tracé.");
        
        // Mettre en évidence les segments à marquer d'une croix
        createCross(gridLines.get("V_0_3"), true); // Droite du 1
        createCross(gridLines.get("H_1_2"), true); // Bas du 1
        createCross(gridLines.get("V_0_2"), true); // Gauche du 1 (attention, possible conflit avec étape précédente)
    }
    
    /**
     * Étape 7: Le 3 du milieu à gauche
     */
    private static void showMiddleThree() {
        stepTitle.setText("Technique: Le 3 du milieu à gauche");
        
        addInstructionParagraph("Examinons le 3 au milieu à gauche. Il a déjà un segment à gauche.");
        addInstructionParagraph("Pour respecter sa contrainte de 3 segments et contribuer à un circuit unique, il doit avoir des segments supplémentaires.");
        addInstructionHighlight("Tracez les segments manquants autour du 3 du milieu (en haut et à droite).");
        
        // Vérifier si les segments sont déjà tracés
        if (!isLineDrawn("H_1_0")) {
            highlightLine("H_1_0"); // Haut du 3 du milieu
        }
        
        if (!isLineDrawn("V_1_1")) {
            highlightLine("V_1_1"); // Droite du 3 du milieu
        }
    }
    
    /**
     * Étape 8: Compléter le circuit
     */
    private static void showCompletingLoop() {
        stepTitle.setText("Technique: Compléter le circuit unique");
        
        addInstructionParagraph("Nous avons maintenant plusieurs segments tracés qui doivent former un circuit unique.");
        addInstructionParagraph("Pour éviter les boucles isolées et créer un circuit fermé, nous devons connecter les segments existants stratégiquement.");
        addInstructionHighlight("Tracez les segments manquants pour compléter le circuit unique.");
        
        // Mettre en évidence les lignes restantes à tracer
        highlightLine("V_1_2"); // Segment vertical entre milieu et bas
        highlightLine("H_2_1"); // Segment horizontal en bas
    }
    
    /**
     * Étape 9: Vérification de la solution
     */
    private static void showVerification() {
        stepTitle.setText("Vérification finale");
        
        addInstructionParagraph("Vérifions que notre solution respecte toutes les contraintes :");
        addInstructionParagraph("• Le 3 en haut à gauche a exactement 3 segments ✓");
        addInstructionParagraph("• Le 2 en haut au centre a exactement 2 segments ✓");
        addInstructionParagraph("• Le 1 en haut à droite a exactement 1 segment ✓");
        addInstructionParagraph("• Le 3 au milieu à gauche a exactement 3 segments ✓");
        addInstructionParagraph("• Le 3 en bas à gauche a exactement 3 segments ✓");
        addInstructionParagraph("• Le 3 en bas à droite a exactement 3 segments ✓");
        addInstructionParagraph("• Le circuit forme une boucle fermée unique, sans branches ✓");
        
        // Mettre en évidence le circuit complet
        highlightCompleteCircuit();
        
        // Activer le bouton suivant
        nextStepButton.setDisable(false);
    }
    
    /**
     * Étape 10: Félicitations et fin du tutoriel
     */
    private static void showCongratulations() {
        stepTitle.setText("Félicitations!");
        
        addInstructionParagraph("Vous avez résolu avec succès cette grille Slitherlink en utilisant des techniques logiques!");
        addInstructionParagraph("Vous avez appris à :");
        addInstructionParagraph("• Identifier les contraintes liées aux 3 adjacents");
        addInstructionParagraph("• Gérer les 3 dans les coins");
        addInstructionParagraph("• Appliquer les contraintes des chiffres 2 et 1");
        addInstructionParagraph("• Former un circuit unique et fermé");
        
        // Ajouter les boutons pour la suite
        Button startGameButton = createStyledButton("Commencer à jouer", true);
        startGameButton.setOnAction(e -> {
            animateButtonClick(startGameButton);
            
            // Animation de sortie avant de montrer le jeu
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), startGameButton.getScene().getRoot());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(evt -> GameScene.show(primaryStage));
            fadeOut.play();
        });
        
        // Ajouter un espace et le bouton
        instructionsText.getChildren().add(new Text("\n\n"));
        instructionsText.getChildren().add(startGameButton);
        
        // Modifier le bouton suivant pour retourner au menu
        nextStepButton.setText("Retour au menu");
        nextStepButton.setOnAction(e -> {
            animateButtonClick(nextStepButton);
            
            // Animation de sortie avant retour au menu
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), nextStepButton.getScene().getRoot());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(evt -> Menu.show(primaryStage));
            fadeOut.play();
        });
        nextStepButton.setDisable(false);
    }
    

    
    /**
     * Traite le clic sur une ligne de la grille
     * @param e Événement de clic
     * @param line Ligne cliquée
     */
    private static void handleLineClick(MouseEvent e, Line line) {
        if (e.getButton() == MouseButton.PRIMARY) {
            // Tracer une ligne
            drawLine(line);
            //vérifier la progression
            System.out.println("Progression step " + currentStep);
            if(checkProgress() > 0){
                System.out.println("Progression step " + currentStep);
                nextStepButton.setDisable(false);
            }
        } else if (e.getButton() == MouseButton.SECONDARY) {
            // Placer une croix
            placeCross(line);
        }
    }
    
    private static void drawLine(Line line) {
        // Arrêter l'animation de surbrillance si elle existe
        stopHighlightAnimation(line);
        
        // Supprimer toute croix sur cette ligne
        removeCrosses(line.getId());
        
        // Tracer la ligne définitivement
        line.setStroke(Color.web(DARK_COLOR));
        line.setStrokeWidth(LINE_THICKNESS);
        
        // Animation de confirmation
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), line);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);
        
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), line);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        
        scaleUp.setOnFinished(e -> scaleDown.play());
        scaleUp.play();
        
        // Vérifier immédiatement la progression

    }

    /**
     * Place une croix sur une ligne
     * @param line Ligne à marquer d'une croix
     */
    private static void placeCross(Line line) {
        // Arrêter l'animation de surbrillance si elle existe
        stopHighlightAnimation(line);
        
        // Rendre la ligne invisible
        line.setStroke(Color.TRANSPARENT);
        
        // Supprimer toute croix existante
        removeCrosses(line.getId());
        
        // Créer une croix permanente
        createCross(line, false);
    }
    
    /**
     * Crée une croix sur une ligne
     * @param line Ligne sur laquelle placer la croix
     * @param isPreview Si true, la croix est une prévisualisation
     */
    private static void createCross(Line line, boolean isPreview) {
        // Coordonnées pour la croix
        Line cross1, cross2;
        
        // Selon l'orientation de la ligne
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
        
        // Configurer les lignes de la croix
        cross1.setStrokeWidth(3);
        cross1.setUserData(line);
        cross2.setStrokeWidth(3);
        cross2.setUserData(line);
        
        // Apparence selon le mode
        if (isPreview) {
            // Prévisualisation semi-transparente
            cross1.setStroke(Color.web(ACCENT_COLOR, 0.4));
            cross2.setStroke(Color.web(ACCENT_COLOR, 0.4));
            
            // Animation de pulsation
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
            // Croix permanente
            cross1.setStroke(Color.web(ACCENT_COLOR));
            cross2.setStroke(Color.web(ACCENT_COLOR));
            
            // Animer l'apparition
            cross1.setOpacity(0);
            cross2.setOpacity(0);
            
            FadeTransition fadeIn1 = new FadeTransition(Duration.millis(200), cross1);
            fadeIn1.setFromValue(0);
            fadeIn1.setToValue(1);
            
            FadeTransition fadeIn2 = new FadeTransition(Duration.millis(200), cross2);
            fadeIn2.setFromValue(0);
            fadeIn2.setToValue(1);
            
            ParallelTransition parallel = new ParallelTransition(fadeIn1, fadeIn2);
            parallel.play();
            
            // Garder une référence pour pouvoir les supprimer
            gridCrosses.get(line.getId()).add(cross1);
            gridCrosses.get(line.getId()).add(cross2);
        }
        
        // Ajouter à la grille
        tutorialGrid.getChildren().addAll(cross1, cross2);
    }
    
    /**
     * Supprime les croix associées à une ligne
     * @param lineId Identifiant de la ligne
     */
    private static void removeCrosses(String lineId) {
        List<Line> crosses = gridCrosses.get(lineId);
        if (crosses != null) {
            tutorialGrid.getChildren().removeAll(crosses);
            crosses.clear();
        }
    }
    
    /**
     * Met en évidence une ligne pour guider l'utilisateur
     * @param lineId Identifiant de la ligne
     */
    private static void highlightLine(String lineId) {
        Line line = gridLines.get(lineId);
        if (line != null && line.getStroke() != Color.web(DARK_COLOR)) {
            // Colorer la ligne
            line.setStroke(Color.LIGHTGRAY);
            
            // Animation de pulsation
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(800), line);
            fadeTransition.setFromValue(0.3);
            fadeTransition.setToValue(0.8);
            fadeTransition.setCycleCount(-1);
            fadeTransition.setAutoReverse(true);
            fadeTransition.play();
            
            // Stocker l'animation pour pouvoir l'arrêter plus tard
            line.getProperties().put("fadeTransition", fadeTransition);
        }
    }
    
    /**
     * Met en évidence le circuit complet pour l'étape de vérification
     */
    private static void highlightCompleteCircuit() {
        // Liste des segments qui forment le circuit complet
        String[] circuitLines = {
            "H_0_0", "V_0_0", "H_1_0", "V_1_1", "V_1_2", 
            "H_2_1", "H_2_0", "V_2_0", "H_3_0", "V_2_1", 
            "H_2_2", "V_2_3", "H_3_2", "H_0_1", "V_0_1"
        };
        
        // Mettre en surbrillance les segments du circuit avec une couleur plus vive
        for (String lineId : circuitLines) {
            Line line = gridLines.get(lineId);
            if (line != null) {
                // Si la ligne n'est pas déjà tracée, la mettre en évidence
                if (line.getStroke() != Color.web(DARK_COLOR)) {
                    line.setStroke(Color.web(MAIN_COLOR));
                }
                
                // Ajouter une légère pulsation
                FadeTransition glow = new FadeTransition(Duration.millis(1500), line);
                glow.setFromValue(0.8);
                glow.setToValue(1.0);
                glow.setCycleCount(-1);
                glow.setAutoReverse(true);
                glow.play();
                
                line.getProperties().put("glowAnimation", glow);
            }
        }
    }
    
    /**
     * Arrête l'animation de surbrillance d'une ligne
     * @param line Ligne dont l'animation doit être arrêtée
     */
    private static void stopHighlightAnimation(Line line) {
        Object fadeObj = line.getProperties().get("fadeTransition");
        if (fadeObj instanceof FadeTransition) {
            ((FadeTransition) fadeObj).stop();
            line.getProperties().remove("fadeTransition");
        }
        
        Object glowObj = line.getProperties().get("glowAnimation");
        if (glowObj instanceof FadeTransition) {
            ((FadeTransition) glowObj).stop();
            line.getProperties().remove("glowAnimation");
        }
    }
    
    /**
     * Supprime toutes les surbrillances et animations
     */
    private static void clearHighlights() {
        // Arrêter toutes les animations et restaurer les lignes
        for (Line line : gridLines.values()) {
            stopHighlightAnimation(line);
            
            // Restaurer l'apparence normale si la ligne n'est pas tracée définitivement
            if (line.getStroke() == Color.LIGHTGRAY || line.getStroke() == Color.web(MAIN_COLOR)) {
                line.setStroke(Color.TRANSPARENT);
            }
        }
        
        // Supprimer les croix de prévisualisation
        tutorialGrid.getChildren().removeIf(node -> 
            node instanceof Line && 
            node.getUserData() instanceof Line &&
            ((Line)node).getStroke() == Color.web(ACCENT_COLOR, 0.4)
        );
    }
    
    /**
     * Vérifie la progression de l'utilisateur et active le bouton suivant si nécessaire
     */
    private static int checkProgress() {
        switch (currentStep) {
            case 2: // 3 adjacents en colonne
                System.out.println("checkProgress 2");
                return checkThreeAdjacentProgress();
            
                case 3: // 3 dans les coins
                return checkCornerThreesProgress();
                
            case 4: // 3 en bas à droite
                return checkBottomRightThreeProgress();

            case 5: // Contraintes sur le 2
                return checkTwoConstraintsProgress();
                
            case 6: // Contraintes sur le 1
                return checkOneConstraintsProgress();
                
            case 7: // 3 du milieu
                return checkMiddleThreeProgress();
                
            case 8: // Compléter le circuit
                return checkCompletionProgress();

            default:
                System.out.println("defaut de checkProgress");
                return 0;
        }
    }
    
    /**
     * Vérifie la progression pour l'étape des 3 adjacents
     */
    private static int checkThreeAdjacentProgress() {
        String[] requiredLines = {"H_0_0", "V_0_0", "H_1_0", "V_1_1", "H_2_0", "V_2_0", "H_3_0"};
        boolean allLinesDrawn = true;
        int drawnCount = 0;
        
        System.out.println("checkThreeAdjacentProgress - Checking lines:");
        for (String lineId : requiredLines) {
            boolean isDrawn = isLineDrawn(lineId);
            System.out.println("Line " + lineId + ": " + (isDrawn ? "DRAWN" : "NOT DRAWN"));
            if (isDrawn) {
                drawnCount++;
            } else {
                allLinesDrawn = false;
            }
        }
        
        // Afficher un message d'encouragement selon la progression
        if (drawnCount > 0 && drawnCount < requiredLines.length) {
            highlightInstructions("Continuez, vous avez tracé " + drawnCount + " sur " + requiredLines.length + 
                                 " segments nécessaires.", Color.web(MAIN_COLOR));
            System.out.println("Progress: " + drawnCount + "/" + requiredLines.length + " lines drawn");
        }
        
        // Activer le bouton uniquement quand tous les segments sont tracés
        if (allLinesDrawn) {
            nextStepButton.setDisable(false);
            highlightInstructions("Excellent! Vous avez correctement appliqué la technique des 3 adjacents.", 
                                Color.web(MAIN_COLOR));
            System.out.println("All lines drawn! Enabling next button");
            return 1;
        }
        System.out.println("Not all lines drawn yet: " + drawnCount + "/" + requiredLines.length);
        return 0;
    }
    
    
    /**
     * Vérifie la progression pour l'étape du 3 en bas à droite
     * @return 
     */
    private static int checkBottomRightThreeProgress() {
        System.out.println("l1 " + isLineDrawn("H_0_1"));
        System.out.println("l2 " + isLineDrawn("V_0_2"));
        if (isLineDrawn("H_0_1") && isLineDrawn("V_0_2")) {
            nextStepButton.setDisable(false);
            highlightInstructions("Parfait! Vous avez correctement placé les segments autour du 3 en bas à droite.", Color.web(MAIN_COLOR));
        }
        // a changer
        return 0;
    }
    
    /**
     * Vérifie la progression pour l'étape des contraintes sur le 2
     * @return 
     */
    private static int checkTwoConstraintsProgress() {
        boolean linesDrawn = isLineDrawn("V_1_2") && isLineDrawn("H_2_2") && isLineDrawn("H_3_1");
        
        if (linesDrawn) {
            nextStepButton.setDisable(false);
            highlightInstructions("Excellent! Vous avez correctement appliqué les contraintes sur le 2.", Color.web(MAIN_COLOR));
        }
        // a changer
        return 0;
    }
    
    /**
     * Vérifie la progression pour l'étape des contraintes sur le 1
     * @return 
     */
    private static int checkOneConstraintsProgress() {
        boolean rightCross = isCrossPlacedOn("V_0_3") || !isLineDrawn("V_0_3");
        boolean bottomCross = isCrossPlacedOn("H_1_2") || !isLineDrawn("H_1_2");
        boolean leftCross = isCrossPlacedOn("V_0_2") || !isLineDrawn("V_0_2");
        
        // Pour cette étape, on peut être tolérant car V_0_2 devait être tracé à l'étape précédente
        if ((rightCross && bottomCross) || (rightCross && leftCross) || (bottomCross && leftCross)) {
            nextStepButton.setDisable(false);
            highlightInstructions("Parfait! Vous avez bien compris les contraintes pour le chiffre 1.", Color.web(MAIN_COLOR));
        }
        // a changer
        return 0;
    }
    
    /**
     * Vérifie la progression pour l'étape du 3 du milieu
     * @return 
     */
    private static int checkMiddleThreeProgress() {
        if (isLineDrawn("H_1_0") && isLineDrawn("V_1_1")) {
            nextStepButton.setDisable(false);
            highlightInstructions("Très bien! Vous avez complété les segments autour du 3 du milieu.", Color.web(MAIN_COLOR));
        }
        // a changer
        return 0;
    }
    
    /**
     * Vérifie la progression pour l'étape de complétion du circuit
     * @return 
     */
    private static int checkCompletionProgress() {
        if (isLineDrawn("V_1_2") && isLineDrawn("H_2_1")) {
            nextStepButton.setDisable(false);
            highlightInstructions("Félicitations! Vous avez complété le circuit unique.", Color.web(MAIN_COLOR));
        }
        // a changer
        return 0;
    }
    
    /**
     * Vérifie si une ligne est tracée
     * @param lineId Identifiant de la ligne
     * @return true si la ligne est tracée
     */
    /**
 * Vérifie si une ligne est tracée en comparant sa couleur à la couleur cible
 * @param lineId Identifiant de la ligne
 * @return true si la ligne est tracée
 */
public static boolean isLineDrawn(String lineId) {
    Line line = gridLines.get(lineId);
    if (line != null && line.getStroke() instanceof Color) {
        Color lineColor = (Color) line.getStroke();
        Color targetColor = Color.web(DARK_COLOR);
        
        // Comparer les composantes de couleur avec une tolérance
        double tolerance = 0.01;
        return Math.abs(lineColor.getRed() - targetColor.getRed()) < tolerance &&
               Math.abs(lineColor.getGreen() - targetColor.getGreen()) < tolerance &&
               Math.abs(lineColor.getBlue() - targetColor.getBlue()) < tolerance;
    }
    return false;
}
    
    /**
     * Vérifie si une croix est placée sur une ligne
     * @param lineId Identifiant de la ligne
     * @return true si une croix est placée
     */
    private static boolean isCrossPlacedOn(String lineId) {
        return gridCrosses.get(lineId) != null && !gridCrosses.get(lineId).isEmpty();
    }
    
    /**
     * Ajoute un paragraphe de texte normal aux instructions
     * @param text Texte à ajouter
     */
    private static void addInstructionParagraph(String text) {
        Text paragraph = new Text(text + "\n\n");
        paragraph.setFont(Font.font("Arial", 16));
        paragraph.setFill(Color.BLACK);
        paragraph.setWrappingWidth(instructionsText.getPrefWidth() - 20);
        instructionsText.getChildren().add(paragraph);
    }
    
    /**
     * Ajoute un paragraphe mis en évidence aux instructions
     * @param text Texte à ajouter
     */
    private static void addInstructionHighlight(String text) {
        Text highlight = new Text(text + "\n\n");
        highlight.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        highlight.setFill(Color.web(MAIN_COLOR));
        highlight.setWrappingWidth(instructionsText.getPrefWidth() - 20);
        instructionsText.getChildren().add(highlight);
    }
    
    /**
     * Met à jour le texte d'instructions avec un message coloré
     * @param text Texte à afficher
     * @param color Couleur du texte
     */
    private static void highlightInstructions(String text, Color color) {
        Text highlight = new Text(text);
        highlight.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        highlight.setFill(color);
        highlight.setWrappingWidth(instructionsText.getPrefWidth() - 20);
        
        instructionsText.getChildren().clear();
        instructionsText.getChildren().add(highlight);
    }
    
    /**
     * Crée un bouton stylé
     * @param text Texte du bouton
     * @param isPrimary Si true, style primaire; sinon style secondaire
     * @return Bouton stylé
     */
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
    
    /**
     * Anime le clic sur un bouton
     * @param button Bouton à animer
     */
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


    /**
 * Étape 3: Technique des 3 dans les coins
 */
private static void showCornerThrees() {
    stepTitle.setText("Technique: 3 dans les coins");
    
    addInstructionParagraph("Un 3 dans un coin ou sur un bord impose des contraintes spécifiques.");
    addInstructionParagraph("Pour le 3 en haut à gauche, les trois segments doivent être placés sur les trois côtés disponibles.");
    addInstructionParagraph("De même, pour le 3 en bas à gauche, les segments doivent être placés de façon similaire.");
    
    addInstructionHighlight("Certains segments sont déjà tracés. Complétez les segments manquants pour les 3 en coins.");
    
    // Mettre en évidence les lignes à tracer qui ne sont pas déjà tracées
    if (!isLineDrawn("H_3_2")) {
        highlightLine("H_3_2"); // Droite du 3 en haut à gauche
    }
    
    if (!isLineDrawn("V_2_3")) {
        highlightLine("V_2_3"); // Droite du 3 en bas à gauche
    }
    
    // Activer automatiquement le bouton après un délai si l'utilisateur a du mal
}

/**
 * Vérifie la progression pour l'étape des 3 dans les coins
 * @return 
 */
private static int checkCornerThreesProgress() {
    // Vérifier si les lignes nécessaires sont tracées
    boolean firstSegmentDrawn = isLineDrawn("H_3_2");
    boolean secondSegmentDrawn = isLineDrawn("V_2_3");
    
    // Si au moins une ligne est tracée, on encourage l'utilisateur
    if (firstSegmentDrawn || secondSegmentDrawn) {
        // Un seul segment est tracé
        highlightInstructions("Bien! Continuez avec l'autre segment manquant.", Color.web(MAIN_COLOR));
    }
    
    // Si les deux lignes sont tracées, on active le bouton suivant
    if (firstSegmentDrawn && secondSegmentDrawn) {
        nextStepButton.setDisable(false);
        highlightInstructions("Très bien! Vous avez complété les segments pour les 3 en coins.", Color.web(MAIN_COLOR));
    }
    // a changer
    return 0;
}


}