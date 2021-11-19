package ch.openscript.gameoflifepaper;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import top.defaults.colorpicker.ColorPickerPopup;

public class MainActivity extends AppCompatActivity {
    private View mColorPreview;
    private View mCellColorPreview;

    private int mDefaultColor;
    private int mCellDefaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("storage", 0);
        SharedPreferences.Editor editor = pref.edit();

        mColorPreview = findViewById(R.id.preview_selected_color);
        mCellColorPreview = findViewById(R.id.preview_cell_selected_color);

        mDefaultColor = Color.WHITE;
        mCellDefaultColor = Color.RED;

        mColorPreview.setBackgroundColor(mDefaultColor);
        mCellColorPreview.setBackgroundColor(mCellDefaultColor);

        Button button = findViewById(R.id.button);

        mColorPreview.setOnClickListener(
                v -> new ColorPickerPopup.Builder(MainActivity.this).initialColor(mDefaultColor)
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
                                mDefaultColor = color;
                                mColorPreview.setBackgroundColor(mDefaultColor);
                            }
                        }));

        mCellColorPreview.setOnClickListener(
                v -> new ColorPickerPopup.Builder(MainActivity.this).initialColor(mCellDefaultColor)
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
                                mCellDefaultColor = color;
                                mCellColorPreview.setBackgroundColor(mCellDefaultColor);
                            }
                        }));

        button.setOnClickListener(v -> {
            editor.putInt("backgroundColor", mDefaultColor);
            editor.putInt("cellColor", mCellDefaultColor);
            editor.apply();

            Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this, GameOfLifeWallpaperService.class));
            startActivity(intent);
        });
    }
}