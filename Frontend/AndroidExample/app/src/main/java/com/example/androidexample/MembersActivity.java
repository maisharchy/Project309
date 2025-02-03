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

public class MembersActivity extends AppCompatActivity {

    private static final String APP_URL = MainActivity.APP_URL;
    private static String CURRENT_USER, CURRENT_SERVER, SERVER_NAME;
    private Button backBtn, homeBtn;
    private TextView screenTitle;
    private GameListAdapter adapter;
    private ListView memberList;
    private boolean assignMode;
    private ArrayList<String> memberIDs, memberRoles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        CURRENT_USER = HomeActivity.currentUser;
        CURRENT_SERVER = DashboardActivity.currentServer;
        SERVER_NAME = DashboardActivity.serverName;

        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);
        screenTitle = findViewById(R.id.screenTitle);
        memberList = findViewById(R.id.memberList);

        adapter = new GameListAdapter(this, new ArrayList<>());
        memberList.setAdapter(adapter);

        String titleMsg = SERVER_NAME + " Members";
        screenTitle.setText(titleMsg);

        Bundle extras = getIntent().getExtras();
        if (extras == null){
            assignMode = false;
        } else {
            assignMode = extras.getString("MODE").equals("assign");
        }

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MembersActivity.this, HomeActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MembersActivity.this, DashboardActivity.class));
            }
        });

        final String REQ_URL = APP_URL + "/api/servers/" + CURRENT_SERVER + "/memberships";
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(
                Request.Method.GET,
                REQ_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());
                        memberIDs = new ArrayList<String>();
                        memberRoles = new ArrayList<String>();
                        // Parse the JSON array and add data to the adapter
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                JSONObject userObject = jsonObject.getJSONObject("user");
                                String first = userObject.getString("firstname");
                                String last = userObject.getString("lastname");
                                String name = first + " " + last;
                                String privilege = jsonObject.getString("role");
                                String id = userObject.getString("id");
                                memberIDs.add(id);
                                memberRoles.add(privilege);
                                String description = privilege + " (User ID: " + id + ")";

                                // Create a GameListObject and add it to the adapter
                                GameListObject item = new GameListObject(name, description);
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

        memberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (assignMode) {
                    GameListObject selected = (GameListObject) memberList.getItemAtPosition(i);
                    Intent intent = new Intent(MembersActivity.this, AssignRolesActivity.class);
                    intent.putExtra("NAME", selected.getGame());
                    intent.putExtra("PRIVILEGE", memberRoles.get(i));
                    intent.putExtra("ID", memberIDs.get(i));
                    startActivity(intent);
                }

            }
        });

    }
}
