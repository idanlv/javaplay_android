package com.levigilad.javaplay;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.basegameutils.games.GameHelper;
import com.levigilad.javaplay.infra.BaseGameFragment;
import com.levigilad.javaplay.infra.entities.Game;
import com.levigilad.javaplay.infra.entities.Playground;

import java.util.Iterator;

public class LeaderboardFragment extends BaseGameFragment {
    private static final String TAG = "LeaderboardFragment";
    private static final String GAME_ID = "GameId";

    private static final int REQUESTED_CLIENTS = GameHelper.CLIENT_GAMES;
    private static final int MAX_RESULTS = 10;

    /**
     * Designer
     */
    private TableLayout mLeaderboardTableLayout;
    private ProgressBar mLeaderboardLoadProgressBar;

    public LeaderboardFragment() {
        super(REQUESTED_CLIENTS);
    }

    public static LeaderboardFragment newInstance(String gameId) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        args.putString(GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        initializeView(view);

        return view;
    }

    private void initializeView(View view) {
        mLeaderboardTableLayout = (TableLayout)view.findViewById(R.id.leaderboard_table_layout);
        mLeaderboardLoadProgressBar =
                (ProgressBar)view.findViewById(R.id.leaderboard_load_progress_bar);
    }

    @Override
    public void onSignInSucceeded() {
        loadTopScores(
                LeaderboardVariant.TIME_SPAN_ALL_TIME,
                LeaderboardVariant.COLLECTION_PUBLIC,
                false);
    }

    private void loadTopScores(int timeFrame, int collectionType, boolean forceReload) {
        Playground playground = Playground.getInstance(this.getActivity().getApplicationContext());

        Game game = playground.getGame(getGameId());

        Games.Leaderboards.loadTopScores(getApiClient(), game.getLeaderboardId(),
                timeFrame, collectionType, MAX_RESULTS, forceReload)
                .setResultCallback(new ResultCallback<Leaderboards.LoadScoresResult>() {
                    @Override
                    public void onResult(@NonNull Leaderboards.LoadScoresResult loadScoresResult) {
                        processResult(loadScoresResult);
                    }
                });

        mLeaderboardLoadProgressBar.setVisibility(View.VISIBLE);
    }

    private void processResult(Leaderboards.LoadScoresResult loadScoresResult) {
        if (!checkStatusCode(loadScoresResult.getStatus().getStatusCode())) {
            return;
        }

        View view = this.getView();

        LeaderboardScoreBuffer scoreBuffer = loadScoresResult.getScores();
        Iterator<LeaderboardScore> it = scoreBuffer.iterator();

        while(it.hasNext()) {
            LeaderboardScore item = it.next();

            TableRow tableRow =
                    (TableRow)view.inflate(this.getActivity(), R.layout.leaderboard_table_row, null);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView playerDisplayNameTextView =
                    (TextView) tableRow.findViewById(R.id.player_display_name_text_view);
            playerDisplayNameTextView.setText(item.getScoreHolderDisplayName());

            TextView playerScoreTextView =
                    (TextView) tableRow.findViewById(R.id.player_score_text_view);
            playerScoreTextView.setText(item.getDisplayScore());

            mLeaderboardTableLayout.addView(tableRow);
        }

        mLeaderboardLoadProgressBar.setVisibility(View.GONE);
    }

    /**
     * Returns false if something went wrong, probably. This should handle
     * more cases, and probably report more accurate results.
     *
     * @param statusCode
     * @return
     */
    private boolean checkStatusCode(int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_NO_DATA:
                // The device was unable to retrieve any data from the network and has no data
                // cached locally
                showErrorMessage(statusCode, R.string.status_multiplayer_error_no_internet_no_cache);
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_STALE_DATA:
                // if the device was unable to retrieve the latest data from the network,
                // but has some data cached locally
                showErrorMessage(statusCode, R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED: {
                // if the client needs to reconnect to the service to access this data
                showErrorMessage(statusCode, R.string.status_client_reconnect_required);
            }
            case GamesStatusCodes.STATUS_LICENSE_CHECK_FAILED: {
                // if the game is not licensed to the user
                showErrorMessage(statusCode, R.string.status_license_failed);
            }
            case GamesStatusCodes.STATUS_INTERNAL_ERROR: {
                // if the game is not licensed to the user
                showErrorMessage(statusCode, R.string.status_internal_error);
            }
            default:
                showErrorMessage(statusCode, R.string.unexpected_status);
                Log.d(TAG, "Did not have warning or string to deal with: " + statusCode);
        }

        return false;
    }
}