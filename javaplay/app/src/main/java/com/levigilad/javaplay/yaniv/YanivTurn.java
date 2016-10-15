package com.levigilad.javaplay.yaniv;

import com.levigilad.javaplay.infra.entities.Turn;
import com.levigilad.javaplay.infra.entities.DeckOfCards;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * This class represents Yaniv game turn data
 */
public class YanivTurn extends Turn {
    // Consts
    public static final String INITIALIZE_DONE = "initializeDone";
    public static final String AVAILABLE_DECK = "availableDeck";
    public static final String DISCARDED_DECK = "discardedDeck";
    public static final String AVAILABLE_CARDS = "availableCards";

    // Members
    private DeckOfCards mAvailableDeck;
    private DeckOfCards mDiscardedDeck;
    private LinkedList<Integer> mAvailableDiscardedCards;
    private boolean mInitializeDone;

    public YanivTurn() {
        super();

        mAvailableDeck = new DeckOfCards();
        mDiscardedDeck = new DeckOfCards();
        mAvailableDiscardedCards = new LinkedList<>();
        mInitializeDone = false;
    }

    public void setAvailableDeck(DeckOfCards deck) {
        mAvailableDeck = new DeckOfCards(deck);
    }

    /**
     * Retrieve Json representation of object
     * @return Json
     * @throws JSONException
     */
    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject gameData = super.toJson();

        gameData.put(INITIALIZE_DONE, this.mInitializeDone);
        gameData.put(AVAILABLE_DECK, this.mAvailableDeck.toJson());
        gameData.put(DISCARDED_DECK, this.mDiscardedDeck.toJson());

        JSONArray availableArray = new JSONArray();

        for (Integer location : this.mAvailableDiscardedCards) {
            availableArray.put(availableArray);
        }

        gameData.put(AVAILABLE_CARDS, availableArray);

        return gameData;
    }

    /**
     * Update data according to Json value
     * @param object turn data
     * @throws JSONException
     */
    @Override
    public void fromJson(JSONObject object) throws JSONException {
        this.mInitializeDone = object.getBoolean(INITIALIZE_DONE);
        this.mAvailableDeck = new DeckOfCards();
        this.mAvailableDeck.fromJson(object.getJSONObject(AVAILABLE_DECK));
        this.mDiscardedDeck = new DeckOfCards();
        this.mDiscardedDeck.fromJson(object.getJSONObject(DISCARDED_DECK));

        this.mAvailableDiscardedCards = new LinkedList<>();
        JSONArray availableArray = object.getJSONArray(AVAILABLE_CARDS);

        for (int i = 0; i < availableArray.length(); i++) {
            this.mAvailableDiscardedCards.add(availableArray.getInt(i));
        }

        super.fromJson(object);
    }
}
