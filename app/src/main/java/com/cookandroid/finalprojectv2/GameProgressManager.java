package com.cookandroid.finalprojectv2;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GameProgressManager {
    private static final String PREF_NAME = "GameProgress";
    private static final String KEY_PROGRESS = "Progress";

    private SharedPreferences sharedPreferences;

    private int currentLevel;
    private int currentHealth;
    private List<String> inventoryList;

    public GameProgressManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.currentLevel = 1; // 기본 레벨
        this.currentHealth = 100; // 기본 체력
        this.inventoryList = new ArrayList<>(); // 기본 빈 인벤토리
    }

    // 진행 상황 저장 (JSON 형태로 저장)
    public void saveProgress(JSONObject progress) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PROGRESS, progress.toString());
        editor.apply();
    }

    // 진행 상황 불러오기 (JSON 형태로 반환)
    public JSONObject loadProgress() {
        String progressString = sharedPreferences.getString(KEY_PROGRESS, null);
        if (progressString != null) {
            try {
                return new JSONObject(progressString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 진행 상황 삭제 (새로운 게임 시작 시)
    public void clearProgress() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_PROGRESS);
        editor.apply();
    }

    // 진행 상황을 JSON 객체로 변환
    public JSONObject createProgressJson() throws JSONException {
        JSONObject progress = new JSONObject();
        progress.put("level", currentLevel);
        progress.put("health", currentHealth);
        progress.put("inventory", new JSONArray(inventoryList));
        return progress;
    }

    // 진행 상황 업데이트
    public void updateProgress(int level, int health, List<String> inventory) {
        this.currentLevel = level;
        this.currentHealth = health;
        this.inventoryList = inventory;
    }

    // 진행 상황 데이터를 불러와 필드에 적용
    public void applyLoadedProgress(JSONObject progress) throws JSONException {
        this.currentLevel = progress.getInt("level");
        this.currentHealth = progress.getInt("health");
        JSONArray inventoryArray = progress.getJSONArray("inventory");
        this.inventoryList = new ArrayList<>();
        for (int i = 0; i < inventoryArray.length(); i++) {
            inventoryList.add(inventoryArray.getString(i));
        }
    }
}
