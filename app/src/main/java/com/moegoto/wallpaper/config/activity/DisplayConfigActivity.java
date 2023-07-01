package com.moegoto.wallpaper.config.activity;

import android.content.Context;
import android.graphics.Color;

import com.moegoto.wallpaper.Constants;
import com.moegoto.wallpaper.component.BooleanConfig;
import com.moegoto.wallpaper.component.ColorSelectConfig;
import com.moegoto.wallpaper.component.ConfigSeparator;
import com.moegoto.wallpaper.component.FontSelectConfig;
import com.moegoto.wallpaper.component.SelectionConfig;
import com.moegoto.wallpaper.component.SliderConfig;
import com.moegoto.wallpaper.util.GdiUtils;
import com.moegoto.wallpaper.util.KeyValue;
import com.moegoto.wallpaper.util.ListAdapter;
import com.moegoto.wallpaper.util.LocaleUtil;

public class DisplayConfigActivity extends BaseListActivity {

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        ListAdapter adapter = new ListAdapter(ctx);

        // ------テキストエフェクトの設定
        adapter.add(new ConfigSeparator(LocaleUtil.locale("テキストエフェクトの設定", "Text Effect Setting")));
        adapter.add(new SelectionConfig(LocaleUtil.locale("エフェクト種別", "Effect type"),
                "TextEffectType",
                new KeyValue[] { new KeyValue(Constants.TEXT_EFFECT_NONE, LocaleUtil.locale("なし", "None")),
                        new KeyValue(Constants.TEXT_EFFECT_SHADOW, LocaleUtil.locale("シャドウ", "Shadow")),
                        new KeyValue(Constants.TEXT_EFFECT_DROP_SHADOW, LocaleUtil.locale("ドロップシャドウ", "Drop Shadow")),
                        new KeyValue(Constants.TEXT_EFFECT_OUTLINE, LocaleUtil.locale("アウトライン", "Out Line")) },
                Constants.DEFAULT_EFFECT_TYPE));
        adapter.add(new SliderConfig(LocaleUtil.locale("エフェクトサイズ", "Effect size"), "TextEffectSize", 4, 10));
        adapter.add(new ColorSelectConfig(LocaleUtil.locale("エフェクト色の指定", "Text effect color"), "TextShadowColor", Color.argb(255, 0, 0, 0)));

        // ------画面背景色の設定
        adapter.add(new ConfigSeparator(LocaleUtil.locale("網掛けの設定", "Shading Color Settings")));
        adapter.add(new ColorSelectConfig(LocaleUtil.locale("全体の網掛け色の指定", "Screen shading color"),
                "ScreenBackgroundColor",
                Constants.DEFAULT_SCREEN_FILTER_COLOR));
        adapter.add(new ColorSelectConfig(LocaleUtil.locale("左右の網掛け色の指定", "Sides shading color"), "SidesBackgroundColor", Constants.DEFAULT_SIDES_FILTER_COLOR));

        // ------文字色の設定
        adapter.add(new ConfigSeparator(LocaleUtil.locale("文字色の設定", "Text Color Setting")));
        adapter.add(new ColorSelectConfig(LocaleUtil.locale("文字色の指定", "Foreground color"), "ForegroundColor", Constants.DEFAULT_FOREGROUND_COLOR));
        adapter.add(new ColorSelectConfig(LocaleUtil.locale("文字色(土曜)の指定", "Saturday color"), "SatForegroundColor", Color.argb(255, 80, 80, 255)));
        adapter.add(new ColorSelectConfig(LocaleUtil.locale("文字色(日曜)の指定", "Sunday color"), "SunForegroundColor", Color.argb(255, 255, 80, 80)));

        // ------文字色の設定
        adapter.add(new ConfigSeparator(LocaleUtil.locale("文字サイズの設定", "Text Size Setting")));
        adapter.add(new SliderConfig(LocaleUtil.locale("時計の文字サイズ", "Clock font size"), "ClockFontSize", (int) GdiUtils.dp(ctx,
                Constants.DEFAULT_CLOCK_TIME_FONTSIZE), (int) GdiUtils.dp(ctx, 100)));
        adapter.add(new SliderConfig(LocaleUtil.locale("日付の文字サイズ", "Date font size"), "DateFontSize", (int) GdiUtils.dp(ctx,
                Constants.DEFAULT_CLOCK_DATE_FONTSIZE), (int) GdiUtils.dp(ctx, 100)));
        adapter.add(new SliderConfig(LocaleUtil.locale("カレンダーの文字サイズ", "Calendar font size"),
                "CalendarFontSize",
                (int) GdiUtils.dp(ctx, 25),
                (int) GdiUtils.dp(ctx, 100)));
        adapter.add(new SliderConfig(LocaleUtil.locale("予定の文字サイズ", "Event font size"), "EventFontSize", (int) GdiUtils.dp(ctx, 10), (int) GdiUtils.dp(ctx, 100)));
        // ------フォントの設定
        adapter.add(new ConfigSeparator(LocaleUtil.locale("フォントの設定", "Font Settings")));
        adapter.add(new FontSelectConfig(LocaleUtil.locale("フォントの選択", "Select font"), "FontPath", ""));
        adapter.add(new BooleanConfig(LocaleUtil.locale("太字を使用する", "Use bold font"), "FontBold", Constants.DEFAULT_FONT_BOLD));
        // ------縦画面での余白の設定
        adapter.add(new ConfigSeparator(LocaleUtil.locale("縦画面での余白の設定", "Portrait Margin")));
        adapter.add(new SliderConfig(LocaleUtil.locale("[カレンダー]画面上部の余白", "[Calendar]Top margin"), "PortCalendarTopMargin", (int) GdiUtils.dp(ctx, 40), 1600));
        adapter.add(new SliderConfig(LocaleUtil.locale("[カレンダー]画面下部の余白", "[Calendar]Bottom margin"), "PortCalendarBottomMargin", (int) GdiUtils.dp(ctx, 40), 1600));
        adapter.add(new SliderConfig(LocaleUtil.locale("[時計]画面下部の余白", "[Clock]Bottom margin"), "PortClockBottomMargin", (int) GdiUtils.dp(ctx, 40), 1600));
        adapter.add(new SliderConfig(LocaleUtil.locale("画面左側の余白", "Left margin"), "PortLeftMargin", (int) GdiUtils.dp(ctx, 2), 1600));
        adapter.add(new SliderConfig(LocaleUtil.locale("画面右側の余白", "Right margin"), "PortRightMargin", (int) GdiUtils.dp(ctx, 2), 1600));
        // ------横画面での余白の設定
        adapter.add(new ConfigSeparator(LocaleUtil.locale("横画面での余白の設定", "Landscape Margin")));
        adapter.add(new SliderConfig(LocaleUtil.locale("[カレンダー]画面上部の余白", "[Calendar]Top margin"), "LandCalendarTopMargin", (int) GdiUtils.dp(ctx, 20), 1600));
        adapter.add(new SliderConfig(LocaleUtil.locale("[カレンダー]画面下部の余白", "[Calendar]Bottom margin"), "LandCalendarBottomMargin", (int) GdiUtils.dp(ctx, 2), 1600));
        adapter.add(new SliderConfig(LocaleUtil.locale("[時計]画面下部の余白", "[Clock]Bottom margin"), "LandClockBottomMargin", (int) GdiUtils.dp(ctx, 2), 1600));
        adapter.add(new SliderConfig(LocaleUtil.locale("画面左側の余白", "Left margin"), "LandLeftMargin", (int) GdiUtils.dp(ctx, 2), 1600));
        adapter.add(new SliderConfig(LocaleUtil.locale("画面右側の余白", "Right margin"), "LandRightMargin", (int) GdiUtils.dp(ctx, 50), 1600));

        getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(adapter);
    };
}
