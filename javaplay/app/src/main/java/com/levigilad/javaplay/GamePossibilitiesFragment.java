package com.levigilad.javaplay;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.levigilad.javaplay.infra.adapters.GameSelectionRecyclerViewAdapter;
import com.levigilad.javaplay.infra.entities.Playground;
import com.levigilad.javaplay.infra.interfaces.NavigationDrawerCallbacks;
import com.levigilad.javaplay.infra.interfaces.OnGameSelectedListener;
import com.levigilad.javaplay.infra.interfaces.OnItemClickListener;

/**
 * This activity is the viewer for picking a game
 */
public class GamePossibilitiesFragment extends Fragment implements OnItemClickListener {

    private static final String TAG = "GamePossibilitiesFragment";

    // Designer members
    private RecyclerView mRecyclerViewGameOptions;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    public static GamePossibilitiesFragment newInstance() {
        GamePossibilitiesFragment fragment = new GamePossibilitiesFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_selection, container, false);
        initializeView(view);

        return view;
    }

    private void initializeView(View parentView) {
        mRecyclerViewGameOptions =
                (RecyclerView) parentView.findViewById(R.id.game_options_recycler_view);
        mRecyclerViewGameOptions.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewGameOptions.setLayoutManager(mLayoutManager);

        mAdapter = new GameSelectionRecyclerViewAdapter(
                Playground.getInstance(getActivity()).getGames());
        mRecyclerViewGameOptions.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((GameSelectionRecyclerViewAdapter) mAdapter).setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof OnGameSelectedListener)) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onItemClicked(int position, View v) {
        String gameId = (String) ((TextView) v.findViewById(R.id.game_name_text_view)).getText();
        ((OnGameSelectedListener)getActivity()).onGameSelected(gameId);
    }
}