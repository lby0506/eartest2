package com.example.t_sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
            int answerId = View.generateViewId();  // 고유 ID 생성
            rb.setId(answerId);
            rb.setText(q.answers.get(i).text);
            rb.setTag(q.answers.get(i));  // Answer 객체를 tag로 저장
            radioGroup.addView(rb);
        }
    }
}
