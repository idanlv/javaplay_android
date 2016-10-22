package com.levigilad.javaplay.infra.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.enums.GameOptions;

import java.util.ArrayList;

/**
 * This class is in charge of creating a view for each game option
 */
public class GameOptionsAdapter extends ArrayAdapter<String>{

    /**
     * Constructor
     * @param context The context in which we create the viewers
     * @param resource The resource for the layout of each view
     */
    public GameOptionsAdapter(Context context, int resource) {
        super(context, resource, new ArrayList<String>(){
            {
                for (GameOptions option : GameOptions.values()) {
                    add(option.name());
                }
            }});
    }

    /**
     * Creates a view object for given position
     * @param position Item position
     * @param convertView
     * @param parent The parent view
     * @return View for item
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String option = this.getItem(position);
        // Assign when a non existing drawable id
        int drawable_id = 0;

        // Attach the relevant drawable
        switch (GameOptions.valueOf(option)) {
            case LEADERBOARDS: {
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
