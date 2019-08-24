package ysn.com.view.colorpicker.slider;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
 * @ClassName AlphaSlideBar
 * @Description 透明滑动条
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
public class AlphaSlideBar extends BaseSliderBar {

    private Bitmap backgroundBitmap;
    private AlphaTileDrawable drawable = new AlphaTileDrawable();

    public AlphaSlideBar(Context context) {
        super(context);
    }

    public AlphaSlideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlphaSlideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AlphaSlideBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AlphaSlideBar);

        dragDrawable = typedArray.getDrawable(R.styleable.AlphaSlideBar_asb_drag);
        dragSize = typedArray.getDimensionPixelSize(R.styleable.AlphaSlideBar_asb_drag_size,
                ConvertUtils.dp2px(getContext(), 30));

        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        if (width > 0 && height > 0) {
            backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas backgroundCanvas = new Canvas(backgroundBitmap);
            drawable.setBounds(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());
            drawable.draw(backgroundCanvas);
        }
    }

    @Override
    public void initPaint() {
        float[] hsv = new float[3];
        Color.colorToHSV(getPureColor(), hsv);
        int startColor = Color.HSVToColor(0, hsv);
        int endColor = Color.HSVToColor(255, hsv);
        Shader shader = new LinearGradient(0, 0, getMeasuredWidth(), getMeasuredHeight(),
                        startColor, endColor, Shader.TileMode.CLAMP);
        colorPaint.setShader(shader);
    }

    @Override
    public void onInflateFinished() {
        int defaultPosition = getMeasuredWidth() - dragImageView.getMeasuredWidth();
        if (getPreferenceName() != null) {
            updateDragX(ColorPickerPreferenceManager.get(getContext())
                            .getAlphaSliderPosition(getPreferenceName(), defaultPosition));
        } else {
            dragImageView.setX(defaultPosition);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        super.onDraw(canvas);
    }

    @Override
    public int assembleColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(getPureColor(), hsv);
        int alpha = (int) (dragPosition * 255);
        return Color.HSVToColor(alpha, hsv);
    }
}
