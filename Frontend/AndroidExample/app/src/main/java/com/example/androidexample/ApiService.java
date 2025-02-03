package com.example.androidexample;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface ApiService {
    @GET("/trivia/leaderboard")
    Call<List<Object[]>> getLeaderboard();
}