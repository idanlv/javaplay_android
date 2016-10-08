package com.levigilad.javaplay.yaniv;

import android.content.Context;
import android.net.Uri;
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
import com.levigilad.javaplay.infra.GameFragment;
import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.PlayingCard;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class YanivGameFragment extends GameFragment implements View.OnClickListener {
    private static final String TAG = "YanivGameFragment";

    private OnFragmentInteractionListener mListener;
    private YanivGame _game;
    private List<PlayingCard> _hand = new LinkedList<>();
    private List<PlayingCard> _cardsToDiscard = new LinkedList<>();

    /**
     * Designer
     */
    private LinearLayout mPlayerDataLinearLayout;
    private Button mDiscardButton;
    private TextView mInstructionsTextView;
    private StackView mDeckStackView;
    private ImageView mDeckImageView;

    /**
     * Required empty constructor
     */
    public YanivGameFragment() {
        super();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment YanivGameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YanivGameFragment newInstance(ArrayList<String> invitees,
                                                Bundle autoMatchCriteria) {
        YanivGameFragment fragment = new YanivGameFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(INVITEES, invitees);
        args.putBundle(AUTO_MATCH, autoMatchCriteria);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeViews();
    }

    private void initializeViews() {
        View view = getView();

        mPlayerDataLinearLayout = (LinearLayout) view.findViewById(R.id.player_data_linear_layout);

        // Remove stub image which was created for design purposes
        View stubImage = view.findViewById(R.id.card_stub_image_view);
        mPlayerDataLinearLayout.removeView(stubImage);

        mDiscardButton = (Button)view.findViewById(R.id.discard_button);
        mDiscardButton.setEnabled(false);
        mDiscardButton.setOnClickListener(this);

        mInstructionsTextView = (TextView)view.findViewById(R.id.instructions_text_view);

        mDeckStackView = (StackView)view.findViewById(R.id.deck_stack_view);

        mDeckImageView = (ImageView)view.findViewById(R.id.deck_image_view);
        mDeckImageView.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_yaniv_game, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
            _cardsToDiscard.add(card);
            view.setRotation(20);

            mDiscardButton.setEnabled(true);
        } else {
            _cardsToDiscard.remove(card);
            view.setRotation(0);

            if (_cardsToDiscard.size() == 0) {
                mDiscardButton.setEnabled(false);
            }
        }
    }

    private void discardButtonOnClicked() {
        boolean isValid = _game.isCardsDiscardValid(_cardsToDiscard);

        if (!isValid) {
            Toast.makeText(this.getActivity().getApplicationContext(),
                    "Invalid discard", Toast.LENGTH_SHORT);
        } else {
            mDiscardButton.setEnabled(false);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    protected void askForRematch() {

    }

    @Override
    protected void startMatch(TurnBasedMatch match) {
        try {
            DeckOfCards cards = _game.generateDeck(2);

            YanivTurn turnData = new YanivTurn();

            _hand.add(cards.pop());
            turnData.setAvailableDeck(cards);

            String playerId = Games.Players.getCurrentPlayerId(getApiClient());
            String myParticipantId = match.getParticipantId(playerId);

            finishTurn(match.getMatchId(), myParticipantId, turnData.export());
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
