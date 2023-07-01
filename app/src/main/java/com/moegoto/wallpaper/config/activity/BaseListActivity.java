package com.moegoto.wallpaper.config.activity;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class BaseListActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setBackgroundColor(Color.argb(200, 255, 255, 255));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            //getWindow().getDecorView().setBackgroundColor(Color.argb(0, 0, 0, 0));
            getListView().setVisibility(View.VISIBLE);
        } else {
            getListView().setVisibility(View.GONE);
        }
    }
}
