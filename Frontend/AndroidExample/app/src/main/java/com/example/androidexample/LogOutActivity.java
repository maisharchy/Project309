package com.example.androidexample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LogOutActivity extends AppCompatActivity {

    private Button logoutBtn, okayBtn;
    private TextView volleyDebug, logoutText;

    private static final String APP_URL = MainActivity.APP_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        // initialize UI elements
        //logoutBtn = findViewById(R.id.btnLogOut);
        okayBtn = findViewById(R.id.btnOkay);
        volleyDebug = findViewById(R.id.volleyMsg);
        logoutText = findViewById(R.id.logOutMsg);
        okayBtn.setBackgroundColor(Color.BLACK);

        //Sets the current user to null since no user is logged in
        HomeActivity.currentUser = null;

        /* button click listeners */

        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogOutActivity.this, MainActivity.class));

            }
        });
    }


    private void makeLogOutReq(JSONObject jsonBody) {
        final String mRequestBody = jsonBody.toString();
        final String REQ_URL = APP_URL + "/api/users/logout";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                REQ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.toString().contains("Login successful")){
                            Intent intent = new Intent(LogOutActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur during the request
                        Log.e("Volley Error", error.toString());
                        volleyDebug.setText(error.toString());
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
               // headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
                //headers.put("Content-Type", "application/json");
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



