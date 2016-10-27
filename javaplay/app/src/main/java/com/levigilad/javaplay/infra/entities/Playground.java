package com.levigilad.javaplay.infra.entities;

import com.levigilad.javaplay.tictactoe.TicTacToeGame;
import com.levigilad.javaplay.yaniv.YanivGame;

import java.util.ArrayList;

/**
 * This class represents a Singleton of a Playground
 */
public class Playground {
    /**
     * Constant
     */
    private static final String TAG = Playground.class.getName();

    /**
     * Members
     */
    private static Playground mInstance = null;
    private ArrayList<Game> mGames = new ArrayList<>();

    /**
     * Constructor
     */
    private Playground() {
        load();
    }

    /**
     * Retrieves Playground instance
     * @return Playground instance
     */
    public static Playground getInstance() {
        if (mInstance == null) {
            mInstance = new Playground();
        }

        return mInstance;
    }

    /**
     * Loads all game options
     */
    private void load() {
        mGames.add(new YanivGame());
        mGames.add(new TicTacToeGame());
    }

    /**
     * Getter
     * @return
     */
    public ArrayList<Game> getGames() {
        return this.mGames;
    }

    /**
     * Retrieves a game by id
     * @param gameId Game's id
     * @return If game id exist, return game object. Otherwise, null.
     */
    public Game getGame(String gameId) {
        for (Game game : this.mGames) {
            if (game.getGameId().equals(gameId)) {
                return game;
            }
        }

        return null;
    }
}
