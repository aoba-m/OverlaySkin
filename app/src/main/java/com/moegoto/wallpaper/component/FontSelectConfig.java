package com.moegoto.wallpaper.component;

import java.io.File;
import java.io.FileFilter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.moegoto.wallpaper.OverlaySkinService;
import com.moegoto.wallpaper.util.GdiUtils;
import com.moegoto.wallpaper.util.ListAdapter;
import com.moegoto.wallpaper.util.ListAdapterItem;
import com.moegoto.wallpaper.util.LocaleUtil;

public class FontSelectConfig extends ListAdapterItem {

    private final String title;
    private final String configKey;
    private String defaultValue;
    private String currentValue;
    private TextView summeryView;
    private TextView titleView;
    private SharedPreferences preferences;

    public FontSelectConfig(String title, String configKey, String defaultValue) {
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

        layout.setOrientation(LinearLayout.VERTICAL);
        titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(18);
        titleView.setGravity(Gravity.BOTTOM);
        titleView.setPadding(GdiUtils.realDp(context, 54), 0, 0, 0);

        summeryView = new TextView(context);
        summeryView.setText(currentValue);
        summeryView.setTextColor(Color.BLACK);
        summeryView.setTextSize(16);
        summeryView.setGravity(Gravity.TOP);
        summeryView.setPadding(GdiUtils.realDp(context, 54), 0, 0, 0);

        layout.addView(titleView, new LayoutParams(LayoutParams.FILL_PARENT, GdiUtils.realDp(context, 30)));
        layout.addView(summeryView, new LayoutParams(LayoutParams.FILL_PARENT, GdiUtils.realDp(context, 30)));

        return layout;
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

            TextView noticeText1View = new TextView(context);
            noticeText1View.setTextColor(Color.WHITE);
            noticeText1View.setText(LocaleUtil.locale("フォントを「/sdcard/fonts/」以下に配置してください", "Please put Truetype font.  /sdcard/fonts/"));
            layout.addView(noticeText1View, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

            TextView noticeText2View = new TextView(context);
            noticeText2View.setTextColor(Color.WHITE);
            noticeText2View.setText(LocaleUtil.locale("※SDカード上のフォントを使用している場合、SDカードをアンマウントするとライブ壁紙が解除されます。",
                    "Notice: If you unmount sd-card , Live wallpaper is unloaded."));
            layout.addView(noticeText2View, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

            listView = new ListView(context);
            File[] fontfiles = new File("/sdcard/fonts").listFiles(new FileFilter() {

                @Override
                public boolean accept(File file) {
                    return file.getName().toLowerCase().endsWith(".ttf");
                }
            });

            listAdapter = new ListAdapter(context);
            listAdapter.add(new FileItem(null));
            if (fontfiles != null) {
                for (File file : fontfiles) {
                    listAdapter.add(new FileItem(file));
                }
            }
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new ListViewSelectListener());
            layout.addView(listView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);// dpToPixel(context, 300));
            return layout;
        }

        private class ListViewSelectListener implements OnItemClickListener {
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                FileItem item = (FileItem) listAdapter.getItem(position);
                if (item.file == null) {
                    currentValue = "";
                } else {
                    currentValue = item.file.getAbsolutePath();
                }
                Editor editor = preferences.edit();
                editor.putString(configKey, currentValue);
                editor.commit();
                FontSelectActivity.this.cancel();
                summeryView.setText(currentValue);
            }
        }

        private class FileItem extends ListAdapterItem {

            private File file;

            public FileItem(File file) {
                this.file = file;
            }

            @Override
            public View getView(Context context, View convertView, ViewGroup parent) {
                TextView textView = new TextView(context);
                if (file == null) {
                    textView.setText("System Font");
                    textView.setTypeface(Typeface.DEFAULT);
                } else {
                    textView.setText(file.getName());
                    try {
                        Typeface typeface = Typeface.createFromFile(file.getAbsoluteFile());
                        if (typeface != null)
                            textView.setTypeface(typeface);
                    } catch (Exception e) {
                    }
                }
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(27);
                return textView;
            }
        }
    }

}
