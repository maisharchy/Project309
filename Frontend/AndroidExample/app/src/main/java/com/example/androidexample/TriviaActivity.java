package com.example.androidexample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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


public class TriviaActivity extends AppCompatActivity {

    private static final String APP_URL = MainActivity.APP_URL;
    private static String CURRENT_USER, CURRENT_SERVER, WS_URL;
    private Button backBtn, homeBtn, answerBtn; //, questionBtn;
    private WebSocket webSocket;
    private TextView questionText, previousQuestion, previousAnswer;
    private EditText answerEntry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

        CURRENT_USER = HomeActivity.currentUser;
        CURRENT_SERVER = DashboardActivity.currentServer;
        WS_URL = APP_URL.replace("http://", "ws://") + "/trivia/" + CURRENT_USER + "/" + CURRENT_SERVER;

        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);
        questionText = findViewById(R.id.questionText);
        previousQuestion = findViewById(R.id.previousQuestionText);
        previousAnswer = findViewById(R.id.previousAnswerText);
        answerEntry = findViewById(R.id.answerEdit);
        answerBtn = findViewById(R.id.btnAnswer);
        homeBtn.setBackgroundColor(Color.BLACK);
        backBtn.setBackgroundColor(Color.BLACK);
        answerBtn.setBackgroundColor(Color.BLACK);

        // call initializers
        initializeWebSocket();
        setupButtons();
    }

    //method to store listeners for nav buttons
    private void setupButtons() {
        homeBtn.setOnClickListener(v ->
                startActivity(new Intent(TriviaActivity.this, HomeActivity.class))
        );

        backBtn.setOnClickListener(v ->
                startActivity(new Intent(TriviaActivity.this, GamesActivity.class))
        );

        answerBtn.setOnClickListener(v -> {
            webSocket.send(CURRENT_USER + ":" + answerEntry.getText().toString());
        });
    }

    // method to initialize web sockets
    private void initializeWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(WS_URL)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                runOnUiThread(() -> handleWebSocketMessage(text));
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                runOnUiThread(() -> Toast.makeText(TriviaActivity.this,
                        "Connection failed: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }

            // Required overrides for WebSocketListener
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                runOnUiThread(() -> Toast.makeText(TriviaActivity.this,
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
        if(message.contains("answered incorrectly")){
            //Expects: "[userId] answered incorrectly"
            String[] parts = message.split(" ");
            if (parts[0].equals(CURRENT_USER)){
                Toast.makeText(TriviaActivity.this, "That is incorrect", Toast.LENGTH_LONG).show();
            }
        } else if (message.contains("answered correctly")) {
            //Expects: "[userId] answered correctly. Correct answer: [answer]"
            String[] parts = message.split("Correct answer: ");
            String previousQuestionTxt = "Previous question:\n" + questionText.getText();
            String previousAnswerTxt = "Previous answer: " + parts[1];
            previousQuestion.setText(previousQuestionTxt);
            previousAnswer.setText(previousAnswerTxt);
            String answerer = parts[0].split(" ")[0];
            if (answerer.equals(CURRENT_USER)){
                Toast.makeText(TriviaActivity.this, "Congrats! You answered correctly", Toast.LENGTH_LONG).show();
            }
        } else if (message.contains("Current question")) {
            //Expect: "Current question: [question]"
            String question = message.substring(18);
            questionText.setText(question);
        } else if (message.contains("Game Over")) {
            questionText.setText(message);
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
