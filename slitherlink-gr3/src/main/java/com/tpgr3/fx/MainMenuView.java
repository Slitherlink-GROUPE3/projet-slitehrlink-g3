package com.tpgr3.fx;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.Parent;

public class MainMenuView {

    public static Parent getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Text titre = new Text("🎮 Slitherlink");
        Button btnClassique = new Button("Classique");
        Button btnAventure = new Button("Aventure");
        Button btnTutoriel = new Button("Tutoriel");

        btnAventure.setOnAction(e -> {
            Scene aventureScene = new Scene(AventureView.getView(), 1000, 700);
            Main.changerScene(aventureScene);
        });
        

        root.getChildren().addAll(titre, btnClassique, btnAventure, btnTutoriel);
        return root;
    }
}
