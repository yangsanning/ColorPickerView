package ysn.com.view.colorpicker.bean;

import ysn.com.view.colorpicker.utils.ColorUtils;

/**
 * @Author yangsanning
 * @ClassName ColorEnvelope
 * @Description 颜色包装类, 返回所有颜色类型
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
public class ColorEnvelope {

    private int color;
    private String hexCode;
    private int[] argb;

    public ColorEnvelope(int color) {
        this.color = color;
        this.hexCode = ColorUtils.getHexCode(color);
        this.argb = ColorUtils.getColorARGB(color);
    }

    public int getColor() {
        return color;
    }

    public String getHexCode() {
        return hexCode;
    }

    public int[] getArgb() {
        return argb;
    }
}
