package com.example.test;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import okhttp3.*;

public class AssistantActivity extends AppCompatActivity {

    private static final String API_KEY = "TON_API_KEY_ICI";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY;

    private TextView tvChat;
    private EditText etMessage;
    private Button btnSend;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);

        tvChat = findViewById(R.id.tvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessageToGemini(message);
            }
        });
    }

    private void sendMessageToGemini(String userMessage) {
        JsonObject messagePart = new JsonObject();
        messagePart.addProperty("text", userMessage);

        JsonArray parts = new JsonArray();
        parts.add(messagePart);

        JsonObject content = new JsonObject();
        content.add("parts", parts);

        JsonArray contents = new JsonArray();
        contents.add(content);

        JsonObject requestBody = new JsonObject();
        requestBody.add("contents", contents);

        RequestBody body = RequestBody.create(
                requestBody.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(GEMINI_URL)
                .post(body)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    String reply = json.split("\"text\":\"")[1].split("\"")[0];

                    runOnUiThread(() -> {
                        tvChat.append("Moi : " + userMessage + "\n");
                        tvChat.append("Gemini : " + reply + "\n\n");
                        etMessage.setText("");
                    });
                }
            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
