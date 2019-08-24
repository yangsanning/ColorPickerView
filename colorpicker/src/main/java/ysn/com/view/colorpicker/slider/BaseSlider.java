package ysn.com.view.colorpicker.slider;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ysn.com.view.colorpicker.ColorPickerView;
import ysn.com.view.colorpicker.annotation.DragMode;

/**
 * @Author yangsanning
 * @ClassName BaseSlider
 * @Description 滑动控件的基类
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
public abstract class BaseSlider extends FrameLayout {

    /**
     * 拖动控件图片
     */
    protected Drawable dragDrawable;

    /**
     * 拖拽控件大小
     */
    protected int dragSize;

    /**
     * 拖拽控件位置
     */
    protected float dragPosition = 1.0f;
    protected int dragX = 0;

    protected int pureColor = Color.WHITE;
    protected String preferenceName;

    protected Paint colorPaint;

    public ColorPickerView colorPickerView;
    protected ImageView dragImageView;

    public BaseSlider(Context context) {
        this(context, null);
    }

    public BaseSlider(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseSlider(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
        initView();
    }

    private void initView() {
        this.colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.setBackgroundColor(Color.WHITE);

        dragImageView = new ImageView(getContext());
        if (dragDrawable != null) {
            dragImageView.setImageDrawable(dragDrawable);
            LayoutParams dragParams = new LayoutParams(dragSize, dragSize);
            dragParams.gravity = Gravity.CENTER_VERTICAL;
            addView(dragImageView, dragParams);
        }

        initializeSelector();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), colorPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (colorPickerView != null) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_UP:
                    dragImageView.setPressed(true);
                    handleOnTouchEvent(event);
                    return true;
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    dragImageView.setPressed(true);
                    handleOnTouchEvent(event);
                    return true;
                default:
                    dragImageView.setPressed(false);
                    return false;
            }
        } else {
            return false;
        }
    }

    private void handleOnTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float left = dragImageView.getMeasuredWidth();
        float right = getMeasuredWidth() - dragImageView.getMeasuredWidth();
        if (eventX < left) {
            eventX = left;
        }
        if (eventX > right) {
            eventX = right;
        }
        dragPosition = (eventX - left) / (right - left);
        if (dragPosition > 1.0f) {
            dragPosition = 1.0f;
        }

        Point snapPoint = new Point((int) event.getX(), (int) event.getY());
        dragX = snapPoint.x;
        dragImageView.setX(snapPoint.x - (dragImageView.getMeasuredWidth() / 2));

        switch (colorPickerView.getDragMode()) {
            case DragMode.LAST:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    colorPickerView.callColorListener(assembleColor(), true);
                }
                break;
            default:
                colorPickerView.callColorListener(assembleColor(), true);
                break;
        }

        if (colorPickerView.getTagView() != null) {
            colorPickerView.getTagView().handleOnTouchEvent(event);
        }

        // 最大位置
        int maxPosition = getMeasuredWidth() - dragImageView.getMeasuredWidth();
        if (dragImageView.getX() >= maxPosition) {
            dragImageView.setX(maxPosition);
        }
        if (dragImageView.getX() <= 0) {
            dragImageView.setX(0);
        }
    }

    /**
     * 当 ColorPickView 拖拽时回调
     */
    public void updateColor() {
        pureColor = colorPickerView.getPureColor();
        initPaint();
        invalidate();
    }

    public void updateDragX(int x) {
        float left = dragImageView.getMeasuredWidth();
        float right = getMeasuredWidth() - dragImageView.getMeasuredWidth();
        dragPosition = (x - left) / (right - left);
        if (dragPosition > 1.0f) {
            dragPosition = 1.0f;
        }
        dragImageView.setX(x - (dragImageView.getMeasuredWidth() / 2));
        dragX = x;
        int maxPos = getMeasuredWidth() - dragImageView.getMeasuredWidth();
        if (dragImageView.getX() >= maxPos) {
            dragImageView.setX(maxPos);
        }
        if (dragImageView.getX() <= 0) {
            dragImageView.setX(0);
        }
        colorPickerView.callColorListener(assembleColor(), false);
    }

    public void setDragPosition(float dragPosition) {
        if (dragPosition > 1.0f) {
            this.dragPosition = 1.0f;
        } else {
            this.dragPosition = dragPosition;
        }
        float x = (getMeasuredWidth() * dragPosition) - (dragImageView.getMeasuredWidth() >> 1);
        dragX = (int) x;
        dragImageView.setX(x);
    }

    private void initializeSelector() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                onInflateFinished();
            }
        });
    }

    public int getPureColor() {
        return pureColor;
    }

    public BaseSlider setColorPickerView(ColorPickerView colorPickerView) {
        this.colorPickerView = colorPickerView;
        return this;
    }

    protected float getDragPosition() {
        return this.dragPosition;
    }

    /**
     * 获取拖动控件的 X 坐标
     */
    public int getDragX() {
        return this.dragX;
    }

    public String getPreferenceName() {
        return preferenceName;
    }

    public void setPreferenceName(String preferenceName) {
        this.preferenceName = preferenceName;
    }

    public abstract void onInflateFinished();

    protected abstract void initAttrs(AttributeSet attrs);

    protected abstract void initPaint();

    /**
     * 纯色
     */
    public abstract int assembleColor();
}
