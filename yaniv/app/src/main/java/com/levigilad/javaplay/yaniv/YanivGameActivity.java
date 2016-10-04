package com.levigilad.javaplay.yaniv;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.CardsArrayAdapter;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_yaniv_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        _game = new YanivGame();

        initializeViews();

    }

    private void initializeViews() {
        _playerDataLinearLayout = (LinearLayout) findViewById(R.id.player_data_linear_layout);

        // Remove stub image which was created for design purposes
        View stubImage = findViewById(R.id.card_stub_image_view);
        _playerDataLinearLayout.removeView(stubImage);
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
            t.setPadding(0, 20, 0, 0);

            t.setImageDrawable(getResources().getDrawable(R.drawable.two_clubs));
            t.setTag(R.string.playing_card_id, new PlayingCard(GameCardRanks.TWO, GameCardSuits.CLUBS));

            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayingCard card = (PlayingCard) v.getTag(R.string.playing_card_id);
                    Object discardedTag = v.getTag(R.bool.isCardDiscarded);

                    boolean shouldDiscard;

                    if (discardedTag == null) {
                        shouldDiscard = true;
                    } else {
                        shouldDiscard = !((boolean)discardedTag);
                    }

                    v.setTag(R.bool.isCardDiscarded, shouldDiscard);

                    if (shouldDiscard) {
                        _cardsToDiscard.add(card);
                        v.setPadding(0, 0, 0, 0);
                    } else {
                        _cardsToDiscard.remove(card);
                        v.setPadding(0, 20, 0, 0);
                    }
                }
            });


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
        if (v instanceof ImageButton) {

        }
    }
}
