package ysn.com.view.colorpicker.listener;

import ysn.com.view.colorpicker.bean.ColorEnvelope;


/**
 * @Author yangsanning
 * @ClassName ColorEnvelopeListener
 * @Description 颜色包装类监听器
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
public interface ColorEnvelopeListener extends BaseColorListener {
  /**
   * @param envelope 颜色包装类
   * @param isDrag 是否拖动
   */
  void onColorSelected(ColorEnvelope envelope, boolean isDrag);
}
