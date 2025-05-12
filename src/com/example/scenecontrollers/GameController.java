package com.example.scenecontrollers;

import com.example.backend.AIOpponent;
import com.example.backend.OthelloGame;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

public class GameController implements Controller {
	private final BorderPane gameBorderPane;
	private final GridPane gameBoard;
	public OthelloGame currentGame;
	public VBox vBoxLeft;
	public VBox vBoxRight;
	public VBox vBoxBottom;
	public HBox hBoxBottom;
	public final Label placeHolderLabel = new Label("");
	private final Text blackScore;
	private final Text whiteScore;
	private final Button playAgainButton = new Button("Play Again");
	private final Button mainMenuButton = new Button("Main Menu");
	private final boolean twoPlayer;

	public GameController(boolean twoPlayer, int boardSize) {
		this.twoPlayer = twoPlayer;

		//VBox Left
		vBoxLeft = new VBox();
		vBoxLeft.setPadding(new Insets(100, 100, 100, 100));
		vBoxLeft.setStyle("-fx-background-color: #000000;");
		vBoxLeft.setAlignment(Pos.CENTER);
		blackScore = new Text();
		blackScore.setX(50.0f);
		blackScore.setY(50.0f);
		blackScore.setText("02");
		blackScore.setFill(Color.WHITE);
		blackScore.setFont(Font.font(null, FontWeight.BOLD, 50));
		vBoxLeft.getChildren().add(blackScore);

		//VBox Right
		vBoxRight = new VBox();
		vBoxRight.setPadding(new Insets(100, 100, 100, 100));
		vBoxRight.setStyle("-fx-background-color: #FFFFFF;");
		vBoxRight.setAlignment(Pos.CENTER);
		whiteScore = new Text();
		whiteScore.setX(50.0f);
		whiteScore.setY(50.0f);
		whiteScore.setText("02");
		whiteScore.setFill(Color.BLACK);
		whiteScore.setFont(Font.font(null, FontWeight.BOLD, 50));
		vBoxRight.getChildren().add(whiteScore);

		//Border Pane
		gameBorderPane = new BorderPane();

		//initialize GUI
		gameBoard = new GridPane();
		gameBoard.setGridLinesVisible(true);
		gameBoard.setAlignment(Pos.CENTER);
		gameBoard.setPadding(new Insets(3, 3, 3, 3));
		gameBoard.setStyle("-fx-background-color: #008000;");
		gameBoard.setOnMouseClicked((e) -> {
			blackScore.setText("" + currentGame.getBlackScore());
			whiteScore.setText("" + currentGame.getWhiteScore());
			gameBorderPane.setLeft(vBoxLeft);
			gameBorderPane.setRight(vBoxRight);
		});

		//initialize game
		currentGame = new OthelloGame(boardSize);
		
		//makes sure initial score is 2-2
		blackScore.setText("" + currentGame.getBlackScore());
		whiteScore.setText("" + currentGame.getWhiteScore());
		
		//set tokens
		updateGameBoard();

		//VBox Bottom
		hBoxBottom = new HBox();
		hBoxBottom.setPadding(new Insets(10, 10, 10, 10));
		hBoxBottom.setAlignment(Pos.CENTER);
		hBoxBottom.setSpacing(10);
		hBoxBottom.getChildren().addAll(playAgainButton, mainMenuButton);

		vBoxBottom = new VBox();
		vBoxBottom.setPadding(new Insets(10, 10, 10, 10));
		vBoxBottom.setAlignment(Pos.CENTER);
		vBoxBottom.getChildren().addAll(placeHolderLabel, hBoxBottom);

		//Button presses
		playAgainButton.setOnAction(e -> {
			try {
				GameController gameController = new GameController(twoPlayer, currentGame.boardSize);
				playAgainButton.getScene().setRoot(gameController.getContent());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		mainMenuButton.setOnAction(e -> {
			try {
				MenuController menuController = new MenuController();
				mainMenuButton.getScene().setRoot(menuController.getContent());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		//gameBorderPane.setTop(headerHBox);
		gameBorderPane.setCenter(gameBoard);
		gameBorderPane.setLeft(vBoxLeft);
		gameBorderPane.setRight(vBoxRight);
		gameBorderPane.setBottom(vBoxBottom);
	}

	private StackPane updateToken(int row, int col) {
		StackPane stackPane = new StackPane();
		Circle circle = new Circle(35, 35, 35);

		if (currentGame.gameState[row][col] == 0) {
			circle.setFill(Color.TRANSPARENT);
		} else if (currentGame.gameState[row][col] == OthelloGame.BLACK) {
			circle.setFill(Color.BLACK);
		} else {
			circle.setFill(Color.WHITE);
		}
		stackPane.getChildren().add(circle);

		stackPane.setOnMouseClicked(event -> {
			if (currentGame.makeMove(row, col)) {
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
		if(currentGame.currentPlayer == OthelloGame.WHITE && !twoPlayer) { //AI Opponent is always white player
			if (OthelloGame.evaluateGameState(currentGame.gameState, OthelloGame.WHITE)) {
				currentGame.currentPlayer = OthelloGame.BLACK;
			} else {
				List<Integer> moveAI = AIOpponent.getMove(currentGame);
				if(moveAI != null) {
					int aiRow = moveAI.get(0);
					int aiColumn = moveAI.get(1);
					currentGame.makeMove(aiRow, aiColumn);
				}
			}
		}
		for (int i = 0; i < currentGame.boardSize; i++) {
			for (int j = 0; j < currentGame.boardSize; j++) {
				StackPane playedToken = updateToken(i, j);
				gameBoard.getChildren().removeAll();
				gameBoard.add(playedToken, i, j);
			}
		}
		if (OthelloGame.evaluateGameState(currentGame.gameState, OthelloGame.BLACK) &&
				!OthelloGame.evaluateGameState(currentGame.gameState, OthelloGame.WHITE) &&
				!twoPlayer) {
			currentGame.currentPlayer = OthelloGame.WHITE;
			updateGameBoard();
		}
		if (currentGame.evaluateGameState()) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Game Over");
			alert.setHeaderText(null);
			if (currentGame.getWinner() == OthelloGame.BLACK) {
				alert.setContentText("Congratulations! You won!");
			} else if (currentGame.getWinner() == OthelloGame.WHITE) {
				alert.setContentText("AI Opponent won");
			} else {
				alert.setContentText("Draw!");
			}
			alert.showAndWait();
		}
	}

	@Override
	public Parent getContent() {
		return gameBorderPane;
	}
}
