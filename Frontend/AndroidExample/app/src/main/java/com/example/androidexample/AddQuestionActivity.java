package com.example.androidexample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AddQuestionActivity extends AppCompatActivity {

    private static final String APP_URL = MainActivity.APP_URL;
    private static final String SCREEN_URL = APP_URL + "/api/questions/question/";
    private static String CURRENT_USER, CURRENT_SERVER;
    private Button backBtn, homeBtn, submitBtn;
    private EditText questionEntry, answerEntry, typeEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        CURRENT_USER = HomeActivity.currentUser;
        CURRENT_SERVER = DashboardActivity.currentServer;

        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);
        submitBtn = findViewById(R.id.btnSubmit);
        questionEntry = findViewById(R.id.questionEdit);
        answerEntry = findViewById(R.id.answerEdit);
        typeEntry = findViewById(R.id.typeEdit);
        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);
        submitBtn.setBackgroundColor(Color.BLACK);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddQuestionActivity.this, HomeActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddQuestionActivity.this, DashboardActivity.class));
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = questionEntry.getText().toString();
                String answer = answerEntry.getText().toString();
                String type = typeEntry.getText().toString();
                try{
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("question", question);
                    jsonBody.put("correctAnswer", answer);
                    jsonBody.put("type", type);
                    addQuestion(jsonBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void addQuestion(JSONObject jsonBody){
        final String mRequestBody = jsonBody.toString();
        final String REQ_URL = SCREEN_URL + CURRENT_SERVER;

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                REQ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        Log.d("Volley Response", response);
                        questionEntry.setText("");
                        answerEntry.setText("");
                        typeEntry.setText("");
                        Toast.makeText(getApplicationContext(), "Question added successfully", Toast.LENGTH_LONG).show();
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
