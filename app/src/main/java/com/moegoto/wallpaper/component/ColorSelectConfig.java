package com.moegoto.wallpaper.component;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.moegoto.wallpaper.OverlaySkinService;
import com.moegoto.wallpaper.util.GdiUtils;
import com.moegoto.wallpaper.util.ListAdapterItem;
import com.moegoto.wallpaper.util.LocaleUtil;

public class ColorSelectConfig extends ListAdapterItem {

    private final String title;
    private final String configKey;
    private int defaultValue;
    private int currentValue;
    private TextView summeryView;
    private TextView titleView;
    private SharedPreferences preferences;

    public ColorSelectConfig(String title, String configKey, int defaultValue) {
        super();
        this.title = title;
        this.configKey = configKey;
        this.defaultValue = defaultValue;
    }

    @Override
    public View getView(Context context, View convertView, ViewGroup parent) {
        preferences = context.getSharedPreferences(OverlaySkinService.SHARED_PREFS_NAME, 0);
        currentValue = preferences.getInt(configKey, defaultValue);

        LinearLayout layout = new LinearLayout(context);

        layout.setOrientation(LinearLayout.HORIZONTAL);
        titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(18);
        titleView.setGravity(Gravity.CENTER_VERTICAL);

        summeryView = new TextView(context);
        summeryView.setText(" ");
        summeryView.setBackgroundColor(currentValue);
        summeryView.setTextColor(Color.BLACK);
        summeryView.setTextSize(16);
        summeryView.setGravity(Gravity.CENTER_VERTICAL);

        LayoutParams params = new LayoutParams(GdiUtils.realDp(context, 40), GdiUtils.realDp(context, 40));
        params.setMargins(GdiUtils.realDp(context, 7), 0, GdiUtils.realDp(context, 7), 0);
        layout.addView(summeryView, params);
        layout.addView(titleView, new LayoutParams(LayoutParams.FILL_PARENT, GdiUtils.realDp(context, 54)));

        return layout;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view) {
        ColorSelectActivity activity = new ColorSelectActivity(parent.getContext());
        activity.show();
    }

    public class ColorSelectActivity extends Dialog {

        private Context context;
        private ColorSlider redSlider;
        private ColorSlider greenSlider;
        private ColorSlider blueSlider;
        private ColorSlider alphaSlider;
        private TextView colorView;

        public ColorSelectActivity(Context context) {
            super(context);
            this.context = context;
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            setContentView(createView());
        }

        private View createView() {
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            setTitle(title);

            colorView = new TextView(context);
            colorView.setText(" ");
            colorView.setBackgroundColor(currentValue);
            colorView.setWidth(GdiUtils.realDp(context, 50));
            colorView.setHeight(GdiUtils.realDp(context, 50));
            colorView.setGravity(Gravity.CENTER);
            LayoutParams colorViewParams = new LayoutParams(LayoutParams.FILL_PARENT, GdiUtils.realDp(context, 50));
            colorViewParams.setMargins(GdiUtils.realDp(context, 60), 0, GdiUtils.realDp(context, 60), 0);
            layout.addView(colorView, colorViewParams);

            redSlider = new ColorSlider(context, "R", Color.red(currentValue));
            layout.addView(redSlider, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

            greenSlider = new ColorSlider(context, "G", Color.green(currentValue));
            layout.addView(greenSlider, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

            blueSlider = new ColorSlider(context, "B", Color.blue(currentValue));
            layout.addView(blueSlider, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

            alphaSlider = new ColorSlider(context, "A", Color.alpha(currentValue));
            layout.addView(alphaSlider, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

            layout.addView(new TextView(context));

            LinearLayout buttonLayout = new LinearLayout(context);
            buttonLayout.setOrientation(LinearLayout.VERTICAL);
            buttonLayout.setGravity(Gravity.CENTER);

            Button restoreButton = new Button(context);
            restoreButton.setText(LocaleUtil.locale("初期値に戻す", "Restore Default"));
            restoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 初期値に戻す
                    currentValue = defaultValue;
                    alphaSlider.setValue(Color.alpha(currentValue));
                    redSlider.setValue(Color.red(currentValue));
                    greenSlider.setValue(Color.green(currentValue));
                    blueSlider.setValue(Color.blue(currentValue));
                    refresh();
                }
            });
            Button okButton = new Button(context);
            okButton.setText(LocaleUtil.locale("決定", "OK"));
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Editor editor = preferences.edit();
                    editor.putInt(configKey, currentValue);
                    editor.commit();
                    ColorSelectActivity.this.cancel();
                    summeryView.setBackgroundColor(currentValue);
                }
            });
            buttonLayout.addView(restoreButton);
            buttonLayout.addView(okButton);
            layout.addView(buttonLayout);
            return layout;
        }

        private void refresh() {
            currentValue = Color.argb(alphaSlider.getValue(), redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
            colorView.setBackgroundColor(currentValue);
        }

        public class ColorSlider extends LinearLayout {

            private TextView textView;
            private SeekBar seekBar;
            private final String prefix;

            public int getValue() {
                return seekBar.getProgress();
            }

            public void setValue(int value) {
                seekBar.setProgress(value);
                textView.setText(prefix + ":" + String.valueOf(value));
            }

            public ColorSlider(Context context, final String prefix, int defaultValue) {
                super(context);
                this.prefix = prefix;
                setOrientation(HORIZONTAL);

                textView = new TextView(context);
                textView.setText(prefix + ":" + String.valueOf(defaultValue));
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(18);

                seekBar = new SeekBar(context);
                seekBar.setMax(255);
                seekBar.setProgress(defaultValue);
                seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekbar) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekbar) {
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekbar, int value, boolean flag) {
                        textView.setText(prefix + ":" + String.valueOf(value));
                        refresh();
                    }
                });
                LayoutParams textViewParams = new LayoutParams(GdiUtils.realDp(context, 60), LayoutParams.FILL_PARENT);
                textViewParams.setMargins(GdiUtils.realDp(context, 5),
                        GdiUtils.realDp(context, 5),
                        GdiUtils.realDp(context, 5),
                        GdiUtils.realDp(context, 5));

                LayoutParams seekBarParams = new LayoutParams(GdiUtils.realDp(context, 160), LayoutParams.FILL_PARENT);
                seekBarParams.setMargins(GdiUtils.realDp(context, 5), GdiUtils.realDp(context, 5), GdiUtils.realDp(context, 5), GdiUtils.realDp(context, 5));

                addView(textView, textViewParams);
                addView(seekBar, seekBarParams);
            }
        }
    }
}
