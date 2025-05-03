package com.example.scenecontrollers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BoardSizeController implements Controller {
    private final BorderPane boardSizeBorderPane;
    private final GridPane gameBoard;
    private final VBox boardSizeVBox;
    private final HBox boardSizeHBox;
    private final Label boardSizeLabel = new Label("Select Board Size:");
    private final Button fourByFourButton = new Button("4x4");
    private final Button sixBySixButton = new Button("6x6");
    private final Button eightByEightButton = new Button("8x8");

    public BoardSizeController(boolean twoPlayer) {
        gameBoard = new GridPane();
        gameBoard.setGridLinesVisible(true);
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.setPadding(new Insets(3, 3, 3, 3));
        gameBoard.setStyle("-fx-background-color: #008000;");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane stackPane = new StackPane();
                Circle circle = new Circle(35, 35, 35);
                if ((i == 3 && j == 3) || (i == 4 && j == 4))
                    circle.setFill(Color.WHITE);
                else if ((i == 3 && j == 4) || (i == 4 && j == 3))
                    circle.setFill(Color.BLACK);
                else
                    circle.setFill(Color.TRANSPARENT);
                stackPane.getChildren().add(circle);
                gameBoard.add(stackPane, i, j);
            }
        }

        //Menu layout
        boardSizeHBox = new HBox();
        boardSizeHBox.setPadding(new Insets(10, 10, 10, 10));
        boardSizeHBox.setAlignment(Pos.CENTER);
        boardSizeHBox.setSpacing(10);
        boardSizeHBox.getChildren().addAll(fourByFourButton, sixBySixButton, eightByEightButton);

        boardSizeVBox = new VBox();
        boardSizeVBox.setPadding(new Insets(10, 10, 10, 10));
        boardSizeVBox.setAlignment(Pos.CENTER);
        boardSizeVBox.getChildren().addAll(boardSizeLabel, boardSizeHBox);

        //Button presses
        fourByFourButton.setOnAction(e -> {
            try {
                GameController gameController = new GameController(twoPlayer, 4);
                fourByFourButton.getScene().setRoot(gameController.getContent());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        sixBySixButton.setOnAction(e -> {
            try {
                GameController gameController = new GameController(twoPlayer, 6);
                sixBySixButton.getScene().setRoot(gameController.getContent());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        eightByEightButton.setOnAction(e -> {
            try {
                GameController gameController = new GameController(twoPlayer, 8);
                eightByEightButton.getScene().setRoot(gameController.getContent());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        boardSizeBorderPane = new BorderPane();
        boardSizeBorderPane.setCenter(gameBoard);
        boardSizeBorderPane.setBottom(boardSizeVBox);
    }

    @Override
    public Parent getContent() { return boardSizeBorderPane; }
}
