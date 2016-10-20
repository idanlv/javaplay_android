package com.levigilad.javaplay.tictactoe;

import com.levigilad.javaplay.infra.interfaces.IJsonSerializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Board implements IJsonSerializable {
    public static final int COLUMNS = 3;
    public static final int ROWS = 3;
    private static final String BOARD = "Board";

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

    @Override
    public void fromJson(JSONObject object) throws JSONException {
        JSONArray rowsArray = object.getJSONArray(BOARD);

        for (int i = 0; i < ROWS; i++) {
            JSONArray columnsArray = rowsArray.getJSONArray(i);

            for (int j = 0; j < COLUMNS; j++) {
                mBoard[i][j] = TicTacToeSymbol.valueOf(columnsArray.getString(j));
            }
        }
    }
}
