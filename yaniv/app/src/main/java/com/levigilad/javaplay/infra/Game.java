package com.levigilad.javaplay.infra;

/**
 * This abstract provides minimal Game functionality
 */
public abstract class Game {
    private int _maxNumberOfPlayers;

    /**
     * Constructor
     * @param maxNumberOfPlayers Maximum number of players in match
     */
    public Game(int maxNumberOfPlayers) {
        this._maxNumberOfPlayers = maxNumberOfPlayers;
    }

    /**
     * Getter
     * @return
     */
    public int getMaxNumberOfPlayers() {
        return _maxNumberOfPlayers;
    }

    /**
     * Returns game's name
     * @return
     */
    public abstract String getDisplayName();

    @Override
    public String toString() {
        return getDisplayName();
    }
}