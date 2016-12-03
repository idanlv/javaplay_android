package com.levigilad.javaplay;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.levigilad.javaplay.infra.adapters.GamesRecyclerViewAdapter;
import com.levigilad.javaplay.infra.entities.Game;
import com.levigilad.javaplay.infra.entities.Playground;
import com.levigilad.javaplay.infra.interfaces.OnGameSelectedListener;
import com.levigilad.javaplay.infra.interfaces.OnItemClickListener;

/**
 * This activity is the viewer for picking a game
 */
public class GamesFragment extends Fragment implements OnItemClickListener {
    /**
     * Designer
     */
    private RecyclerView mRecyclerViewGameOptions;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private OnGameSelectedListener mListener;

    /**
     * Use this factory method to create a new instance of this fragment
     * @return GamesFragment instance
     */
    public static GamesFragment newInstance() {
        GamesFragment fragment = new GamesFragment();

        return fragment;
    }

    /**
     * On Create View
     * @param inflater as inflater layout
     * @param container as ViewGroup of views
     * @param savedInstanceState Saved Bundle state
     * @return the created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_selection, container, false);
        initializeView(view);

        return view;
    }

    /**
     * Initializes view
     * @param parentView parent view
     */
    private void initializeView(View parentView) {
        mRecyclerViewGameOptions =
                (RecyclerView) parentView.findViewById(R.id.game_options_recycler_view);
        mRecyclerViewGameOptions.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewGameOptions.setLayoutManager(mLayoutManager);

        mAdapter = new GamesRecyclerViewAdapter(Playground.getInstance().getGames());
        mRecyclerViewGameOptions.setAdapter(mAdapter);
    }

    /**
     * On resume
     */
    @Override
    public void onResume() {
        super.onResume();
        ((GamesRecyclerViewAdapter) mAdapter).setOnClickListener(this);
    }

    /**
     * On Attach
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Verifies attached context implements listener interface
        if (context instanceof OnGameSelectedListener) {
            mListener = (OnGameSelectedListener)context;
            ((Activity)context).setTitle(R.string.pick_a_game);
        } else {
            throw new RuntimeException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    /**
     * Handles On Item Clicked events
     * @param position The position of the item
     * @param v view
     */
    @Override
    public void onItemClicked(int position, View v) {
        Game selectedGame = (Game) v.getTag();
        if (mListener != null) {
            mListener.onGameSelected(selectedGame);
        }
    }
}