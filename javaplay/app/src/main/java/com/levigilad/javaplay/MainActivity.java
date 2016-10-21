package com.levigilad.javaplay;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.basegameutils.games.BaseGameActivity;

public class MainActivity extends BaseGameActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    /**
     * Designer
     */
    private com.google.android.gms.common.SignInButton mBtnSignIn;
    private Button mBtnSignOut;
    private Button mBtnPlay;
    private ProgressBar mProgressBarConnect;
    private LinearLayout mBarSignIn;
    private LinearLayout mBarSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeViews();
    }

    private void initializeViews() {
        mBarSignIn = (LinearLayout) findViewById(R.id.sign_in_bar);

        mBarSignOut = (LinearLayout) findViewById(R.id.sign_out_bar);

        mBtnSignIn = (com.google.android.gms.common.SignInButton) findViewById(R.id.button_sign_in);
        mBtnSignIn.setOnClickListener(this);

        mBtnSignOut = (Button) findViewById(R.id.button_sign_out);
        mBtnSignOut.setOnClickListener(this);

        mBtnPlay = (Button) findViewById(R.id.btn_play);
        mBtnPlay.setOnClickListener(this);
        mBtnPlay.setVisibility(View.GONE);

        mProgressBarConnect = (ProgressBar) findViewById(R.id.progressBar_connect);
        mProgressBarConnect.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the "sign in" bar (explanation and button)
     */
    private void showSignInBar() {
        Log.d(TAG, "Showing sign in bar");
        mBarSignIn.setVisibility(View.VISIBLE);
        mBarSignOut.setVisibility(View.GONE);
    }

    /**
     * Shows the "sign out" bar (explanation and button)
     */
    private void showSignOutBar() {
        Log.d(TAG, "Showing sign out bar");
        mBarSignIn.setVisibility(View.GONE);
        mBarSignOut.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_sign_in:
                btnSignIn_OnClick();
                break;
            case R.id.button_sign_out:
                btnSignOut_OnClick();
                break;
            case R.id.btn_play:
                btnPlay_OnClick();
                break;
        }
    }

    private void btnSignOut_OnClick() {
        // sign out.
        Log.d(TAG, "Sign-out button clicked");
        signOut();
        showSignInBar();
    }

    private void btnSignIn_OnClick() {
        Log.d(TAG, "Sign-in button clicked");
        beginUserInitiatedSignIn();
        mProgressBarConnect.setVisibility(View.VISIBLE);
        mBtnPlay.setVisibility(View.GONE);
        showSignOutBar();
    }

    private void btnPlay_OnClick() {
        Intent i = new Intent(this, GameOptionsActivity.class);
        startActivity(i);
    }

    @Override
    public void onSignInFailed() {
        beginUserInitiatedSignIn();
        mBtnPlay.setVisibility(View.GONE);
        mProgressBarConnect.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSignInSucceeded() {
        showSignOutBar();
        mProgressBarConnect.setVisibility(View.GONE);
        mBtnPlay.setVisibility(View.VISIBLE);
    }
}
