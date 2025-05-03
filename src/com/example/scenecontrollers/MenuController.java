package com.example.scenecontrollers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MenuController implements Controller {
    private final BorderPane menuBorderPane;
    private final GridPane gameBoard;
    private final VBox menuVBox;
    private final HBox menuHBox;
    private final Label playGameLabel = new Label("Play Now?");
    private final Button twoPlayerButton = new Button("Two Player");
    private final Button vsAIButton = new Button("AI Opponent");

    public MenuController() {
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
        menuHBox = new HBox();
        menuHBox.setPadding(new Insets(10, 10, 10, 10));
        menuHBox.setAlignment(Pos.CENTER);
        menuHBox.setSpacing(10);
        menuHBox.getChildren().addAll(twoPlayerButton, vsAIButton);

        menuVBox = new VBox();
        menuVBox.setPadding(new Insets(10, 10, 10, 10));
        menuVBox.setAlignment(Pos.CENTER);
        menuVBox.getChildren().addAll(playGameLabel, menuHBox);

        //Button presses
        twoPlayerButton.setOnAction(e -> {
            try {
                BoardSizeController boardSizeController = new BoardSizeController(true);
                twoPlayerButton.getScene().setRoot(boardSizeController.getContent());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        vsAIButton.setOnAction(e -> {
            try {
                BoardSizeController boardSizeController = new BoardSizeController(false);
                vsAIButton.getScene().setRoot(boardSizeController.getContent());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        //add VBox to BorderPane
        menuBorderPane = new BorderPane();
        menuBorderPane.setCenter(gameBoard);
        menuBorderPane.setBottom(menuVBox);
    }

    @Override
    public Parent getContent() { return menuBorderPane; }
}
