package com.moegoto.wallpaper.component;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.moegoto.wallpaper.util.GdiUtils;
import com.moegoto.wallpaper.util.ListAdapterItem;

public abstract class AbstractConfig extends ListAdapterItem {

    private final String title;
    private ImageView summeryView;
    private TextView titleView;
    private final int resourceId;

    public AbstractConfig(String title) {
        super();
        this.title = title;
        this.resourceId = 0;
    }

    public AbstractConfig(String title, int resourceId) {
        super();
        this.title = title;
        this.resourceId = resourceId;
    }

    @Override
    public View getView(Context context, View convertView, ViewGroup parent) {
        LinearLayout layout = new LinearLayout(context);

        layout.setOrientation(LinearLayout.HORIZONTAL);
        titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(18);
        titleView.setGravity(Gravity.CENTER_VERTICAL);

        summeryView = new ImageView(context);
        if (resourceId != 0) {
            summeryView.setImageResource(resourceId);
        }

        LayoutParams params = new LayoutParams(GdiUtils.realDp(context, 40), GdiUtils.realDp(context, 40));
        params.gravity = Gravity.CENTER;
        params.setMargins(GdiUtils.realDp(context, 7), 0, GdiUtils.realDp(context, 7), 0);
        layout.addView(summeryView, params);
        layout.addView(titleView, new LayoutParams(LayoutParams.FILL_PARENT, GdiUtils.realDp(context, 54)));

        return layout;
    }

}
