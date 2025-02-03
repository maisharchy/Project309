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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import org.json.JSONException;
import org.json.JSONObject;

public class NumberGuessActivity extends AppCompatActivity {

    private static final String TAG = "NumberGuessActivity";
    private static String CURRENT_USER;
    private static final String APP_URL = MainActivity.APP_URL;

    private Button backBtn, homeBtn, submitButton;
    private EditText guessEditText;
    private TextView resultTextView, attemptsTextView;

    private WebSocket webSocket;
    private int attempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_guess);

        CURRENT_USER = HomeActivity.currentUser;

        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);
        guessEditText = findViewById(R.id.guessEditText);
        submitButton = findViewById(R.id.submitButton);
        resultTextView = findViewById(R.id.resultTextView);
        //attemptsTextView = findViewById(R.id.attemptsTextView);

        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);

        setupButtons();
        initializeWebSocket();
    }

    private void setupButtons() {
        homeBtn.setOnClickListener(v ->
                startActivity(new Intent(NumberGuessActivity.this, HomeActivity.class))
        );

        backBtn.setOnClickListener(v ->
                startActivity(new Intent(NumberGuessActivity.this, GamesActivity.class))
        );

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitGuess();
            }
        });
    }

    // method to initialize web sockets
    private void initializeWebSocket() {
        OkHttpClient client = new OkHttpClient();
        String wsUrl = APP_URL.replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder()
                .url(wsUrl + "/guess-number/" + CURRENT_USER)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                runOnUiThread(() -> handleWebSocketMessage(text));
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                runOnUiThread(() -> {
                    Toast.makeText(NumberGuessActivity.this,
                            "Connection failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "WebSocket connection failure", t);
                });
            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                runOnUiThread(() -> Toast.makeText(NumberGuessActivity.this,
                        "Connected to game server", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosed(webSocket, code, reason);
            }
        });
    }

    private void handleWebSocketMessage(String text) {
        if (!text.isEmpty()) {
            switch (text.toUpperCase()) {
                case "HIGHER":
                    resultTextView.setText("Too low! Try a higher number.");
                    break;
                case "LOWER":
                    resultTextView.setText("Too high! Try a lower number.");
                    break;
                case "CORRECT":
                    resultTextView.setText("Congratulations! You guessed the number!");
                    submitButton.setEnabled(false); // Disable the submit button after correct guess
                    break;
                default:
                    // Handle other messages like welcome messages or round information
                    resultTextView.setText(text);
            }
        } else {
            resultTextView.setText("Empty response from server.");
        }
        // Clear the guess input for the next attempt
        guessEditText.setText("");
    }


    private void submitGuess() {
        String guessString = guessEditText.getText().toString().trim();

        if (guessString.isEmpty()) {
            Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show();
            return;
        }

        int guess;
        try {
            guess = Integer.parseInt(guessString);

            // Add range validation
            if (guess < 1 || guess > 100) {
                Toast.makeText(this, "Please enter a number between 1 and 100", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Increment attempts and update UI
//        attempts++;
//        attemptsTextView.setText("Attempts: " + attempts);

        // Modify to send the guess as a plain text message to match Spring's TextMessage
        if (webSocket != null) {
            webSocket.send(String.valueOf(guess));
        } else {
            Toast.makeText(this, "WebSocket connection lost", Toast.LENGTH_SHORT).show();
            initializeWebSocket(); // Try to reconnect
        }
    }
}
