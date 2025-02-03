package com.example.androidexample;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.bumptech.glide.load.DataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileSettingsActivity extends AppCompatActivity {
    private static final String APP_URL = MainActivity.APP_URL;
    private static final String TAG = "ProfileSettings";
    private static String CURRENT_USER;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Buttons to show/hide sections
    private Button backBtn, homeBtn, editNameButton, editUsernameButton, editBioButton, editAvatarButton, editPasswordButton, deleteAccountButton;

    // Layout containers for each section
    private LinearLayout nameSection, usernameSection, bioSection, avatarSection, passwordSection;

    // Input fields
    private EditText firstNameInput, lastNameInput, usernameInput, bioInput, avatarInput, currentPasswordInput, newPasswordInput, deleteAccountPasswordInput;

    private ImageView avatarPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        CURRENT_USER = HomeActivity.currentUser;
        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileSettingsActivity.this, HomeActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileSettingsActivity.this, DashboardActivity.class));
            }
        });

        initializeViews();
        setupButtonListeners();
        setupAvatarPreview();
        fetchProfile();
    }



    private void initializeViews() {

        // Initialize buttons
        editNameButton = findViewById(R.id.editNameButton);
        editUsernameButton = findViewById(R.id.editUsernameButton);
        editBioButton = findViewById(R.id.editBioButton);
        editAvatarButton = findViewById(R.id.editAvatarButton);
        editPasswordButton = findViewById(R.id.editPasswordButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);

        // Initialize sections
        nameSection = findViewById(R.id.nameSection);
        usernameSection = findViewById(R.id.usernameSection);
        bioSection = findViewById(R.id.bioSection);
        avatarSection = findViewById(R.id.avatarSection);
        passwordSection = findViewById(R.id.passwordSection);

        // Initialize input fields
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        usernameInput = findViewById(R.id.usernameInput);
        bioInput = findViewById(R.id.bioInput);
        avatarInput = findViewById(R.id.avatarInput);
        avatarPreview = findViewById(R.id.avatarPreview);
        currentPasswordInput = findViewById(R.id.currentPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);

        // Hide all sections initially
        nameSection.setVisibility(View.GONE);
        usernameSection.setVisibility(View.GONE);
        bioSection.setVisibility(View.GONE);
        avatarSection.setVisibility(View.GONE);
        passwordSection.setVisibility(View.GONE);

        // Entering password to delete the account
        deleteAccountPasswordInput = new EditText(this);
        deleteAccountPasswordInput.setHint("Enter your password");
        deleteAccountPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        setDefaultAvatar();
    }

    private void setupButtonListeners() {
        editNameButton.setOnClickListener(v -> toggleSection(nameSection));
        editUsernameButton.setOnClickListener(v -> toggleSection(usernameSection));
        editBioButton.setOnClickListener(v -> toggleSection(bioSection));
        editAvatarButton.setOnClickListener(v -> toggleSection(avatarSection));
        editPasswordButton.setOnClickListener(v -> toggleSection(passwordSection));

        // Set up update buttons within each section
        findViewById(R.id.updateNameButton).setOnClickListener(v -> updateName());
        findViewById(R.id.updateUsernameButton).setOnClickListener(v -> updateUsername());
        findViewById(R.id.updateBioButton).setOnClickListener(v -> updateBio());
        findViewById(R.id.updateAvatarButton).setOnClickListener(v -> updateAvatar());
        findViewById(R.id.updatePasswordButton).setOnClickListener(v -> updatePassword());
        deleteAccountButton.setOnClickListener(v -> showDeleteAccountDialog());
    }

    private void toggleSection(View section) {
        // Hide all sections first
        nameSection.setVisibility(View.GONE);
        usernameSection.setVisibility(View.GONE);
        bioSection.setVisibility(View.GONE);
        avatarSection.setVisibility(View.GONE);
        passwordSection.setVisibility(View.GONE);

        // Show the selected section if it wasn't already visible
        if (section.getVisibility() != View.VISIBLE) {
            section.setVisibility(View.VISIBLE);
        }
    }

    private void setDefaultAvatar() {
        // Use the custom default avatar
        avatarPreview.setImageResource(R.drawable.ic_default_avatar);
    }


    private void setupAvatarPreview() {
        // Update preview when avatar URL changes
        avatarInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateAvatarPreview(s.toString());
            }
        });
    }

    private void updateAvatarPreview(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar)
                        .apply(RequestOptions.circleCropTransform())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                setDefaultAvatar();
                                if (e != null) {
                                    Log.e(TAG, "Error loading avatar", e);
                                    showError("Error loading avatar image");
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(avatarPreview);
            } catch (Exception e) {
                Log.e(TAG, "Error loading avatar preview", e);
                showError("Error loading avatar preview");
                setDefaultAvatar();
            }
        } else {
            setDefaultAvatar();
        }
    }

    private void fetchProfile() {
        executorService.execute(() -> {
            try {
                URL url = new URL(APP_URL + "/api/profile/" + CURRENT_USER);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String response = readResponse(conn);
                    JsonObject profile = new Gson().fromJson(response, JsonObject.class);

                    runOnUiThread(() -> {
                        JsonObject user = profile.getAsJsonObject("user");
                        JsonObject profileData = profile.getAsJsonObject("profile");

                        firstNameInput.setText(user.get("firstname").getAsString());
                        lastNameInput.setText(user.get("lastname").getAsString());
                        usernameInput.setText(user.get("username").getAsString());
                        bioInput.setText(profileData.get("bio").getAsString());

                        // Get avatar URL and handle null case
                        String avatarUrl = profileData.has("avatar") && !profileData.get("avatar").isJsonNull()
                                ? profileData.get("avatar").getAsString()
                                : "";

                        avatarInput.setText(avatarUrl);
                        updateAvatarPreview(avatarUrl); // Update the avatar preview
                    });
                }
            } catch (Exception e) {
                showError("Error loading profile: " + e.getMessage());
            }
        });
    }



    private void updateName() {
        JsonObject nameData = new JsonObject();
        nameData.addProperty("firstname", firstNameInput.getText().toString());
        nameData.addProperty("lastname", lastNameInput.getText().toString());
        makeRequest("/edit/name", "PUT", nameData);
    }

    private void updateUsername() {
        JsonObject usernameData = new JsonObject();
        usernameData.addProperty("username", usernameInput.getText().toString());
        makeRequest("/edit/username", "PUT", usernameData);
    }

    private void updateBio() {
        JsonObject bioData = new JsonObject();
        bioData.addProperty("bio", bioInput.getText().toString());
        makeRequest("/edit/bio", "PUT", bioData);
    }

    private void updateAvatar() {
        JsonObject avatarData = new JsonObject();
        avatarData.addProperty("avatar", avatarInput.getText().toString());
        makeRequest("/edit/avatar", "PUT", avatarData);
    }

//    private void updateAvatar() {
//        String newAvatarUrl = avatarInput.getText().toString();
//
//        // Validate URL format
//        try {
//            new URL(newAvatarUrl);
//        } catch (Exception e) {
//            showError("Please enter a valid URL");
//            return;
//        }
//
//        JsonObject avatarData = new JsonObject();
//        avatarData.addProperty("avatar", newAvatarUrl);
//        makeRequest("/edit/avatar", "PUT", avatarData);
//    }

    private void updatePassword() {
        JsonObject passwordData = new JsonObject();
        passwordData.addProperty("currentPassword", currentPasswordInput.getText().toString());
        passwordData.addProperty("newPassword", newPasswordInput.getText().toString());
        makeRequest("/update-password", "PUT", passwordData);
    }

    private void showDeleteAccountDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);

        layout.addView(deleteAccountPasswordInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("This action cannot be undone. Please enter your password to confirm.")
                .setView(layout)
                .setPositiveButton("Delete", null)
                .setNegativeButton("Cancel", (dialog1, which) -> {
                    deleteAccountPasswordInput.setText("");
                })
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String password = deleteAccountPasswordInput.getText().toString().trim();

                if (password.isEmpty()) {
                    showError("Please enter your password");
                    return;
                }

                JsonObject deleteData = new JsonObject();
                deleteData.addProperty("password", password);

                executorService.execute(() -> {
                    HttpURLConnection conn = null;
                    try {
                        // Ensure CURRENT_USER is properly set and not null
                        if (CURRENT_USER == null || CURRENT_USER.isEmpty()) {
                            throw new IllegalStateException("User ID is not set");
                        }

                        // Create properly formatted URL
                        String fullUrl = String.format("%s/api/profile/%s/delete-account",
                                APP_URL,
                                CURRENT_USER.trim());

                        Log.d(TAG, "Making DELETE request to: " + fullUrl);
                        URL url = new URL(fullUrl);
                        conn = (HttpURLConnection) url.openConnection();

                        // Set up connection
                        conn.setRequestMethod("DELETE");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setDoOutput(true);
                        conn.setConnectTimeout(15000);
                        conn.setReadTimeout(15000);

                        // Write request body
                        try (OutputStream os = conn.getOutputStream()) {
                            byte[] input = deleteData.toString().getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }

                        // Check response
                        int responseCode = conn.getResponseCode();
                        Log.d(TAG, "Delete response code: " + responseCode);

                        if (responseCode == HttpURLConnection.HTTP_OK ||
                                responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                            runOnUiThread(() -> {
                                dialog.dismiss();
                                showSuccess("Account successfully deleted");
                                // Navigate back to login
                                Intent intent = new Intent(ProfileSettingsActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            });
                        } else {
                            String errorResponse = readErrorResponse(conn);
                            Log.e(TAG, "Delete error response: " + errorResponse);

                            runOnUiThread(() -> {
                                String errorMessage = errorResponse.isEmpty() ?
                                        "Failed to delete account" : errorResponse;
                                showError(errorMessage);
                                deleteAccountPasswordInput.setText("");
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Delete request failed", e);
                        runOnUiThread(() -> {
                            showError("Error: " + e.getMessage());
                            deleteAccountPasswordInput.setText("");
                        });
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                });
            });
        });

        dialog.show();
    }


    private void makeRequest(String endpoint, String method, JsonObject data) {
        executorService.execute(() -> {
            HttpURLConnection conn = null;
            try {
                // Construct the URL based on the endpoint type
                String fullUrl;
                switch (endpoint) {
                    case "/edit/name":
                    case "/edit/username":
                    case "/edit/bio":
                    case "/edit/avatar":
                    case "/update-password":
                    case "/delete-account":
                        fullUrl = APP_URL + "/api/profile/" + CURRENT_USER + endpoint;
                        break;
                    default:
                        fullUrl = APP_URL + "/api/profile/" + CURRENT_USER;
                        break;
                }

                Log.d(TAG, "Making " + method + " request to: " + fullUrl);
                Log.d(TAG, "Request body: " + data.toString());

                URL url = new URL(fullUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(method);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);

                // Write request body
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = data.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String successResponse = readResponse(conn);
                    Log.d(TAG, "Success response: " + successResponse);

                    runOnUiThread(() -> {
                        showSuccess("Update successful");
                        if (method.equals("DELETE")) {
                            startActivity(new Intent(ProfileSettingsActivity.this, MainActivity.class));
                            finish();
                        }
                    });
                } else {
                    String errorResponse = readErrorResponse(conn);
                    Log.e(TAG, "Error response: " + errorResponse);

                    runOnUiThread(() -> showError("Update failed: " + errorResponse));
                }
            } catch (Exception e) {
                Log.e(TAG, "Network error", e);
                runOnUiThread(() -> showError("Network error: " + e.getMessage()));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    private String readErrorResponse(HttpURLConnection conn) {
        try {
            InputStream errorStream = conn.getErrorStream();
            if (errorStream == null) {
                return "Error code: " + conn.getResponseCode();
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                String errorResponse = response.toString();

                // Try to parse as JSON
                try {
                    JsonObject errorJson = new Gson().fromJson(errorResponse, JsonObject.class);
                    if (errorJson.has("message")) {
                        return errorJson.get("message").getAsString();
                    } else if (errorJson.has("error")) {
                        return errorJson.get("error").getAsString();
                    }
                } catch (Exception e) {
                    // If not JSON, return raw response
                    Log.d(TAG, "Error response is not JSON: " + e.getMessage());
                }

                return errorResponse;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading error stream", e);
            return "Failed to read error response";
        }
    }


    // Add this helper method to show an error message
    private void showError(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }


    private void showSuccess(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}