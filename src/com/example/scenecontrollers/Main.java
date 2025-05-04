package com.example.scenecontrollers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        MenuController menuController = new MenuController();
        Scene scene = new Scene(menuController.getContent(), 1100, 750, Color.GREEN);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
