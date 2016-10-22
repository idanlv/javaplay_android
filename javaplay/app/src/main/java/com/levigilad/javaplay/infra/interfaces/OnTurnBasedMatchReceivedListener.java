package com.levigilad.javaplay.infra.interfaces;

import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;

/**
 * This method must be implemented by an object which wishes to be informed
 * whenever a match is updated
 */
public interface OnTurnBasedMatchReceivedListener {
    /**
     * This method handles match update
     * @param match The updated match
     */
    void onTurnBasedMatchReceived(TurnBasedMatch match);
}
