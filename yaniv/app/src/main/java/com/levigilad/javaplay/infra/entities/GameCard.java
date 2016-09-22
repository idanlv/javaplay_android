package com.levigilad.javaplay.infra.entities;

import com.levigilad.javaplay.infra.IJsonSerializable;
import com.levigilad.javaplay.infra.enums.GameCardSymbols;
import com.levigilad.javaplay.infra.enums.GameCardValues;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class represents a game card
 */
public class GameCard implements IJsonSerializable, Comparable {
    private GameCardValues _value;
    private GameCardSymbols _symbol;

    /**
     * Empty Constructor
     */
    public GameCard() {
    }

    /**
     * Constructor
     * @param value Value of card
     * @param symbol Symbol of card
     */
    public GameCard(GameCardValues value, GameCardSymbols symbol) {
        this._value = value;
        this._symbol = symbol;
        validate(value, symbol);
    }

    /**
     * Getter
     * @return Card's value
     */
    public GameCardValues getValue() {
        return this._value;
    }

    /**
     * Getter
     * @return Card's symbol
     */
    public GameCardSymbols getSymbol() {
        return this._symbol;
    }

    /**
     * Validates card was initialized properly
     * @param value Card numeric value
     * @param symbol Card symbol
     */
    private void validate(GameCardValues value, GameCardSymbols symbol) {
        if (((value == GameCardValues.JOKER) && (symbol != GameCardSymbols.NONE)) ||
                ((value != GameCardValues.JOKER) && (symbol == GameCardSymbols.NONE))) {
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
        cardObject.put("value", this._value);
        cardObject.put("symbol", this._symbol);

        return cardObject;
    }

    /**
     * Load card from given json format
     * @param object Card in json format
     * @throws JSONException
     */
    @Override
    public void fromJson(JSONObject object) throws JSONException {
        this._value = (GameCardValues)object.get("value");
        this._symbol = (GameCardSymbols)object.get("symbol");
    }

    /**
     * This method compares between two given cards
     * @param another compared to card
     * @return
     */
    @Override
    public int compareTo(Object another) {
        if (another instanceof GameCard) {
            GameCard anotherCard = (GameCard)another;

            return this.getValue().compareTo(anotherCard.getValue());
        }

        throw new ClassCastException(
                String.format("Comparing type (0) to GameCard is not supported",
                        another.getClass().getName()));
    }
}
