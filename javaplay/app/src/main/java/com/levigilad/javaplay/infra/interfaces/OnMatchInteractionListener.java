package com.levigilad.javaplay.infra.interfaces;

import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 */
public interface OnMatchInteractionListener {
    void onMatchLoaded(TurnBasedMatch match);
}
