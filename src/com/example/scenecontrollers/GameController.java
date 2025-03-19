package com.example.scenecontrollers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameController implements Controller {
    private final GridPane gameBoard;
    private int[][] gameState;
    private int currentPlayer;
    private static final int[] DX = {-1, -1, -1, 0 , 0, 1, 1, 1};
    private static final int[] DY = {-1, 0, 1, -1, 1, -1, 0 ,1};

    public GameController() {
        //initialize game board
        gameBoard = new GridPane();
        gameBoard.setGridLinesVisible(true);
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.setPadding(new Insets(3, 3, 3, 3));
        gameBoard.setStyle("-fx-background-color: #008000;");

        //initialize game board
        gameState = new int[8][8];
        gameState[3][3] = 2;
        gameState[3][4] = 1;
        gameState[4][3] = 1;
        gameState[4][4] = 2;
        currentPlayer = 1;

        //set tokens
        updateGameBoard();
    }

    private StackPane updateToken(int row, int col) {
        StackPane stackPane = new StackPane();
        Circle circle = new Circle(50, 50, 50);
        if (gameState[row][col] == 0) {
            circle.setFill(Color.TRANSPARENT);
        } else if (gameState[row][col] == 1) {
            circle.setFill(Color.BLACK);
        } else circle.setFill(Color.WHITE);
        stackPane.getChildren().add(circle);

        stackPane.setOnMouseClicked(event -> {
            if (makeMove(row, col)) {
                if (currentPlayer == 2) {
                    circle.setFill(Color.BLACK);
                } else circle.setFill(Color.WHITE);
                updateGameBoard();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Move");
                alert.setHeaderText(null);
                alert.setContentText("You cannot place a token there.");
                alert.showAndWait();
            }
        });
        return stackPane;
    }

    private void updateGameBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane playedToken = updateToken(i, j);
                gameBoard.add(playedToken, i, j);
            }
        }
    }

    private boolean isValidMove(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7 || gameState[row][col] != 0) {
            return false;
        }
        for (int i = 0; i < 8; i++) {
            if (checkDirection(row, col, DX[i], DY[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDirection(int row, int col, int dx, int dy) {
        int opponent = (currentPlayer == 1) ? 2 : 1;
        int x = row + dx;
        int y = col + dy;

        if (x < 0 || x > 7 || y < 0 || y > 7 || gameState[x][y] != opponent) {
            return false;
        }
        while (x >= 0 && x <= 7 && y >= 0 && y <= 7) {
            if (gameState[x][y] == 0) {
                return false;
            }
            if (gameState[x][y] == currentPlayer) {
                return true;
            }
            x += dx;
            y += dy;
        }
        return false;
    }

    public boolean makeMove(int row, int col) {
        if (!isValidMove(row, col)) {
            return false;
        }
        gameState[row][col] = currentPlayer;
        for (int i = 0; i < 8; i++) {
            flipTokens(row, col, DX[i], DY[i]);
        }
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        return true;
    }

    private void flipTokens(int row, int col, int dx, int dy) {
        int opponent = (currentPlayer == 1) ? 2 : 1;
        int x = row + dx;
        int y = col + dy;

        while (x >= 0 && x <= 7 && y >= 0 && y <= 7) {
            if (gameState[x][y] == 0) {
                return;
            }
            if (gameState[x][y] == currentPlayer) {
                x -= dx;
                y -= dy;
                while (x != row || y != col) {
                    gameState[x][y] = currentPlayer;
                    x -= dx;
                    y -= dy;
                }
                return;
            }
            x += dx;
            y += dy;
        }
    }

    public boolean evaluateGameState() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (gameState[i][j] == 0) {
                    if (isValidMove(i, j)) return false;
                }
            }
        }
        return true;
    }

    public int getWinner() {
        int blackCount = 0;
        int whiteCount = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (gameState[i][j] == 1) {
                    blackCount++;
                } else if (gameState[i][j] == 2) {
                    whiteCount++;
                }
            }
        }
        return (blackCount > whiteCount) ? 1 : (whiteCount > blackCount) ? 2 : 0;
    }

    @Override
    public Parent getContent() {
        return gameBoard;
    }
}
