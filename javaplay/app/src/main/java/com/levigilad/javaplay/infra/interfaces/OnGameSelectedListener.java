package com.levigilad.javaplay.infra.interfaces;

import com.levigilad.javaplay.infra.entities.Game;

/**
 * This interface must be implemented by an object which wants
 * to be inform whenever a new game is selected by the user
 */
public interface OnGameSelectedListener {
    /**
     * This methods handles a selection of a game
     * @param game The id of the game
     */
    void onGameSelected(Game game);
}
