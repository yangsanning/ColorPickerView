package ysn.com.demo.colorpicker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ysn.com.view.colorpicker.ColorPickerView;
import ysn.com.view.colorpicker.slider.AlphaSlideBar;
import ysn.com.view.colorpicker.slider.BrightnessSlideBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ColorPickerView colorPickerView = findViewById(R.id.colorPickerView);

        BrightnessSlideBar brightnessSlideBar = findViewById(R.id.brightnessSlide);
        colorPickerView.setBrightnessSlider(brightnessSlideBar);

        AlphaSlideBar alphaSlideBar = findViewById(R.id.alphaSlideBar);
        colorPickerView.setAlphaSlider(alphaSlideBar);
        colorPickerView.setLifecycleOwner(this);
    }
}
