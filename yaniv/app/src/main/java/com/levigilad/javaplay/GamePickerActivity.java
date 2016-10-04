package com.levigilad.javaplay;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.basegameutils.games.BaseGameActivity;
import com.levigilad.javaplay.infra.entities.Game;
import com.levigilad.javaplay.infra.entities.Playground;

import java.util.ArrayList;

/**
 * This activity is the viewer for picking a game
 */
public class GamePickerActivity extends BaseGameActivity implements ListView.OnItemClickListener {

    private static final int RC_SELECT_PLAYERS = 5001;
    private Game _game = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_picker);

        ListView gameOptionsListView = (ListView)findViewById(R.id.game_options_listview);
        gameOptionsListView.setOnItemClickListener(this);

        loadGames(gameOptionsListView);
    }

    private void loadGames(ListView gameOptionsListView) {
        Playground playground = Playground.getInstance();

        ArrayAdapter<Game> adapter = new ArrayAdapter<>(
                this,
                R.layout.game_list_item,
                playground.getGames());

        gameOptionsListView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView gameOptions = (ListView) findViewById(R.id.game_options_listview);
        _game = (Game) gameOptions.getItemAtPosition(position);

        Intent intent =
                Games.TurnBasedMultiplayer.getSelectOpponentsIntent(getApiClient(), 1, 7, true);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    @Override
    public void onSignInFailed() {
        reconnectClient();
    }

    @Override
    public void onSignInSucceeded() {

    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);

        if (request == RC_SELECT_PLAYERS) {
            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            // Get the invitee list.
            final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // Get auto-match criteria.
            Bundle autoMatchCriteria = null;
            int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            Intent intent = new Intent(this, _game.getActivity());
            intent.putExtra("Invitees", invitees);
            intent.putExtra("AutoMatchCriteria", autoMatchCriteria);

            startActivity(intent);
        }
    }
}