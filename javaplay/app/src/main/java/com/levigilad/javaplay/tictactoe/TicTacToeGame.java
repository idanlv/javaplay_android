package com.levigilad.javaplay.tictactoe;

import android.content.Context;

import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.entities.Game;

public class TicTacToeGame extends Game {
    /**
     * Constructor
     */
    public TicTacToeGame(Context context) {
        super(context.getString(R.string.tictactoe_game_id),
                context.getString(R.string.tictactoe_description),
                context.getString(R.string.tictactoe_leaderboard_id),
                2);
    }
}
