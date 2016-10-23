package com.levigilad.javaplay.yaniv;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.ActivityUtils;
import com.levigilad.javaplay.infra.PlayFragment;
import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.PlayingCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/* TODO :
    1) Prevent Discard from accruing twice
    2) calculateScore(Array deckofcards) - return array key value of player and score
    3) convert players deck to array
    4) make yaniv
    5) cards history and scores

    Flow :
    1) deal cards to all
    2) turn
        0) yaniv - yes\no
        a) discard cards
        b) take cards
        c) allowed discarted cards to next player
*/
public class YanivPlayFragment extends PlayFragment {
    /**
     * Constants
     */
    private static final String TAG = "YanivPlayFragment";
    private static final int CARD_WIDTH_DP = 70;
    private static final int CARD_HEIGHT_DP = 100;
    private static final int PADDING_AS_RECT_SIZE= 5;
    private static final float LIGHTED_IMAGE_VIEW_ALPHA = 1f;
    private static final float DIMMED_IMAGE_VIEW_ALPHA = 0.6f;
    private static final int DEFAULT_NUMBER_OF_DECKS = 2;
    private static final int DEFAULT_NUMBER_OF_JOKERS = 4;
    private static final int MARKED_IMAGE_BACKGROUND = Color.BLUE;

    /**
     * Members
     */
    private YanivGame mGame;
    private DeckOfCards mAvailableDiscardedCards;
    private DeckOfCards mDiscardedCards;
    private DeckOfCards mGlobalCardDeck;
    private HashMap<String, DeckOfCards> mPlayersHands;
    private DeckOfCards mCurrPlayersHand;
    private DeckOfCards mPlayersMarkedCards;
    private boolean mGetNewCard;

    /**
     * Designer
     */
    private ImageView mDeckIV;
    private Button mDiscardBtn;
    private Button mYanivBtn;
    private LinearLayout mHandLL;
    private LinearLayout mDiscardedCardsLL;
    private ListView mPlayersCardCountLV;
    private TextView mScoreTV;

    /**
     * Required empty constructor
     */
    public YanivPlayFragment() {
        super(new YanivTurn());
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

    //TODO: make comment
    public static YanivPlayFragment newInstance(TurnBasedMatch match) {
        YanivPlayFragment fragment = new YanivPlayFragment();
        Bundle args = new Bundle();
        args.putParcelable(MATCH_ID, match);
        fragment.setArguments(args);
        return fragment;
    }

    // Like the onCreate in Activity TODO: make a nice /* */
    private void initializeView(View parentView) {

        // Set game Theme
        getActivity().setTheme(android.R.style.Theme_Material_NoActionBar_Fullscreen);

        // Fragment locked in landscape screen orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        // Set members
        YanivTurn turn = (YanivTurn)mTurnData; // Local var for YanivTurn
        mGame = new YanivGame(getActivity().getApplicationContext());
        mAvailableDiscardedCards = turn.getmAvailableDiscardedCards(); // use member by ref
        mDiscardedCards = turn.getmDiscardedCards(); // use member by ref
        mGlobalCardDeck = turn.getmGlobalCardDeck(); // use member by ref
        mPlayersHands = turn.getmPlayersHands(); // use member by ref
        mCurrPlayersHand = new DeckOfCards();
        mPlayersMarkedCards = new DeckOfCards();
        mGetNewCard = false;

        // Hook id's
        mHandLL = (LinearLayout) parentView.findViewById(R.id.ll_hand);
        mDiscardedCardsLL = (LinearLayout) parentView.findViewById(R.id.ll_discarded);
        mDeckIV = (ImageView) parentView.findViewById(R.id.iv_deck);
        mDiscardBtn = (Button) parentView.findViewById(R.id.btn_discard);
        mYanivBtn = (Button) parentView.findViewById(R.id.btn_Yaniv);
        mPlayersCardCountLV = (ListView) parentView.findViewById(R.id.lv_cards_count);
        mScoreTV = (TextView) parentView.findViewById(R.id.tv_score);

        // Set listeners
        mDeckIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawCardFromDeck(v);
            }
        });
        mDiscardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discard();
            }
        });
        mYanivBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declareYaniv();
            }
        });

        // Disable GUI
        setPlayStatus(false);
    }

    //TODO: make comment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_yaniv_game, container, false);
        initializeView(view);

        return view;
    }

    /**
     * Clicked the discard button
     */
    private void discard() {
        PlayingCard playingCard;
        View v;
        int i = 0;

        mPlayersMarkedCards.clear();
        Iterator<PlayingCard> it;

        // Get marked cards to discarded deck
        it = mCurrPlayersHand.iterator();
        while (it.hasNext()) {
            playingCard = it.next();
            v = mHandLL.getChildAt(i);
            if (v.isActivated()) {
                mPlayersMarkedCards.addCardToTop(playingCard);
            }
            i++;
        }

        if (YanivGame.isCardsDiscardValid(mPlayersMarkedCards)) {
            mYanivBtn.setEnabled(false);
            mDiscardBtn.setEnabled(false);

            // Remove discarded cards from players hand
            it = mPlayersMarkedCards.iterator();
            while (it.hasNext()) {
                playingCard = it.next();
                mCurrPlayersHand.removeCard(playingCard);
            }

            showCardsInHandView();
            mGetNewCard = true;

        }
    }

    /**
     * Show the cards in the players hand and set the listeners for the cards
     */
    private void showCardsInHandView(){
        PlayingCard playingCard;
        Drawable drawable;
        ImageView img;
        int i = 0;

        // Clear all cards from view
        mHandLL.removeAllViews();

        // Draw cards
        Iterator<PlayingCard> it = mCurrPlayersHand.iterator();
        while (it.hasNext()) {
            playingCard = it.next();
            img = new ImageView(mAppContext);

            drawable = ActivityUtils.getCardAsDrawable(playingCard, mAppContext);
            img.setImageDrawable(drawable);

            img.setId(i);

            img.setLayoutParams(new LinearLayout.LayoutParams(
                    ActivityUtils.dpToPx(CARD_WIDTH_DP, mAppContext),
                    ActivityUtils.dpToPx(CARD_HEIGHT_DP,mAppContext)));
            img.setAdjustViewBounds(true);

            img.setPadding(PADDING_AS_RECT_SIZE,
                    PADDING_AS_RECT_SIZE,
                    PADDING_AS_RECT_SIZE,
                    PADDING_AS_RECT_SIZE);
            img.setActivated(false);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playerCardOnClick(v);
                }
            });

            mHandLL.addView(img);

            i++;
        }

        // Update my score
        mScoreTV.setText("" + YanivGame.calculateDeckScore(mCurrPlayersHand));
    }

    /**
     * Mark the card that was clicked
     * @param v as clicked view
     */
    private void playerCardOnClick(View v) {
        if (v.isActivated()) {
            v.setAlpha(LIGHTED_IMAGE_VIEW_ALPHA);
            v.setBackgroundColor(Color.TRANSPARENT);
            v.setActivated(false);
        } else {
            v.setAlpha(DIMMED_IMAGE_VIEW_ALPHA);
            v.setBackgroundColor(MARKED_IMAGE_BACKGROUND);
            v.setActivated(true);
        }
    }

    /**
     * Show the available discarded cards and set the listeners
     */
    private void showCardsInDiscardView(){
        PlayingCard playingCard;
        Drawable drawable;
        ImageView img;
        int i = 0;

        // Clear all cards from view
        mDiscardedCardsLL.removeAllViews();

        // Draw cards
        Iterator<PlayingCard> it = mAvailableDiscardedCards.iterator();
        while (it.hasNext()) {
            playingCard = it.next();
            img = new ImageView(mAppContext);

            drawable = ActivityUtils.getCardAsDrawable(playingCard,mAppContext);
            img.setImageDrawable(drawable);

            img.setId(i);

            img.setLayoutParams(new LinearLayout.LayoutParams(
                    ActivityUtils.dpToPx(CARD_WIDTH_DP,mAppContext),
                    ActivityUtils.dpToPx(CARD_HEIGHT_DP,mAppContext)));
            img.setAdjustViewBounds(true);

            img.setPadding(PADDING_AS_RECT_SIZE,
                    PADDING_AS_RECT_SIZE,
                    PADDING_AS_RECT_SIZE,
                    PADDING_AS_RECT_SIZE);
            img.setActivated(false);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCardFromAvailableDiscardedPile(v);
                }
            });

            mDiscardedCardsLL.addView(img);

            i++;
        }
    }

    /**
     * Get the selected card from available discarded pile
     * @param v as the clicked view (card)
     */
    private void getCardFromAvailableDiscardedPile(View v) {
        PlayingCard playingCard;
        if (mGetNewCard) {
            mCurrPlayersHand.addCardToBottom(mAvailableDiscardedCards.get(v.getId()));

            // Get unused cards to discarded pile
            Iterator<PlayingCard> it = mAvailableDiscardedCards.iterator();
            while (it.hasNext()) {
                playingCard = it.next();
                mDiscardedCards.addCardToTop(playingCard);
            }
            mAvailableDiscardedCards.clear();

            mGetNewCard = false;
            playerEndOfTurn();
        }
    }

    /**
     * Draw a new card from the global card deck
     * @param v as the View clicked
     */
    private void drawCardFromDeck(View v) {
        if (mGetNewCard) {
            // If we have fresh cards, deal them
            if (mGlobalCardDeck.size() > 0) {
                mCurrPlayersHand.addCardToBottom(mGlobalCardDeck.pop());
            }
            // No cards in deck, add discarded cards to global and reshuffle
            else {
                mGlobalCardDeck.addAll(mDiscardedCards);
                mGlobalCardDeck.shuffle();
                mDiscardedCards.clear();
            }
            mGetNewCard = false;
        }
        playerEndOfTurn();
    }

    /**
     * Play the end of turn of the player
     */
    private void playerEndOfTurn(){
        PlayingCard playingCard;
        Iterator<PlayingCard> it;

        // Move available discard to discard
        it = mAvailableDiscardedCards.iterator();
        while (it.hasNext()) {
            playingCard = it.next();
            mDiscardedCards.addCardToTop(playingCard);
        }
        mAvailableDiscardedCards.replace(
                YanivGame.getAvailableCardsFromDiscard(mPlayersMarkedCards));

        setPlayStatus(false);
        finishTurn(getNextParticipantId());
    }


    /**
     * Declare Yaniv and finish session
     */
    private void declareYaniv() {
        //TODO : make something here
        Log.i(TAG, "Yaniv !");
    }

    /**
     * Generate the playing deck's
     */
    private void dealCards() {
        ArrayList<String> participantIds = mMatch.getParticipantIds();

        mGlobalCardDeck = YanivGame.generateDeck(DEFAULT_NUMBER_OF_DECKS, DEFAULT_NUMBER_OF_JOKERS);

        for(String participant : participantIds) {
            DeckOfCards deck = new DeckOfCards();

            // Deal Hand to participant
            for (int i = 0; i < mGame.getInitialNumOfPlayerCards(); i++){
                deck.addCardToTop(mGlobalCardDeck.pop());
            }

            // Save
            mPlayersHands.put(participant, deck);
        }

        // Link the me to my hand
        mCurrPlayersHand = mPlayersHands.get(getCurrentParticipantId());

        // Draw first card to available discarded cards
        mAvailableDiscardedCards.addCardToTop(mGlobalCardDeck.pop());
    }

    /**
     * Enable/Disable playing gui between turns
     * @param enabled as to Enable/Disable game play
     */
    private void setPlayStatus(boolean enabled) {
        ActivityUtils.setEnabledRecursively(mHandLL, enabled);
        ActivityUtils.setEnabledRecursively(mDiscardedCardsLL, enabled);
        mDeckIV.setEnabled(enabled);
        mDiscardBtn.setEnabled(enabled);

        // Set enabled only if allowed
        if (enabled && YanivGame.canYaniv(mCurrPlayersHand)) {
            mYanivBtn.setEnabled(true);
        } else {
            mYanivBtn.setEnabled(false);
        }
    }

    /**
     * Start of match
     */
    @Override
    protected void startMatch() {
        dealCards();
        Log.i("match","Start of Match");
        Toast.makeText(mAppContext, "startMatch()", Toast.LENGTH_LONG).show();
    }

    /**
     * User take turn
     */
    @Override
    protected void startTurn() {
        setPlayStatus(true);
        Log.i("turn","Start of Turn");
        Toast.makeText(mAppContext, "Its your turn...", Toast.LENGTH_LONG).show();
    }

    /**
     * Update the user view
     */
    @Override
    protected void updateView() {
        showCardsInDiscardView();
        showCardsInHandView();
    }
}
