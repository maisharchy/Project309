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

public class BugReportActivity extends AppCompatActivity {
    public boolean testing;
    private Button submitBtn, backBtn, homeBtn;
    private EditText userIdEntry, titleEntry, descriptionEntry, typeEntry;
    private TextView volleyDebug;

    private static final String APP_URL = MainActivity.APP_URL;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_report);

        userIdEntry = findViewById(R.id.reporterNameEdit);
        titleEntry = findViewById(R.id.titleEdit);
        descriptionEntry = findViewById(R.id.issueEdit);
        typeEntry = findViewById(R.id.typeEdit);
        submitBtn = findViewById(R.id.btnSubmit);
        volleyDebug = findViewById(R.id.volleyMsg);
        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);

        // Set button colors
        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);
        submitBtn.setBackgroundColor(Color.BLACK);

        // Home button navigation
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BugReportActivity.this, HomeActivity.class));
            }
        });

        // Back button navigation
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BugReportActivity.this, ReportActivity.class));
            }
        });

        // Submit button functionality
        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Extract input values
                String userIdStr = userIdEntry.getText().toString();
                String title = titleEntry.getText().toString();
                String description = descriptionEntry.getText().toString();
                String type = typeEntry.getText().toString();

                // Validate inputs
                if (userIdStr.isEmpty() || title.isEmpty() || description.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Make sure all fields are filled", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    // Convert user ID to integer
                    int userId = Integer.parseInt(userIdStr);

                    // Prepare JSON body for bug report
                    JSONObject jsonBody = new JSONObject();

                    jsonBody.put("userId", userId);
                    jsonBody.put("title", title);
                    jsonBody.put("description", description);
                    jsonBody.put("type", type);

                    // Send the request
                    makeReportReq(jsonBody);

                    // Show success toast
                    Toast.makeText(getApplicationContext(), "Report Submitted", Toast.LENGTH_LONG).show();

                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Please enter valid numbers for IDs", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error preparing report", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void makeReportReq(JSONObject jsonBody) {
        final String mRequestBody = jsonBody.toString();
        volleyDebug.setText(mRequestBody);
        final String REQ_URL = APP_URL + "/api/problem-reports";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                REQ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        Log.d("Volley Response", response);
                        volleyDebug.setText(response);
                        // TODO: Add any additional response handling
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur during the request
                        Log.e("Volley Error", error.toString());
                        volleyDebug.setText(error.toString());
                        Toast.makeText(getApplicationContext(), "Failed to submit report", Toast.LENGTH_LONG).show();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Uncomment and add authentication if needed
                // headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
                return headers;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}