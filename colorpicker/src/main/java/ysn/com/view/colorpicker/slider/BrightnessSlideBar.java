package ysn.com.view.colorpicker.slider;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RectF;
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
public class BrightnessSlideBar extends BaseSliderBar {

    /**
     * 中间seekBar的高度
     */
    protected int seekBarHeight;
    /**
     * 中间seekBar的圆角
     */
    protected int seekBarRound;
    /**
     * 中间seekBar的左右边距
     */
    protected int seekBarMargin;
    protected RectF seekBarRectF = new RectF();

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
        dragSize = typedArray.getDimensionPixelSize(R.styleable.BrightnessSlideBar_bsb_drag_size,
                ConvertUtils.dp2px(getContext(), 30));
        seekBarHeight = typedArray.getDimensionPixelSize(R.styleable.BrightnessSlideBar_bsb_seek_bar_height,
                ConvertUtils.dp2px(getContext(), 6));
        seekBarRound = typedArray.getDimensionPixelSize(R.styleable.BrightnessSlideBar_bsb_seek_bar_round,
                ConvertUtils.dp2px(getContext(), 30));
        seekBarMargin = dragSize / 2;

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
    protected void onSizeChanged(int viewWidth, int viewHeight, int oldw, int oldh) {
        super.onSizeChanged(viewWidth, viewHeight, oldw, oldh);
        int height = (viewHeight - seekBarHeight) / 2;
        seekBarRectF.left = seekBarMargin;
        seekBarRectF.right = viewWidth - seekBarMargin;
        seekBarRectF.top = height;
        seekBarRectF.bottom = height + seekBarHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(seekBarRectF, seekBarRound, seekBarRound, colorPaint);
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
