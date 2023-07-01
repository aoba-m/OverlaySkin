package com.moegoto.wallpaper.config.activity;

import android.Manifest;
import android.R;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.moegoto.wallpaper.component.AbstractConfig;
import com.moegoto.wallpaper.component.SubMenuConfig;
import com.moegoto.wallpaper.util.ListAdapter;
import com.moegoto.wallpaper.util.LocaleUtil;

import java.util.Arrays;

public class MainConfigActivity extends BaseListActivity {

    public MainConfigActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        ListAdapter adapter = new ListAdapter(ctx);

        adapter.add(new SubMenuConfig(LocaleUtil.locale("背景画像の設定", "Background Setting"), BackgroundConfigActivity.class, R.drawable.ic_menu_gallery));
        adapter.add(new SubMenuConfig(LocaleUtil.locale("表示内容設定", "Contents Setting"), ContentsConfigActivity.class, R.drawable.ic_menu_my_calendar));
        adapter.add(new SubMenuConfig(LocaleUtil.locale("画面調整", "Display Setting"), DisplayConfigActivity.class, R.drawable.ic_menu_crop));
        adapter.add(new SubMenuConfig(LocaleUtil.locale("ホームスクリーン設定", "Home screen Setting"), HomeScreenConfigActivity.class, R.drawable.ic_menu_slideshow));
        adapter.add(new SubMenuConfig(LocaleUtil.locale("設定の保存 & 復元", "Save & Restore"), SaveRestoreConfigActivity.class, R.drawable.ic_menu_save));

        adapter.add(new SubMenuConfig(LocaleUtil.locale("ファイル権限設定", "File Access Permission"), null, R.drawable.ic_menu_manage) {
            @Override
            public void onItemClick(AdapterView<?> parent, View view) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        adapter.add(new SubMenuConfig(LocaleUtil.locale("カレンダー権限設定", "Calender Permission"), null, R.drawable.ic_menu_manage) {
            @Override
            public void onItemClick(AdapterView<?> parent, View view) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(adapter);
    }
}
