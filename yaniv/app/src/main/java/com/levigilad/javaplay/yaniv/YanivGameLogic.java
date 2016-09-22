package com.levigilad.javaplay.yaniv;

import com.levigilad.javaplay.infra.BaseTurn;
import com.levigilad.javaplay.infra.CardGameLogic;
import com.levigilad.javaplay.infra.entities.CardsDeck;
import com.levigilad.javaplay.infra.entities.GameCard;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Created by User on 22/09/2016.
 */
public class YanivGameLogic extends CardGameLogic {
    private final int INITIAL_CARD_COUNT = 5;

    @Override
    public BaseTurn playTurn(BaseTurn currentTurnData) {
        return null;
    }

    public boolean isCardsDiscardValid(LinkedList<GameCard> discardCards) {
        // Any discard of one card is valid
        if (discardCards.size() == 1) {
            return true;
        }

        // Cards are as valid sequence of 3 cards or more
        if ((discardCards.size() >= 3) && isSequence(discardCards)) {
            return true;
        }
    }

    private boolean isSequence(LinkedList<GameCard> discardCards) {
        Collections.sort(discardCards, new Comparator<GameCard>() {
            @Override
            public int compare(GameCard lhs, GameCard rhs) {
                return lhs.compareTo(rhs);
            }
        });
    }

    private int getCardValue(GameCard card) {
        switch (card.getValue()) {
            case ACE:
                return 1;
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FOUR:
                return 4;
            case FIVE:
                return 5;
            case SIX:
                return 6;
            case SEVEN:
                return 7;
            case EIGHT:
                return 8;
            case NINE:
                return 9;
            case TEN:
            case PRINCE:
            case QUEEN:
            case KING:
                return 10;
            case JOKER:
                return 0;
        }
    }

    public void dealCards(int numberOfPlayers) {
        CardsDeck hiddenDeck = generateDeck();
        LinkedList<CardsDeck> playerDecks = generateEmptyDecks(numberOfPlayers);

        // Divide cards to players
        for (int i = 0; i < INITIAL_CARD_COUNT; i++) {
            for (int j = 0; j < numberOfPlayers; j++) {
                // Remove card from cashier deck
                GameCard card = hiddenDeck.pop();

                // Add card to player
                playerDecks.get(j).addCard(card);
            }
        }

        CardsDeck exposedDeck = new CardsDeck();
        GameCard exposed = hiddenDeck.pop();
        exposedDeck.addCard(exposed);
    }
}
