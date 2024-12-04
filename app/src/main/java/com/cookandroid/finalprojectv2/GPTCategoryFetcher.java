package com.cookandroid.finalprojectv2;

import android.util.Log;

import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import org.json.JSONArray;

public class GPTCategoryFetcher {
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public static void fetchQuestion(String question, CategoryCallback callback) {
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonBody = new JSONObject();
        String API_KEY = "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"; // 실제 유효한 API Key로 변경

        try {
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", question + " Please explain or answer the following sentence in Korean.");
            messages.put(message);

            jsonBody.put("model", "gpt-4"); // 모델지정
            jsonBody.put("messages", messages);
            jsonBody.put("max_tokens", 50);
            jsonBody.put("temperature", 0.7);

            RequestBody body = RequestBody.create(
                    MediaType.get("application/json; charset=utf-8"),
                    jsonBody.toString()
            );

            Request request = new Request.Builder()
                    .url(OPENAI_API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("GPTCategoryFetcher", "API 요청 실패", e);
                    callback.onCategoryFetched("정보 없음");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            JSONArray choices = jsonResponse.getJSONArray("choices");
                            if (choices.length() > 0) {
                                String category = choices.getJSONObject(0)
                                        .getJSONObject("message")
                                        .getString("content");
                                Log.d("GPTCategoryFetcher", "카테고리 정보: " + category);
                                callback.onCategoryFetched(category);
                            } else {
                                Log.d("GPTCategoryFetcher", "응답 choices 배열이 비어 있음");
                                callback.onCategoryFetched("정보 없음");
                            }
                        } catch (JSONException e) {
                            Log.e("GPTCategoryFetcher", "JSON Parsing Error", e);
                            callback.onCategoryFetched("정보 없음");
                        }
                    } else {
                        Log.e("GPTCategoryFetcher", "응답 실패, 코드: " + response.code());
                        callback.onCategoryFetched("정보 없음");
                    }
                }
            });
        } catch (JSONException e) {
            Log.e("GPTCategoryFetcher", "JSON 생성 실패", e);
            callback.onCategoryFetched("정보 없음");
        }
    }

    public interface CategoryCallback {
        void onCategoryFetched(String category);
    }
}
