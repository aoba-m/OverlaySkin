package com.moegoto.wallpaper;

import android.app.KeyguardManager;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.moegoto.wallpaper.receiver.BatteryEventReceiver;
import com.moegoto.wallpaper.receiver.ScreenEventHandler;
import com.moegoto.wallpaper.receiver.TouchScreenHandler;

public class OverlaySkinService extends WallpaperService {

    public static final String SHARED_PREFS_NAME = "com.moegoto.overlay.skin";

    private final Handler handler = new Handler();

    public static boolean disableScreenUpdatable = false;

    @Override
    public Engine onCreateEngine() {
        startForeground(0, new Notification());
        return new WallpaperEngine();
    }

    private class WallpaperEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {
        private boolean visible;
        private WallpaperRenderer wallpaperRenderer;
        private ScreenEventHandler screenEventHandler;
        private BatteryEventReceiver batteryEventHandler;
        private SharedPreferences prefs;
        TouchScreenHandler touchScreenHandler;

        private Configs conf = new Configs(getApplicationContext());

        private final Runnable drawThread = new Runnable() {
            @Override
            public void run() {
                drawFrame(false);
            }
        };

        public WallpaperEngine() {
            wallpaperRenderer = new WallpaperRenderer(getApplicationContext(), conf);

            // スクリーンイベント
            screenEventHandler = new ScreenEventHandler(conf, wallpaperRenderer);
            screenEventHandler.register(OverlaySkinService.this);

            // バッテリー関連のイベント
            batteryEventHandler = new BatteryEventReceiver(conf, wallpaperRenderer);
            batteryEventHandler.register(OverlaySkinService.this);

            // タッチスクリーン
            touchScreenHandler = new TouchScreenHandler(OverlaySkinService.this, conf);

            prefs = OverlaySkinService.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
            prefs.registerOnSharedPreferenceChangeListener(this);
            onSharedPreferenceChanged(prefs, null);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
        }

        private int screenWidth = 0;
        private int screenHeight = 0;

        @Override
        public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
            if (action.equals("android.wallpaper.tap")) {
                touchScreenHandler.onTouchEvent(x, y);
            }
            return super.onCommand(action, x, y, z, extras, resultRequested);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawThread);
            unregisterReceiver(screenEventHandler);
            unregisterReceiver(batteryEventHandler);
            stopForeground(true);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display disp = wm.getDefaultDisplay();
            Point size = new Point();
            disp.getRealSize(size);

            screenWidth = size.x;
            screenHeight = size.y;

            super.onSurfaceChanged(holder, format, width, height);
            onSharedPreferenceChanged(prefs, null);
            drawFrame(false);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            visible = false;
            handler.removeCallbacks(drawThread);
        }

        @Override
        public void onDesiredSizeChanged(int desiredWidth, int desiredHeight) {
            conf.desiredWidth = desiredWidth;
            conf.desiredHeight = desiredHeight;
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) {
            conf.onOffsetsChanged(xOffset, yOffset);
            drawFrame(true);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                drawFrame(false);
            } else {
                handler.removeCallbacks(drawThread);
            }
        }

        private synchronized void drawFrame(boolean scrolling) {

            long drawStartTime = System.currentTimeMillis();
            final SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas(new Rect(0, 0, screenWidth, screenHeight));
                if (canvas != null) {
                    boolean screenLocking = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE)).inKeyguardRestrictedInputMode();
                    wallpaperRenderer.draw(drawStartTime, canvas, screenLocking, scrolling, isPreview());
                }
            } finally {
                if (canvas != null) {
                    try {
                        holder.unlockCanvasAndPost(canvas);
                    } catch (IllegalArgumentException e) {
                        Log.e("OverlaySkin", e.getMessage(), e);
                    }
                }
            }
            handler.removeCallbacks(drawThread);
            if (visible) {
                // 次フレームまでの時間を計算する
                long drawTime = drawStartTime % 1000;
                handler.postDelayed(drawThread, 1000 - drawTime);
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (screenWidth == 0 || screenHeight == 0) {
                return;
            }
            if (!disableScreenUpdatable) {
                conf.updateSharedPreferences(sharedPreferences);
                wallpaperRenderer.init(getResources(), screenWidth, screenHeight);
            }
        }
    }
}
