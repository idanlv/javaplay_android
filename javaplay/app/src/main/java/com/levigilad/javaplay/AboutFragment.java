package com.levigilad.javaplay;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.levigilad.javaplay.infra.adapters.GamesRecyclerViewAdapter;
import com.levigilad.javaplay.infra.entities.Game;
import com.levigilad.javaplay.infra.entities.Playground;
import com.levigilad.javaplay.infra.interfaces.OnGameSelectedListener;
import com.levigilad.javaplay.infra.interfaces.OnItemClickListener;

/**
 * This activity is the viewer for viewing about fragment
 */
public class AboutFragment extends Fragment {
    /**
     * Designer
     */
    private TextView mAboutTextView;

    /**
     * Use this factory method to create a new instance of this fragment
     * @return GamesFragment instance
     */
    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();

        return fragment;
    }

    /**
     * onCreateView: Initializes the fragment
     * @param inflater as inflater layout
     * @param container as ViewGroup of views
     * @param savedInstanceState Saved Bundle state
     * @return the created fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        mAboutTextView = (TextView) view.findViewById(R.id.about_version);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            Context context = this.getActivity().getApplicationContext();
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

            mAboutTextView.setText(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}