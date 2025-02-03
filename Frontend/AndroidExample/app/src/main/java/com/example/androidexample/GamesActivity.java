package com.example.androidexample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GamesActivity extends AppCompatActivity {

    private static final String APP_URL = MainActivity.APP_URL;
    private static String CURRENT_USER, CURRENT_SERVER;
    private Button backBtn, homeBtn;
    private TextView screenTitle;
    private GameListAdapter adapter;
    private ListView gamesList;
    private static final String SCREEN_URL = APP_URL + "/api/games";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        CURRENT_USER = HomeActivity.currentUser;
        CURRENT_SERVER = DashboardActivity.currentServer;

        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);
        screenTitle = findViewById(R.id.screenTitle);
        gamesList = findViewById(R.id.gamesList);

        adapter = new GameListAdapter(this, new ArrayList<>());
        gamesList.setAdapter(adapter);

        screenTitle.setText("Games");

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GamesActivity.this, HomeActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GamesActivity.this, DashboardActivity.class));
            }
        });

        //final String REQ_URL = SCREEN_URL + CURRENT_SERVER;
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(
                Request.Method.GET,
                SCREEN_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());

                        // Parse the JSON array and add data to the adapter
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String game = jsonObject.getString("title");
                                String isAssigned = jsonObject.getString("assignmentStatus");

                                // Create a GameListObject and add it to the adapter
                                GameListObject item = new GameListObject(game, isAssigned);
                                adapter.add(item);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
//                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
//                params.put("param1", "value1");
//                params.put("param2", "value2");
                return params;
            }
        };
        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrReq);

        gamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    startActivity(new Intent(GamesActivity.this, TileFlipActivity.class));
                } else if (i == 1) {
                    startActivity(new Intent(GamesActivity.this, TriviaActivity.class));
                } else if (i == 2) {
                    startActivity(new Intent(GamesActivity.this, UnscrambleActivity.class));
                } else if (i == 3) {
                        startActivity(new Intent(GamesActivity.this, NumberGuessActivity.class));
                } else {
                screenTitle.setText("Games (X)");
                }
            }
        });

    }
}
