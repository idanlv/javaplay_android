package com.levigilad.javaplay.infra.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.games.achievement.Achievement;
import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.interfaces.OnItemClickListener;

import java.util.ArrayList;

public class AchievementsRecyclerViewAdapter extends
        RecyclerView.Adapter<AchievementsRecyclerViewAdapter.AchievementHolder> {
    private static final String TAG = "GameAchievementsView";

    private ArrayList<Achievement> mAchievements;
    private static OnItemClickListener mListener;

    public static class AchievementHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImageView;
        TextView mName;
        TextView mDescription;

        public AchievementHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView)itemView.findViewById(R.id.achievement_image_view);
            mName = (TextView)itemView.findViewById(R.id.achievement_name_text_view);
            mDescription = (TextView)itemView.findViewById(R.id.achievement_description_text_view);

            Log.i(TAG, "Added listener");

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClicked(getAdapterPosition(), v);
        }
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public AchievementsRecyclerViewAdapter(ArrayList<Achievement> achievements) {
        this.mAchievements = achievements;
    }

    @Override
    public AchievementsRecyclerViewAdapter.AchievementHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_achievement_view, parent, false);

        return (new AchievementHolder(view));
    }

    @Override
    public void onBindViewHolder(AchievementHolder holder, int position) {
        Achievement achievement = mAchievements.get(position);

        holder.mName.setText(achievement.getName());
        holder.mDescription.setText(achievement.getDescription());
        // TODO: holder.mImage
    }

    public void addItem(Achievement achievement, int index) {
        mAchievements.add(index, achievement);
        notifyItemInserted(index);
    }

    public void removeItem(int index) {
        mAchievements.remove(index);
        notifyItemRemoved(index);
    }

    public Achievement getItem(int index) {
        return mAchievements.get(index);
    }

    @Override
    public int getItemCount() {
        return mAchievements.size();
    }
}
