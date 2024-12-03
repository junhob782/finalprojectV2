package com.cookandroid.finalprojectv2;

import static com.cookandroid.finalprojectv2.GPTCategoryFetcher.fetchQuestion;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.cookandroid.finalprojectv2.ChatMessage;
import com.cookandroid.finalprojectv2.ChatAdapter;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

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
    }

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
}
