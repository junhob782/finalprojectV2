package com.cookandroid.finalprojectv2;

import static com.cookandroid.finalprojectv2.GPTCategoryFetcher.fetchQuestion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private GameManager gameManager;
//    private GameProgressManager progressManager; // 게임 진행 상황 관리
//    private String gameProgress = "Level 1"; // 기본 시작 지점
//
//    // 플레이어 초기 상태
//    private int playerHealth = 100;
//    private int playerAttack = 10;
//    private int playerDefense = 5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.recGpt);
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        editText = findViewById(R.id.edtInput);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

//        // GameProgressManager 초기화
//        progressManager = new GameProgressManager(this);


        gameManager = new GameManager(this);

        // "StartPlayActivity"에서 전달된 인텐트 받기
        Intent intent = getIntent();
        int currentStage = intent.getIntExtra("currentStage", 1);  // 기본값 1로 설정

        // 저장된 게임 진행을 통해 받은 currentStage 출력
        Toast.makeText(this, "현재 스테이지: " + currentStage, Toast.LENGTH_SHORT).show();


        //게임 진행사항 로드
        JSONObject saveData = gameManager.loadSaveData();
        if (saveData != null) {
            try {
                currentStage = saveData.getInt("currentStage");
                Toast.makeText(this, "현재 스테이지: " + currentStage, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        // 템플릿 데이터 로드
        JSONObject templateData = JsonLoader.loadTemplateData(this, "game_template.json");
        if (templateData != null) {
            // 데이터 활용
            Toast.makeText(this, "Template Loaded Successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to Load Template", Toast.LENGTH_SHORT).show();
        }


//        // 게임 이어하기 확인
//        checkContinueGame();
    }

//    private void checkContinueGame() {
//        JSONObject savedProgress = progressManager.loadProgress();
//
//        if (savedProgress != null) {
//            // 저장된 진행 상황이 있으면 이어할지 묻는 대화 상자 표시
//            try {
//                gameProgress = savedProgress.getString("gameProgress");
//                playerHealth = savedProgress.getInt("playerHealth");
//                playerAttack = savedProgress.getInt("playerAttack");
//                playerDefense = savedProgress.getInt("playerDefense");
//
//                new AlertDialog.Builder(this)
//                        .setTitle("게임을 이어하시겠습니까?")
//                        .setMessage("이전에 저장된 진행 상황: " + gameProgress)
//                        .setPositiveButton("Y (이어하기)", (dialog, which) -> {
//                            Toast.makeText(this, "게임을 이어합니다: " + gameProgress, Toast.LENGTH_SHORT).show();
//                        })
//                        .setNegativeButton("N (새 게임 시작)", (dialog, which) -> {
//                            progressManager.clearProgress(); // 저장된 진행 상황 초기화
//                            resetGameState(); // 초기 상태로 복원
//                            Toast.makeText(this, "새 게임을 시작합니다.", Toast.LENGTH_SHORT).show();
//                        })
//                        .setCancelable(false)
//                        .show();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
//            // 저장된 진행 상황이 없으면 새 게임 시작
//            resetGameState();
//            Toast.makeText(this, "새 게임을 시작합니다.", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void resetGameState() {
//        gameProgress = "Level 1";
//        playerHealth = 100;
//        playerAttack = 10;
//        playerDefense = 5;
//    }

//    public void saveProgress(View view) {
//        // 현재 진행 상황 저장
//        JSONObject progress = new JSONObject();
//        try {
//            progress.put("gameProgress", gameProgress);
//            progress.put("playerHealth", playerHealth);
//            progress.put("playerAttack", playerAttack);
//            progress.put("playerDefense", playerDefense);
//
//            progressManager.saveProgress(progress);
//            Toast.makeText(this, "진행 상황이 저장되었습니다: " + gameProgress, Toast.LENGTH_SHORT).show();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


//    public void nextLevel(View view) {
//        // 게임 진행 상황 업데이트
//        int currentLevel = Integer.parseInt(gameProgress.split(" ")[1]);
//        gameProgress = "Level " + (currentLevel + 1);
//        Toast.makeText(this, "다음 레벨로 이동합니다: " + gameProgress, Toast.LENGTH_SHORT).show();
//    }
    public void gptSend(View view) {
        if (editText.getText().toString().isEmpty()) {
            return;
        }

        // 사용자의 입력 추가
        String userMessage = editText.getText().toString();
        chatMessages.add(new ChatMessage(userMessage, true)); // true: 사용자 메시지
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);

        // RecyclerView 자동 스크롤
        recyclerView.scrollToPosition(chatMessages.size() - 1);

        // GPT 응답 처리
        fetchQuestion(userMessage, new GPTCategoryFetcher.CategoryCallback() {
            @Override
            public void onCategoryFetched(String gptAnswer) {
                runOnUiThread(() -> {
                    // GPT 응답 추가
                    chatMessages.add(new ChatMessage(gptAnswer, false)); // false: GPT 메시지
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);

                    // RecyclerView 자동 스크롤
                    recyclerView.scrollToPosition(chatMessages.size() - 1);
                });
            }
        });

        // 입력 필드 초기화
        editText.setText("");
    }

    public void progressToNextEncounter(View view) {
        // 세이브 데이터를 로드
        JSONObject saveData = gameManager.loadSaveData();
        if (saveData != null) {
            try {
                // currentStage가 존재하는지 확인
                if (saveData.has("currentStage")) {
                    int currentStage = saveData.getInt("currentStage");

                    // 진행 상황 업데이트
                    JSONObject progress = saveData.getJSONObject("progress");
                    JSONObject currentStageProgress = progress.getJSONObject("stage_" + currentStage);

                    // encounterId 값 처리
                    int encounterId = 101; // 예시로 encounterId를 101로 설정
                    gameManager.progressToNextEncounter(saveData, encounterId);

                    // 완료 메시지 출력
                    Toast.makeText(this, "인카운터 진행 완료!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "currentStage 값이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "JSON 파싱 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "세이브 데이터를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }


}
