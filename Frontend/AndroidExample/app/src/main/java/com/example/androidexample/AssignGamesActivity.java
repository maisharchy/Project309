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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
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

public class AssignGamesActivity extends AppCompatActivity {

    private static final String APP_URL = MainActivity.APP_URL;
    private Button assignBtn, backBtn, homeBtn;
    private static String CURRENT_USER, CURRENT_SERVER;
    private GameListAdapter adapter;
    private ListView gamesList;
    private static final String SCREEN_URL = APP_URL + "/api/games";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_games);

        CURRENT_USER = HomeActivity.currentUser;
        CURRENT_SERVER = DashboardActivity.currentServer;

        assignBtn = findViewById(R.id.btnAssign);
        gamesList = findViewById(R.id.gamesList);
        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);
        assignBtn.setBackgroundColor(Color.BLACK);

        adapter = new GameListAdapter(this, new ArrayList<>());
        gamesList.setAdapter(adapter);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AssignGamesActivity.this, HomeActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AssignGamesActivity.this, HomeActivity.class));
            }
        });

        assignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {updateAssignments();}
        });

        final String REQ_URL = SCREEN_URL + CURRENT_SERVER;
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
                GameListObject selected = (GameListObject) gamesList.getItemAtPosition(i);
                if(selected.getIsAssigned().equals("Assigned")){
                    selected.setIsAssigned("To be Unassigned");
                } else if (selected.getIsAssigned().equals("To be Assigned")) {
                    selected.setIsAssigned("Not Assigned");
                } else if (selected.getIsAssigned().equals("To Be Unassigned")) {
                    selected.setIsAssigned("Assigned");
                } else{
                    selected.setIsAssigned("To be Assigned");
                }
                updateList();
            }
        });
    }

    private void updateList(){
        for (int i = 0; i < adapter.getCount(); i++){
            GameListObject game = (GameListObject) gamesList.getItemAtPosition(0);
            adapter.remove(game);
            adapter.add(game);
        }
    }

    private void updateAssignments() {
        for (int i = 0; i < adapter.getCount(); i++){
            GameListObject game = (GameListObject) gamesList.getItemAtPosition(i);
            if (game.getIsAssigned().equals("To be Assigned")) {
                sendAssignments("Assigned", i + 1);
            } else if (game.getIsAssigned().equals("To be Unassigned")) {
                sendAssignments("Not Assigned", i + 1);
            }
        }
        startActivity(new Intent(AssignGamesActivity.this, AssignGamesActivity.class));
    }

    private void sendAssignments(String mRequestBody, int id){
        //String REQ_URL = APP_URL + "/dashboard/admin/assignGame";
        String REQ_URL = SCREEN_URL + "/" + CURRENT_SERVER + "/" + id + "/assign";

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                REQ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        Log.d("Volley Response", response);
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
            public byte[] getBody() throws AuthFailureError {
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
}
