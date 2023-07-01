package com.moegoto.wallpaper.component;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moegoto.wallpaper.util.ListAdapterItem;

public class ConfigSeparator extends ListAdapterItem {

    private final String text;

    public ConfigSeparator(String text) {
        this.text = text;
    }

    @Override
    public View getView(Context context, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setBackgroundColor(Color.BLACK);
        textView.setTextColor(Color.WHITE);
        return textView;
    }
}
