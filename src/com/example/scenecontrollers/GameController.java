package com.example.scenecontrollers;

import javafx.scene.Parent;
import javafx.scene.layout.VBox;

public class GameController implements Controller {
    private final VBox rootVBox;

    public GameController() {
        rootVBox = new VBox();
    }

    @Override
    public Parent getContent() {
        return rootVBox;
    }
}
