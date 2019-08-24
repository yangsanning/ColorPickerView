package ysn.com.view.colorpicker.listener;

/**
 * @Author yangsanning
 * @ClassName OnColorSelectListener
 * @Description 颜色监听器
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
public interface OnColorSelectListener extends BaseColorListener {

    /**
     * @param isDrag 是否拖动
     */
    void onColor(int color, boolean isDrag);
}
