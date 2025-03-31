package com.menu.javafx;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text; // Pour l'ombre des boutons
import javafx.util.Duration; // Pour la couleur des effets


public class ButtonFactory {

    public static Button createAnimatedButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 18));
        button.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-border-radius: 15; -fx-background-radius: 15;");

        // Animation au survol
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #444444; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #000000; -fx-text-fill: white;"));

        // Animation au clic
        button.setOnMousePressed(e -> animateButton(button, 1.1));
        button.setOnMouseReleased(e -> animateButton(button, 1.0));

        return button;
    }

    public static Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 20));
        button.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 15;");

        // Effet d'ombre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.GRAY);
        button.setEffect(shadow);

        // Style original
        String defaultStyle = "-fx-background-color: black; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 15;";
        String hoverStyle = "-fx-background-color: #333333; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 15;";

        // Appliquer le style au survol
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));

        return button;
    }

    public static Button createImageButton(String imagePath) {
        Image image = new Image(ButtonFactory.class.getResourceAsStream("/" + imagePath));
    
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50); // Ajuste la taille selon ton besoin
        imageView.setFitHeight(50);
    
        Button button = new Button();
        button.setGraphic(imageView);
        
        // Supprime l'arrière-plan, la bordure et le padding
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0;");
        
        // Effet au survol
        button.setOnMouseEntered(e -> button.setOpacity(0.7));
        button.setOnMouseExited(e -> button.setOpacity(1.0));
        
        // Effet de clic
        button.setOnMousePressed(e -> animateButton(button, 1.1));
        button.setOnMouseReleased(e -> animateButton(button, 1.0));
    
        return button;
    }

    public static Button createHelpButton() {
        Button button = new Button("AIDE ?");
        button.setFont(Font.font("Arial", 18));
        button.setPrefSize(190, 50);
        button.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 15px 30px; -fx-background-radius: 10;");

        // Effet d'ombre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.GRAY);
        button.setEffect(shadow);

        // Animation au survol
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #444444; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: black; -fx-text-fill: white;"));

        return button;
    }

    public static Button createCheckButton(Runnable onClick) {
        Button button = new Button("Check");
        button.setFont(Font.font("Arial", 18));
        button.setPrefSize(140, 40);
        button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 10;");

        // Effet d'ombre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.DARKGREEN);
        button.setEffect(shadow);

        // Animation au survol
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #388E3C; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"));

        // Animation au clic
        button.setOnMousePressed(e -> animateButton(button, 1.1));
        button.setOnMouseReleased(e -> animateButton(button, 1.0));

        button.setOnAction(e -> onClick.run());
        return button;
    }
    
    public static Button createHypothesisButton() {
        Button button = new Button("Hypothèse");
        button.setFont(Font.font("Arial", 18));
        button.setPrefSize(190, 50);
        button.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 15px 30px; -fx-background-radius: 10;");

        // Effet d'ombre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.GRAY);
        button.setEffect(shadow);

        // Animation au survol
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #444444; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: black; -fx-text-fill: white;"));

        return button;
    }

    public static Button createSkullButton(String text, int difficulty, String imagePath) {
        Image skullImage = new Image(imagePath);

        // Contenu du bouton
        HBox content = new HBox(5);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        Text buttonText = new Text(text);
        buttonText.setFont(Font.font("Arial", 14));

        for (int i = 0; i < 3; i++) {
            ImageView skullIcon = new ImageView(skullImage);
            skullIcon.setFitWidth(20);
            skullIcon.setFitHeight(20);
            if (i >= difficulty) skullIcon.setOpacity(0.5);
            content.getChildren().add(skullIcon);
        }
        content.getChildren().add(0, buttonText);

        // Créer le bouton
        Button button = new Button();
        button.setGraphic(content);
        button.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: black;");

        // Animation au clic
        button.setOnMousePressed(e -> animateButton(button, 1.1));
        button.setOnMouseReleased(e -> animateButton(button, 1.0));

        return button;
    }

    private static void animateButton(Button button, double scale) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.play();
    }
}
