package com.levigilad.javaplay.infra.entities;

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
    private Playground() {
        init();
    }

    /**
     * Retrieves Playground instance
     * @return Playground instance
     */
    public static Playground getInstance() {
        if (_instance == null) {
            _instance = new Playground();
        }

        return _instance;
    }

    /**
     * Loads all game options
     */
    private void init() {
        _games.add(new YanivGame());
    }

    /**
     * Getter
     * @return
     */
    public ArrayList<Game> getGames() {
        return this._games;
    }
}
