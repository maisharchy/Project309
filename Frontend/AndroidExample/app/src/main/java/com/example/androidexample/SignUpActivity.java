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

public class SignUpActivity extends AppCompatActivity{

    private Button signUpBtn, homeBtn;
    private EditText firstNameEntry, lastNameEntry, usernameEntry, passwordEntry, emailEntry, confirmEntry;
    private TextView volleyDebug;

    private static final String APP_URL = MainActivity.APP_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // initialize UI elements
        signUpBtn = findViewById(R.id.btnSignUp);
        firstNameEntry = findViewById(R.id.firstNameEdit);
        lastNameEntry = findViewById(R.id.lastNameEdit);
        usernameEntry = findViewById(R.id.usernameEdit);
        usernameEntry = findViewById(R.id.usernameEdit);
        passwordEntry = findViewById(R.id.passwordEdit);
        confirmEntry = findViewById(R.id. confirmEdit);
        emailEntry = findViewById(R.id.emailEdit);
        volleyDebug = findViewById(R.id.volleyMsg);
        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setBackgroundColor(Color.BLACK);
        signUpBtn.setBackgroundColor(Color.BLACK);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            }
        });

        /* button click listeners */
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEntry.getText().toString();
                String lastName = lastNameEntry.getText().toString();
                String username = usernameEntry.getText().toString();
                String password = passwordEntry.getText().toString();
                String confirm = confirmEntry.getText().toString();
                String email = emailEntry.getText().toString();


                if (!password.equals(confirm)){ // test if the password and confirm password fields match
                    Toast.makeText(getApplicationContext(), "Password don't match", Toast.LENGTH_LONG).show();
                } else if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || confirm.isEmpty() || email.isEmpty()) { // test if any fields are empty
                    Toast.makeText(getApplicationContext(), "Make sure all fields are filled", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Signing up", Toast.LENGTH_LONG).show();
                }



                try{
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("firstname", firstName);
                    jsonBody.put("lastname", lastName);
                    jsonBody.put("username", username);
                    jsonBody.put("password", password);
                    jsonBody.put("email", email);
                    makeSignUpReq(jsonBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void makeSignUpReq(JSONObject jsonBody) {
        final String mRequestBody = jsonBody.toString();
        final String REQ_URL = APP_URL + "/api/users/signup";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                REQ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        Log.d("Volley Response", response);
                        volleyDebug.setText(response.toString());
                        //TODO:
                        //Go to log-in screen
                        //Login as user, INTENT
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
