package com.example.androidexample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StatisticsActivity extends AppCompatActivity {

    private static final String APP_URL = MainActivity.APP_URL;
    private static String CURRENT_USER, CURRENT_SERVER;
    private Button backBtn, homeBtn;
    private TextView screenTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        CURRENT_USER = HomeActivity.currentUser;
        CURRENT_SERVER = DashboardActivity.currentServer;

        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);
        screenTitle = findViewById(R.id.screenTitle);

        screenTitle.setText("Stats");

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StatisticsActivity.this, HomeActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StatisticsActivity.this, DashboardActivity.class));
            }
        });

    }
}
