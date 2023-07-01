package com.moegoto.wallpaper.util;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;

public class ThumbnailTask {
    public boolean started;
    public boolean finished;
    public boolean success;
    public File targetFile;
    public Context context;
    public Bitmap bitmap;
    public ThumbnailHandler handler;
}
