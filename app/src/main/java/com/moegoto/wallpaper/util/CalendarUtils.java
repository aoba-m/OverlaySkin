package com.moegoto.wallpaper.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.util.Log;

public class CalendarUtils {

    private static String CALENDAR_URL = (7 < Build.VERSION.SDK_INT) ? "content://com.android.calendar/" : "content://calendar/";

    public CalendarUtils() {
        super();
    }

    public static List<CalendarRecord> getCalendars(Context context) {
        List<CalendarRecord> calendars = new ArrayList<CalendarRecord>();
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Uri.Builder builder = Uri.parse(CALENDAR_URL + "instances/when").buildUpon();
            long now = new Date().getTime();
            ContentUris.appendId(builder, now - DateUtils.WEEK_IN_MILLIS);
            ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);
            final Cursor cursor = contentResolver.query(
                    Uri.parse(CALENDAR_URL + "calendars"), (new String[] {
                            CalendarContract.Calendars._ID,
                            CalendarContract.Calendars.NAME,
                            CalendarContract.Calendars.VISIBLE,
                            CalendarContract.Calendars.CALENDAR_TIME_ZONE }),
                    null, null, null);
            while (cursor.moveToNext()) {
                CalendarRecord calendar = new CalendarRecord();
                calendar._id = cursor.getString(0);
                calendar.displayName = cursor.getString(1);
                calendar.selected = cursor.getInt(2);
                calendar.timezone = cursor.getString(3);
                calendars.add(calendar);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("OverlaySkin", e.getMessage(), e);
        }
        return calendars;
    }

    public static Map<String, List<EventRecord>> getEvent(Context context,
            HashSet<String> targetCalendar) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            List<CalendarRecord> calendarIdList = new ArrayList<CalendarRecord>();
            {
                Uri.Builder builder = Uri
                        .parse(CALENDAR_URL + "instances/when").buildUpon();

                long now = new Date().getTime();
                ContentUris.appendId(builder, now - DateUtils.WEEK_IN_MILLIS);
                ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);

                // カレンダーの取得
                final Cursor cursor = contentResolver
                        .query(Uri.parse(CALENDAR_URL + "calendars"),
                                (new String[] {
                                        CalendarContract.Calendars._ID,
                                        CalendarContract.Calendars.NAME,
                                        CalendarContract.Calendars.VISIBLE,
                                        CalendarContract.Calendars.CALENDAR_TIME_ZONE }),
                                null, null, null);
                while (cursor.moveToNext()) {
                    CalendarRecord calendar = new CalendarRecord();
                    calendar._id = cursor.getString(0);
                    calendar.displayName = cursor.getString(1);
                    calendar.selected = cursor.getInt(2);
                    calendar.timezone = cursor.getString(3);
                    if (targetCalendar.contains(calendar._id)) {
                        // 選択されているカレンダーのみ
                        calendarIdList.add(calendar);
                    }
                }
                cursor.close();
            }
            // 予定の取得
            List<EventRecord> eventlist = new ArrayList<EventRecord>();
            for (CalendarRecord calendar : calendarIdList) {

                Uri.Builder builder = Uri.parse(CALENDAR_URL + "instances/when").buildUpon();
                long now = new Date().getTime();
                ContentUris.appendId(builder, now - DateUtils.WEEK_IN_MILLIS);
                ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);

                Cursor eventCursor = null;
                if (Build.VERSION.SDK_INT < 11) {
                    eventCursor = contentResolver.query(builder.build(),
                            new String[] { "_id", "event_id", "title", "begin", "end", "allDay" },
                            "Calendars._id=" + calendar._id,
                            null,
                            "startDay ASC, startMinute ASC");
                } else {
                    eventCursor = contentResolver.query(builder.build(),
                            new String[] { "_id", "event_id", "title", "begin", "end", "allDay" },
                            "calendar_id=" + calendar._id,
                            null,
                            "startDay ASC, startMinute ASC");
                }
                while (eventCursor.moveToNext()) {
                    EventRecord event = new EventRecord();
                    event.calendar = calendar;
                    event._id = eventCursor.getInt(0);
                    event.event_id = eventCursor.getInt(1);
                    event.setTitle(eventCursor.getString(2));
                    event.setBegin(new Date(eventCursor.getLong(3)));
                    event.setEnd(new Date(eventCursor.getLong(4)));
                    event.setAllDay(!eventCursor.getString(5).equals("0"));
                    if (event.allDay) {
                        event.setEnd(new Date(event.getEnd().getTime() - DateUtils.DAY_IN_MILLIS));
                    }
                    eventlist.add(event);
                }
                eventCursor.close();
            }
            Map<String, List<EventRecord>> map = new HashMap<String, List<EventRecord>>();
            for (EventRecord event : eventlist) {

                Uri.Builder builder = Uri.parse(CALENDAR_URL + "events").buildUpon();

                Cursor eventCursor;
                if (Build.VERSION.SDK_INT <= 7) {
                    eventCursor = contentResolver.query(builder.build(),
                            new String[] { "title", "dtstart", "eventTimezone" },
                            "Events._id=" + event.getEvent_id(),
                            null,
                            "");
                } else {
                    eventCursor = contentResolver.query(builder.build(),
                            new String[] { "title", "dtstart", "eventTimezone" },
                            "view_events._id=" + event.getEvent_id(),
                            null,
                            "");
                }
                while (eventCursor.moveToNext()) {
                    // イベントタイムの補正
                    event.setBegin(offsetTime(event.getBegin()));
                    event.setEnd(offsetTime(event.getEnd()));
                    pushToMap(map, event);
                }
                eventCursor.close();
            }
            return map;
        } catch (Exception e) {
            Log.e("OverlaySkin", e.getMessage(), e);
            return null;
        }
    }

    private static Date offsetTime(Date date) {
        int timezoneOffset = TimeZone.getDefault().getRawOffset();
        int gmtOffset = Calendar.getInstance().getTimeZone().getRawOffset();
        return new Date(date.getTime() - gmtOffset + timezoneOffset);
    }

    private static void pushToMap(Map<String, List<EventRecord>> map, EventRecord event) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        // 連日イベント
        for (int offset = 0; offset < 14; offset++) {
            String offsetStartDate = format.format(new Date(event.getBegin().getTime() + DateUtils.DAY_IN_MILLIS * offset));
            String endDate = format.format(event.getEnd().getTime());
            if (offsetStartDate.compareTo(endDate) <= 0) {
                List<EventRecord> list = map.get(offsetStartDate);
                if (list == null) {
                    list = new ArrayList<EventRecord>();
                    map.put(offsetStartDate, list);
                }
                list.add(event);
            }
        }
    }

    public static class CalendarRecord {
        public String _id;
        public String displayName;

        public String get_id() {
            return _id;
        }

        public void set_id(String id) {
            _id = id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public int getSelected() {
            return selected;
        }

        public void setSelected(int selected) {
            this.selected = selected;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public int selected;
        public String timezone;
    }

    public static class EventRecord {
        private CalendarRecord calendar;

        public CalendarRecord getCalendar() {
            return calendar;
        }

        public void setCalendar(CalendarRecord calendar) {
            this.calendar = calendar;
        }

        public int get_id() {
            return _id;
        }

        public void set_id(int id) {
            _id = id;
        }

        public int getEvent_id() {
            return event_id;
        }

        public void setEvent_id(int eventId) {
            event_id = eventId;
        }

        private int _id;
        private int event_id;
        private String title;
        private Date begin;
        private Date end;
        private Boolean allDay;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Date getBegin() {
            return begin;
        }

        public void setBegin(Date begin) {
            this.begin = begin;
        }

        public Date getEnd() {
            return end;
        }

        public void setEnd(Date end) {
            this.end = end;
        }

        public Boolean getAllDay() {
            return allDay;
        }

        public void setAllDay(Boolean allDay) {
            this.allDay = allDay;
        }
    }

}
