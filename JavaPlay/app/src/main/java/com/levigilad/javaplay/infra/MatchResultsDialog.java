package com.levigilad.javaplay.infra;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.ParticipantResult;
import com.levigilad.javaplay.R;

import java.util.ArrayList;

/**
 * This dialog is in charge of displaying match results
 */
public class MatchResultsDialog extends Dialog {
    /**
     * Designer
     */
    private Button mRematchButton;
    private TableLayout mMatchResultsTableLayout;

    /**
     * Constructor
     * @param context as the application context
     */
    public MatchResultsDialog(Context context, String participantId,
                              ArrayList<Participant> participantsData,
                              boolean canRematch) {
        super(context);

        initializeDialog(participantId, participantsData, canRematch);
    }

    /**
     * Initializes viewer
     * @param participantId Id of participant
     * @param participantsData participant's scores
     * @param canRematch is a rematch possible
     */
    private void initializeDialog(String participantId,
                                  ArrayList<Participant> participantsData,
                                  boolean canRematch) {
        this.setContentView(R.layout.dialog_match_results);
        mRematchButton = (Button) findViewById(R.id.rematch_button);

        if (canRematch) {
            mRematchButton.setVisibility(View.VISIBLE);
        } else {
            mRematchButton.setVisibility(View.GONE);
        }

        mMatchResultsTableLayout = (TableLayout)findViewById(R.id.match_results_table_layout);

        for (Participant participant : participantsData) {
            TableRow participantRow = new TableRow(getContext());

            ImageView participantIV = new ImageView(getContext());
            ImageManager.create(getContext()).loadImage(participantIV, participant.getIconImageUri());
            participantIV.setPadding(10,10,10,10);
            participantRow.addView(participantIV);

            TextView participantTextView = new TextView(getContext());
            participantTextView.setText(participant.getDisplayName());
            participantTextView.setTextColor(Color.BLACK);
            participantTextView.setPadding(10,10,10,10);

            participantRow.addView(participantTextView);

            TextView resultTextView = new TextView(getContext());

            switch (participant.getResult().getResult()) {
                case ParticipantResult.MATCH_RESULT_WIN: {
                    resultTextView.setText("WIN");
                    break;
                }
                case ParticipantResult.MATCH_RESULT_TIE: {
                    resultTextView.setText("TIE");
                    break;
                }
                case ParticipantResult.MATCH_RESULT_LOSS: {
                    resultTextView.setText("LOSS");
                    break;
                }
                default: {
                    resultTextView.setText("UNKNOWN");
                    break;
                }
            }

            resultTextView.setTextColor(Color.BLACK);
            resultTextView.setPadding(10,10,10,10);
            participantRow.addView(resultTextView);

            if (participant.getParticipantId().equals(participantId)) {
                participantTextView.setTypeface(null, Typeface.BOLD);
                resultTextView.setTypeface(null, Typeface.BOLD);
            }

            mMatchResultsTableLayout.addView(participantRow);
        }
    }

    /**
     * Sets a listener for rematch callback
     * @param listener listener
     */
    public void setRematchOnClickListener(View.OnClickListener listener) {
        if (listener != null) {
            mRematchButton.setOnClickListener(listener);
        }
    }
}