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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.moegoto.wallpaper.OverlaySkinService;
import com.moegoto.wallpaper.util.CalendarUtils;
import com.moegoto.wallpaper.util.GdiUtils;
import com.moegoto.wallpaper.util.ListAdapter;
import com.moegoto.wallpaper.util.ListAdapterItem;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CalendarSelectConfig extends ListAdapterItem {

    private final String title;
    private final String configKey;
    private List<CalendarUtils.CalendarRecord> calendarRecords;
    private Set<String> selectedValue;
    private TextView titleView;
    private SharedPreferences preferences;
    private ImageView iconImageView;

    public CalendarSelectConfig(String title, String configKey) {
        super();
        this.title = title;
        this.configKey = configKey;
    }

    @Override
    public View getView(Context context, View convertView, ViewGroup parent) {
        preferences = context.getSharedPreferences(OverlaySkinService.SHARED_PREFS_NAME, 0);
        String configValue = preferences.getString(configKey, null);
        calendarRecords = CalendarUtils.getCalendars(context);

        selectedValue = new HashSet<String>();
        if (configValue == null) {
            for (CalendarUtils.CalendarRecord record : calendarRecords)
                selectedValue.add(record._id);
        } else {
            for (String value : configValue.split(","))
                selectedValue.add(value);
        }
        LinearLayout layout = new LinearLayout(context);

        layout.setOrientation(LinearLayout.HORIZONTAL);

        iconImageView = new ImageView(context);
        iconImageView.setImageResource(R.drawable.ic_menu_my_calendar);

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
        CalendarPreferenceActivity activity = new CalendarPreferenceActivity(parent.getContext());
        activity.show();
    }

    public class CalendarPreferenceActivity extends Dialog {
        private final Context context;
        private ListView itemView;
        private ListAdapter adapter;

        public CalendarPreferenceActivity(Context context) {
            super(context);
            this.context = context;
            setTitle(title);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            setContentView(createView());
            getWindow().getAttributes().gravity = Gravity.TOP;
        }

        private View createView() {
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            adapter = new ListAdapter(context);
            for (CalendarUtils.CalendarRecord record : calendarRecords) {
                adapter.add(new CalendarItem(record));
            }

            itemView = new ListView(context);
            itemView.setAdapter(adapter);
            itemView.setOnItemClickListener(adapter);

            layout.addView(itemView);
            return layout;
        }

        private class CalendarItem extends ListAdapterItem {
            private final CalendarUtils.CalendarRecord record;
            private CheckBox check;

            public CalendarItem(CalendarUtils.CalendarRecord record) {
                this.record = record;
            }

            @Override
            public View getView(Context context, View convertView, ViewGroup parent) {
                LinearLayout layout = new LinearLayout(context);
                TextView textView = new TextView(context);
                textView.setText(record.getDisplayName());

                check = new CheckBox(context);
                check.setChecked(selectedValue.contains(record.get_id()));
                check.setClickable(false);
                check.setFocusable(false);
                layout.addView(check);
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
                check.setChecked(!check.isChecked());
                adapter.notifyDataSetChanged();
                onCheckChange(check.isChecked());
            }

            public void onCheckChange(boolean isChecked) {
                if (isChecked) {
                    selectedValue.add(record.get_id());
                } else {
                    selectedValue.remove(record.get_id());
                }
                // ここで保存してしまう
                StringBuilder sb = new StringBuilder();
                for (Iterator<String> it = selectedValue.iterator(); it.hasNext();) {
                    sb.append(it.next()).append(",");
                }
                Editor editor = preferences.edit();
                editor.putString(configKey, sb.toString());
                editor.commit();
            }
        }
    }
}
