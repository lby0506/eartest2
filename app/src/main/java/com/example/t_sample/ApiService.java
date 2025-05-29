package com.example.t_sample;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("search/{query}")
    Call<EarphoneItem> searchEarphone(@Path("query") String query);
}
