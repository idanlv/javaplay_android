package com.levigilad.javaplay.infra.enums;

import java.io.Serializable;

/**
 * Created by User on 21/09/2016.
 */
public enum GameCardValues implements Serializable {
    ACE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    PRINCE,
    QUEEN,
    KING,
    JOKER;

    public String getValue() {
        return this.name();
    }
}
