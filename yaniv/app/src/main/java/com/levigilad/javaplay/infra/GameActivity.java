package com.levigilad.javaplay.infra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.basegameutils.games.BaseGameActivity;
import com.levigilad.javaplay.R;

import java.util.ArrayList;

/**
 * This class represents a basic game activity
 */

public abstract class GameActivity extends BaseGameActivity implements
        ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> {
    private static final String TAG = "GameActivity";
    private ArrayList<String> _invitees;
    private Bundle _autoMatchCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        _invitees = intent.getStringArrayListExtra("Invitees");
        _autoMatchCriteria = intent.getBundleExtra("AutoMatchCriteria");

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        TurnBasedMatch match = result.getMatch();

        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }

        // This indicates that the game data is uninitialized because no player has taken a turn yet
        // Therefore, current player is the first one to take a turn in the match
        if (match.getData() == null) {
            startNewMatch(match);
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
            setWaitView();
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


    private void askForRematch() {
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
                        this,
                        "Stored action for later.  (Please remove this toast before release.)",
                        Toast.LENGTH_SHORT).show();
                // NOTE: This toast is for informative reasons only; please remove
                // it from your final application.
                return true;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(match, statusCode,
                        R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_already_rematched);
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(match, statusCode,
                        R.string.network_error_operation_failed);
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                showErrorMessage(match, statusCode,
                        R.string.client_reconnect_required);
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                showErrorMessage(match, statusCode, R.string.internal_error);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(match, statusCode,
                        R.string.match_error_inactive_match);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_locally_modified);
                break;
            default:
                showErrorMessage(match, statusCode, R.string.unexpected_status);
                Log.d(TAG, "Did not have warning or string to deal with: "
                        + statusCode);
        }

        return false;
    }

    protected void showErrorMessage(TurnBasedMatch match, int statusCode, int stringId) {
        showWarning("Warning", getResources().getString(stringId));
    }

    protected void showWarning(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                    }
                });

        // create alert dialog
        AlertDialog dialog = alertDialogBuilder.create();

        // show it
        dialog.show();
    }

    protected void finishTurn(String matchId, String participantId, byte[] turnData) {
        setTurnView();

        Games.TurnBasedMultiplayer.takeTurn(getApiClient(), matchId, turnData, participantId)
                .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(@NonNull TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
                        processResult(updateMatchResult);
                    }
                });
    }

    @Override
    public void onSignInFailed() {
        reconnectClient();
    }

    @Override
    public void onSignInSucceeded() {
        TurnBasedMatchConfig config = TurnBasedMatchConfig.builder()
                .addInvitedPlayers(_invitees)
                .setAutoMatchCriteria(_autoMatchCriteria)
                .build();

        Games.TurnBasedMultiplayer.createMatch(getApiClient(), config).setResultCallback(this);
    }

    protected abstract void updateMatch(TurnBasedMatch match);

    protected abstract void startNewMatch(TurnBasedMatch match);

    protected abstract void setWaitView();

    protected abstract void setTurnView();
}
