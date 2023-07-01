package com.moegoto.wallpaper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.widget.Toast;

import com.moegoto.wallpaper.util.CalendarUtils;
import com.moegoto.wallpaper.util.GdiUtils;
import com.moegoto.wallpaper.util.ImageFileFilter;
import com.moegoto.wallpaper.util.LocaleUtil;
import com.moegoto.wallpaper.util.StringUtils;

/**
 * 時計を描画するクラスです。
 *
 * @author mikage
 */
public class WallpaperRenderer {

    private final Configs conf;
    private final Context context;
    private static Bitmap verticalBgImage;
    private static Bitmap horizontalBgImage;
    private static Bitmap offscreenImage;
    private static Canvas offscreenCanvas;
    private static String verticalBitmapPath;
    private static String horizontalBitmapPath;
    private static Typeface typeface;
    private int rotateDegree = 0;
    private Map<String, List<CalendarUtils.EventRecord>> eventMap;

    /**
     * コンストラクタ
     *
     * @param context
     * @param configuration
     */
    public WallpaperRenderer(Context context, Configs configuration) {
        super();
        this.context = context;
        this.conf = configuration;
    }

    public long lastScrollTime = 0;
    public long lastUpdateTime = 0;
    public String lastDate = "";
    public String lastTime = "";

    public void init(Resources r, int screenWidth, int screenHeight) {

        synchronized (this) {
            conf.screenWidth = screenWidth;
            conf.screenHeight = screenHeight;
            eventMap = CalendarUtils.getEvent(context, conf.selectedCalendars);
            reloadBackgroundImage();
            typeface = GdiUtils.getFont(conf.fontPath);
            try {
                if (offscreenCanvas == null) {
                    int longLength = conf.isLandscape() ? screenWidth : screenHeight;
                    offscreenImage = Bitmap.createBitmap(longLength, longLength, Bitmap.Config.ARGB_8888);
                    offscreenCanvas = new Canvas(offscreenImage);
                }
            } catch (OutOfMemoryError e) {
                offscreenImage = null;
                offscreenCanvas = null;
            }
            lastScrollTime = 0;
            lastUpdateTime = 0;
            lastDate = "";
        }
        System.gc();
    }

    public void draw(long currentTime, Canvas canvas, boolean screenLocking, boolean scrolling, boolean preview) {

        synchronized (this) {
            Date currentDate = new Date();
            String timeString = new SimpleDateFormat("HHmm").format(currentDate);

            if (!timeString.equals(lastTime)) {
                lastTime = timeString;
                reloadBackgroundImage();
            }

            String dateString = new SimpleDateFormat(conf.isHideClockSecond() ? "HHmm" : "HHmmss").format(currentDate);
            if (!dateString.equals(lastDate) && !scrolling && currentTime - lastScrollTime > 50) {
                lastDate = dateString;

                // イベント情報の更新
                if (currentTime - lastUpdateTime > 30000) {
                    Map<String, List<CalendarUtils.EventRecord>> map = CalendarUtils.getEvent(context, conf.selectedCalendars);
                    if (map != null) {
                        eventMap = map;
                    }
                    lastUpdateTime = currentTime;
                }
                // バッファの更新
                offscreenCanvas.save();
                {
                    offscreenCanvas.drawColor(Color.argb(0, 0, 0, 0), Mode.CLEAR);
                    drawSidesCurtain(offscreenCanvas, 0);
                    offscreenCanvas.clipRect(0, 0, conf.screenWidth, conf.screenHeight);
                    drawClock(offscreenCanvas, 0, currentDate);
                    drawSchedule(offscreenCanvas, 0, currentDate);
                }
                offscreenCanvas.restore();
            }
            drawbackgroundImage(canvas);

            if (Color.alpha(conf.getScreenFilterColor()) > 0) {
                canvas.drawColor(conf.getScreenFilterColor());
            }

            if (conf.clockOnLockScreen || !screenLocking) {
                int xWidgetOffset = 0;
                if (conf.homeScreenLock && !preview) {
                    int xOffset = (int) ((float) (conf.screenWidth * conf.homeScreenCount - conf.screenWidth) * conf.xOffset);
                    xWidgetOffset = -xOffset + (conf.screenWidth * ((conf.homeScreenLockIndex - 1)));
                }
                canvas.drawBitmap(offscreenImage, xWidgetOffset, 0, new Paint());
            }
        }
        if (scrolling) {
            lastScrollTime = currentTime;
        }
    }

    /**
     * 背景画像の描画
     *
     * @param canvas
     */
    private void drawbackgroundImage(Canvas canvas) {
        Paint paint = new Paint();
        Bitmap bgImage = conf.isLandscape() ? horizontalBgImage : verticalBgImage;

        if (bgImage != null) {
            // まずは背景色描画
            GdiUtils.setPaintARGB(paint, conf.backgoundColor);
            canvas.drawRect(0, 0, conf.screenWidth, conf.screenHeight, paint);
            int xOffset, yOffset;
            if (conf.useWideScroll) {
                xOffset = (int) ((float) (bgImage.getWidth() - conf.screenWidth) * conf.xOffset);
                yOffset = (int) ((float) (bgImage.getHeight() - conf.screenHeight) * 0.5);
            } else {
                xOffset = (int) ((float) (bgImage.getWidth() - conf.screenWidth) * 0.5);
                yOffset = (int) ((float) (bgImage.getHeight() - conf.screenHeight) * 0.5);
            }
            // 背景画像の描画
            canvas.drawBitmap(bgImage, -xOffset, -yOffset, paint);
        } else {
            // 画像がないなら背景色描画のみ
            GdiUtils.setPaintARGB(paint, Color.BLACK);
            canvas.drawRect(0, 0, conf.screenWidth, conf.screenHeight, paint);
        }
    }

    /**
     * 左右の背景色の描画
     *
     * @param canvas
     * @param xWidgetOffset
     */
    private void drawSidesCurtain(Canvas canvas, int xWidgetOffset) {
        Paint paint = new Paint();
        GdiUtils.setPaintARGB(paint, conf.getSidesFilterColor());
        if (!conf.hideClock) {
            if (!conf.swapPosition) {
                canvas.drawRect(xWidgetOffset, 0, xWidgetOffset + conf.curtainWidth, conf.screenHeight, paint);
            } else {
                canvas.drawRect(xWidgetOffset + conf.screenWidth - conf.curtainWidth, 0, xWidgetOffset + conf.screenWidth, conf.screenHeight, paint);
            }
        }
        if (!conf.hideSchedule) {
            if (!conf.swapPosition) {
                canvas.drawRect(xWidgetOffset + conf.screenWidth - conf.curtainWidth, 0, xWidgetOffset + conf.screenWidth, conf.screenHeight, paint);
            } else {
                canvas.drawRect(xWidgetOffset, 0, xWidgetOffset + conf.curtainWidth, conf.screenHeight, paint);
            }
        }
    }

    private void rotateCanvas(Canvas canvas, int degree) {
        canvas.rotate(-rotateDegree);
        canvas.rotate(degree);
        rotateDegree = degree;
    }

    /**
     * 時計の描画
     *
     * @param canvas
     * @param xWidgetOffset
     */
    private void drawClock(Canvas canvas, int xWidgetOffset, Date currentDate) {

        if (!conf.hideClock) {
            String timeString = new SimpleDateFormat(conf.clock12HourFormat ? "hh:mm" : "HH:mm").format(currentDate);
            String dateString = formatDateString(currentDate);
            String ampm = currentDate.getHours() > 11 ? "PM" : "AM";
            String batteryString = formatBattryString();

            float timeStringWidth = GdiUtils.calcTextWidth(timeString, typeface, conf.getClockTimeFontSize(), conf.isFontBold());
            float dateStringWidth = GdiUtils.calcTextWidth(dateString, typeface, conf.getClockDateFontSize(), conf.isFontBold());
            float ampmWidth = GdiUtils.calcTextWidth(ampm, typeface, conf.getClockDateFontSize(), conf.isFontBold());
            float batteryWidth = GdiUtils.calcTextWidth(batteryString, typeface, conf.getBatteryFontSize(), conf.isFontBold());

            Align align;

            // 秒の表示
            float secondStringWidth = 0;
            if (!conf.isHideClockSecond()) {
                int secondFontSize = conf.getClockTimeFontSize();
                String secondString = new SimpleDateFormat(":ss").format(currentDate);
                secondStringWidth = GdiUtils.calcTextWidth(":88", typeface, secondFontSize, conf.isFontBold());
                if (conf.clockSmallSecond) {
                    secondFontSize = conf.getClockTimeFontSize() * 3 / 5;
                    secondString = new SimpleDateFormat("|ss").format(currentDate);
                    secondStringWidth = GdiUtils.calcTextWidth("|88", typeface, secondFontSize, conf.isFontBold());
                }
                float secondOffsetX, secondOffsetY;
                if (!conf.swapPosition) {
                    rotateCanvas(canvas, 90);
                    secondOffsetX = conf.getLeftMargin();
                    secondOffsetY = conf.screenHeight - conf.getClockBottomMargin() - secondStringWidth;
                    align = Align.LEFT;
                } else {
                    rotateCanvas(canvas, 270);
                    secondOffsetX = -(conf.screenWidth - conf.getRightMargin());
                    secondOffsetY = -(conf.screenHeight - conf.getClockBottomMargin() - timeStringWidth);
                    align = Align.LEFT;
                }
                drawString(canvas,
                        secondString,
                        secondOffsetY,
                        -secondOffsetX - xWidgetOffset,
                        secondFontSize,
                        conf.getForegroundColor(),
                        align,
                        conf.isFontBold());
            }
            // 時刻の表示
            float clockOffsetX, clockOffsetY;
            if (!conf.swapPosition) {
                rotateCanvas(canvas, 90);
                clockOffsetX = conf.getLeftMargin();
                clockOffsetY = conf.screenHeight - conf.getClockBottomMargin() - secondStringWidth;
                align = Align.RIGHT;
            } else {
                rotateCanvas(canvas, 270);
                clockOffsetX = -(conf.screenWidth - conf.getRightMargin());
                clockOffsetY = -(conf.screenHeight - conf.getClockBottomMargin());
                align = Align.LEFT;
            }
            drawString(canvas,
                    timeString,
                    clockOffsetY,
                    -clockOffsetX - xWidgetOffset,
                    conf.getClockTimeFontSize(),
                    conf.getForegroundColor(),
                    align,
                    conf.isFontBold());

            // 日付の描画
            float dateOffsetX, dateOffsetY;
            if (!conf.swapPosition) {
                rotateCanvas(canvas, 90);
                if (conf.isLandscape()) {
                    dateOffsetX = (conf.getLeftMargin() + conf.gap + GdiUtils.calcTextHeight(typeface, conf.getClockTimeFontSize()));
                    dateOffsetY = (conf.screenHeight - conf.getClockBottomMargin());
                } else {
                    dateOffsetX = (conf.getLeftMargin());
                    dateOffsetY = (conf.screenHeight - conf.getClockBottomMargin() - timeStringWidth - secondStringWidth - conf.gap);
                }
            } else {
                rotateCanvas(canvas, 270);
                if (conf.isLandscape()) {
                    dateOffsetX = -(conf.screenWidth - conf.getRightMargin() - conf.gap - GdiUtils.calcTextHeight(typeface, conf.getClockTimeFontSize()));
                    dateOffsetY = -(conf.screenHeight - conf.getClockBottomMargin() - dateStringWidth);
                } else {
                    dateOffsetX = -(conf.screenWidth - conf.getRightMargin());
                    dateOffsetY = -(conf.screenHeight - conf.getClockBottomMargin() - dateStringWidth - timeStringWidth - secondStringWidth - conf.gap);
                }
            }
            drawString(canvas,
                    dateString,
                    dateOffsetY,
                    -dateOffsetX - xWidgetOffset,
                    conf.getClockDateFontSize(),
                    conf.getForegroundColor(),
                    Align.RIGHT,
                    conf.isFontBold());

            // AM/PMの描画
            if (conf.clock12HourFormat) {
                float ampmOffsetX, ampmOffsetY;
                if (!conf.swapPosition) {
                    rotateCanvas(canvas, 90);
                    if (conf.isLandscape()) {
                        ampmOffsetX = conf.getLeftMargin();
                        ampmOffsetY = conf.screenHeight - conf.getClockBottomMargin() - timeStringWidth - secondStringWidth - conf.gap;
                    } else {
                        ampmOffsetX = conf.getLeftMargin() + conf.getClockDateFontSize();
                        ampmOffsetY = conf.screenHeight - conf.getClockBottomMargin() - timeStringWidth - secondStringWidth - conf.gap;
                    }
                } else {
                    rotateCanvas(canvas, 270);
                    if (conf.isLandscape()) {
                        ampmOffsetX = -(conf.screenWidth - conf.getRightMargin());
                        ampmOffsetY = -(conf.screenHeight - conf.getClockBottomMargin() - timeStringWidth - secondStringWidth - ampmWidth - conf.gap);
                    } else {
                        ampmOffsetX = -(conf.screenWidth - conf.getRightMargin() - conf.getClockDateFontSize());
                        ampmOffsetY = -(conf.screenHeight - conf.getClockBottomMargin() - timeStringWidth - secondStringWidth - ampmWidth - conf.gap);
                    }
                }
                drawString(canvas,
                        ampm,
                        ampmOffsetY,
                        -ampmOffsetX - xWidgetOffset,
                        conf.getClockDateFontSize(),
                        conf.getForegroundColor(),
                        Align.RIGHT,
                        conf.isFontBold());
            }

            // バッテリー情報
            if (conf.displayBatteryInfo) {
                float batteryOffsetX, batteryOffsetY;
                float clockHeight = GdiUtils.calcTextHeight(typeface, conf.getClockTimeFontSize());
                float dateHeight = GdiUtils.calcTextHeight(typeface, conf.getClockDateFontSize());
                if (!conf.swapPosition) {
                    rotateCanvas(canvas, 90);
                    if (conf.isLandscape()) {
                        batteryOffsetX = conf.getLeftMargin() + conf.gap * 2 + clockHeight + dateHeight;
                        batteryOffsetY = conf.screenHeight - conf.getClockBottomMargin();
                    } else {
                        batteryOffsetX = conf.getLeftMargin() + conf.gap + clockHeight;
                        batteryOffsetY = conf.screenHeight - conf.getClockBottomMargin();
                    }
                } else {
                    rotateCanvas(canvas, 270);
                    if (conf.isLandscape()) {
                        batteryOffsetX = -(conf.screenWidth - conf.getRightMargin() - conf.gap * 2 - clockHeight - dateHeight);
                        batteryOffsetY = -(conf.screenHeight - conf.getClockBottomMargin() - batteryWidth);
                    } else {
                        batteryOffsetX = -(conf.screenWidth - conf.getRightMargin() - conf.gap - clockHeight);
                        batteryOffsetY = -(conf.screenHeight - conf.getClockBottomMargin() - batteryWidth);
                    }
                }
                drawString(canvas,
                        batteryString,
                        batteryOffsetY,
                        -batteryOffsetX - xWidgetOffset,
                        conf.getBatteryFontSize(),
                        conf.getForegroundColor(),
                        Align.RIGHT,
                        conf.isFontBold());
            }
        }
        rotateCanvas(canvas, 0);
    }

    /**
     * カレンダーの描画
     *
     * @param canvas
     * @param currentDate
     */
    private void drawSchedule(Canvas canvas, int xWidgetOffset, Date currentDate) {
        if (!conf.hideSchedule) {
            Align textAlign = null;

            Calendar date = Calendar.getInstance();
            date.setTime(new Date());
            SimpleDateFormat dayFormatter = new SimpleDateFormat("d");
            for (int i = 0; i < 7; i++) {

                int rowHeight = (conf.screenHeight - conf.getCalendarBottomMargin() - conf.getCalendarTopMargin()) / 7;
                int rowStart = conf.getCalendarTopMargin() + conf.getCalendarDayFontSize();

                // 日付の描画
                int weekdayColor = conf.getForegroundColor();
                if (date.getTime().getDay() == 0) {
                    weekdayColor = conf.sundayColor;
                } else if (date.getTime().getDay() == 6) {
                    weekdayColor = conf.saturdayColor;
                }
                // 休日判定
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
                if (eventMap != null) {
                    List<CalendarUtils.EventRecord> eventList = eventMap.get(dateFormatter.format(date.getTime()));
                    if (eventList != null) {
                        for (CalendarUtils.EventRecord event : eventList) {
                            if (StringUtils.isNotEmpty(conf.holidayKeyword) && conf.holidayKeyword.equals(event.getTitle())) {
                                weekdayColor = conf.sundayColor;
                            }
                        }
                    }
                }

                String day = dayFormatter.format(date.getTime());
                float dayStringWidth = GdiUtils.calcTextWidth("88", typeface, conf.getCalendarDayFontSize(), conf.isFontBold());
                if (!conf.swapPosition) {
                    drawString(canvas,
                            day,
                            conf.screenWidth - conf.getRightMargin() + xWidgetOffset,
                            rowStart + i * rowHeight,
                            conf.getCalendarDayFontSize(),
                            weekdayColor,
                            Align.RIGHT,
                            conf.isFontBold());
                } else {
                    drawString(canvas,
                            day,
                            conf.getLeftMargin() + xWidgetOffset,
                            rowStart + i * rowHeight,
                            conf.getCalendarDayFontSize(),
                            weekdayColor,
                            Align.LEFT,
                            conf.isFontBold());
                }
                // 曜日の描画
                float weekdayX, weekdayY;
                if (conf.isLandscape()) {
                    if (!conf.swapPosition) {
                        weekdayX = conf.screenWidth - conf.getRightMargin() - conf.gap;
                        weekdayY = rowStart + i * rowHeight + GdiUtils.calcTextHeight(typeface, conf.getCalendarDayFontSize() / 2) + conf.gap;
                        textAlign = Align.RIGHT;
                    } else {
                        weekdayX = conf.getLeftMargin() + conf.gap;
                        weekdayY = rowStart + i * rowHeight + GdiUtils.calcTextHeight(typeface, conf.getCalendarDayFontSize() / 2) + conf.gap;
                        textAlign = Align.LEFT;
                    }
                } else {
                    if (!conf.swapPosition) {
                        weekdayX = conf.screenWidth - conf.getRightMargin() - dayStringWidth - conf.gap;
                        weekdayY = rowStart + i * rowHeight;
                        textAlign = Align.RIGHT;
                    } else {
                        weekdayX = conf.getLeftMargin() + dayStringWidth + conf.gap;
                        weekdayY = rowStart + i * rowHeight;
                        textAlign = Align.LEFT;
                    }
                }
                drawString(canvas,
                        Constants.WEEKDAY_SHORT_NAME[date.getTime().getDay()],
                        weekdayX + xWidgetOffset,
                        weekdayY,
                        conf.getCalendarDayFontSize() / 2,
                        conf.getForegroundColor(),
                        textAlign,
                        conf.isFontBold());

                // イベントの描画
                drawEventDetails(canvas, date, i, rowHeight, rowStart, xWidgetOffset);
                // 日付を進める
                date.add(Calendar.DATE, 1);
            }
        }
    }

    private void drawEventDetails(Canvas canvas, Calendar date, int dateOffset, int scheduleOneDayHeight, int yOffset, int xWidgetOffset) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
        String currentDay = dateFormatter.format(date.getTime());
        float dayStringWidth = GdiUtils.calcTextWidth("88", typeface, conf.getCalendarDayFontSize(), conf.isFontBold());

        if (eventMap != null) {
            List<CalendarUtils.EventRecord> eventList = eventMap.get(currentDay);
            if (eventList != null) {
                if (conf.eventAsMark) {
                    // イベント数のみ表示
                    if (eventList.size() > 0) {
                        float eventX, eventY;
                        Align textAlign = null;
                        if (conf.isLandscape()) {
                            if (!conf.swapPosition) {
                                eventX = (conf.screenWidth - conf.getRightMargin() - dayStringWidth - conf.gap);
                                eventY = (GdiUtils.calcTextHeight(typeface, conf.getCalendarEventFontSize()) - GdiUtils.calcTextHeight(typeface,
                                        conf.getCalendarDayFontSize()) + conf.gap);
                                textAlign = Align.RIGHT;
                            } else {
                                eventX = conf.getLeftMargin() + dayStringWidth + conf.gap;
                                eventY = (GdiUtils.calcTextHeight(typeface, conf.getCalendarEventFontSize()) - GdiUtils.calcTextHeight(typeface,
                                        conf.getCalendarDayFontSize()) + conf.gap);
                                textAlign = Align.LEFT;
                            }
                        } else {
                            if (!conf.swapPosition) {
                                eventX = conf.screenWidth - conf.getRightMargin();
                                eventY = conf.gap + conf.getCalendarEventFontSize();
                                textAlign = Align.RIGHT;
                            } else {
                                eventX = conf.getLeftMargin();
                                eventY = conf.gap + conf.getCalendarEventFontSize();
                                textAlign = Align.LEFT;
                            }
                        }
                        drawString(canvas,
                                eventList.size() + " Events",
                                eventX + xWidgetOffset,
                                eventY + yOffset + dateOffset * scheduleOneDayHeight,
                                conf.getCalendarEventFontSize(),
                                conf.getForegroundColor(),
                                textAlign,
                                false);
                    }
                } else {
                    // イベント詳細も表示
                    int eventIndex = 0;
                    int eventCount = scheduleOneDayHeight / (conf.getCalendarEventFontSize() + conf.gap) - 1;
                    for (CalendarUtils.EventRecord event : eventList) {
                        if (StringUtils.isNotEmpty(conf.holidayKeyword) && conf.holidayKeyword.equals(event.getTitle()))
                            continue;

                        // 当日のイベントか
                        boolean beginToday = StringUtils.equals(currentDay, dateFormatter.format(event.getBegin()));

                        // 終日のイベントか
                        boolean notAllday = Boolean.FALSE.equals(event.getAllDay());

                        String eventStrnig = String.valueOf(event.getTitle());
                        if (conf.displayEventStartTime && event.getBegin() != null && notAllday && beginToday) {
                            eventStrnig = new SimpleDateFormat("HH:mm").format(event.getBegin()) + " - " + eventStrnig;
                        }

                        float eventX, eventY;
                        Align textAlign = null;
                        if (conf.isLandscape()) {
                            if (!conf.swapPosition) {
                                eventX = conf.screenWidth - conf.getRightMargin() - dayStringWidth - conf.gap;
                                eventY = (eventIndex * (conf.getCalendarEventFontSize() + conf.gap)
                                        + GdiUtils.calcTextHeight(typeface, conf.getCalendarEventFontSize())
                                        - GdiUtils.calcTextHeight(typeface, conf.getCalendarDayFontSize()) + conf.gap);
                                textAlign = Align.RIGHT;
                            } else {
                                eventX = conf.getLeftMargin() + dayStringWidth + conf.gap;
                                eventY = (eventIndex * (conf.getCalendarEventFontSize() + conf.gap)
                                        + GdiUtils.calcTextHeight(typeface, conf.getCalendarEventFontSize())
                                        - GdiUtils.calcTextHeight(typeface, conf.getCalendarDayFontSize()) + conf.gap);
                                textAlign = Align.LEFT;
                            }
                        } else {
                            if (!conf.swapPosition) {
                                eventX = conf.screenWidth - conf.getRightMargin();
                                eventY = conf.gap + conf.getCalendarEventFontSize() + eventIndex * (conf.getCalendarEventFontSize() + conf.gap);
                                textAlign = Align.RIGHT;
                            } else {
                                eventX = conf.getLeftMargin();
                                eventY = conf.gap + conf.getCalendarEventFontSize() + eventIndex * (conf.getCalendarEventFontSize() + conf.gap);
                                textAlign = Align.LEFT;
                            }
                        }
                        drawString(canvas,
                                eventStrnig,
                                eventX + xWidgetOffset,
                                eventY + yOffset + dateOffset * scheduleOneDayHeight,
                                conf.getCalendarEventFontSize(),
                                conf.getForegroundColor(),
                                textAlign,
                                false);

                        eventIndex++;
                        if (eventIndex >= eventCount) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private void drawString(Canvas canvas, String timeString, float x, float y, float fontSize, int color, Align align, boolean fakebold) {
        Path path = new Path();
        Paint paint = new Paint();
        paint.setTypeface(typeface);
        paint.setTextSize(fontSize);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(fakebold);
        paint.setStrokeWidth(conf.textEffectSize);
        paint.setTextAlign(align);
        paint.getTextPath(timeString, 0, timeString.length(), x, y, path);
        if (conf.textEffectType == Constants.TEXT_EFFECT_OUTLINE) {
            paint.setStyle(Style.STROKE);
            paint.setColor(conf.textShadowColor);
            canvas.drawPath(path, paint);
        }
        if (conf.textEffectType == Constants.TEXT_EFFECT_SHADOW) {
            paint.setShadowLayer(conf.textEffectSize, 0, 0, conf.textShadowColor);
        }
        if (conf.textEffectType == Constants.TEXT_EFFECT_DROP_SHADOW) {
            int offset = (int) GdiUtils.dp(context, 3);
            if (rotateDegree == 90) {
                paint.setShadowLayer(conf.textEffectSize, offset, -offset, conf.textShadowColor);
            } else if (rotateDegree == 270) {
                paint.setShadowLayer(conf.textEffectSize, -offset, +offset, conf.textShadowColor);
            } else {
                paint.setShadowLayer(conf.textEffectSize, offset, offset, conf.textShadowColor);
            }
        }
        paint.setStyle(Style.FILL);
        paint.setColor(color);
        canvas.drawPath(path, paint);
        path.reset();
    }

    /**
     * 背景画像の再読込を行います。
     */
    public void reloadBackgroundImage() {

        // 長辺の長さ
        int longLength = conf.isLandscape() ? conf.screenWidth : conf.screenHeight;
        // 短辺の長さ
        int shortLength = conf.isLandscape() ? conf.screenHeight : conf.screenWidth;

        String portraitimageFilepath = conf.PortraitimageFilepath;
        String landScapeFilepath = conf.LandScapeFilepath;

        if (StringUtils.isNotEmpty(conf.PortraitimageFilepath) && !conf.PortraitimageFilepath.equals("/") && new File(conf.PortraitimageFilepath).isDirectory()) {
            portraitimageFilepath = "";
            File[] files = new File(conf.PortraitimageFilepath).listFiles(new ImageFileFilter());
            if (0 < files.length) {
                //                int index = (int) (System.currentTimeMillis() / 1000 / 60) % (files.length);
                int index = conf.slideShowIndex % (files.length);
                portraitimageFilepath = files[index].getAbsolutePath();
            }
        }
        if (StringUtils.isNotEmpty(conf.LandScapeFilepath) && !conf.LandScapeFilepath.equals("/") && new File(conf.LandScapeFilepath).isDirectory()) {
            landScapeFilepath = "";
            File[] files = new File(conf.LandScapeFilepath).listFiles(new ImageFileFilter());
            if (0 < files.length) {
                //                int index = (int) (System.currentTimeMillis() / 1000 / 60) % (files.length);
                int index = conf.slideShowIndex % (files.length);
                landScapeFilepath = files[index].getAbsolutePath();
            }
        }

        // 縦画像の再読込
        if (verticalBgImage == null || !StringUtils.equals(portraitimageFilepath, verticalBitmapPath)) {
            try {
                // 現在の画像を破棄
                if (verticalBgImage != null) {
                    verticalBgImage.recycle();
                    verticalBgImage = null;
                }
                // 画像のロード
                if (StringUtils.isNotEmpty(portraitimageFilepath)) {
                    verticalBgImage = GdiUtils.loadResizedImage(portraitimageFilepath, shortLength, longLength, true);
                }
                // 読み込み済みの画像ファイルパスを退避
                if (verticalBgImage != null) {
                    verticalBitmapPath = portraitimageFilepath;
                }
            } catch (Exception e) {
                verticalBgImage = null;
                verticalBitmapPath = null;
                Log.e(Constants.TAG, e.getMessage(), e);
            } catch (OutOfMemoryError e) {
                verticalBgImage = null;
                verticalBitmapPath = null;
            }
        }

        // 横画像の再読込
        if (horizontalBgImage == null || !StringUtils.equals(landScapeFilepath, horizontalBitmapPath)) {
            try {
                // 現在の画像を破棄
                if (horizontalBgImage != null) {
                    horizontalBgImage.recycle();
                    horizontalBgImage = null;
                }
                // 画像のロード
                if (StringUtils.isNotEmpty(landScapeFilepath)) {
                    horizontalBgImage = GdiUtils.loadResizedImage(landScapeFilepath, longLength, shortLength, true);
                }
                // 読み込み済みの画像ファイルパスを退避
                if (horizontalBgImage != null) {
                    horizontalBitmapPath = landScapeFilepath;
                }
            } catch (Exception e) {
                horizontalBgImage = null;
                horizontalBitmapPath = null;
                Log.e(Constants.TAG, e.getMessage(), e);
            } catch (OutOfMemoryError e) {
                horizontalBgImage = null;
                horizontalBitmapPath = null;
            }
        }
     }

    /**
     * バッテリー情報の文字列を生成します。
     *
     * @return
     */
    private String formatBattryString() {
        String batteryPercentString = "BATTERY " + conf.getBatteryLevel() + "%";
        return StringUtils.explode(new String[]{batteryPercentString, conf.getBatteryStatus(), conf.batteryPlugged}, " - ");
    }

    /**
     * 日付の文字列を生成します。
     *
     * @param currentDate
     * @return
     */
    private String formatDateString(Date currentDate) {
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(currentDate);
        String weekDayName = conf.getWeekDayName(currentDate.getDay());
        return StringUtils.explode(new String[]{dateString, weekDayName}, " ");
    }

    /**
     * typefaceを設定します。
     *
     * @param typeface typeface
     */
    public void setTypeface(Typeface typeface) {
        WallpaperRenderer.typeface = typeface;
    }
}
