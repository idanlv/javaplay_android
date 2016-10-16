package com.levigilad.javaplay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.basegameutils.games.BaseGameActivity;
import com.levigilad.javaplay.infra.adapters.GameSelectionRecyclerViewAdapter;
import com.levigilad.javaplay.infra.entities.Game;
import com.levigilad.javaplay.infra.entities.Playground;
import com.levigilad.javaplay.infra.interfaces.OnItemClickListener;

/**
 * This activity is the viewer for picking a game
 */
public class GameSelectionActivity extends BaseGameActivity implements
        OnItemClickListener {

    private static final int RC_SELECT_PLAYERS = 5001;
    private static final String TAG = "GameSelectionActivity";
    private static final String GAME_ID = "GameId";
    private Game _game;

    // Designer members
    private RecyclerView mRecyclerViewGameOptions;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selection);

        initializeViews();
    }

    private void initializeViews() {
        mRecyclerViewGameOptions = (RecyclerView) findViewById(R.id.game_options_recycler_view);
        mRecyclerViewGameOptions.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewGameOptions.setLayoutManager(mLayoutManager);

        mAdapter = new GameSelectionRecyclerViewAdapter(
                Playground.getInstance(this.getApplicationContext()).getGames());
        mRecyclerViewGameOptions.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((GameSelectionRecyclerViewAdapter) mAdapter).setOnClickListener(this);
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
        intent.putExtra(GAME_ID, ((TextView) v.findViewById(R.id.game_name_text_view)).getText());
        startActivity(intent);
    }
}