package com.levigilad.javaplay;

import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.basegameutils.games.BaseGameActivity;
import com.levigilad.javaplay.infra.LeaderBoardFragment;
import com.levigilad.javaplay.infra.enums.GameOptions;
import com.levigilad.javaplay.infra.interfaces.OnFragmentInteractionListener;
import com.levigilad.javaplay.yaniv.YanivPlayFragment;

import java.util.ArrayList;

public class GameOptionsActivity extends BaseGameActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        OnFragmentInteractionListener {

    private static final int RC_SELECT_PLAYERS = 5001;
    private static final String GAME_ID = "GameId";

    private String mGameId;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private boolean mStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_options);

        if (savedInstanceState != null) {
            mGameId = savedInstanceState.getString(GAME_ID);
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void onStart() {
        super.onStart();

        mStarted = true;
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
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

            YanivPlayFragment fragment = YanivPlayFragment.newInstance(invitees, autoMatchCriteria);

            replaceFragment(fragment);
        } else {
            mHelper.onActivityResult(request, response, data);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        GameOptions option = GameOptions.values()[position];

        switch (option) {
            case PLAY: {
                if (mStarted) {
                    Intent intent = Games.TurnBasedMultiplayer
                            .getSelectOpponentsIntent(getApiClient(), 1, 7, true);
                    startActivityForResult(intent, RC_SELECT_PLAYERS);
                }
                break;
            }
            case LEADERSHIP_BOARD: {
                LeaderBoardFragment fragment = LeaderBoardFragment.newInstance(mGameId);
                replaceFragment(fragment);
                break;
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public void onSignInFailed() {
        reconnectClient();
    }

    @Override
    public void onSignInSucceeded() {
        // TODO
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO
    }
}
