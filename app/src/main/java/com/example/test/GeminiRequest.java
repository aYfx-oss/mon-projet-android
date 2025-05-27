package com.example.test.network;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

public class GeminiRequest {
    @SerializedName("contents")
    public List<Content> contents;

    public GeminiRequest(String userText) {
        this.contents = Collections.singletonList(new Content(userText));
    }

    public static class Content {
        @SerializedName("role")
        public String role = "user";

        @SerializedName("parts")
        public List<Part> parts;

        public Content(String text) {
            this.parts = Collections.singletonList(new Part(text));
        }
    }

    public static class Part {
        @SerializedName("text")
        public String text;

        public Part(String text) {
            this.text = text;
        }
    }
}
