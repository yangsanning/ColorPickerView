package ysn.com.view.colorpicker.listener;

/**
 * @Author yangsanning
 * @ClassName ColorListener
 * @Description 颜色监听器
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
public interface ColorListener extends BaseColorListener {

    /**
     * @param isDrag 是否拖动
     */
    void onColorSelected(int color, boolean isDrag);
}
