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

public class TextInputConfig extends ListAdapterItem {

    private final String title;
    private final String configKey;
    private String defaultValue;
    private String currentValue;
    private ImageView iconImageView;
    private TextView summeryView;
    private TextView titleView;
    private SharedPreferences preferences;
    private final String helpText;

    public TextInputConfig(String title, String configKey, String defaultValue, String helpText) {
        super();
        this.title = title;
        this.configKey = configKey;
        this.defaultValue = defaultValue;
        this.helpText = helpText;
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

        iconImageView = new ImageView(context);
        iconImageView.setImageResource(R.drawable.ic_menu_edit);

        summeryView = new TextView(context);
        summeryView.setText(currentValue);
        summeryView.setTextColor(Color.BLACK);
        summeryView.setTextSize(16);
        summeryView.setGravity(Gravity.TOP);

        layout.addView(titleView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        layout.addView(summeryView, new LayoutParams(LayoutParams.FILL_PARENT, GdiUtils.realDp(context, 30)));

        LayoutParams params = new LayoutParams(GdiUtils.realDp(context, 40), GdiUtils.realDp(context, 40));
        params.setMargins(GdiUtils.realDp(context, 7), 0, GdiUtils.realDp(context, 7), 0);
        params.gravity = Gravity.CENTER;

        rowLayout.addView(iconImageView, params);
        rowLayout.addView(layout);

        return rowLayout;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view) {
        TextInputActivity activity = new TextInputActivity(parent.getContext());
        activity.show();
    }

    public class TextInputActivity extends Dialog {

        private Context context;
        private EditText editText;

        public TextInputActivity(Context context) {
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

            TextView textView = new TextView(context);
            textView.setTextSize(18);
            textView.setTextColor(Color.WHITE);
            textView.setText(helpText);
            layout.addView(textView);

            editText = new EditText(context);
            editText.setText(currentValue);
            layout.addView(editText, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            layout.addView(new TextView(context));

            LinearLayout buttonLayout = new LinearLayout(context);
            buttonLayout.setOrientation(LinearLayout.VERTICAL);
            buttonLayout.setGravity(Gravity.CENTER);

            Button okButton = new Button(context);
            okButton.setText(LocaleUtil.locale("決定", "OK"));
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentValue = editText.getText().toString();
                    Editor editor = preferences.edit();
                    editor.putString(configKey, currentValue);
                    editor.commit();
                    cancel();
                    summeryView.setText(String.valueOf(currentValue));
                }
            });
            buttonLayout.addView(okButton);
            layout.addView(buttonLayout);
            return layout;
        }
    }
}
