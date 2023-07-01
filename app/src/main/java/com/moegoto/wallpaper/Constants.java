package com.moegoto.wallpaper;

import android.graphics.Color;

/**
 * 定数クラス
 * 
 * @author mikage aoba
 */
public class Constants {

    /** 曜日の文字列 */
    public static String[] WEEKDAY_SHORT_NAME = new String[] { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT", };

    /** 曜日の文字列 */
    public static String[] WEEKDAY_NAME = new String[] { "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", };

    /** テキストエフェクト：なし */
    public static final int TEXT_EFFECT_NONE = -1;
    /** テキストエフェクト：シャドウ */
    public static final int TEXT_EFFECT_SHADOW = 0;
    /** テキストエフェクト：アウトライン */
    public static final int TEXT_EFFECT_OUTLINE = 1;
    /** テキストエフェクト：ドロップシャドウ */
    public static final int TEXT_EFFECT_DROP_SHADOW = 2;

    /** エフェクト種別(デフォルト値) */
    public static final int DEFAULT_EFFECT_TYPE = TEXT_EFFECT_NONE;

    /** 前景色(デフォルト値) */
    public static final int DEFAULT_FOREGROUND_COLOR = Color.argb(255, 255, 255, 255);

    /** 時計(時刻)のフォントサイズ(デフォルト値) */
    public static final int DEFAULT_CLOCK_TIME_FONTSIZE = 62;

    /** 時計(日付)のフォントサイズ(デフォルト値) */
    public static final int DEFAULT_CLOCK_DATE_FONTSIZE = 20;

    /** カレンダー(日)のフォントサイズ(デフォルト値) */
    public static final int DEFAULT_CALENDAR_DAY_FONTSIZE = 22;

    /** カレンダー(イベント)のフォントサイズ(デフォルト値) */
    public static final int DEFAULT_CALENDAR_EVENT_FONTSIZE = 9;

    /** PowerAmp 連携設定状態 */
    public static final boolean DEFAULT_POWEAMP_ENABLE = false;
    /** PowerAmp 連携 - 曲名フォントサイズ */
    public static final int DEFAULT_POWEAMP_TRACK_FONTSIZE = 30;
    /** PowerAmp 連携 - アーティストフォントサイズ */
    public static final int DEFAULT_POWEAMP_ARTIST_FONTSIZE = 12;
    /** PowerAmp 連携 - アルバムフォントサイズ */
    public static final int DEFAULT_POWEAMP_ALBUM_FONTSIZE = 20;

    /** フォントを太字表示するか(デフォルト値) */
    public static final boolean DEFAULT_FONT_BOLD = false;

    /** スクリーンのフィルター色(デフォルト値) */
    public static final int DEFAULT_SCREEN_FILTER_COLOR = Color.argb(100, 0, 0, 0);

    /** 左右のフィルター色(デフォルト値) */
    public static final int DEFAULT_SIDES_FILTER_COLOR = Color.argb(0, 0, 0, 0);

    /** LOGCAT用のタグ名 */
    public static final String TAG = "OverlaySkin";

    public static final boolean DEFAULT_CLOCK_SMALL_SECOND = true;

}
