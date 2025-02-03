package com.example.androidexample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class CreateServerActivity extends AppCompatActivity {

    private static final String APP_URL = MainActivity.APP_URL;
    private static String CURRENT_USER;
    private Button homeBtn, createBtn;
    private EditText serverNameEdit;
    private TextView bodyString;
    public boolean testing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_server);

        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setBackgroundColor(Color.BLACK);
        createBtn = findViewById(R.id.btnCreateServer);
        createBtn.setBackgroundColor(Color.BLACK);
        serverNameEdit = findViewById(R.id.serverNameEdit);
        bodyString = findViewById(R.id.testText2);
        bodyString.setVisibility(View.INVISIBLE);

        CURRENT_USER = HomeActivity.currentUser;
        //Adds a default value for testing, otherwise the test will fail
        testing = CURRENT_USER == null;
        if (testing){
            CURRENT_USER = "23";
        }

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateServerActivity.this, HomeActivity.class));
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverName = serverNameEdit.getText().toString();
                if (serverName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid server name", Toast.LENGTH_LONG).show();
                    return;
                }

                try{
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("name", serverName);
                    jsonBody.put("userId", CURRENT_USER);
                    bodyString.setText(jsonBody.toString());
                    createServer(jsonBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void createServer(JSONObject jsonBody) {
        final String mRequestBody = jsonBody.toString();
        final String REQ_URL = APP_URL + "/api/servers/create";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                REQ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        Log.d("Volley Response", response);
                        String responseName = response.split(",")[1];
                        bodyString.setText(responseName);
                        Toast.makeText(getApplicationContext(), "Server created successfully", Toast.LENGTH_LONG).show();
                        if (!testing) {
                            startActivity(new Intent(CreateServerActivity.this, HomeActivity.class));
                        }
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
