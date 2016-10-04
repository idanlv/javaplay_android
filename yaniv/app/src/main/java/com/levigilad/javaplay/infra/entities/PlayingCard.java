package com.levigilad.javaplay.infra.entities;

import com.levigilad.javaplay.infra.interfaces.IJsonSerializable;
import com.levigilad.javaplay.infra.enums.GameCardSuits;
import com.levigilad.javaplay.infra.enums.GameCardRanks;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class represents a game card
 */
public class PlayingCard implements IJsonSerializable, Comparable {
    // Consts
    public static final String CARD_VALUE = "card_value";
    public static final String CARD_SYMBOL = "card_symbol";

    // Members
    private GameCardRanks _rank;
    private GameCardSuits _suit;

    /**
     * Empty Constructor
     */
    public PlayingCard() {
    }

    /**
     * Constructor
     * @param rank Value of card
     * @param suit Symbol of card
     */
    public PlayingCard(GameCardRanks rank, GameCardSuits suit) {
        this._rank = rank;
        this._suit = suit;
        validate(rank, suit);
    }

    /**
     * Copy constructor
     * @param other PlayingCard to copy from
     */
    public PlayingCard(PlayingCard other) {
        this._rank = other.getRank();
        this._suit = other.getSuit();
    }

    /**
     * Getter
     * @return Card's value
     */
    public GameCardRanks getRank() {
        return this._rank;
    }

    /**
     * Getter
     * @return Card's symbol
     */
    public GameCardSuits getSuit() {
        return this._suit;
    }

    public int getDrawableId() {
        return 0;
    }

    /**
     * Validates card was initialized properly
     * @param rank Card numeric rank
     * @param suit Card symbol
     */
    private void validate(GameCardRanks rank, GameCardSuits suit) {
        if (((rank == GameCardRanks.JOKER) && (suit != GameCardSuits.NONE)) ||
                ((rank != GameCardRanks.JOKER) && (suit == GameCardSuits.NONE))) {
            throw new IllegalArgumentException("Card game cannot be initialized");
        }
    }

    /**
     * * Generate a json representation of the card
     * @return
     * @throws JSONException
     */
    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject cardObject = new JSONObject();
        cardObject.put(CARD_VALUE, this._rank);
        cardObject.put(CARD_SYMBOL, this._suit);

        return cardObject;
    }

    /**
     * Load card from given json format
     * @param object Card in json format
     * @throws JSONException
     */
    @Override
    public void fromJson(JSONObject object) throws JSONException {
        this._rank = (GameCardRanks)object.get(CARD_VALUE);
        this._suit = (GameCardSuits)object.get(CARD_SYMBOL);
    }

    /**
     * This method compares between two given cards
     * @param another compared to card
     * @return
     */
    @Override
    public int compareTo(Object another) {
        if (another instanceof PlayingCard) {
            PlayingCard anotherCard = (PlayingCard)another;

            return this.getRank().compareTo(anotherCard.getRank());
        }

        throw new ClassCastException(
                String.format("Comparing type (0) to PlayingCard is not supported",
                        another.getClass().getName()));
    }
}
