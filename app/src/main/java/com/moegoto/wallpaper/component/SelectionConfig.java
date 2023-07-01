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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.moegoto.wallpaper.OverlaySkinService;
import com.moegoto.wallpaper.util.GdiUtils;
import com.moegoto.wallpaper.util.KeyValue;
import com.moegoto.wallpaper.util.ListAdapter;
import com.moegoto.wallpaper.util.ListAdapterItem;

public class SelectionConfig extends ListAdapterItem {

    private final String title;
    private final String configKey;
    private int defaultValue;
    private int currentValue;
    private TextView summeryView;
    private TextView titleView;
    private SharedPreferences preferences;
    private final KeyValue[] keyValues;

    public SelectionConfig(String title, String configKey, KeyValue[] keyValues, int defaultValue) {
        super();
        this.title = title;
        this.configKey = configKey;
        this.keyValues = keyValues;
        this.defaultValue = defaultValue;
    }

    @Override
    public View getView(Context context, View convertView, ViewGroup parent) {
        preferences = context.getSharedPreferences(OverlaySkinService.SHARED_PREFS_NAME, 0);
        currentValue = preferences.getInt(configKey, defaultValue);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(18);
        titleView.setGravity(Gravity.BOTTOM);
        titleView.setPadding(GdiUtils.realDp(context, 54), 0, 0, 0);

        summeryView = new TextView(context);
        for (KeyValue keyValue : keyValues) {
            if (keyValue.getValue() == currentValue) {
                summeryView.setText(keyValue.getLabel());
            }
        }

        summeryView.setTextColor(Color.BLACK);
        summeryView.setTextSize(16);
        summeryView.setGravity(Gravity.TOP);
        summeryView.setSingleLine(true);
        summeryView.setPadding(GdiUtils.realDp(context, 54), 0, 0, 0);

        layout.addView(titleView, new LayoutParams(LayoutParams.FILL_PARENT, GdiUtils.realDp(context, 30)));
        layout.addView(summeryView, new LayoutParams(LayoutParams.FILL_PARENT, GdiUtils.realDp(context, 30)));

        return layout;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view) {
        SelectionActivity activity = new SelectionActivity(parent.getContext());
        activity.show();
    }

    public class SelectionActivity extends Dialog {
        private final Context context;
        private ListView itemView;
        private ListAdapter adapter;

        public SelectionActivity(Context context) {
            super(context);
            this.context = context;
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            setContentView(createView());
            setTitle(title);
        }

        private View createView() {
            adapter = new ListAdapter(context);
            itemView = new ListView(context);
            itemView.setAdapter(adapter);
            itemView.setOnItemClickListener(adapter);
            for (KeyValue keyValue : keyValues) {
                adapter.add(new KeyValueItem(keyValue));
            }
            return itemView;
        }

        private class KeyValueItem extends ListAdapterItem {

            private final KeyValue keyValue;

            public KeyValueItem(KeyValue keyValue) {
                this.keyValue = keyValue;
            }

            @Override
            public View getView(Context context, View convertView, ViewGroup parent) {
                LinearLayout layout = new LinearLayout(context);
                TextView textView = new TextView(context);
                textView.setText(keyValue.getLabel());
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(18);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                LayoutParams textLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, (int) GdiUtils.dp(context, 40));
                textLayout.leftMargin = (int) GdiUtils.dp(context, 5);
                layout.addView(textView, textLayout);
                return layout;
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view) {
                summeryView.setText(keyValue.getLabel());
                Editor editor = preferences.edit();
                editor.putInt(configKey, keyValue.getValue());
                editor.commit();
                cancel();
            }
        }
    }
}
