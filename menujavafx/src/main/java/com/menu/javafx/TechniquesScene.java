package com.menu.javafx;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.application.Platform;

public class TechniquesScene {
    private static String MAIN_COLOR;
    private static String SECONDARY_COLOR;
    private static String ACCENT_COLOR;
    private static String DARK_COLOR;
    private static String LIGHT_COLOR;
    private static String TEXT_COLOR;
    
    private static final double CELL_SIZE = 100;       // Augmenté pour un affichage plus grand
    private static final double DOT_RADIUS = 7;       // Augmenté pour correspondre à la taille
    private static final double LINE_THICKNESS = 4;   // Augmenté pour une meilleure visibilité
    private static final double PANEL_WIDTH = 500;    // Augmenté pour plus d'espace
    private static final double GRID_OFFSET_X = 100;  // Augmenté pour un meilleur espacement
    private static final double GRID_OFFSET_Y = 100;  // Augmenté pour un meilleur espacement

    private static int currentStep = 0;
    private static Map<String, Line> gridLines = new HashMap<>();
    private static Map<String, List<Line>> gridCrosses = new HashMap<>();
    private static Pane tutorialGrid;
    private static VBox instructionsPanel;
    private static TextFlow instructionsText;
    private static Button nextStepButton;
    private static Text stepTitle;
    private static Stage primaryStage;
    private static final Set<String> highlightedLines = new HashSet<>();

    private static final int[][] tutorialNumbers = {
        {3, 2, 1},
        {3, -1, -1},
        {3, -1, 3}
    };
    
    public static void show(Stage stage) {
        boolean isDarkMode = SettingScene.isDarkModeEnabled();
        updateThemeColors(isDarkMode);
        
        currentStep = 0;
        gridLines.clear();
        gridCrosses.clear();
        highlightedLines.clear();
        primaryStage = stage;
        
        StackPane root = new StackPane();
        createModernBackground(root, isDarkMode);
        
        VBox mainLayout = new VBox(25);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        
        HBox header = createHeader(isDarkMode);
        mainLayout.getChildren().add(header);
        
        HBox contentArea = new HBox(40);
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setPadding(new Insets(20, 30, 30, 30));
        
        StackPane gridContainer = createGridContainer(isDarkMode);
        instructionsPanel = createInstructionsPanel(isDarkMode);
        
        contentArea.getChildren().addAll(gridContainer, instructionsPanel);
        mainLayout.getChildren().add(contentArea);
        
        root.getChildren().add(mainLayout);
        
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        
        // Animation d'entrée
        mainLayout.setOpacity(0);
        mainLayout.setTranslateY(20);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), mainLayout);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        TranslateTransition moveIn = new TranslateTransition(Duration.millis(600), mainLayout);
        moveIn.setFromY(20);
        moveIn.setToY(0);
        
        ParallelTransition entryAnimation = new ParallelTransition(fadeIn, moveIn);
        entryAnimation.play();
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Slitherlink - Tutoriel Techniques");
        primaryStage.setMaximized(true);
        
        showStep(currentStep);
        primaryStage.show();
    }
    
    private static void updateThemeColors(boolean isDarkMode) {
        if (isDarkMode) {
            MAIN_COLOR = "#42b996";      // Vert émeraude
            SECONDARY_COLOR = "#1E2530";  // Bleu-gris
            ACCENT_COLOR = "#ff8080";     // Rouge clair
            DARK_COLOR = "#192730";      // Bleu-gris profond
            LIGHT_COLOR = "#74c99e";     // Vert menthe
            TEXT_COLOR = "#ffffff";      // Blanc pour meilleur contraste
        } else {
            MAIN_COLOR = "#36846d";      // Vert turquoise
            SECONDARY_COLOR = "#f5f8fa";  // Blanc cassé
            ACCENT_COLOR = "#d65c5c";     // Rouge corail
            DARK_COLOR = "#255a4b";      // Vert foncé
            LIGHT_COLOR = "#a9d8bd";     // Vert clair menthe
            TEXT_COLOR = "#255a4b";      // Vert foncé pour le texte
        }
    }
    
    private static void createModernBackground(StackPane root, boolean isDarkMode) {
        Rectangle background = new Rectangle();
        background.widthProperty().bind(root.widthProperty());
        background.heightProperty().bind(root.heightProperty());
        
        Color color1 = Color.web(isDarkMode ? SECONDARY_COLOR : SECONDARY_COLOR);
        Color color2 = Color.web(isDarkMode ? DARK_COLOR : LIGHT_COLOR, 0.7);
        Color accentColor = Color.web(isDarkMode ? MAIN_COLOR : MAIN_COLOR, 0.1);
        
        RadialGradient gradient = new RadialGradient(
                0, 0, 0.5, 0.3, 1.0, true, CycleMethod.NO_CYCLE,
                new Stop(0, color1),
                new Stop(0.8, color2),
                new Stop(1, accentColor)
        );
        
        background.setFill(gradient);
        
        // Ajout de quelques formes organiques en arrière-plan (réduit pour les performances)
        int numShapes = isDarkMode ? 5 : 5;
        for (int i = 0; i < numShapes; i++) {
            double x = Math.random() * Screen.getPrimary().getVisualBounds().getWidth();
            double y = Math.random() * Screen.getPrimary().getVisualBounds().getHeight();
            double size = 100 + Math.random() * 200;
            
            Circle shape = new Circle(size);
            shape.setCenterX(x);
            shape.setCenterY(y);
            shape.setFill(Color.web(isDarkMode ? MAIN_COLOR : MAIN_COLOR, 0.02 + Math.random() * 0.03));
            
            root.getChildren().add(shape);
        }
        
        root.getChildren().add(0, background);
    }

    private static HBox createHeader(boolean isDarkMode) {
        StackPane titleContainer = new StackPane();
        titleContainer.setPadding(new Insets(0, 0, 10, 0));
        
        Text title = new Text("Tutoriel Slitherlink");
        title.setFont(Font.font("Montserrat", FontWeight.BOLD, 36));
        
        // Gradient pour le titre adapté au mode
        LinearGradient textGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(isDarkMode ? LIGHT_COLOR : MAIN_COLOR, 0.9)),
                new Stop(0.5, Color.web(isDarkMode ? "#ffffff" : DARK_COLOR, 1.0)),
                new Stop(1, Color.web(isDarkMode ? LIGHT_COLOR : MAIN_COLOR, 0.9))
        );
        title.setFill(textGradient);
        
        // Effets visuels
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.web(isDarkMode ? "#000000" : "#333333", 0.3));
        dropShadow.setRadius(5);
        dropShadow.setOffsetX(2);
        dropShadow.setOffsetY(2);
        
        Reflection reflection = new Reflection();
        reflection.setFraction(0.2);
        reflection.setTopOpacity(0.5);
        
        Glow glow = new Glow(0.4);
        dropShadow.setInput(reflection);
        glow.setInput(dropShadow);
        title.setEffect(glow);
        
        titleContainer.getChildren().add(title);
        
        Button backButton = createModernButton("Retour au menu", false, isDarkMode);
        backButton.setOnAction(e -> {
            addClickEffect(backButton);
            
            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), backButton.getScene().getRoot());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(evt -> Menu.show(primaryStage));
            fadeOut.play();
        });
        
        HBox header = new HBox(40);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(30, 20, 10, 20));
        header.getChildren().addAll(titleContainer, backButton);
        
        return header;
    }
    
    private static StackPane createGridContainer(boolean isDarkMode) {
        tutorialGrid = new Pane();
        tutorialGrid.setMinSize(500, 500);  // Augmentation de la taille minimale
        
        StackPane gridContainer = new StackPane();
        gridContainer.setPadding(new Insets(30));
        gridContainer.setMinSize(550, 550);  // Augmentation de la taille du conteneur
        
        String boxBackground = isDarkMode ? 
                "rgba(30, 35, 45, 0.9)" : 
                "rgba(255, 255, 255, 0.95)";
        
        Rectangle containerBg = new Rectangle();
        containerBg.setWidth(550);  // Augmentation de la largeur
        containerBg.setHeight(550); // Augmentation de la hauteur
        containerBg.setArcWidth(20);
        containerBg.setArcHeight(20);
        containerBg.setFill(Color.web(boxBackground));
        containerBg.setStroke(Color.web(MAIN_COLOR, 0.2));
        containerBg.setStrokeWidth(1.5);
        
        DropShadow gridShadow = new DropShadow();
        gridShadow.setColor(Color.web("#000000", 0.2));
        gridShadow.setRadius(15);
        gridShadow.setSpread(0.05);
        gridShadow.setOffsetY(5);
        containerBg.setEffect(gridShadow);
        
        gridContainer.getChildren().addAll(containerBg, tutorialGrid);
        
        initializeGrid(isDarkMode);
        
        return gridContainer;
    }
    
    private static VBox createInstructionsPanel(boolean isDarkMode) {
        VBox panel = new VBox(24);
        panel.setPadding(new Insets(30));
        panel.setMinWidth(420);
        panel.setPrefWidth(PANEL_WIDTH);
        panel.setMaxWidth(500);
        
        String boxBackground = isDarkMode ? 
                "rgba(30, 35, 45, 0.9)" : 
                "rgba(255, 255, 255, 0.95)";
        
        panel.setStyle(
            "-fx-background-color: " + boxBackground + ";" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: " + Color.web(MAIN_COLOR, 0.2) + ";" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 20;" +
            "-fx-padding: 25;"
        );
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.2));
        shadow.setRadius(15);
        shadow.setSpread(0.05);
        shadow.setOffsetY(5);
        panel.setEffect(shadow);
        
        stepTitle = new Text("Bienvenue au tutoriel Slitherlink");
        stepTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 22));
        stepTitle.setFill(Color.web(isDarkMode ? "#ffffff" : DARK_COLOR));
        
        instructionsText = new TextFlow();
        instructionsText.setLineSpacing(8);
        instructionsText.setPrefWidth(PANEL_WIDTH - 60);
        instructionsText.setTextAlignment(TextAlignment.LEFT);
        
        ScrollPane scrollPane = new ScrollPane(instructionsText);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle(
            "-fx-background: transparent; " +
            "-fx-background-color: transparent;" +
            "-fx-padding: 5;" +
            "-fx-border-color: " + Color.web(MAIN_COLOR, 0.1) + ";" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;"
        );
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        
        nextStepButton = createModernButton("Suivant", true, isDarkMode);
        nextStepButton.setPrefWidth(200);
        nextStepButton.setOnAction(e -> {
            addClickEffect(nextStepButton);
            currentStep++;
            showStep(currentStep);
        });
        
        panel.getChildren().addAll(stepTitle, scrollPane, nextStepButton);
        return panel;
    }

    private static void initializeGrid(boolean isDarkMode) {
        tutorialGrid.getChildren().clear();
        gridLines.clear();
        gridCrosses.clear();
        
        int gridRows = tutorialNumbers.length;
        int gridCols = tutorialNumbers[0].length;
        
        // Dessiner les cellules de fond
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridCols; j++) {
                Rectangle cellBg = new Rectangle(
                    GRID_OFFSET_X + j * CELL_SIZE,
                    GRID_OFFSET_Y + i * CELL_SIZE,
                    CELL_SIZE, CELL_SIZE
                );
                cellBg.setFill(Color.web(isDarkMode ? "#1E2530" : SECONDARY_COLOR, 0.1));
                cellBg.setArcWidth(5);
                cellBg.setArcHeight(5);
                cellBg.setStroke(Color.web(isDarkMode ? LIGHT_COLOR : MAIN_COLOR, 0.1));
                cellBg.setStrokeWidth(0.5);
                tutorialGrid.getChildren().add(cellBg);
            }
        }
        
        // Dessiner les nombres
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridCols; j++) {
                int value = tutorialNumbers[i][j];
                if (value > 0) {
                    Text numberText = new Text(String.valueOf(value));
                    numberText.setFont(Font.font("Montserrat", FontWeight.BOLD, 28));
                    numberText.setFill(Color.web(isDarkMode ? "#ffffff" : DARK_COLOR, 0.9));
                    
                    DropShadow textShadow = new DropShadow();
                    textShadow.setRadius(1.0);
                    textShadow.setOffsetX(0.5);
                    textShadow.setOffsetY(0.5);
                    textShadow.setColor(Color.web("#000000", 0.3));
                    numberText.setEffect(textShadow);
                    
                    // Centrer le texte dans la cellule
                    double textWidth = numberText.getBoundsInLocal().getWidth();
                    double textHeight = numberText.getBoundsInLocal().getHeight();
                    
                    numberText.setX(GRID_OFFSET_X + j * CELL_SIZE + (CELL_SIZE - textWidth) / 2);
                    numberText.setY(GRID_OFFSET_Y + i * CELL_SIZE + (CELL_SIZE + textHeight) / 2);
                    
                    tutorialGrid.getChildren().add(numberText);
                }
            }
        }
        
        // Créer les segments horizontaux
        for (int i = 0; i <= gridRows; i++) {
            for (int j = 0; j < gridCols; j++) {
                Line line = new Line(
                    GRID_OFFSET_X + j * CELL_SIZE + CELL_SIZE * 0.1,
                    GRID_OFFSET_Y + i * CELL_SIZE,
                    GRID_OFFSET_X + (j + 1) * CELL_SIZE - CELL_SIZE * 0.1,
                    GRID_OFFSET_Y + i * CELL_SIZE
                );
                line.setStrokeWidth(LINE_THICKNESS);
                line.setStroke(Color.TRANSPARENT);
                line.setStrokeLineCap(StrokeLineCap.ROUND);
                line.setId("H_" + i + "_" + j);
                
                Rectangle hitbox = new Rectangle(
                    GRID_OFFSET_X + j * CELL_SIZE,
                    GRID_OFFSET_Y + i * CELL_SIZE - 10,
                    CELL_SIZE, 20
                );
                hitbox.setFill(Color.TRANSPARENT);
                hitbox.setCursor(javafx.scene.Cursor.HAND);
                hitbox.setUserData(line);
                hitbox.setOnMouseClicked(e -> handleLineClick(e, line));
                
                gridLines.put(line.getId(), line);
                gridCrosses.put(line.getId(), new ArrayList<>());
                tutorialGrid.getChildren().addAll(line, hitbox);
            }
        }
        
        // Créer les segments verticaux
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j <= gridCols; j++) {
                Line line = new Line(
                    GRID_OFFSET_X + j * CELL_SIZE,
                    GRID_OFFSET_Y + i * CELL_SIZE + CELL_SIZE * 0.1,
                    GRID_OFFSET_X + j * CELL_SIZE,
                    GRID_OFFSET_Y + (i + 1) * CELL_SIZE - CELL_SIZE * 0.1
                );
                line.setStrokeWidth(LINE_THICKNESS);
                line.setStroke(Color.TRANSPARENT);
                line.setStrokeLineCap(StrokeLineCap.ROUND);
                line.setId("V_" + i + "_" + j);
                
                Rectangle hitbox = new Rectangle(
                    GRID_OFFSET_X + j * CELL_SIZE - 10,
                    GRID_OFFSET_Y + i * CELL_SIZE,
                    20, CELL_SIZE
                );
                hitbox.setFill(Color.TRANSPARENT);
                hitbox.setCursor(javafx.scene.Cursor.HAND);
                hitbox.setUserData(line);
                hitbox.setOnMouseClicked(e -> handleLineClick(e, line));
                
                gridLines.put(line.getId(), line);
                gridCrosses.put(line.getId(), new ArrayList<>());
                tutorialGrid.getChildren().addAll(line, hitbox);
            }
        }
        
        // Dessiner les points aux intersections
        for (int i = 0; i <= gridRows; i++) {
            for (int j = 0; j <= gridCols; j++) {
                // Gradient pour les points
                RadialGradient dotGradient = new RadialGradient(
                    0, 0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web(isDarkMode ? LIGHT_COLOR : DARK_COLOR)),
                    new Stop(0.8, Color.web(isDarkMode ? LIGHT_COLOR : DARK_COLOR, 0.8)),
                    new Stop(1, Color.web(isDarkMode ? LIGHT_COLOR : DARK_COLOR, 0.4))
                );
                
                Circle dot = new Circle(GRID_OFFSET_X + j * CELL_SIZE, GRID_OFFSET_Y + i * CELL_SIZE, DOT_RADIUS);
                dot.setFill(dotGradient);
                
                // Effet de lueur subtil
                DropShadow dotShadow = new DropShadow();
                dotShadow.setRadius(2.0);
                dotShadow.setColor(Color.web(isDarkMode ? LIGHT_COLOR : DARK_COLOR, 0.3));
                dot.setEffect(dotShadow);
                
                tutorialGrid.getChildren().add(dot);
            }
        }
    }
    
    private static void showStep(int step) {
        boolean isDarkMode = SettingScene.isDarkModeEnabled();
        nextStepButton.setDisable(true);
        instructionsText.getChildren().clear();
        clearHighlights();
        
        switch (step) {
            case 0: showIntroduction(isDarkMode); break;
            case 1: showRules(isDarkMode); break;
            case 2: showAdjacentThrees(isDarkMode); break;
            case 3: showCornerThrees(isDarkMode); break;
            case 4: showTechniqueTwoTop(isDarkMode); break;
            case 5: showTwoConstraints(isDarkMode); break;
            case 6: showCongratulations(isDarkMode); break;
            default: Menu.show(primaryStage); break;
        }
    }
    
    private static void showIntroduction(boolean isDarkMode) {
        stepTitle.setText("Bienvenue au tutoriel Slitherlink");
        stepTitle.setFill(Color.web(isDarkMode ? "#ffffff" : DARK_COLOR));
        
        addInstructionParagraph("Dans ce tutoriel, vous apprendrez à résoudre des puzzles Slitherlink en utilisant des techniques logiques éprouvées.", isDarkMode);
        addInstructionParagraph("Vous découvrirez comment analyser les indices et progresser méthodiquement vers la solution.", isDarkMode);
        addInstructionParagraph("Les techniques présentées ici sont utilisables dans n'importe quel puzzle Slitherlink, quel que soit son niveau de difficulté.", isDarkMode);
        addInstructionHighlight("Cliquez sur \"Suivant\" pour commencer.", isDarkMode);
        
        nextStepButton.setDisable(false);
    }
    
    private static void showRules(boolean isDarkMode) {
        stepTitle.setText("Rappel des règles");
        stepTitle.setFill(Color.web(isDarkMode ? "#ffffff" : DARK_COLOR));
        
        addInstructionParagraph("• Le but est de tracer un circuit fermé unique qui ne se croise ni ne se divise.", isDarkMode);
        addInstructionParagraph("• Chaque chiffre indique le nombre exact de segments qui doivent être tracés autour de cette case.", isDarkMode);
        addInstructionParagraph("• Les cases vides n'imposent aucune contrainte particulière.", isDarkMode);
        addInstructionParagraph("• Le circuit doit former une boucle continue sans branches ni intersections.", isDarkMode);
        
        addInstructionParagraph("Commandes :", isDarkMode);
        addInstructionParagraph("• Cliquez avec le bouton gauche pour tracer une ligne.", isDarkMode);
        addInstructionParagraph("• Cliquez avec le bouton droit pour placer une croix (segment interdit).", isDarkMode);
        
        nextStepButton.setDisable(false);
    }
    
    private static void showAdjacentThrees(boolean isDarkMode) {
        stepTitle.setText("Technique: 3 adjacents en colonne");
        stepTitle.setFill(Color.web(isDarkMode ? "#ffffff" : DARK_COLOR));
        
        addInstructionParagraph("Quand plusieurs 3 sont alignés en colonne ou en ligne, ils doivent partager certains segments pour former un circuit valide.", isDarkMode);
        addInstructionParagraph("Dans notre grille, les trois 3 de la première colonne ne peuvent former un circuit valide que si les segments sont placés de façon spécifique.", isDarkMode);
        addInstructionHighlight("Tracez les lignes indiquées en alternant entre les segments comme montré par les segments clignotants.", isDarkMode);
        
        highlightLine("H_0_0", isDarkMode);
        highlightLine("V_0_0", isDarkMode);
        highlightLine("H_1_0", isDarkMode);
        highlightLine("V_1_1", isDarkMode);
        highlightLine("H_2_0", isDarkMode);
        highlightLine("V_2_0", isDarkMode);
        highlightLine("H_3_0", isDarkMode);
    }

    private static void showCornerThrees(boolean isDarkMode) {
        stepTitle.setText("Technique: 3 dans les coins");
        stepTitle.setFill(Color.web(isDarkMode ? "#ffffff" : DARK_COLOR));
        
        addInstructionParagraph("Un 3 dans un coin ou sur un bord impose des contraintes spécifiques.", isDarkMode);
        addInstructionParagraph("Pour le 3 en haut à gauche, les trois segments doivent être placés sur les trois côtés disponibles.", isDarkMode);
        addInstructionParagraph("De même, pour le 3 en bas à gauche, les segments doivent être placés de façon similaire.", isDarkMode);
        
        addInstructionHighlight("Certains segments sont déjà tracés. Complétez les segments manquants pour les 3 en coins.", isDarkMode);
        
        if (!isLineDrawn("H_3_2")) {
            highlightLine("H_3_2", isDarkMode);
        }
        
        if (!isLineDrawn("V_2_3")) {
            highlightLine("V_2_3", isDarkMode);
        }
    }
    
    private static void showTechniqueTwoTop(boolean isDarkMode) {
        stepTitle.setText("On fait le 2");
        stepTitle.setFill(Color.web(isDarkMode ? "#ffffff" : DARK_COLOR));
        
        addInstructionParagraph("Le chiffre 2 en haut au centre doit avoir exactement deux segments autour de lui.", isDarkMode);
        addInstructionParagraph("Pour former la boucle correctement, ces segments doivent être placés en haut et à droite du 2, ce qui permettra de faire la liaison avec le 1 à sa droite.", isDarkMode);
        addInstructionParagraph("Ce 2 partage son côté gauche avec le 3 en haut à gauche. Si ce segment était tracé pour les deux cases, cela créerait des configurations impossibles.", isDarkMode);
        addInstructionHighlight("Tracez les segments en haut et à droite du 2 pour continuer le circuit.", isDarkMode);
        
        highlightLine("H_0_1", isDarkMode);
        highlightLine("V_0_2", isDarkMode);
    }
    
    private static void showTwoConstraints(boolean isDarkMode) {
        stepTitle.setText("On finit le tracé");
        stepTitle.setFill(Color.web(isDarkMode ? "#ffffff" : DARK_COLOR));

        addInstructionParagraph("Nous avons maintenant plusieurs segments tracés qui doivent former un circuit unique.", isDarkMode);
        addInstructionParagraph("Pour éviter les boucles isolées et créer un circuit fermé, nous devons connecter les segments existants stratégiquement.", isDarkMode);
        addInstructionHighlight("Tracez les segments manquants pour compléter le circuit unique.", isDarkMode);

        highlightLine("V_1_2", isDarkMode);
        highlightLine("H_2_2", isDarkMode);
        highlightLine("H_3_1", isDarkMode);
    }
    
        private static void showCongratulations(boolean isDarkMode) {
        stepTitle.setText("Félicitations!");
        stepTitle.setFill(Color.web(isDarkMode ? "#ffffff" : DARK_COLOR));
        
        addInstructionParagraph("Vous avez résolu avec succès cette grille Slitherlink en utilisant des techniques logiques!", isDarkMode);
        addInstructionParagraph("Vous avez appris à :", isDarkMode);
        addInstructionParagraph("• Identifier les contraintes liées aux 3 adjacents", isDarkMode);
        addInstructionParagraph("• Gérer les 3 dans les coins", isDarkMode);
        addInstructionParagraph("• Appliquer les contraintes du chiffre 2", isDarkMode);
        addInstructionParagraph("• Former un circuit unique et fermé", isDarkMode);
        
        Button startGameButton = createModernButton("Commencer à jouer", true, isDarkMode);
        startGameButton.setOnAction(e -> {
            addClickEffect(startGameButton);
            
            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), startGameButton.getScene().getRoot());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(evt -> GameScene.show(primaryStage));
            fadeOut.play();
        });
        
        VBox buttonBox = new VBox(20, startGameButton);
        buttonBox.setAlignment(Pos.CENTER);
        
        Text spacer = new Text("\n");
        instructionsText.getChildren().addAll(spacer, buttonBox);
        
        nextStepButton.setText("Retour au menu");
        nextStepButton.setOnAction(e -> {
            addClickEffect(nextStepButton);
            
            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), nextStepButton.getScene().getRoot());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(evt -> Menu.show(primaryStage));
            fadeOut.play();
        });
        nextStepButton.setDisable(false);
    }

    private static void handleLineClick(MouseEvent e, Line line) {
        if (!highlightedLines.contains(line.getId())) {
            Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION
                );
                alert.setTitle("Action non autorisée");
                alert.setHeaderText(null);
                alert.setContentText("Seules les lignes en surbrillance peuvent être tracées ou croisées.");
                alert.showAndWait();
            });
            return;
        }
        
        if (e.getButton() == MouseButton.PRIMARY) {
            drawLine(line);
            if(checkProgress() > 0) {
                nextStepButton.setDisable(false);
            }
        } else if (e.getButton() == MouseButton.SECONDARY) {
            placeCross(line);
        }
    }
    
    private static void drawLine(Line line) {
        stopHighlightAnimation(line);
        removeCrosses(line.getId());
        
        boolean isDarkMode = SettingScene.isDarkModeEnabled();
        // Couleur adaptée au mode sombre pour meilleure visibilité
        line.setStroke(Color.web(isDarkMode ? "#ffffff" : DARK_COLOR));
        line.setStrokeWidth(LINE_THICKNESS);
        
        // Animation de trait
        ScaleTransition scaleX = new ScaleTransition(Duration.millis(200), line);
        scaleX.setFromX(0.2);
        scaleX.setToX(1.0);
        
        FadeTransition fade = new FadeTransition(Duration.millis(200), line);
        fade.setFromValue(0.5);
        fade.setToValue(1.0);
        
        ParallelTransition draw = new ParallelTransition(scaleX, fade);
        draw.play();
    }
        
        private static void placeCross(Line line) {
            stopHighlightAnimation(line);
            line.setStroke(Color.TRANSPARENT);
            removeCrosses(line.getId());
            createCross(line, false);
        }
        
        private static void createCross(Line line, boolean isPreview) {
            Line cross1, cross2;
            boolean isVertical = line.getStartX() == line.getEndX();
            boolean isDarkMode = SettingScene.isDarkModeEnabled();
            
            if (isVertical) {
                double midY = (line.getStartY() + line.getEndY()) / 2;
                double length = Math.abs(line.getEndY() - line.getStartY()) * 0.3;
                
                cross1 = new Line(
                    line.getStartX() - length/2, midY - length/2,
                    line.getStartX() + length/2, midY + length/2
                );
                cross2 = new Line(
                    line.getStartX() - length/2, midY + length/2,
                    line.getStartX() + length/2, midY - length/2
                );
            } else {
                double midX = (line.getStartX() + line.getEndX()) / 2;
                double length = Math.abs(line.getEndX() - line.getStartX()) * 0.3;
                
                cross1 = new Line(
                    midX - length/2, line.getStartY() - length/2,
                    midX + length/2, line.getStartY() + length/2
                );
                cross2 = new Line(
                    midX - length/2, line.getStartY() + length/2,
                    midX + length/2, line.getStartY() - length/2
                );
            }
            
            cross1.setStrokeWidth(2.5);
            cross1.setStrokeLineCap(StrokeLineCap.ROUND);
            cross1.setUserData(line);
            
            cross2.setStrokeWidth(2.5);
            cross2.setStrokeLineCap(StrokeLineCap.ROUND);
            cross2.setUserData(line);
            
            if (isPreview) {
                cross1.setStroke(Color.web(ACCENT_COLOR, 0.5));
                cross2.setStroke(Color.web(ACCENT_COLOR, 0.5));
                
                FadeTransition fade1 = new FadeTransition(Duration.millis(800), cross1);
                fade1.setFromValue(0.3);
                fade1.setToValue(0.8);
                fade1.setCycleCount(-1);
                fade1.setAutoReverse(true);
                fade1.play();
                
                FadeTransition fade2 = new FadeTransition(Duration.millis(800), cross2);
                fade2.setFromValue(0.3);
                fade2.setToValue(0.8);
                fade2.setCycleCount(-1);
                fade2.setAutoReverse(true);
                fade2.play();
            } else {
                // Couleur adaptée selon le mode
                cross1.setStroke(Color.web(isDarkMode ? "#ff8080" : ACCENT_COLOR));
                cross2.setStroke(Color.web(isDarkMode ? "#ff8080" : ACCENT_COLOR));
                
                ScaleTransition scale1 = new ScaleTransition(Duration.millis(200), cross1);
                scale1.setFromX(0);
                scale1.setFromY(0);
                scale1.setToX(1.0);
                scale1.setToY(1.0);
                
                ScaleTransition scale2 = new ScaleTransition(Duration.millis(200), cross2);
                scale2.setFromX(0);
                scale2.setFromY(0);
                scale2.setToX(1.0);
                scale2.setToY(1.0);
                
                ParallelTransition parallel = new ParallelTransition(scale1, scale2);
                parallel.play();
                
                gridCrosses.get(line.getId()).add(cross1);
                gridCrosses.get(line.getId()).add(cross2);
            }
            
            tutorialGrid.getChildren().addAll(cross1, cross2);
        }
        
        private static void removeCrosses(String lineId) {
            List<Line> crosses = gridCrosses.get(lineId);
            if (crosses != null) {
                tutorialGrid.getChildren().removeAll(crosses);
                crosses.clear();
            }
        }
        
        private static void highlightLine(String lineId, boolean isDarkMode) {
            Line line = gridLines.get(lineId);
            if (line != null && !isLineDrawn(lineId)) {
                // Animation de surbrillance avec couleurs adaptées au mode
                FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), line);
                fadeIn.setFromValue(0.2);
                fadeIn.setToValue(0.8);
                fadeIn.setCycleCount(-1);
                fadeIn.setAutoReverse(true);
                
                // Couleur plus visible en mode sombre
                line.setStroke(Color.web(isDarkMode ? LIGHT_COLOR : MAIN_COLOR, 0.7));
                line.setStrokeWidth(LINE_THICKNESS);
                
                fadeIn.play();
                line.getProperties().put("fadeTransition", fadeIn);
                highlightedLines.add(lineId);
            }
        }
        
        private static void stopHighlightAnimation(Line line) {
            Object fadeObj = line.getProperties().get("fadeTransition");
            if (fadeObj instanceof FadeTransition) {
                ((FadeTransition) fadeObj).stop();
                line.getProperties().remove("fadeTransition");
            }
        }
        
        private static void clearHighlights() {
            for (Line line : gridLines.values()) {
                stopHighlightAnimation(line);
                boolean isDarkMode = SettingScene.isDarkModeEnabled();
                
                // Ne rendre transparent que si la ligne n'est pas déjà tracée
                if (!isLineDrawn(line.getId()) && line.getStroke() != Color.web(isDarkMode ? "#ffffff" : DARK_COLOR)) {
                    line.setStroke(Color.TRANSPARENT);
                }
            }
            
            highlightedLines.clear();
        }
        
        private static int checkProgress() {
            switch (currentStep) {
                case 2: return checkThreeAdjacentProgress();
                case 3: return checkCornerThreesProgress();
                case 4: return checkBottomRightThreeProgress();
                case 5: return checkTwoConstraintsProgress();
                default: return 0;
            }
        }
        
        private static int checkThreeAdjacentProgress() {
            boolean isDarkMode = SettingScene.isDarkModeEnabled();
            String[] requiredLines = {"H_0_0", "V_0_0", "H_1_0", "V_1_1", "H_2_0", "V_2_0", "H_3_0"};
            boolean allLinesDrawn = true;
            int drawnCount = 0;
            
            for (String lineId : requiredLines) {
                boolean isDrawn = isLineDrawn(lineId);
                if (isDrawn) {
                    drawnCount++;
                } else {
                    allLinesDrawn = false;
                }
            }
            
            if (drawnCount > 0 && drawnCount < requiredLines.length) {
                highlightInstructions("Continuez, vous avez tracé " + drawnCount + " sur " + requiredLines.length + 
                                    " segments nécessaires.", Color.web(isDarkMode ? LIGHT_COLOR : MAIN_COLOR));
            }
            
            if (allLinesDrawn) {
                nextStepButton.setDisable(false);
                highlightInstructions("Excellent! Vous avez correctement appliqué la technique des 3 adjacents.", 
                                   Color.web(isDarkMode ? LIGHT_COLOR : MAIN_COLOR));
                return 1;
            }
            return 0;
        }
        
        private static int checkCornerThreesProgress() {
            boolean isDarkMode = SettingScene.isDarkModeEnabled();
            boolean firstSegmentDrawn = isLineDrawn("H_3_2");
            boolean secondSegmentDrawn = isLineDrawn("V_2_3");
            
            if (firstSegmentDrawn || secondSegmentDrawn) {
                highlightInstructions("Bien! Continuez avec l'autre segment manquant.", 
                                   Color.web(isDarkMode ? LIGHT_COLOR : MAIN_COLOR));
            }
            
            if (firstSegmentDrawn && secondSegmentDrawn) {
                nextStepButton.setDisable(false);
                highlightInstructions("Très bien! Vous avez complété les segments pour les 3 en coins.", 
                                   Color.web(isDarkMode ? LIGHT_COLOR : MAIN_COLOR));
                return 1;
            }
            return 0;
        }
        
        private static int checkBottomRightThreeProgress() {
            boolean isDarkMode = SettingScene.isDarkModeEnabled();
            if (isLineDrawn("H_0_1") && isLineDrawn("V_0_2")) {
                nextStepButton.setDisable(false);
                highlightInstructions("Parfait! Vous avez correctement placé les segments autour du 2.", 
                                   Color.web(isDarkMode ? LIGHT_COLOR : MAIN_COLOR));
                return 1;
            }
            return 0;
        }
        
        private static int checkTwoConstraintsProgress() {
            boolean isDarkMode = SettingScene.isDarkModeEnabled();
            boolean linesDrawn = isLineDrawn("V_1_2") && isLineDrawn("H_2_2") && isLineDrawn("H_3_1");        
            if (linesDrawn) {
                nextStepButton.setDisable(false);
                highlightInstructions("Excellent! Vous avez correctement fermé la boucle.", 
                                   Color.web(isDarkMode ? LIGHT_COLOR : MAIN_COLOR));
                return 1;
            }
            return 0;
        }
        
        private static boolean isLineDrawn(String lineId) {
            Line line = gridLines.get(lineId);
            boolean isDarkMode = SettingScene.isDarkModeEnabled();
            if (line != null && line.getStroke() instanceof Color) {
                Color lineColor = (Color) line.getStroke();
                // Vérifier par rapport au blanc en mode sombre
                Color targetColor = Color.web(isDarkMode ? "#ffffff" : DARK_COLOR);
                
                double tolerance = 0.05;
                return Math.abs(lineColor.getRed() - targetColor.getRed()) < tolerance &&
                       Math.abs(lineColor.getGreen() - targetColor.getGreen()) < tolerance &&
                       Math.abs(lineColor.getBlue() - targetColor.getBlue()) < tolerance;
            }
            return false;
        }
        
        private static boolean isCrossPlacedOn(String lineId) {
            return gridCrosses.get(lineId) != null && !gridCrosses.get(lineId).isEmpty();
        }
        
        private static void addInstructionParagraph(String text, boolean isDarkMode) {
            Text paragraph = new Text(text + "\n\n");
            paragraph.setFont(Font.font("Segoe UI", 16));
            paragraph.setFill(Color.web(isDarkMode ? TEXT_COLOR : DARK_COLOR, 0.9));
            paragraph.setWrappingWidth(instructionsText.getPrefWidth() - 20);
            instructionsText.getChildren().add(paragraph);
        }
        
        private static void addInstructionHighlight(String text, boolean isDarkMode) {
            Text highlight = new Text(text + "\n\n");
            highlight.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            highlight.setFill(Color.web(isDarkMode ? LIGHT_COLOR : MAIN_COLOR));
            highlight.setWrappingWidth(instructionsText.getPrefWidth() - 20);
            instructionsText.getChildren().add(highlight);
        }
        
        private static void highlightInstructions(String text, Color color) {
            Text highlight = new Text(text);
            highlight.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            highlight.setFill(color);
            highlight.setWrappingWidth(instructionsText.getPrefWidth() - 20);
            
            instructionsText.getChildren().clear();
            instructionsText.getChildren().add(highlight);
        }
        
        private static Button createModernButton(String text, boolean isPrimary, boolean isDarkMode) {
            Button button = new Button(text);
            button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            button.setPrefHeight(45);
            button.setMinWidth(150);
            
            String buttonColor = isPrimary ? 
                    (isDarkMode ? MAIN_COLOR : MAIN_COLOR) : 
                    (isDarkMode ? "rgba(60, 65, 75, 0.7)" : "rgba(240, 240, 240, 0.9)");
            
            String textColor = isPrimary ? 
                    "white" : 
                    (isDarkMode ? LIGHT_COLOR : DARK_COLOR);
            
            String hoverColor = isPrimary ? 
                    (isDarkMode ? LIGHT_COLOR : DARK_COLOR) : 
                    (isDarkMode ? "rgba(70, 75, 85, 0.8)" : "rgba(230, 230, 230, 0.95)");
            
            button.setStyle(
                    "-fx-background-color: " + buttonColor + ";" +
                    "-fx-text-fill: " + textColor + ";" +
                    "-fx-background-radius: 10px;" +
                    "-fx-padding: 10px 20px;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);" +
                    "-fx-border-width: 0;");
            
            button.setOnMouseEntered(e -> {
                ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), button);
                scaleUp.setToX(1.05);
                scaleUp.setToY(1.05);
                scaleUp.play();
                
                button.setStyle(
                        "-fx-background-color: " + hoverColor + ";" +
                        "-fx-text-fill: " + textColor + ";" +
                        "-fx-background-radius: 10px;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 3);" +
                        "-fx-border-width: 0;");
            });
            
            button.setOnMouseExited(e -> {
                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), button);
                scaleDown.setToX(1.0);
                scaleDown.setToY(1.0);
                scaleDown.play();
                
                button.setStyle(
                        "-fx-background-color: " + buttonColor + ";" +
                        "-fx-text-fill: " + textColor + ";" +
                        "-fx-background-radius: 10px;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);" +
                        "-fx-border-width: 0;");
            });
            
            return button;
        }
        
        private static void addClickEffect(Button button) {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), button);
            scaleDown.setToX(0.95);
            scaleDown.setToY(0.95);
            scaleDown.play();
            
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(e -> {
                ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), button);
                scaleUp.setToX(1.0);
                scaleUp.setToY(1.0);
                scaleUp.play();
            });
            pause.play();
        }
    }
