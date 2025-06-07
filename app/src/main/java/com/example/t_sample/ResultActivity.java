package com.example.t_sample;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private LinearLayout resultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultLayout = findViewById(R.id.resultLayout);

        List<String> selectedFilters = getIntent().getStringArrayListExtra("filters");
        EarbudData data = JsonLoader.loadJson(this);

        if (data != null) {
            List<String> codecFilters = Arrays.asList(
                    "AAC", "SSC", "LDAC", "APTX", "APTX-adaptive", "APTX-Loseless", "APTX-Adaptive"
            );
            List<String> selectedCodecFilters = new ArrayList<>();
            List<String> selectedOtherFilters = new ArrayList<>();

            for (String filter : selectedFilters) {
                if (codecFilters.contains(filter)) selectedCodecFilters.add(filter);
                else selectedOtherFilters.add(filter);
            }

            // 선택한 조건 출력
            TextView header = new TextView(this);
            header.setText("🧠 선택한 조건: " + String.join(", ", selectedFilters));
            header.setTextSize(16);
            header.setPadding(16, 16, 16, 32);
            header.setTypeface(null, Typeface.BOLD);
            resultLayout.addView(header);

            boolean hasMatch = false;
            List<Earbud> similarResults = new ArrayList<>();

            for (Earbud e : data.earbuds) {
                List<String> features = e.features;

                boolean match = true;
                for (String filter : selectedOtherFilters) {
                    if (!features.contains(filter)) {
                        match = false;
                        break;
                    }
                }

                if (match && !selectedCodecFilters.isEmpty()) {
                    boolean hasCodec = false;
                    for (String codec : selectedCodecFilters) {
                        if (features.contains(codec)) {
                            hasCodec = true;
                            break;
                        }
                    }
                    if (!hasCodec) match = false;
                }

                if (match) {
                    hasMatch = true;
                    addResultCard(e, true);
                } else {
                    int matchCount = 0;
                    for (String f : selectedFilters) {
                        if (features.contains(f)) matchCount++;
                    }
                    if (matchCount >= selectedFilters.size() / 2) {
                        similarResults.add(e);
                    }
                }
            }

            if (!hasMatch) {
                TextView tv = new TextView(this);
                tv.setText("🔍 정확히 일치하는 이어폰이 없습니다.\n아래는 유사한 추천입니다.");
                tv.setTextSize(16);
                tv.setPadding(16, 16, 16, 16);
                tv.setTypeface(null, Typeface.BOLD);
                resultLayout.addView(tv);
            }

            for (Earbud e : similarResults) {
                addResultCard(e, false);
            }
        }
    }

    private void addResultCard(Earbud e, boolean isExactMatch) {
        // 카드 전체 레이아웃
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(32, 32, 32, 32);
        card.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
        card.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 32);
        card.setLayoutParams(cardParams);

        // 썸네일 이미지
        ImageView img = new ImageView(this);
        int size = (int) (72 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(size, size);
        img.setLayoutParams(imgParams);

        // 임시 이미지 (나중에 이미지 URL 넣으면 여기 수정 가능)
        Glide.with(this)
                .load("https://logo.clearbit.com/naver.com")
                .into(img);

        // 이어폰 정보 텍스트
        TextView info = new TextView(this);
        info.setText("🎧 " + e.name + "\n브랜드: " + e.brand + "\n예상 가격대: " + priceLabel(e.features));
        info.setTextSize(14);
        info.setPadding(32, 0, 0, 0);
        info.setTextColor(getResources().getColor(android.R.color.black));

        if (!isExactMatch) {
            card.setAlpha(0.6f);
        }

        // 클릭 시 네이버 쇼핑 검색
        String keyword = e.name.replace(" ", "+");
        String url = "https://search.shopping.naver.com/search/all?query=" + keyword;
        info.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        card.addView(img);
        card.addView(info);
        resultLayout.addView(card);
    }

    private String priceLabel(List<String> features) {
        if (features.contains("1")) return "~5만원";
        if (features.contains("5")) return "5~10만";
        if (features.contains("10")) return "10~20만";
        if (features.contains("20")) return "20~30만";
        if (features.contains("30")) return "30~50만";
        if (features.contains("50")) return "50만 이상";
        return "?";
    }
}
