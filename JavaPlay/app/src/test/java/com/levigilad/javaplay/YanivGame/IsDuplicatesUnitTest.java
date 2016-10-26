package com.levigilad.javaplay.YanivGame;

import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.PlayingCard;
import com.levigilad.javaplay.infra.enums.PlayingCardRanks;
import com.levigilad.javaplay.infra.enums.PlayingCardSuits;
import com.levigilad.javaplay.yaniv.YanivGame;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by User on 26/10/2016.
 */

public class IsDuplicatesUnitTest {

    @Test
    public void testGoodDuplicates() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.HEARTS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));

        boolean expected = true;
        boolean actual = YanivGame.isDuplicates(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testDuplicatedJoker() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.HEARTS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));

        boolean expected = true;
        boolean actual = YanivGame.isDuplicates(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testNothingButJokers() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));

        boolean expected = true;
        boolean actual = YanivGame.isDuplicates(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testTooSmallDeck() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.HEARTS));

        boolean expected = false;
        boolean actual = YanivGame.isDuplicates(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testNotDuplicates() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.HEARTS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.TWO, PlayingCardSuits.DIAMONDS));

        boolean expected = false;
        boolean actual = YanivGame.isDuplicates(cards);
        assertEquals(expected, actual);
    }
}
