package com.example.t_sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button btnEarphoneList, btnStartTest, btnSearch;
    EditText editQuery;
    TextView textViewTitle, textViewPrice;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("이어폰 추천 테스트");

        // 버튼 연결
        btnEarphoneList = findViewById(R.id.btnEarphoneList);
        btnStartTest = findViewById(R.id.btnStartTest);
        btnSearch = findViewById(R.id.btnSearch);
        editQuery = findViewById(R.id.editQuery);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewPrice = findViewById(R.id.textViewPrice);
        imageView = findViewById(R.id.imageView);

        // 이어폰 리스트 화면으로 이동
        btnEarphoneList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EarphoneListActivity.class);
            startActivity(intent);
        });

        // 테스트 시작 화면으로 이동
        btnStartTest.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
            startActivity(intent);
        });

        // 검색 버튼 클릭 시
        btnSearch.setOnClickListener(v -> {
            String query = editQuery.getText().toString().trim();
            if (!query.isEmpty()) {
                searchEarphone(query);
            } else {
                Toast.makeText(this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
            }
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

    private void searchEarphone(String query) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<EarphoneItem> call = apiService.searchEarphone(query);

        call.enqueue(new Callback<EarphoneItem>() {
            @Override
            public void onResponse(Call<EarphoneItem> call, Response<EarphoneItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EarphoneItem item = response.body();
                    textViewTitle.setText("상품명: " + item.getTitle());
                    textViewPrice.setText("최저가: " + item.getLprice() + "원");
                    Glide.with(MainActivity.this).load(item.getImage()).into(imageView);
                } else {
                    Toast.makeText(MainActivity.this, "검색 결과 없음", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EarphoneItem> call, Throwable t) {
                Toast.makeText(MainActivity.this, "서버 요청 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.toString());
            }
        });
    }
}
