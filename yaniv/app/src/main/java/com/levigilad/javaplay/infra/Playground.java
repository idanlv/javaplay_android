package com.levigilad.javaplay.infra;

import android.util.Log;

import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        Reflections reflections = new Reflections(this.getClass().getPackage());

        // Gets all subclass of type Game
        Set<Class<? extends Game>> gameModules = reflections.getSubTypesOf(Game.class);

        for (Class<? extends Game> game : gameModules) {
            // Subclass is not abstract
            if (!Modifier.isAbstract(game.getModifiers())) {
                Constructor<?> gameConstructor = game.getConstructors()[0];
                try {
                    _games.add(((Game) gameConstructor.newInstance()));
                } catch (Exception ex){
                    Log.e(TAG, String.format("Could not initiate game of type (0)", game.getName()));
                }
            }
        }
    }

    /**
     * Getter
     * @return
     */
    public List<Game> getGames() {
        return this._games;
    }
}
