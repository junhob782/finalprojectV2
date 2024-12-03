package com.cookandroid.finalprojectv2;

import static com.cookandroid.finalprojectv2.GPTCategoryFetcher.fetchQuestion;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private TextView textView;
    private String anser1;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editText = findViewById(R.id.edtInput);
        textView = findViewById(R.id.edtGpt);
    }

    public void gptSend(View view) {
        if(editText.getText().toString().isEmpty())
            return;
        String string = editText.getText().toString();
        fetchQuestion(string, new GPTCategoryFetcher.CategoryCallback() {
                    @Override
                    public void onCategoryFetched(String answer) {
                        runOnUiThread(() -> {
                            textView.setText(answer);
                            editText.setText("");
                        });
                    }
                });
                editText.setText("");  //UI 작업은 CategoryCallback (Callback)을 통해서 하면 코딩 규칙 위반
        textView.setText("GPT가 질문을 작성하는 중입니다.");
    }
}
