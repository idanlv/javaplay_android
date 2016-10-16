package com.levigilad.javaplay.tictactoe;

/**
 * Created by User on 15/10/2016.
 */

public class Board {
    public static final int COLUMNS = 3;
    public static final int ROWS = 3;

    private TicTacToeSymbol[][] mBoard;

    public Board() {
        mBoard = new TicTacToeSymbol[ROWS][COLUMNS];

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                mBoard[i][j] = TicTacToeSymbol.NONE;
            }
        }
    }

    public void setCell(TicTacToeSymbol player, int row, int column) {
        mBoard[row][column] = player;
    }

    public TicTacToeSymbol getCell(int row, int column) {
        return mBoard[row][column];
    }
}
