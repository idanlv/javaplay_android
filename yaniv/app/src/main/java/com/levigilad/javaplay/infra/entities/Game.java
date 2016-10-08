package com.levigilad.javaplay.infra.entities;

import android.content.res.Resources;

import java.io.Serializable;

/**
 * This abstract provides minimal Game functionality
 */
public abstract class Game implements Serializable {
    private final String mGameId;
    private final String mLeaderboardId;
    private final String mDescription;
    private final int mMaxNumberOfPlayers;

    /**
     * Constructor
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
     */
    public int getMaxNumberOfPlayers() {
        return mMaxNumberOfPlayers;
    }

    /**
     * Getter
     */
    public String getLeaderboardId() {
        return mLeaderboardId;
    }

    /**
     * Getter
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Getter
     * @return
     */
    public String getGameId() {
        return mGameId;
    }
}