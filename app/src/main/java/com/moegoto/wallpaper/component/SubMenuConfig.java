package com.moegoto.wallpaper.component;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.moegoto.wallpaper.util.GdiUtils;
import com.moegoto.wallpaper.util.ListAdapterItem;

public class SubMenuConfig extends ListAdapterItem {

    private final String title;
    private TextView titleView;
    private ImageView icon;
    private final Class<? extends ListActivity> subMenu;
    protected Context context;
    private final int iconResource;

    public SubMenuConfig(String title, Class<? extends ListActivity> subMenu) {
        super();
        this.title = title;
        this.subMenu = subMenu;
        this.iconResource = 0;
    }

    public SubMenuConfig(String title, Class<? extends ListActivity> subMenu, int iconResource) {
        super();
        this.title = title;
        this.subMenu = subMenu;
        this.iconResource = iconResource;
    }

    @Override
    public View getView(Context context, View convertView, ViewGroup parent) {
        this.context = context;
        LinearLayout layout = new LinearLayout(context);

        layout.setOrientation(LinearLayout.HORIZONTAL);
        titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(18);
        titleView.setGravity(Gravity.CENTER_VERTICAL);

        icon = new ImageView(context);
        if (iconResource != 0) {
            icon.setImageResource(iconResource);
        }

        LayoutParams params = new LayoutParams(GdiUtils.realDp(context, 40), GdiUtils.realDp(context, 40));
        params.setMargins(GdiUtils.realDp(context, 7), 0, GdiUtils.realDp(context, 7), 0);
        params.gravity = Gravity.CENTER_VERTICAL;

        layout.addView(icon, params);
        layout.addView(titleView, new LayoutParams(LayoutParams.FILL_PARENT, GdiUtils.realDp(context, 54)));

        return layout;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view) {
        if (context != null) {
            Intent intent = new Intent(context, subMenu);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
