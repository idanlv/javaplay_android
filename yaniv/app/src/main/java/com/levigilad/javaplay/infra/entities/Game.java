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
    public Game(int gameId, int descriptionId, int leaderboardId, int maxNumberOfPlayers) {

        this.mGameId = Resources.getSystem().getString(gameId);
        this.mLeaderboardId = Resources.getSystem().getString(leaderboardId);
        this.mDescription = Resources.getSystem().getString(descriptionId);
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