package com.moegoto.wallpaper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.moegoto.wallpaper.Configs;
import com.moegoto.wallpaper.OverlaySkinService;
import com.moegoto.wallpaper.WallpaperRenderer;

public class ScreenEventHandler extends BroadcastReceiver {

    private final Configs conf;
    private final WallpaperRenderer renderer;

    public ScreenEventHandler(Configs conf, WallpaperRenderer renderer) {
        this.renderer = renderer;
        this.conf = conf;
    }

    public void register(OverlaySkinService service) {
        IntentFilter screenIntentFilter = new IntentFilter();
        screenIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        service.registerReceiver(this, screenIntentFilter);
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // 画像を切り替える
            conf.slideShowIndex++;
            renderer.reloadBackgroundImage();
        }
    }

    public void unregister(OverlaySkinService service) {
        service.unregisterReceiver(this);
    }

}
