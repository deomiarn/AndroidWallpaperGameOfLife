package ch.openscript.gameoflifepaper;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Objects;

import top.defaults.colorpicker.ColorPickerPopup;

public class MainActivity extends AppCompatActivity {
    private View mColorPreview;
    private View mCellColorPreview;
    private TextView delay;
    private Button minusButton;
    private Button plusButton;
    private Button button;
    private SharedPreferences pref;

    private int mDefaultColor;
    private int mCellDefaultColor;

    @Override
    protected void onStart() {
        System.out.println("ja");
        super.onStart();
        int backgroundColor = pref.getInt("backgroundColor", Color.WHITE);
        int cellColor = pref.getInt("cellColor", Color.RED);
        int delayMultiplication = pref.getInt("delayMultiplication", 3);

        mDefaultColor = backgroundColor;
        mCellDefaultColor = cellColor;
        mColorPreview.setBackgroundColor(backgroundColor);
        mCellColorPreview.setBackgroundColor(cellColor);
        delay.setText(String.valueOf(delayMultiplication));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("storage", 0);
        SharedPreferences.Editor editor = pref.edit();

        mColorPreview = findViewById(R.id.preview_selected_color);
        mColorPreview = findViewById(R.id.preview_selected_color);
        mCellColorPreview = findViewById(R.id.preview_cell_selected_color);
        delay = findViewById(R.id.delay);
        minusButton = findViewById(R.id.minusButton);
        plusButton = findViewById(R.id.plusButton);
        mDefaultColor = Color.WHITE;
        mCellDefaultColor = Color.RED;
        mColorPreview.setBackgroundColor(mDefaultColor);
        mCellColorPreview.setBackgroundColor(mCellDefaultColor);
        button = findViewById(R.id.button);

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
                                editor.putInt("backgroundColor", mDefaultColor);
                                editor.apply();
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
                                editor.putInt("cellColor", mCellDefaultColor);
                                editor.apply();
                            }
                        }));

        minusButton.setOnClickListener(v -> {
            int number = Integer.parseInt(delay.getText().toString());
            if (number > 1) {
                number = number - 1;
                delay.setText(String.valueOf(number));
                editor.putInt("delayMultiplication", Integer.parseInt(delay.getText().toString()));
                editor.apply();
            }
        });

        plusButton.setOnClickListener(v -> {
            int number = Integer.parseInt(delay.getText().toString());
            if (number < 5) {
                number = number + 1;
                delay.setText(String.valueOf(number));
                editor.putInt("delayMultiplication", Integer.parseInt(delay.getText().toString()));
                editor.apply();
            }
        });

        button.setOnClickListener(v -> {
            editor.putInt("backgroundColor", mDefaultColor);
            editor.putInt("cellColor", mCellDefaultColor);
            editor.putInt("delayMultiplication", Integer.parseInt(delay.getText().toString()));
            editor.apply();

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try {
                wallpaperManager.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this, GameOfLifeWallpaperService.class));
            startActivity(intent);
        });
    }
}