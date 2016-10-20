package com.levigilad.javaplay.infra;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.basegameutils.games.GameHelper;
import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.entities.Game;

import java.util.ArrayList;

public abstract class PlayFragment extends BaseGameFragment {
    private static final String TAG = "PlayFragment";

    protected static final String INVITEES = "INVITEES";
    protected static final String AUTO_MATCH = "AUTOMATCH";

    private static final int REQUESTED_CLIENTS = GameHelper.CLIENT_GAMES;
    private static final int RC_SELECT_PLAYERS = 5001;

    private ArrayList<String> _invitees;
    private Bundle _autoMatchCriteria;
    private Game _game;

    public PlayFragment() {
        super(REQUESTED_CLIENTS);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            _invitees = getArguments().getStringArrayList(INVITEES);
            _autoMatchCriteria = getArguments().getBundle(AUTO_MATCH);
        }
    }

    public void onSignInSucceeded() {
        TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                .addInvitedPlayers(_invitees)
                .setAutoMatchCriteria(_autoMatchCriteria)
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
        TurnBasedMatch match = result.getMatch();

        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }

        // This indicates that the game data is uninitialized because no player has taken a turn yet
        // Therefore, current player is the first one to take a turn in the match
        if (match.getData() == null) {
            startMatch(match);
        }
        // This indicates that the game has already started and the game data is already initialized,
        // Therefore, we need to make sure your game does not reinitialize the data
        else {
            updateMatch(match);
        }
    }

    protected void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
        TurnBasedMatch match = result.getMatch();

        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }

        if (match.canRematch()) {
            askForRematch();
        }

        if (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {
            updateMatch(match);
        } else {
            updateView(match.getData());
        }

    }

    protected void processResult(TurnBasedMultiplayer.CancelMatchResult result) {
        if (!checkStatusCode(null, result.getStatus().getStatusCode())) {
            return;
        }

        // TODO
    }

    protected void processResult(TurnBasedMultiplayer.LeaveMatchResult result) {
        TurnBasedMatch match = result.getMatch();

        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }

        // TODO
    }

    protected void finishTurn(String matchId, String participantId, byte[] turnData) {
        updateView(turnData);

        Games.TurnBasedMultiplayer.takeTurn(getApiClient(), matchId, turnData, participantId)
                .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(@NonNull TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
                        processResult(updateMatchResult);
                    }
                });
    }

    protected void finishMatch(String matchId, byte[] turnData) {
        Games.TurnBasedMultiplayer.finishMatch(getApiClient(), matchId, turnData);
    }

    protected abstract void startMatch(TurnBasedMatch match);

    protected abstract void updateMatch(TurnBasedMatch match);

    protected abstract void updateView(byte[] turnData);

    protected abstract void askForRematch();
}
