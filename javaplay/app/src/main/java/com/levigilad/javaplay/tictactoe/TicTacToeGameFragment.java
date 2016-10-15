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
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.PlayFragment;

import org.json.JSONException;

import java.util.ArrayList;

public class TicTacToeGameFragment extends PlayFragment implements View.OnClickListener {
    private static final String TAG = "TicTacToeGameFragment";

    private TicTacToeTurn mTurnData = null;
    private TableLayout mTableLayoutBoard;
    private TicTacToeSymbol mCurrentPlayerSymbol;
    private TurnBasedMatch mMatch;

    public TicTacToeGameFragment() {
        super();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment YanivPlayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TicTacToeGameFragment newInstance(ArrayList<String> invitees,
                                                    Bundle autoMatchCriteria) {
        TicTacToeGameFragment fragment = new TicTacToeGameFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(INVITEES, invitees);
        args.putBundle(AUTO_MATCH, autoMatchCriteria);
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
        mTableLayoutBoard.setActivated(false);

        for (int i = 0; i < mTableLayoutBoard.getChildCount(); i++) {
            TableRow row = (TableRow) mTableLayoutBoard.getChildAt(i);

            for (int j = 0; j < row.getChildCount(); j++) {
                Button cell = (Button) row.getChildAt(j);
                cell.setOnClickListener(this);
            }
        }
    }

    @Override
    protected void startMatch(TurnBasedMatch match) {
        try {
            String participantId = Games.Players.getCurrentPlayer(getApiClient()).getPlayerId();

            mCurrentPlayerSymbol = TicTacToeSymbol.X;
            mMatch = match;

            mTurnData = new TicTacToeTurn();
            mTurnData.addParticipant(participantId, mCurrentPlayerSymbol);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    protected void updateMatch(TurnBasedMatch match) {
        mMatch = match;
        mTableLayoutBoard.setActivated(true);
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

    public void btnCell_OnClick(int row, int column) {
        try {
            mTurnData.getBoard().setCell(mCurrentPlayerSymbol, row, column);

            mTableLayoutBoard.setActivated(false);

            finishTurn(mMatch.getMatchId(), null, mTurnData.export());
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
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
}
