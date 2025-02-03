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

public class UpdateReportActivity extends AppCompatActivity {

    private Button submitBtn, backBtn, homeBtn;
    private EditText reportIDEntry, reporterIDEntry, reportedIDEntry, issueEntry, bugReporterEntry;
    private TextView volleyDebug;


    private static final String APP_URL = MainActivity.APP_URL;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_report);

        reportIDEntry = findViewById(R.id.reportIDEdit);
        reporterIDEntry = findViewById(R.id.reporterNameEdit);
        reportedIDEntry = findViewById(R.id.reportedNameEdit);
        issueEntry = findViewById(R.id.issueEdit);
        submitBtn = findViewById(R.id.btnSubmit);
        volleyDebug = findViewById(R.id.volleyMsg);
        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);
        submitBtn.setBackgroundColor(Color.BLACK);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateReportActivity.this, HomeActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateReportActivity.this, ReportActivity.class));
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                //int reporterInt, reportedInt;

                // Extract input values
                String reportID = reportIDEntry.getText().toString();
                String reporterID = reporterIDEntry.getText().toString();
                String reportedID = reportedIDEntry.getText().toString();
                String issue = issueEntry.getText().toString();



                // Validate inputs
                if (reporterID.isEmpty() || reportedID.isEmpty() || issue.isEmpty() || reportID.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Make sure all fields are filled", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Report Submitted", Toast.LENGTH_LONG).show();
                }



                try {
                    // Convert strings to integers
                    int reportInt = Integer.parseInt(reportID);
                    int reporterInt = Integer.parseInt(reporterID);
                    int reportedInt = Integer.parseInt(reportedID);

                    // Prepare JSON body
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("complainedUserId", reporterInt);  // Sending as integer
                    jsonBody.put("complainantUserId", reportedInt);  // Sending as integer
                    jsonBody.put("reason", issue);               // Still sending issue as string

                    // Send the request
                    makeReportReq(jsonBody, reportInt);

                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Please enter valid numbers for IDs", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void makeReportReq (JSONObject jsonBody, int reportId){
        final String mRequestBody = jsonBody.toString();
        final String REQ_URL = APP_URL + "/api/complaints/" + reportId;

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                REQ_URL,
                //jsonBody,
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
