package com.cookandroid.finalprojectv2;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GameManager {
    private Context context;

    public GameManager(Context context) {
        this.context = context;
    }

    // 게임 템플릿 JSON 파일을 로드
    public JSONObject loadGameTemplate() {
        try {
            FileInputStream fis = context.openFileInput("game_template.json");
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            String jsonStr = new String(buffer, StandardCharsets.UTF_8);
            return new JSONObject(jsonStr);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 세이브 파일 로드
    public JSONObject loadSaveData() {
        try {
            FileInputStream fis = context.openFileInput("save_data.json");
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            String jsonStr = new String(buffer, StandardCharsets.UTF_8);
            return new JSONObject(jsonStr);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 세이브 데이터 저장
    public void saveGameProgress(JSONObject saveData) {
        try {
            FileOutputStream fos = context.openFileOutput("save_data.json", Context.MODE_PRIVATE);
            fos.write(saveData.toString().getBytes(StandardCharsets.UTF_8));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 게임 진행
    public void progressToNextEncounter(JSONObject saveData, int encounterId) {
        try {
            // 현재 진행 중인 스테이지 정보
            int currentStage = saveData.getInt("currentStage");

            // 해당 스테이지의 진행 상황 업데이트
            JSONObject progress = saveData.getJSONObject("progress");
            JSONObject currentStageProgress = progress.getJSONObject("stage_" + currentStage);

            // 인카운터 완료 목록에 추가
            JSONArray completedEncounters = currentStageProgress.getJSONArray("encountersCompleted");
            completedEncounters.put(encounterId);

            // 세이브 데이터 업데이트
            saveData.put("progress", progress);
            saveGameProgress(saveData);

            // 다음 스테이지로 이동
            if (completedEncounters.length() == loadGameTemplate().getJSONArray("stages").getJSONObject(currentStage - 1).getJSONArray("encounters").length()) {
                currentStage++;
                saveData.put("currentStage", currentStage);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}