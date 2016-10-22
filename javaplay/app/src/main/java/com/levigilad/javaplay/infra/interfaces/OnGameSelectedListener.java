package com.levigilad.javaplay.infra.interfaces;

/**
 * This interface must be implemented by an object which wants
 * to be inform whenever a new game is selected by the user
 */
public interface OnGameSelectedListener {
    /**
     * This methods handles a selection of a game
     * @param gameId The id of the game
     */
    void onGameSelected(String gameId);
}
