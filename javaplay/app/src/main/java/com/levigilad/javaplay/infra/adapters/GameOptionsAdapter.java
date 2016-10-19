package com.levigilad.javaplay.infra.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.levigilad.javaplay.R;;
import com.levigilad.javaplay.infra.enums.GameOptions;

import java.util.ArrayList;

/**
 * Created by User on 08/10/2016.
 */

public class GameOptionsAdapter extends ArrayAdapter<String>{

    public GameOptionsAdapter(Context context, int resource) {
        super(context, resource, new ArrayList<String>(){
            {
                for (GameOptions option : GameOptions.values()) {
                    add(option.name().replace("_", " "));
                }
            }});
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String option = this.getItem(position);
        int drawable_id = 0;

        switch (GameOptions.valueOf(option.replace(" ", "_"))) {
            case LEADERBOARD: {
                drawable_id = R.drawable.game_leaderboards;
                break;
            }
            case INBOX: {
                drawable_id = R.drawable.game_invitation;
                break;
            }
            case ACHIEVEMENTS: {
                drawable_id = R.drawable.game_achievments;
                break;
            }
            case GAMES: {
                drawable_id = R.drawable.game_play;
                break;
            }
        }

        LayoutInflater inflater =
                (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View optionView = inflater.inflate(R.layout.game_option_list_item, parent, false);

        ImageView iconView = (ImageView) optionView.findViewById(R.id.section_icon);
        iconView.setImageResource(drawable_id);

        TextView labelView = (TextView) optionView.findViewById(R.id.section_label);
        labelView.setText(option);

        return optionView;
    }
}
