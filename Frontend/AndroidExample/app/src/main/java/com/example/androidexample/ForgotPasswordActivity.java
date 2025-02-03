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

public class ForgotPasswordActivity extends AppCompatActivity {
    private Button resetPasswordBtn, backBtn, homeBtn;
    private EditText emailEntry, passwordEntry, confirmEntry, answerEntry;
    private boolean gotQuestion;
    private TextView securityQuestion, passwordTitle, confirmTitle, questionTitle;
    private TextView bodyString;
    public boolean testing = false;

    private static final String APP_URL = MainActivity.APP_URL;
    private static final String SCREEN_URL = APP_URL + "/api/password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        resetPasswordBtn = findViewById(R.id.btnResetPassword);
        emailEntry = findViewById(R.id.resetEmailEdit);
        passwordEntry = findViewById(R.id.resetPasswordEdit);
        confirmEntry = findViewById(R.id.resetconfirmEdit);
        answerEntry = findViewById(R.id.resetAnswerEdit);
        securityQuestion = findViewById(R.id.securityQuestion);
        questionTitle = findViewById(R.id.questionTitle);
        confirmTitle = findViewById(R.id.passwordTitle);
        passwordTitle = findViewById(R.id.confirmTitle);
        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);
        resetPasswordBtn.setBackgroundColor(Color.BLACK);
        bodyString = findViewById(R.id.testText3);
        bodyString.setVisibility(View.INVISIBLE);

        gotQuestion = false;
        setVisibility();

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            }
        });

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gotQuestion){
                    reset();
                } else {
                    getQuestion();
                }
            }
        });

    }

    private void setVisibility(){
        //View.VISIBLE and .INVISIBLE are both ints, uses this value to set the visibility
        int visibility;
        if (gotQuestion) {
            visibility = View.VISIBLE;
            emailEntry.setEnabled(false);
            resetPasswordBtn.setText("Reset Password");
        } else if (testing) {
            visibility = View.VISIBLE;
        } else {
            visibility = View.INVISIBLE;
        }

        passwordTitle.setVisibility(visibility);
        confirmTitle.setVisibility(visibility);
        questionTitle.setVisibility(visibility);
        passwordEntry.setVisibility(visibility);
        confirmEntry.setVisibility(visibility);
        answerEntry.setVisibility(visibility);
    }

    private void getQuestion(){
        String email = emailEntry.getText().toString();try{
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            bodyString.setText(jsonBody.toString());
            getID(jsonBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void reset(){
        String email = emailEntry.getText().toString();
        String password = passwordEntry.getText().toString();
        String confirm = confirmEntry.getText().toString();
        String answer = answerEntry.getText().toString();
        if(!confirm.equals(password)){
            Toast.makeText(getApplicationContext(), "Password don't match", Toast.LENGTH_LONG).show();
            return;
        }
        try{
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("securityQuestionAnswer", answer);
//            bodyString.setText(jsonBody.toString());
            makeResetReq(jsonBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getID(JSONObject jsonBody){
        final String mRequestBody = jsonBody.toString();
        final String REQ_URL = SCREEN_URL + "/getUserIdByEmail";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                REQ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        Log.d("Volley Response", response);
                        bodyString.setText(response);
                        getQuestion(response);
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

    private void getQuestion(String id){
        final String REQ_URL = SCREEN_URL + "/securityQuestion/" + id;

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                REQ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        Log.d("Volley Response", response);
                        securityQuestion.setText(response);
                        gotQuestion = true;
                        setVisibility();
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
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
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

    private void makeResetReq(JSONObject jsonBody) {
        final String mRequestBody = jsonBody.toString();
        final String REQ_URL = SCREEN_URL + "/reset";

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                REQ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        Log.d("Volley Response", response);
                        if (response.contains("success") && !testing){
                            Toast.makeText(getApplicationContext(), "Password reset successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Security answer incorrect", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur during the request
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(getApplicationContext(), "Security answer incorrect", Toast.LENGTH_LONG).show();
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