package com.levigilad.javaplay.tictactoe;

import com.levigilad.javaplay.infra.interfaces.IJsonSerializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class represents a tic tac toe board game
 */
public class Board implements IJsonSerializable {
    /**
     * Constants
     */
    public static final int COLUMNS = 3;
    public static final int ROWS = 3;
    private static final String BOARD = "Board";

    /**
     * Members
     */
    private TicTacToeSymbol[][] mBoard;

    /**
     * Constructor: Creates an empty board
     */
    public Board() {
        mBoard = new TicTacToeSymbol[ROWS][COLUMNS];

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                mBoard[i][j] = TicTacToeSymbol.NONE;
            }
        }
    }

    /**
     * Places the player's symbol on the board
     * @param symbol symbol of the player
     * @param row row in board
     * @param column column in board
     */
    public void placePlayerOnBoard(TicTacToeSymbol symbol, int row, int column) {
        mBoard[row][column] = symbol;
    }

    /**
     * Returns the player on a specific board cell
     * @param row row in board
     * @param column column in board
     * @return The symbol located in given position
     */
    public TicTacToeSymbol getPlayerOnBoard(int row, int column) {
        return mBoard[row][column];
    }

    /**
     * Generates a json representation of the board
     * @return Json representation of the board
     * @throws JSONException If the json wasn't created correctly
     */
    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject board = new JSONObject();
        JSONArray rowsArray = new JSONArray();

        for (int i = 0; i < ROWS; i++) {
            JSONArray columnsArray = new JSONArray();

            for (int j = 0; j < COLUMNS; j++) {
                columnsArray.put(j, mBoard[i][j].name());
            }

            rowsArray.put(i, columnsArray);
        }

        board.put(BOARD, rowsArray);
        return board;
    }

    /**
     * Updates board according to json
     * @param jsonObject json representation of the board
     * @throws JSONException If the json wasn't read correctly
     */
    @Override
    public void fromJson(JSONObject jsonObject) throws JSONException {
        JSONArray rowsArray = jsonObject.getJSONArray(BOARD);

        for (int i = 0; i < ROWS; i++) {
            JSONArray columnsArray = rowsArray.getJSONArray(i);

            for (int j = 0; j < COLUMNS; j++) {
                mBoard[i][j] = TicTacToeSymbol.valueOf(columnsArray.getString(j));
            }
        }
    }
}
