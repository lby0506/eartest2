package com.example.t_sample;

import android.content.Context;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonLoader {

    public static EarbudData loadJson(Context context) {
        try {
            InputStream is = context.getAssets().open("earphones.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // ✅ UTF-8 인코딩
            String json = new String(buffer, StandardCharsets.UTF_8);

            // ✅ GSON으로 파싱
            Gson gson = new Gson();
            return gson.fromJson(json, EarbudData.class);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
