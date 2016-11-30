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
    private DeckOfCards mTurnDiscardedDeck;

    /**
     * Constructor
     */
    public YanivTurn() {
        super(GAME_NAME);

        mAvailableDiscardedCards = new DeckOfCards();
        mDiscardedCards = new DeckOfCards();
        mGlobalCardDeck = new DeckOfCards();
        mPlayersHands = new HashMap<>();
    }

    /**
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

    /**
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

    /**
     * Adds a new deck to turn data
     * @param participantId participant which the deck belongs to
     * @param deck new deck
     */
    public void addParticipantDeck(String participantId, DeckOfCards deck) {
        mPlayersHands.put(participantId, deck);
    }

    /**
     * Setter
     * @param globalDeck global deck
     */
    public void setGlobalDeck(DeckOfCards globalDeck) {
        this.mGlobalCardDeck = globalDeck;
    }

    /**
     * Setter
     * @param availableDiscardedDeck Available for discard deck
     */
    public void setAvailableDiscardedDeck(DeckOfCards availableDiscardedDeck) {
        this.mAvailableDiscardedCards = availableDiscardedDeck;
    }

    /**
     * Getter
     * @param participantId participant's id
     * @return deck of participant
     */
    public DeckOfCards getPlayerHand(String participantId) {
        return mPlayersHands.get(participantId);
    }

    /**
     * Setter
     * @param turnDiscardedDeck discarded deck in current turn
     */
    public void setTurnDiscardedDeck(DeckOfCards turnDiscardedDeck) {
        this.mTurnDiscardedDeck = turnDiscardedDeck;
    }

    /**
     * Getter
     * @return True if cards were already discarded in this turn, otherwise False
     */
    public boolean hasTurnDiscardedDeck() {
        return this.mTurnDiscardedDeck != null;
    }

    public DeckOfCards getTurnDiscardedDeck() {
        return mTurnDiscardedDeck;
    }
}
