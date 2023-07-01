package com.moegoto.wallpaper.config.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;

import com.moegoto.wallpaper.component.ColorSelectConfig;
import com.moegoto.wallpaper.component.ConfigSeparator;
import com.moegoto.wallpaper.component.ImageSelectConfig;
import com.moegoto.wallpaper.util.ListAdapter;
import com.moegoto.wallpaper.util.LocaleUtil;

public class BackgroundConfigActivity extends BaseListActivity {

    public BackgroundConfigActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        ListAdapter adapter = new ListAdapter(ctx);

        adapter.add(new ConfigSeparator(LocaleUtil.locale("背景画像の設定", "Background Settings")));
        adapter.add(new ImageSelectConfig(LocaleUtil.locale("縦画面背景の選択", "Portrait bg image"), "FileSelect", Environment.getExternalStorageDirectory().getAbsolutePath()));
        adapter.add(new ImageSelectConfig(LocaleUtil.locale("横画面背景の選択", "Landscape bg image"), "FileSelect2", Environment.getExternalStorageDirectory().getAbsolutePath()));
        adapter.add(new ColorSelectConfig(LocaleUtil.locale("背景色の指定", "Backgound color"), "BackgoundColor", Color.argb(255, 0, 0, 0)));

        getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(adapter);
    }
}
