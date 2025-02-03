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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final String APP_URL = MainActivity.APP_URL;
    private static final String SCREEN_URL = APP_URL + "/api/servers";
    public static String currentUser;
    private static String username;
    private GameListAdapter adapter;
    private TextView welcomeText;
    private ListView serverList;
    private Button createServerBtn, logoutBtn, joinServerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeText = findViewById(R.id.welcomeMsg);
        createServerBtn = findViewById(R.id.btnCreateServer);
        logoutBtn = findViewById(R.id.btnLogOut);
        joinServerBtn = findViewById(R.id.btnJoinServer);
        createServerBtn.setBackgroundColor(Color.BLACK);
        logoutBtn.setBackgroundColor(Color.BLACK);
        joinServerBtn.setBackgroundColor(Color.BLACK);
        serverList = findViewById(R.id.serverList);
        adapter = new GameListAdapter(this, new ArrayList<>());
        serverList.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        if (extras == null && currentUser == null) {
            currentUser = "User";
        } else if(extras != null) {
            currentUser = extras.getString("ID");
            username = extras.getString("USERNAME");
        }
        String message = "Welcome, " + username + " (ID: " + currentUser + ")";
        welcomeText.setText(message);
        //Sets the server in the dashboard to null since there is no longer a server in use
        DashboardActivity.currentServer = null;

        String REQ_URL = SCREEN_URL + "/my-servers/" + currentUser;

        createServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CreateServerActivity.class));
            }
        });

        joinServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, JoinServerActivity.class));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, LogOutActivity.class));
            }
        });

        JsonArrayRequest jsonArrReq = new JsonArrayRequest(
                Request.Method.GET,
                REQ_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());

                        // Parse the JSON array and add data to the adapter
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String server = jsonObject.getString("serverName");
                                String id = jsonObject.getString("serverId");

                                // Create a GameListObject and add it to the adapter
                                GameListObject item = new GameListObject(server, id);
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

        serverList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GameListObject selected = (GameListObject) serverList.getItemAtPosition(i);
                String serverID = selected.getIsAssigned();
                String serverName = selected.getGame();
                String REQ_URL = SCREEN_URL + "/enter/" + serverID + "/" + currentUser;
                final String mRequestBody = null;

                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        REQ_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Handle the successful response here
                                Log.d("Volley Response", response);
                                Intent intent = new Intent(HomeActivity.this, DashboardActivity.class);
                                //A substring is used because if admin or moderator are in the server name
                                //they would be counted here if the full string was used.
                                String end = response.substring(response.length() - 10);
                                String privilege;
                                if(end.contains("admin")){
                                    privilege = "admin";
                                } else if (end.contains("moderator")) {
                                    privilege = "moderator";
                                }
                                else {
                                    privilege = "player";
                                }
                                intent.putExtra("PRIVILEGE", privilege);
                                intent.putExtra("ID", serverID);
                                intent.putExtra("NAME", serverName);
                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle any errors that occur during the request
                                Log.e("Volley Error", error.toString());
                            }
                        }
                ) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody(){// throws AuthFailureError {
                        try {
                            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.d("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                            return null;
                        }
                    }

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
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

            }
        });


    }
}
