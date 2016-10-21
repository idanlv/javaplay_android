package com.levigilad.javaplay.infra.interfaces;

import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;

public interface OnTurnBasedMatchReceivedListener {
    void onTurnBasedMatchReceived(TurnBasedMatch match);
}
