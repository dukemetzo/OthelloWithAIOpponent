package com.example.scenecontrollers;

public class GameAI {

	//TODO:implementation of minimax w/ AB pruning algorithm
	public void minimax() {

	}

	//TODO: method that finds the best move for AI opponent
	public int[] findBestMove(int[][] board, int currentPlayer) {
		int[] bestMove = {-1,-1};
		return bestMove;
	}

	//method to check if game is over
	public boolean gameOver(int[][] board) {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(board[i][j] == 0) { //tells us if a spot on the board is empty
					return false;		//means the game is not over yet
				}
			}
		}
		return true; //game is over if all spots are full
	}
	
	//TODO: method that finds possible moves for AI opponent
	
	//TODO: method to find the score of the game
}
