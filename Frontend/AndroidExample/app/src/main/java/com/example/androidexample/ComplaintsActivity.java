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

public class ComplaintsActivity extends AppCompatActivity {

    private static final String APP_URL = MainActivity.APP_URL;
    private static String CURRENT_USER, CURRENT_SERVER;
    private Button backBtn, homeBtn;
    private TextView screenTitle;
    private ListView complaintList;
    private GameListAdapter adapter;
    private ArrayList<String> complaintIDs, againstIDs, againstNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);

        CURRENT_USER = HomeActivity.currentUser;
        CURRENT_SERVER = DashboardActivity.currentServer;

        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);
        screenTitle = findViewById(R.id.screenTitle);
        complaintList = findViewById(R.id.complaintList);
        adapter = new GameListAdapter(this, new ArrayList<>());
        complaintList.setAdapter(adapter);

        screenTitle.setText("Complaints");

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ComplaintsActivity.this, HomeActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ComplaintsActivity.this, DashboardActivity.class));
            }
        });

        final String REQ_URL = APP_URL + "/api/servers/" + CURRENT_SERVER + "/complaints";
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(
                Request.Method.GET,
                REQ_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());
                        complaintIDs = new ArrayList<String>();
                        againstIDs = new ArrayList<String>();
                        againstNames = new ArrayList<String>();

                        // Parse the JSON array and add data to the adapter
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                JSONObject against = jsonObject.getJSONObject("complainedUser");
                                JSONObject by = jsonObject.getJSONObject("complainantUser");
                                String againstName = against.getString("firstname") + " " + against.getString("lastname");
                                String byName = by.getString("firstname") + " " + by.getString("lastname");
                                String reason = jsonObject.getString("reason");
                                String users = "Against: " + againstName + "; By: " + byName;
                                complaintIDs.add(jsonObject.getString("complaintId"));
                                againstNames.add(againstName);
                                againstIDs.add(against.getString("id"));

                                // Create a GameListObject and add it to the adapter
                                GameListObject item = new GameListObject(users, reason);
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

        complaintList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GameListObject selected = (GameListObject) complaintList.getItemAtPosition(i);
                Intent intent = new Intent(ComplaintsActivity.this, DeleteActivity.class);
                intent.putExtra("USERS", selected.getGame());
                intent.putExtra("REASON", selected.getIsAssigned());
                intent.putExtra("COMPLAINTID", complaintIDs.get(i));
                intent.putExtra("AGAINSTID", againstIDs.get(i));
                intent.putExtra("AGAINSTNAME", againstNames.get(i));
                intent.putExtra("TYPE", "ResolveComplaint");
                startActivity(intent);
            }
        });

    }
}
