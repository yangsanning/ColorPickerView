package ysn.com.view.colorpicker.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;

import ysn.com.view.colorpicker.ColorPickerView;
import ysn.com.view.colorpicker.slider.AlphaSlideBar;
import ysn.com.view.colorpicker.slider.BrightnessSlideBar;

/**
 * @Author yangsanning
 * @ClassName ColorPickerPreferenceManager
 * @Description ColorPickerView的资源管理器
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
public class ColorPickerPreferenceManager {

    private static final String KEY_COLOR = "_KEY_COLOR";
    private static final String KEY_COLOR_PICK_DRAG_X = "_KEY_COLOR_PICK_DRAG_X";
    private static final String KEY_COLOR_PICK_DRAG_Y = "_KEY_COLOR_PICK_DRAG_Y";
    private static final String KEY_ALPHA_SLIDER = "_KEY_ALPHA_SLIDER";
    private static final String KEY_BRIGHTNESS_SLIDER = "_KEY_BRIGHTNESS_SLIDER";

    private static ColorPickerPreferenceManager instance;
    private SharedPreferences sharedPreferences;

    public static ColorPickerPreferenceManager get(Context context) {
        if (instance == null) {
            synchronized (ColorPickerPreferenceManager.class) {
                if (instance == null) {
                    instance = new ColorPickerPreferenceManager(context);
                }
            }
        }
        return instance;
    }

    private ColorPickerPreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * 从配置项中还原数据
     */
    public void restoreColorPickerData(ColorPickerView colorPickerView) {
        if (colorPickerView != null && colorPickerView.getPreferenceName() != null) {
            String name = colorPickerView.getPreferenceName();
            colorPickerView.setPureColor(getColor(name, -1));
            Point defaultPoint =
                    new Point((colorPickerView.getMeasuredWidth() / 2), (colorPickerView.getMeasuredHeight() / 2));
            colorPickerView.updateDragCoordinate(
                    getColorPickDragPoint(name, defaultPoint).x, getColorPickDragPoint(name, defaultPoint).y);
            colorPickerView.setDragPoint(
                    getColorPickDragPoint(name, defaultPoint).x, getColorPickDragPoint(name, defaultPoint).y,
                    getColor(name, -1));
        }
    }

    /**
     * 获取颜色
     */
    public int getColor(String name, int defaultColor) {
        return sharedPreferences.getInt(getColorName(name), defaultColor);
    }

    /**
     * 保存颜色
     */
    public ColorPickerPreferenceManager saveColor(String name, int color) {
        sharedPreferences.edit().putInt(getColorName(name), color).apply();
        return instance;
    }

    /**
     * 获取取色控件滑动控件的位置
     */
    public Point getColorPickDragPoint(String name, Point defaultPoint) {
        return new Point(sharedPreferences.getInt(getColorPickDragXName(name), defaultPoint.x),
                sharedPreferences.getInt(getColorPickDragYName(name), defaultPoint.y));
    }

    /**
     * 保存所有数据
     */
    public void saveAllData(ColorPickerView colorPickerView) {
        if (colorPickerView != null && colorPickerView.getPreferenceName() != null) {
            String name = colorPickerView.getPreferenceName();
            saveColor(name, colorPickerView.getColor());
            saveColorPickDragPoint(name, colorPickerView.getDragPoint());

            AlphaSlideBar alphaSlideBar = colorPickerView.getAlphaSlideBar();
            if (alphaSlideBar != null) {
                saveAlphaSliderPosition(name, alphaSlideBar.getDragX());
            }

            BrightnessSlideBar brightnessSlider = colorPickerView.getBrightnessSlider();
            if (brightnessSlider != null) {
                setBrightnessSliderPosition(name, brightnessSlider.getDragX());
            }
        }
    }

    /**
     * 保存取色控件滑动控件的位置
     */
    public ColorPickerPreferenceManager saveColorPickDragPoint(String name, Point dragPoint) {
        sharedPreferences.edit().putInt(getColorPickDragXName(name), dragPoint.x).apply();
        sharedPreferences.edit().putInt(getColorPickDragYName(name), dragPoint.y).apply();
        return instance;
    }

    private String getColorPickDragXName(String name) {
        return name + KEY_COLOR_PICK_DRAG_X;
    }

    private String getColorPickDragYName(String name) {
        return name + KEY_COLOR_PICK_DRAG_Y;
    }

    /**
     * 获取透明滑动控件的位置
     */
    public int getAlphaSliderPosition(String name, int defaultPosition) {
        return sharedPreferences.getInt(getAlphaSliderName(name), defaultPosition);
    }

    /**
     * 保存透明滑动控件的位置
     */
    public ColorPickerPreferenceManager saveAlphaSliderPosition(String name, int x) {
        sharedPreferences.edit().putInt(getAlphaSliderName(name), x).apply();
        return instance;
    }

    private String getAlphaSliderName(String name) {
        return name + KEY_ALPHA_SLIDER;
    }

    /**
     * 获取亮度滑动控件的位置
     */
    public int getBrightnessSliderPosition(String name, int defaultPosition) {
        return sharedPreferences.getInt(getBrightnessSliderName(name), defaultPosition);
    }

    /**
     * 保存亮度滑动控件的位置
     */
    public ColorPickerPreferenceManager setBrightnessSliderPosition(String name, int position) {
        sharedPreferences.edit().putInt(getBrightnessSliderName(name), position).apply();
        return instance;
    }

    private String getBrightnessSliderName(String name) {
        return name + KEY_BRIGHTNESS_SLIDER;
    }

    /**
     * 删除已保存的颜色
     */
    public ColorPickerPreferenceManager clearColor(String name) {
        sharedPreferences.edit().remove(getColorName(name)).apply();
        return instance;
    }

    /**
     * 清除取色控件滑动控件的坐标数据
     */
    public ColorPickerPreferenceManager clearColorPickDragData(String name) {
        sharedPreferences.edit().remove(getColorPickDragXName(name)).apply();
        sharedPreferences.edit().remove(getColorPickDragYName(name)).apply();
        return instance;
    }

    /**
     * 清除透明滑动控件的坐标数据
     */
    public ColorPickerPreferenceManager clearAlphaSliderData(String name) {
        sharedPreferences.edit().remove(getAlphaSliderName(name)).apply();
        return instance;
    }

    /**
     * 清除亮度滑动控件的坐标数据
     */
    public ColorPickerPreferenceManager clearBrightnessSliderData(String name) {
        sharedPreferences.edit().remove(getBrightnessSliderName(name)).apply();
        return instance;
    }

    /**
     * 清除所有数据
     */
    public ColorPickerPreferenceManager clearAllData() {
        sharedPreferences.edit().clear().apply();
        return instance;
    }

    protected String getColorName(String name) {
        return name + KEY_COLOR;
    }
}
