package com.levigilad.javaplay.infra.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.entities.Game;
import com.levigilad.javaplay.infra.interfaces.OnItemClickListener;

import java.util.ArrayList;

/**
 * Created by User on 08/10/2016.
 */

public class GameSelectionRecyclerViewAdapter extends
        RecyclerView.Adapter<GameSelectionRecyclerViewAdapter.GameHolder> {
    private static final String TAG = "GameSelectionView";

    private ArrayList<Game> mGames;
    private static OnItemClickListener mListener;

    public static class GameHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mNameTextView;
        TextView mDescriptionTextView;

        public GameHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.game_name_text_view);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.game_description_text_view);

            Log.i(TAG, "Added listener");

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClicked(getAdapterPosition(), v);
        }
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public GameSelectionRecyclerViewAdapter(ArrayList<Game> games) {
        this.mGames = games;
    }

    @Override
    public GameHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragement_game_selection, parent, false);

        GameHolder gameHolder = new GameHolder(view);
        return gameHolder;
    }

    @Override
    public void onBindViewHolder(GameHolder holder, int position) {
        holder.mNameTextView.setText(mGames.get(position).getGameId());
        holder.mDescriptionTextView.setText(mGames.get(position).getDescription());
    }

    public void addItem(Game game, int index) {
        mGames.add(index, game);
        notifyItemInserted(index);
    }

    public void removeItem(int index) {
        mGames.remove(index);
        notifyItemRemoved(index);
    }

    public Game getItem(int index) {
        return mGames.get(index);
    }

    @Override
    public int getItemCount() {
        return mGames.size();
    }
}
