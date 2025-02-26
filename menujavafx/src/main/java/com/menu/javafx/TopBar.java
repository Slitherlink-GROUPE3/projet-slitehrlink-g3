package com.menu.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import com.menu.ButtonFactory;

/**
 * Une barre responsive à mettre dans les scenes de jeu
 * Fourni des boutons, des informations sur la partie ainsi qu'un chronometre.
 */
public class TopBar {
    private final Stage primaryStage;
    private final String pseudoJoueur;
    private final String niveau;
    private final String difficulte;
    private HBox topBarContainer;
    private Label chronoLabel;
    
    // Constante pour stylizé la barre 
    private static final String BACKGROUND_COLOR = "#4a90e2";
    private static final String HOVER_COLOR = "#3a80d2";
    private static final double BASE_FONT_SIZE = 14;
    private static final double MIN_FONT_SIZE = 12;
    private static final double MAX_FONT_SIZE = 20;
    
    /**
     * Crée une instance de TopBar
     * 
     * @param primaryStage Le stage de l'application
     * @param pseudoJoueur Le pseudo du joueur
     * @param niveau Le niveau du jeu
     * @param difficulte La difficulté du jeu
     */
    public TopBar(Stage primaryStage, String pseudoJoueur, String niveau, String difficulte) {
        this.primaryStage = primaryStage;
        this.pseudoJoueur = pseudoJoueur;
        this.niveau = niveau;
        this.difficulte = difficulte;
    }

    // Getters
    public String getPseudoJoueur() {
        return this.pseudoJoueur;
    }

    public String getNiveau() {
        return this.niveau;
    }

    public String getDifficulte() {
        return this.difficulte;
    }

    public HBox getTopBar() {
        return topBarContainer;
    }
    
    /**
     * Crée et retourne la HBox de la TopBar
     * 
     * @param scene La scene principal où la TopBar va etre affiché 
     * @return HBox
     */
    public HBox createTopBar(Scene scene) {
        // Main container
        topBarContainer = new HBox();
        topBarContainer.setStyle(
            "-fx-background-color: " + BACKGROUND_COLOR + "; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        );
        topBarContainer.setPadding(new Insets(12, 20, 12, 20));
        topBarContainer.setSpacing(15);
        topBarContainer.setAlignment(Pos.CENTER_LEFT);
        topBarContainer.prefWidthProperty().bind(scene.widthProperty().multiply(0.98));
        topBarContainer.maxHeightProperty().setValue(70);
        
        // Infos joueur
        HBox playerInfoBox = createPlayerInfoSection();
        HBox.setHgrow(playerInfoBox, Priority.ALWAYS);
        
        // Control section (Réessayer, Pause..)
        HBox controlsBox = createGameControlsSection();
        
        // Ajoute a la TopBar
        topBarContainer.getChildren().addAll(playerInfoBox, controlsBox);
        
        // La barre est responsive
        setupResponsiveLayout(scene);
        
        return topBarContainer;
    }
    
    /**
     * Creates the player information section (username, level, difficulty)
     * 
     * @return HBox containing player information
     */
    private HBox createPlayerInfoSection() {
        HBox playerInfoBox = new HBox();
        playerInfoBox.prefWidthProperty().bind(topBarContainer.widthProperty().multiply(0.5));
        playerInfoBox.maxWidthProperty().bind(topBarContainer.widthProperty().multiply(0.5));
        playerInfoBox.setSpacing(20);
        playerInfoBox.setAlignment(Pos.CENTER_LEFT);
        
        // Create info labels with icons
        VBox usernameBox = createInfoLabel("Joueur", pseudoJoueur);
        VBox levelBox = createInfoLabel("Niveau", niveau);
        VBox difficultyBox = createInfoLabel("Difficulté", difficulte);
        
        // Ajout d'un conteneur pour chaque VBox avec HGrow
        HBox usernameContainer = new HBox(usernameBox);
        HBox levelContainer = new HBox(levelBox);
        HBox difficultyContainer = new HBox(difficultyBox);
        
        HBox.setHgrow(usernameContainer, Priority.ALWAYS);
        HBox.setHgrow(levelContainer, Priority.ALWAYS);
        HBox.setHgrow(difficultyContainer, Priority.ALWAYS);
        
        // Ajout des conteneurs dans la HBox
        playerInfoBox.getChildren().addAll(usernameContainer, levelContainer, difficultyContainer);
        
        return playerInfoBox;
    }
    
    /**
     * Creates the game controls section (timer, retry, pause)
     * 
     * @return HBox containing game controls
     */
    private HBox createGameControlsSection() {
        HBox controlsBox = new HBox();
        controlsBox.prefWidthProperty().bind(topBarContainer.widthProperty().multiply(0.5));
        controlsBox.maxWidthProperty().bind(topBarContainer.widthProperty().multiply(0.5));
        controlsBox.setSpacing(20);
        controlsBox.setAlignment(Pos.CENTER_RIGHT);
        
        // Timer
        chronoLabel = new Label("00:00");
        chronoLabel.setFont(Font.font("Arial", FontWeight.BOLD, BASE_FONT_SIZE));
        chronoLabel.setTextFill(Color.WHITE);
        chronoLabel.setEffect(new DropShadow(5, Color.BLACK));
        
        // Create action buttons
        Button retryButton = createActionButton("Réessayer");
        Button pauseButton = createActionButton("Pause");
        
        // Add action handlers
        retryButton.setOnAction(e -> {
            // TODO: Implement retry logic
            System.out.println("Game retry requested");
        });
        
        pauseButton.setOnAction(e -> {
            // TODO: Implement pause logic
            System.out.println("Game pause requested");
        });

        HBox chronoLabelContainer = new HBox(chronoLabel);
        HBox retryButtonContainer = new HBox(retryButton);
        HBox pauseButtonContainer = new HBox(pauseButton);

        HBox.setHgrow(chronoLabelContainer, Priority.ALWAYS);
        HBox.setHgrow(retryButtonContainer, Priority.ALWAYS);
        HBox.setHgrow(pauseButtonContainer, Priority.ALWAYS);
        
        controlsBox.getChildren().addAll(chronoLabelContainer, retryButtonContainer, pauseButtonContainer);
        
        return controlsBox;
    }
    
    /**
     * Creates a labeled info field with title and value
     * 
     * @param title The field title
     * @param value The field value
     * @return VBox containing the labeled field
     */
    private VBox createInfoLabel(String title, String value) {
        VBox container = new VBox(3);
        container.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, BASE_FONT_SIZE - 2));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setEffect(new DropShadow(3, Color.BLACK));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, BASE_FONT_SIZE));
        valueLabel.setTextFill(Color.WHITE);
        valueLabel.setEffect(new DropShadow(5, Color.BLACK));
        
        container.getChildren().addAll(titleLabel, valueLabel);
        return container;
    }
    
    /**
     * Creates a styled action button
     * 
     * @param text Button text
     * @return Styled Button
     */
    private Button createActionButton(String text) {
        Button button = ButtonFactory.createAnimatedButton(text);
        
        button.setStyle(
            "-fx-background-color: white; " +
            "-fx-text-fill: #303030; " +
            "-fx-font-size: " + BASE_FONT_SIZE + "px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8px 12px; " +
            "-fx-background-radius: 5px;"
        );
        
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: #f0f0f0; " +
                "-fx-text-fill: #303030; " +
                "-fx-font-size: " + BASE_FONT_SIZE + "px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8px 12px; " +
                "-fx-background-radius: 5px;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: white; " +
                "-fx-text-fill: #303030; " +
                "-fx-font-size: " + BASE_FONT_SIZE + "px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8px 12px; " +
                "-fx-background-radius: 5px;"
            )
        );
        
        return button;
    }
    
    /**
     * Sets up responsive behavior for the top bar
     * 
     * @param scene The main scene
     */
    private void setupResponsiveLayout(Scene scene) {
        // Adjust font size based on window width
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            
            // Calculate appropriate font size based on window width
            double fontSizeFactor = Math.max(0.85, Math.min(1.3, width / 1200.0));
            double fontSize = Math.max(MIN_FONT_SIZE, Math.min(MAX_FONT_SIZE, BASE_FONT_SIZE * fontSizeFactor));
            
            // Update all UI elements with new font size
            updateFontSizes(fontSize);
        });
        
        // Adjust padding based on window width
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            
            if (width < 800) {
                topBarContainer.setPadding(new Insets(8, 10, 8, 10));
                topBarContainer.setSpacing(8);
            } else {
                topBarContainer.setPadding(new Insets(12, 20, 12, 20));
                topBarContainer.setSpacing(15);
            }
        });
    }
    
    /**
     * Updates font sizes for all UI elements
     * 
     * @param fontSize Base font size to use
     */
    private void updateFontSizes(double fontSize) {
        // Traverse through all elements in the top bar and update fonts
        updateNodeFonts(topBarContainer, fontSize);
    }
    
    /**
     * Recursively updates fonts for a JavaFX node and its children
     * 
     * @param node The node to update
     * @param fontSize Base font size to use
     */
    private void updateNodeFonts(javafx.scene.Node node, double fontSize) {
        if (node instanceof Label) {
            Label label = (Label) node;
            
            // Determine if this is a title label (slightly smaller) or a value label
            boolean isTitle = label.getFont().getSize() < BASE_FONT_SIZE;
            double adjustedSize = isTitle ? fontSize - 2 : fontSize;
            
            label.setFont(Font.font(
                label.getFont().getFamily(),
                label.getFont().getStyle().contains("Bold") ? FontWeight.BOLD : FontWeight.NORMAL,
                adjustedSize
            ));
        } else if (node instanceof Button) {
            Button button = (Button) node;
            
            // Update button style with new font size
            String currentStyle = button.getStyle();
            String newStyle = currentStyle.replaceAll(
                "-fx-font-size: [^;]+;", 
                "-fx-font-size: " + fontSize + "px;"
            );
            
            button.setStyle(newStyle);
        } else if (node instanceof javafx.scene.Parent) {
            // Recursively update children
            for (javafx.scene.Node child : ((javafx.scene.Parent) node).getChildrenUnmodifiable()) {
                updateNodeFonts(child, fontSize);
            }
        }
    }
    
    /**
     * Updates the chronometer time display
     * 
     * @param minutes Minutes elapsed
     * @param seconds Seconds elapsed
     */
    public void updateChronometer(int minutes, int seconds) {
        String formattedTime = String.format("%02d:%02d", minutes, seconds);
        chronoLabel.setText(formattedTime);
    }
}