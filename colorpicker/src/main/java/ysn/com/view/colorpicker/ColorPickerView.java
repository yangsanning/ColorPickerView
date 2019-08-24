package ysn.com.view.colorpicker;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import ysn.com.view.colorpicker.annotation.DragMode;
import ysn.com.view.colorpicker.annotation.TagMode;
import ysn.com.view.colorpicker.bean.ColorEnvelope;
import ysn.com.view.colorpicker.listener.OnColorEnvelopeListener;
import ysn.com.view.colorpicker.listener.OnColorSelectListener;
import ysn.com.view.colorpicker.listener.BaseColorListener;
import ysn.com.view.colorpicker.preference.ColorPickerPreferenceManager;
import ysn.com.view.colorpicker.slider.AlphaSlideBar;
import ysn.com.view.colorpicker.slider.BrightnessSlideBar;
import ysn.com.view.colorpicker.tag.BaseTagView;
import ysn.com.view.colorpicker.utils.ColorUtils;
import ysn.com.view.colorpicker.utils.ConvertUtils;
import ysn.com.view.colorpicker.utils.PointMapper;

/**
 * @Author yangsanning
 * @ClassName ColorPickerView
 * @Description 取色控件: 从任何图像获取hsv颜色、argb值、十六进制颜色
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
public class ColorPickerView extends FrameLayout implements LifecycleObserver {

    /**
     * 取色板图片
     */
    private Drawable swatchesDrawable;
    /**
     * 拖动控件图片
     */
    private Drawable dragDrawable;
    private int dragSize;

    @DragMode
    private int dragMode = DragMode.ALWAYS;
    private float dragAlpha;
    private float tagAlpha;
    private String preferenceName;

    private int dragPureColor;
    private int dragColor;
    private Point dragPoint;
    private boolean isVisibleTag = false;

    public BaseColorListener onColorSelectListener;

    /**
     * 取色板(被取色的 view)
     */
    private ImageView swatchesImageView;
    /**
     * 拖动控件(滑动的 view)
     */
    private ImageView dragImageView;

    /**
     * 标注 View
     */
    private BaseTagView tagView;
    private AlphaSlideBar alphaSlideBar;
    private BrightnessSlideBar brightnessSlider;

    public ColorPickerView(Context context) {
        this(context, null);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
        initView();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPickerView);

        swatchesDrawable = typedArray.getDrawable(R.styleable.ColorPickerView_cpv_swatches);
        dragDrawable = typedArray.getDrawable(R.styleable.ColorPickerView_cpv_drag);
        dragSize = typedArray.getDimensionPixelSize(R.styleable.ColorPickerView_cpv_drag_size,
                ConvertUtils.dp2px(getContext(), 30));
        dragAlpha = typedArray.getFloat(R.styleable.ColorPickerView_cpv_drag_alpha, 1.0f);
        tagAlpha = typedArray.getFloat(R.styleable.ColorPickerView_cpv_tag_alpha, 1.0f);
        dragMode = typedArray.getInteger(R.styleable.ColorPickerView_cpv_drag_mode, DragMode.ALWAYS);

        if (typedArray.hasValue(R.styleable.ColorPickerView_cpv_preference_name)) {
            this.preferenceName = typedArray.getString(R.styleable.ColorPickerView_cpv_preference_name);
        }

        typedArray.recycle();
    }

    private void initView() {
        setPadding(0, 0, 0, 0);
        initPaletteView();
        initDragView();
        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        if (getPreferenceName() != null) {
                            ColorPickerPreferenceManager.get(getContext())
                                    .restoreColorPickerData(ColorPickerView.this);
                        } else {
                            selectCenter();
                        }
                    }
                });
    }

    /**
     * 初始化调色板控件
     */
    private void initPaletteView() {
        swatchesImageView = new ImageView(getContext());
        if (swatchesDrawable != null) {
            swatchesImageView.setImageDrawable(swatchesDrawable);
        } else {
            swatchesImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_default_palette));
        }

        FrameLayout.LayoutParams paletteParam = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        paletteParam.gravity = Gravity.CENTER;
        addView(swatchesImageView, paletteParam);
    }

    /**
     * 初始化拖动控件
     */
    private void initDragView() {
        dragImageView = new ImageView(getContext());
        if (dragDrawable != null) {
            dragImageView.setImageDrawable(dragDrawable);
        } else {
            dragImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_default_drag));
        }

        LayoutParams dragParam = new LayoutParams(dragSize, dragSize);
        dragParam.gravity = Gravity.CENTER;
        addView(dragImageView, dragParam);
        dragImageView.setAlpha(dragAlpha);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                if (getTagView() != null) {
                    getTagView().handleOnTouchEvent(event);
                }
                dragImageView.setPressed(true);
                return onTouchReceived(event);
            default:
                dragImageView.setPressed(false);
                return false;
        }
    }

    /**
     * 更新操作
     */
    private boolean onTouchReceived(MotionEvent event) {
        Point snapPoint = PointMapper.getColorPoint(this, new Point((int) event.getX(), (int) event.getY()));
        int pixelColor = getColorFromBitmap(snapPoint.x, snapPoint.y);

        dragPureColor = pixelColor;
        dragColor = pixelColor;
        dragPoint = PointMapper.getColorPoint(this, new Point(snapPoint.x, snapPoint.y));
        updateDragCoordinate(snapPoint.x, snapPoint.y);
        updateTagView(dragPoint);

        switch (dragMode) {
            case DragMode.LAST:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    callColorListener(getColor(), true);
                    updateSlideBars();
                }
                break;
            default:
                callColorListener(getColor(), true);
                updateSlideBars();
                break;
        }
        return true;
    }

    /**
     * 根据 Bitmap 和坐标获取颜色值
     */
    public int getColorFromBitmap(float x, float y) {
        return ColorUtils.getColorFromBitmap(this, swatchesImageView, x, y);
    }

    /**
     * 设置拖动控件坐标
     */
    public void updateDragCoordinate(int x, int y) {
        dragImageView.setX(x - (dragImageView.getMeasuredWidth() / 2));
        dragImageView.setY(y - (dragImageView.getMeasuredHeight() / 2));
    }

    /**
     * 更新标注 View
     */
    private void updateTagView(Point point) {
        Point centerPoint = getDragCenterPoint(point.x, point.y);
        if (tagView != null) {
            if (tagView.getTagMode() == TagMode.ALWAYS) {
                tagView.visible();
            }
            int posX = centerPoint.x - tagView.getWidth() / 2 + dragImageView.getWidth() / 2;
            if (centerPoint.y - tagView.getHeight() > 0) {
                tagView.setRotation(0);
                tagView.setX(posX);
                tagView.setY(centerPoint.y - tagView.getHeight());
                tagView.onRefresh(getColorEnvelope());
            } else if (tagView.isFlipAble()) {
                tagView.setRotation(180);
                tagView.setX(posX);
                tagView.setY(centerPoint.y + tagView.getHeight() - dragImageView.getHeight() / 2);
                tagView.onRefresh(getColorEnvelope());
            }
            if (posX < 0) {
                tagView.setX(0);
            }
            if (posX + tagView.getMeasuredWidth() > getMeasuredWidth()) {
                tagView.setX(getMeasuredWidth() - tagView.getMeasuredWidth());
            }
        }
    }

    /**
     * 进行回调
     *
     * @param isDrag 是否拖动
     */
    public void callColorListener(int color, boolean isDrag) {
        if (onColorSelectListener != null) {
            dragColor = color;
            if (getAlphaSlideBar() != null) {
                getAlphaSlideBar().updateColor();
                dragColor = getAlphaSlideBar().assembleColor();
            }
            if (getBrightnessSlider() != null) {
                getBrightnessSlider().updateColor();
                dragColor = getBrightnessSlider().assembleColor();
            }
            if (onColorSelectListener instanceof OnColorSelectListener) {
                ((OnColorSelectListener) onColorSelectListener).onColor(dragColor, isDrag);
            } else if (onColorSelectListener instanceof OnColorEnvelopeListener) {
                ((OnColorEnvelopeListener) onColorSelectListener).onColor(new ColorEnvelope(dragColor), isDrag);
            }

            if (tagView != null) {
                tagView.onRefresh(getColorEnvelope());
            }

            if (isVisibleTag) {
                isVisibleTag = false;
                if (dragImageView != null) {
                    dragImageView.setAlpha(dragAlpha);
                }
                if (tagView != null) {
                    tagView.setAlpha(tagAlpha);
                }
            }
        }
    }

    /**
     * 更新所有绑定控件
     */
    private void updateSlideBars() {
        if (alphaSlideBar != null) {
            alphaSlideBar.updateColor();
        }
        if (brightnessSlider != null) {
            brightnessSlider.updateColor();
            if (brightnessSlider.assembleColor() != Color.WHITE) {
                dragColor = brightnessSlider.assembleColor();
            } else if (alphaSlideBar != null) {
                dragColor = alphaSlideBar.assembleColor();
            }
        }
    }

    public void setOnColorSelectListener(BaseColorListener onColorSelectListener) {
        this.onColorSelectListener = onColorSelectListener;
    }


    public int getColor() {
        return dragColor;
    }

    public int getPureColor() {
        return dragPureColor;
    }

    public void setPureColor(int color) {
        this.dragPureColor = color;
    }

    /**
     * 获取颜色包装类
     */
    public ColorEnvelope getColorEnvelope() {
        return new ColorEnvelope(getColor());
    }

    /**
     * 获取标注 View
     */
    public BaseTagView getTagView() {
        return this.tagView;
    }

    /**
     * 设置标注 View
     */
    public void setTagView(@NonNull BaseTagView tagView) {
        tagView.gone();
        addView(tagView);
        this.tagView = tagView;
        tagView.setAlpha(tagAlpha);
    }

    /**
     * 获取拖动控件的圆点
     */
    private Point getDragCenterPoint(int x, int y) {
        return new Point(x - (dragImageView.getMeasuredWidth() / 2), y - (dragImageView.getMeasuredHeight() / 2));
    }

    /**
     * 获取拖动控件的 X 坐标
     */
    public float getDragX() {
        return dragImageView.getX() - (dragImageView.getMeasuredWidth() >> 1);
    }

    /**
     * 获取拖动控件的 Y 坐标
     */
    public float getDragY() {
        return dragImageView.getY() - (dragImageView.getMeasuredHeight() >> 1);
    }

    /**
     * 获取拖动控件的坐标
     */
    public Point getDragPoint() {
        return dragPoint;
    }

    /**
     * 设置拖动控件坐标(放在中间)
     */
    public void selectCenter() {
        setDragPoint(getMeasuredWidth() / 2, getMeasuredHeight() / 2);
    }

    /**
     * 设置拖动控件坐标
     */
    public void setDragPoint(int x, int y) {
        Point mappedPoint = PointMapper.getColorPoint(this, new Point(x, y));
        int color = getColorFromBitmap(mappedPoint.x, mappedPoint.y);
        dragPureColor = color;
        dragColor = color;
        dragPoint = new Point(mappedPoint.x, mappedPoint.y);
        updateDragCoordinate(mappedPoint.x, mappedPoint.y);
        callColorListener(getColor(), false);
        updateTagView(dragPoint);
        updateSlideBars();
    }

    /**
     * 设置拖动控件坐标以及绑定控件颜色
     */
    public void setDragPoint(int x, int y, int color) {
        dragPureColor = color;
        dragColor = color;

        if (getAlphaSlideBar() != null) {
            getAlphaSlideBar().updateColor();
            dragColor = getAlphaSlideBar().assembleColor();
        }
        if (getBrightnessSlider() != null) {
            getBrightnessSlider().updateColor();
            dragColor = getBrightnessSlider().assembleColor();
        }

        dragPoint = new Point(x, y);
        updateDragCoordinate(x, y);
        callColorListener(getColor(), false);
        updateTagView(dragPoint);
        updateSlideBars();
    }

    /**
     * 根据颜色更新拖动控件坐标
     */
    public void selectByHsv(int color) {
        int radius = getMeasuredWidth() / 2;

        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        double x = hsv[1] * Math.cos(Math.toRadians(hsv[0]));
        double y = hsv[1] * Math.sin(Math.toRadians(hsv[0]));

        int pointX = (int) ((x + 1) * radius);
        int pointY = (int) ((1 - y) * radius);

        if (this.brightnessSlider != null) {
            this.brightnessSlider.setDragPosition(hsv[2]);
        }
        Point mappedPoint = PointMapper.getColorPoint(this, new Point(pointX, pointY));
        dragPureColor = color;
        dragColor = color;
        dragPoint = new Point(mappedPoint.x, mappedPoint.y);
        updateDragCoordinate(mappedPoint.x, mappedPoint.y);
        callColorListener(getColor(), false);
        updateTagView(dragPoint);
        updateSlideBars();
    }

    /**
     * 更改取色板图片
     */
    public void setSwatchesDrawable(@NonNull Drawable drawable) {
        removeView(swatchesImageView);
        swatchesImageView = new ImageView(getContext());
        swatchesDrawable = drawable;
        swatchesImageView.setImageDrawable(swatchesDrawable);
        addView(swatchesImageView);

        removeView(dragImageView);
        addView(dragImageView);

        if (tagView != null) {
            removeView(tagView);
            addView(tagView);
        }

        if (!isVisibleTag) {
            isVisibleTag = true;
            if (dragImageView != null) {
                dragAlpha = dragImageView.getAlpha();
                dragImageView.setAlpha(0.0f);
            }
            if (tagView != null) {
                tagAlpha = tagView.getAlpha();
                tagView.setAlpha(0.0f);
            }
        }
    }

    /**
     * 更改拖动控件图片
     */
    public void setDragDrawable(@NonNull Drawable drawable) {
        dragImageView.setImageDrawable(drawable);
    }


    public int getDragMode() {
        return this.dragMode;
    }

    public void setDragMode(@DragMode int dragMode) {
        this.dragMode = dragMode;
    }

    public AlphaSlideBar getAlphaSlideBar() {
        return alphaSlideBar;
    }

    public void setAlphaSlider(@NonNull AlphaSlideBar alphaSlideBar) {
        this.alphaSlideBar = alphaSlideBar;
        alphaSlideBar.setColorPickerView(this).updateColor();

        if (getPreferenceName() != null) {
            alphaSlideBar.setPreferenceName(getPreferenceName());
        }
    }


    public BrightnessSlideBar getBrightnessSlider() {
        return brightnessSlider;
    }

    public void setBrightnessSlider(@NonNull BrightnessSlideBar brightnessSlider) {
        this.brightnessSlider = brightnessSlider;
        brightnessSlider.setColorPickerView(this).updateColor();

        if (getPreferenceName() != null) {
            brightnessSlider.setPreferenceName(getPreferenceName());
        }
    }

    public String getPreferenceName() {
        return preferenceName;
    }

    public void setPreferenceName(String preferenceName) {
        this.preferenceName = preferenceName;
        if (this.alphaSlideBar != null) {
            this.alphaSlideBar.setPreferenceName(preferenceName);
        }
        if (this.brightnessSlider != null) {
            this.brightnessSlider.setPreferenceName(preferenceName);
        }
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    public void removeLifecycleOwner(LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().removeObserver(this);
    }

    /**
     * 保存数据
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        ColorPickerPreferenceManager.get(getContext()).saveAllData(this);
    }
}

