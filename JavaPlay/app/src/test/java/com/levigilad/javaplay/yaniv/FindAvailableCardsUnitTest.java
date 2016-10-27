package com.levigilad.javaplay.yaniv;

import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.PlayingCard;
import com.levigilad.javaplay.infra.enums.PlayingCardRanks;
import com.levigilad.javaplay.infra.enums.PlayingCardSuits;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by User on 26/10/2016.
 */

public class FindAvailableCardsUnitTest {

    @Test
    public void discardedSequence() {
        DeckOfCards discarded = new DeckOfCards();
        PlayingCard card1 = new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS);
        discarded.addCardToBottom(card1);
        PlayingCard card2 = new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE);
        discarded.addCardToBottom(card2);
        PlayingCard card3 = new PlayingCard(PlayingCardRanks.THREE, PlayingCardSuits.DIAMONDS);
        discarded.addCardToBottom(card3);

        DeckOfCards expected = new DeckOfCards();
        expected.addCardToBottom(card1);
        expected.addCardToBottom(card3);

        DeckOfCards actual = YanivGame.findAvailableCards(discarded);

        int expectedSize = 1;
        int actualSize = discarded.size();

        assertEquals(expected, actual);
        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void discardedOneCard() {
        DeckOfCards discarded = new DeckOfCards();
        PlayingCard card1 = new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS);
        discarded.addCardToBottom(card1);

        DeckOfCards expected = new DeckOfCards();
        expected.addCardToBottom(card1);

        DeckOfCards actual = YanivGame.findAvailableCards(discarded);

        int expectedSize = 0;
        int actualSize = discarded.size();

        assertEquals(expected, actual);
        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void discardedTwoDuplicates() {
        DeckOfCards discarded = new DeckOfCards();
        PlayingCard card1 = new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.CLUBS);
        discarded.addCardToBottom(card1);
        PlayingCard card2 = new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS);
        discarded.addCardToBottom(card2);

        DeckOfCards expected = new DeckOfCards();
        expected.addCardToBottom(card1);
        expected.addCardToBottom(card2);

        DeckOfCards actual = YanivGame.findAvailableCards(discarded);

        int expectedSize = 0;
        int actualSize = discarded.size();

        assertEquals(expected, actual);
        assertEquals(expectedSize, actualSize);
    }
}
