package com.levigilad.javaplay.infra;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.entities.Game;

import java.util.ArrayList;

/**
 * Created by User on 08/10/2016.
 */

public class GameOptionsRecyclerViewAdapter extends
        RecyclerView.Adapter<GameOptionsRecyclerViewAdapter.GameHolder> {
    private static final String TAG = "GameOptionsRecyclerView";
    private ArrayList<Game> _games;
    private static MyClickListener _listener;

    public static class GameHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView _nameTextView;
        TextView _descriptionTextView;

        public GameHolder(View itemView) {
            super(itemView);
            _nameTextView = (TextView) itemView.findViewById(R.id.game_name_text_view);
            _descriptionTextView = (TextView) itemView.findViewById(R.id.game_description_text_view);

            Log.i(TAG, "Added listener");

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            _listener.onItemClicked(getAdapterPosition(), v);
        }
    }

    public void setOnClickListener(MyClickListener listener) {
        this._listener = listener;
    }

    public GameOptionsRecyclerViewAdapter(ArrayList<Game> games) {
        this._games = games;
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
        holder._nameTextView.setText(_games.get(position).getDisplayName());
        holder._descriptionTextView.setText(_games.get(position).getDescription());
    }

    public void addItem(Game game, int index) {
        _games.add(index, game);
        notifyItemInserted(index);
    }

    public void removeItem(int index) {
        _games.remove(index);
        notifyItemRemoved(index);
    }

    public Game getItem(int index) {
        return _games.get(index);
    }

    @Override
    public int getItemCount() {
        return _games.size();
    }

    public interface MyClickListener {
        void onItemClicked(int position, View v);
    }
}
