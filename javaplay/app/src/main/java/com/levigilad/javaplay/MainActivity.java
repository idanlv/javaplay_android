package com.levigilad.javaplay;

import android.app.Activity;
import android.app.Dialog;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.basegameutils.games.BaseGameActivity;
import com.google.basegameutils.games.BaseGameUtils;
import com.levigilad.javaplay.infra.NetworkStateReceiver;
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
        OnFragmentInteractionListener, NetworkStateReceiver.NetworkStateReceiverListener, View.OnClickListener {
    /**
     * Constants
     */
    private static final String TAG = "MainActivity";
    private static final int RC_SELECT_PLAYERS = 5001;
    private final static int RC_LOOK_AT_MATCHES = 10001;
    private static final int RC_LOOK_AT_LEADERBOARD = 11001;
    private static final int RC_LOOK_AT_ACHIEVEMENTS = 12001;

    /**
     * Members
     */
    private String mGameId;
    private NetworkStateReceiver mNetworkStateReceiver;
    private TurnBasedMatch mMatch;

    /**
     * Designer
     */
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView = null;
    private Toolbar mToolBar = null;
    private DrawerLayout mDrawerLayout;
    private CoordinatorLayout mCoordinatorLayour;
    private Dialog mNetworStatusDialog;
    private ImageView mExpandImageView;
    private ImageView mCollapseImageView;
    private TextView mUsernameTextView;
    private TextView mEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        if (intent != null) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                mMatch =  (TurnBasedMatch) bundle.getParcelable("MATCH");
            }
        }

        mNetworkStateReceiver = new NetworkStateReceiver();
        mNetworkStateReceiver.addListener(this);
        this.registerReceiver(mNetworkStateReceiver,
                new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        mNetworStatusDialog = new Dialog(this);
        mNetworStatusDialog.setContentView(R.layout.dialog_network_status);
        mNetworStatusDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mNetworStatusDialog.setCanceledOnTouchOutside(false);
        mNetworStatusDialog.setCancelable(false);

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

        View header = mNavigationView.getHeaderView(0);

        mExpandImageView = (ImageView) header.findViewById(R.id.expand_button);
        mExpandImageView.setOnClickListener(this);

        mCollapseImageView = (ImageView) header.findViewById(R.id.collapse_button);
        mCollapseImageView.setOnClickListener(this);

        mUsernameTextView = (TextView) header.findViewById(R.id.username);
        mUsernameTextView.setVisibility(View.GONE);

        mEmailTextView = (TextView) header.findViewById(R.id.email);
        mEmailTextView.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        if (isSignedIn()) {
            Games.TurnBasedMultiplayer.unregisterMatchUpdateListener(getApiClient());
        }

        this.unregisterReceiver(mNetworkStateReceiver);

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
                break;
            }
            case R.id.nav_sign_out: {
                finish();
                break;
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
        }
    }

    private void showAchievements() {
        if (isSignedIn()) {
            Intent intent = Games.Achievements.getAchievementsIntent(getApiClient());
            startActivityForResult(intent, RC_LOOK_AT_ACHIEVEMENTS);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.achievements_not_available)).show();
        }
    }

    private void showInbox() {
        if (isSignedIn()) {
            Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(getApiClient());
            startActivityForResult(intent, RC_LOOK_AT_MATCHES);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.inbox_not_available)).show();
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
        } else if (mMatch != null) {
            loadExistingMatch(mMatch);
            mMatch = null;
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

    @Override
    public void networkAvailable() {
        Log.d(TAG, "Entered networkAvailable");
        if (mNetworStatusDialog.isShowing()) {
            mNetworStatusDialog.dismiss();
            reconnectClient();
        }
    }

    @Override
    public void networkUnavailable() {
        Log.d(TAG, "Entered networkUnavailable");

        mNetworStatusDialog.show();
    }

    /**
     * This method handles on click events
     * @param view Viewer which was clicked on
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.expand_button: {
                mNavigationView.getMenu().setGroupVisible(R.id.google_play_services_menu, true);
                mNavigationView.getMenu().setGroupVisible(R.id.games_menu, false);
                mExpandImageView.setVisibility(View.GONE);
                mCollapseImageView.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.collapse_button: {
                mNavigationView.getMenu().setGroupVisible(R.id.google_play_services_menu, false);
                mNavigationView.getMenu().setGroupVisible(R.id.games_menu, true);
                mExpandImageView.setVisibility(View.VISIBLE);
                mCollapseImageView.setVisibility(View.GONE);
                break;
            }
        }
    }
}
