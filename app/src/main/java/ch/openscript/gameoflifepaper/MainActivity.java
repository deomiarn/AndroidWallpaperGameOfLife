package ch.openscript.gameoflifepaper;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import top.defaults.colorpicker.ColorPickerPopup;

public class MainActivity extends AppCompatActivity {

    private Button mPickColorButton;

    private View mColorPreview;

    private int mDefaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPickColorButton = findViewById(R.id.pick_color_button);
        mColorPreview = findViewById(R.id.preview_selected_color);

        mDefaultColor = 0;

        Button button = findViewById(R.id.button);

        mPickColorButton.setOnClickListener(
                v -> new ColorPickerPopup.Builder(MainActivity.this).initialColor(Color.RED)
                        .enableBrightness(true)
                        .enableAlpha(true)
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                System.out.println(color);
                                mDefaultColor = color;
                                mColorPreview.setBackgroundColor(mDefaultColor);
                            }
                        }));

        button.setOnClickListener(v -> {

            Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this, GameOfLifeWallpaperService.class));
            intent.putExtra("backgroundColor", mDefaultColor);
            startActivity(intent);
        });

    }
}