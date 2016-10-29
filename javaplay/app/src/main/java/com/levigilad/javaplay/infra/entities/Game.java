package com.levigilad.javaplay.infra.entities;

import java.io.Serializable;

/**
 * This abstract provides minimal Game functionality
 */
public abstract class Game implements Serializable {
    /**
     * Constants
     */
    private static final int DEFAULT_MIN_NUM_PLAYERS = 2;
    private static final boolean DEFAULT_AUTO_MATCH = false;

    /**
     * Members
     */
    private boolean mAllowAutoMatch;
    private final String mDescription;
    private final int mMaxNumberOfPlayers;
    private final String mLeaderboardId;
    private final String mGameId;
    private int mMinNumberOfPlayers;

    /**
     * Constructor: Creates a game object
     * @param gameId The game name (used as id)
     * @param description The description for the game
     * @param leaderboardId The id of the leaderboard in google play services
     * @param maxNumberOfPlayers Maximum number of players in match
     * @param allowAutoMatch as boolean to allow auto matching or not
     */
    public Game(String gameId, String description, String leaderboardId,
                int maxNumberOfPlayers, boolean allowAutoMatch) {
        this(gameId, description, leaderboardId, DEFAULT_MIN_NUM_PLAYERS, maxNumberOfPlayers, allowAutoMatch);
    }

    /**
     * Constructor: Creates a game object
     * @param gameId The game name (used as id)
     * @param description The description for the game
     * @param leaderboardId The id of the leaderboard in google play services
     * @param minNumberOfPlayers Minimum number of players in match
     * @param maxNumberOfPlayers Maximum number of players in match
     */
    public Game(String gameId, String description, String leaderboardId,
                int minNumberOfPlayers, int maxNumberOfPlayers) {
        this(gameId, description, leaderboardId, minNumberOfPlayers, maxNumberOfPlayers, DEFAULT_AUTO_MATCH);
    }

    /**
     * Constructor: Creates a game object
     * @param gameId The game name (used as id)
     * @param description The description for the game
     * @param leaderboardId The id of the leaderboard in google play services
     * @param maxNumberOfPlayers Maximum number of players in match
     */
    public Game(String gameId, String description, String leaderboardId,
                int maxNumberOfPlayers) {
        this(gameId, description, leaderboardId, DEFAULT_MIN_NUM_PLAYERS, maxNumberOfPlayers, DEFAULT_AUTO_MATCH);
    }

    /**
     * Constructor: Creates a game object
     * @param gameId The game name (used as id)
     * @param description The description for the game
     * @param leaderboardId The id of the leaderboard in google play services
     * @param minNumberOfPlayers Minimum number of players in match
     * @param maxNumberOfPlayers Maximum number of players in match
     */
    public Game(String gameId, String description, String leaderboardId,
                int minNumberOfPlayers, int maxNumberOfPlayers, boolean allowAutoMatch) {
        this.mGameId = gameId;
        this.mLeaderboardId = leaderboardId;
        this.mDescription = description;
        this.mMaxNumberOfPlayers = maxNumberOfPlayers;
        this.mMinNumberOfPlayers = minNumberOfPlayers;
        this.mAllowAutoMatch = allowAutoMatch;
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

    public boolean getmAllowAutoMatch() {
        return mAllowAutoMatch;
    }

    public int getMinNumberOfPlayers() {
        return mMinNumberOfPlayers;
    }
}