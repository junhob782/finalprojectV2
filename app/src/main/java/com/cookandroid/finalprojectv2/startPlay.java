package com.cookandroid.finalprojectv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class startPlay extends AppCompatActivity {

    private GameManager gameManager;  // 게임 진행 관리

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_play);  // activity_start_play.xml

        gameManager = new GameManager(this);  // GameManager 초기화

        // "새로하기" 버튼 클릭 리스너
        Button btnStart = findViewById(R.id.btn_Start);
        btnStart.setOnClickListener(view -> startNewGame());

        // "이어하기" 버튼 클릭 리스너
        Button btnLoad = findViewById(R.id.btn_Load);
        btnLoad.setOnClickListener(view -> loadGameProgress());
    }

    // 새로 게임 시작
    private void startNewGame() {
        // 게임 상태 초기화
        JSONObject initialSaveData = new JSONObject();
        try {
            initialSaveData.put("currentStage", 1);  // 처음에는 스테이지 1
            initialSaveData.put("player", new JSONObject());
            initialSaveData.put("progress", new JSONObject());

            // 새로 게임 시작 시, 템플릿과 함께 초기화된 세이브 데이터 저장
            gameManager.saveGameProgress(initialSaveData);

            // MainActivity로 이동
            Intent intent = new Intent(startPlay.this, MainActivity.class);
            startActivity(intent);
            finish();  // 현재 액티비티 종료

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "새 게임 시작 중 오류 발생", Toast.LENGTH_SHORT).show();
        }
    }

    // 저장된 게임 진행 상태 로드
    private void loadGameProgress() {
        // 저장된 세이브 파일 로드
        JSONObject savedData = gameManager.loadSaveData();
        if (savedData != null) {
            try {
                int currentStage = savedData.getInt("currentStage");
                // 저장된 데이터에 따라 게임을 이어서 시작
                Intent intent = new Intent(startPlay.this, MainActivity.class);
                intent.putExtra("currentStage", currentStage);  // 현재 스테이지 전달
                startActivity(intent);
                finish();  // 현재 액티비티 종료

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "세이브 파일 로드 실패", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "세이브된 게임이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}