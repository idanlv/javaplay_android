package com.levigilad.javaplay.tictactoe;

import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.Turn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;

public class TicTacToeTurn extends Turn {
    private static final String BOARD = "Board";
    private static final String PARTICIPANTS = "Participants";

    private Board mBoard;
    private HashMap<String, TicTacToeSymbol> mParticipants;

    public TicTacToeTurn() {
        super("Tic Tac Toe");

        mBoard = new Board();
        mParticipants = new HashMap<>();
    }

    public void setBoard(Board updatedBoard) {
        mBoard = updatedBoard;
    }

    public Board getBoard() {
        return this.mBoard;
    }

    public void addParticipant(String participantId, TicTacToeSymbol symbol) {
        mParticipants.put(participantId, symbol);
    }

    /**
     * Retrieve Json representation of object
     * @return Json
     * @throws JSONException
     */
    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject gameData = super.toJson();

        gameData.put(PARTICIPANTS, mParticipants);
        gameData.put(BOARD, mBoard);

        return gameData;
    }

    /**
     * Update data according to Json value
     * @param object turn data
     * @throws JSONException
     */
    @Override
    public void fromJson(JSONObject object) throws JSONException {
        //this.mParticipants = object.getJSONObject(PARTICIPANTS);
        //this.mBoard = object.getJSONObject(BOARD);

        super.fromJson(object);
    }
}
