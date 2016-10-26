package com.levigilad.javaplay.infra.entities;

import com.levigilad.javaplay.infra.enums.PlayingCardState;
import com.levigilad.javaplay.infra.interfaces.IJsonSerializable;
import com.levigilad.javaplay.infra.enums.PlayingCardSuits;
import com.levigilad.javaplay.infra.enums.PlayingCardRanks;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class represents a game card
 */
public class PlayingCard implements IJsonSerializable, Comparable<PlayingCard> {
    /**
     * Constants
    */
    public static final String CARD_VALUE = "card_value";
    public static final String CARD_SYMBOL = "card_symbol";
    private static final String CARD_FORMAT = "(%s , %s)";

    /**
     * Members
    */
    private PlayingCardRanks mRank;
    private PlayingCardSuits mSuit;
    private PlayingCardState mState;

    /**
     * Empty constructor
     */
    public PlayingCard() {

    }

    /**
     * Constructor
     * @param rank Value of card
     * @param suit Symbol of card
     */
    public PlayingCard(PlayingCardRanks rank, PlayingCardSuits suit) {
        this.mRank = rank;
        this.mSuit = suit;
        this.mState = PlayingCardState.AVAILABLE;
        validate(rank, suit);
    }

    /**
     * Copy constructor
     * @param other PlayingCard to copy from
     */
    public PlayingCard(PlayingCard other) {
        this.mRank = other.getRank();
        this.mSuit = other.getSuit();
        this.mState = other.mState;
    }

    /**
     * Getter
     * @return Card's value
     */
    public PlayingCardRanks getRank() {
        return this.mRank;
    }

    /**
     * Getter
     * @return Card's symbol
     */
    public PlayingCardSuits getSuit() {
        return this.mSuit;
    }

    /**
     * Checks if the card is discarded
     * @return
     */
    public boolean isDiscarded() {
        return mState == PlayingCardState.DISCARDED;
    }

    /**
     * Setter
     * @param state new playing card state (discarded/available)
     */
    public void setState(PlayingCardState state) {
        mState  = state;
    }

    /**
     * Validates card was initialized properly
     * @param rank Card numeric rank
     * @param suit Card symbol
     * @throws IllegalArgumentException If playing card is invalid
     */
    private void validate(PlayingCardRanks rank, PlayingCardSuits suit) {
        if (((rank == PlayingCardRanks.JOKER) && (suit != PlayingCardSuits.NONE)) ||
                ((rank != PlayingCardRanks.JOKER) && (suit == PlayingCardSuits.NONE))) {
            throw new IllegalArgumentException("Card game cannot be initialized");
        }
    }

    /**
     * Generate a json representation of the card
     * @return card as json object
     * @throws JSONException If the json object wasn't created correctly
     */
    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject cardObject = new JSONObject();
        cardObject.put(CARD_VALUE, this.mRank);
        cardObject.put(CARD_SYMBOL, this.mSuit);

        return cardObject;
    }

    /**
     * Load card from given json format
     * @param jsonObject Card in json format
     * @throws JSONException If the json object wasn't read correctly
     */
    @Override
    public void fromJson(JSONObject jsonObject) throws JSONException {
        this.mRank = PlayingCardRanks.valueOf(jsonObject.getString(CARD_VALUE));
        this.mSuit = PlayingCardSuits.valueOf(jsonObject.getString(CARD_SYMBOL));
    }

    /**
     * This method compares between two given cards
     * @param another compared to card
     * @return the difference between cards values
     */
    @Override
    public int compareTo(PlayingCard another) {
        return this.getRank().getNumericValue() - another.getRank().getNumericValue();
    }

    /**
     * Retrieves a string representation of the playing card
     * @return String representation
     */
    @Override
    public String toString() {
        return String.format(CARD_FORMAT, getRank(), getSuit());
    }
}
