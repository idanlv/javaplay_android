package com.levigilad.javaplay.infra.entities;

import android.content.Context;

import com.levigilad.javaplay.yaniv.YanivGame;

import java.util.ArrayList;

/**
 * This class represents a Singleton of a Playground
 */
public class Playground {
    private static final String TAG = Playground.class.getName();
    private static Playground _instance = null;
    private ArrayList<Game> mGames = new ArrayList<>();

    /**
     * Constructor
     */
    private Playground(Context context) {
        init(context);
    }

    /**
     * Retrieves Playground instance
     * @return Playground instance
     */
    public static Playground getInstance(Context context) {
        if (_instance == null) {
            _instance = new Playground(context);
        }

        return _instance;
    }

    /**
     * Loads all game options
     */
    private void init(Context context) {
        mGames.add(new YanivGame(context));
    }

    /**
     * Getter
     * @return
     */
    public ArrayList<Game> getGames() {
        return this.mGames;
    }

    public Game getGame(String gameId) {
        for (Game game : this.mGames) {
            if (game.getGameId().equals(gameId)) {
                return game;
            }
        }

        return null;
    }
}
