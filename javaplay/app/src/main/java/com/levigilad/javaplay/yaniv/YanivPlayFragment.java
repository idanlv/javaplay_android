package com.levigilad.javaplay.yaniv;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.ActivityUtils;
import com.levigilad.javaplay.infra.PlayFragment;
import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.PlayingCard;
import com.levigilad.javaplay.infra.enums.PlayingCardRanks;
import com.levigilad.javaplay.infra.enums.PlayingCardSuits;

import java.util.ArrayList;
import java.util.Iterator;

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

    /**
     * Members
     */
    private YanivGame mGame;
    private DeckOfCards mCurrPlayersHand;
    private DeckOfCards mAvailableDiscardedCards;
    private DeckOfCards mDiscardedCards;
    private DeckOfCards mFreshDeckCards;
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

    public static YanivPlayFragment newInstance(TurnBasedMatch match) {
        YanivPlayFragment fragment = new YanivPlayFragment();
        Bundle args = new Bundle();
        args.putParcelable(MATCH_ID, match);
        fragment.setArguments(args);
        return fragment;
    }

    // Like the onCreate in Activity
    private void initializeView(View parentView) {

        // Set game Theme
        getActivity().setTheme(android.R.style.Theme_Material_NoActionBar_Fullscreen);

        // Fragment locked in landscape screen orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        // Set members
        mGame = new YanivGame(getActivity().getApplicationContext());
        mCurrPlayersHand = new DeckOfCards();
        mAvailableDiscardedCards = new DeckOfCards();
        mDiscardedCards = new DeckOfCards();
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
                dealCardFromDeck(v);
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

        // Start
        mFreshDeckCards = new DeckOfCards();
        mFreshDeckCards = mGame.generateDeck(4);

        setDemoCards();
        setCardsInHandView();
        setCardsInDiscardView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_yaniv_game, container, false);
        initializeView(view);

        return view;
    }

    private void discard() {

        PlayingCard playingCard;
        View v;
        int i = 0;
        DeckOfCards discardedCards = new DeckOfCards();
        Iterator<PlayingCard> it;

        // Get marked cards to discarded deck
        it = mCurrPlayersHand.iterator();
        while (it.hasNext()) {
            playingCard = it.next();
            v = mHandLL.getChildAt(i);
            if (v.isActivated()) {
                discardedCards.addCardToTop(playingCard);
            }
            i++;
        }


        if (mGame.isCardsDiscardValid(discardedCards)) {
            mGetNewCard = true;

            // Remove discarded cards
            it = discardedCards.iterator();
            while (it.hasNext()) {
                playingCard = it.next();
                mCurrPlayersHand.removeCard(playingCard);
            }
            setCardsInHandView();
        }
    }

    private void setCardsInHandView(){
        PlayingCard playingCard;
        Drawable drawable;
        Context context;
        ImageView img;
        int i = 0;

        context = getActivity().getApplicationContext();
        mYanivBtn.setEnabled(false);
        mHandLL.removeAllViews();

        Iterator<PlayingCard> it = mCurrPlayersHand.iterator();

        while (it.hasNext()) {
            playingCard = it.next();
            img = new ImageView(context);

            drawable = ActivityUtils.getCardAsDrawable(playingCard, context);
            img.setImageDrawable(drawable);

            img.setId(i);

            img.setLayoutParams(new LinearLayout.LayoutParams(
                    ActivityUtils.dpToPx(CARD_WIDTH_DP, context),
                    ActivityUtils.dpToPx(CARD_HEIGHT_DP,context)));
            img.setAdjustViewBounds(true);

            img.setPadding(PADDING_AS_RECT_SIZE,
                    PADDING_AS_RECT_SIZE,
                    PADDING_AS_RECT_SIZE,
                    PADDING_AS_RECT_SIZE);
            img.setActivated(false);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setMarkedImageView(v);
                }
            });

            mHandLL.addView(img);

            i++;
        }

        mScoreTV.setText("" + mGame.calculateDeckScore(mCurrPlayersHand));
        if (mGame.canYaniv(mCurrPlayersHand)) {
            mYanivBtn.setEnabled(true);
        }
    }

    private void setCardsInDiscardView(){
        PlayingCard playingCard;
        Drawable drawable;
        Context context;
        ImageView img;
        int i = 0;

        context = getActivity().getApplicationContext();
        mDiscardedCardsLL.removeAllViews();

        Iterator<PlayingCard> it = mAvailableDiscardedCards.iterator();

        while (it.hasNext()) {
            playingCard = it.next();
            img = new ImageView(context);

            drawable = ActivityUtils.getCardAsDrawable(playingCard,context);
            img.setImageDrawable(drawable);

            img.setId(i);

            img.setLayoutParams(new LinearLayout.LayoutParams(
                    ActivityUtils.dpToPx(CARD_WIDTH_DP,context),
                    ActivityUtils.dpToPx(CARD_HEIGHT_DP,context)));
            img.setAdjustViewBounds(true);

            img.setPadding(PADDING_AS_RECT_SIZE,
                    PADDING_AS_RECT_SIZE,
                    PADDING_AS_RECT_SIZE,
                    PADDING_AS_RECT_SIZE);
            img.setActivated(false);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCardFromDiscardedPile(v);
                }
            });

            mDiscardedCardsLL.addView(img);

            i++;
        }
    }

    private void setMarkedImageView(View v) {
        if (v.isActivated()) {
            v.setAlpha(LIGHTED_IMAGE_VIEW_ALPHA);
            v.setBackgroundColor(Color.TRANSPARENT);
            v.setActivated(false);
        } else {
            v.setAlpha(DIMMED_IMAGE_VIEW_ALPHA);
            v.setBackgroundColor(Color.BLUE);
            v.setActivated(true);
        }
    }

    private void dealCardFromDeck(View v) {
        if (mGetNewCard) {
            mCurrPlayersHand.addCardToBottom(mFreshDeckCards.pop());
            setCardsInHandView();
            mGetNewCard = false;
        }
    }

    private void getCardFromDiscardedPile(View v) {
        if (mGetNewCard) {
            mCurrPlayersHand.addCardToBottom(mAvailableDiscardedCards.get(v.getId()));
            mAvailableDiscardedCards.removeCardByIndex(v.getId());
            setCardsInDiscardView();
            setCardsInHandView();
            mGetNewCard = false;
        }
    }

    private void declareYaniv() {
        Log.println(Log.INFO,"yaniv","Yaniv !");
    }

    //TODO: Del demo for testing
    private void setDemoCards() {
        PlayingCard pc1 = new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.CLUBS);
        PlayingCard pc2 = new PlayingCard(PlayingCardRanks.TWO, PlayingCardSuits.CLUBS);
        PlayingCard pc3 = new PlayingCard(PlayingCardRanks.THREE, PlayingCardSuits.CLUBS);
        PlayingCard pc4 = new PlayingCard(PlayingCardRanks.FOUR, PlayingCardSuits.CLUBS);
        PlayingCard pc5 = new PlayingCard(PlayingCardRanks.FIVE, PlayingCardSuits.CLUBS);

        mCurrPlayersHand.addCardToTop(pc1);
        mCurrPlayersHand.addCardToTop(pc2);
        mCurrPlayersHand.addCardToTop(pc3);
        mCurrPlayersHand.addCardToTop(pc4);
        mCurrPlayersHand.addCardToTop(pc5);

        PlayingCard dpc1 = new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.HEARTS);
        PlayingCard dpc2 = new PlayingCard(PlayingCardRanks.TWO, PlayingCardSuits.HEARTS);
        PlayingCard dpc3 = new PlayingCard(PlayingCardRanks.THREE, PlayingCardSuits.HEARTS);
        //PlayingCard dpc4 = new PlayingCard(PlayingCardRanks.FOUR, PlayingCardSuits.HEARTS);
        //PlayingCard dpc5 = new PlayingCard(PlayingCardRanks.FIVE, PlayingCardSuits.HEARTS);
        PlayingCard dpc4 = new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE);
        PlayingCard dpc5 = new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE);

        mAvailableDiscardedCards.addCardToTop(dpc1);
        mAvailableDiscardedCards.addCardToTop(dpc2);
        mAvailableDiscardedCards.addCardToTop(dpc3);
        mAvailableDiscardedCards.addCardToTop(dpc4);
        mAvailableDiscardedCards.addCardToTop(dpc5);

    }

    @Override
    protected void startMatch() {

    }

    @Override
    protected void startTurn() {

    }

    @Override
    protected void updateView() {

    }

}
