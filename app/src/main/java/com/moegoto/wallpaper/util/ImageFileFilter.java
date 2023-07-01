package com.moegoto.wallpaper.util;

import java.io.File;
import java.io.FileFilter;

public class ImageFileFilter implements FileFilter {

    @Override
    public boolean accept(File file) {
        String filePath = file.getAbsolutePath().toLowerCase();
        return (file.isDirectory() || filePath.endsWith(".jpg") || filePath.endsWith(".jpeg") || filePath.endsWith(".png") || filePath.endsWith(".gif")) && file.canRead();
    }

}
