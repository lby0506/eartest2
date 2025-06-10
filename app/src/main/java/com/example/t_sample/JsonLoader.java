package com.example.t_sample;

import android.content.Context;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonLoader {

    /** 기존 loadJson 그대로 놔두고… **/
    public static EarbudData loadJson(Context context) {
        try {
            InputStream is = context.getAssets().open("earphones.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer); is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            return new Gson().fromJson(json, EarbudData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** 새로 추가: 동일 파일에서 Q 배열만 뽑아오기 **/
    public static List<Question> loadQuestions(Context context) {
        try {
            InputStream is = context.getAssets().open("earphones.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer); is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(json);
            JSONArray qArr = root.getJSONArray("Q");

            List<Question> list = new ArrayList<>();
            for (int i = 0; i < qArr.length(); i++) {
                JSONObject o = qArr.getJSONObject(i);
                Question q = new Question();
                q.id       = o.getString("id");
                q.question = o.getString("question");

                JSONArray answers = o.getJSONArray("answers");
                q.answers = new ArrayList<>();
                for (int j = 0; j < answers.length(); j++) {
                    JSONObject a = answers.getJSONObject(j);
                    Answer ans = new Answer();
                    ans.text = a.getString("text");
                    JSONArray farr = a.getJSONArray("filters");
                    ans.filters = new ArrayList<>();
                    for (int k = 0; k < farr.length(); k++) {
                        ans.filters.add(farr.getString(k));
                    }
                    q.answers.add(ans);
                }
                list.add(q);
            }
            return list;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
