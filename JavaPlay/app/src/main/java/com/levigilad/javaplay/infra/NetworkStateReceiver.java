package com.levigilad.javaplay.infra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements network change alerts
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    /**
     * Members
     */
    private List<NetworkStateReceiverListener> mListeners;
    private Boolean mConnected;

    /**
     * Constructor
     */
    public NetworkStateReceiver() {
        mListeners = new ArrayList<>();
        mConnected = null;
    }

    /**
     * Handles network change notification
     * @param context as application context
     * @param intent as the received intent
     */
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null) {
            return;
        }

        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = manager.getActiveNetworkInfo();

        // Network services are connected
        if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
            mConnected = true;
        }
        // No connectivity found
        else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
            mConnected = false;
        }

        if (mConnected != null) {
            notifyStateToAll();
        }
    }

    /**
     * Notifies all listeners
     */
    private void notifyStateToAll() {
        for (NetworkStateReceiverListener listener : mListeners) {
            notifyState(listener);
        }
    }

    /**
     * Notifies state to listener
     * @param listener Current listener
     */
    private void notifyState(NetworkStateReceiverListener listener) {
        if (mConnected == null || listener == null) {
            return;
        }

        if (mConnected) {
            listener.networkAvailable();
        } else {
            listener.networkUnavailable();
        }
    }

    /**
     * Adds a new listener and notifies on current state
     * @param listener New listener
     */
    public void addListener(NetworkStateReceiverListener listener) {
        mListeners.add(listener);
        notifyState(listener);
    }

    /**
     * Removes a listener
     * @param listener Listener to remove
     */
    public void removeListener(NetworkStateReceiverListener listener) {
        mListeners.remove(listener);
    }

    /**
     * Listener interface
     */
    public interface NetworkStateReceiverListener {
        void networkAvailable();
        void networkUnavailable();
    }
}