package ysn.com.demo.colorpicker;

import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import ysn.com.view.colorpicker.ColorPickerView;
import ysn.com.view.colorpicker.listener.ColorEnvelopeListener;
import ysn.com.view.colorpicker.slider.AlphaSlideBar;
import ysn.com.view.colorpicker.slider.BrightnessSlideBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ColorPickerView colorPickerView = findViewById(R.id.main_activity_color_picker_view);
        colorPickerView.setColorListener((ColorEnvelopeListener) (envelope, isDrag) ->
                Log.d("test", "color: " + envelope.getHexCode()));

        SeekBar seekBar = findViewById(R.id.main_activity_seek_bar);
        BrightnessSlideBar brightnessSlideBar = findViewById(R.id.main_activity_brightness_slide);
        brightnessSlideBar.setOnDragPositionListener(dragPosition ->
                seekBar.setProgress((int) (dragPosition * 100)));
        colorPickerView.setBrightnessSlider(brightnessSlideBar);

        AlphaSlideBar alphaSlideBar = findViewById(R.id.main_activity_alpha_slide_bar);
        colorPickerView.setAlphaSlider(alphaSlideBar);
        colorPickerView.setLifecycleOwner(this);
    }
}
