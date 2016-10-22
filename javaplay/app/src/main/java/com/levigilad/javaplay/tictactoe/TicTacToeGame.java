package com.levigilad.javaplay.tictactoe;

import android.content.Context;

import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.entities.Game;

/**
 * This class represents a Tic Tac Toe game
 */
public class TicTacToeGame extends Game {
    /**
     * Constructor
     */
    public TicTacToeGame(Context context) {
        super(context.getString(R.string.tictactoe_game_id),
                context.getString(R.string.tictactoe_description),
                context.getString(R.string.tictactoe_leaderboard_id),
                2);
    }

    /**
     * Checks if given symbol has won the game
     * @param board current board
     * @param symbol player's symbol
     * @return True if the player has won the game, otherwise false
     */
    public static boolean isWin(Board board, TicTacToeSymbol symbol) {
        return isHorizontalWin(board, symbol) ||
                isVerticalWin(board, symbol) ||
                isDiagonalWin(board, symbol) ||
                isSecondaryDiagonalWin(board, symbol);
    }

    /**
     * Checks if the symbol has won the game using the diagonal of the board
     * @param board current board
     * @param symbol player's symbol
     * @return True if the player has won the game, otherwise false
     */
    private static boolean isDiagonalWin(Board board, TicTacToeSymbol symbol) {
        for (int i = 0; i < board.ROWS; i++) {
            if (board.getPlayerOnBoard(i, i) != symbol) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the user has won the game using the secondary diagonal of the board
     * @param board current board
     * @param symbol player's symbol
     * @return True if the player has won the game, otherwise false
     */
    private static boolean isSecondaryDiagonalWin(Board board, TicTacToeSymbol symbol) {
        for (int i = 0; i < board.ROWS; i++) {
            if (board.getPlayerOnBoard(i, board.ROWS - 1 - i) != symbol) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the user has won the game using a row
     * @param board current board
     * @param symbol player's symbol
     * @return True if the player has won the game, otherwise false
     */
    private static boolean isHorizontalWin(Board board, TicTacToeSymbol symbol) {
        for (int i = 0; i < Board.ROWS; i++) {
            boolean isWin = true;

            for (int j = 0; j < Board.COLUMNS; j++) {
                if (board.getPlayerOnBoard(i, j) != symbol) {
                    isWin = false;
                    break;
                }
            }

            if (isWin) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the user won the game using a column
     * @param board current board
     * @param symbol player's symbol
     * @return True if the player has won the game, otherwise false
     */
    private static boolean isVerticalWin(Board board, TicTacToeSymbol symbol) {
        for (int i = 0; i < Board.COLUMNS; i++) {
            boolean isWin = true;

            for (int j = 0; j < Board.ROWS; j++) {
                if (board.getPlayerOnBoard(j, i) != symbol) {
                    isWin = false;
                    break;
                }
            }

            if (isWin) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if we've reached a tie
     * @param board current board
     * @return True if the game is tied, otherwise false
     */
    public static boolean isTie(Board board) {
        return !isWin(board, TicTacToeSymbol.X) && !isWin(board, TicTacToeSymbol.O) && isFull(board);
    }

    /**
     * Checks if there is an empty cell in board
     * @param board current board
     * @return True if there is an empty cell, otherwise false
     */
    private static boolean isFull(Board board) {
        for (int i = 0; i < Board.COLUMNS; i++) {
            for (int j = 0; j < Board.ROWS; j++) {
                if (board.getPlayerOnBoard(j, i) == TicTacToeSymbol.NONE) {
                    return false;
                }
            }
        }

        return true;
    }
}
