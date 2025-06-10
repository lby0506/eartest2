package com.example.t_sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EarphoneListActivity extends AppCompatActivity {

    LinearLayout earphoneListLayout;
    private boolean showFavoritesOnly = false;
    private EarbudData fullData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earphone_list);

        setTitle("추천 이어폰 리스트");

        earphoneListLayout = findViewById(R.id.earphoneListLayout);
        Button btnToggleFavorites = findViewById(R.id.btnToggleFavorites);

        fullData = JsonLoader.loadJson(this);
        if (fullData == null) return;

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");

        if (mode != null && mode.equals("recent")) {
            List<String> recentIds = loadRecentlyViewed();
            List<Earbud> recentList = new ArrayList<>();
            for (Earbud e : fullData.earbuds) {
                if (recentIds.contains(e.id)) {
                    recentList.add(e);
                }
            }
            renderEarbuds(recentList);
        } else {
            renderEarbuds(fullData.earbuds);
        }

        btnToggleFavorites.setOnClickListener(v -> {
            showFavoritesOnly = !showFavoritesOnly;

            List<Earbud> filtered;
            if (showFavoritesOnly) {
                btnToggleFavorites.setText("전체 보기");
                filtered = new ArrayList<>();
                for (Earbud e : fullData.earbuds) {
                    if (e.isFavorite()) filtered.add(e);
                }
            } else {
                btnToggleFavorites.setText("즐겨찾기만 보기");
                filtered = fullData.earbuds;
            }

            renderEarbuds(filtered);
        });
    }

    private void renderEarbuds(List<Earbud> earbuds) {
        earphoneListLayout.removeAllViews();
        List<String> savedFavorites = loadFavorites();

        for (Earbud e : earbuds) {
            if (savedFavorites.contains(e.id)) {
                e.setFavorite(true);
            }

            View card = getLayoutInflater().inflate(R.layout.item_earphone, null);

            TextView name = card.findViewById(R.id.earphoneName);
            TextView brand = card.findViewById(R.id.earphoneBrand);
            TextView info = card.findViewById(R.id.earphoneInfo);
            ImageView imageView = card.findViewById(R.id.earphoneImage);
            ImageButton btnFavorite = card.findViewById(R.id.btnFavorite);

            name.setText(e.name);
            brand.setVisibility(View.GONE);
            info.setText("네이버 최저가 보기");

            // ✅ 이미지 assets → 내부 저장소로 복사
            ImageUtil.copyImageFromAssets(this, e.image);

            // ✅ 내부 저장소 경로에서 Glide로 불러오기
            File imgFile = new File(getFilesDir(), "earbud_images/" + e.image);
            if (imgFile.exists()) {
                Glide.with(this).load(imgFile).into(imageView);
            } else {
                Glide.with(this).load(R.drawable.no_image).into(imageView);
            }

            String keyword = e.name.replace(" ", "+");
            String link = "https://search.shopping.naver.com/search/all?query=" + keyword;

            card.setOnClickListener(v -> {
                saveRecentlyViewed(e.id);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            });

            if (e.isFavorite()) {
                btnFavorite.setImageResource(R.drawable.ic_heart_filled);
            } else {
                btnFavorite.setImageResource(R.drawable.ic_heart_outline);
            }

            btnFavorite.setOnClickListener(v -> {
                boolean newState = !e.isFavorite();
                e.setFavorite(newState);

                if (newState) {
                    btnFavorite.setImageResource(R.drawable.ic_heart_filled);
                    Toast.makeText(this, "즐겨찾기에 추가됨", Toast.LENGTH_SHORT).show();
                } else {
                    btnFavorite.setImageResource(R.drawable.ic_heart_outline);
                    Toast.makeText(this, "즐겨찾기에서 제거됨", Toast.LENGTH_SHORT).show();
                }

                List<String> favIds = new ArrayList<>();
                for (Earbud eb : fullData.earbuds) {
                    if (eb.isFavorite()) {
                        favIds.add(eb.id);
                    }
                }
                saveFavorites(favIds);

                if (showFavoritesOnly) {
                    List<Earbud> filtered = new ArrayList<>();
                    for (Earbud eb : fullData.earbuds) {
                        if (eb.isFavorite()) filtered.add(eb);
                    }
                    renderEarbuds(filtered);
                }
            });

            earphoneListLayout.addView(card);
        }
    }

    private void saveFavorites(List<String> favoriteIds) {
        SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray jsonArray = new JSONArray(favoriteIds);
        editor.putString("favorite_ids", jsonArray.toString());
        editor.apply();
    }

    private List<String> loadFavorites() {
        SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
        String json = prefs.getString("favorite_ids", "[]");

        List<String> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void saveRecentlyViewed(String id) {
        SharedPreferences prefs = getSharedPreferences("recent", MODE_PRIVATE);
        String json = prefs.getString("recent_ids", "[]");

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                if (array.getString(i).equals(id)) {
                    array.remove(i);
                    break;
                }
            }

            JSONArray newArray = new JSONArray();
            newArray.put(id);
            for (int i = 0; i < array.length(); i++) {
                newArray.put(array.get(i));
            }

            while (newArray.length() > 10) {
                newArray.remove(newArray.length() - 1);
            }

            prefs.edit().putString("recent_ids", newArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<String> loadRecentlyViewed() {
        SharedPreferences prefs = getSharedPreferences("recent", MODE_PRIVATE);
        String json = prefs.getString("recent_ids", "[]");

        List<String> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
