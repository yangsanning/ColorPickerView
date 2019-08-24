package ysn.com.view.colorpicker.listener;

import ysn.com.view.colorpicker.bean.ColorEnvelope;


/**
 * @Author yangsanning
 * @ClassName OnColorEnvelopeListener
 * @Description 颜色包装类监听器
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
public interface OnColorEnvelopeListener extends BaseColorListener {
  /**
   * @param envelope 颜色包装类
   * @param isDrag 是否拖动
   */
  void onColor(ColorEnvelope envelope, boolean isDrag);
}
