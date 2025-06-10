package com.example.t_sample;

import java.util.List;

public class Earbud {
    public String id;
    public String name;
    public String brand;
    public String image; // ✅ 이미지 경로 추가
    public List<String> features;

    // ✅ 즐겨찾기 상태
    private boolean isFavorite = false;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // ✅ 이미지 경로 getter
    public String getImage() {
        return image;
    }
}
