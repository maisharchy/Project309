package com.example.androidexample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private static final String APP_URL = MainActivity.APP_URL;
    private static String CURRENT_USER;
    public static String currentServer, serverName;
    private static String privilege;
    private Button homeBtn, leaveBtn, reportBtn, playBtn, profileBtn, statsBtn, membersBtn, complaintsBtn, gamesAssignBtn, rolesAssignBtn, addQuestionBtn;
    private TextView serverTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        CURRENT_USER = HomeActivity.currentUser;
        homeBtn = findViewById(R.id.btnHome);
        reportBtn = findViewById(R.id.btnReport);
        playBtn = findViewById(R.id.btnPlay);
        profileBtn = findViewById(R.id.btnProfile);
        statsBtn = findViewById(R.id.btnStatistics);
        membersBtn = findViewById(R.id.btnMembers);
        leaveBtn = findViewById(R.id.btnLeave);
        complaintsBtn = findViewById(R.id.btnComplaints);
        rolesAssignBtn = findViewById(R.id.btnAssignRoles);
        gamesAssignBtn = findViewById(R.id.btnAssignGames);
        addQuestionBtn = findViewById(R.id.btnAddQuestion);

        homeBtn.setBackgroundColor(Color.BLACK);
        reportBtn.setBackgroundColor(Color.BLACK);
        playBtn.setBackgroundColor(Color.BLACK);
        profileBtn.setBackgroundColor(Color.BLACK);
        statsBtn.setBackgroundColor(Color.BLACK);
        membersBtn.setBackgroundColor(Color.BLACK);
        complaintsBtn.setBackgroundColor(Color.BLACK);
        rolesAssignBtn.setBackgroundColor(Color.BLACK);
        gamesAssignBtn.setBackgroundColor(Color.BLACK);
        addQuestionBtn.setBackgroundColor(Color.BLACK);

        leaveBtn.setBackgroundColor(Color.RED);
        serverTitle = findViewById(R.id.serverName);

        Bundle extras = getIntent().getExtras();
        if (extras == null && currentServer == null) {
            currentServer = "0";
            serverName = "Server";
        } else if(extras != null) {
            currentServer = extras.getString("ID");
            serverName = extras.getString("NAME");
            privilege = extras.getString("PRIVILEGE");
        }
        String titleBanner = serverName + " Dashboard";
        serverTitle.setText(titleBanner);

        //Only admins can assign roles and games and add questions
        if(!privilege.equals("admin")){
            gamesAssignBtn.setVisibility(View.INVISIBLE);
            rolesAssignBtn.setVisibility(View.INVISIBLE);
            addQuestionBtn.setVisibility(View.INVISIBLE);
        }
        //Both moderators and admins can resolve complaints
        if (privilege.equals("player")){
            complaintsBtn.setVisibility(View.INVISIBLE);
        }

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, HomeActivity.class));
            }
        });

        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, DeleteActivity.class);
                intent.putExtra("TYPE", "LeaveServer");
                intent.putExtra("USERID", CURRENT_USER);
                intent.putExtra("SERVER", currentServer);
                startActivity(intent);
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ReportActivity.class));
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, GamesActivity.class));
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ProfileSettingsActivity.class));
            }
        });

        statsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, LeaderboardActivity.class));
            }
        });

        membersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, MembersActivity.class);
                intent.putExtra("MODE", "view");
                startActivity(intent);
            }
        });

        complaintsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ComplaintsActivity.class));
            }
        });

        gamesAssignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, AssignGamesActivity.class));
            }
        });

        rolesAssignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, MembersActivity.class);
                intent.putExtra("MODE", "assign");
                startActivity(intent);
            }
        });

        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, AddQuestionActivity.class));
            }
        });
    }
}
