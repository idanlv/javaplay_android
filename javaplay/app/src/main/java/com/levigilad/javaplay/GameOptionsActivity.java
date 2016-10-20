package com.levigilad.javaplay;

import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.basegameutils.games.BaseGameActivity;
import com.levigilad.javaplay.infra.PlayFragment;
import com.levigilad.javaplay.infra.enums.GameOptions;
import com.levigilad.javaplay.infra.interfaces.OnFragmentInteractionListener;
import com.levigilad.javaplay.infra.interfaces.OnTurnBasedMatchReceivedListener;
import com.levigilad.javaplay.tictactoe.TicTacToeGameFragment;
import com.levigilad.javaplay.yaniv.YanivPlayFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GameOptionsActivity extends BaseGameActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        OnFragmentInteractionListener,
        OnTurnBasedMatchUpdateReceivedListener  {

    private static final String GAME_ID = "GameId";

    private static final int RC_SELECT_PLAYERS = 5001;
    private final static int RC_LOOK_AT_MATCHES = 10001;
    private static final String TAG = "GameOptionsActivity";

    private String mGameId;
    private OnTurnBasedMatchReceivedListener mListener = null;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_options);

        mGameId = getIntent().getStringExtra(GAME_ID);

        initializeViews();
    }

    private void initializeViews() {
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        setTitle(mGameId);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);

        if (request == RC_SELECT_PLAYERS) {
            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            startNewGame(data);
        } else if (request == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            returnToGame(data);
        } else {
            mHelper.onActivityResult(request, response, data);
        }
    }

    private void startNewGame(Intent data) {
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

    private void returnToGame(Intent data) {
        TurnBasedMatch match = data.getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

        if ((match != null) && (match.getData() != null)) {
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
        }

        Log.d(TAG, "Match = " + match);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (!isSignedIn()) {
            return;
        }

        GameOptions option = GameOptions.values()[position];

        switch (option) {
            case GAMES: {
                // TODO: change to const
                Intent intent = Games.TurnBasedMultiplayer
                        .getSelectOpponentsIntent(getApiClient(), 1, 7, true);
                startActivityForResult(intent, RC_SELECT_PLAYERS);
                break;
            }
            case LEADERBOARD: {
                LeaderboardFragment fragment = LeaderboardFragment.newInstance(mGameId);
                replaceFragment(fragment);
                break;
            }
            case ACHIEVEMENTS: {
                AchievementsFragment fragment = AchievementsFragment.newInstance(mGameId);
                replaceFragment(fragment);
                break;
            }
            case INBOX: {
                Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(getApiClient());
                startActivityForResult(intent, RC_LOOK_AT_MATCHES);
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
        Games.TurnBasedMultiplayer.registerMatchUpdateListener(getApiClient(), this);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO
    }

    @Override
    public void onTurnBasedMatchReceived(TurnBasedMatch turnBasedMatch) {
        if (mListener != null) {
            mListener.onTurnBasedMatchReceived(turnBasedMatch);
        }
    }

    @Override
    public void onTurnBasedMatchRemoved(String s) {
        Log.i(TAG, "onTurnBasedMatchRemoved");
    }
}
