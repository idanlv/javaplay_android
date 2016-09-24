package com.levigilad.javaplay.infra;

import android.util.Log;

import com.levigilad.javaplay.yaniv.YanivGame;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.reflections.ReflectionUtils.*;

/**
 * This class represents a Singleton of a Playground
 */
public class Playground {
    private static final String TAG = Playground.class.getName();
    private static Playground _instance = null;
    private List<Game> _games = new LinkedList<>();

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
        // TODO: fix reflection
        /*Reflections reflections = new Reflections("com.levigilad");

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
        }*/

        _games.add(new YanivGame());
    }

    /**
     * Getter
     * @return
     */
    public List<Game> getGames() {
        return this._games;
    }
}
