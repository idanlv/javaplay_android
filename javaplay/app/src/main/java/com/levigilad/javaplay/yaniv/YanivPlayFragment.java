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
import java.util.Iterator;

/*
  TODO: 1) Convert to fragment
        2) Convert to turn based game
        3) Play with others
        4) QA
        5) Check royal's hang
  */
public class YanivPlayFragment extends PlayFragment {
    /**
     * Constants
     */
    private static final String TAG = "YanivPlayFragment";
    private static final String PLAYING_CARD_PREFIX = "playingcard_";
    private static final String DRAWABLE_TYPE_NAME = "drawable";
    private static final int CARD_WIDTH_DP = 70;
    private static final int CARD_HEIGHT_DP = 100;
    private static final int PADDING_AS_RECT_SIZE= 5;
    private static final float LIGHTED_IMAGE_VIEW_ALPHA = 1f;
    private static final float DIMMED_IMAGE_VIEW_ALPHA = 0.6f;

    /**
     * Members
     */
    private YanivGame mGame;
    private DeckOfCards mPlayersHand;
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

    public static YanivPlayFragment newInstance(String matchId) {
        YanivPlayFragment fragment = new YanivPlayFragment();
        Bundle args = new Bundle();
        args.putString(MATCH_ID, matchId);
        fragment.setArguments(args);
        return fragment;
    }

    // Like the onCreate in Activity
    private void initializeView(View parentView) {

        getActivity().setTheme(android.R.style.Theme_Material_NoActionBar_Fullscreen);
        //! setContentView(R.layout.activity_main);
        // Fragment locked in landscape screen orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        mGame = new YanivGame(getActivity().getApplicationContext());
        mPlayersHand = new DeckOfCards();
        mDiscardedCards = new DeckOfCards();
        mGetNewCard = false;

        // Hook Id's
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

    @Override
    protected void askForRematch() {

    }

    @Override
    protected byte[] startMatch() {
        return null;
    }

    @Override
    protected void startTurn() {

    }

    @Override
    protected void updateView() {

    }

    private void discard() {

        PlayingCard playingCard;
        View v;
        int i = 0;
        DeckOfCards discardedCards = new DeckOfCards();
        Iterator<PlayingCard> it;

        // Get marked cards to discarded deck
        it = mPlayersHand.iterator();
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
                mPlayersHand.removeCard(playingCard);
            }
            setCardsInHandView();
        }
    }

    private void setCardsInHandView(){
        PlayingCard playingCard;
        ImageView img;
        int i = 0;

        mYanivBtn.setEnabled(false);
        mHandLL.removeAllViews();

        Iterator<PlayingCard> it = mPlayersHand.iterator();

        while (it.hasNext()) {
            playingCard = it.next();
            img = new ImageView(getActivity().getApplicationContext());
            img.setImageDrawable(getCardAsDrawable(playingCard));

            img.setId(i);

            img.setLayoutParams(new LinearLayout.LayoutParams(
                    dpToPx(CARD_WIDTH_DP),dpToPx(CARD_HEIGHT_DP)));
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

        mScoreTV.setText("" + mGame.calculateDeckScore(mPlayersHand));
        if (mGame.canYaniv(mPlayersHand)) {
            mYanivBtn.setEnabled(true);
        }
    }

    private void setCardsInDiscardView(){
        PlayingCard playingCard;
        ImageView img;
        int i = 0;

        mDiscardedCardsLL.removeAllViews();

        Iterator<PlayingCard> it = mDiscardedCards.iterator();

        while (it.hasNext()) {
            playingCard = it.next();
            img = new ImageView(getActivity().getApplicationContext());
            img.setImageDrawable(getCardAsDrawable(playingCard));

            img.setId(i);

            img.setLayoutParams(new LinearLayout.LayoutParams(
                    dpToPx(CARD_WIDTH_DP),dpToPx(CARD_HEIGHT_DP)));
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
            mPlayersHand.addCardToBottom(mFreshDeckCards.pop());
            setCardsInHandView();
            mGetNewCard = false;
        }
    }

    private void getCardFromDiscardedPile(View v) {
        if (mGetNewCard) {
            mPlayersHand.addCardToBottom(mDiscardedCards.get(v.getId()));
            mDiscardedCards.removeCardByIndex(v.getId());
            setCardsInDiscardView();
            setCardsInHandView();
            mGetNewCard = false;
        }
    }

    private void declareYaniv() {
        Log.println(Log.INFO,"yaniv","Yaniv !");
    }

    //TODO : Consider moving to Playing Card
    private Drawable getCardAsDrawable(PlayingCard playingCard) {
        Context context = getActivity().getApplicationContext();

        // Build card name
        String shapeName = PLAYING_CARD_PREFIX + playingCard.getRank().getNameString().toLowerCase()
                + playingCard.getSuit().name().toLowerCase().charAt(0);

        int shapeID = context.getResources()
                .getIdentifier(shapeName,DRAWABLE_TYPE_NAME,context.getPackageName());

        Drawable drawable = context.getResources().getDrawable(shapeID,null);

        return drawable;
    }

    //TODO : check if should be moved to another util class
    /**
     * Converts dp to px
     * @param dp size
     * @return size in px
     */
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics =
                getActivity().getApplicationContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    //TODO: Del demo for testing
    private void setDemoCards() {
        PlayingCard pc1 = new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.CLUBS);
        PlayingCard pc2 = new PlayingCard(PlayingCardRanks.TWO, PlayingCardSuits.CLUBS);
        PlayingCard pc3 = new PlayingCard(PlayingCardRanks.THREE, PlayingCardSuits.CLUBS);
        PlayingCard pc4 = new PlayingCard(PlayingCardRanks.FOUR, PlayingCardSuits.CLUBS);
        PlayingCard pc5 = new PlayingCard(PlayingCardRanks.FIVE, PlayingCardSuits.CLUBS);

        mPlayersHand.addCardToTop(pc1);
        mPlayersHand.addCardToTop(pc2);
        mPlayersHand.addCardToTop(pc3);
        mPlayersHand.addCardToTop(pc4);
        mPlayersHand.addCardToTop(pc5);

        PlayingCard dpc1 = new PlayingCard(PlayingCardRanks.ACE, PlayingCardSuits.HEARTS);
        PlayingCard dpc2 = new PlayingCard(PlayingCardRanks.TWO, PlayingCardSuits.HEARTS);
        PlayingCard dpc3 = new PlayingCard(PlayingCardRanks.THREE, PlayingCardSuits.HEARTS);
        //PlayingCard dpc4 = new PlayingCard(PlayingCardRanks.FOUR, PlayingCardSuits.HEARTS);
        //PlayingCard dpc5 = new PlayingCard(PlayingCardRanks.FIVE, PlayingCardSuits.HEARTS);
        PlayingCard dpc4 = new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE);
        PlayingCard dpc5 = new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE);

        mDiscardedCards.addCardToTop(dpc1);
        mDiscardedCards.addCardToTop(dpc2);
        mDiscardedCards.addCardToTop(dpc3);
        mDiscardedCards.addCardToTop(dpc4);
        mDiscardedCards.addCardToTop(dpc5);

    }

}
