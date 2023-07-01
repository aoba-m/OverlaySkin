package com.moegoto.wallpaper.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

public class ThumbnailManager {

    private static ThumbnailManager manager;
    private Handler handler = new Handler();

    public static ThumbnailManager getInstance() {
        if (manager == null) {
            manager = new ThumbnailManager();
        }
        return manager;
    }

    private Queue<ThumbnailTask> taskQueue = new LinkedList<ThumbnailTask>();

    private Thread thread;
    private int avaiableThreads = 0;

    public void add(File targetFile, Context context, ThumbnailHandler handler) {
        ThumbnailTask task = new ThumbnailTask();
        task.targetFile = targetFile;
        task.handler = handler;
        task.started = false;
        task.finished = false;
        task.success = false;
        task.context = context;
        taskQueue.add(task);
        startTask();
    }

    public void startTask() {
        if (avaiableThreads > 3) {
            return;
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                avaiableThreads++;
                while (taskQueue.size() > 0) {
                    ThumbnailTask task = taskQueue.poll();
                    doTask(task);
                }
                avaiableThreads--;
            }
        });
        thread.start();
    }

    public void doTask(final ThumbnailTask task) {
        if (task == null) {
            return;
        }
        task.started = true;
        task.bitmap = GdiUtils.loadResizedImage(task.targetFile.getAbsolutePath(),
                (int) GdiUtils.dp(task.context, 40),
                (int) GdiUtils.dp(task.context, 40),
                false);
        if (task.bitmap != null) {
            task.success = true;
            if (task.handler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.handler.finish(task);
                    }
                });
            }
        } else {
            task.success = false;
        }
        task.finished = true;
    }
}