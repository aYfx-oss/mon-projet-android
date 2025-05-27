package com.example.test.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiApi {
    @POST("models/gemini-2.0-flash:generateContent")
    Call<com.example.test.network.GeminiResponse> getChatResponse(
            @Query("key") String apiKey,
            @Body com.example.test.network.GeminiRequest request
    );
}
