package ysn.com.view.colorpicker.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author yangsanning
 * @ClassName DragMode
 * @Description 拖动回调方式
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
@IntDef({DragMode.ALWAYS, DragMode.LAST})
@Retention(RetentionPolicy.SOURCE)
public @interface DragMode {

    /**
     * 总是回调
     */
    int ALWAYS = 0;

    /**
     * 只回调最后一次
     */
    int LAST = 1;
}
