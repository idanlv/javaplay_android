package com.levigilad.javaplay.infra;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.basegameutils.games.GameHelper;
import com.levigilad.javaplay.infra.interfaces.OnFragmentInteractionListener;

public abstract class BaseGameFragment extends Fragment implements GameHelper.GameHelperListener {
    /**
     * Constants
     */
    private static final String TAG = "BaseGameFragment";
    private static final String GAME_ID = "GameId";

    /**
     * Members
     */
    private GameHelper mHelper;
    private boolean mDebugLog;
    private int mRequestedClients;
    protected OnFragmentInteractionListener mListener;
    private String mGameId;

    /**
     * Constructor: Creates a basic game fragment
     * @param requestedClients Requested clients
     */
    public BaseGameFragment(int requestedClients) {
        super();
        mRequestedClients = requestedClients;
    }

    /**
     * On Create
     * @param savedInstanceState instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mHelper == null) {
            getGameHelper();
        }

        mHelper.setup(this);

        // Handle arguments that were attached to the fragment
        if (getArguments() != null) {
            Bundle bundle = getArguments();

            mGameId = bundle.getString(GAME_ID);
        }
    }

    /**
     * On Start
     */
    @Override
    public void onStart() {
        super.onStart();

        mHelper.onStart(this.getActivity());
    }

    /**
     * On Stop
     */
    @Override
    public void onStop() {
        super.onStop();
        mHelper.onStop();
    }

    /**
     * Retrieves a game helper instance
     * @return GameHelper instance
     */
    public GameHelper getGameHelper() {
        if (mHelper == null) {
            mHelper = new GameHelper(this.getActivity(), mRequestedClients);
            mHelper.enableDebugLog(mDebugLog);
        }
        return mHelper;
    }

    /**
     * Retrieves a GoogleApiClient instance
     * @return GoogleApiClient instance
     */
    protected GoogleApiClient getApiClient() {
        return mHelper.getApiClient();
    }

    /**
     * Checks if the user is signed in
     * @return True if signed in, otherwise false
     */
    protected boolean isSignedIn() {
        return mHelper.isSignedIn();
    }

    /**
     * Begins user sign in
     */
    protected void beginUserInitiatedSignIn() {
        mHelper.beginUserInitiatedSignIn();
    }

    /**
     * Signs out of user account
     */
    protected void signOut() {
        mHelper.signOut();
    }

    /**
     * Shows alert
     * @param message Alert's message
     */
    protected void showAlert(String message) {
        mHelper.makeSimpleDialog(message).show();
    }

    /**
     * Show alert
     * @param title Alert's title
     * @param message Alert's message
     */
    protected void showAlert(String title, String message) {
        mHelper.makeSimpleDialog(title, message).show();
    }

    /**
     * Enable log
     * @param enabled Whether to enable or not
     */
    protected void enableDebugLog(boolean enabled) {
        mDebugLog = true;
        if (mHelper != null) {
            mHelper.enableDebugLog(enabled);
        }
    }

    /**
     * Reconnect GoogleApiClient
     */
    protected void reconnectClient() {
        mHelper.reconnectClient();
    }

    /**
     * Checks whether sign in had errors
     * @return True if an error happened, otherwise false
     */
    protected boolean hasSignInError() {
        return mHelper.hasSignInError();
    }

    /**
     * Retrieves reason for sign in failure
     * @return Reason for sign in failure
     */
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

    /**
     * On Attach
     * @param context Activity or fragment that current fragment was attached to
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verifies the context implements needed listener
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * On Detach
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Getter
     * @return game id
     */
    public String getGameId() {
        return mGameId;
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
