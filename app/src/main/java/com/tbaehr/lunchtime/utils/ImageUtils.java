package com.tbaehr.lunchtime.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.tbaehr.lunchtime.LunchtimeApplication;
import com.tbaehr.lunchtime.model.Restaurant;

import java.io.File;
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

    public static List<Drawable> downloadDrawables(@NonNull Restaurant restaurant) throws IOException {
        String restaurantId = restaurant.getId();
        String[] photoUrls = restaurant.getPhotoUrls();

        // path to folder for selected restaurant
        Context context = LunchtimeApplication.getContext();
        String imageFolderPath = context.getFilesDir().getPath() + restaurantId + "/";
        File imageDirectory = new File(imageFolderPath);
        imageDirectory.mkdirs();

        // result list
        List<Drawable> drawables = new ArrayList<>();

        // try to load images from cache, otherwise download
        if (photoUrls != null) {
            for (String url : photoUrls) {
                String imagePath = imageFolderPath + stripFileName(url);
                File image = new File(imagePath);
                Bitmap bitmap;
                if (!image.exists()) {
                    URLConnection connection = new URL(url).openConnection();
                    connection.setRequestProperty("connection", "close");
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(input);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(image));
                } else {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap = BitmapFactory.decodeFile(imagePath, options);
                }
                drawables.add(new BitmapDrawable(LunchtimeApplication.getContext().getResources(), bitmap));
            }
        }

        return drawables;
    }

    private static String stripFileName(@NonNull String url) {
        String[] parts = url.split("/");
        String imageName = parts[parts.length-1];
        imageName = imageName.replace(".gif", "");
        imageName += ".jpg";
        return imageName;
    }
}
