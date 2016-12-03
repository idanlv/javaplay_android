package com.levigilad.javaplay.yaniv;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.ParticipantResult;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.ActivityUtils;
import com.levigilad.javaplay.infra.PlayFragment;
import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.PlayingCard;
import com.levigilad.javaplay.infra.enums.PlayingCardState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Yaniv Play Fragment, Game Flow :<BR>
 * 1) Deal cards to all players<BR>
 * 2) Submit Turn :<BR>
 *      1) Yaniv declaration YES\NO<BR>
 *      2) Discard cards<BR>
 *      3) Take cards from deck form last player tryDiscard<BR>
 * 3) Process win
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
    private static final int MARKED_IMAGE_BACKGROUND = Color.BLUE;
    private static final int GLOBAL_TEXT_COLOR = Color.BLACK;

    /**
     * Members
     */
    private boolean mDrawCard;
    private int mDiscardCardsCount;

    /**
     * Designer
     */
    private ImageView mDeckIV;
    private Button mDiscardBtn;
    private Button mYanivBtn;
    private LinearLayout mHandLL;
    private LinearLayout mDiscardedCardsLL;
    private TableLayout mPlayersCardsCountTBLL;
    private TextView mScoreTV;
    private TextView mInstructionsTV;

    /**
     * Required empty constructor
     */
    public YanivPlayFragment() {
        super(new YanivTurn(), ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
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

    /**
     * Use this factory method to load a new instance of
     * this fragment using existing data.
     *
     * @return A new instance of fragment YanivPlayFragment.
     */
    public static YanivPlayFragment newInstance(TurnBasedMatch match) {
        YanivPlayFragment fragment = new YanivPlayFragment();
        Bundle args = new Bundle();
        args.putParcelable(MATCH_ID, match);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * onCreateView: Initializes the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_yaniv_game, container, false);
        initializeView(view);

        return view;
    }

    /**
     * Initialize fragment view
     * @param parentView as the parent layout for this fragment
     */
    private void initializeView(View parentView) {
        // Set members
        mDrawCard = false;

        // Hook id's
        mHandLL = (LinearLayout) parentView.findViewById(R.id.ll_hand);
        mDiscardedCardsLL = (LinearLayout) parentView.findViewById(R.id.ll_discarded);
        mDeckIV = (ImageView) parentView.findViewById(R.id.iv_deck);
        mDiscardBtn = (Button) parentView.findViewById(R.id.btn_discard);
        mYanivBtn = (Button) parentView.findViewById(R.id.btn_Yaniv);
        mPlayersCardsCountTBLL = (TableLayout) parentView.findViewById(R.id.tbll_players_card_count);
        mScoreTV = (TextView) parentView.findViewById(R.id.tv_score);
        mInstructionsTV = (TextView) parentView.findViewById(R.id.tv_instructions);

        // Set attributes
        mInstructionsTV.setTextColor(GLOBAL_TEXT_COLOR);

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

        disableGui();
    }

    /**
     * Clicked the tryDiscard button
     */
    private void discard() {
        if (YanivGame.tryDiscard(getCurrentParticipantId(), (YanivTurn) mTurnData)) {
            mYanivBtn.setEnabled(false);
            mDiscardBtn.setEnabled(false);
            ActivityUtils.setEnabledRecursively(mDiscardedCardsLL, true);
            mDeckIV.setEnabled(true);

            updatePlayerHandView();
            mDrawCard = true;
        }
    }

    /**
     * Show the cards in the players hand and set the listeners for the cards
     */
    private void updatePlayerHandView(){
        // Clear all cards from view
        mHandLL.removeAllViews();

        // Draw cards
        Iterator<PlayingCard> it = getCurrPlayersHand().iterator();
        while (it.hasNext()) {
            PlayingCard playingCard = it.next();
            mHandLL.addView(createImageView(playingCard));
        }

        mScoreTV.setText(String.valueOf(YanivGame.calculateDeckScore(getCurrPlayersHand())));
        mScoreTV.setText(String.valueOf(YanivGame.calculateDeckScore(getCurrPlayersHand())));
    }

    /**
     * Creates a view of a playing card
     * @param playingCard playing card to create image from
     * @return A new image view
     */
    private ImageView createImageView(PlayingCard playingCard) {
        ImageView img = new ImageView(mAppContext);

        Drawable drawable = ActivityUtils.getCardAsDrawable(playingCard, mAppContext);
        img.setImageDrawable(drawable);

        img.setLayoutParams(new LinearLayout.LayoutParams(
                ActivityUtils.dpToPx(CARD_WIDTH_DP, mAppContext),
                ActivityUtils.dpToPx(CARD_HEIGHT_DP,mAppContext)));
        img.setAdjustViewBounds(true);

        img.setPadding(PADDING_AS_RECT_SIZE,
                PADDING_AS_RECT_SIZE,
                PADDING_AS_RECT_SIZE,
                PADDING_AS_RECT_SIZE);
        img.setActivated(false);

        img.setTag(playingCard);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerCardOnClick(v);
            }
        });

        return img;
    }

    /**
     * Mark the card that was clicked
     * @param v as clicked view
     */
    private void playerCardOnClick(View v) {
        PlayingCard card = (PlayingCard) v.getTag();
        if (v.isActivated()) {
            v.setAlpha(LIGHTED_IMAGE_VIEW_ALPHA);
            v.setBackgroundColor(Color.TRANSPARENT);
            v.setActivated(false);
            card.setState(PlayingCardState.AVAILABLE);
            mDiscardCardsCount--;
        } else {
            v.setAlpha(DIMMED_IMAGE_VIEW_ALPHA);
            v.setBackgroundColor(MARKED_IMAGE_BACKGROUND);
            v.setActivated(true);
            card.setState(PlayingCardState.DISCARDED);
            mDiscardCardsCount++;
        }

        mDiscardBtn.setEnabled(mDiscardCardsCount > 0);
    }

    /**
     * Show the available discarded cards and set the listeners
     */
    private void updateAvailableDiscardView(){
        PlayingCard playingCard;
        Drawable drawable;
        ImageView img;

        // Clear all cards from view
        mDiscardedCardsLL.removeAllViews();

        // Draw cards
        Iterator<PlayingCard> it = getAvailableDiscardedCards().iterator();
        while (it.hasNext()) {
            playingCard = it.next();
            img = new ImageView(mAppContext);

            drawable = ActivityUtils.getCardAsDrawable(playingCard, mAppContext);
            img.setImageDrawable(drawable);

            img.setLayoutParams(new LinearLayout.LayoutParams(
                    ActivityUtils.dpToPx(CARD_WIDTH_DP,mAppContext),
                    ActivityUtils.dpToPx(CARD_HEIGHT_DP,mAppContext)));
            img.setAdjustViewBounds(true);

            img.setPadding(PADDING_AS_RECT_SIZE,
                    PADDING_AS_RECT_SIZE,
                    PADDING_AS_RECT_SIZE,
                    PADDING_AS_RECT_SIZE);
            img.setActivated(false);
            img.setTag(playingCard);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawCardFromDiscardedDeck(v);
                }
            });

            mDiscardedCardsLL.addView(img);
        }
    }

    /**
     * Get the selected card from available discarded pile
     * @param v as the clicked view (card)
     */
    private void drawCardFromDiscardedDeck(View v) {
        if (mDrawCard) {
            ActivityUtils.setEnabledRecursively(mDiscardedCardsLL, false);
            mDeckIV.setEnabled(false);
            PlayingCard drawnCard = (PlayingCard) v.getTag();
            YanivGame.onDrawFromDiscardedDeck(
                    getCurrentParticipantId(), (YanivTurn) mTurnData, drawnCard);

            mDrawCard = false;
            finishTurn();
        }
    }

    /**
     * Draw a new card from the global card deck
     * @param v as the View clicked
     */
    private void drawCardFromDeck(View v) {
        if (mDrawCard) {
            ActivityUtils.setEnabledRecursively(mDiscardedCardsLL, false);
            mDeckIV.setEnabled(false);
            YanivGame.onDrawFromGlobalDeck(getCurrentParticipantId(), (YanivTurn) mTurnData);
            mDrawCard = false;

            finishTurn();
        }
    }

    /**
     * Play the end of turn of the player
     */
    private void finishTurn(){
        super.finishTurn(getNextParticipantId());
        mInstructionsTV.setText(R.string.games_waiting_for_other_player_turn);
        Log.i(TAG,"Turn Ended");
    }


    /**
     * Declare Yaniv and finish game session
     */
    private void declareYaniv() {
        String winnerID;

        disableGui();
        winnerID = YanivGame.declareYanivWinner(
                getCurrentParticipantId(),getCurrPlayersHand(),getPlayersHands());

        // Show win status
        if (winnerID.equals(getCurrentParticipantId())) {
            mInstructionsTV.setText(R.string.games_you_win);
            Toast.makeText(mAppContext, R.string.games_you_win, Toast.LENGTH_LONG).show();
            Games.Achievements.unlockImmediate(getApiClient(),
                    getString(R.string.achievement_first_yaniv_win));
        } else {
            Toast.makeText(mAppContext, R.string.games_you_lose, Toast.LENGTH_SHORT).show();
            mInstructionsTV.setText(R.string.games_you_lose);
        }

        processWin(winnerID);
    }

    /**
     * Processes a win result
     * @param winnerParticipantId as String of winner ID
     */
    private void processWin(String winnerParticipantId) {
        List<ParticipantResult> results = new LinkedList<>();

        results.add(new ParticipantResult(
                winnerParticipantId, ParticipantResult.MATCH_RESULT_WIN,
                ParticipantResult.PLACING_UNINITIALIZED));

        YanivTurn yanivTurn = (YanivTurn)mTurnData;

        // Create lose result for other participants
        for (String participantId : mMatch.getParticipantIds()) {
            if (!participantId.equals(winnerParticipantId)) {
                results.add(new ParticipantResult(
                        participantId, ParticipantResult.MATCH_RESULT_LOSS,
                        ParticipantResult.PLACING_UNINITIALIZED));
            }

            Games.Leaderboards.submitScoreImmediate(getApiClient(),
                    getString(R.string.yaniv_leaderboard_id),
                    YanivGame.calculateDeckScore(yanivTurn.getPlayerHand(participantId)));
        }

        finishMatch(results);

        //  Unlock Achievements
        Games.Achievements.unlockImmediate(getApiClient(),
                getString(R.string.achievement_first_yaniv_win));
    }

    /**
     * Display players' card count
     */
    private void showPlayersCardCount() {
        HashMap<String, DeckOfCards> users = getPlayersHands();
        mPlayersCardsCountTBLL.removeAllViews();

        //Generate title row
        TableRow tblrowTitle = new TableRow(mAppContext);
        TextView tvTitle = new TextView(mAppContext);
        tvTitle.setText(R.string.yaniv_players_cards_count);
        tvTitle.setTextColor(GLOBAL_TEXT_COLOR);
        tvTitle.setGravity(Gravity.CENTER);
        // span on the entire line
        TableRow.LayoutParams trpTitle = new TableRow.LayoutParams();
        trpTitle.weight = 1;
        trpTitle.setMargins(1,1,1,1);
        tvTitle.setLayoutParams(trpTitle);

        tblrowTitle.addView(tvTitle);
        mPlayersCardsCountTBLL.addView(tblrowTitle);

        // Generate data rows

        for (Participant participant : mMatch.getParticipants()) {
            TableRow tblrowUser = new TableRow(mAppContext);

            // User Name
            TextView tvUserName = new TextView(mAppContext);
            tvUserName.setText(participant.getDisplayName());
            tvUserName.setTextColor(GLOBAL_TEXT_COLOR);
            tvUserName.setGravity(Gravity.START);
            tvUserName.setPaddingRelative(0,0,10,0);
            tblrowUser.addView(tvUserName);

            // Card Count
            int numOfCards = users.get(participant.getParticipantId()).size();
            TextView tvUserCardCount = new TextView(mAppContext);
            tvUserCardCount.setText(String.valueOf(numOfCards));
            tvUserCardCount.setTextColor(GLOBAL_TEXT_COLOR);
            tvUserCardCount.setGravity(Gravity.END);
            tblrowUser.addView(tvUserCardCount);

            mPlayersCardsCountTBLL.addView(tblrowUser);
        }
    }

    /**
     * Generate the playing deck's
     */
    private void dealCards() {
        ArrayList<String> participantIds = mMatch.getParticipantIds();
        mTurnData = YanivGame.initiateMatch(mMatch.getParticipantIds());
    }

    /**
     * Disable playing gui between turns
     */
    private void disableGui() {
        ActivityUtils.setEnabledRecursively(mHandLL, false);
        ActivityUtils.setEnabledRecursively(mDiscardedCardsLL, false);

        mDeckIV.setEnabled(false);
        mDiscardBtn.setEnabled(false);

        mYanivBtn.setEnabled(false);
    }

    /**
     * Start of match
     */
    @Override
    protected void startMatch() {
        Log.d(TAG,"Match Started");

        mDiscardCardsCount = 0;

        mTurnData = YanivGame.initiateMatch(mMatch.getParticipantIds());
        mInstructionsTV.setText(getString(R.string.games_waiting_for_other_player_turn));

        Toast.makeText(mAppContext, R.string.games_waiting_for_other_player_turn, Toast.LENGTH_LONG).show();
    }

    /**
     * User take turn
     */
    @Override
    protected void startTurn() {
        Log.i(TAG,"Start of Turn");
        mYanivBtn.setEnabled(YanivGame.canYaniv(getCurrPlayersHand()));
        ActivityUtils.setEnabledRecursively(mHandLL, true);

        mDiscardCardsCount = 0;

        mInstructionsTV.setText(getString(R.string.games_play_your_turn));
        Toast.makeText(mAppContext, R.string.games_play_your_turn, Toast.LENGTH_LONG).show();

    }

    /**
     * Update the user view
     */
    @Override
    protected void updateView() {
        updateAvailableDiscardView();
        updatePlayerHandView();
        showPlayersCardCount();
    }

    /**
     * Get players hands from YanivTurn
     * @return players hands as HashMap from YanivTurn
     */
    public HashMap<String, DeckOfCards> getPlayersHands() {
        return ((YanivTurn)mTurnData).getPlayersHands();
    }

    /**
     * Get available discarded cards from YanivTurn
     * @return get available discarded cards as DeckOfCards from YanivTurn
     */
    private DeckOfCards getAvailableDiscardedCards() {
        return ((YanivTurn)mTurnData).getAvailableDiscardedCards();
    }

    /**
     * Get my current deck of cards from YanivTurn
     * @return get my current deck of cards as DeckOfCards from YanivTurn
     */
    private DeckOfCards getCurrPlayersHand() {
        return ((YanivTurn)mTurnData).getPlayersHands().get(getCurrentParticipantId());
    }
}
