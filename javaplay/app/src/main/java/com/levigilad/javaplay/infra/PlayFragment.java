package com.levigilad.javaplay.infra;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.ParticipantResult;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.basegameutils.games.GameHelper;
import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.entities.Game;
import com.levigilad.javaplay.infra.entities.Turn;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayFragment extends BaseGameFragment {
    private static final String TAG = "PlayFragment";

    protected static final String INVITEES = "INVITEES";
    protected static final String AUTO_MATCH = "AUTOMATCH";
    protected static final String MATCH_ID = "MATCH_ID";

    private static final int REQUESTED_CLIENTS = GameHelper.CLIENT_GAMES;

    private ArrayList<String> mInvitees;
    private Bundle mAutoMatchCriteria;

    private Game _game;

    private String mMatchId;
    protected TurnBasedMatch mMatch;
    protected Turn mTurnData;

    public PlayFragment() {
        super(REQUESTED_CLIENTS);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle bundle = getArguments();

            mMatchId = bundle.getString(MATCH_ID);

            if (mMatchId == null) {
                mInvitees = getArguments().getStringArrayList(INVITEES);
                mAutoMatchCriteria = getArguments().getBundle(AUTO_MATCH);
            }
        }
    }

    public void onSignInSucceeded() {
        if (mMatchId == null) {
            TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(mInvitees)
                    .setAutoMatchCriteria(mAutoMatchCriteria)
                    .build();

            // Create and start the match.
            Games.TurnBasedMultiplayer
                    .createMatch(getApiClient(), tbmc)
                    .setResultCallback(new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                        @Override
                        public void onResult(@NonNull TurnBasedMultiplayer.InitiateMatchResult initiateMatchResult) {
                            processResult(initiateMatchResult);
                        }
                    });
        } else {
            Games.TurnBasedMultiplayer.loadMatch(getApiClient(), mMatchId)
                    .setResultCallback(new ResultCallback<TurnBasedMultiplayer.LoadMatchResult>() {
                        @Override
                        public void onResult(@NonNull TurnBasedMultiplayer.LoadMatchResult loadMatchResult) {
                            processResult(loadMatchResult);
                        }
                    });
        }
    }

    /**
     * Returns false if something went wrong, probably. This should handle
     * more cases, and probably report more accurate results.
     *
     * @param match
     * @param statusCode
     * @return
     */
    private boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                // This is OK; the action is stored by Google Play Services and will
                // be dealt with later.
                Toast.makeText(
                        this.getActivity().getApplicationContext(),
                        "Stored action for later.  (Please remove this toast before release.)",
                        Toast.LENGTH_SHORT).show();
                // NOTE: This toast is for informative reasons only; please remove
                // it from your final application.
                return true;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(statusCode, R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(statusCode, R.string.match_error_already_rematched);
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(statusCode, R.string.network_error_operation_failed);
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                showErrorMessage(statusCode, R.string.status_client_reconnect_required);
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                showErrorMessage(statusCode, R.string.status_internal_error);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(statusCode, R.string.match_error_inactive_match);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(statusCode, R.string.match_error_locally_modified);
                break;
            default:
                showErrorMessage(statusCode, R.string.unexpected_status);
                Log.d(TAG, "Did not have warning or string to deal with: "
                        + statusCode);
        }

        return false;
    }


    public void processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        mMatch = result.getMatch();

        if (!checkStatusCode(mMatch, result.getStatus().getStatusCode())) {
            return;
        }

        // This indicates that the game data is uninitialized because no player has taken a turn yet
        // Therefore, current player is the first one to take a turn in the match
        if (mMatch.getData() == null) {
            try {
                byte[] turnData = startMatch();
                mTurnData.update(turnData);

                String nextParticipantId = getNextParticipantId();
                finishTurn(nextParticipantId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // This indicates that the game has already started and the game data is already initialized,
        // Therefore, we need to make sure your game does not reinitialize the data
        else {
            try {
                mTurnData.update(mMatch.getData());
                updateView();
                startTurn();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
        mMatch = result.getMatch();

        if (!checkStatusCode(mMatch, result.getStatus().getStatusCode())) {
            return;
        }

        if (mMatch.canRematch()) {
            askForRematch();
        }

        try {
            mTurnData.update(mMatch.getData());
            updateView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processResult(TurnBasedMultiplayer.LoadMatchResult result) {
        mMatch = result.getMatch();

        if (!checkStatusCode(mMatch, result.getStatus().getStatusCode())) {
            return;
        }

        try {
            mTurnData.update(mMatch.getData());
            updateView();

            if (mMatch.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {
                startTurn();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void processResult(TurnBasedMultiplayer.CancelMatchResult result) {
        if (!checkStatusCode(null, result.getStatus().getStatusCode())) {
            return;
        }

        // TODO
    }

    protected void processResult(TurnBasedMultiplayer.LeaveMatchResult result) {
        mMatch = result.getMatch();

        if (!checkStatusCode(mMatch, result.getStatus().getStatusCode())) {
            return;
        }

        // TODO
    }

    protected void finishTurn(String participantId) {
        try {
            updateView();

            Games.TurnBasedMultiplayer.takeTurn(getApiClient(), mMatch.getMatchId(), mTurnData.export(), participantId)
                    .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                        @Override
                        public void onResult(@NonNull TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
                            processResult(updateMatchResult);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void finishMatch(List<ParticipantResult> results) {
        try {
            Games.TurnBasedMultiplayer.finishMatch(getApiClient(), mMatch.getMatchId(),
                    mTurnData.export(), results);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the next participant. In this function, we assume that we are
     * round-robin, with all known players going before all automatch players.
     * This is not a requirement; players can go in any order. However, you can
     * take turns in any order.
     *
     * @return participantId of next player, or null if automatching
     */
    public String getNextParticipantId() {

        String playerId = Games.Players.getCurrentPlayerId(getApiClient());
        String myParticipantId = mMatch.getParticipantId(playerId);

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }

    protected String getCurrentParticipantId() {
        String playerId = Games.Players.getCurrentPlayerId(getApiClient());
        return mMatch.getParticipantId(playerId);
    }

    protected abstract byte[] startMatch();

    protected abstract void startTurn();

    protected abstract void updateView();

    protected abstract void askForRematch();
}
