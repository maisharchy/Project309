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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DeleteActivity extends AppCompatActivity {

    private static final String APP_URL = MainActivity.APP_URL;
    private static String CURRENT_USER, CURRENT_SERVER, SERVER_NAME;
    private Button cancelBtn, confirmBtn, dismissBtn;
    private String confirmBtnTxt, REQ_URL, type;
    private TextView confirmMsg;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        CURRENT_USER = HomeActivity.currentUser;
        CURRENT_SERVER = DashboardActivity.currentServer;
        SERVER_NAME = DashboardActivity.serverName;

        cancelBtn = findViewById(R.id.btnCancel);
        confirmBtn = findViewById(R.id.btnConfirm);
        dismissBtn = findViewById(R.id.btnDismiss);
        confirmBtn.setBackgroundColor(Color.RED);
        cancelBtn.setBackgroundColor(Color.BLACK);
        dismissBtn.setBackgroundColor(Color.BLACK);
        confirmMsg = findViewById(R.id.confirmMsg);

        extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getString("TYPE");
            setupActivityBasedOnType();
        }

        setupButtonListeners();
    }

    private void setupActivityBasedOnType() {
        switch (type) {
            case "LeaveServer":
                handleLeaveServer();
                break;
            case "ResolveComplaint":
                handleResolveComplaint();
                break;
            case "ResolveBugReport":
                handleResolveBugReport();
                break;
        }
    }

    private void handleLeaveServer() {
        confirmBtnTxt = "Leave Server";
        String headerTxt = "Are you sure you want to leave " + SERVER_NAME + "?";
        confirmMsg.setText(headerTxt);
        dismissBtn.setVisibility(View.INVISIBLE);
    }

    private void handleResolveComplaint() {
        String againstName = extras.getString("AGAINSTNAME");
        String headerTxt = extras.getString("USERS") + "\n" + extras.getString("REASON");
        confirmMsg.setText(headerTxt);
        confirmBtnTxt = "Ban " + againstName + "?";
        confirmBtn.setText(confirmBtnTxt);
    }

    private void handleResolveBugReport() {
        String bugInfo = extras.getString("BUGINFO");
        String bugDetails = extras.getString("BUGDETAILS");
        confirmMsg.setText(bugInfo + "\n" + bugDetails);
        confirmBtnTxt = "Resolve Bug Report";
        confirmBtn.setText(confirmBtnTxt);
        dismissBtn.setVisibility(View.INVISIBLE);
    }

    private void setupButtonListeners() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extras != null) {
                    switch (type) {
                        case "LeaveServer":
                            REQ_URL = APP_URL + "/api/servers/leave/" + CURRENT_SERVER + "/" + CURRENT_USER;
                            delete();
                            break;
                        case "ResolveComplaint":
                            // Delete the complaint
                            REQ_URL = APP_URL + "/api/complaints/" + extras.getString("COMPLAINTID");
                            delete();
                            break;
                        case "ResolveBugReport":
                            // Delete the bug report
                            REQ_URL = APP_URL + "/api/problem-reports/" + extras.getString("BUGID");
                            delete();
                            break;
                    }
                }
            }
        });

        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("ResolveComplaint")) {
                    type = "ComplaintDismissed";
                    REQ_URL = APP_URL + "/api/complaints/" + extras.getString("COMPLAINTID");
                    delete();
                }
            }
        });

        confirmBtn.setText(confirmBtnTxt);
    }

    private void navigateBack() {
        Intent backIntent;
        switch (type) {
            case "LeaveServer":
                backIntent = new Intent(DeleteActivity.this, DashboardActivity.class);
                break;
            case "ResolveComplaint":
            case "ComplaintDismissed":
            case "ResolveBugReport":
                backIntent = new Intent(DeleteActivity.this, ComplaintsActivity.class);
                break;
            default:
                backIntent = new Intent(DeleteActivity.this, HomeActivity.class);
        }
        startActivity(backIntent);
    }

    private void delete() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE,
                REQ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Response", response);
                        handleSuccessResponse();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        confirmMsg.setText(error.toString());
                        Toast.makeText(getApplicationContext(), "Operation failed", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void handleSuccessResponse() {
        switch (type) {
            case "LeaveServer":
                Toast.makeText(getApplicationContext(), "Left server successfully", Toast.LENGTH_LONG).show();
                startActivity(new Intent(DeleteActivity.this, HomeActivity.class));
                break;
            case "ResolveComplaint":
                Toast.makeText(getApplicationContext(), "Complaint deleted successfully", Toast.LENGTH_LONG).show();
                startActivity(new Intent(DeleteActivity.this, ComplaintsActivity.class));
                break;
            case "ResolveBugReport":
                Toast.makeText(getApplicationContext(), "Bug report deleted successfully", Toast.LENGTH_LONG).show();
                startActivity(new Intent(DeleteActivity.this, ComplaintsActivity.class));
                break;
        }
    }
}