package com.cookandroid.finalprojectv2;
import android.util.Log;

import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Locale;

import org.json.JSONArray;

public class GPTCategoryFetcher {
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public static void fetchQuestion(String question, CategoryCallback callback) {
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonBody = new JSONObject();
        String API_KEY = "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";
        Log.d("GPTCategoryFetcher","API : " + API_KEY);
        try {
            // 요청할 메시지를 작성합니다.
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", question + "Please explain or answer the following sentence in Korean" ); // 여기에서 질문을 수정(왠만하면 영어로 질문하는게 더 똑똑해짐)
            messages.put(message);


            jsonBody.put("model", "gpt-4o");
            jsonBody.put("messages", messages);  // messages 배열 추가
            jsonBody.put("max_tokens", 50);
            jsonBody.put("temperature", 0.7);

            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), jsonBody.toString());

            Request request = new Request.Builder()
                    .url(OPENAI_API_URL) // 이 부분은 v1/chat/completions로 수정해야 함
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Log.d("GPTCategoryFetcher","onFailure메소드에서 실패한 응답 걸러냄");
                    callback.onCategoryFetched("정보 없음"); // 기본값 설정
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            String category = jsonResponse.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message") // "message" 객체를 가져옴
                                    .getString("content");
                            Log.d("GPTCategoryFetcher", "카테고리 정보 : " + category);
                            callback.onCategoryFetched(category);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("GPTCategoryFetcher", "응답은 받았는데! 처리가 이상하네!");
                            callback.onCategoryFetched("정보 없음");
                        }
                    } else {
                        int responseCode = response.code();
                        Log.d("GPTCategoryFetcher", "onResponse에서 응답 실패");
                        //Log.d("GPTCategoryFetcher", "응답 코드: " + responseCode);
                        //String errorMessage = response.body() != null ? response.body().string() : "No response body";
                        //Log.d("GPTCategoryFetcher", "서버 응답: " + errorMessage);

                        // 429 코드 처리
                        if (responseCode == 429) {
                            Log.d("GPTCategoryFetcher", "쿼타 초과 발생. 잠시 후 다시 시도합니다.");
                            // 사용자에게 알리거나 재시도를 고려합니다.
                        }

                        callback.onCategoryFetched("정보 없음");
                    }
                }
            });
        }  catch (JSONException e) {
            Log.e("GPTCategoryFetcher", "JSON Parsing Error", e);
            callback.onCategoryFetched("정보 없음");
        }
    }


    //인터페이스가 존재해야 콜백을 통해 비동기적으로 네트워크 처리 가능. 이젠 쓸모 없지만
    public interface CategoryCallback {
        void onCategoryFetched(String category);
    }
}




