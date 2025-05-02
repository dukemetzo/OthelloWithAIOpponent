package com.example.backend;

public class OthelloGame implements Cloneable {
	public int boardSize;
	public int[][] gameState;
	public int currentPlayer;
	public static final int[] DX = {-1, -1, -1, 0 , 0, 1, 1, 1};
	public static final int[] DY = {-1, 0, 1, -1, 1, -1, 0 ,1};
	public static final int BLACK = 1;
	public static final int WHITE = 2;

	public OthelloGame(int boardSize) {
		this.boardSize = boardSize;
		gameState = new int[boardSize][boardSize];
		gameState[boardSize/2 - 1][boardSize/2 - 1] = WHITE;
		gameState[boardSize/2 - 1][boardSize/2] = BLACK;
		gameState[boardSize/2][boardSize/2 - 1] = BLACK;
		gameState[boardSize/2][boardSize/2] = WHITE;
		currentPlayer = BLACK;
	}

	private boolean isValidMove(int row, int col) {
		if (row < 0 || row > boardSize - 1 || col < 0 || col > boardSize - 1 || gameState[row][col] != 0) {
			return false;
		}
		for (int i = 0; i < 8; i++) {
			if (checkDirection(row, col, DX[i], DY[i])) {
				return true;
			}
		}
		return false;
	}

	//static version of method for use by AI Opponent
	public static boolean isValidMove(int row, int col, int[][] gameState, int boardSize, int currentPlayer) {
		if (row < 0 || row > boardSize - 1 || col < 0 || col > boardSize - 1 || gameState[row][col] != 0) {
			return false;
		}
		for (int i = 0; i < 8; i++) {
			if (checkDirection(row, col, DX[i], DY[i], gameState, boardSize, currentPlayer)) {
				System.out.println("Direction " + DX[i] + ", " + DY[i] + " passes checkDirection for move " + row + ", " + col);
				return true;
			}
		}
		return false;
	}

	private boolean checkDirection(int row, int col, int dx, int dy) {
		int opponent = (currentPlayer == BLACK) ? WHITE : BLACK;
		int x = row + dx;
		int y = col + dy;

		if (x < 0 || x > boardSize - 1 || y < 0 || y > boardSize - 1 || gameState[x][y] != opponent) {
			return false;
		}
		while (x >= 0 && x <= boardSize - 1 && y >= 0 && y <= boardSize - 1) {
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

	//static version of method for use by AI Opponent
	private static boolean checkDirection(int row, int col, int dx, int dy, int[][] gameState, int boardSize, int currentPlayer) {
		int opponent = (currentPlayer == BLACK) ? WHITE : BLACK;
		int x = row + dx;
		int y = col + dy;

		if (x < 0 || x > boardSize - 1 || y < 0 || y > boardSize - 1 || gameState[x][y] != opponent) {
			return false;
		}
		while (x >= 0 && x <= boardSize - 1 && y >= 0 && y <= boardSize - 1) {
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
		currentPlayer = (currentPlayer == BLACK) ? WHITE : BLACK;
		return true;
	}

	//static version of method for use by AI Opponent
	public static int[][] makeMove(int row, int col, int[][] gameState, int currentPlayer) {
		/*if (!isValidMove(row, col, gameState, gameState.length, currentPlayer)) {
            return gameState;
        }*/
		gameState[row][col] = currentPlayer;
		for (int i = 0; i < 8; i++) {
			flipTokens(row, col, DX[i], DY[i], gameState, currentPlayer);
		}
		//currentPlayer = (currentPlayer == BLACK) ? WHITE : BLACK;
		return gameState;
	}

	private void flipTokens(int row, int col, int dx, int dy) {
		int opponent = (currentPlayer == BLACK) ? WHITE : BLACK;
		int x = row + dx;
		int y = col + dy;

		while (x >= 0 && x <= boardSize - 1 && y >= 0 && y <= boardSize - 1) {
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

	//static version of method for use by AI Opponent
	private static void flipTokens(int row, int col, int dx, int dy, int[][] gameState, int currentPlayer) {
		int opponent = (currentPlayer == BLACK) ? WHITE : BLACK;
		int x = row + dx;
		int y = col + dy;

		while (x >= 0 && x <= gameState.length - 1 && y >= 0 && y <= gameState.length - 1) {
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
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (gameState[i][j] == 0) {
					if (isValidMove(i, j)) return false;
				}
			}
		}
		return true;
	}

	//static version of method for use by AI Opponent
	public static boolean evaluateGameState(int[][] gameState, int currentPlayer) {
		for (int i = 0; i < gameState.length; i++) {
			for (int j = 0; j < gameState.length; j++) {
				if (gameState[i][j] == 0) {
					if (isValidMove(i, j, gameState, gameState.length, currentPlayer))
						return false;
				}
			}
		}
		return true;
	}

	public int getBlackScore() {
		int blackScore = 0;
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (gameState[i][j] == BLACK)
					blackScore++;
			}
		}
		return blackScore;
	}

	//static version of method for use by AI Opponent
	public static int getBlackScore(int[][] gameState) {
		int blackScore = 0;
		for (int i = 0; i < gameState.length; i++) {
			for (int j = 0; j < gameState.length; j++) {
				if (gameState[i][j] == BLACK)
					blackScore++;
			}
		}
		return blackScore;
	}

	public int getWhiteScore() {
		int whiteScore = 0;
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (gameState[i][j] == WHITE)
					whiteScore++;
			}
		}
		return whiteScore;
	}

	//static version of method for use by AI Opponent
	public static int getWhiteScore(int[][] gameState) {
		int whiteScore = 0;
		for (int i = 0; i < gameState.length; i++) {
			for (int j = 0; j < gameState.length; j++) {
				if (gameState[i][j] == WHITE) {
					whiteScore++;
				}
			}
		}
		return whiteScore;
	}

	public int getWinner() {
		return (getBlackScore() > getWhiteScore()) ? BLACK : (getWhiteScore() > getBlackScore()) ? WHITE : 0;
	}

	//static version of method for use by AI Opponent
	public static int getWinner(int[][] gameState, int currentPlayer) {
		int blackScore = 0;
		int whiteScore = 0;
		for (int i = 0; i < gameState.length; i++) {
			for (int j = 0; j < gameState.length; j++) {
				if (gameState[i][j] == WHITE)
					whiteScore++;
				if (gameState[i][j] == BLACK)
					blackScore++;
			}
		}
		if (currentPlayer == BLACK)
			return Integer.compare(blackScore, whiteScore);
		else return Integer.compare(whiteScore, blackScore);
	}

	@Override
	public OthelloGame clone() {
		try {
			OthelloGame clonedGame = (OthelloGame) super.clone();
			clonedGame.boardSize = this.boardSize;
			clonedGame.currentPlayer = this.currentPlayer;

			clonedGame.gameState = new int[this.boardSize][this.boardSize];
			for(int i = 0; i < this.boardSize; i++) {
				System.arraycopy(this.gameState[i], 0, clonedGame.gameState[i], 0, this.boardSize);
			}
			return clonedGame;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
