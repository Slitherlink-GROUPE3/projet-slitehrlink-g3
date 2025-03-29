package com.tpgr3.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        Scene scene = new Scene(MainMenuView.getView(), 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Slitherlink");
        stage.show();
    }

    public static void changerScene(Scene nouvelleScene) {
        primaryStage.setScene(nouvelleScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
