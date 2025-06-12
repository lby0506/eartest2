package com.example.t_sample;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private LinearLayout resultLayout;
    private Button btnGoHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultLayout = findViewById(R.id.resultLayout);
        btnGoHome = findViewById(R.id.btnGoHome); // 버튼 초기화

        List<String> selectedFilters = getIntent().getStringArrayListExtra("filters");
        List<String> selectedAnswersText = getIntent().getStringArrayListExtra("selectedAnswersText");

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

            // ✅ 조건 설명 텍스트 (줄바꿈 + 문장 스타일)
            TextView header = new TextView(this);
            StringBuilder conditionText = new StringBuilder();
            for (String answer : selectedAnswersText) {
                conditionText.append("• ").append(answer).append("\n");
            }
            conditionText.append("\n👉 이런 당신에게 어울리는 이어폰을 추천합니다!");

            header.setText(conditionText.toString());
            header.setTextSize(16);
            header.setPadding(32, 32, 32, 32);
            header.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            header.setBackgroundResource(R.drawable.rounded_card_bg);
            header.setLineSpacing(8f, 1.2f);
            header.setTypeface(null, Typeface.BOLD);
            resultLayout.addView(header);

            // 홈으로 돌아가는 버튼 클릭 리스너
            btnGoHome.setOnClickListener(v -> {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // 현재 스택 비우고 새 태스크 시작
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            });

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
                    if (matchCount >= selectedFilters.size() * 0.6) {
                        similarResults.add(e);
                    }
                }
            }

            if (!hasMatch) {
                TextView tv = new TextView(this);
                tv.setText("🔍 정확히 일치하는 이어폰이 없습니다.\n아래는 유사한 추천입니다.");
                tv.setTextSize(16);
                tv.setPadding(24, 24, 24, 24);
                tv.setTypeface(null, Typeface.BOLD);
                resultLayout.addView(tv);


                for (Earbud e : similarResults) {
                    addResultCard(e, false);
                }
            }

        }
    }

    private void addResultCard(Earbud e, boolean isExactMatch) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(24, 24, 24, 24);
        card.setBackgroundResource(R.drawable.rounded_card_bg);
        card.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 24, 0, 0);
        card.setLayoutParams(cardParams);


        ImageView img = new ImageView(this);
        int size = (int) (64 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(size, size);
        img.setLayoutParams(imgParams);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);


        ImageUtil.copyImageFromAssets(this, e.image);
        File imageFile = new File(getFilesDir(), "earbud_images/" + e.image);
        if (imageFile.exists()) {
            Glide.with(this).load(imageFile).into(img);
        } else {
            Glide.with(this).load(R.drawable.no_image).into(img);
        }

        String price = priceLabel(e.features);
        String fullText = "🎧 " + e.name + "\n브랜드: " + e.brand + "\n💸 예상 가격대: " + price;

        TextView info = new TextView(this);
        info.setText(fullText);
        info.setTextSize(15);
        info.setPadding(24, 0, 0, 0);
        info.setTextColor(ContextCompat.getColor(this, android.R.color.black));

        if (!isExactMatch) {
            card.setAlpha(0.6f);
        }

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