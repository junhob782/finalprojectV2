package com.cookandroid.finalprojectv2;

import static com.cookandroid.finalprojectv2.GPTCategoryFetcher.fetchQuestion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    private GameProgressManager progressManager; // 게임 진행 상황 관리
    private String gameProgress = "Level 1"; // 기본 시작 지점

    // 플레이어 초기 상태
    private int playerHealth = 100;
    private int playerAttack = 10;
    private int playerDefense = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.recGpt); // RecyclerView 참조
        editText = findViewById(R.id.edtInput);  // EditText 참조
        chatMessages = new ArrayList<>();        // 데이터 리스트 초기화
        chatAdapter = new ChatAdapter(chatMessages); // 어댑터 초기화

        // RecyclerView 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // 레이아웃 매니저 설정
        recyclerView.setAdapter(chatAdapter); // 어댑터 연결

        // GameProgressManager 초기화
        progressManager = new GameProgressManager(this);

        // 게임 이어하기 확인
        checkContinueGame();
    }

    // 자동 저장을 위한 onPause
    @Override
    protected void onPause() {
        super.onPause();
        // GameProgressManager를 통해 진행 상황 저장
        try {
            JSONObject progress = progressManager.createProgressJson();
            progressManager.saveProgress(progress);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadGame(View view) {
        JSONObject savedProgress = progressManager.loadProgress();
        if (savedProgress != null) {
            try {
                // 저장되어 있는 진행 상황 복원하기
                gameProgress = savedProgress.getString("gameProgress");
                playerHealth = savedProgress.getInt("playerHealth");
                playerAttack = savedProgress.getInt("playerAttack");
                playerDefense = savedProgress.getInt("playerDefense");

                // 진행 상황을 성공적으로 로드했음을 알림
                Toast.makeText(this, "저장된 게임을 불러옵니다: " + gameProgress, Toast.LENGTH_SHORT).show();

                // Intent를 통해 GameManager로 데이터 전달
                Intent intent = new Intent(this, GameManager.class);
                intent.putExtra("gameProgress", gameProgress);
                intent.putExtra("playerHealth", playerHealth);
                intent.putExtra("playerAttack", playerAttack);
                intent.putExtra("playerDefense", playerDefense);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "저장된 데이터를 읽는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 저장된 진행 상황이 없는 경우
            Toast.makeText(this, "저장된 진행 상황이 없습니다. 새 게임을 시작하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkContinueGame() {
        JSONObject savedProgress = progressManager.loadProgress();

        if (savedProgress != null) {
            // 저장된 진행 상황이 있으면 이어할지 묻는 대화 상자 표시
            try {
                gameProgress = savedProgress.getString("gameProgress");
                playerHealth = savedProgress.getInt("playerHealth");
                playerAttack = savedProgress.getInt("playerAttack");
                playerDefense = savedProgress.getInt("playerDefense");

                new AlertDialog.Builder(this)
                        .setTitle("게임을 이어하시겠습니까?")
                        .setMessage("이전에 저장된 진행 상황: " + gameProgress)
                        .setPositiveButton("Y (이어하기)", (dialog, which) -> {
                            // Intent를 통해 GameManager로 데이터 전달
                            Intent intent = new Intent(this, GameManager.class);
                            intent.putExtra("gameProgress", gameProgress);
                            intent.putExtra("playerHealth", playerHealth);
                            intent.putExtra("playerAttack", playerAttack);
                            intent.putExtra("playerDefense", playerDefense);
                            startActivity(intent);
                        })
                        .setNegativeButton("N (새 게임 시작)", (dialog, which) -> {
                            progressManager.clearProgress(); // 저장된 진행 상황 초기화
                            resetGameState(); // 초기 상태로 복원
                            Toast.makeText(this, "새 게임을 시작합니다.", Toast.LENGTH_SHORT).show();
                        })
                        .setCancelable(false)
                        .show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // 저장된 진행 상황이 없으면 새 게임 시작
            resetGameState();
            Toast.makeText(this, "새 게임을 시작합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetGameState() {
        gameProgress = "Level 1";
        playerHealth = 100;
        playerAttack = 10;
        playerDefense = 5;
    }

    public void saveProgress(View view) {
        // 현재 진행 상황 저장
        JSONObject progress = new JSONObject();
        try {
            progress.put("gameProgress", gameProgress);
            progress.put("playerHealth", playerHealth);
            progress.put("playerAttack", playerAttack);
            progress.put("playerDefense", playerDefense);

            progressManager.saveProgress(progress);
            Toast.makeText(this, "진행 상황이 저장되었습니다: " + gameProgress, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
