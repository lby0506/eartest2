package com.example.t_sample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnEarphoneList, btnStartTest, btnSearchActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ 시스템 네비게이션 바 항상 보이도록 설정
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        // ✅ ActionBar 숨기기 (앱 제목줄)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setTitle("이어폰 추천 테스트");

        // 버튼 연결
        btnEarphoneList = findViewById(R.id.btnEarphoneList);
        btnStartTest = findViewById(R.id.btnStartTest);
        btnSearchActivity = findViewById(R.id.btnSearchActivity);

        btnEarphoneList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EarphoneListActivity.class);
            startActivity(intent);
        });

        btnStartTest.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
            startActivity(intent);
        });

        btnSearchActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        // JSON 테스트 로그 출력
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
