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
    /**
     * Constants
     */
    public static final String GAME_NAME = "Yaniv";
    public static final String INITIALIZE_DONE = "initializeDone";
    public static final String CURRENT_PLAYRE_HAND = "mCurrPlayersHand";
    public static final String AVAILABLE_DISCARDED_CARDS = "mAvailableDiscardedCards";
    public static final String DISCARDED_CARDS = "mDiscardedCards";
    public static final String GLOBAL_CARD_DECK = "mGlobalCardDeck";

    /**
     * Members
     */
    private DeckOfCards mCurrPlayersHand;
    private DeckOfCards mAvailableDiscardedCards;
    private DeckOfCards mDiscardedCards;
    private DeckOfCards mGlobalCardDeck;

    private boolean mInitializeDone;

    public YanivTurn() {
        super(GAME_NAME);

        mCurrPlayersHand = new DeckOfCards();
        mAvailableDiscardedCards = new DeckOfCards();
        mDiscardedCards = new DeckOfCards();
        mGlobalCardDeck = new DeckOfCards();
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
        gameData.put(CURRENT_PLAYRE_HAND, this.mCurrPlayersHand.toJson());
        gameData.put(AVAILABLE_DISCARDED_CARDS, this.mAvailableDiscardedCards.toJson());
        gameData.put(DISCARDED_CARDS, this.mDiscardedCards.toJson());
        gameData.put(GLOBAL_CARD_DECK, this.mGlobalCardDeck.toJson());

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

        this.mCurrPlayersHand = new DeckOfCards();
        this.mCurrPlayersHand.fromJson(jsonObject.getJSONObject(CURRENT_PLAYRE_HAND));
        this.mAvailableDiscardedCards = new DeckOfCards();
        this.mAvailableDiscardedCards.fromJson(jsonObject.getJSONObject(AVAILABLE_DISCARDED_CARDS));
        this.mDiscardedCards = new DeckOfCards();
        this.mDiscardedCards.fromJson(jsonObject.getJSONObject(DISCARDED_CARDS));
        this.mGlobalCardDeck = new DeckOfCards();
        this.mGlobalCardDeck.fromJson(jsonObject.getJSONObject(GLOBAL_CARD_DECK));

    }

    public DeckOfCards getmCurrPlayersHand() {
        return mCurrPlayersHand;
    }

    public void setmCurrPlayersHand(DeckOfCards mCurrPlayersHand) {
        this.mCurrPlayersHand = mCurrPlayersHand;
    }

    public DeckOfCards getmAvailableDiscardedCards() {
        return mAvailableDiscardedCards;
    }

    public void setmAvailableDiscardedCards(DeckOfCards mAvailableDiscardedCards) {
        this.mAvailableDiscardedCards = mAvailableDiscardedCards;
    }


    public DeckOfCards getmDiscardedCards() {
        return mDiscardedCards;
    }

    public void setmDiscardedCards(DeckOfCards mDiscardedCards) {
        this.mDiscardedCards = mDiscardedCards;
    }

    public DeckOfCards getmGlobalCardDeck() {
        return mGlobalCardDeck;
    }

    public void setmGlobalCardDeck(DeckOfCards mGlobalCardDeck) {
        this.mGlobalCardDeck = mGlobalCardDeck;
    }
}
