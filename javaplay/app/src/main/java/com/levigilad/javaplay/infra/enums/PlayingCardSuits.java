package com.levigilad.javaplay.infra.enums;

import java.io.Serializable;

/**
 * This enum provides symbol options for game card
 */
public enum PlayingCardSuits implements Serializable {
    /**
     * Card doesn't have a suit
     */
    NONE,
    /**
     * Card has hearts suit
     */
    HEARTS,
    /**
     * Card has diamonds suit
     */
    DIAMONDS,
    /**
     * Card has clubs suit
     */
    CLUBS,
    /**
     * Card has spades suit
     */
    SPADES
}
