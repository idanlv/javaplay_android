package com.levigilad.javaplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.basegameutils.games.BaseGameActivity;
import com.levigilad.javaplay.infra.adapters.GameSelectionRecyclerViewAdapter;
import com.levigilad.javaplay.infra.entities.Game;
import com.levigilad.javaplay.infra.entities.Playground;

import java.util.ArrayList;

/**
 * This activity is the viewer for picking a game
 */
public class GameSelectionActivity extends BaseGameActivity implements
        GameSelectionRecyclerViewAdapter.GameClickedListener {

    private static final int RC_SELECT_PLAYERS = 5001;
    private static final String TAG = "GameSelectionActivity";
    private static final String GAME_ID = "GameId";
    private Game _game;

    // Designer members
    private RecyclerView _gameOptionsRecyclerView;
    private RecyclerView.LayoutManager _layoutManager;
    private RecyclerView.Adapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selection);

        initializeViews();
    }

    private void initializeViews() {
        _gameOptionsRecyclerView = (RecyclerView) findViewById(R.id.game_options_recycler_view);
        _gameOptionsRecyclerView.setHasFixedSize(true);

        _layoutManager = new LinearLayoutManager(this);
        _gameOptionsRecyclerView.setLayoutManager(_layoutManager);

        _adapter = new GameSelectionRecyclerViewAdapter(
                Playground.getInstance(this.getApplicationContext()).getGames());
        _gameOptionsRecyclerView.setAdapter(_adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((GameSelectionRecyclerViewAdapter)_adapter).setOnClickListener(this);
    }

    @Override
    public void onSignInFailed() {
        reconnectClient();
    }

    @Override
    public void onSignInSucceeded() {

    }

    @Override
    public void onItemClicked(int position, View v) {
        Intent intent = new Intent(this, GameOptionsActivity.class);
        intent.putExtra(GAME_ID, position);
        startActivity(intent);
    }
}