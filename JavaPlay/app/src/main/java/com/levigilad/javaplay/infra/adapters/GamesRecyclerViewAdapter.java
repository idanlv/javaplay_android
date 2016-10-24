package com.levigilad.javaplay.infra.adapters;

import android.support.design.widget.FloatingActionButton;
import android.support.transition.TransitionManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.entities.Game;
import com.levigilad.javaplay.infra.interfaces.OnItemClickListener;

import java.util.ArrayList;

/**
 * This class is in charge of creating a view object for each game possibility in our game
 */
public class GamesRecyclerViewAdapter extends
        RecyclerView.Adapter<GamesRecyclerViewAdapter.GameHolder> {
    /**
     * Constants
     */
    private static final String TAG = "GamePossibilityView";

    /**
     * Members
     */
    private ArrayList<Game> mGames;
    private static OnItemClickListener mListener;

    /**
     * Inner class for handling events on game view
     */
    public static class GameHolder extends RecyclerView.ViewHolder {
        /**
         * Designer
         */
        private TextView mNameTextView;
        private TextView mDescriptionTextView;
        private FloatingActionButton mPlayFloatingActionButton;
        private CardView mCardView;
        private Game tag;

        /**
         * Constructor
         * @param itemView Game view
         */
        public GameHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.game_card_view);

            mNameTextView = (TextView) itemView.findViewById(R.id.game_name_text_view);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.game_description_text_view);
            mPlayFloatingActionButton =
                    (FloatingActionButton) itemView.findViewById(R.id.game_start_new_match_action_button);


            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TransitionManager.beginDelayedTransition(mCardView);
                    mDescriptionTextView.setVisibility(View.VISIBLE);
                }
            });

            mPlayFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemClicked(getAdapterPosition(), mCardView);
                    }
                }
            });
        }

        /**
         * Sets the name of the game
         * @param name The name of the game
         */
        public void setName(String name) {
            mNameTextView.setText(name);
        }

        /**
         * Sets the description for the game
         * @param description The description for the game
         */
        public void setDescription(String description) {
            mDescriptionTextView.setText(description);
        }

        public void setTag(Game tag) {
            mCardView.setTag(tag);
        }
    }

    /**
     * Constructor
     * @param games Game possibilities
     */
    public GamesRecyclerViewAdapter(ArrayList<Game> games) {
        this.mGames = games;
    }

    /**
     * Attach a listener
     * @param listener Listener
     */
    public void setOnClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    /**
     * This method creates the GameHolder view and attaches it to the parent view
     * @param parent parent view
     * @param viewType
     * @return GameHolder view
     */
    @Override
    public GameHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragement_game_possibility, parent, false);

        return (new GameHolder(view));
    }

    /**
     * Determines GameHolder data according the viewer position in list of games
     *
     * @param holder   Object to update
     * @param position Position in game list
     */
    @Override
    public void onBindViewHolder(GameHolder holder, int position) {
        Game currentGame = mGames.get(position);
        holder.setName(currentGame.getGameId());
        holder.setDescription(currentGame.getDescription());
        holder.setTag(currentGame);
    }

    /**
     * Adds a new game
     * @param game Game to add
     * @param index Index for the game
     */
    public void addItem(Game game, int index) {
        mGames.add(index, game);
        notifyItemInserted(index);
    }

    /**
     * Removes a game in a specific index
     * @param index Index to remove
     */
    public void removeItem(int index) {
        mGames.remove(index);
        notifyItemRemoved(index);
    }

    /**
     * Retrieves the game object in a specific index
     * @param index Index to retrieve
     * @return If index in bounds, returns game object
     * @throws IndexOutOfBoundsException
     */
    public Game getItem(int index) {
        return mGames.get(index);
    }

    /**
     * Retrieves the number of items
     * @return Numebr of game objects
     */
    @Override
    public int getItemCount() {
        return mGames.size();
    }
}
