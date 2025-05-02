package com.example.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AIOpponent {

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
			/*int[][] successorGameState = new int[currentGame.boardSize][currentGame.boardSize];
			for (int i = 0; i < currentGame.boardSize; i++) {
				for (int j = 0; j < currentGame.boardSize; j++) {
					successorGameState[i][j] = successor.getValue().get(i).get(j);
				}
			}*/
			ValueMovePair minPair = minValue(successor.getValue(), alpha, beta);
			if(minPair.v > pair.v) {
				pair = new ValueMovePair(minPair.v, successor.getKey());
			}
			//pair = (pair.v >= minPair.v) ? pair.setMove(successor.getKey()) : minPair;
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
			/*int[][] successorGameState = new int[gameState.length][gameState.length];
			for (int i = 0; i < gameState.length; i++) {
				for (int j = 0; j < gameState.length; j++) {
					successorGameState[i][j] = successor.getValue().get(i).get(j);
				}
			}*/
			ValueMovePair maxPair = maxValue(successor.getValue(), alpha, beta);
			if(maxPair.v < pair.v) {
				pair = new ValueMovePair(maxPair.v, successor.getKey());
			}
			
			//pair = (pair.v <= maxPair.v) ? pair.setMove(successor.getKey()) : maxPair;
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
			/*int[][] successorGameState = new int[currentGame.boardSize][currentGame.boardSize];
			for (int i = 0; i < currentGame.boardSize; i++) {
				for (int j = 0; j < currentGame.boardSize; j++) {
					successorGameState[i][j] = successor.getValue().gameState[i][j];
				}
			}*/
			double currentEvaluation = getEvaluation(successor.getValue().gameState, currentGame.currentPlayer);
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
		double diskParity = getDiskParity(gameState, currentPlayer);
		double mobility = getMobility(gameState, currentPlayer);
		double cornerOccupancy = getCornerOccupancy(gameState, currentPlayer);
		double cornerCloseness = getCornerCloseness(gameState, currentPlayer);
		return ((25 * diskParity) + (5 * mobility) + (30 * cornerOccupancy) - (25 * cornerCloseness)) / 35;
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

	public static double getCornerOccupancy(int[][] gameState, int currentPlayer) {
		int whiteCorners = 0;
		int blackCorners = 0;
		if (gameState[0][0] == OthelloGame.WHITE) whiteCorners++;
		else if (gameState[0][0] == OthelloGame.BLACK) blackCorners++;
		if (gameState[0][gameState.length - 1] == OthelloGame.WHITE) whiteCorners++;
		else if (gameState[0][gameState.length - 1] == OthelloGame.BLACK) blackCorners++;
		if (gameState[gameState.length - 1][0] == OthelloGame.WHITE) whiteCorners++;
		else if (gameState[gameState.length - 1][0] == OthelloGame.BLACK) blackCorners++;
		if (gameState[gameState.length - 1][gameState.length - 1] == OthelloGame.WHITE) whiteCorners++;
		else if (gameState[gameState.length - 1][gameState.length - 1] == OthelloGame.BLACK) blackCorners++;
		if (currentPlayer == OthelloGame.WHITE) {
			return (double) whiteCorners / 4;
		} else
			return (double) blackCorners / 4;
	}

	public static double getCornerCloseness(int[][] gameState, int currentPlayer) {
		int whiteNearCorners = 0;
		int blackNearCorners = 0;
		if (gameState[0][0] == 0) {
			if (gameState[0][1] == OthelloGame.WHITE) whiteNearCorners++;
			else if (gameState[0][1] == OthelloGame.BLACK) blackNearCorners++;
			if (gameState[1][1] == OthelloGame.WHITE) whiteNearCorners++;
			else if (gameState[1][1] == OthelloGame.BLACK) blackNearCorners++;
			if (gameState[1][0] == OthelloGame.WHITE) whiteNearCorners++;
			else if (gameState[1][0] == OthelloGame.BLACK) blackNearCorners++;
		}
		if (gameState[0][gameState.length - 1] == 0) {
			if (gameState[0][gameState.length - 2] == OthelloGame.WHITE) whiteNearCorners++;
			else if (gameState[0][gameState.length - 2] == OthelloGame.BLACK) blackNearCorners++;
			if (gameState[1][gameState.length - 2] == OthelloGame.WHITE) whiteNearCorners++;
			else if (gameState[1][gameState.length - 2] == OthelloGame.BLACK) blackNearCorners++;
			if (gameState[1][gameState.length - 1] == OthelloGame.WHITE) whiteNearCorners++;
			else if (gameState[1][gameState.length - 1] == OthelloGame.BLACK) blackNearCorners++;
		}
		if (gameState[gameState.length - 1][0] == 0) {
			if (gameState[gameState.length - 1][1] == OthelloGame.WHITE) whiteNearCorners++;
			else if (gameState[gameState.length - 1][1] == OthelloGame.BLACK) blackNearCorners++;
			if (gameState[gameState.length - 2][1] == OthelloGame.WHITE) whiteNearCorners++;
			else if (gameState[gameState.length - 2][1] == OthelloGame.BLACK) blackNearCorners++;
			if (gameState[gameState.length - 2][0] == OthelloGame.WHITE) whiteNearCorners++;
			else if (gameState[gameState.length - 2][0] == OthelloGame.BLACK) blackNearCorners++;
		}
		if (gameState[gameState.length - 1][gameState.length - 1] == 0) {
			if (gameState[gameState.length - 2][gameState.length - 1] == OthelloGame.WHITE) whiteNearCorners++;
			else if (gameState[gameState.length - 2][gameState.length - 1] == OthelloGame.BLACK) blackNearCorners++;
			if (gameState[gameState.length - 2][gameState.length - 2] == OthelloGame.WHITE) whiteNearCorners++;
			else if (gameState[gameState.length - 2][gameState.length - 2] == OthelloGame.BLACK) blackNearCorners++;
			if (gameState[gameState.length - 1][gameState.length - 2] == OthelloGame.WHITE) whiteNearCorners++;
			else if (gameState[gameState.length - 1][gameState.length - 2] == OthelloGame.BLACK) blackNearCorners++;
		}
		if (currentPlayer == OthelloGame.WHITE) {
			return (double) (whiteNearCorners - blackNearCorners) / 12;
		} else {
			return (double) (blackNearCorners - whiteNearCorners) / 12;
		}
	}

	/*public static double getStability(int[][] gameState, int currentPlayer) {
		int whiteStableTokens = 0, whiteSemiStableTokens = 0, whiteUnstableTokens = 0;
		int blackStableTokens = 0, blackSemiStableTokens = 0, blackUnstableTokens = 0;
		for (int i = 0; i < gameState.length; i++) {
			if (i == 0 || i == gameState.length - 1) {
				if (gameState[i][0] == OthelloGame.WHITE) {
					whiteStableTokens++;
					if
				} else if (gameState[i][gameState.length - 1] == OthelloGame.WHITE) {
					whiteStableTokens++;
				}

			}
			for (int j = 0; j < gameState.length; j++) {
				//count stable tokens
				if (((i == 0 && j == 0) || (i == 0 && j == gameState.length - 1) ||
						(i == gameState.length - 1 && j == gameState.length - 1) ||
						(i == gameState.length - 1 && j == 0)) && gameState[i][j] != 0) {
					if (gameState[i][j] == OthelloGame.WHITE) whiteStableTokens++;
					else blackStableTokens++;
				} else if ()
			}
		}
		return 0;
	}*/

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
					System.out.println("Possible AI move: " + i + ", " + j);

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
