package com.moegoto.wallpaper.config.activity;

import android.content.Context;

import com.moegoto.wallpaper.component.ActivitySelectConfig;
import com.moegoto.wallpaper.component.BooleanConfig;
import com.moegoto.wallpaper.component.ConfigSeparator;
import com.moegoto.wallpaper.component.SliderConfig;
import com.moegoto.wallpaper.util.ListAdapter;
import com.moegoto.wallpaper.util.LocaleUtil;

public class HomeScreenConfigActivity extends BaseListActivity {

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        ListAdapter adapter = new ListAdapter(ctx);

        // ------ホームスクリーンの設定       
        adapter.add(new ConfigSeparator(LocaleUtil.locale("ホームスクリーンの設定", "Home screen Setting")));
        adapter.add(new BooleanConfig(LocaleUtil.locale("背景画像をスクロールさせる", "Scroll background image"), "UseWideScroll", false));
        adapter.add(new BooleanConfig(LocaleUtil.locale("時計/予定を特定のスクリーンに固定する", "Clock/schedule scroll lock"), "HomeScreenLock", false));
        adapter.add(new SliderConfig(LocaleUtil.locale("ホームスクリーン数の設定", "Set home screent count"), "HomeScreenCount", 0, 10));
        adapter.add(new SliderConfig(LocaleUtil.locale("時計/予定の固定スクリーンの設定", "Set clock/schedule lock index"), "HomeScreenLockIndex", 1, 9, 1));

        adapter.add(new ConfigSeparator(LocaleUtil.locale("ダブルタップ起動設定", "Double tap launcher Setting")));
        adapter.add(new ActivitySelectConfig(LocaleUtil.locale("ダブルタップで起動するアプリの設定", "Select app on double tap"), "DoubleTapActivity", ""));
        //        adapter.add(new BooleanConfig(LocaleUtil.locale("ウィジェット上でのダブルタップを有効にする", "Allow double tap on widgets"), "AllowWidgetDoubleTap", true));

        adapter.add(new ConfigSeparator(LocaleUtil.locale("ロック画面設定", "Lock screen Setting")));
        adapter.add(new BooleanConfig(LocaleUtil.locale("ロック画面で時計/カレンダーを表示する", "Display Clock/Schedule on Lock screen"), "ClockOnLockScreen", false));

        getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(adapter);
    };
}
