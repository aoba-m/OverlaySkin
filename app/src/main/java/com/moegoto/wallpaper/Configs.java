package com.moegoto.wallpaper;

import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;

import com.moegoto.wallpaper.util.CalendarUtils;
import com.moegoto.wallpaper.util.GdiUtils;

/**
 * 設定を格納するコンテナクラスです。
 *
 * @author mikage aoba
 */
public class Configs {

    /**
     * @param context
     */
    public Configs(Context context) {
        super();
        this.ctx = context;
    }

    /**
     * コンテキスト
     */
    private final Context ctx;

    /**
     * 時計の秒を表示するか否か
     */
    private boolean hideClockSecond;

    /**
     * 前景色
     */
    private int foregroundColor;

    /**
     * フォントを太字で表示するか否か
     */
    private boolean fontBold;

    /**
     * スクリーン全体のフィルター色
     */
    private int screenFilterColor;

    /**
     * 左右のフィルター色
     */
    private int sidesFilterColor;

    /**
     * 時計(時刻)のフォントサイズ
     */
    private int clockTimeFontSize;
    /**
     * 時計(日付)のフォントサイズ
     */
    private int clockDateFontSize;

    /**
     * カレンダー(日)のフォントサイズ
     */
    private int calendarDayFontSize;
    /**
     * カレンダー(イベント)のフォントサイズ
     */
    private int calendarEventFontSize;
    /**
     * 曜日名
     */
    private String[] weekDayName;

    public String PortraitimageFilepath;
    public String LandScapeFilepath;
    public int backgoundColor;
    public boolean calendarOnDoubleTap;
    public boolean hideClock;

    public boolean hideSchedule;
    public boolean clock12HourFormat;
    public int textShadowColor;
    public int saturdayColor;
    public int sundayColor;
    public boolean useWideScroll;
    public int curtainWidth;
    public int gap;
    public String fontPath;
    public boolean eventAsMark;
    public boolean displayEventStartTime;
    public float xOffset = 0;
    public float yOffset = 0;
    public boolean homeScreenLock;
    public int homeScreenCount;
    public int homeScreenLockIndex;
    public int textEffectType;
    public int textEffectSize;
    public String holidayKeyword;
    public String doubleTapActivityClass;
    public String doubleTapActivityName;
    public Location location;

    /**
     * バッテリー残量(%)
     */
    private int batteryLevel;
    public int batteryScale;
    public int batteryVoltage;
    public int batteryTemp;
    public String batteryPlugged;

    /**
     * バッテリー状態
     */
    private String batteryStatus;
    public String batteryHealth;
    public boolean displayBatteryInfo;

    /**
     * バッテリー情報の文字サイズ
     */
    private int batteryFontSize;
    public HashSet<String> selectedCalendars;
    public int screenWidth;
    public int screenHeight;


    private int portraitCalendarTopMargin;
    private int portraitCalendarBottomMargin;
    private int portraitClockBottomMargin;
    private int portraitLeftMargin;
    private int portraitRightMargin;
    private int landscapeCalendarTopMargin;
    private int landscapeCalendarBottomMargin;
    private int landscapeClockBottomMargin;
    private int landscapeLeftMargin;
    private int landscapeRightMargin;

    public int desiredWidth;
    public int desiredHeight;
    public boolean swapPosition;
    public int slideShowIndex;

    public boolean clockSmallSecond;

    public boolean clockOnLockScreen;

    public boolean allowWidgetDoubleTap;

    public void onOffsetsChanged(float xOffset, float yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public void updateSharedPreferences(SharedPreferences preferences) {
        String[] items = preferences.getString("DoubleTapActivity", "").split("/", 3);
        if (items.length > 1) {
            doubleTapActivityClass = items[0];
            doubleTapActivityName = items[1];
        } else {
            doubleTapActivityClass = "";
            doubleTapActivityName = "";
        }
        // 前景色
        foregroundColor = preferences.getInt("ForegroundColor", Constants.DEFAULT_FOREGROUND_COLOR);
        // 画面のフィルター色
        screenFilterColor = preferences.getInt("ScreenBackgroundColor", Constants.DEFAULT_SCREEN_FILTER_COLOR);
        // 左右のフィルター色
        sidesFilterColor = preferences.getInt("SidesBackgroundColor", Constants.DEFAULT_SIDES_FILTER_COLOR);
        //
        saturdayColor = preferences.getInt("SatForegroundColor", Color.argb(255, 80, 80, 255));
        //
        sundayColor = preferences.getInt("SunForegroundColor", Color.argb(255, 255, 80, 80));
        //
        textShadowColor = preferences.getInt("TextShadowColor", Color.argb(255, 255, 255, 255));
        // 背景画像のファイルパス
        PortraitimageFilepath = preferences.getString("FileSelect", null);
        //
        LandScapeFilepath = preferences.getString("FileSelect2", null);
        //
        backgoundColor = preferences.getInt("BackgoundColor", Color.argb(255, 0, 0, 0));
        // ダブルタップでカレンダーを開く
        calendarOnDoubleTap = preferences.getBoolean("CalendarOnDoubleTap", true);
        // 時計を隠す
        hideClock = preferences.getBoolean("HideClock", false);
        // 
        hideClockSecond = preferences.getBoolean("HideClockSecond", false);
        // スケジュールを隠す
        hideSchedule = preferences.getBoolean("HideSchedule", false);
        // 時計の12時間表記
        clock12HourFormat = preferences.getBoolean("Clock12HourFormat", false);
        // 背景のスクロール
        useWideScroll = preferences.getBoolean("UseWideScroll", false);
        //
        portraitCalendarTopMargin = preferences.getInt("PortCalendarTopMargin", (int) GdiUtils.dp(ctx, 40));
        portraitCalendarBottomMargin = preferences.getInt("PortCalendarBottomMargin", (int) GdiUtils.dp(ctx, 40));
        portraitClockBottomMargin = preferences.getInt("PortClockBottomMargin", (int) GdiUtils.dp(ctx, 40));
        portraitLeftMargin = preferences.getInt("PortLeftMargin", (int) GdiUtils.dp(ctx, 2));
        portraitRightMargin = preferences.getInt("PortRightMargin", (int) GdiUtils.dp(ctx, 2));
        //
        landscapeCalendarTopMargin = preferences.getInt("LandCalendarTopMargin", (int) GdiUtils.dp(ctx, 40));
        landscapeCalendarBottomMargin = preferences.getInt("LandCalendarBottomMargin", (int) GdiUtils.dp(ctx, 40));
        landscapeClockBottomMargin = preferences.getInt("LandClockBottomMargin", (int) GdiUtils.dp(ctx, 40));
        landscapeLeftMargin = preferences.getInt("LandLeftMargin", (int) GdiUtils.dp(ctx, 2));
        landscapeRightMargin = preferences.getInt("LandRightMargin", (int) GdiUtils.dp(ctx, 2));

        // 時計(時刻)のフォントサイズ
        clockTimeFontSize = preferences.getInt("ClockFontSize", (int) GdiUtils.dp(ctx, Constants.DEFAULT_CLOCK_TIME_FONTSIZE));
        //
        clockDateFontSize = preferences.getInt("DateFontSize", (int) GdiUtils.dp(ctx, 19));
        // カレンダー(日)のフォントサイズ
        calendarDayFontSize = preferences.getInt("CalendarFontSize", (int) GdiUtils.dp(ctx, Constants.DEFAULT_CALENDAR_DAY_FONTSIZE));
        // カレンダー(イベント)のフォントサイズ
        calendarEventFontSize = preferences.getInt("EventFontSize", (int) GdiUtils.dp(ctx, Constants.DEFAULT_CALENDAR_EVENT_FONTSIZE));
        //
        curtainWidth = (int) GdiUtils.dp(ctx, 40);
        //
        gap = (int) GdiUtils.dp(ctx, 3);
        // フォントパス
        fontPath = preferences.getString("FontPath", "");
        // 太字フォントを使用する
        fontBold = preferences.getBoolean("FontBold", Constants.DEFAULT_FONT_BOLD);
        //
        eventAsMark = preferences.getBoolean("EventAsMark", false);
        //
        displayEventStartTime = preferences.getBoolean("DisplayEventStartTime", true);
        // 曜日表示
        weekDayName = new String[]{preferences.getString("WeekDayName0", Constants.WEEKDAY_NAME[0]),
                preferences.getString("WeekDayName1", Constants.WEEKDAY_NAME[1]),
                preferences.getString("WeekDayName2", Constants.WEEKDAY_NAME[2]),
                preferences.getString("WeekDayName3", Constants.WEEKDAY_NAME[3]),
                preferences.getString("WeekDayName4", Constants.WEEKDAY_NAME[4]),
                preferences.getString("WeekDayName5", Constants.WEEKDAY_NAME[5]),
                preferences.getString("WeekDayName6", Constants.WEEKDAY_NAME[6])};
        //
        homeScreenLock = preferences.getBoolean("HomeScreenLock", false);
        homeScreenCount = preferences.getInt("HomeScreenCount", 0);
        homeScreenLockIndex = preferences.getInt("HomeScreenLockIndex", 1);
        // テキストエフェクトの種別
        textEffectType = preferences.getInt("TextEffectType", Constants.DEFAULT_EFFECT_TYPE);
        textEffectSize = preferences.getInt("TextEffectSize", 4);
        //
        holidayKeyword = preferences.getString("HolidayKeyword", "");
        //
        displayBatteryInfo = preferences.getBoolean("DisplayBatteryInfo", true);
        //
        batteryFontSize = preferences.getInt("BatteryFontSize", (int) GdiUtils.dp(ctx, 15));
        //
        swapPosition = preferences.getBoolean("SwapPosition", false);
        // カレンダー情報
        selectedCalendars = new HashSet<String>();
        //
        String configValue = preferences.getString("SelectCalendar", null);
        if (configValue == null) {
            List<CalendarUtils.CalendarRecord> calendarRecords = CalendarUtils.getCalendars(ctx);
            for (CalendarUtils.CalendarRecord record : calendarRecords)
                selectedCalendars.add(record._id);
        } else {
            for (String value : configValue.split(","))
                selectedCalendars.add(value);
        }

        clockSmallSecond = preferences.getBoolean("ClockSmallSecond", Constants.DEFAULT_CLOCK_SMALL_SECOND);

        clockOnLockScreen = preferences.getBoolean("ClockOnLockScreen", false);
        allowWidgetDoubleTap = preferences.getBoolean("AllowWidgetDoubleTap", true);

    }

    public int getCalendarTopMargin() {
        return isLandscape() ? landscapeCalendarTopMargin : portraitCalendarTopMargin;
    }

    public int getCalendarBottomMargin() {
        return isLandscape() ? landscapeCalendarBottomMargin : portraitCalendarBottomMargin;
    }

    public int getClockBottomMargin() {
        return isLandscape() ? landscapeClockBottomMargin : portraitClockBottomMargin;
    }

    public int getLeftMargin() {
        return isLandscape() ? landscapeLeftMargin : portraitLeftMargin;
    }

    public int getRightMargin() {
        return isLandscape() ? landscapeRightMargin : portraitRightMargin;
    }

    /**
     * 曜日の文字列を取得します。
     *
     * @return 曜日の文字列
     */
    public String getWeekDayName(int weekday) {
        return weekDayName[weekday];
    }

    /**
     * 時計の秒を表示するか否かを取得します。
     *
     * @return 時計の秒を表示するか否か
     */
    public boolean isHideClockSecond() {
        return hideClockSecond;
    }

    /**
     * 前景色を取得します。
     *
     * @return 前景色
     */
    public int getForegroundColor() {
        return foregroundColor;
    }

    /**
     * フォントを太字で表示するか否かを取得します。
     *
     * @return フォントを太字で表示するか否か
     */
    public boolean isFontBold() {
        return fontBold;
    }

    /**
     * スクリーン全体のフィルター色を取得します。
     *
     * @return スクリーン全体のフィルター色
     */
    public int getScreenFilterColor() {
        return screenFilterColor;
    }

    /**
     * 左右のフィルター色を取得します。
     *
     * @return 左右のフィルター色
     */
    public int getSidesFilterColor() {
        return sidesFilterColor;
    }

    /**
     * 時計(時刻)のフォントサイズを取得します。
     *
     * @return 時計(時刻)のフォントサイズ
     */
    public int getClockTimeFontSize() {
        return clockTimeFontSize;
    }

    /**
     * 時計(日付)のフォントサイズを取得します。
     *
     * @return 時計(日付)のフォントサイズ
     */
    public int getClockDateFontSize() {
        return clockDateFontSize;
    }

    /**
     * カレンダー(日)のフォントサイズを取得します。
     *
     * @return カレンダー(日)のフォントサイズ
     */
    public int getCalendarDayFontSize() {
        return calendarDayFontSize;
    }

    /**
     * カレンダー(イベント)のフォントサイズを取得します。
     *
     * @return カレンダー(イベント)のフォントサイズ
     */
    public int getCalendarEventFontSize() {
        return calendarEventFontSize;
    }

    /**
     * バッテリー残量(%)を取得します。
     *
     * @return バッテリー残量(%)
     */
    public int getBatteryLevel() {
        return batteryLevel;
    }

    /**
     * バッテリー残量(%)を設定します。
     *
     * @param batteryLevel バッテリー残量(%)
     */
    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    /**
     * バッテリー状態を取得します。
     *
     * @return バッテリー状態
     */
    public String getBatteryStatus() {
        return batteryStatus;
    }

    /**
     * バッテリー状態を設定します。
     *
     * @param batteryStatus バッテリー状態
     */
    public void setBatteryStatus(String batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    /**
     * バッテリー情報の文字サイズを取得します。
     *
     * @return バッテリー情報の文字サイズ
     */
    public int getBatteryFontSize() {
        return batteryFontSize;
    }

    /**
     * 横画面か否かを取得します。
     *
     * @return 横画面か否か
     */
    public boolean isLandscape() {
        return screenWidth > screenHeight;
    }
}
