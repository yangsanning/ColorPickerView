package ysn.com.view.colorpicker.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author yangsanning
 * @ClassName TagMode
 * @Description 标注类显示方式
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
@IntDef({TagMode.ALWAYS, TagMode.LAST, TagMode.FADE})
@Retention(RetentionPolicy.SOURCE)
public @interface TagMode {

    /**
     * 总是显示
     */
    int ALWAYS = 0;

    /**
     * 手指松开时显示
     */
    int LAST = 1;

    /**
     * 淡入淡出
     */
    int FADE = 2;
}
