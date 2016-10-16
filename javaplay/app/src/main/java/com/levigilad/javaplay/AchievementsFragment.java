package com.levigilad.javaplay;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;
import com.google.basegameutils.games.GameHelper;
import com.levigilad.javaplay.infra.BaseGameFragment;
import com.levigilad.javaplay.infra.adapters.AchievementsRecyclerViewAdapter;

import java.util.ArrayList;


public class AchievementsFragment extends BaseGameFragment {
    private static final String TAG = "AchievementsFragment";
    private static final String GAME_ID = "GameId";

    private static final int REQUESTED_CLIENTS = GameHelper.CLIENT_GAMES;

    private String mGameId;

    /**
     * Designer
     */
    private RecyclerView mAchievementOptionsRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private AchievementsRecyclerViewAdapter mAdapter;

    public AchievementsFragment() {
        super(REQUESTED_CLIENTS);
    }

    public static AchievementsFragment newInstance(String gameId) {
        AchievementsFragment fragment = new AchievementsFragment();
        Bundle args = new Bundle();
        args.putString(GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_achievements, container, false);
        initializeView(view);

        return view;
    }

    private void initializeView(View view) {
        Context context = this.getActivity().getApplicationContext();
        mAchievementOptionsRecyclerView =
                (RecyclerView)view.findViewById(R.id.achievements_recycler_view);
        mAchievementOptionsRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(context);
        mAchievementOptionsRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new AchievementsRecyclerViewAdapter(new ArrayList<Achievement>());
        mAchievementOptionsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSignInSucceeded() {
        loadAchievements(false);
    }

    private void loadAchievements(boolean forceReload) {
        Games.Achievements.load(getApiClient(), forceReload)
                .setResultCallback(new ResultCallback<Achievements.LoadAchievementsResult>() {
                    @Override
                    public void onResult(@NonNull Achievements.LoadAchievementsResult loadAchievementsResult) {
                        processResult(loadAchievementsResult);
                    }
                });
    }

    private void processResult(Achievements.LoadAchievementsResult loadAchievementsResult) {
        if (!checkStatusCode(loadAchievementsResult.getStatus().getStatusCode())) {
            return;
        }

        AchievementBuffer buffer = loadAchievementsResult.getAchievements();

        int achievementIndex = -1;

        for (int i = 0; i < buffer.getCount(); i++) {
            Achievement achievement = buffer.get(i);

            if (filter(achievement)) {
                continue;
            }

            achievementIndex++;
            mAdapter.addItem(achievement, achievementIndex);
        }

        buffer.release();
        loadAchievementsResult.release();
    }

    private boolean filter(Achievement achievement) {
        String achievementName = achievement.getName();

        return (!achievementName.startsWith(getGameId()));
    }

    /**
     * Returns false if something went wrong, probably. This should handle
     * more cases, and probably report more accurate results.
     *
     * @param statusCode
     * @return
     */
    private boolean checkStatusCode(int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_NO_DATA:
                // The device was unable to retrieve any data from the network and has no data
                // cached locally
                showErrorMessage(statusCode, R.string.status_multiplayer_error_no_internet_no_cache);
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_STALE_DATA:
                // if the device was unable to retrieve the latest data from the network,
                // but has some data cached locally
                showErrorMessage(statusCode, R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED: {
                // if the client needs to reconnect to the service to access this data
                showErrorMessage(statusCode, R.string.status_client_reconnect_required);
            }
            case GamesStatusCodes.STATUS_LICENSE_CHECK_FAILED: {
                // if the game is not licensed to the user
                showErrorMessage(statusCode, R.string.status_license_failed);
            }
            case GamesStatusCodes.STATUS_INTERNAL_ERROR: {
                // if the game is not licensed to the user
                showErrorMessage(statusCode, R.string.status_internal_error);
            }
            default:
                showErrorMessage(statusCode, R.string.unexpected_status);
                Log.d(TAG, "Did not have warning or string to deal with: " + statusCode);
        }

        return false;
    }
}
