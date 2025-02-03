package com.example.androidexample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AssignRolesActivity extends AppCompatActivity {

    private static final String APP_URL = MainActivity.APP_URL;
    private static String CURRENT_USER, CURRENT_SERVER, SERVER_NAME;
    private Button backBtn, homeBtn, playerBtn, moderatorBtn, adminBtn;
    private TextView screenTitle, userInfo;
    private String currentPrivilege, name, updateId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_roles);

        CURRENT_USER = HomeActivity.currentUser;
        CURRENT_SERVER = DashboardActivity.currentServer;
        SERVER_NAME = DashboardActivity.serverName;

        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);
        playerBtn = findViewById(R.id.btnPlayer);
        moderatorBtn = findViewById(R.id.btnModerator);
        adminBtn = findViewById(R.id.btnAdmin);
        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);
        playerBtn.setBackgroundColor(Color.BLACK);
        moderatorBtn.setBackgroundColor(Color.BLACK);
        adminBtn.setBackgroundColor(Color.BLACK);
        screenTitle = findViewById(R.id.screenTitle);
        userInfo = findViewById(R.id.userInfo);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            name = extras.getString("NAME");
            currentPrivilege = extras.getString("PRIVILEGE");
            updateId = extras.getString("ID");
            String user = name + "\n" + currentPrivilege;
            userInfo.setText(user);
            if (currentPrivilege.equals("player")){
                playerBtn.setVisibility(View.INVISIBLE);
            } else if (currentPrivilege.equals("moderator")) {
                moderatorBtn.setVisibility(View.INVISIBLE);
            } else{
                adminBtn.setVisibility(View.INVISIBLE);
            }
        }

        String titleMsg = "Assign a new Role";
        screenTitle.setText(titleMsg);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AssignRolesActivity.this, HomeActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AssignRolesActivity.this, MembersActivity.class);
                intent.putExtra("MODE", "assign");
                startActivity(intent);
            }
        });

        playerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("role", "player");
                    jsonBody.put("userId", updateId);
                    jsonBody.put("serverId", CURRENT_SERVER);
                    updateRole(jsonBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        moderatorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("role", "moderator");
                    jsonBody.put("userId", updateId);
                    jsonBody.put("serverId", CURRENT_SERVER);
                    updateRole(jsonBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("role", "admin");
                    jsonBody.put("userId", updateId);
                    jsonBody.put("serverId", CURRENT_SERVER);
                    updateRole(jsonBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void updateRole(JSONObject jsonBody){
        final String mRequestBody = jsonBody.toString();
        final String REQ_URL = APP_URL + "/api/servers/roles/assign";
        //final String REQ_URL = APP_URL + "/dashboard/admin/manageUsers";

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                REQ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        Log.d("Volley Response", response);
                        Toast.makeText(getApplicationContext(), "Role assigned successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AssignRolesActivity.this, MembersActivity.class);
                        intent.putExtra("MODE", "assign");
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
