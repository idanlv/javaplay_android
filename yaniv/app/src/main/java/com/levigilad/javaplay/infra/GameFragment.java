package com.levigilad.javaplay.infra;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.basegameutils.games.GameHelper;
import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.entities.Game;

import java.util.ArrayList;

/**
 * Created by User on 08/10/2016.
 */

public abstract class GameFragment extends Fragment implements GameHelper.GameHelperListener {
    private static final String TAG = "GameFragment";

    protected static final String INVITEES = "INVITEES";
    protected static final String AUTO_MATCH = "AUTOMATCH";
    private static final int RC_SELECT_PLAYERS = 5001;

    private int mRequestedClients = GameHelper.CLIENT_GAMES;

    private GameHelper mHelper;
    private boolean mDebugLog;
    private ArrayList<String> _invitees;
    private Bundle _autoMatchCriteria;
    private Game _game;

    public GameFragment() {
        super();
    }

    public GameHelper getGameHelper() {
        if (mHelper == null) {
            mHelper = new GameHelper(this.getActivity(), mRequestedClients);
            mHelper.enableDebugLog(mDebugLog);
        }
        return mHelper;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mHelper == null) {
            getGameHelper();
        }

        mHelper.setup(this);

        if (getArguments() != null) {
            _invitees = getArguments().getStringArrayList(INVITEES);
            _autoMatchCriteria = getArguments().getBundle(AUTO_MATCH);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mHelper.onStart(this.getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        mHelper.onStop();
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

            _invitees = invitees;
            _autoMatchCriteria = autoMatchCriteria;

            /*Intent intent = new Intent(this, _game.getActivity());
            intent.putExtra("Invitees", invitees);
            intent.putExtra("AutoMatchCriteria", autoMatchCriteria);

            startActivity(intent);*/
        } else {
            mHelper.onActivityResult(request, response, data);
        }
    }

    protected GoogleApiClient getApiClient() {
        return mHelper.getApiClient();
    }

    protected boolean isSignedIn() {
        return mHelper.isSignedIn();
    }

    protected void beginUserInitiatedSignIn() {
        mHelper.beginUserInitiatedSignIn();
    }

    protected void signOut() {
        mHelper.signOut();
    }

    protected void showAlert(String message) {
        mHelper.makeSimpleDialog(message).show();
    }

    protected void showAlert(String title, String message) {
        mHelper.makeSimpleDialog(title, message).show();
    }

    protected void enableDebugLog(boolean enabled) {
        mDebugLog = true;
        if (mHelper != null) {
            mHelper.enableDebugLog(enabled);
        }
    }

    protected void reconnectClient() {
        mHelper.reconnectClient();
    }

    protected boolean hasSignInError() {
        return mHelper.hasSignInError();
    }

    protected GameHelper.SignInFailureReason getSignInError() {
        return mHelper.getSignInError();
    }

    /**
     * Called when sign-in fails. As a result, a "Sign-In" button can be
     * shown to the user; when that button is clicked, call
     *
     * @link{GamesHelper#beginUserInitiatedSignIn . Note that not all calls
     *                                            to this method mean an
     *                                            error; it may be a result
     *                                            of the fact that automatic
     *                                            sign-in could not proceed
     *                                            because user interaction
     *                                            was required (consent
     *                                            dialogs). So
     *                                            implementations of this
     *                                            method should NOT display
     *                                            an error message unless a
     *                                            call to @link{GamesHelper#
     *                                            hasSignInError} indicates
     *                                            that an error indeed
     *                                            occurred.
     */
    public void onSignInFailed() {
        reconnectClient();
    }

    /** Called when sign-in succeeds. */
    public void onSignInSucceeded() {
        // TODO: Handle resume of game
        Intent intent =
                Games.TurnBasedMultiplayer.getSelectOpponentsIntent(getApiClient(), 1, 7, true);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    };


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
        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(this.getActivity().getApplicationContext());

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

    protected abstract void startMatch(TurnBasedMatch match);

    protected abstract void updateMatch(TurnBasedMatch match);

    protected abstract void updateView(byte[] turnData);

    protected abstract void askForRematch();
}
