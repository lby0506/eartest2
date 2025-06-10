package com.example.t_sample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    CardView cardStartTest, cardEarphoneList, cardSearch, cardRecent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // XML 리디자인된 파일 그대로 사용

        // ✅ 시스템 네비게이션 바 항상 보이도록 설정
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        // ✅ ActionBar 숨기기 (앱 제목줄)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // ✅ 카드뷰 연결
        cardStartTest = findViewById(R.id.cardStartTest);
        cardEarphoneList = findViewById(R.id.cardEarphoneList);
        cardSearch = findViewById(R.id.cardSearch);
        cardRecent = findViewById(R.id.cardRecent);

        // ✅ 클릭 이벤트 연결
        cardStartTest.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
            startActivity(intent);
        });

        cardEarphoneList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EarphoneListActivity.class);
            startActivity(intent);
        });

        cardSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        cardRecent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EarphoneListActivity.class);
            intent.putExtra("mode", "recent"); // recent 모드로 구분
            startActivity(intent);
        });

        // ✅ JSON 파일 로딩 테스트 로그 출력
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
