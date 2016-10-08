package com.levigilad.javaplay.yaniv;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.StackView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.PlayFragment;
import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.PlayingCard;
import com.levigilad.javaplay.infra.enums.PlayingCardRanks;
import com.levigilad.javaplay.infra.enums.PlayingCardSuits;

import org.json.JSONException;

import java.util.ArrayList;


public class YanivPlayFragment extends PlayFragment implements View.OnClickListener {
    private static final String TAG = "YanivPlayFragment";
    private static final String PLAYINGCARD_PREFIX = "playingcard_";
    private static final String DRAWABLE_TYPE_NAME = "drawable";

    private YanivGame _game;
    private DeckOfCards _hand = new DeckOfCards();
    private DeckOfCards _cardsToDiscard = new DeckOfCards();

    /**
     * Designer
     */
    private GridLayout handLayout;
    private Button mDiscardButton;
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
        // Fragment locked in landscape screen orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        handLayout =
                (GridLayout) parentView.findViewById(R.id.hand_layout);

        // Remove stub image which was created for design purposes
        View stubImage = parentView.findViewById(R.id.card_stub_image_view);
        handLayout.removeAllViews();
        handLayout.setColumnCount(YanivGame.INITIAL_DEFAULT_CARD_COUNT);
        handLayout.setRowCount(1);
        handLayout.setColumnOrderPreserved(true);



        mDiscardButton = (Button)parentView.findViewById(R.id.discard_button);
        mDiscardButton.setEnabled(false);
        mDiscardButton.setOnClickListener(this);

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

        // test code for deck
        _hand.addCardToTop(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));
        ImageView img = new ImageView(getActivity().getApplicationContext());
        img.setImageDrawable(getCardAsDrawable(_hand.get(0)));


        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = GridLayout.LayoutParams.WRAP_CONTENT;
        param.width = GridLayout.LayoutParams.WRAP_CONTENT;
        param.rightMargin = 5;
        param.setGravity(Gravity.CENTER);

        GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
        GridLayout.Spec colSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);

        img.setLayoutParams(new ViewGroup.LayoutParams(100,100));

        GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(rowSpan, colSpan);
        handLayout.addView(img, gridParam);

        //handLayout.addView(img);

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
            _cardsToDiscard.addCardToTop(card);
            view.setRotation(20);

            mDiscardButton.setEnabled(true);
        } else {
            _cardsToDiscard.removeCard(card);
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


    @Override
    protected void askForRematch() {

    }

    @Override
    protected void startMatch(TurnBasedMatch match) {
        /*
        try {

            DeckOfCards cards = _game.generateDeck(2);

            YanivTurn turnData = new YanivTurn();
            _hand.addCardToTop(cards.pop());
            turnData.setAvailableDeck(cards);

            String playerId = Games.Players.getCurrentPlayerId(getApiClient());
            String myParticipantId = match.getParticipantId(playerId);

            finishTurn(match.getMatchId(), myParticipantId, turnData.export());



        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    protected void updateMatch(TurnBasedMatch match) {

    }

    @Override
    protected void updateView(byte[] turnData) {

    }

    public Drawable getCardAsDrawable(PlayingCard playingCard) {
        Context context = this.getActivity().getApplicationContext();

        // Build card name
        String shapeName = PLAYINGCARD_PREFIX + playingCard.getRank().getNameString().toLowerCase()
                + playingCard.getSuit().name().toLowerCase().charAt(0);

        int shapeID = context.getResources()
                .getIdentifier(shapeName,DRAWABLE_TYPE_NAME,context.getPackageName());

        Drawable drawable = context.getResources().getDrawable(shapeID,null);

        return drawable;
    }

}
