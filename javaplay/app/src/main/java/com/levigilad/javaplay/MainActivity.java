package com.levigilad.javaplay;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.basegameutils.games.BaseGameActivity;
import com.levigilad.javaplay.infra.PlayFragment;
import com.levigilad.javaplay.infra.interfaces.OnFragmentInteractionListener;
import com.levigilad.javaplay.infra.interfaces.OnGameSelectedListener;
import com.levigilad.javaplay.infra.interfaces.OnTurnBasedMatchReceivedListener;
import com.levigilad.javaplay.tictactoe.TicTacToeGameFragment;
import com.levigilad.javaplay.yaniv.YanivPlayFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends BaseGameActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnTurnBasedMatchUpdateReceivedListener,
        OnGameSelectedListener,
        OnFragmentInteractionListener {
    /**
     * Constants
     */
    private static final int RC_SELECT_PLAYERS = 5001;
    private final static int RC_LOOK_AT_MATCHES = 10001;
    private static final int RC_LOOK_AT_LEADERBOARD = 11001;
    private static final int RC_LOOK_AT_ACHIEVEMENTS = 12001;
    private static final String TAG = "MainActivity";

    /**
     * Members
     */
    private String mGameId;
    private OnTurnBasedMatchReceivedListener mListener = null;

    /**
     * Designer
     */
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView = null;
    private Toolbar mToolBar = null;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        mNavigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            showLogin();
        }
    }

    @Override
    protected void onStop() {
        if (isSignedIn()) {
            Games.TurnBasedMultiplayer.unregisterMatchUpdateListener(getApiClient());
        }

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_inbox: {
                Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(getApiClient());
                startActivityForResult(intent, RC_LOOK_AT_MATCHES);
                break;
            }
            case R.id.nav_achievements: {
                Intent intent = Games.Achievements.getAchievementsIntent(getApiClient());
                startActivityForResult(intent, RC_LOOK_AT_ACHIEVEMENTS);

                break;
            }
            case R.id.nav_leaderboards: {
                Intent intent = Games.Leaderboards.getAllLeaderboardsIntent(getApiClient());
                startActivityForResult(intent, RC_LOOK_AT_LEADERBOARD);
                break;
            }
            case R.id.nav_new_match: {
                showGameOptions();
            }
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);

        if (request == RC_SELECT_PLAYERS) {
            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            setTitle(mGameId);
            startNewMatch(data);
        } else if (request == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            setTitle(mGameId);
            // TODO: Does this handle rematch?
            enterExistingMatch(data);
        } else if (request == RC_LOOK_AT_LEADERBOARD) {
            // Does nothing
        } else if (request == RC_LOOK_AT_ACHIEVEMENTS) {
            // Does nothing
        }
    }

    @Override
    public void onSignInFailed() {
        reconnectClient();
        Toast.makeText(this, "Reconnecting to Google Servers", Toast.LENGTH_LONG);
    }

    @Override
    public void onSignInSucceeded() {
        Games.TurnBasedMultiplayer.registerMatchUpdateListener(getApiClient(), this);
        Toast.makeText(this, "Connected to Google Servers", Toast.LENGTH_LONG);

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if ((fragment instanceof LoginFragment) && (fragment.isVisible())) {
            showGameOptions();
        }
    }

    @Override
    public void onTurnBasedMatchRemoved(String s) {
        // TODO: Handle this in some way
    }

    @Override
    public void onTurnBasedMatchReceived(TurnBasedMatch turnBasedMatch) {
        if (mListener != null) {
            mListener.onTurnBasedMatchReceived(turnBasedMatch);
        }
    }

    @Override
    public void onGameSelected(String gameId) {
        mGameId = gameId;

        Intent intent = Games.TurnBasedMultiplayer
                .getSelectOpponentsIntent(getApiClient(), 1, 7, true);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    private void startNewMatch(Intent data) {
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

        PlayFragment fragment = null;

        if (mGameId.equals(getString(R.string.yaniv_game_id))) {
            fragment = YanivPlayFragment.newInstance(invitees, autoMatchCriteria);
        } else if (mGameId.equals(getString(R.string.tictactoe_game_id))) {
            fragment = TicTacToeGameFragment.newInstance(invitees, autoMatchCriteria);
        }

        mListener = fragment;

        replaceFragment(fragment);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void enterExistingMatch(Intent data) {
        TurnBasedMatch match = data.getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

        if (match != null) {
            if (match.getData() != null) {
                try {
                    JSONObject turnData = new JSONObject(new String(match.getData()));
                    mGameId = turnData.getString("game_id");

                    PlayFragment fragment = null;

                    if (mGameId.equals(getString(R.string.yaniv_game_id))) {
                        fragment = YanivPlayFragment.newInstance(match);
                    } else if (mGameId.equals(getString(R.string.tictactoe_game_id))) {
                        fragment = TicTacToeGameFragment.newInstance(match);
                    }

                    mListener = fragment;

                    replaceFragment(fragment);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // TODO: rematch?
            }
        }

        Log.d(TAG, "Match = " + match);
    }

    private void replaceFragment(Fragment fragment) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showGameOptions() {
        GamePossibilitiesFragment fragment = GamePossibilitiesFragment.newInstance();
        setTitle(getString(R.string.pick_a_game));
        replaceFragment(fragment);
    }

    private void showLogin() {
        LoginFragment fragment = LoginFragment.newInstance();
        setTitle(getString(R.string.app_name));
        replaceFragment(fragment);
    }
}
