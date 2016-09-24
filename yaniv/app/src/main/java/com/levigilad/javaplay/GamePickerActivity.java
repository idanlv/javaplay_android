package com.levigilad.javaplay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.levigilad.javaplay.infra.Game;
import com.levigilad.javaplay.infra.Playground;

import java.util.ArrayList;

/**
 * This activity is the viewer for picking a game
 */
public class GamePickerActivity extends Activity implements
        View.OnClickListener, ListView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_picker);

        findViewById(R.id.continue_button).setOnClickListener(this);
        findViewById(R.id.continue_button).setClickable(false);

        ListView gameOptionsListView = (ListView)findViewById(R.id.game_options_listview);
        gameOptionsListView.setOnItemClickListener(this);

        loadGames(gameOptionsListView);
    }

    private void loadGames(ListView gameOptionsListView) {
        Playground playground = Playground.getInstance();

        ArrayList<String> names = new ArrayList<>();

        for (Game game : playground.getGames()) {
            names.add(game.getDisplayName());
        }

        ArrayAdapter<Game> adapter = new ArrayAdapter<>(
                this,
                R.layout.game_list_item,
                playground.getGames());

        gameOptionsListView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continue_button: {
                continue_button_OnClicked();
            }
        }
    }

    /**
     * Moves user into game activity
     */
    private void continue_button_OnClicked() {
        ListView gameOptions = (ListView)findViewById(R.id.game_options_listview);

        // Get clicked item
        Game gameName = (Game) gameOptions.getItemAtPosition((Integer) gameOptions.getTag());

        // TODO: Start game activity
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Enable continue button
        findViewById(R.id.continue_button).setClickable(true);

        ListView gameOptions = (ListView) findViewById(R.id.game_options_listview);

        if (gameOptions.getTag() != null) {
            view.setBackgroundColor(Color.WHITE);
        }

        // Save clicked item's position for future use
        gameOptions.setTag(position);
    }
}
