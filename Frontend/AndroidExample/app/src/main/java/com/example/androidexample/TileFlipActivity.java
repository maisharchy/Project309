package com.example.androidexample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.GridLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TileFlipActivity extends AppCompatActivity {

    private static final String APP_URL = MainActivity.APP_URL;
    private static String CURRENT_USER = HomeActivity.currentUser;
    private static String CURRENT_SERVER = DashboardActivity.currentServer;

    private Button backBtn, homeBtn;
    private GridLayout gridLayout;
    private WebSocket webSocket;
    private List<CardView> tiles;
    private TextView scoreText;

    private static final int COLUMN_COUNT = 4;
    private static final int ROW_COUNT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tile_flip);

        // Initialize buttons
        backBtn = findViewById(R.id.btnBack);
        homeBtn = findViewById(R.id.btnHome);

        backBtn.setBackgroundColor(Color.BLACK);
        homeBtn.setBackgroundColor(Color.BLACK);

        gridLayout = findViewById(R.id.gridLayout);
        scoreText = findViewById(R.id.scoreText);

        tiles = new ArrayList<>();

        // call initializers
        initializeWebSocket();
        setupGrid();
        setupNavigationButtons();
    }

    //method to store listeners for nav buttons
    private void setupNavigationButtons() {
        homeBtn.setOnClickListener(v ->
                startActivity(new Intent(TileFlipActivity.this, HomeActivity.class))
        );

        backBtn.setOnClickListener(v ->
                startActivity(new Intent(TileFlipActivity.this, GamesActivity.class))
        );
    }

    // method to initialize web sockets
    private void initializeWebSocket() {
        OkHttpClient client = new OkHttpClient();
        String wsUrl = APP_URL.replace("http://", "ws://");
        // Correcting WebSocket URL to include userId and serverId as path parameters
        String websocketUrl = wsUrl + "/flipthetile/" + CURRENT_USER + "/" + CURRENT_SERVER;
        Request request = new Request.Builder()
                .url(websocketUrl)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                runOnUiThread(() -> handleWebSocketMessage(text));
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                runOnUiThread(() -> {
                    Toast.makeText(TileFlipActivity.this,
                            "Connection failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Optionally attempt to reconnect
                    initializeWebSocket();
                });
            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                runOnUiThread(() -> Toast.makeText(TileFlipActivity.this,
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


    //method to set up the grid for the tile flip game
    private void setupGrid() {
        int tileSize = getResources().getDimensionPixelSize(R.dimen.tile_size);

        for (int i = 0; i < ROW_COUNT * COLUMN_COUNT; i++) {
            CardView cardView = new CardView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = tileSize;
            params.height = tileSize;
            params.setMargins(8, 8, 8, 8);
            cardView.setLayoutParams(params);
            cardView.setCardBackgroundColor(Color.GRAY);
            cardView.setRadius(8);
            cardView.setId(i);

            final int tileId = i;
            cardView.setOnClickListener(v -> {
                if (cardView.isClickable()) {
                    onTileClick(tileId);
                }
            });

            tiles.add(cardView);
            gridLayout.addView(cardView);
        }
    }

    private void onTileClick(int tileId) {
        if (tiles.get(tileId).isClickable()) {
            webSocket.send("flip:" + tileId);
        }
    }

    private void handleWebSocketMessage(String message) {
        try {
            if (message.startsWith("User")) {
                // Parse user flip messages: "User 4 flipped tile 13"
                String[] parts = message.split(" ");
                int userId = Integer.parseInt(parts[1]);  // User 4
                int tileId = Integer.parseInt(parts[4]);  // tile 13
                // Handle tile flip action
                handleUserTileFlip(userId, tileId);

            } else if (message.startsWith("Tiles did not match")) {
                // Handle mismatch: "Tiles did not match: pink and orange"
                String[] parts = message.split(": ");
                String tileInfo = parts[1];  // pink and orange
                String[] colors = tileInfo.split(" and ");
                handleTileMismatch(colors);

            } else if (message.startsWith("Tiles have been flipped back")) {
                // Reset tiles back to unflipped state
                resetUnmatchedTiles();
            } else if (message.startsWith("ScoreUpdate:")) {
                // Handle score updates
                String scoreText = message.substring("ScoreUpdate:".length());
                updateScore(scoreText);
            } else if (message.startsWith("Game over!")) {
                showGameOver(message);
            } else if (message.startsWith("Matched pair:")) {
                // Extract tile IDs and mark them as matched
                String[] parts = message.split(": ")[1].split(" and ");
                int firstTileId = Integer.parseInt(parts[0]);
                int secondTileId = Integer.parseInt(parts[1]);
                markTilesAsMatched(firstTileId, secondTileId);
            }
        } catch (Exception e) {
            Log.e("WebSocket", "Error processing message: " + message, e);
        }
    }

    // Handle tile flip actions from the user (from server message like "User 4 flipped tile 13")
    private void handleUserTileFlip(int userId, int tileId) {
        runOnUiThread(() -> {
            CardView tile = tiles.get(tileId);
            // Display tile flipped by user
            tile.setCardBackgroundColor(getTileColor(tileId)); // Set a random or predefined color for flipped tile
            tile.setAlpha(1.0f);
            tile.setClickable(false); // Disable click to prevent re-flipping
            // Optionally show who flipped the tile
            //Toast.makeText(TileFlipActivity.this, "User " + userId + " flipped tile " + tileId, Toast.LENGTH_SHORT).show();
        });
    }

    // Handle non-matching tiles
    private void handleTileMismatch(String[] colors) {
        runOnUiThread(() -> {
            String tile1Color = colors[0];  // e.g., pink
            String tile2Color = colors[1];  // e.g., orange
            //Toast.makeText(TileFlipActivity.this, "Tiles did not match: " + tile1Color + " and " + tile2Color, Toast.LENGTH_SHORT).show();
        });
        resetUnmatchedTiles();
    }

    // Reset the tiles if they don't match
    private void resetUnmatchedTiles() {
        runOnUiThread(() -> {
            for (CardView tile : tiles) {
                // Only reset tiles that are not gray and not already matched (alpha != 0.5)
                if (tile.getCardBackgroundColor().getDefaultColor() != Color.GRAY && tile.getAlpha() != 0.5f) {
                    tile.setCardBackgroundColor(Color.GRAY);
                    tile.setClickable(true);
                }
            }
        });
    }



    // This function updates the UI when tiles are flipped
    private void updateTileState(int tileId, String state, String color) {
        runOnUiThread(() -> {
            CardView tile = tiles.get(tileId);
            switch (state) {
                case "flipped":
                    // Use color if provided, otherwise use default color method
                    int tileColor = (color != null) ? getColorFromString(color) : getTileColor(tileId);
                    tile.setCardBackgroundColor(tileColor);
                    tile.setAlpha(1.0f);
                    tile.setClickable(false);
                    break;
                case "unflipped":
                    tile.setCardBackgroundColor(Color.GRAY);
                    tile.setAlpha(1.0f);
                    tile.setClickable(true);
                    break;
                case "matched":
                case "vanished":
                    tile.setAlpha(0.5f);
                    tile.setClickable(false);
                    break;
            }
        });
    }

    // Add a new method to convert string colors
    // Convert string color names to Color objects
    private int getColorFromString(String colorName) {
        switch (colorName.toLowerCase()) {
            case "red": return Color.RED;
            case "blue": return Color.BLUE;
            case "green": return Color.GREEN;
            case "yellow": return Color.YELLOW;
            case "orange": return Color.rgb(255, 165, 0);
            case "purple": return Color.rgb(128, 0, 128);
            case "pink": return Color.rgb(255, 192, 203);
            case "brown": return Color.rgb(165, 42, 42);
            case "black": return Color.BLACK;
            case "teal": return Color.rgb(0, 128, 128);
            default: return Color.GRAY;
        }
    }


    private int getTileColor(int tileId) {
        Map<Integer, Integer> tileColorMap = new HashMap<Integer, Integer>() {{
            put(0, Color.RED);
            put(1, Color.BLUE);
            put(2, Color.GREEN);
            put(3, Color.YELLOW);
            put(4, Color.rgb(255, 165, 0)); // Orange
            put(5, Color.rgb(128, 0, 128)); // Purple
            put(6, Color.rgb(255, 192, 203)); // Pink
            put(7, Color.rgb(165, 42, 42)); // Brown
            put(8, Color.BLACK);
            put(9, Color.rgb(0, 128, 128)); // Teal
            // Match these with your backend colors
        }};

        return tileColorMap.getOrDefault(tileId % 10, Color.GRAY);
    }


    // Update the UI to reflect changes in tile colors
    private void updateTileUI(int tileId) {
        CardView tile = tiles.get(tileId);
        if (tile.getCardBackgroundColor().getDefaultColor() == Color.GRAY) {
            tile.setCardBackgroundColor(getTileColor(tileId));
        } else {
            tile.setCardBackgroundColor(Color.GRAY);
        }
    }

    private void updateTileColors(String colorMessage) {
        runOnUiThread(() -> {
            // Map color names to their hex values
            Map<String, String> colorMap = new HashMap<String, String>() {{
                put("red", "#FF0000");
                put("blue", "#0000FF");
                put("green", "#00FF00");
                put("yellow", "#FFFF00");
                put("orange", "#FFA500");
                put("purple", "#800080");
                put("pink", "#FFC0CB");
                put("brown", "#A52A2A");
                put("black", "#000000");
                put("teal", "#008080");
            }};

            // Find and update tiles with matching colors
            for (int i = 0; i < tiles.size(); i++) {
                for (Map.Entry<String, String> entry : colorMap.entrySet()) {
                    if (colorMessage.contains(entry.getKey())) {
                        tiles.get(i).setCardBackgroundColor(Color.parseColor(entry.getValue()));
                    }
                }
            }
        });
    }

    // Mark the matched tiles as matched
    private void markTilesAsMatched(int firstTileId, int secondTileId) {
        runOnUiThread(() -> {
            CardView firstTile = tiles.get(firstTileId);
            CardView secondTile = tiles.get(secondTileId);
            firstTile.setAlpha(0.5f);  // Make matched tiles semi-transparent
            secondTile.setAlpha(0.5f);
            firstTile.setClickable(false);
            secondTile.setClickable(false);
            //Toast.makeText(TileFlipActivity.this, "Matched pair: " + firstTileId + " and " + secondTileId, Toast.LENGTH_SHORT).show();
        });
    }


    // Update the score in the UI
    private void updateScore(String scoreMessage) {
        runOnUiThread(() -> {
            scoreText.setText("Scores: " + scoreMessage);  // Ensure the score is updated here
        });
    }


    private void showGameOver(String message) {
        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                    .setTitle("Game Over")
                    .setMessage(message)
                    .setPositiveButton("Play Again", (dialog, which) -> {
                        webSocket.send("restart"); // Send restart message to backend
                        recreate();
                    })
                    .setNegativeButton("Exit", (dialog, which) -> {
                        webSocket.close(1000, "Game ended");
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        });
    }



    private boolean isWebSocketConnected() {
        return webSocket != null; // You might want to add additional checks here
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webSocket != null) {
            webSocket.close(1000, "Activity paused");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webSocket == null || !isWebSocketConnected()) {
            initializeWebSocket();
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
