package com.example.t_sample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EarphoneListActivity extends AppCompatActivity {

    LinearLayout earphoneListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earphone_list);

        setTitle("추천 이어폰 리스트");

        earphoneListLayout = findViewById(R.id.earphoneListLayout);

        EarbudData data = JsonLoader.loadJson(this);
        if (data != null) {
            for (Earbud e : data.earbuds) {
                View card = getLayoutInflater().inflate(R.layout.item_earphone, null);

                TextView name = card.findViewById(R.id.earphoneName);
                TextView brand = card.findViewById(R.id.earphoneBrand);
                TextView info = card.findViewById(R.id.earphoneInfo);

                // 이름만 표시
                name.setText(e.name);

                // 브랜드/기능은 숨김
                brand.setVisibility(View.GONE);

                // 링크 안내 문구
                info.setText("네이버 최저가 보기");

                // 네이버 쇼핑 링크 자동 생성
                String keyword = e.name.replace(" ", "+");
                String link = "https://search.shopping.naver.com/search/all?query=" + keyword;

                card.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(intent);
                });

                earphoneListLayout.addView(card);
            }
        }
    }
}
