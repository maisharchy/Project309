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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity{

    private Button loginBtn, forgotPasswordBtn, homeBtn;
    private EditText usernameEntry, passwordEntry;
    private TextView bodyString;
    public boolean testing = false;

    private static final String APP_URL = MainActivity.APP_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.btnLogin);
        forgotPasswordBtn = findViewById(R.id.btnForgotPassword);
        usernameEntry = findViewById(R.id.loginUsernameEdit);
        passwordEntry = findViewById(R.id.loginPasswordEdit);
        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setBackgroundColor(Color.BLACK);
        forgotPasswordBtn.setBackgroundColor(Color.BLACK);
        loginBtn.setBackgroundColor(Color.BLACK);
        bodyString = findViewById(R.id.testText4);
        bodyString.setVisibility(View.INVISIBLE);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });

        /* button click listeners */
        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEntry.getText().toString();
                String password = passwordEntry.getText().toString();
                try{
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("username", username);
                    jsonBody.put("password", password);
                    makeLoginReq(jsonBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void makeLoginReq(JSONObject jsonBody) {
        final String mRequestBody = jsonBody.toString();
        final String REQ_URL = APP_URL + "/api/login";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,
                REQ_URL,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the successful response here
                        Log.d("Volley Response", response.toString());
                        bodyString.setText(response.toString());
                        try {
                            String status = response.getString("message");
                            String id = response.getString("userId");
                            if (response.toString().contains("Login successful") && !testing){
                                //Login
                                //Bundle username as extra, send
                                String username = usernameEntry.getText().toString();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.putExtra("USERNAME", username);  // key-value to pass to the MainActivity
                                intent.putExtra("ID", id);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Password is incorrect", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }
}
