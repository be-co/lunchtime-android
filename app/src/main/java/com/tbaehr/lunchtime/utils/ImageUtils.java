package com.tbaehr.lunchtime.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.tbaehr.lunchtime.LunchtimeApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by timo.baehr@gmail.com on 09.02.17.
 */
public class ImageUtils {

    public static List<Drawable> downloadDrawables(@NonNull String[] imageUrls) throws IOException {
        List<Drawable> drawablesResult = new ArrayList<>();

        if (imageUrls.length > 0) {
            for (String url : imageUrls) {
                File image = createImageFile(url);
                Bitmap bitmap;
                if (!image.exists()) {
                    bitmap = loadBitmapFromServerAndSaveToMemory(url, image);
                } else {
                    bitmap = loadBitmapFromMemory(image);
                }
                drawablesResult.add(new BitmapDrawable(LunchtimeApplication.getContext().getResources(), bitmap));
            }
        }

        return drawablesResult;
    }

    private static Bitmap loadBitmapFromServerAndSaveToMemory(String url, File image) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("connection", "close");
        connection.connect();
        InputStream input = connection.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(image));
        return bitmap;
    }

    private static Bitmap loadBitmapFromMemory(File image) throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(image);
        return BitmapFactory.decodeStream(inputStream);
    }

    private static File createImageFile(@NonNull String url) {
        Context context = LunchtimeApplication.getContext();
        File imageDirectory = context.getDir("images", Context.MODE_PRIVATE);
        imageDirectory.mkdirs();
        String fileName = url.replace("http://", "").replace("/","").replace(".gif", "");
        return new File(imageDirectory, fileName);
    }
}
