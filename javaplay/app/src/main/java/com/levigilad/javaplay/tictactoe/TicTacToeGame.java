package com.levigilad.javaplay.tictactoe;

import android.content.Context;

import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.entities.Game;

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

    public static boolean isWin(Board board, TicTacToeSymbol symbolForWin) {
        return isHorizontalWin(board, symbolForWin) ||
                isVerticalWin(board, symbolForWin) ||
                isDiagonalWin(board, symbolForWin) ||
                isSecondaryDiagonalWin(board, symbolForWin);
    }

    private static boolean isDiagonalWin(Board board, TicTacToeSymbol symbol) {
        for (int i = 0; i < board.ROWS; i++) {
            if (board.getCell(i, i) != symbol) {
                return false;
            }
        }

        return true;
    }

    private static boolean isSecondaryDiagonalWin(Board board, TicTacToeSymbol symbol) {
        for (int i = 0; i < board.ROWS; i++) {
            if (board.getCell(i, board.ROWS - 1 - i) != symbol) {
                return false;
            }
        }

        return true;
    }

    private static boolean isHorizontalWin(Board board, TicTacToeSymbol symbol) {
        for (int i = 0; i < Board.ROWS; i++) {
            boolean isWin = true;

            for (int j = 0; j < Board.COLUMNS; j++) {
                if (board.getCell(i, j) != symbol) {
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

    private static boolean isVerticalWin(Board board, TicTacToeSymbol symbol) {
        for (int i = 0; i < Board.COLUMNS; i++) {
            boolean isWin = true;

            for (int j = 0; j < Board.ROWS; j++) {
                if (board.getCell(j, i) != symbol) {
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

    public static boolean isTie(Board board) {
        return !isWin(board, TicTacToeSymbol.X) && !isWin(board, TicTacToeSymbol.O) && isFull(board);
    }

    private static boolean isFull(Board board) {
        for (int i = 0; i < Board.COLUMNS; i++) {
            for (int j = 0; j < Board.ROWS; j++) {
                if (board.getCell(j, i) == TicTacToeSymbol.NONE) {
                    return false;
                }
            }
        }

        return true;
    }
}
