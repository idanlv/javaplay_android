package com.levigilad.javaplay.infra;

import android.app.Dialog;
import android.content.Context;

import com.levigilad.javaplay.R;

public class MatchResultsDialog extends Dialog {
    public MatchResultsDialog(Context context) {
        super(context);
        initializeDialog();
        this.setContentView(R.layout.dialog_match_results);
    }

    private void initializeDialog() {

    }

    public void setRematch(boolean rematch) {

    }

    @Override
    public void show() {
        super.show();
    }
}
