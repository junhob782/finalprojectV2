package com.cookandroid.finalprojectv2;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class GameProgressManager {
    private static final String PREF_NAME = "GameProgress";
    private static final String KEY_PROGRESS = "Progress";

    private SharedPreferences sharedPreferences;

    public GameProgressManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // 진행 상황 저장 (JSON 형태로 저장)
    public void saveProgress(JSONObject progress) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PROGRESS, progress.toString()); // JSON 객체를 문자열로 변환 후 저장
        editor.apply(); // 비동기로 저장 적용
    }

    // 진행 상황 불러오기 (JSON 형태로 반환)
    public JSONObject loadProgress() {
        String progressString = sharedPreferences.getString(KEY_PROGRESS, null); // 저장된 문자열 불러오기
        if (progressString != null) {
            try {
                return new JSONObject(progressString); // 문자열을 JSON 객체로 변환
            } catch (JSONException e) {
                e.printStackTrace(); // JSON 변환 실패 시 예외 처리
            }
        }
        return null; // 저장된 데이터가 없거나 변환 실패
    }

    // 진행 상황 삭제 (새로운 게임 시작 시)
    public void clearProgress() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_PROGRESS); // 저장된 데이터 삭제
        editor.apply();
    }

    // 진행 상황이 존재하는지 확인
    public boolean hasProgress() {
        return sharedPreferences.contains(KEY_PROGRESS); // KEY_PROGRESS 키가 있는지 확인
    }
}
