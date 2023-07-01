package com.moegoto.wallpaper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.moegoto.wallpaper.Configs;
import com.moegoto.wallpaper.OverlaySkinService;
import com.moegoto.wallpaper.WallpaperRenderer;

public class BatteryEventReceiver extends BroadcastReceiver {

    private Configs conf;
    private final WallpaperRenderer renderer;

    public BatteryEventReceiver(Configs conf, WallpaperRenderer renderer) {
        this.conf = conf;
        this.renderer = renderer;
    }

    public void register(OverlaySkinService service) {
        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        service.registerReceiver(this, batteryFilter);
    }

    public void unregister(OverlaySkinService service) {
        service.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
            conf.setBatteryLevel(intent.getIntExtra("level", 0));
            conf.batteryScale = intent.getIntExtra("scale", 0);
            conf.batteryVoltage = intent.getIntExtra("voltage", 0);
            conf.batteryTemp = intent.getIntExtra("temperature", 0);

            // プラグの種別
            int plugged = intent.getIntExtra("plugged", 0);
            conf.batteryPlugged = "";
            if (plugged == BatteryManager.BATTERY_PLUGGED_AC) {
                conf.batteryPlugged = "AC";
            }
            if (plugged == BatteryManager.BATTERY_PLUGGED_USB) {
                conf.batteryPlugged = "USB";
            }

            // ステータス
            int status = intent.getIntExtra("status", 0);
            conf.setBatteryStatus("");
            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                conf.setBatteryStatus("CHARGING");
            }
            if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                conf.setBatteryStatus("");
            }
            if (status == BatteryManager.BATTERY_STATUS_FULL) {
                conf.setBatteryStatus("FULL");
            }

            // 健康状態
            int health = intent.getIntExtra("health", 0);
            conf.batteryHealth = "";
            if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                conf.setBatteryStatus("OVERHEAT");
            }
            if (health == BatteryManager.BATTERY_HEALTH_DEAD) {
                conf.setBatteryStatus("DEAD");
            }
            if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
                conf.setBatteryStatus("OVER VOLTAGE");
            }

            // 表示内容の反映
            renderer.lastUpdateTime = 0;
            renderer.lastDate = "";
        }
    }
}
