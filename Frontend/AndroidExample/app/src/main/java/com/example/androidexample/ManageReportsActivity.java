package com.example.androidexample;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ManageReportsActivity extends AppCompatActivity {
    private ListView reportsListView;
    private ArrayList<Report> reportsList;
    private ReportAdapter adapter;
    private String currentUserId;
    private Button homeBtn, backBtn, switchTypeBtn;
    private boolean isBugReportsView = true;

    private static final String APP_URL = MainActivity.APP_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_reports);

        // Get current user ID
        currentUserId = HomeActivity.currentUser;

        // Initialize UI components
        reportsListView = findViewById(R.id.reportsListView);
        homeBtn = findViewById(R.id.btnHome);
        backBtn = findViewById(R.id.btnBack);
        switchTypeBtn = findViewById(R.id.btnSwitchReportType);

        // Set button colors
        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);
        switchTypeBtn.setBackgroundColor(Color.BLACK);

        // Navigation buttons
        homeBtn.setOnClickListener(v ->
                startActivity(new Intent(ManageReportsActivity.this, HomeActivity.class))
        );

        backBtn.setOnClickListener(v ->
                startActivity(new Intent(ManageReportsActivity.this, DashboardActivity.class))
        );

        // Switch report type button
        switchTypeBtn.setOnClickListener(v -> {
            isBugReportsView = !isBugReportsView;
            switchTypeBtn.setText(isBugReportsView ? "Switch to User Reports" : "Switch to Bug Reports");
            fetchReports();
        });

        // Initialize list
        reportsList = new ArrayList<>();
        adapter = new ReportAdapter();
        reportsListView.setAdapter(adapter);

        reportsListView = findViewById(R.id.reportsListView);
        TextView emptyView = findViewById(R.id.emptyView);
        reportsListView.setEmptyView(emptyView);

        // Fetch initial reports
        fetchReports();
    }

    private void fetchReports() {
        String url;
        if (isBugReportsView) {
            url = APP_URL + "/api/problem-reports/user/" + currentUserId;
            Log.d("BUG_REPORTS", "Fetching URL: " + url);
            fetchBugReports(url);
        } else {
            url = APP_URL + "/api/complaints/user/" + currentUserId;
            Log.d("USER_REPORTS", "Fetching URL: " + url);
            fetchUserReports(url);
        }
    }

    private void fetchBugReports(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d("BUG_REPORTS", "Full Response: " + response.toString());
                    reportsList.clear();
                    try {
                        // Add more detailed error checking
                        if (response == null || response.length() == 0) {
                            Log.e("BUG_REPORTS", "No reports found or empty response");
                            Toast.makeText(this, "No reports available", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject reportJson = response.getJSONObject(i);

                                // Log each report for debugging
                                Log.d("BUG_REPORTS", "Report " + i + ": " + reportJson.toString());

                                // Add null checks for each field
                                int id = reportJson.optInt("id", -1);
                                int userId = reportJson.optInt("userId", -1);
                                String title = reportJson.optString("title", "Untitled");
                                String description = reportJson.optString("description", "No description");
                                String type = reportJson.optString("type", "Unknown");

                                Report report = new Report(
                                        id,
                                        userId,
                                        title,
                                        description,
                                        type,
                                        true // is bug report
                                );
                                reportsList.add(report);
                            } catch (JSONException e) {
                                Log.e("BUG_REPORTS", "Error parsing individual report", e);
                            }
                        }

                        adapter.notifyDataSetChanged();

                        // Add a check if no reports were added
                        if (reportsList.isEmpty()) {
                            Toast.makeText(this, "No bug reports found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("BUG_REPORTS", "Comprehensive parsing error", e);
                        Toast.makeText(this, "Error processing reports: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // More detailed error logging
                    Log.e("BUG_REPORTS", "Volley Error Details:", error);

                    // Check network response if available
                    if (error.networkResponse != null) {
                        String errorBody = new String(error.networkResponse.data);
                        Log.e("BUG_REPORTS", "Error Response Body: " + errorBody);
                        Log.e("BUG_REPORTS", "Status Code: " + error.networkResponse.statusCode);
                    }

                    Toast.makeText(this, "Failed to fetch bug reports: " +
                                    (error.getMessage() != null ? error.getMessage() : "Unknown error"),
                            Toast.LENGTH_LONG).show();
                }
        );

        // Set retry policy to handle potential network issues
        int socketTimeout = 30000; // 30 seconds
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private void fetchUserReports(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    reportsList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject reportJson = response.getJSONObject(i);
                            Report report = new Report(
                                    reportJson.getInt("id"),
                                    reportJson.getInt("complainantUserId"),
                                    "User Complaint",
                                    reportJson.getString("reason"),
                                    "User Report",
                                    false // is user report
                            );
                            reportsList.add(report);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing user reports", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(this, "Failed to fetch user reports", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private void updateReport(final Report report) {
        Intent updateIntent;
        if (report.isBugReport) {
            updateIntent = new Intent(this, BugReportActivity.class);
            updateIntent.putExtra("REPORT_TYPE", "BUG");
            updateIntent.putExtra("REPORT_ID", report.getId());
            updateIntent.putExtra("USER_ID", report.getUserId());
            updateIntent.putExtra("TITLE", report.getTitle());
            updateIntent.putExtra("DESCRIPTION", report.getDescription());
            updateIntent.putExtra("REPORT_TYPE", report.getType());
        } else {
            updateIntent = new Intent(this, UpdateReportActivity.class);
            updateIntent.putExtra("REPORT_TYPE", "USER");
            updateIntent.putExtra("REPORT_ID", report.getId());
            updateIntent.putExtra("REPORTER_ID", report.getUserId());
            updateIntent.putExtra("REASON", report.getDescription());
        }
        startActivity(updateIntent);
    }

    private void deleteReport(final Report report) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this report?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String url = report.isBugReport
                            ? APP_URL + "/api/problem-reports/" + report.getId()
                            : APP_URL + "/api/complaints/" + report.getId();

                    StringRequest deleteRequest = new StringRequest(
                            Request.Method.DELETE,
                            url,
                            response -> {
                                reportsList.remove(report);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(this,
                                        report.isBugReport
                                                ? "Bug report deleted successfully"
                                                : "User report deleted successfully",
                                        Toast.LENGTH_SHORT).show();
                            },
                            error -> {
                                Log.e("Delete Error", "Failed to delete report", error);
                                Toast.makeText(this,
                                        "Failed to delete report. Please try again.",
                                        Toast.LENGTH_SHORT).show();
                            }
                    );

                    VolleySingleton.getInstance(this).addToRequestQueue(deleteRequest);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Custom adapter for reports
    private class ReportAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return reportsList.size();
        }

        @Override
        public Object getItem(int position) {
            return reportsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return reportsList.get(position).getId();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ManageReportsActivity.this)
                        .inflate(R.layout.item_reports, parent, false);
            }

            // Find views
            TextView titleTextView = convertView.findViewById(R.id.reportTitleTextView);
            TextView descriptionTextView = convertView.findViewById(R.id.reportDescriptionTextView);
            TextView typeTextView = convertView.findViewById(R.id.reportTypeTextView);
            Button updateBtn = convertView.findViewById(R.id.updateReportBtn);
            Button deleteBtn = convertView.findViewById(R.id.deleteReportBtn);

            // Get current report
            final Report report = reportsList.get(position);

            // Set text
            titleTextView.setText(report.isBugReport ? report.getTitle() : "User Complaint");
            descriptionTextView.setText(report.getDescription());
            typeTextView.setText(report.isBugReport ? "Bug Report" : "User Report");

            // Update button
            updateBtn.setOnClickListener(v -> updateReport(report));

            // Delete button
            deleteBtn.setOnClickListener(v -> deleteReport(report));

            return convertView;
        }
    }

    // Generalized Report model class
    private class Report {
        private int id;
        private int userId;
        private String title;
        private String description;
        private String type;
        private boolean isBugReport;

        public Report(int id, int userId, String title, String description, String type, boolean isBugReport) {
            this.id = id;
            this.userId = userId;
            this.title = title;
            this.description = description;
            this.type = type;
            this.isBugReport = isBugReport;
        }

        // Getters
        public int getId() { return id; }
        public int getUserId() { return userId; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getType() { return type; }
    }
}