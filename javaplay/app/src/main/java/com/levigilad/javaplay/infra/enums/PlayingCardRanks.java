package com.levigilad.javaplay.infra.enums;

import java.io.Serializable;

/**
 * This enum provides value options for game card
 */
public enum PlayingCardRanks implements Serializable {
    ACE(1, "ace"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    JACK(11, "jack"),
    QUEEN(12, "queen"),
    KING(13, "king"),
    JOKER(0, "joker");

    /**
     * Members
     */
    private int mNumericValue;
    private String mName;

    PlayingCardRanks(int numericValue, String name) {
        this.mNumericValue = numericValue;
        this.mName = name;
    }

    /**
     * Getter
     * @return Returns the numeric value of the rank
     */
    public int getNumericValue() {
        return this.mNumericValue;
    }

    /**
     * Getter
     * @return Returns the name of the rank
     */
    public String getName() {
        return this.mName;
    }
}
