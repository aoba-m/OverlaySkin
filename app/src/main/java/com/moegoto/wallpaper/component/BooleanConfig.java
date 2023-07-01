package com.moegoto.wallpaper.component;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.moegoto.wallpaper.OverlaySkinService;
import com.moegoto.wallpaper.util.GdiUtils;
import com.moegoto.wallpaper.util.ListAdapterItem;

public class BooleanConfig extends ListAdapterItem {

    private final String title;
    private final String configKey;
    private boolean defaultValue;
    private boolean currentValue;
    private SharedPreferences preferences;

    private TextView titleView;

    public BooleanConfig(String title, String configKey, boolean defaultValue) {
        super();
        this.title = title;
        this.configKey = configKey;
        this.defaultValue = defaultValue;
    }

    @Override
    public View getView(Context context, View convertView, ViewGroup parent) {
        preferences = context.getSharedPreferences(OverlaySkinService.SHARED_PREFS_NAME, 0);
        currentValue = preferences.getBoolean(configKey, defaultValue);

        LinearLayout layout = new LinearLayout(context);

        layout.setOrientation(LinearLayout.HORIZONTAL);
        titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(18);
        titleView.setGravity(Gravity.CENTER_VERTICAL);

        CheckBox checkBox = new CheckBox(context);
        checkBox.setText("");
        checkBox.setTextColor(Color.BLACK);
        checkBox.setTextSize(18);
        checkBox.setGravity(Gravity.CENTER_VERTICAL);
        checkBox.setChecked(currentValue);
        checkBox.setPadding(GdiUtils.realDp(context, 4), 0, 0, 0);
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundbutton, boolean flag) {
                currentValue = flag;
                Editor editor = preferences.edit();
                editor.putBoolean(configKey, currentValue);
                editor.commit();
            }
        });

        LayoutParams params = new LayoutParams(GdiUtils.realDp(context, 40), GdiUtils.realDp(context, 40));
        params.setMargins(GdiUtils.realDp(context, 7), 0, GdiUtils.realDp(context, 7), 0);
        layout.addView(checkBox, params);
        layout.addView(titleView, new LayoutParams(LayoutParams.FILL_PARENT, GdiUtils.realDp(context, 54)));

        return layout;
    }
}
