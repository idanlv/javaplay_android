package com.levigilad.javaplay;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.basegameutils.games.BaseGameActivity;
import com.google.basegameutils.games.BaseGameUtils;
import com.levigilad.javaplay.infra.ActivityUtils;
import com.levigilad.javaplay.infra.PlayFragment;
import com.levigilad.javaplay.infra.entities.Game;
import com.levigilad.javaplay.infra.interfaces.OnFragmentInteractionListener;
import com.levigilad.javaplay.infra.interfaces.OnGameSelectedListener;
import com.levigilad.javaplay.tictactoe.TicTacToeGameFragment;
import com.levigilad.javaplay.yaniv.YanivPlayFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends BaseGameActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnGameSelectedListener,
        OnFragmentInteractionListener, NetworkStateReceiver.NetworkStateReceiverListener {
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
    private NetworkStateReceiver mNetworkStateReceiver;

    /**
     * Designer
     */
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView = null;
    private Toolbar mToolBar = null;
    private DrawerLayout mDrawerLayout;
    private CoordinatorLayout mCoordinatorLayour;
    private TextProgressBar mConnectionProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            showLogin();
        }

        mNetworkStateReceiver = new NetworkStateReceiver();
        mNetworkStateReceiver.addListener(this);
        this.registerReceiver(mNetworkStateReceiver,
                new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        mConnectionProgressBar =
                (TextProgressBar) findViewById(R.id.internet_connection_progress_bar);
        mConnectionProgressBar.setVisibility(View.GONE);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        mNavigationView.setNavigationItemSelectedListener(this);

        mCoordinatorLayour = (CoordinatorLayout)findViewById(R.id.app_coordinator_layout);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_inbox: {
                showInbox();
                break;
            }
            case R.id.nav_achievements: {
                showAchievements();

                break;
            }
            case R.id.nav_leaderboards: {
                showLeaderboards();
                break;
            }
            case R.id.nav_new_match: {
                showGameOptions();
            }
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLeaderboards() {
        if (isSignedIn()) {
            Intent intent = Games.Leaderboards.getAllLeaderboardsIntent(getApiClient());
            startActivityForResult(intent, RC_LOOK_AT_LEADERBOARD);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.leaderboards_not_available)).show();
            showLogin();
        }
    }

    private void showAchievements() {
        if (isSignedIn()) {
            Intent intent = Games.Achievements.getAchievementsIntent(getApiClient());
            startActivityForResult(intent, RC_LOOK_AT_ACHIEVEMENTS);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.achievements_not_available)).show();
            showLogin();
        }
    }

    private void showInbox() {
        if (isSignedIn()) {
            Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(getApiClient());
            startActivityForResult(intent, RC_LOOK_AT_MATCHES);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.inbox_not_available)).show();
            showLogin();
        }
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);

        if (request == RC_SELECT_PLAYERS) {
            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            startNewMatch(data);
        } else if (request == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            // TODO: Does this handle rematch?
            loadExistingMatch(
                    (TurnBasedMatch) data.getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH));
        } else if (request == RC_LOOK_AT_LEADERBOARD) {
            // TODO: Handle errors
        } else if (request == RC_LOOK_AT_ACHIEVEMENTS) {
            // TODO: Handle errors
        }
    }

    @Override
    public void onSignInFailed() {
        reconnectClient();
    }

    @Override
    public void onSignInSucceeded() {
        Log.d(TAG, "Entered onSignInSucceeded()");
        Games.TurnBasedMultiplayer.registerMatchUpdateListener(getApiClient(), this);

        if (getGameHelper().hasTurnBasedMatch()) {
            loadExistingMatch(getGameHelper().getTurnBasedMatch());
        }

        Log.d(TAG, "Exited onSignInSucceeded()");
    }

    @Override
    public void onGameSelected(Game game) {
        mGameId = game.getGameId();

        Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(
                getApiClient(),
                game.getMinNumberOfPlayers() - 1,
                game.getMaxNumberOfPlayers() - 1,
                game.getmAllowAutoMatch());
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

        replaceFragment(fragment);
    }

    @Override
    public void onFragmentInteraction(String message) {
        
    }

    private void loadExistingMatch(TurnBasedMatch match) {
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
        GamesFragment fragment = GamesFragment.newInstance();
        replaceFragment(fragment);
    }

    private void showLogin() {
        LoginFragment fragment = LoginFragment.newInstance();
        setTitle(getString(R.string.app_name));
        replaceFragment(fragment);
    }

    @Override
    public void networkAvailable() {
        Log.d(TAG, "Entered networkAvailable");
        reconnectClient();
        mConnectionProgressBar.setVisibility(View.GONE);
        ActivityUtils.setEnabledRecursively(mDrawerLayout, true);
    }

    @Override
    public void networkUnavailable() {
        Log.d(TAG, "Entered networkUnavailable");
        mConnectionProgressBar.setVisibility(View.VISIBLE);
        ActivityUtils.setEnabledRecursively(mDrawerLayout, false);
    }
}
