package com.levigilad.javaplay;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.SignInButton;
import com.google.basegameutils.games.BaseGameActivity;
import com.levigilad.javaplay.infra.interfaces.OnFragmentInteractionListener;

/**
 * A simple {@link BaseGameActivity} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoginActivity extends BaseGameActivity implements
        View.OnClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Constants
     */
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_OUT = 6001;
    private static final int REQUEST_READ_PHONE_STATE = 4001;

    /**
     * Designer
     */
    private LinearLayout mSignOutBar;
    private LinearLayout mSignInBar;
    private SignInButton mSignInButton;
    private Button mSignOutButton;

    /**
     * Members
     */
    private Thread mThread;
    private String mIMEI;

    /**
     * Constructor
     */
    public LoginActivity() {
        // Required empty public constructor
    }

    /**
     * OnStart: Handles event of activity start
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     *Handles result of permission request
     * @param requestCode request's code
     * @param permissions permissions requested
     * @param grantResults permission request results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(TAG, "Permission granted");
                } else {
                    Log.d(TAG, "Permission not granted");
                }
                startMainActivity();
                break;

            default:
                break;
        }
    }

    /**
     * onCreate: initializes layout
     * @param bundle
     */
    @Override
    protected void onCreate(Bundle bundle) {
        Log.d(TAG, "onCreate() called");
        super.onCreate(bundle);

        setContentView(R.layout.activity_login);

        initializeView();
    }

    /**
     * Initializes view
     */
    private void initializeView() {
        mSignOutBar = (LinearLayout) findViewById(R.id.sign_out_bar);
        mSignOutButton = (Button) findViewById(R.id.button_sign_out);
        mSignOutButton.setOnClickListener(this);

        mSignInBar = (LinearLayout) findViewById(R.id.sign_in_bar);
        mSignInButton = (SignInButton) findViewById(R.id.button_sign_in);
        mSignInButton.setOnClickListener(this);

        showSignInBar();
    }

    /**
     * Handles results returned from another activity
     * @param request activity request code
     * @param response activity's response
     * @param data
     */
    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);

        switch (request) {
            case RC_SIGN_OUT: {
                signOut();
                getGameHelper().setConnectOnStart(false);
                break;
            }
        }
    }

    /**
     * Handles clicking on viewer
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_sign_in: {
                beginUserInitiatedSignIn();
                showSignOutBar();
                break;
            }
            case R.id.button_sign_out: {
                signOut();
                showSignInBar();
                break;
            }
        }
    }

    /**
     * Handles sign in failure
     */
    @Override
    public void onSignInFailed() {
        showSignInBar();
    }

    /**
     * Handles sign in success
     */
    @Override
    public void onSignInSucceeded() {
        // Check if application has permission for checking IMEI
        int permissionCheck =
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);

        // Request permission if needed
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    REQUEST_READ_PHONE_STATE);
        } else {
            startMainActivity();
        }
    }

    /**
     * Switches activity to MainActivity
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);

        // Checks if application was opened with a connection hint
        if (getGameHelper().hasTurnBasedMatch()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(getString(R.string.loaded_match), getGameHelper().getTurnBasedMatch());
            intent.putExtras(bundle);
        }

        startActivityForResult(intent, RC_SIGN_OUT);
    }

    /**
     * Shows the "sign in" bar (explanation and button).
     */
    private void showSignInBar() {
        Log.d(TAG, "Showing sign in bar");
        mSignInBar.setVisibility(View.VISIBLE);
        mSignOutBar.setVisibility(View.GONE);
    }

    /**
     * Shows the "sign out" bar (explanation and button).
     */
    private void showSignOutBar() {
        Log.d(TAG, "Showing sign out bar");
        mSignInBar.setVisibility(View.GONE);
        mSignOutBar.setVisibility(View.VISIBLE);
    }
}
