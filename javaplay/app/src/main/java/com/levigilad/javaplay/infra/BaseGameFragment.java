package com.levigilad.javaplay.infra;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.basegameutils.games.GameHelper;
import com.levigilad.javaplay.infra.interfaces.OnFragmentInteractionListener;

/**
 * Created by User on 08/10/2016.
 */

public abstract class BaseGameFragment extends Fragment implements GameHelper.GameHelperListener {
    private static final String TAG = "BaseGameFragment";
    private static final String GAME_ID = "GameId";

    private GameHelper mHelper;
    private boolean mDebugLog;
    private int mRequestedClients;

    private OnFragmentInteractionListener mListener;
    private String mGameId;

    public BaseGameFragment(int requestedClients) {
        super();
        mRequestedClients = requestedClients;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mHelper == null) {
            getGameHelper();
        }

        mHelper.setup(this);

        if (getArguments() != null) {
            mGameId = getArguments().getString(GAME_ID);
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

    public GameHelper getGameHelper() {
        if (mHelper == null) {
            mHelper = new GameHelper(this.getActivity(), mRequestedClients);
            mHelper.enableDebugLog(mDebugLog);
        }
        return mHelper;
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public String getGameId() {
        return mGameId;
    }

    protected void showErrorMessage(int statusCode, int stringId) {
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

}
