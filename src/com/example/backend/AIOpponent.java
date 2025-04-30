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
			return alphaBetaSearch(currentGame);
		} else {
			return heuresticSearch(currentGame);
		}
	}

	public static List<Integer> alphaBetaSearch(OthelloGame currentGame) {
		OthelloGame cloneGame = currentGame.clone();
		//int nextPlayer = (currentGame.currentPlayer == OthelloGame.BLACK) ? OthelloGame.WHITE : OthelloGame.BLACK;
		return maxValue(cloneGame.gameState, cloneGame.currentPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE).move;
	}

	public static ValueMovePair maxValue(int[][] gameState, int currentPlayer, int alpha, int beta) {
		if (OthelloGame.evaluateGameState(gameState, currentPlayer))
			return new ValueMovePair(calculateUtility(gameState, currentPlayer), null);
		ValueMovePair pair = new ValueMovePair(Integer.MIN_VALUE, null);
		for (Map.Entry<List<Integer>, List<List<Integer>>> successor : getSuccessors(gameState, currentPlayer).entrySet()) {
			int[][] successorGameState = new int[gameState.length][gameState.length];
			for (int i = 0; i < gameState.length; i++) {
				for (int j = 0; j < gameState.length; j++) {
					successorGameState[i][j] = successor.getValue().get(i).get(j);
				}
			}
			int nextPlayer = (currentPlayer == OthelloGame.BLACK) ? OthelloGame.WHITE : OthelloGame.BLACK;
			ValueMovePair minPair = minValue(successorGameState, nextPlayer, alpha, beta);
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

	public static ValueMovePair minValue(int[][] gameState, int currentPlayer, int alpha, int beta) {
		if (OthelloGame.evaluateGameState(gameState, currentPlayer))
			return new ValueMovePair(calculateUtility(gameState, currentPlayer), null);
		ValueMovePair pair = new ValueMovePair(Integer.MAX_VALUE, null);
		for (Map.Entry<List<Integer>, List<List<Integer>>> successor : getSuccessors(gameState, currentPlayer).entrySet()) {
			int[][] successorGameState = new int[gameState.length][gameState.length];
			for (int i = 0; i < gameState.length; i++) {
				for (int j = 0; j < gameState.length; j++) {
					successorGameState[i][j] = successor.getValue().get(i).get(j);
				}
			}
			int nextPlayer = (currentPlayer == OthelloGame.BLACK) ? OthelloGame.WHITE : OthelloGame.BLACK;
			ValueMovePair maxPair = maxValue(successorGameState, nextPlayer, alpha, beta);
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

	public static List<Integer> heuresticSearch(OthelloGame currentGame) {
		return new ArrayList<Integer>();
	}

	//Returns list of <Move, State> pairs specifying legal moves
	public static Map<List<Integer>, List<List<Integer>>> getSuccessors(int[][] gameState, int currentPlayer) {
		Map<List<Integer>, List<List<Integer>>> successors = new HashMap<>();
		for (int i = 0; i < gameState.length; i++) {
			for (int j = 0; j < gameState.length; j++) {
				if (OthelloGame.isValidMove(i, j, gameState, gameState.length, currentPlayer)) {
					//get move
					List<Integer> move = new ArrayList<>();
					move.add(i);
					move.add(j);

					//get corresponding game state
					List<List<Integer>> newGameState = new ArrayList<>();
					for (int[] row : OthelloGame.makeMove(i, j, gameState, currentPlayer)) {
						List<Integer> rowList = new ArrayList<>();
						for (int element : row) {
							rowList.add(element);
						}
						newGameState.add(rowList);
					}

					successors.put(move, newGameState);
				}
			}
		}
		return successors;
	}
}
