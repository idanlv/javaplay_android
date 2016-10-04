package com.levigilad.javaplay.infra;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.entities.PlayingCard;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by User on 04/10/2016.
 */

public class CardsArrayAdapter extends ArrayAdapter<PlayingCard> {
    private final Context _context;
    private List<PlayingCard> _cards;

    public CardsArrayAdapter(Context context, int resource, PlayingCard[] cards) {
        super(context, resource, cards);

        _cards = new LinkedList<>();
        _context = context;

        loadCards(cards);
    }

    private void loadCards(PlayingCard[] cards) {
        _cards.clear();

        for (int i = 0; i < cards.length; i++) {
            _cards.add(new PlayingCard(cards[i]));
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("CardsArrayAdapter", "Load cards view");
        LayoutInflater inflater =
                (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_card, parent, false);

        ImageView cardImageView = (ImageView) rowView.findViewById(R.id.card_image_view);
        cardImageView.setImageResource(_cards.get(position).getDrawableId());

        return rowView;
    }
}
