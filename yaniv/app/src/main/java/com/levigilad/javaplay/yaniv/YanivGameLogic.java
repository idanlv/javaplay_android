package com.levigilad.javaplay.yaniv;

import com.levigilad.javaplay.infra.BaseGameLogic;
import com.levigilad.javaplay.infra.BaseTurn;
import com.levigilad.javaplay.infra.entities.GameCard;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Created by User on 22/09/2016.
 */
public class YanivGameLogic extends BaseGameLogic {
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

            }
        });
    }
}
