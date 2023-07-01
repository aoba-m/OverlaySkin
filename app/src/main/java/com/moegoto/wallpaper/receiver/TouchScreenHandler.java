package com.moegoto.wallpaper.receiver;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;

import com.moegoto.wallpaper.Configs;
import com.moegoto.wallpaper.util.StringUtils;

public class TouchScreenHandler {

    private final Configs conf;
    private final WallpaperService service;

    public TouchScreenHandler(WallpaperService service, Configs conf) {
        this.service = service;
        this.conf = conf;
    }

    private long lastTapStartTime = 0;
    private long lastTapHoldDelta = 0;

    public void onTouchEvent(int x, int y) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTapStartTime < 400) {
            if (StringUtils.isNotEmpty(conf.doubleTapActivityClass) && StringUtils.isNotEmpty(conf.doubleTapActivityName)) {
                execApplication(conf.doubleTapActivityClass, conf.doubleTapActivityName);
            }
        }
        lastTapStartTime = currentTime;
    }

    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            long delta = event.getEventTime() - event.getDownTime();
            if (delta < 200 && lastTapHoldDelta < 200 && event.getEventTime() - lastTapStartTime < 400) {
                if (StringUtils.isNotEmpty(conf.doubleTapActivityClass) && StringUtils.isNotEmpty(conf.doubleTapActivityName)) {
                    execApplication(conf.doubleTapActivityClass, conf.doubleTapActivityName);
                    //conf.getPowerAmpHandler().nextTrack(service);
                }
            }
            lastTapStartTime = event.getEventTime();
            lastTapHoldDelta = delta;
        }
    }

    private void execApplication(String doubleTapActivityClass, String doubleTapActivityName) {
        try {
            PackageManager packageManager = service.getApplicationContext().getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName componentName = new ComponentName(doubleTapActivityClass, doubleTapActivityName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(componentName);
            boolean existsIntent = 0 < packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size();
            if (existsIntent) {
                service.startActivity(intent);
                return;
            }
        } catch (Exception e) {
            Log.e("OverlaySkin", e.getMessage(), e);
        }
    }
}
