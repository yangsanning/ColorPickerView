package ysn.com.view.colorpicker.tag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import ysn.com.view.colorpicker.R;
import ysn.com.view.colorpicker.annotation.TagMode;
import ysn.com.view.colorpicker.bean.ColorEnvelope;

/**
 * @Author yangsanning
 * @ClassName BaseTagView
 * @Description 标注View基类
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
public abstract class BaseTagView extends RelativeLayout {

    private int tagMode = TagMode.ALWAYS;
    /**
     * 是否可翻转
     */
    private boolean isFlipAble = true;

    public BaseTagView(Context context, int layout) {
        super(context);
        initializeLayout(layout);
    }

    private void initializeLayout(int layout) {
        View inflated = LayoutInflater.from(getContext()).inflate(layout, this);
        inflated.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        inflated.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        inflated.layout(0, 0, inflated.getMeasuredWidth(), inflated.getMeasuredHeight());
    }

    public void handleOnTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                switch (getTagMode()) {
                    case TagMode.LAST:
                        gone();
                        break;
                    case TagMode.FADE:
                        fadeIn(this);
                        break;
                    default:
                        break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                switch (getTagMode()) {
                    case TagMode.LAST:
                        gone();
                        break;
                    default:
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (getTagMode()) {
                    case TagMode.LAST:
                        visible();
                        break;
                    case TagMode.FADE:
                        fadeOut(this);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    /**
     * 淡入
     */
    private void fadeIn(View view) {
        Animation fadeIn = AnimationUtils.loadAnimation(view.getContext(), R.anim.tag_view_fade_in);
        fadeIn.setFillAfter(true);
        view.startAnimation(fadeIn);
    }

    /**
     * 淡出
     */
    public void fadeOut(View view) {
        Animation fadeOut = AnimationUtils.loadAnimation(view.getContext(), R.anim.tag_view_fade_out);
        fadeOut.setFillAfter(true);
        view.startAnimation(fadeOut);
    }

    public void visible() {
        setVisibility(View.VISIBLE);
    }

    public void gone() {
        setVisibility(View.GONE);
    }

    public int getTagMode() {
        return tagMode;
    }

    public void setTagMode(int tagMode) {
        this.tagMode = tagMode;
    }

    public boolean isFlipAble() {
        return isFlipAble;
    }

    public void setFlipAble(boolean flipAble) {
        this.isFlipAble = flipAble;
    }

    /**
     * 更新颜色
     */
    public abstract void onRefresh(ColorEnvelope colorEnvelope);
}
