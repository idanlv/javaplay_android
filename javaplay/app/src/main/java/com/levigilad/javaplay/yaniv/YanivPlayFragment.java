package com.levigilad.javaplay.yaniv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.StackView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.PlayFragment;
import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.PlayingCard;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class YanivPlayFragment extends PlayFragment implements View.OnClickListener {
    private static final String TAG = "YanivPlayFragment";

    private YanivGame mGame;
    private List<PlayingCard> mHand = new LinkedList<>();
    private List<PlayingCard> mCardsToDiscard = new LinkedList<>();

    /**
     * Designer
     */
    private LinearLayout mLinearLayoutPlayerData;
    private Button mBtnDiscard;
    private TextView mInstructionsTextView;
    private StackView mDeckStackView;
    private ImageView mDeckImageView;

    /**
     * Required empty constructor
     */
    public YanivPlayFragment() {
        super();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment YanivPlayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YanivPlayFragment newInstance(ArrayList<String> invitees,
                                                Bundle autoMatchCriteria) {
        YanivPlayFragment fragment = new YanivPlayFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(INVITEES, invitees);
        args.putBundle(AUTO_MATCH, autoMatchCriteria);
        fragment.setArguments(args);
        return fragment;
    }

    private void initializeView(View parentView) {
        mLinearLayoutPlayerData = (LinearLayout) parentView.findViewById(R.id.player_data_linear_layout);

        // Remove stub image which was created for design purposes
        View stubImage = parentView.findViewById(R.id.card_stub_image_view);
        mLinearLayoutPlayerData.removeView(stubImage);

        mBtnDiscard = (Button)parentView.findViewById(R.id.discard_button);
        mBtnDiscard.setEnabled(false);
        mBtnDiscard.setOnClickListener(this);

        mInstructionsTextView = (TextView)parentView.findViewById(R.id.instructions_text_view);

        mDeckStackView = (StackView)parentView.findViewById(R.id.deck_stack_view);

        mDeckImageView = (ImageView)parentView.findViewById(R.id.deck_image_view);
        mDeckImageView.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_yaniv_game, container, false);
        initializeView(view);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.discard_button): {
                discardButtonOnClicked();
                break;
            }
            case (R.id.deck_image_view): {
                deckImageViewOnClicked();
                break;
            }
            default: {
                if (v instanceof ImageView) {
                    cardOnClicked(v);
                }
            }
        }
    }

    private void deckImageViewOnClicked() {
        // TODO: Add card from deck to player

        // TODO: Disable view and change opacity if deck is empty

    }

    private void cardOnClicked(View view) {
        PlayingCard card = (PlayingCard) view.getTag(R.string.playing_card_id);
        Object discardedTag = view.getTag(R.bool.shouldDiscard);

        boolean shouldDiscard;

        if (discardedTag == null) {
            shouldDiscard = true;
        } else {
            shouldDiscard = !((boolean)discardedTag);
        }

        view.setTag(R.bool.shouldDiscard, shouldDiscard);

        if (shouldDiscard) {
            mCardsToDiscard.add(card);
            view.setRotation(20);

            mBtnDiscard.setEnabled(true);
        } else {
            mCardsToDiscard.remove(card);
            view.setRotation(0);

            if (mCardsToDiscard.size() == 0) {
                mBtnDiscard.setEnabled(false);
            }
        }
    }

    private void discardButtonOnClicked() {
        boolean isValid = mGame.isCardsDiscardValid(mCardsToDiscard);

        if (!isValid) {
            Toast.makeText(this.getActivity().getApplicationContext(),
                    "Invalid discard", Toast.LENGTH_SHORT);
        } else {
            mBtnDiscard.setEnabled(false);
        }
    }


    @Override
    protected void askForRematch() {

    }

    @Override
    protected void startMatch(TurnBasedMatch match) {
        try {
            DeckOfCards cards = mGame.generateDeck(2);

            YanivTurn turnData = new YanivTurn();

            mHand.add(cards.pop());
            turnData.setAvailableDeck(cards);

            String playerId = Games.Players.getCurrentPlayerId(getApiClient());

            finishTurn(match.getMatchId(), null, turnData.export());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void updateMatch(TurnBasedMatch match) {

    }

    @Override
    protected void updateView(byte[] turnData) {

    }
}
