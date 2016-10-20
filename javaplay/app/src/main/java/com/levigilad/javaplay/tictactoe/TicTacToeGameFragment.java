package com.levigilad.javaplay.tictactoe;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.android.gms.games.Games;
import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.PlayFragment;

import org.json.JSONException;

import java.util.ArrayList;

public class TicTacToeGameFragment extends PlayFragment implements View.OnClickListener {
    private static final String TAG = "TicTacToeGameFragment";

    private TicTacToeSymbol mCurrentPlayerSymbol;
    private TicTacToeTurn mTurnData = null;

    /**
     * Designer
     */
    private TableLayout mTableLayoutBoard;

    public TicTacToeGameFragment() {
        super();
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

    public static TicTacToeGameFragment newInstance(String matchId) {
        TicTacToeGameFragment fragment = new TicTacToeGameFragment();
        Bundle args = new Bundle();
        args.putString(MATCH_ID, matchId);
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
        mTableLayoutBoard = (TableLayout) parentView.findViewById(R.id.table_layout_board);
        mTableLayoutBoard.setEnabled(false);

        for (int i = 0; i < mTableLayoutBoard.getChildCount(); i++) {
            TableRow row = (TableRow) mTableLayoutBoard.getChildAt(i);

            for (int j = 0; j < row.getChildCount(); j++) {
                Button cell = (Button) row.getChildAt(j);
                cell.setOnClickListener(this);
            }
        }

        setEnabledRecursively(mTableLayoutBoard, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mTurnData = new TicTacToeTurn();
    }

    @Override
    protected byte[] startMatch() {
        try {
            String playerId = Games.Players.getCurrentPlayerId(getApiClient());
            String participantId = mMatch.getParticipantId(playerId);

            mCurrentPlayerSymbol = mTurnData.addParticipant(participantId);

            return mTurnData.export();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

        return null;
    }

    @Override
    protected void updateMatch() {
        try {
            mTurnData.update(mMatch.getData());

            if (mCurrentPlayerSymbol == null) {
                String participantId = getCurrentParticipantId();
                mCurrentPlayerSymbol = mTurnData.getParticipantSymbol(participantId);

                if (mCurrentPlayerSymbol == TicTacToeSymbol.NONE) {
                    mCurrentPlayerSymbol = mTurnData.addParticipant(participantId);
                }
            }

            setEnabledRecursively(mTableLayoutBoard, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateView(byte[] turnData) {
        try {
            mTurnData.update(turnData);

            Board board = mTurnData.getBoard();

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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void askForRematch() {

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
            mTurnData.getBoard().setCell(mCurrentPlayerSymbol, row, column);

            setEnabledRecursively(mTableLayoutBoard, false);

            if (TicTacToeGame.isWin(mTurnData.getBoard(), mCurrentPlayerSymbol)) {
                finishMatch(mTurnData.export());
            } else if (TicTacToeGame.isTie(mTurnData.getBoard())) {
                finishMatch(mTurnData.export());
            } else {
                finishTurn(getNextParticipantId(), mTurnData.export());
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
                child.setEnabled(enabled);
            }
        }
    }
}
