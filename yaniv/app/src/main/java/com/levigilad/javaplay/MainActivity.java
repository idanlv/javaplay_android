package com.levigilad.javaplay;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.games.Games;
import com.google.basegameutils.games.BaseGameActivity;
import com.google.basegameutils.games.BaseGameUtils;

/**
 * Trivial quest. A sample game that sets up the Google Play game services
 * API and allows the user to click a button to win (yes, incredibly epic).
 * Even though winning the game is fun, the purpose of this sample is to
 * illustrate the simplest possible game that uses the API.
 *
 * @author Bruno Oliveira (Google)
 */
public class MainActivity extends BaseGameActivity implements View.OnClickListener {

    private static final String TAG = "TrivialQuest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // set this class to listen for the button clicks
        findViewById(R.id.button_sign_in).setOnClickListener(this);
        findViewById(R.id.button_sign_out).setOnClickListener(this);
        findViewById(R.id.button_win).setOnClickListener(this);
    }

    // Shows the "sign in" bar (explanation and button).
    private void showSignInBar() {
        Log.d(TAG, "Showing sign in bar");
        findViewById(R.id.sign_in_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_out_bar).setVisibility(View.GONE);
    }

    // Shows the "sign out" bar (explanation and button).
    private void showSignOutBar() {
        Log.d(TAG, "Showing sign out bar");
        findViewById(R.id.sign_in_bar).setVisibility(View.GONE);
        findViewById(R.id.sign_out_bar).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_sign_in:
                // Check to see the developer who's running this sample code read the instructions :-)
                // NOTE: this check is here only because this is a sample! Don't include this
                // check in your actual production app.
                if (!BaseGameUtils.verifySampleSetup(this, R.string.app_id,
                        R.string.achievement_trivial_victory)) {
                    Log.w(TAG, "*** Warning: setup problems detected. Sign in may not work!");
                }

                // start the sign-in flow
                Log.d(TAG, "Sign-in button clicked");
                beginUserInitiatedSignIn();
                showSignOutBar();
                break;
            case R.id.button_sign_out:
                // sign out.
                Log.d(TAG, "Sign-out button clicked");
                signOut();
                showSignInBar();
                break;
            case R.id.button_win:
                // win!
                Log.d(TAG, "Win button clicked");
                BaseGameUtils.showAlert(this, getString(R.string.you_won));
                if (isSignedIn()) {
                    // unlock the "Trivial Victory" achievement.
                    Games.Achievements.unlock(getApiClient(),
                            getString(R.string.achievement_trivial_victory));
                }
                break;
        }
    }

    @Override
    public void onSignInFailed() {
        beginUserInitiatedSignIn();
    }

    @Override
    public void onSignInSucceeded() {

    }
}
