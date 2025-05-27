package com.example.test;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.network.GeminiApi;
import com.example.test.network.GeminiRequest;
import com.example.test.network.GeminiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeminiChatActivity extends AppCompatActivity {

    private TextView tvResponse;
    private EditText etMessage;
    private Button btnEnvoyer;
    private GeminiApi geminiApi;

    private final String BASE_URL = "https://generativelanguage.googleapis.com/";
    private final String API_KEY = "AIzaSyC4loTatpXuPFmbJb6BH8rlj1GTWXDrBk0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gemini_chat);

        tvResponse = findViewById(R.id.tvResponse);
        etMessage = findViewById(R.id.etMessage);
        btnEnvoyer = findViewById(R.id.btnEnvoyer);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL + "v1beta/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        geminiApi = retrofit.create(GeminiApi.class);

        btnEnvoyer.setOnClickListener(v -> envoyerMessage());
    }

    private void envoyerMessage() {
        String message = etMessage.getText().toString().trim();
        if (message.isEmpty()) {
            etMessage.setError("Champ vide !");
            return;
        }

        GeminiRequest request = new GeminiRequest(message);

        geminiApi.getChatResponse(API_KEY, request).enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().candidates != null && !response.body().candidates.isEmpty()) {

                    String text = response.body().candidates.get(0).content.parts.get(0).text;
                    tvResponse.setText(text);
                } else {
                    tvResponse.setText("Erreur dans la réponse de Gemini.");
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                tvResponse.setText("Erreur réseau : " + t.getMessage());
            }
        });
    }
}
