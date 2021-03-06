package com.levigilad.javaplay.infra;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.ParticipantResult;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.basegameutils.games.BaseGameActivity;
import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.entities.Turn;
import com.levigilad.javaplay.infra.interfaces.OnMatchInteractionListener;
import com.levigilad.javaplay.infra.interfaces.OnTurnBasedMatchReceivedListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a fragment of a game
 */
public abstract class PlayFragment extends Fragment implements OnTurnBasedMatchReceivedListener {
    /**
     * Constants
     */
    private static final String TAG = "PlayFragment";
    protected static final String INVITEES = "INVITEES";
    protected static final String AUTO_MATCH = "AUTO_MATCH";
    protected static final String MATCH_ID = "MATCH_ID";
    private static final String GAME_ID = "GameId";

    /**
     * Members
     */
    private ArrayList<String> mInvitees;
    private Bundle mAutoMatchCriteria;
    private int mScreenOrientation;
    protected TurnBasedMatch mMatch;
    protected Turn mTurnData;
    protected BaseGameActivity mAppContext;
    private String mGameId;
    private OnMatchInteractionListener mMatchListener;

    /**
     * Constructor: Creates a game fragment
     * @param turnData A turn data to start with
     * @param screenOrientation as screen orientation
     */
    public PlayFragment(Turn turnData, int screenOrientation) {
        super();
        mTurnData = turnData;
        mScreenOrientation = screenOrientation;
    }

    /**
     * Set the caller Activity as context
     * @param context Activity or fragment that current fragment was attached to
     */
    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "Entered onAttach()");
        super.onAttach(context);

        try {
            mAppContext = (BaseGameActivity)context;
            mMatchListener = (OnMatchInteractionListener)context;
            // Change application orientation according to game specifications
            mAppContext.setRequestedOrientation(mScreenOrientation);
        } catch (Exception ex) {
            throw new RuntimeException("Activity must be sub class of BaseGameActivity");
        }
        Log.d(TAG, "Exited onAttach()");
    }

    /**
     * On Create
     * @param savedInstanceState instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Entered onCreate()");
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        // Extract arguments from the fragment
        if (bundle != null) {
            mMatch = bundle.getParcelable(MATCH_ID);
            mGameId = bundle.getString(GAME_ID);

            // A new match will be initiated
            if (mMatch == null) {
                mInvitees = bundle.getStringArrayList(INVITEES);
                mAutoMatchCriteria = bundle.getBundle(AUTO_MATCH);
            }
        }

        mAppContext.setTitle(mGameId);

        Log.d(TAG, "Exited onCreate()");
    }

    @Override
    public void onStart() {
        super.onStart();

        // We're starting a new match
        if (mMatch == null) {
            Log.d(TAG, "Run match initiation");
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
        } // We have an existing game
        else {
            Log.d(TAG, "Run match load");
            mAppContext.addListenerForMatchUpdates(this, mMatch.getMatchId());
            mMatchListener.onMatchLoaded(mMatch);

            // This is a rematch
            if (mMatch.getData() == null) {
                handleMatchStart();
            } else {
                handleMatchUpdate();
            }
        }
    }

    /**
     * Returns false if something went wrong, probably. This should handle
     * more cases, and probably report more accurate results.
     * @param match match instance
     * @param statusCode status code of result
     * @return True if no error has occurred, otherwise false
     */
    private boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                // This is OK; the action is stored by Google Play Services and will
                // be dealt with later.
                Toast.makeText(
                        this.getActivity(),
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
                Log.d(TAG, "Did not have warning or string to deal with: " + statusCode);
        }

        return false;
    }

    /**
     * OnDetach
     * Handles event of fragment detaching from activity
     */
    @Override
    public void onDetach() {
        // Don't get more game updates
        mAppContext.removeListenerForMatchUpdates(this);

        // Allow user to change screen orientation
        mAppContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);

        // Continue with normal detach process
        super.onDetach();
    }

    /**
     * Performs initiate of a match
     * @param result Initiate match result
     */
    public void processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        mMatch = result.getMatch();

        if (!checkStatusCode(mMatch, result.getStatus().getStatusCode())) {
            return;
        }

        mMatchListener.onMatchLoaded(mMatch);

        mAppContext.addListenerForMatchUpdates(this, mMatch.getMatchId());

        handleMatchStart();
    }

    /**
     * Handles match start
     */
    private void handleMatchStart() {
        // This indicates that the game data is uninitialized because no player has taken a turn yet
        // Therefore, current player is the first one to take a turn in the match
        if (mMatch.getData() == null) {
            mTurnData = null;
            startMatch();

            String nextParticipantId = getNextParticipantId();
            finishTurn(nextParticipantId);
        }
        // This indicates that the game has already started and the game data is already initialized,
        // Therefore, we need to make sure your game does not reinitialize the data
        else {
            handleMatchUpdate();
        }
    }

    /**
     * Process update match result
     * We reach this when we or any other participant updates the match
     * @param result Update match result
     */
    protected void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
        mMatch = result.getMatch();

        if (!checkStatusCode(mMatch, result.getStatus().getStatusCode())) {
            return;
        }

        handleMatchUpdate();
    }

    /**
     * Finishes a turn and update Google Games
     * @param nextParticipantId Next participant id
     */
    protected void finishTurn(String nextParticipantId) {
        try {
            updateView();

            Games.TurnBasedMultiplayer.takeTurn(getApiClient(), mMatch.getMatchId(), mTurnData.export(), nextParticipantId)
                    .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                        @Override
                        public void onResult(@NonNull TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
                            processResult(updateMatchResult);
                        }
                    });
        } catch (JSONException e) {
            // This shouldn't be reached on production version
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Finishes a match and update Google Games
     * @param results Game result for each participant
     */
    protected void finishMatch(List<ParticipantResult> results) {
        try {
            Games.TurnBasedMultiplayer.finishMatch(getApiClient(), mMatch.getMatchId(),
                    mTurnData.export(), results).setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                @Override
                public void onResult(@NonNull TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
                    processResult(updateMatchResult);
                }
            });
        } catch (JSONException e) {
            // This shouldn't be reached on production version
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Finishes a match and update Google Games
     */
    private void finishMatch() {
        Games.TurnBasedMultiplayer.finishMatch(getApiClient(), mMatch.getMatchId()).setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
            @Override
            public void onResult(@NonNull TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
                processResult(updateMatchResult);
            }
        });
    }

    /**
     * Get the next participant. In this function, we assume that we are
     * round-robin, with all known players going before all automatch players.
     * This is not a requirement; players can go in any order. However, you can
     * take turns in any order.
     *
     * @return participantId of next player, or null if auto-matching
     */
    public String getNextParticipantId() {
        String myParticipantId = getCurrentParticipantId();

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

    /**
     * Returns the participant id for the current player
     * @return Participant id
     */
    public String getCurrentParticipantId() {
        String playerId = Games.Players.getCurrentPlayerId(getApiClient());
        return mMatch.getParticipantId(playerId);
    }

    /**
     * This method is called whenever a match update is received
     * @param match The updated match
     */
    public void onTurnBasedMatchReceived(TurnBasedMatch match) {
        mMatch = match;

        handleMatchUpdate();
    }

    /**
     * Handle any match update that was received from Google Games
     */
    private void handleMatchUpdate() {
        try {
            if (mMatch.getData() != null) {
                mTurnData.update(mMatch.getData());
                updateView();
            }

            if (mMatch.getStatus() == TurnBasedMatch.MATCH_STATUS_COMPLETE) {
                notifyParticipantResult(mMatch.getParticipant(getCurrentParticipantId()).getResult());
            }

            // Start my turn
            if (mMatch.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {
                if (mMatch.getStatus() != TurnBasedMatch.MATCH_STATUS_COMPLETE) {
                    mTurnData.increaseTurnCounter();
                    startTurn();
                } else {
                    finishMatch();
                }
            } else {
                // Checks if the user can ask for a rematch.
                // This can only happen when the game is completed
                if (mMatch.canRematch()) {
                    askForRematch();
                }
            }
        } catch (JSONException e) {
            // This shouldn't be reached on production version
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Notify user on its' result
     * @param participantResult result
     */
    protected abstract void notifyParticipantResult(ParticipantResult participantResult);

    /**
     * Asks the player if he wants to rematch and start rematch if user agrees
     */
    private void askForRematch() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());

        alertDialogBuilder.setMessage(getString(R.string.rematch_question));

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.rematch_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                rematch();
                            }
                        })
                .setNegativeButton(getString(R.string.rematch_no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

        alertDialogBuilder.show();
    }

    /**
     * Start rematch for current match
     */
    private void rematch() {
        if (mMatch.canRematch()) {
            Games.TurnBasedMultiplayer.rematch(getApiClient(), mMatch.getMatchId()).setResultCallback(
                    new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                            processResult(result);
                        }
                    });
            mMatch = null;
        }
    }

    /**
     * Starts a match
     */
    protected abstract void startMatch();

    /**
     * Starts a turn
     */
    protected abstract void startTurn();

    /**
     * Updates player's view according to turn data
     */
    protected abstract void updateView();

    public GoogleApiClient getApiClient() {
        return mAppContext.getGameHelper().getApiClient();
    }

    /**
     * Shows an error message
     * @param statusCode Status code of the error
     * @param stringId The resource id for the message
     */
    protected void showErrorMessage(int statusCode, int stringId) {
        showWarning("Warning", getResources().getString(stringId));
    }

    /**
     * Shows a warning
     * @param title Warning's title
     * @param message Warning' message
     */
    protected void showWarning(String title, String message) {
        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(this.getActivity());

        // Set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // Set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // If this button is clicked, close
                        // current activity
                    }
                });

        // create alert dialog
        AlertDialog dialog = alertDialogBuilder.create();

        // show it
        dialog.show();
    }
}