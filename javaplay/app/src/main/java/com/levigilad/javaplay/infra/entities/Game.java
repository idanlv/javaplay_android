package com.levigilad.javaplay.infra.entities;

import java.io.Serializable;

/**
 * This abstract provides minimal Game functionality
 */
public abstract class Game implements Serializable {
    /**
     * Constants
     */
    private final String mGameId;
    private final String mLeaderboardId;
    private final String mDescription;
    private final int mMaxNumberOfPlayers;

    /**
     * Constructor: Creates a game object
     * @param gameId The game name (used as id)
     * @param description The description for the game
     * @param leaderboardId The id of the leaderboard in google play services
     * @param maxNumberOfPlayers Maximum number of players in match
     */
    public Game(String gameId, String description, String leaderboardId, int maxNumberOfPlayers) {
        this.mGameId = gameId;
        this.mLeaderboardId = leaderboardId;
        this.mDescription = description;
        this.mMaxNumberOfPlayers = maxNumberOfPlayers;
    }

    /**
     * Getter
     * @return Number of players
     */
    public int getMaxNumberOfPlayers() {
        return mMaxNumberOfPlayers;
    }

    /**
     * Getter
     * @return Id of leaderboard
     */
    public String getLeaderboardId() {
        return mLeaderboardId;
    }

    /**
     * Getter
     * @return Description of the game
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Getter
     * @return Id of the game
     */
    public String getGameId() {
        return mGameId;
    }
}