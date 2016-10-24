package com.levigilad.javaplay.yaniv;

import com.levigilad.javaplay.infra.entities.Turn;
import com.levigilad.javaplay.infra.entities.DeckOfCards;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class represents Yaniv game turn data
 */
public class YanivTurn extends Turn {
    /**
     * Constants
     */
    public static final String GAME_NAME = "Yaniv";
    public static final String AVAILABLE_DISCARDED_CARDS = "mAvailableDiscardedCards";
    public static final String DISCARDED_CARDS = "mDiscardedCards";
    public static final String GLOBAL_CARD_DECK = "mGlobalCardDeck";
    public static final String PLAYERS_HANDS = "mPlayersHands";

    /**
     * Members
     */
    private DeckOfCards mAvailableDiscardedCards;
    private DeckOfCards mDiscardedCards;
    private DeckOfCards mGlobalCardDeck;
    private HashMap <String,DeckOfCards> mPlayersHands;

    public YanivTurn() {
        super(GAME_NAME);

        mAvailableDiscardedCards = new DeckOfCards();
        mDiscardedCards = new DeckOfCards();
        mGlobalCardDeck = new DeckOfCards();
        mPlayersHands = new HashMap<>();
    }

    /** TODO
     * Retrieve Json representation of object
     * @return Json
     * @throws JSONException
     */
    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject gameData = super.toJson();

        gameData.put(AVAILABLE_DISCARDED_CARDS, this.mAvailableDiscardedCards.toJson());
        gameData.put(DISCARDED_CARDS, this.mDiscardedCards.toJson());
        gameData.put(GLOBAL_CARD_DECK, this.mGlobalCardDeck.toJson());

        JSONObject playersHandsObject = new JSONObject();

        for(String key : mPlayersHands.keySet()) {
            playersHandsObject.put(key, mPlayersHands.get(key).toJson());
        }
        gameData.put(PLAYERS_HANDS, playersHandsObject);

        return gameData;
    }

    /** TODO
     * Update data according to Json value
     * @param jsonObject turn data
     * @throws JSONException
     */
    @Override
    public void fromJson(JSONObject jsonObject) throws JSONException {
        super.fromJson(jsonObject);

        this.mAvailableDiscardedCards.fromJson(jsonObject.getJSONObject(AVAILABLE_DISCARDED_CARDS));
        this.mDiscardedCards.fromJson(jsonObject.getJSONObject(DISCARDED_CARDS));
        this.mGlobalCardDeck.fromJson(jsonObject.getJSONObject(GLOBAL_CARD_DECK));

        this.mPlayersHands.clear();
        JSONObject jsonPlayersHands =  jsonObject.getJSONObject(PLAYERS_HANDS);

        Iterator<String> it = jsonPlayersHands.keys();
        String key;

        while (it.hasNext()) {
            DeckOfCards value = new DeckOfCards();

            key = it.next();
            value.fromJson(jsonPlayersHands.getJSONObject(key));
            mPlayersHands.put(key, value);
        }
    }

    /**
     * Get players hands
     * @return players hands as HashMap
     */
    public HashMap<String, DeckOfCards> getPlayersHands() {
        return mPlayersHands;
    }

    /**
     * Get available discarded cards
     * @return get available discarded cards as DeckOfCards
     */
    public DeckOfCards getAvailableDiscardedCards() {
        return mAvailableDiscardedCards;
    }

    /**
     * Get discarded cards
     * @return get discarded cards as DeckOfCards
     */
    public DeckOfCards getDiscardedCards() {
        return mDiscardedCards;
    }

    /**
     * Get global card deck
     * @return get global card deck as DeckOfCards
     */
    public DeckOfCards getGlobalCardDeck() {
        return mGlobalCardDeck;
    }

}
