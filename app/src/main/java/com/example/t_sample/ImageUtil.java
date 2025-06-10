package com.example.t_sample;

import android.content.Context;
import java.io.*;

public class ImageUtil {
    public static void copyImageFromAssets(Context context, String fileName) {
        File outFile = new File(context.getFilesDir(), "earbud_images/" + fileName);
        if (outFile.exists()) return; // 이미 있으면 복사 안 함

        try {
            InputStream is = context.getAssets().open("earbud_images/" + fileName);
            outFile.getParentFile().mkdirs(); // 폴더 없으면 생성
            OutputStream os = new FileOutputStream(outFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
