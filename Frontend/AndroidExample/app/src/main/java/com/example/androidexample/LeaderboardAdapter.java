package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {
    private ArrayList<LeaderboardItem> leaderboardItems;

    public LeaderboardAdapter(ArrayList<LeaderboardItem> leaderboardItems) {
        this.leaderboardItems = leaderboardItems;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iteam_leaderbord, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        LeaderboardItem item = leaderboardItems.get(position);
        holder.rankTextView.setText(String.valueOf(position + 1));
        holder.usernameTextView.setText(item.getUsername());
        holder.highScoreTextView.setText(String.valueOf(item.getHighScore()));
        holder.gamesPlayedTextView.setText(String.valueOf(item.getGamesPlayed()));
        holder.avgScoreTextView.setText(String.format(Locale.getDefault(), "%.2f", item.getAverageScore()));
    }

    @Override
    public int getItemCount() {
        return leaderboardItems.size();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView, usernameTextView, highScoreTextView, gamesPlayedTextView, avgScoreTextView;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.rankTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            highScoreTextView = itemView.findViewById(R.id.highScoreTextView);
            gamesPlayedTextView = itemView.findViewById(R.id.gamesPlayedTextView);
            avgScoreTextView = itemView.findViewById(R.id.avgScoreTextView);
        }
    }
}