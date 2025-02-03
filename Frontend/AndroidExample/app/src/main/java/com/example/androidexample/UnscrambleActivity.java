package com.example.androidexample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class UnscrambleActivity extends AppCompatActivity {

    private static final String APP_URL = MainActivity.APP_URL;
    private static String CURRENT_USER, CURRENT_SERVER, WS_URL;
    private Button backBtn, homeBtn, controlBtn;
    private WebSocket webSocket;
    private TextView scrambled, scrambledTitle, unscrambledTitle;
    private EditText unscrambled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unscramble);

        CURRENT_USER = HomeActivity.currentUser;
        CURRENT_SERVER = DashboardActivity.currentServer;
        WS_URL = APP_URL.replace("http://", "ws://") + "/unscramble/" + CURRENT_USER + "/" + CURRENT_SERVER;

        scrambled = findViewById(R.id.scrambled);
        unscrambled = findViewById(R.id.unscrambled);
        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);
        controlBtn = findViewById(R.id.btnControl);
        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);
        controlBtn.setBackgroundColor(Color.BLACK);
        scrambledTitle = findViewById(R.id.scrambledTitle);
        unscrambledTitle = findViewById(R.id.unscrambledTitle);

        initializeWebSocket();
        setupButtons();

    }

    //method to store listeners for nav buttons
    private void setupButtons() {
        homeBtn.setOnClickListener(v ->
                startActivity(new Intent(UnscrambleActivity.this, HomeActivity.class))
        );

        backBtn.setOnClickListener(v ->
                startActivity(new Intent(UnscrambleActivity.this, GamesActivity.class))
        );

        controlBtn.setOnClickListener(v -> {
            //webSocket.send(CURRENT_USER + ":" + unscrambled.getText().toString());
            webSocket.send(unscrambled.getText().toString());
        });
    }

    // method to initialize web sockets
    private void initializeWebSocket() {
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new Request.Builder()
                .url(WS_URL)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                runOnUiThread(() -> handleWebSocketMessage(text));
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable okhttp3.Response response) {
                runOnUiThread(() -> Toast.makeText(UnscrambleActivity.this,
                        "Connection failed: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }

            // Required overrides for WebSocketListener
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                runOnUiThread(() -> Toast.makeText(UnscrambleActivity.this,
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

    private void handleWebSocketMessage(String message) {
        if(message.contains("Incorrect")){
            //Expects: "Incorrect! Try again."
            Toast.makeText(UnscrambleActivity.this, "That is incorrect", Toast.LENGTH_LONG).show();
        } else if (message.contains("answered correctly")) {
            //Expects: "[userId] answered correctly..."
            String answerer = message.split(" ")[0];
            if (answerer.equals(CURRENT_USER)){
                Toast.makeText(UnscrambleActivity.this, "Congrats! You answered correctly", Toast.LENGTH_LONG).show();
            }
        } else if (message.contains("Unscramble this word")) {
            //Expect: "Unscramble this word: [word]"
            String scrambledWord = message.substring(22);
            scrambled.setText(scrambledWord);
            unscrambled.setText("");
        } else if (message.contains("Game Over")) {
            scrambled.setText(message);
            scrambledTitle.setVisibility(View.INVISIBLE);
            unscrambledTitle.setVisibility(View.INVISIBLE);
            unscrambled.setVisibility(View.INVISIBLE);
            controlBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Activity destroyed");
        }
    }
}
