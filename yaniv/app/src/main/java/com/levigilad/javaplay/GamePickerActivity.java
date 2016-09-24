package com.levigilad.javaplay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.common.collect.ArrayListMultimap;
import com.levigilad.javaplay.infra.Game;
import com.levigilad.javaplay.infra.Playground;

/**
 * This activity is the viewer for picking a game
 */
public class GamePickerActivity extends AppCompatActivity implements
        View.OnClickListener, ListView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_picker);

        findViewById(R.id.continue_button).setOnClickListener(this);

        ListView gameOptionsListView = (ListView)findViewById(R.id.game_options_listview);
        gameOptionsListView.setOnItemClickListener(this);

        loadGames(gameOptionsListView);
    }

    private void loadGames(ListView gameOptionsListView) {
        Playground playground = Playground.getInstance();

        ArrayAdapter<Game> adapter = new ArrayAdapter<Game>(
                getApplicationContext(),
                R.layout.activity_game_picker,
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

        Game game = (Game) gameOptions.getSelectedItem();

        // TODO: Start game activity
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        findViewById(R.id.continue_button).setClickable(true);
    }
}
