package com.example.scenecontrollers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        GameController gameController = new GameController();
        Scene scene = new Scene(gameController.getContent(), 1100, 700, Color.GREEN);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
