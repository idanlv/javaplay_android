package com.levigilad.javaplay.tictactoe;

import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.Turn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class TicTacToeTurn extends Turn {
    private static final String BOARD = "Board";
    private static final String PARTICIPANTS = "Participants";
    private static final String NO_PARTICIPANT = "";

    private Board mBoard;
    private HashMap<TicTacToeSymbol, String> mParticipants;

    public TicTacToeTurn() {
        super("Tic Tac Toe");

        mBoard = new Board();
        mParticipants = new HashMap<>();
        mParticipants.put(TicTacToeSymbol.X, NO_PARTICIPANT);
        mParticipants.put(TicTacToeSymbol.O, NO_PARTICIPANT);
    }

    public void setBoard(Board updatedBoard) {
        mBoard = updatedBoard;
    }

    public Board getBoard() {
        return this.mBoard;
    }

    public TicTacToeSymbol addParticipant(String participantId) {
        TicTacToeSymbol participantSymbol = TicTacToeSymbol.NONE;

        if (mParticipants.get(TicTacToeSymbol.X).equals(NO_PARTICIPANT)) {
            participantSymbol = TicTacToeSymbol.X;
        } else if (mParticipants.get(TicTacToeSymbol.O).equals(NO_PARTICIPANT)) {
            participantSymbol = TicTacToeSymbol.O;
        }

        mParticipants.put(participantSymbol, participantId);

        return participantSymbol;
    }

    public TicTacToeSymbol getParticipantSymbol(String participantId) {
        for (TicTacToeSymbol symbol : mParticipants.keySet()) {
            if (mParticipants.get(symbol).equals(participantId)) {
                return symbol;
            }
        }

        return TicTacToeSymbol.NONE;
    }

    /**
     * Retrieve Json representation of object
     * @return Json
     * @throws JSONException
     */
    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject gameData = super.toJson();

        JSONObject participants = new JSONObject();
        for (TicTacToeSymbol symbol : mParticipants.keySet()) {
            participants.put(symbol.name(), mParticipants.get(symbol));
        }

        gameData.put(PARTICIPANTS, participants);
        gameData.put(BOARD, mBoard.toJson());

        return gameData;
    }

    /**
     * Update data according to Json value
     * @param object turn data
     * @throws JSONException
     */
    @Override
    public void fromJson(JSONObject object) throws JSONException {
        JSONObject participants = object.getJSONObject(PARTICIPANTS);

        mParticipants = new HashMap<>();

        Iterator<String> iterator = participants.keys();

        while (iterator.hasNext()){
            TicTacToeSymbol symbol = TicTacToeSymbol.valueOf(iterator.next());
            mParticipants.put(symbol, participants.getString(symbol.name()));
        }

        this.mBoard.fromJson(object.getJSONObject(BOARD));

        super.fromJson(object);
    }
}
