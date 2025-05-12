package com.example.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AIOpponent {
	private static final int[][] positionalWeights_6x6 = {
			{100, -20, 10, 10, -20, 100},
			{-20, -50, -2, -2, -50, -20},
			{10,   -2,  0,  0, -2,   10},
			{10,   -2,  0,  0, -2,   10},
			{-20, -50, -2, -2, -50, -20},
			{100, -20, 10, 10, -20, 100}
	};
	private static final int[][] positionalWeights_8x8 = {
			{ 100, -20, 10,  5,  5, 10, -20, 100 },
			{ -20, -50, -2, -2, -2, -2, -50, -20 },
			{  10,  -2,  5,  1,  1,  5,  -2,  10 },
			{   5,  -2,  1,  0,  0,  1,  -2,   5 },
			{   5,  -2,  1,  0,  0,  1,  -2,   5 },
			{  10,  -2,  5,  1,  1,  5,  -2,  10 },
			{ -20, -50, -2, -2, -2, -2, -50, -20 },
			{ 100, -20, 10,  5,  5, 10, -20, 100 }
	};
	private static final double positionalWeight = 0.9;
	private static final double mobilityWeight = 0.3;

	static class ValueMovePair {
		int v;
		List<Integer> move;

		ValueMovePair (int v, List<Integer> move) {
			this.v = v;
			this.move = move;
		}

		public ValueMovePair setMove (List<Integer> move) {
			this.move = move;
			return this;
		}
	}

	public static List<Integer> getMove(OthelloGame currentGame) {
		if (currentGame.boardSize == 4) {
			return alphaBetaSearch(currentGame.clone());
		} else {
			return heuristicSearch(currentGame.clone());
		}
	}

	public static List<Integer> alphaBetaSearch(OthelloGame currentGame) {
		return maxValue(currentGame, Integer.MIN_VALUE, Integer.MAX_VALUE).move;
	}

	public static ValueMovePair maxValue(OthelloGame currentGame, int alpha, int beta) {
		if (OthelloGame.evaluateGameState(currentGame.gameState, currentGame.currentPlayer))
			return new ValueMovePair(calculateUtility(currentGame.gameState, currentGame.currentPlayer), null);
		ValueMovePair pair = new ValueMovePair(Integer.MIN_VALUE, null);
		for (Map.Entry<List<Integer>, OthelloGame> successor : getSuccessors(currentGame.clone()).entrySet()) {
			ValueMovePair minPair = minValue(successor.getValue(), alpha, beta);
			if(minPair.v > pair.v) {
				pair = new ValueMovePair(minPair.v, successor.getKey());
			}
			if (pair.v >= beta)
				return pair;
			alpha = Math.max(alpha, pair.v);
		}
		return pair;
	}

	public static ValueMovePair minValue(OthelloGame currentGame, int alpha, int beta) {
		if (OthelloGame.evaluateGameState(currentGame.gameState, currentGame.currentPlayer))
			return new ValueMovePair(calculateUtility(currentGame.gameState, currentGame.currentPlayer), null);
		ValueMovePair pair = new ValueMovePair(Integer.MAX_VALUE, null);
		for (Map.Entry<List<Integer>, OthelloGame> successor : getSuccessors(currentGame).entrySet()) {
			ValueMovePair maxPair = maxValue(successor.getValue(), alpha, beta);
			if(maxPair.v < pair.v) {
				pair = new ValueMovePair(maxPair.v, successor.getKey());
			}
			if (pair.v <= alpha)
				return pair;
			beta = Math.min(beta, pair.v);
		}
		return pair;
	}

	//Gives numerical value of terminal states. e.g. win (+1), lose(-1), and draw(0)
	public static int calculateUtility(int[][] gameState, int currentPlayer) {
		return OthelloGame.getWinner(gameState, currentPlayer);
	}

	public static List<Integer> heuristicSearch(OthelloGame currentGame) {
		List<Integer> bestSuccessorMove = new ArrayList<>();
		double bestEvaluation = Integer.MIN_VALUE;
		for (Map.Entry<List<Integer>, OthelloGame> successor : getSuccessors(currentGame).entrySet()) {
			double currentEvaluation = getEvaluation(successor.getValue().gameState, currentGame.currentPlayer);
			/**
			 * Alternative evaluation using weights assigned to each position
			 */
			/*double currentEvaluation = 0;
			if (currentGame.boardSize == 6) {
				currentEvaluation = getEvaluation_6x6(successor.getValue().gameState, currentGame.currentPlayer);
			} else if (currentGame.boardSize == 8) {
				currentEvaluation = getEvaluation_8x8(successor.getValue().gameState, currentGame.currentPlayer);
			}
			 */
			if (bestSuccessorMove.isEmpty()) {
				bestSuccessorMove.add(successor.getKey().get(0));
				bestSuccessorMove.add(successor.getKey().get(1));
				bestEvaluation = currentEvaluation;
			}
			if (currentEvaluation > bestEvaluation) {
				bestSuccessorMove.set(0, successor.getKey().get(0));
				bestSuccessorMove.set(1, successor.getKey().get(1));
				bestEvaluation = currentEvaluation;
			}
		}
		return bestSuccessorMove;
	}

	public static double getEvaluation(int[][] gameState, int currentPlayer) {
		int whiteCorners = 0, whiteCornerDanger = 0, blackCorners = 0, blackCornerDanger = 0;
		int[] cornerOccupancy = getCornerOccupancy(gameState);
		int[][] nearCorners = getNearCorners(gameState);
		double cornerControl = 0.0, cornerDanger = 0.0;
		for (int i = 0; i < 4; i++) {
			if (cornerOccupancy[i] == OthelloGame.WHITE) whiteCorners++;
			else if (cornerOccupancy[i] == OthelloGame.BLACK) blackCorners++;
			for (int j = 0; j < 3; j++) {
				if (nearCorners[i][j] == OthelloGame.WHITE) whiteCornerDanger++;
				else if (nearCorners[i][j] == OthelloGame.BLACK) blackCornerDanger++;
			}
		}
		if (currentPlayer == OthelloGame.WHITE) {
			if (whiteCorners + blackCorners == 0) cornerControl = 0;
			else cornerControl = ((double) whiteCorners) / (whiteCorners + blackCorners);
			if (whiteCornerDanger + blackCornerDanger == 0) cornerDanger = 0;
			else cornerDanger = ((double) whiteCornerDanger) / (whiteCornerDanger + blackCornerDanger);
		} else if (currentPlayer == OthelloGame.BLACK) {
			if (whiteCorners + blackCorners == 0) cornerControl = 0;
			else cornerControl = ((double) blackCorners) / (blackCorners + whiteCorners);
			if (whiteCornerDanger + blackCornerDanger == 0) cornerDanger = 0;
			else cornerDanger = ((double) blackCornerDanger) / (blackCornerDanger + whiteCornerDanger);
		}
		return (0.95 * (cornerControl - cornerDanger) + 0.25 * getMobility(gameState, currentPlayer) +
				0.25 * getDiskParity(gameState, currentPlayer));
	}

	public static double getDiskParity(int[][] gameState, int currentPlayer) {
		int whiteScore = OthelloGame.getWhiteScore(gameState);
		int blackScore = OthelloGame.getBlackScore(gameState);
		if (currentPlayer == OthelloGame.WHITE) {
			return (double) (whiteScore - blackScore) / (whiteScore + blackScore);
		} else {
			return (double) (blackScore - whiteScore) / (whiteScore + blackScore);
		}
	}

	public static double getMobility(int[][] gameState, int currentPlayer) {
		int validMovesBlack = 0;
		int validMovesWhite = 0;
		for (int i = 0; i < gameState.length; i++) {
			for (int j = 0; j < gameState.length; j++) {
				if (OthelloGame.isValidMove(i, j, gameState, gameState.length, OthelloGame.BLACK))
					validMovesBlack++;
				if (OthelloGame.isValidMove(i, j, gameState, gameState.length, OthelloGame.WHITE))
					validMovesWhite++;
			}
		}
		if (currentPlayer == OthelloGame.WHITE) {
			return (double) (validMovesWhite - validMovesBlack) / (validMovesWhite + validMovesBlack);
		} else {
			return (double) (validMovesBlack - validMovesWhite) / (validMovesWhite + validMovesBlack);
		}
	}

	public static int[] getCornerOccupancy(int[][] gameState) {
		//index 0=[0][0], 1=[0][boardSize-1], 2=[boardSize-1][0], 3=[boardSize-1][boardSize-1]
		int[] corners = {0, 0, 0, 0};
		if (gameState[0][0] == OthelloGame.BLACK) corners[0] = OthelloGame.BLACK;
		else if (gameState[0][0] == OthelloGame.WHITE) corners[0] = OthelloGame.WHITE;
		if (gameState[0][gameState.length - 1] == OthelloGame.BLACK) corners[1] = OthelloGame.BLACK;
		else if (gameState[0][gameState.length - 1] == OthelloGame.WHITE) corners[1] = OthelloGame.WHITE;
		if (gameState[gameState.length - 1][0] == OthelloGame.BLACK) corners[2] = OthelloGame.BLACK;
		else if (gameState[gameState.length - 1][0] == OthelloGame.WHITE) corners[2] = OthelloGame.WHITE;
		if (gameState[gameState.length - 1][gameState.length - 1] == OthelloGame.BLACK) corners[3] = OthelloGame.BLACK;
		else if (gameState[gameState.length - 1][gameState.length - 1] == OthelloGame.WHITE) corners[3] = OthelloGame.WHITE;
		return corners;
	}

	public static int[][] getNearCorners(int[][] gameState) {
		//first subarray corresponds to top left, second to top right, third to bottom left, last to bottom right
		int[][] nearCorners = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
		if (gameState[0][0] == 0) {
			if (gameState[0][1] == OthelloGame.WHITE) nearCorners[0][0] = OthelloGame.WHITE;
			else if (gameState[0][1] == OthelloGame.BLACK) nearCorners[0][0] = OthelloGame.BLACK;
			if (gameState[1][1] == OthelloGame.WHITE) nearCorners[0][1] = OthelloGame.WHITE;
			else if (gameState[1][1] == OthelloGame.BLACK) nearCorners[0][1] = OthelloGame.BLACK;
			if (gameState[1][0] == OthelloGame.WHITE) nearCorners[0][2] = OthelloGame.WHITE;
			else if (gameState[1][0] == OthelloGame.BLACK) nearCorners[0][2] = OthelloGame.BLACK;
		}
		if (gameState[0][gameState.length - 1] == 0) {
			if (gameState[0][gameState.length - 2] == OthelloGame.WHITE) nearCorners[1][0] = OthelloGame.WHITE;
			else if (gameState[0][gameState.length - 2] == OthelloGame.BLACK) nearCorners[1][0] = OthelloGame.BLACK;
			if (gameState[1][gameState.length - 2] == OthelloGame.WHITE) nearCorners[1][1] = OthelloGame.WHITE;
			else if (gameState[1][gameState.length - 2] == OthelloGame.BLACK) nearCorners[1][1] = OthelloGame.BLACK;
			if (gameState[1][gameState.length - 1] == OthelloGame.WHITE) nearCorners[1][2] = OthelloGame.WHITE;
			else if (gameState[1][gameState.length - 1] == OthelloGame.BLACK) nearCorners[1][2] = OthelloGame.BLACK;
		}
		if (gameState[gameState.length - 1][0] == 0) {
			if (gameState[gameState.length - 1][1] == OthelloGame.WHITE) nearCorners[2][0] = OthelloGame.WHITE;
			else if (gameState[gameState.length - 1][1] == OthelloGame.BLACK) nearCorners[2][0] = OthelloGame.BLACK;
			if (gameState[gameState.length - 2][1] == OthelloGame.WHITE) nearCorners[2][1] = OthelloGame.WHITE;
			else if (gameState[gameState.length - 2][1] == OthelloGame.BLACK) nearCorners[2][1] = OthelloGame.BLACK;
			if (gameState[gameState.length - 2][0] == OthelloGame.WHITE) nearCorners[2][2] = OthelloGame.WHITE;
			else if (gameState[gameState.length - 2][0] == OthelloGame.BLACK) nearCorners[2][2] = OthelloGame.BLACK;
		}
		if (gameState[gameState.length - 1][gameState.length - 1] == 0) {
			if (gameState[gameState.length - 2][gameState.length - 1] == OthelloGame.WHITE) nearCorners[3][0] = OthelloGame.WHITE;
			else if (gameState[gameState.length - 2][gameState.length - 1] == OthelloGame.BLACK) nearCorners[3][0] = OthelloGame.BLACK;
			if (gameState[gameState.length - 2][gameState.length - 2] == OthelloGame.WHITE) nearCorners[3][1] = OthelloGame.WHITE;
			else if (gameState[gameState.length - 2][gameState.length - 2] == OthelloGame.BLACK) nearCorners[3][1] = OthelloGame.BLACK;
			if (gameState[gameState.length - 1][gameState.length - 2] == OthelloGame.WHITE) nearCorners[3][2] = OthelloGame.WHITE;
			else if (gameState[gameState.length - 1][gameState.length - 2] == OthelloGame.BLACK) nearCorners[3][2] = OthelloGame.BLACK;
		}
		return nearCorners;
	}

	public static double getEvaluation_6x6(int[][] gameState, int currentPlayer) {
		int opponent;
		if(currentPlayer == OthelloGame.BLACK) {
			opponent = OthelloGame.WHITE;
		}else {
			opponent = OthelloGame.BLACK;
		}

		//set variables to 0, mobility is # of avail moves
		//posScore helps favor corners & edges
		int currentMobility = 0, opponentMobility = 0 , currentPositionalScore = 0,
				opponentPositionalScore = 0;

		//check for valid moves.
		for(int i = 0; i < gameState.length; i++) {
			for(int j = 0; j < gameState.length; j++) {
				//mobility increases for each valid move
				if(OthelloGame.isValidMove(i, j, gameState, gameState.length, currentPlayer)) {
					currentMobility++;
				}
				if(OthelloGame.isValidMove(i, j, gameState, gameState.length, opponent)) {
					opponentMobility++;
				}
				//add to positional score based on values in positionalWeights_6x6
				if(gameState[i][j] == currentPlayer) {
					currentPositionalScore += positionalWeights_6x6[i][j];
				}else if(gameState[i][j] == opponent) {
					opponentPositionalScore += positionalWeights_6x6[i][j];
				}
			}
		}

		double mobilityScore = 0;
		if(currentMobility + opponentMobility != 0) { //don't want to divide by zero!
			//divide mobility advantage by total mobility advantage
			//multiply  by 100 to find percent better score (idk if needed??)
			mobilityScore = 100.0 * (currentMobility - opponentMobility) / (currentMobility + opponentMobility);
		}
		//calculate positional advantage
		double positionalScore = currentPositionalScore - opponentPositionalScore;
		//return weighted score
		return positionalWeight*positionalScore + mobilityWeight*mobilityScore;
	}

	//same as 6x6 but 8x8 variables
	public static double getEvaluation_8x8(int[][] gameState, int currentPlayer) {
		int opponent;
		if(currentPlayer == OthelloGame.BLACK) {
			opponent = OthelloGame.WHITE;
		}else {
			opponent = OthelloGame.BLACK;
		}

		int currentMobility = 0, opponentMobility = 0 , currentPositionalScore = 0,
				opponentPositionalScore = 0;

		for(int i = 0; i < gameState.length; i++) {
			for(int j = 0; j < gameState.length; j++) {
				if(OthelloGame.isValidMove(i, j, gameState, gameState.length, currentPlayer)) {
					currentMobility++;
				}
				if(OthelloGame.isValidMove(i, j, gameState, gameState.length, opponent)) {
					opponentMobility++;
				}
				if(gameState[i][j] == currentPlayer) {
					currentPositionalScore += positionalWeights_8x8[i][j];
				}else if(gameState[i][j] == opponent) {
					opponentPositionalScore += positionalWeights_8x8[i][j];
				}
			}
		}

		double mobilityScore = 0;
		if(currentMobility + opponentMobility != 0) {
			mobilityScore = 100.0 * (currentMobility - opponentMobility) / (currentMobility + opponentMobility);
		}
		double positionalScore = currentPositionalScore - opponentPositionalScore;
		return positionalWeight*positionalScore + mobilityWeight*mobilityScore;
	}

	//Returns list of <Move, State> pairs specifying legal moves
	public static Map<List<Integer>, OthelloGame> getSuccessors(OthelloGame currentGame) {
		Map<List<Integer>, OthelloGame> successors = new HashMap<>();
		for (int i = 0; i < currentGame.boardSize; i++) {
			for (int j = 0; j < currentGame.boardSize; j++) {
				if (OthelloGame.isValidMove(i, j, currentGame.gameState, currentGame.boardSize, currentGame.currentPlayer)) {
					//get move
					List<Integer> move = new ArrayList<>();
					move.add(i);
					move.add(j);

					//get corresponding game state
					OthelloGame nextTurn = currentGame.clone();
					OthelloGame.makeMove(i, j, nextTurn.gameState, nextTurn.currentPlayer);

					successors.put(move, nextTurn);
				}
			}
		}
        return successors;
	}
}
