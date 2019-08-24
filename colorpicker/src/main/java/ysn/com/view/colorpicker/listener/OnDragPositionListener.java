package ysn.com.view.colorpicker.listener;

/**
 * @Author yangsanning
 * @ClassName OnDragPositionListener
 * @Description 滑动控件位置的回调
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
public interface OnDragPositionListener {

    /**
     * 滑动控件位置的回调
     *
     * @param dragPosition 0~1
     */
    void onDragPosition(float dragPosition);
}
