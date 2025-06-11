package com.example.t_sample;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {
    private List<Question> questions;
    private int currentIndex = 0;
    private List<Answer> selectedAnswers;

    private TextView tvQuestion;
    private RadioGroup radioGroup;
    private Button btnPrev, btnNext;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        tvQuestion = findViewById(R.id.tvQuestion);
        radioGroup = findViewById(R.id.radioGroupAnswers);
        btnPrev    = findViewById(R.id.btnPrev);
        btnNext    = findViewById(R.id.btnNext);

        questions = JsonLoader.loadQuestions(this);
        if (questions.isEmpty()) {
            Toast.makeText(this, "질문 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        selectedAnswers = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            selectedAnswers.add(null);
        }

        showQuestion();

        btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                showQuestion();
            }
        });

        btnNext.setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(this, "하나를 선택해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton rb = findViewById(checkedId);
            Answer sel = (Answer) rb.getTag();
            selectedAnswers.set(currentIndex, sel);

            currentIndex++;
            if (currentIndex < questions.size()) {
                showQuestion();
            } else {
                // QA 텍스트 리스트
                ArrayList<String> qaList = new ArrayList<>();
                // 필터 리스트
                ArrayList<String> filters = new ArrayList<>();
                // 선택한 답변 텍스트 리스트 (ResultActivity에서 조건 문장용)
                ArrayList<String> selectedAnswersText = new ArrayList<>();

                for (int i = 0; i < questions.size(); i++) {
                    Answer a = selectedAnswers.get(i);
                    if (a != null) {
                        qaList.add(questions.get(i).question + "\n→ " + a.text);
                        selectedAnswersText.add(a.text);
                        for (String f : a.filters) {
                            if (!filters.contains(f)) filters.add(f);
                        }
                    } else {
                        qaList.add(questions.get(i).question + "\n→ 선택 없음");
                    }
                }

                Intent intent = new Intent(this, ResultActivity.class);
                intent.putStringArrayListExtra("qaList", qaList);
                intent.putStringArrayListExtra("filters", filters);
                intent.putStringArrayListExtra("selectedAnswersText", selectedAnswersText); // ✅ 추가
                startActivity(intent);
                finish();
            }
        });
    }

    private void showQuestion() {
        radioGroup.clearCheck();
        radioGroup.removeAllViews();

        Question q = questions.get(currentIndex);
        tvQuestion.setText(q.question);

        Answer prev = selectedAnswers.get(currentIndex);
        for (Answer a : q.answers) {
            RadioButton rb = new RadioButton(this);
            rb.setText(a.text);
            rb.setTag(a);
            rb.setId(View.generateViewId());
            rb.setTextSize(16);
            rb.setPadding(40,48,40,48);
            rb.setBackground(getDrawable(R.drawable.card_selector));
            rb.setTextColor(getResources().getColor(R.color.black));
            rb.setButtonDrawable(android.R.color.transparent);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0,16,0,0);
            rb.setLayoutParams(params);

            if (prev != null && prev.text.equals(a.text)) {
                rb.setChecked(true);
            }

            radioGroup.addView(rb);
        }
    }
}
