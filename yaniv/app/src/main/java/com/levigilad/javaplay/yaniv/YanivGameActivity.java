package com.levigilad.javaplay.yaniv;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.StackView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.GameActivity;
import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.PlayingCard;
import com.levigilad.javaplay.infra.enums.GameCardRanks;
import com.levigilad.javaplay.infra.enums.GameCardSuits;

import java.util.LinkedList;
import java.util.List;

public class YanivGameActivity extends GameActivity implements View.OnClickListener {

    private static final String TAG = "YanivGameActivity";
    private YanivGame _game;
    private List<PlayingCard> _hand = new LinkedList<>();
    private List<PlayingCard> _cardsToDiscard = new LinkedList<>();

    // Designer members
    private LinearLayout _playerDataLinearLayout;
    private Button _discardButton;
    private TextView _instructionsTextView;
    private StackView _deckStackView;
    private ImageView _deckImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_yaniv_game);;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        _game = new YanivGame();

        initializeViews();

    }

    private void initializeViews() {
        _playerDataLinearLayout = (LinearLayout) findViewById(R.id.player_data_linear_layout);

        // Remove stub image which was created for design purposes
        View stubImage = findViewById(R.id.card_stub_image_view);
        _playerDataLinearLayout.removeView(stubImage);

        _discardButton = (Button)findViewById(R.id.discard_button);
        _discardButton.setEnabled(false);
        _discardButton.setOnClickListener(this);

        _instructionsTextView = (TextView)findViewById(R.id.instructions_text_view);

        _deckStackView = (StackView)findViewById(R.id.deck_stack_view);

        _deckImageView = (ImageView)findViewById(R.id.deck_image_view);
        _deckImageView.setOnClickListener(this);
    }

    @Override
    protected void setWaitView() {

    }

    @Override
    protected void setTurnView() {
        setHandView();
        setDeckView();
    }

    private void setDeckView() {
    }

    private void setHandView() {
        try
        {
            ImageView t = new ImageView(getBaseContext());
            t.setOnClickListener(this);
            t.setPadding(10, 10, 10, 10);

            t.setImageDrawable(getResources().getDrawable(R.drawable.ic_joker));
            t.setTag(R.string.playing_card_id, new PlayingCard(GameCardRanks.JOKER, GameCardSuits.NONE));

            _playerDataLinearLayout.addView(t);
        } catch (Exception ex) {
            String m = ex.getMessage();
        }

    }

    @Override
    protected void updateMatch(TurnBasedMatch match) {

    }

    @Override
    protected void startNewMatch(TurnBasedMatch match) {
        try {
            DeckOfCards cards = _game.generateDeck(2);

            YanivTurn turnData = new YanivTurn();

            _hand.add(cards.pop());
            turnData.setAvailableDeck(cards);

            String playerId = Games.Players.getCurrentPlayerId(getApiClient());
            String myParticipantId = match.getParticipantId(playerId);

            finishTurn(match.getMatchId(), myParticipantId, turnData.export());
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            //TODO
        }
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

            _discardButton.setEnabled(true);
        } else {
            _cardsToDiscard.remove(card);
            view.setRotation(0);

            if (_cardsToDiscard.size() == 0) {
                _discardButton.setEnabled(false);
            }
        }
    }

    private void discardButtonOnClicked() {
        boolean isValid = _game.isCardsDiscardValid(_cardsToDiscard);

        if (!isValid) {
            Toast.makeText(this, "Invalid discard", Toast.LENGTH_SHORT);
        } else {
            _discardButton.setEnabled(false);
        }
    }
}