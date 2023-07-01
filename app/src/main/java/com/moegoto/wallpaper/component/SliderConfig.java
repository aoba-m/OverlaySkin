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

public class SliderConfig extends ListAdapterItem {

    private final String title;
    private final String configKey;
    private int maxValue;
    private int defaultValue;
    private int currentValue;
    private TextView summeryView;
    private TextView titleView;
    private SharedPreferences preferences;
    private final int minValue;

    public SliderConfig(String title, String configKey, int defaultValue, int maxValue) {
        super();
        this.title = title;
        this.configKey = configKey;
        this.defaultValue = defaultValue;
        this.maxValue = maxValue;
        this.minValue = 0;
    }

    public SliderConfig(String title, String configKey, int defaultValue, int maxValue, int minValue) {
        super();
        this.title = title;
        this.configKey = configKey;
        this.defaultValue = defaultValue;
        this.maxValue = maxValue;
        this.minValue = minValue;
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
        summeryView.setText(String.valueOf(currentValue));
        summeryView.setTextColor(Color.BLACK);
        summeryView.setTextSize(16);
        summeryView.setGravity(Gravity.CENTER);

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
        private Slider slider;

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

            LinearLayout plusMinusBar = new LinearLayout(context);
            Button minusButton = new Button(context);
            minusButton.setText("-");
            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (minValue < currentValue) {
                        currentValue--;
                        slider.setValue(currentValue);
                    }
                }
            });

            Button plusButton = new Button(context);
            plusButton.setText("+");
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (maxValue > currentValue) {
                        currentValue++;
                        slider.setValue(currentValue);
                    }
                }
            });

            plusMinusBar.setGravity(Gravity.RIGHT);
            plusMinusBar.addView(minusButton, GdiUtils.layoutParam((int) GdiUtils.dp(context, 80), LayoutParams.WRAP_CONTENT));
            plusMinusBar.addView(plusButton, GdiUtils.layoutParam((int) GdiUtils.dp(context, 80), LayoutParams.WRAP_CONTENT));
            layout.addView(plusMinusBar);

            slider = new Slider(context, maxValue, currentValue);
            layout.addView(slider, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            layout.addView(new TextView(context));

            //
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
                    slider.setValue(currentValue - minValue);
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
                    summeryView.setText(String.valueOf(currentValue));
                }
            });
            buttonLayout.addView(restoreButton);
            buttonLayout.addView(okButton);
            layout.addView(buttonLayout);
            return layout;
        }

        private void refresh() {
            currentValue = slider.getValue();
        }

        public class Slider extends LinearLayout {

            private TextView textView;
            private SeekBar seekBar;

            public int getValue() {
                return seekBar.getProgress() + minValue;
            }

            public void setValue(int value) {
                seekBar.setProgress(value - minValue);
                textView.setText(String.valueOf(value));
            }

            public Slider(Context context, int maxValue, int defaultValue) {
                super(context);
                setOrientation(HORIZONTAL);

                textView = new TextView(context);
                textView.setText(String.valueOf(defaultValue));
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(18);

                seekBar = new SeekBar(context);
                seekBar.setMax(maxValue - minValue);
                seekBar.setProgress(defaultValue - minValue);
                seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekbar) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekbar) {
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekbar, int value, boolean flag) {
                        textView.setText(String.valueOf(value + minValue));
                        refresh();
                    }
                });
                LayoutParams textViewParams = new LayoutParams(GdiUtils.realDp(context, 60), LayoutParams.FILL_PARENT);
                textViewParams.setMargins(GdiUtils.realDp(context, 10),
                        GdiUtils.realDp(context, 10),
                        GdiUtils.realDp(context, 10),
                        GdiUtils.realDp(context, 10));

                LayoutParams seekBarParams = new LayoutParams(GdiUtils.realDp(context, 160), LayoutParams.FILL_PARENT);
                seekBarParams.setMargins(GdiUtils.realDp(context, 10), GdiUtils.realDp(context, 10), GdiUtils.realDp(context, 10), GdiUtils.realDp(context, 10));

                addView(textView, textViewParams);
                addView(seekBar, seekBarParams);
            }
        }
    }
}
