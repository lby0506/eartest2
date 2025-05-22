package com.example.t_sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private LinearLayout resultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultLayout = findViewById(R.id.resultLayout); // ScrollView 안의 LinearLayout

        List<String> selectedFilters = getIntent().getStringArrayListExtra("filters");
        EarbudData data = JsonLoader.loadJson(this);

        if (data != null) {
            boolean hasMatch = false;

            for (Earbud e : data.earbuds) {
                boolean match = true;
                for (String filter : selectedFilters) {
                    if (!e.features.contains(filter)) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    hasMatch = true;

                    TextView tv = new TextView(this);
                    tv.setText("🎧 " + e.name);
                    tv.setTextSize(18);
                    tv.setPadding(16, 16, 16, 16);
                    tv.setTextColor(getResources().getColor(android.R.color.black));

                    // 네이버 링크 연결
                    String keyword = e.name.replace(" ", "+");
                    String url = "https://search.shopping.naver.com/search/all?query=" + keyword;

                    tv.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    });

                    resultLayout.addView(tv);
                }
            }

            if (!hasMatch) {
                TextView tv = new TextView(this);
                tv.setText("조건에 맞는 이어폰이 없습니다.");
                tv.setTextSize(16);
                tv.setPadding(16, 16, 16, 16);
                resultLayout.addView(tv);
            }
        }
    }
}
