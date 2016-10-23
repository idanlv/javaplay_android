package com.levigilad.javaplay.infra;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.levigilad.javaplay.infra.entities.PlayingCard;

public class ActivityUtils {

    public static final String PLAYING_CARD_PREFIX = "playingcard_";
    public static final String DRAWABLE_TYPE_NAME = "drawable";

    /**
     * Converts dp to px
     * @param dp size
     * @return size in px
     */
    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics =
                context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /**
     * Gets a playing card Drawable resource to place in an Image View
     * @param playingCard as the playing card to get his Drawable
     * @param context as the application context
     * @return the drawable resource of the playing card
     */
    public static Drawable getCardAsDrawable(PlayingCard playingCard, Context context) {

        // Build card name
        String shapeName = PLAYING_CARD_PREFIX + playingCard.getRank().getName().toLowerCase()
                + playingCard.getSuit().name().toLowerCase().charAt(0);

        int shapeID = context.getResources()
                .getIdentifier(shapeName,DRAWABLE_TYPE_NAME,context.getPackageName());

        Drawable drawable = context.getResources().getDrawable(shapeID,null);

        return drawable;
    }

    /**
     * Enables/Disables all inner views
     * @param parentView parent view
     * @param enabled Should enable or disable
     */
    public static void setEnabledRecursively(ViewGroup parentView, boolean enabled) {
        for (int i = 0; i < parentView.getChildCount(); i++) {
            View child = parentView.getChildAt(i);
            child.setEnabled(enabled);
            if (child instanceof ViewGroup){
                setEnabledRecursively((ViewGroup)child, enabled);
            }
        }
    }
}
