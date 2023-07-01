package com.moegoto.wallpaper.component;

import java.util.List;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

import com.moegoto.wallpaper.OverlaySkinService;
import com.moegoto.wallpaper.util.GdiUtils;
import com.moegoto.wallpaper.util.ListAdapter;
import com.moegoto.wallpaper.util.ListAdapterItem;
import com.moegoto.wallpaper.util.LocaleUtil;

public class ActivitySelectConfig extends ListAdapterItem {

    private final String title;
    private final String configKey;
    private String defaultValue;
    private String currentValue;
    private TextView summeryView;
    private TextView titleView;
    private SharedPreferences preferences;
    private ImageView iconImageView;

    public ActivitySelectConfig(String title, String configKey, String defaultValue) {
        super();
        this.title = title;
        this.configKey = configKey;
        this.defaultValue = defaultValue;
    }

    @Override
    public View getView(Context context, View convertView, ViewGroup parent) {
        preferences = context.getSharedPreferences(OverlaySkinService.SHARED_PREFS_NAME, 0);
        currentValue = preferences.getString(configKey, defaultValue);

        LinearLayout layout = new LinearLayout(context);
        LinearLayout rowLayout = new LinearLayout(context);

        layout.setOrientation(LinearLayout.VERTICAL);
        titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(18);
        titleView.setGravity(Gravity.BOTTOM);
        //        titleView.setPadding(GdiUtils.realDp(context, 54), 0, 0, 0);

        String[] items = currentValue.split("/", 3);

        summeryView = new TextView(context);
        if (items.length > 2) {
            summeryView.setText(items[2]);
        }
        summeryView.setTextColor(Color.BLACK);
        summeryView.setTextSize(14);
        summeryView.setGravity(Gravity.TOP);

        iconImageView = new ImageView(context);
        updateImage(context);

        layout.addView(titleView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        layout.addView(summeryView, new LayoutParams(LayoutParams.FILL_PARENT, GdiUtils.realDp(context, 30)));

        LayoutParams params = new LayoutParams(GdiUtils.realDp(context, 40), GdiUtils.realDp(context, 40));
        params.setMargins(GdiUtils.realDp(context, 7), 0, GdiUtils.realDp(context, 7), 0);
        params.gravity = Gravity.CENTER;

        rowLayout.addView(iconImageView, params);
        rowLayout.addView(layout);

        return rowLayout;
    }

    private void updateImage(Context context) {
        try {
            String[] items = currentValue.split("/", 3);
            if (items.length > 2) {
                PackageManager packageManager = context.getPackageManager();
                ComponentName componentName = new ComponentName(items[0], items[1]);
                iconImageView.setImageDrawable(packageManager.getActivityIcon(componentName));
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view) {
        FontSelectActivity activity = new FontSelectActivity(parent.getContext());
        activity.show();
    }

    public class FontSelectActivity extends Dialog {

        private ListView listView;
        private ListAdapter listAdapter;
        private Context context;

        public FontSelectActivity(Context context) {
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

            listAdapter = new ListAdapter(context);
            listView = new ListView(context);

            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> appList = context.getPackageManager().queryIntentActivities(mainIntent, 0);
            listAdapter.add(new ActivityItem(null));
            for (ResolveInfo resolveInfo : appList) {
                listAdapter.add(new ActivityItem(resolveInfo));
            }
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new ListViewSelectListener());
            layout.addView(listView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);// dpToPixel(context, 300));
            return layout;
        }

        private class ListViewSelectListener implements OnItemClickListener {
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                ActivityItem item = (ActivityItem) listAdapter.getItem(position);
                if (item.info == null) {
                    currentValue = "";
                    summeryView.setText("");
                } else {
                    currentValue = item.info.activityInfo.packageName + "/"
                            + item.info.activityInfo.name
                            + "/"
                            + item.info.activityInfo.applicationInfo.loadLabel(context.getPackageManager());
                    summeryView.setText(item.info.activityInfo.applicationInfo.loadLabel(context.getPackageManager()));
                }
                updateImage(context);
                Editor editor = preferences.edit();
                editor.putString(configKey, currentValue);
                editor.commit();
                FontSelectActivity.this.cancel();
            }
        }

        private class ActivityItem extends ListAdapterItem {

            private final ResolveInfo info;

            public ActivityItem(ResolveInfo info) {
                this.info = info;
            }

            @Override
            public View getView(Context context, View convertView, ViewGroup parent) {
                LinearLayout layout = new LinearLayout(context);
                TextView textView = new TextView(context);

                ImageView icon = new ImageView(context);
                if (info == null) {
                    textView.setText(LocaleUtil.locale("設定しない", "None"));
                } else {
                    textView.setText(info.activityInfo.applicationInfo.loadLabel(context.getPackageManager()));
                    icon.setImageDrawable(info.activityInfo.applicationInfo.loadIcon(context.getPackageManager()));
                }
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(18);

                layout.addView(icon, GdiUtils.layoutParam(GdiUtils.realDp(context, 40), GdiUtils.realDp(context, 40)));
                layout.addView(textView);
                return layout;
            }
        }
    }
}
