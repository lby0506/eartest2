package com.example.t_sample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    private List<Question> questions;
    private int currentIndex = 0;
    private List<String> selectedFilters = new ArrayList<>();

    private TextView tvQuestion;
    private RadioGroup radioGroup;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        tvQuestion = findViewById(R.id.tvQuestion);
        radioGroup = findViewById(R.id.radioGroupAnswers);
        btnNext = findViewById(R.id.btnNext);

        EarbudData data = JsonLoader.loadJson(this);

        // ✅ Null 체크 추가 (중요!)
        if (data == null || data.Q == null || data.Q.isEmpty()) {
            Toast.makeText(this, "질문 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 화면 종료
            return;
        }

        questions = data.Q;

        showQuestion();

        btnNext.setOnClickListener(view -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            if (checkedId != -1) {
                RadioButton selectedButton = findViewById(checkedId);
                Answer selected = (Answer) selectedButton.getTag();
                selectedFilters.addAll(selected.filters);

                currentIndex++;
                if (currentIndex < questions.size()) {
                    showQuestion();
                } else {
                    Intent intent = new Intent(QuestionActivity.this, ResultActivity.class);
                    intent.putStringArrayListExtra("filters", new ArrayList<>(selectedFilters));
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void showQuestion() {
        Question q = questions.get(currentIndex);
        tvQuestion.setText(q.question);
        radioGroup.removeAllViews();

        for (int i = 0; i < q.answers.size(); i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(q.answers.get(i).text);
            rb.setTextSize(16);
            rb.setPadding(40, 48, 40, 48);
            rb.setTag(q.answers.get(i));
            rb.setBackground(getDrawable(R.drawable.card_selector));
            rb.setTextColor(getResources().getColor(R.color.black));
            rb.setButtonDrawable(android.R.color.transparent); // 라디오 아이콘 제거

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 16, 0, 0);
            rb.setLayoutParams(params);

            radioGroup.addView(rb);
        }
    }
}
