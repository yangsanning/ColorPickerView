package ysn.com.view.colorpicker.slider;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;

import ysn.com.view.colorpicker.R;
import ysn.com.view.colorpicker.preference.ColorPickerPreferenceManager;
import ysn.com.view.colorpicker.utils.ConvertUtils;

/**
 * @Author yangsanning
 * @ClassName BrightnessSlideBar
 * @Description 亮度滑动控件
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
public class BrightnessSlideBar extends BaseSlider {

    public BrightnessSlideBar(Context context) {
        super(context);
    }

    public BrightnessSlideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BrightnessSlideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BrightnessSlideBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BrightnessSlideBar);

        dragDrawable = typedArray.getDrawable(R.styleable.BrightnessSlideBar_bsb_drag);
        dragSize = typedArray.getDimensionPixelSize(R.styleable.ColorPickerView_cpv_drag_size,
                ConvertUtils.dp2px(getContext(), 30));

        typedArray.recycle();
    }

    @Override
    protected void initPaint() {
        float[] hsv = new float[3];
        Color.colorToHSV(getPureColor(), hsv);
        hsv[2] = 0;
        int startColor = Color.HSVToColor(hsv);
        hsv[2] = 1;
        int endColor = Color.HSVToColor(hsv);
        Shader shader = new LinearGradient(0, 0, getWidth(), getHeight(), startColor, endColor, Shader.TileMode.CLAMP);
        colorPaint.setShader(shader);
    }

    @Override
    public void onInflateFinished() {
        int defaultPosition = getMeasuredWidth() - dragImageView.getMeasuredWidth();
        if (getPreferenceName() != null) {
            updateDragX(ColorPickerPreferenceManager.get(getContext())
                    .getBrightnessSliderPosition(getPreferenceName(), defaultPosition));
        } else {
            dragImageView.setX(defaultPosition);
        }
    }

    @Override
    public int assembleColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(getPureColor(), hsv);
        hsv[2] = dragPosition;
        if (colorPickerView != null && colorPickerView.getAlphaSlideBar() != null) {
            int alpha = (int) (colorPickerView.getAlphaSlideBar().getDragPosition() * 255);
            return Color.HSVToColor(alpha, hsv);
        }
        return Color.HSVToColor(hsv);
    }
}
