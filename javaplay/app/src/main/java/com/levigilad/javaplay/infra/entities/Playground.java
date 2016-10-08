package com.levigilad.javaplay.infra.entities;

import android.content.Context;

import com.levigilad.javaplay.yaniv.YanivGame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a Singleton of a Playground
 */
public class Playground {
    private static final String TAG = Playground.class.getName();
    private static Playground _instance = null;
    private ArrayList<Game> _games = new ArrayList<>();

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
        _games.add(new YanivGame(context));
    }

    /**
     * Getter
     * @return
     */
    public ArrayList<Game> getGames() {
        return this._games;
    }

    public void getGame(String gameId) {

    }
}
