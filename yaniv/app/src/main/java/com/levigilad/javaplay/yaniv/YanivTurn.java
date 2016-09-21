package com.levigilad.javaplay.yaniv;

import com.levigilad.javaplay.infra.BaseTurn;
import com.levigilad.javaplay.infra.entities.CardDeck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by User on 21/09/2016.
 */
public class YanivTurn extends BaseTurn {
    private HashMap<String, CardDeck> _playerDecks = new HashMap<>();
    private CardDeck _cashierDeck = new CardDeck();
    private CardDeck _playedDeck = new CardDeck();

    @Override
    protected JSONObject dataToJson() throws JSONException {
        JSONObject gameData = new JSONObject();

        JSONArray playersArray = new JSONArray();

        for (String playerId : this._playerDecks.keySet()) {
            JSONObject player = new JSONObject();
            player.put("id", playerId);

            player.put("cards", this._playerDecks.get(playerId).toJson());
            playersArray.put(player)
        }

        gameData.put("playersDecks", playersArray);
        gameData.put("cashierDeck", this._cashierDeck.toJson());
        gameData.put("playedDeck", this._playedDeck.toJson());

        return gameData;
    }
}
