package com.moegoto.wallpaper.component;

import android.R;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.moegoto.wallpaper.OverlaySkinService;
import com.moegoto.wallpaper.util.GdiUtils;
import com.moegoto.wallpaper.util.ListAdapterItem;
import com.moegoto.wallpaper.util.LocaleUtil;

public class WeeklyNameConfig extends ListAdapterItem {

    private final String title;
    private final String configKey;
    private String[] defaultValue;
    private String[] currentValue;
    private TextView titleView;
    private ImageView iconImageView;
    private SharedPreferences preferences;

    public WeeklyNameConfig(String title, String configKey, String[] defaultValue) {
        super();
        this.title = title;
        this.configKey = configKey;
        this.defaultValue = defaultValue;
    }

    @Override
    public View getView(Context context, View convertView, ViewGroup parent) {
        preferences = context.getSharedPreferences(OverlaySkinService.SHARED_PREFS_NAME, 0);
        currentValue = new String[] { preferences.getString(configKey + "0", defaultValue[0]),
                preferences.getString(configKey + "1", defaultValue[1]),
                preferences.getString(configKey + "2", defaultValue[2]),
                preferences.getString(configKey + "3", defaultValue[3]),
                preferences.getString(configKey + "4", defaultValue[4]),
                preferences.getString(configKey + "5", defaultValue[5]),
                preferences.getString(configKey + "6", defaultValue[6]) };

        LinearLayout layout = new LinearLayout(context);

        layout.setOrientation(LinearLayout.HORIZONTAL);

        iconImageView = new ImageView(context);
        iconImageView.setImageResource(R.drawable.ic_menu_week);

        titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(18);
        titleView.setGravity(Gravity.CENTER_VERTICAL);

        LayoutParams params = new LayoutParams(GdiUtils.realDp(context, 54), GdiUtils.realDp(context, 54));
        params.gravity = Gravity.CENTER;

        layout.addView(iconImageView, params);
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
        private WeekDayInput input0;
        private WeekDayInput input1;
        private WeekDayInput input2;
        private WeekDayInput input3;
        private WeekDayInput input4;
        private WeekDayInput input5;
        private WeekDayInput input6;

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

            input0 = new WeekDayInput(context, "SUNDAY");
            layout.addView(input0);
            input1 = new WeekDayInput(context, "MONDAY");
            layout.addView(input1);
            input2 = new WeekDayInput(context, "TUESDAY");
            layout.addView(input2);
            input3 = new WeekDayInput(context, "WEDNESDAY");
            layout.addView(input3);
            input4 = new WeekDayInput(context, "THURSDAY");
            layout.addView(input4);
            input5 = new WeekDayInput(context, "FRIDAY");
            layout.addView(input5);
            input6 = new WeekDayInput(context, "SATURDAY");
            layout.addView(input6);

            input0.setValue(currentValue[0]);
            input1.setValue(currentValue[1]);
            input2.setValue(currentValue[2]);
            input3.setValue(currentValue[3]);
            input4.setValue(currentValue[4]);
            input5.setValue(currentValue[5]);
            input6.setValue(currentValue[6]);

            //
            LinearLayout buttonLayout = new LinearLayout(context);
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
            buttonLayout.setGravity(Gravity.CENTER);

            Button restoreButton = new Button(context);
            restoreButton.setText(LocaleUtil.locale("初期値に戻す", "Restore Default"));
            restoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 初期値に戻す
                    input0.setValue(defaultValue[0]);
                    input1.setValue(defaultValue[1]);
                    input2.setValue(defaultValue[2]);
                    input3.setValue(defaultValue[3]);
                    input4.setValue(defaultValue[4]);
                    input5.setValue(defaultValue[5]);
                    input6.setValue(defaultValue[6]);
                }
            });
            Button okButton = new Button(context);
            okButton.setText(LocaleUtil.locale("決定", "OK"));
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Editor editor = preferences.edit();
                    OverlaySkinService.disableScreenUpdatable = true;
                    try {
                        editor.putString(configKey + "0", input0.getValue());
                        editor.putString(configKey + "1", input1.getValue());
                        editor.putString(configKey + "2", input2.getValue());
                        editor.putString(configKey + "3", input3.getValue());
                        editor.putString(configKey + "4", input4.getValue());
                        editor.putString(configKey + "5", input5.getValue());
                        editor.putString(configKey + "6", input6.getValue());
                    } finally {
                        OverlaySkinService.disableScreenUpdatable = false;
                        editor.putString("hoge", "");
                    }
                    editor.commit();
                    ColorSelectActivity.this.cancel();
                }
            });
            buttonLayout.addView(restoreButton);
            buttonLayout.addView(okButton);
            layout.addView(buttonLayout);
            return layout;
        }

        private class WeekDayInput extends LinearLayout {

            private EditText editText;

            public WeekDayInput(Context context, String labelText) {
                super(context);
                setOrientation(HORIZONTAL);
                TextView label = new TextView(context);
                label.setTextColor(Color.WHITE);
                label.setTextSize(14);
                label.setText(labelText);

                LayoutParams labelParams = new LayoutParams(GdiUtils.realDp(context, 18 * 7), LayoutParams.WRAP_CONTENT);
                labelParams.setMargins(GdiUtils.realDp(context, 10), 0, GdiUtils.realDp(context, 10), 0);
                addView(label, labelParams);

                editText = new EditText(context);
                editText.setSingleLine(true);
                editText.setCompoundDrawablePadding(0);
                editText.setTextSize(14);
                editText.setHeight(GdiUtils.realDp(context, 16));

                LayoutParams editParams = new LayoutParams(GdiUtils.realDp(context, 18 * 7), GdiUtils.realDp(context, 34));
                editParams.setMargins(GdiUtils.realDp(context, 10), 0, GdiUtils.realDp(context, 10), 0);
                addView(editText, editParams);
            }

            public void setValue(String value) {
                editText.setText(value);
            }

            public String getValue() {
                return editText.getText().toString();
            }
        }
    }
}
