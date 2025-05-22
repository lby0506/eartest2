package com.example.t_sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;  // 로그를 찍기 위해 필요!
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnEarphoneList, btnStartTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();  // 액션바 제거
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("이어폰 추천 테스트");

        // 버튼 연결
        btnEarphoneList = findViewById(R.id.btnEarphoneList);
        btnStartTest = findViewById(R.id.btnStartTest);

        // 이어폰 리스트 화면으로 이동
        btnEarphoneList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EarphoneListActivity.class);
                startActivity(intent);
            }
        });

        // 질문 화면으로 이동
        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
                startActivity(intent);
            }
        });

        // JSON 파일 읽고 로그 찍기 (테스트용)
        EarbudData data = JsonLoader.loadJson(this);

        if (data != null) {
            for (Earbud e : data.earbuds) {
                Log.d("이어폰", e.name + " / 브랜드: " + e.brand);
            }

            for (Question q : data.Q) {
                Log.d("질문", q.question);
            }
        }
    }
}
