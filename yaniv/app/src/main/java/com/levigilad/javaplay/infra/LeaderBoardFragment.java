package com.levigilad.javaplay.infra;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.basegameutils.games.GameHelper;
import com.levigilad.javaplay.R;

public class LeaderBoardFragment extends BaseGameFragment {
    private static final String TAG = "LeaderBoardFragment";
    private static final String GAME_ID = "GameId";

    private static final int REQUESTED_CLIENTS = GameHelper.CLIENT_GAMES;

    public LeaderBoardFragment() {
        super(REQUESTED_CLIENTS);
    }

    public static LeaderBoardFragment newInstance(int gameId) {
        LeaderBoardFragment fragment = new LeaderBoardFragment();
        Bundle args = new Bundle();
        args.putInt(GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leadership, container, false);

        return view;
    }

    @Override
    public void onSignInSucceeded() {

    }
}
