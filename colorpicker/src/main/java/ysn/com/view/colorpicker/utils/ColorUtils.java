package ysn.com.view.colorpicker.utils;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import java.util.Locale;

/**
 * @Author yangsanning
 * @ClassName ColorEnvelope
 * @Description 颜色工具
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
public class ColorUtils {

    /**
     * 转十六进制颜色
     */
    public static String getHexCode(int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return String.format(Locale.getDefault(), "%02X%02X%02X%02X", a, r, g, b);
    }

    /**
     * 转RGB颜色
     */
    public static int[] getColorARGB(int color) {
        int[] argb = new int[4];
        argb[0] = Color.alpha(color);
        argb[1] = Color.red(color);
        argb[2] = Color.green(color);
        argb[3] = Color.blue(color);
        return argb;
    }

    /**
     * 根据 Bitmap 和坐标获取颜色值
     */
    public static int getColorFromBitmap(View parent, ImageView imageView, float x, float y) {
        Matrix invertMatrix = new Matrix();
        imageView.getImageMatrix().invert(invertMatrix);

        float[] mappedPoints = new float[]{x, y};
        invertMatrix.mapPoints(mappedPoints);

        if (imageView.getDrawable() != null
                && imageView.getDrawable() instanceof BitmapDrawable
                && mappedPoints[0] >= 0
                && mappedPoints[1] >= 0
                && mappedPoints[0] < imageView.getDrawable().getIntrinsicWidth()
                && mappedPoints[1] < imageView.getDrawable().getIntrinsicHeight()) {

            parent.invalidate();

            Rect rect = imageView.getDrawable().getBounds();
            float scaleX = mappedPoints[0] / rect.width();
            int x1 = (int) (scaleX * ((BitmapDrawable) imageView.getDrawable()).getBitmap().getWidth());
            float scaleY = mappedPoints[1] / rect.height();
            int y1 = (int) (scaleY * ((BitmapDrawable) imageView.getDrawable()).getBitmap().getHeight());
            return ((BitmapDrawable) imageView.getDrawable()).getBitmap().getPixel(x1, y1);
        }
        return 0;
    }
}
