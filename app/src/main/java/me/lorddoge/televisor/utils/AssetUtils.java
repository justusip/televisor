package me.lorddoge.televisor.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

public class AssetUtils {

    public static boolean assetExists(Context context, String folder, String file) {
        boolean exists = false;
        try {
            exists = Arrays.asList(context.getResources().getAssets().list(folder))
                    .contains(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exists;
    }

    public static Drawable getDrawableFromAssets(Context context, String url) {
        InputStream inputStream = null;
        Drawable drawable = null;
        try {
            inputStream = context.getAssets().open(url);
            drawable = Drawable.createFromStream(inputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return drawable;
    }

    public static Bitmap getBitmapFromAsset(Context context, String url) {
        InputStream inputStream = null;
        Bitmap bitmap = null;
        try {
            inputStream = context.getAssets().open(url);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    public static String getTextFromAsset(Context context, String url) {
        InputStream inputStream = null;
        Scanner scanner = null;
        String text = null;
        try {
            inputStream = context.getAssets().open(url);
            scanner = new Scanner(inputStream).useDelimiter("\\A");
            text = scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (scanner != null) {
                scanner.close();
            }
        }
        return text;
    }
}
