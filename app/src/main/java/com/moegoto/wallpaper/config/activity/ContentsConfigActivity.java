package com.moegoto.wallpaper.config.activity;

import android.R;
import android.content.Context;

import com.moegoto.wallpaper.Constants;
import com.moegoto.wallpaper.component.BooleanConfig;
import com.moegoto.wallpaper.component.CalendarSelectConfig;
import com.moegoto.wallpaper.component.ConfigSeparator;
import com.moegoto.wallpaper.component.SliderConfig;
import com.moegoto.wallpaper.component.SubMenuConfig;
import com.moegoto.wallpaper.component.TextInputConfig;
import com.moegoto.wallpaper.component.WeeklyNameConfig;
import com.moegoto.wallpaper.util.GdiUtils;
import com.moegoto.wallpaper.util.ListAdapter;
import com.moegoto.wallpaper.util.LocaleUtil;

public class ContentsConfigActivity extends BaseListActivity {

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        ListAdapter adapter = new ListAdapter(ctx);

        adapter.add(new BooleanConfig(LocaleUtil.locale("時計を非表示にする", "Hide clock"), "HideClock", false));
        adapter.add(new BooleanConfig(LocaleUtil.locale("予定を非表示にする", "Hide schedule"), "HideSchedule", false));
        adapter.add(new BooleanConfig(LocaleUtil.locale("時計と予定を入れ替える", "Swap clock and schedule Position"), "SwapPosition", false));

        // ------時計表示設定
        adapter.add(new ConfigSeparator(LocaleUtil.locale("時計表示設定", "Clock Settings")));
        adapter.add(new BooleanConfig(LocaleUtil.locale("時計の12時間表記", "Use 12hour format"), "Clock12HourFormat", false));
        adapter.add(new BooleanConfig(LocaleUtil.locale("秒を表示しない", "Hide clock second"), "HideClockSecond", false));
        adapter.add(new BooleanConfig(LocaleUtil.locale("秒を小さく表示する", "Display second small style"), "ClockSmallSecond", Constants.DEFAULT_CLOCK_SMALL_SECOND));
        adapter.add(new WeeklyNameConfig(LocaleUtil.locale("曜日名の変更", "Change weekday name"), "WeekDayName", new String[]{"SUNDAY",
                "MONDAY",
                "TUESDAY",
                "WEDNESDAY",
                "THURSDAY",
                "FRIDAY",
                "SATURDAY",}));

        // ------カレンダー設定
        adapter.add(new ConfigSeparator(LocaleUtil.locale("カレンダー設定", "Calendar Settings")));
        adapter.add(new CalendarSelectConfig(LocaleUtil.locale("表示するカレンダーを選択する", "Select calendar"), "SelectCalendar"));
        adapter.add(new BooleanConfig(LocaleUtil.locale("予定のタイトルを表示しない", "Hide event title"), "EventAsMark", false));
        adapter.add(new BooleanConfig(LocaleUtil.locale("予定の開始時刻を表示する", "Display event start time"), "DisplayEventStartTime", true));
        adapter.add(new TextInputConfig(LocaleUtil.locale("祝日扱いにするイベント名の設定", "Set event name as holiday"),
                "HolidayKeyword",
                "",
                LocaleUtil.locale("指定したイベント名がカレンダーに登録されている場合、該当する日を祝日扱いにします。", "The day is regard as holiday when specified event name in the calendar. ")));

        // ------バッテリー設定
        adapter.add(new ConfigSeparator(LocaleUtil.locale("バッテリー情報", "Battery Information")));
        adapter.add(new BooleanConfig(LocaleUtil.locale("バッテリー情報を表示する", "Display battery info"), "DisplayBatteryInfo", true));
        adapter.add(new SliderConfig(LocaleUtil.locale("文字サイズ", "Font size"), "BatteryFontSize", (int) GdiUtils.dp(ctx, 15), 100));

        getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(adapter);
    }

    ;
}
