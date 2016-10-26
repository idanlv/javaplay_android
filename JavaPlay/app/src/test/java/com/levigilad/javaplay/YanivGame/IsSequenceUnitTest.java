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

public class IsSequenceUnitTest {
    @Test
    public void testIsSequenceGood() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.TWO, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.THREE, PlayingCardSuits.DIAMONDS));

        boolean expected = true;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testIsSequenceTooSmallDeck() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.TWO, PlayingCardSuits.DIAMONDS));

        boolean expected = false;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testIsSequenceDifferentSuits() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.TWO, PlayingCardSuits.CLUBS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.THREE, PlayingCardSuits.DIAMONDS));

        boolean expected = false;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testIsSequenceAllJokers() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));

        boolean expected = true;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testIsSequenceGoodWithJokers() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.THREE, PlayingCardSuits.DIAMONDS));

        boolean expected = true;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testIsSequenceBadWithJokers() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.FOUR, PlayingCardSuits.DIAMONDS));

        boolean expected = false;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testIsSequenceGapsNoJokers() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.FOUR, PlayingCardSuits.DIAMONDS));

        boolean expected = false;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testIsSequenceDuplicatesNoJoker() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));

        boolean expected = false;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }

    @Test
    public void testIsSequenceDuplicatesWithJoker() {
        DeckOfCards cards = new DeckOfCards();
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.DIAMONDS));
        cards.addCardToBottom(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));

        boolean expected = false;
        boolean actual = YanivGame.isSequence(cards);
        assertEquals(expected, actual);
    }
}
