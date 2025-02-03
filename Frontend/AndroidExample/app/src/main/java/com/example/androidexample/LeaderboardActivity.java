package com.example.androidexample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LeaderboardActivity extends AppCompatActivity {

    private static final String APP_URL = MainActivity.APP_URL;
    private static String CURRENT_USER, CURRENT_SERVER;

    private Button backBtn, homeBtn, tileStatsBtn, unscrambleStatsBtn, numberStatsBtn, triviaStatsBtn;
    private RecyclerView leaderboardRecycler;
    private LeaderboardAdapter leaderboardAdapter;
    private ArrayList<LeaderboardItem> leaderboardItems;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView leaderboardTitleText;
    private OkHttpClient client;
    private String currentLeaderboardType = "trivia"; // Default to trivia

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        CURRENT_USER = HomeActivity.currentUser;
        CURRENT_SERVER = DashboardActivity.currentServer;

        // Initialize OkHttpClient
        client = new OkHttpClient();

        // Initialize views
        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);
        tileStatsBtn = findViewById(R.id.btnTF);
        unscrambleStatsBtn = findViewById(R.id.btnU);
        numberStatsBtn = findViewById(R.id.btnNG);
        triviaStatsBtn = findViewById(R.id.btnT);
        leaderboardRecycler = findViewById(R.id.leaderboardRecycler);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        leaderboardTitleText = findViewById(R.id.leaderboardTitleText);

        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);

        // Initialize data structures
        leaderboardItems = new ArrayList<>();

        // Initialize adapter and set up recycler view
        leaderboardAdapter = new LeaderboardAdapter(leaderboardItems);
        leaderboardRecycler.setLayoutManager(new LinearLayoutManager(this));
        leaderboardRecycler.setAdapter(leaderboardAdapter);

        // Setup swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(() -> fetchLeaderboardStats());

        // Call initializers
        setupButtons();
        fetchLeaderboardStats(); // Initial fetch
    }

    private void fetchLeaderboardStats() {
        String url = APP_URL;
        switch (currentLeaderboardType) {
            case "trivia":
                url += "/trivia/stats";
                break;
            case "unscramble":
                url += "/unscramble/stats";
                break;
            case "tile_flip":
                url += "/flipthetile/stats";
                break;
            case "number_guess":
                url += "/guess-the-number/stats";
                break;
        }

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showError("Failed to fetch leaderboard: " + e.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    showError("Server error: " + response.code());
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }

                try {
                    String jsonData = response.body().string();
                    JSONArray jsonArray = new JSONArray(jsonData);

                    runOnUiThread(() -> {
                        leaderboardItems.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                LeaderboardItem item = new LeaderboardItem(
                                        jsonObject.getString("username"),
                                        jsonObject.getInt("highScore"),
                                        jsonObject.getLong("gamesPlayed"),
                                        jsonObject.getDouble("averageScore")
                                );
                                leaderboardItems.add(item);
                            } catch (JSONException e) {
                                showError("Error parsing JSON: " + e.getMessage());
                            }
                        }
                        leaderboardAdapter.notifyDataSetChanged();

                        // Update title based on current leaderboard type
                        String title = "";
                        switch (currentLeaderboardType) {
                            case "trivia":
                                title = "TRIVIA LEADERBOARD";
                                break;
                            case "unscramble":
                                title = "UNSCRAMBLE LEADERBOARD";
                                break;
                            case "tile_flip":
                                title = "TILE FLIP LEADERBOARD";
                                break;
                            case "number_guess":
                                title = "NUMBER GUESS LEADERBOARD";
                                break;
                        }
                        leaderboardTitleText.setText(title);

                        swipeRefreshLayout.setRefreshing(false);
                    });
                } catch (JSONException e) {
                    showError("JSON parsing error: " + e.getMessage());
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void setupButtons() {
        homeBtn.setOnClickListener(v -> startActivity(new Intent(LeaderboardActivity.this, HomeActivity.class)));
        backBtn.setOnClickListener(v -> startActivity(new Intent(LeaderboardActivity.this, DashboardActivity.class)));

        triviaStatsBtn.setOnClickListener(v -> {
            currentLeaderboardType = "trivia";
            fetchLeaderboardStats();
        });

        unscrambleStatsBtn.setOnClickListener(v -> {
            currentLeaderboardType = "unscramble";
            fetchLeaderboardStats();
        });

        tileStatsBtn.setOnClickListener(v -> {
            currentLeaderboardType = "tile_flip";
            fetchLeaderboardStats();
        });

        numberStatsBtn.setOnClickListener(v -> {
            currentLeaderboardType = "number_guess";
            fetchLeaderboardStats();
        });
    }

    private void showError(String message) {
        runOnUiThread(() ->
                Toast.makeText(LeaderboardActivity.this, message, Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}