package com.levigilad.javaplay.yaniv;

import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.PlayingCard;
import com.levigilad.javaplay.infra.enums.PlayingCardRanks;
import com.levigilad.javaplay.infra.enums.PlayingCardSuits;

import org.junit.Test;

import static org.junit.Assert.*;

// TODO Comments

public class IsSequenceUnitTest {
    @Test
    public void testGoodSequence() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.TWO, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.THREE, PlayingCardSuits.DIAMONDS));

        boolean expected = true;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testTooSmallDeck() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.TWO, PlayingCardSuits.DIAMONDS));

        boolean expected = false;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testDifferentSuits() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.TWO, PlayingCardSuits.CLUBS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.THREE, PlayingCardSuits.DIAMONDS));

        boolean expected = false;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testNothingButJokers() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));

        boolean expected = true;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testGoodWithJokers() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.THREE, PlayingCardSuits.DIAMONDS));

        boolean expected = true;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testGoodWithJokersAtStart() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.SEVEN, PlayingCardSuits.CLUBS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.SIX, PlayingCardSuits.CLUBS));
        cards.sort();

        boolean expected = true;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testGoodWith2Jokers() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.SEVEN, PlayingCardSuits.CLUBS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));
        cards.sort();

        boolean expected = true;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testBadWithJokers() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.FOUR, PlayingCardSuits.DIAMONDS));

        boolean expected = false;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testGapsNoJokers() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.FOUR, PlayingCardSuits.DIAMONDS));

        boolean expected = false;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testDuplicatesNoJoker() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));

        boolean expected = false;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testDuplicatesWithJoker() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));

        boolean expected = false;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testRoyalty() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JACK, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.QUEEN, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.KING, PlayingCardSuits.DIAMONDS));

        boolean expected = true;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }
}
