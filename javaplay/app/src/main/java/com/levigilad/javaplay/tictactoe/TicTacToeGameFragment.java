package com.levigilad.javaplay.tictactoe;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.ParticipantResult;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.PlayFragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TicTacToeGameFragment extends PlayFragment implements View.OnClickListener {
    private static final String TAG = "TicTacToeGameFragment";

    private TicTacToeSymbol mCurrentPlayerSymbol;

    /**
     * Designer
     */
    private TableLayout mTableLayoutBoard;
    private TextView mInstructionsTextView;

    public TicTacToeGameFragment() {
        super();

        this.mTurnData = new TicTacToeTurn();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment YanivPlayFragment.
     */
    public static TicTacToeGameFragment newInstance(ArrayList<String> invitees,
                                                    Bundle autoMatchCriteria) {
        TicTacToeGameFragment fragment = new TicTacToeGameFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(INVITEES, invitees);
        args.putBundle(AUTO_MATCH, autoMatchCriteria);
        fragment.setArguments(args);
        return fragment;
    }

    public static TicTacToeGameFragment newInstance(TurnBasedMatch match) {
        TicTacToeGameFragment fragment = new TicTacToeGameFragment();
        Bundle args = new Bundle();
        args.putParcelable(MATCH_ID, match);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tic_tac_toe_game, container, false);
        initializeView(view);

        return view;
    }

    private void initializeView(View parentView) {
        // Fragment locked in portrait screen orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        mTableLayoutBoard = (TableLayout) parentView.findViewById(R.id.table_layout_board);
        mTableLayoutBoard.setEnabled(false);

        for (int i = 0; i < mTableLayoutBoard.getChildCount(); i++) {
            TableRow row = (TableRow) mTableLayoutBoard.getChildAt(i);

            for (int j = 0; j < row.getChildCount(); j++) {
                Button cell = (Button) row.getChildAt(j);
                cell.setOnClickListener(this);
            }
        }

        mInstructionsTextView =
                (TextView)parentView.findViewById(R.id.tic_tac_toe_instructions_text_view);

        setEnabledRecursively(mTableLayoutBoard, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void startMatch() {
        try {
            String playerId = Games.Players.getCurrentPlayerId(getApiClient());
            String participantId = mMatch.getParticipantId(playerId);

            mCurrentPlayerSymbol = ((TicTacToeTurn)mTurnData).addParticipant(participantId);

            mInstructionsTextView.setText(getString(R.string.games_waiting_for_player_turn));
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    protected void startTurn() {
        if (mCurrentPlayerSymbol == null) {
            String participantId = getCurrentParticipantId();
            mCurrentPlayerSymbol = ((TicTacToeTurn)mTurnData).getParticipantSymbol(participantId);

            if (mCurrentPlayerSymbol == TicTacToeSymbol.NONE) {
                mCurrentPlayerSymbol = ((TicTacToeTurn)mTurnData).addParticipant(participantId);
            }
        }

        mInstructionsTextView.setText(getString(R.string.games_play_your_turn));

        setEnabledRecursively(mTableLayoutBoard, true);
    }

    @Override
    protected void updateView() {
        Board board = ((TicTacToeTurn)mTurnData).getBoard();

        for (int i = 0; i < board.ROWS; i++) {
            TableRow row = (TableRow) mTableLayoutBoard.getChildAt(i);

            for (int j = 0; j < board.COLUMNS; j++) {
                Button cell = (Button) row.getChildAt(j);

                String text;

                switch (board.getCell(i, j)) {
                    case X: {
                        text = TicTacToeSymbol.X.name();
                        break;
                    }
                    case O: {
                        text = TicTacToeSymbol.O.name();
                        break;
                    }
                    default: {
                        text = getString(R.string.tictactoe_empty_cell);
                    }
                }

                cell.setText(text);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1: {
                btnCell_OnClick(0, 0);
                break;
            }
            case R.id.button2: {
                btnCell_OnClick(0, 1);
                break;
            }
            case R.id.button3: {
                btnCell_OnClick(0, 2);
                break;
            }
            case R.id.button4: {
                btnCell_OnClick(1, 0);
                break;
            }
            case R.id.button5: {
                btnCell_OnClick(1, 1);
                break;
            }
            case R.id.button6: {
                btnCell_OnClick(1, 2);
                break;
            }
            case R.id.button7: {
                btnCell_OnClick(2, 0);
                break;
            }
            case R.id.button8: {
                btnCell_OnClick(2, 1);
                break;
            }
            case R.id.button9: {
                btnCell_OnClick(2, 2);
                break;
            }
        }
    }

    public void btnCell_OnClick(int row, int column) {
        try {
            ((TicTacToeTurn)mTurnData).getBoard().setCell(mCurrentPlayerSymbol, row, column);

            setEnabledRecursively(mTableLayoutBoard, false);

            if (TicTacToeGame.isWin(((TicTacToeTurn)mTurnData).getBoard(), mCurrentPlayerSymbol)) {
                String winnerParticipantId = getCurrentParticipantId();

                List<ParticipantResult> results = new LinkedList<>();

                results.add(new ParticipantResult(
                        winnerParticipantId, ParticipantResult.MATCH_RESULT_WIN,
                        ParticipantResult.PLACING_UNINITIALIZED));

                for (String participantId : mMatch.getParticipantIds()) {
                    if (!participantId.equals(winnerParticipantId)) {
                        results.add(new ParticipantResult(
                                participantId, ParticipantResult.MATCH_RESULT_LOSS,
                                ParticipantResult.PLACING_UNINITIALIZED));
                    }
                }

                finishMatch(results);

                Games.Achievements.unlockImmediate(getApiClient(),
                        getString(R.string.achievement_first_tic_tac_toe_win));

                Games.Achievements.incrementImmediate(getApiClient(),
                        getString(R.string.achievement_3_tic_tac_toe_wins), 1);

            } else if (TicTacToeGame.isTie(((TicTacToeTurn)mTurnData).getBoard())) {
                List<ParticipantResult> results = new LinkedList<>();

                for (String participantId : mMatch.getParticipantIds()) {
                    results.add(new ParticipantResult(
                            participantId, ParticipantResult.MATCH_RESULT_LOSS,
                            ParticipantResult.PLACING_UNINITIALIZED));
                }

                finishMatch(results);

                Games.Achievements.unlockImmediate(getApiClient(),
                        getString(R.string.achievement_first_tic_tac_toe_tie));
            } else {
                finishTurn(getNextParticipantId());
                mInstructionsTextView.setText(getString(R.string.games_waiting_for_player_turn));
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private void setEnabledRecursively(ViewGroup parentView, boolean enabled) {
        parentView.setEnabled(enabled);
        for (int i = 0; i < parentView.getChildCount(); i++) {
            View child = parentView.getChildAt(i);
            if (child instanceof ViewGroup) {
                setEnabledRecursively((ViewGroup) child, enabled);
            } else {
                if (enabled && (child instanceof Button)) {
                    Button btn = (Button) child;

                    if (btn.getText().equals(getString(R.string.tictactoe_empty_cell))) {
                        child.setEnabled(enabled);
                    }
                } else {
                    child.setEnabled(enabled);
                }
            }
        }
    }
}
