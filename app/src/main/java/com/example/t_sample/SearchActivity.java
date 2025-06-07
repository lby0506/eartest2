package com.example.t_sample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    EditText editQuery;
    Button btnSearch;
    TextView textViewTitle, textViewPrice;
    ImageView imageView;
    LinearLayout resultCard; // ✅ 카드 전체 레이아웃 참조 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_earphone);
        setTitle("이어폰 검색");

        // 뷰 연결
        editQuery = findViewById(R.id.editQuery);
        btnSearch = findViewById(R.id.btnSearch);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewPrice = findViewById(R.id.textViewPrice);
        imageView = findViewById(R.id.imageView);
        resultCard = findViewById(R.id.resultCard); // ✅ 레이아웃 연결

        // 검색 버튼 클릭 시
        btnSearch.setOnClickListener(v -> {
            String query = editQuery.getText().toString().trim();
            if (!query.isEmpty()) {
                searchEarphone(query);
            } else {
                Toast.makeText(this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchEarphone(String query) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<EarphoneItem> call = apiService.searchEarphone(query);

        call.enqueue(new Callback<EarphoneItem>() {
            @Override
            public void onResponse(Call<EarphoneItem> call, Response<EarphoneItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EarphoneItem item = response.body();

                    // 텍스트 적용
                    textViewTitle.setText("상품명: " + item.getTitle());
                    textViewPrice.setText("최저가: " + item.getLprice() + "원");

                    // 이미지 적용
                    Glide.with(SearchActivity.this)
                            .load(item.getImage())
                            .into(imageView);

                    // ✅ 카드 전체 보여주기
                    resultCard.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(SearchActivity.this, "검색 결과 없음", Toast.LENGTH_SHORT).show();
                    resultCard.setVisibility(View.GONE); // 실패 시 숨기기
                }
            }

            @Override
            public void onFailure(Call<EarphoneItem> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "서버 요청 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.toString());
                resultCard.setVisibility(View.GONE); // 실패 시 숨기기
            }
        });
    }
}
