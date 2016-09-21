package com.levigilad.javaplay.infra.enums;

import java.io.Serializable;

/**
 * Created by User on 21/09/2016.
 */
public enum GameCardSymbols implements Serializable {
    NONE,
    HEARTS,
    DIAMONDS,
    CLUBS,
    SPADES;

    public String getSymbol() {
        return this.name();
    }
}
