package com.levigilad.javaplay.infra.interfaces;

/**
 * Created by User on 22/10/2016.
 */

/**
 * Callbacks interface that all activities using this fragment must implement.
 */
public interface NavigationDrawerCallbacks {
    /**
     * Called when an item in the navigation drawer is selected.
     * @param position The position of the selected item
     */
    void onNavigationDrawerItemSelected(int position);
}