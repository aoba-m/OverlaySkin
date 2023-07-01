package com.moegoto.wallpaper.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;

import com.moegoto.wallpaper.Constants;
import com.moegoto.wallpaper.OverlaySkinService;
import com.moegoto.wallpaper.util.GdiUtils;
import com.moegoto.wallpaper.util.NumberUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class ConfigrationIO {

    private void propToPrefs(Editor edit, Properties props, String key, String defaultValue) {
        edit.putString(key, props.getProperty(key, defaultValue));
    }

    private void propToPrefs(Editor edit, Properties props, String key, int defaultValue) {
        edit.putInt(key, NumberUtils.parseInt(props.getProperty(key, String.valueOf(defaultValue)), defaultValue));
    }

    private void propToPrefs(Editor edit, Properties props, String key, boolean defaultValue) {
        String value = props.getProperty(key, String.valueOf(defaultValue));
        edit.putBoolean(key, "true".equals(value));
    }

    private void prefsToProp(SharedPreferences edit, Properties props, String key, String defaultValue) {
        props.setProperty(key, edit.getString(key, defaultValue));
    }

    private void prefsToProp(SharedPreferences edit, Properties props, String key, int defaultValue) {
        props.setProperty(key, String.valueOf(edit.getInt(key, defaultValue)));
    }

    private void prefsToProp(SharedPreferences edit, Properties props, String key, boolean defaultValue) {
        props.setProperty(key, String.valueOf(edit.getBoolean(key, defaultValue)));
    }

    public void importConfig(File file, SharedPreferences preferences, Context ctx) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
        Properties props = new Properties();
        props.loadFromXML(new FileInputStream(file));
        Editor pref = preferences.edit();

        OverlaySkinService.disableScreenUpdatable = true;
        try {
            propToPrefs(pref, props, "DoubleTapActivity", "");
            propToPrefs(pref, props, "ForegroundColor", Constants.DEFAULT_FOREGROUND_COLOR);
            propToPrefs(pref, props, "ScreenBackgroundColor", Constants.DEFAULT_SCREEN_FILTER_COLOR);
            propToPrefs(pref, props, "SidesBackgroundColor", Constants.DEFAULT_SIDES_FILTER_COLOR);
            propToPrefs(pref, props, "SatForegroundColor", Color.argb(255, 80, 80, 255));
            propToPrefs(pref, props, "SunForegroundColor", Color.argb(255, 255, 80, 80));
            propToPrefs(pref, props, "TextShadowColor", Color.argb(255, 255, 255, 255));
            propToPrefs(pref, props, "FileSelect", "");
            propToPrefs(pref, props, "FileSelect2", "");
            propToPrefs(pref, props, "BackgoundColor", Color.argb(255, 0, 0, 0));
            propToPrefs(pref, props, "CalendarOnDoubleTap", true);
            propToPrefs(pref, props, "HideClock", false);
            propToPrefs(pref, props, "HideClockSecond", false);
            propToPrefs(pref, props, "Clock12HourFormat", false);
            propToPrefs(pref, props, "UseWideScroll", false);
            propToPrefs(pref, props, "TopMargin", (int) GdiUtils.dp(ctx, 40));
            propToPrefs(pref, props, "LeftMargin", (int) GdiUtils.dp(ctx, 2));
            propToPrefs(pref, props, "RightMargin", (int) GdiUtils.dp(ctx, 2));
            propToPrefs(pref, props, "BottomMargin", (int) GdiUtils.dp(ctx, 40));
            propToPrefs(pref, props, "LandTopMargin", (int) GdiUtils.dp(ctx, 20));
            propToPrefs(pref, props, "LandLeftMargin", (int) GdiUtils.dp(ctx, 2));
            propToPrefs(pref, props, "LandRightMargin", (int) GdiUtils.dp(ctx, 50));
            propToPrefs(pref, props, "LandBottomMargin", (int) GdiUtils.dp(ctx, 2));
            propToPrefs(pref, props, "ClockFontSize", (int) GdiUtils.dp(ctx, Constants.DEFAULT_CLOCK_TIME_FONTSIZE));
            propToPrefs(pref, props, "DateFontSize", (int) GdiUtils.dp(ctx, 19));
            propToPrefs(pref, props, "CalendarFontSize", (int) GdiUtils.dp(ctx, Constants.DEFAULT_CALENDAR_DAY_FONTSIZE));
            propToPrefs(pref, props, "EventFontSize", (int) GdiUtils.dp(ctx, Constants.DEFAULT_CALENDAR_EVENT_FONTSIZE));
            propToPrefs(pref, props, "FontPath", "");
            propToPrefs(pref, props, "FontBold", Constants.DEFAULT_FONT_BOLD);
            propToPrefs(pref, props, "EventAsMark", false);
            propToPrefs(pref, props, "DisplayEventStartTime", true);
            propToPrefs(pref, props, "WeekDayName0", Constants.WEEKDAY_NAME[0]);
            propToPrefs(pref, props, "WeekDayName1", Constants.WEEKDAY_NAME[1]);
            propToPrefs(pref, props, "WeekDayName2", Constants.WEEKDAY_NAME[2]);
            propToPrefs(pref, props, "WeekDayName3", Constants.WEEKDAY_NAME[3]);
            propToPrefs(pref, props, "WeekDayName4", Constants.WEEKDAY_NAME[4]);
            propToPrefs(pref, props, "WeekDayName5", Constants.WEEKDAY_NAME[5]);
            propToPrefs(pref, props, "WeekDayName6", Constants.WEEKDAY_NAME[6]);
            propToPrefs(pref, props, "HomeScreenLock", false);
            propToPrefs(pref, props, "HomeScreenCount", 0);
            propToPrefs(pref, props, "HomeScreenLockIndex", 1);
            propToPrefs(pref, props, "TextEffectType", Constants.DEFAULT_EFFECT_TYPE);
            propToPrefs(pref, props, "TextEffectSize", 4);
            propToPrefs(pref, props, "HolidayKeyword", "");
            propToPrefs(pref, props, "DisplayBatteryInfo", true);
            propToPrefs(pref, props, "BatteryFontSize", (int) GdiUtils.dp(ctx, 15));
            propToPrefs(pref, props, "SwapPosition", false);
            propToPrefs(pref, props, "SelectCalendar", "");
            propToPrefs(pref, props, "ClockSmallSecond", Constants.DEFAULT_CLOCK_SMALL_SECOND);
            propToPrefs(pref, props, "ClockOnLockScreen", false);
            propToPrefs(pref, props, "AllowWidgetDoubleTap", true);

        } finally {
            OverlaySkinService.disableScreenUpdatable = false;
            pref.putString("hoge", "");
        }
        pref.commit();
    }

    public void exportConfig(File file, SharedPreferences pref, Context ctx) throws FileNotFoundException, IOException {
        Properties props = new Properties();
        prefsToProp(pref, props, "DoubleTapActivity", "");
        prefsToProp(pref, props, "ForegroundColor", Constants.DEFAULT_FOREGROUND_COLOR);
        prefsToProp(pref, props, "ScreenBackgroundColor", Constants.DEFAULT_SCREEN_FILTER_COLOR);
        prefsToProp(pref, props, "SidesBackgroundColor", Constants.DEFAULT_SIDES_FILTER_COLOR);
        prefsToProp(pref, props, "SatForegroundColor", Color.argb(255, 80, 80, 255));
        prefsToProp(pref, props, "SunForegroundColor", Color.argb(255, 255, 80, 80));
        prefsToProp(pref, props, "TextShadowColor", Color.argb(255, 255, 255, 255));
        prefsToProp(pref, props, "FileSelect", "");
        prefsToProp(pref, props, "FileSelect2", "");
        prefsToProp(pref, props, "BackgoundColor", Color.argb(255, 0, 0, 0));
        prefsToProp(pref, props, "CalendarOnDoubleTap", true);
        prefsToProp(pref, props, "HideClock", false);
        prefsToProp(pref, props, "HideClockSecond", false);
        prefsToProp(pref, props, "Clock12HourFormat", false);
        prefsToProp(pref, props, "UseWideScroll", false);
        prefsToProp(pref, props, "PortCalendarTopMargin", (int) GdiUtils.dp(ctx, 40));
        prefsToProp(pref, props, "PortLeftMargin", (int) GdiUtils.dp(ctx, 2));
        prefsToProp(pref, props, "PortRightMargin", (int) GdiUtils.dp(ctx, 2));
        prefsToProp(pref, props, "PortCalendarBottomMargin", (int) GdiUtils.dp(ctx, 40));
        prefsToProp(pref, props, "PortClockBottomMargin", (int) GdiUtils.dp(ctx, 40));
        prefsToProp(pref, props, "LandCalendarTopMargin", (int) GdiUtils.dp(ctx, 20));
        prefsToProp(pref, props, "LandLeftMargin", (int) GdiUtils.dp(ctx, 2));
        prefsToProp(pref, props, "LandRightMargin", (int) GdiUtils.dp(ctx, 50));
        prefsToProp(pref, props, "LandCalendarBottomMargin", (int) GdiUtils.dp(ctx, 2));
        prefsToProp(pref, props, "LandClockBottomMargin", (int) GdiUtils.dp(ctx, 2));
        prefsToProp(pref, props, "ClockFontSize", (int) GdiUtils.dp(ctx, Constants.DEFAULT_CLOCK_TIME_FONTSIZE));
        prefsToProp(pref, props, "DateFontSize", (int) GdiUtils.dp(ctx, 19));
        prefsToProp(pref, props, "CalendarFontSize", (int) GdiUtils.dp(ctx, Constants.DEFAULT_CALENDAR_DAY_FONTSIZE));
        prefsToProp(pref, props, "EventFontSize", (int) GdiUtils.dp(ctx, Constants.DEFAULT_CALENDAR_EVENT_FONTSIZE));
        prefsToProp(pref, props, "FontPath", "");
        prefsToProp(pref, props, "FontBold", Constants.DEFAULT_FONT_BOLD);
        prefsToProp(pref, props, "EventAsMark", false);
        prefsToProp(pref, props, "DisplayEventStartTime", true);
        prefsToProp(pref, props, "WeekDayName0", Constants.WEEKDAY_NAME[0]);
        prefsToProp(pref, props, "WeekDayName1", Constants.WEEKDAY_NAME[1]);
        prefsToProp(pref, props, "WeekDayName2", Constants.WEEKDAY_NAME[2]);
        prefsToProp(pref, props, "WeekDayName3", Constants.WEEKDAY_NAME[3]);
        prefsToProp(pref, props, "WeekDayName4", Constants.WEEKDAY_NAME[4]);
        prefsToProp(pref, props, "WeekDayName5", Constants.WEEKDAY_NAME[5]);
        prefsToProp(pref, props, "WeekDayName6", Constants.WEEKDAY_NAME[6]);
        prefsToProp(pref, props, "HomeScreenLock", false);
        prefsToProp(pref, props, "HomeScreenCount", 0);
        prefsToProp(pref, props, "HomeScreenLockIndex", 1);
        prefsToProp(pref, props, "TextEffectType", Constants.DEFAULT_EFFECT_TYPE);
        prefsToProp(pref, props, "TextEffectSize", 4);
        prefsToProp(pref, props, "HolidayKeyword", "");
        prefsToProp(pref, props, "DisplayBatteryInfo", true);
        prefsToProp(pref, props, "BatteryFontSize", (int) GdiUtils.dp(ctx, 15));
        prefsToProp(pref, props, "SwapPosition", false);
        prefsToProp(pref, props, "SelectCalendar", "");
        prefsToProp(pref, props, "ClockSmallSecond", Constants.DEFAULT_CLOCK_SMALL_SECOND);
        prefsToProp(pref, props, "ClockOnLockScreen", false);
        prefsToProp(pref, props, "AllowWidgetDoubleTap", true);

        FileOutputStream os = new FileOutputStream(file);
        props.storeToXML(os, "", "UTF-8");
        os.close();
    }
}
