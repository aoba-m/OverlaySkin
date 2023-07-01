package com.moegoto.wallpaper.component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.R;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout.LayoutParams;

import com.moegoto.wallpaper.OverlaySkinService;
import com.moegoto.wallpaper.util.GdiUtils;
import com.moegoto.wallpaper.util.ImageFileFilter;
import com.moegoto.wallpaper.util.ListAdapter;
import com.moegoto.wallpaper.util.ListAdapterItem;
import com.moegoto.wallpaper.util.LocaleUtil;
import com.moegoto.wallpaper.util.ThumbnailHandler;
import com.moegoto.wallpaper.util.ThumbnailManager;
import com.moegoto.wallpaper.util.ThumbnailTask;

public class ImageSelectConfig extends ListAdapterItem {

    private final String title;
    private final String configKey;
    private String defaultValue;
    private File selectedFile;
    private TextView summeryView;
    private TextView titleView;
    private ImageView thumnail;
    private SharedPreferences preferences;

    public ImageSelectConfig(String title, String configKey, String defaultValue) {
        super();
        this.title = title;
        this.configKey = configKey;
        this.defaultValue = defaultValue;
    }

    public File getDefaultPath() {
        return Environment.getExternalStorageDirectory();
    }

    @Override
    public View getView(Context context, View convertView, ViewGroup parent) {
        preferences = context.getSharedPreferences(OverlaySkinService.SHARED_PREFS_NAME, 0);
        selectedFile = new File(preferences.getString(configKey, defaultValue));

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout rowLayout = new LinearLayout(context);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);

        titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(18);
        titleView.setGravity(Gravity.BOTTOM);

        thumnail = new ImageView(context);
        thumnail.setImageResource(R.drawable.ic_menu_gallery);

        updateIconImage(context);

        summeryView = new TextView(context);
        summeryView.setText(selectedFile == null ? "" : selectedFile.getAbsolutePath());
        summeryView.setTextColor(Color.BLACK);
        summeryView.setTextSize(16);
        summeryView.setGravity(Gravity.TOP);
        summeryView.setSingleLine(true);

        layout.addView(titleView, new LayoutParams(LayoutParams.FILL_PARENT, GdiUtils.realDp(context, 30)));
        layout.addView(summeryView, new LayoutParams(LayoutParams.FILL_PARENT, GdiUtils.realDp(context, 30)));

        LayoutParams params = new LayoutParams(GdiUtils.realDp(context, 40), GdiUtils.realDp(context, 40));
        params.setMargins(GdiUtils.realDp(context, 7), 0, GdiUtils.realDp(context, 7), 0);
        params.gravity = Gravity.CENTER;

        rowLayout.addView(thumnail, params);
        rowLayout.addView(layout);

        return rowLayout;
    }


    private void updateIconImage(Context context) {
        if (selectedFile.isDirectory()) {
            thumnail.setImageResource(R.drawable.ic_menu_gallery);
        } else {
            ThumbnailManager thumbnailManager = ThumbnailManager.getInstance();
            thumbnailManager.add(selectedFile, context, new ThumbnailHandler() {
                @Override
                public void finish(ThumbnailTask task) {
                    thumnail.setImageBitmap(task.bitmap);
                }
            });
//            Bitmap bitmap = GdiUtils.loadResizedImage(selectedFile.getAbsolutePath(),
//                    (int) GdiUtils.dp(context, 40),
//                    (int) GdiUtils.dp(context, 40),
//                    false);
//            thumnail.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view) {
        FileSelectPreferenceActivity activity = new FileSelectPreferenceActivity(parent.getContext());
        activity.show();
    }

    public class FileSelectPreferenceActivity extends Dialog {
        private static final String CHECKBOX_KEY = "ImageSelectConfig.ViewImage";
        private final Context context;
        private ListView itemView;
        private ListAdapter adapter;
        private String currentDirectory;
        private CheckBox viewImageCheck;

        public FileSelectPreferenceActivity(Context context) {
            super(context, R.style.Theme_DeviceDefault_Light_Dialog);
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            setContentView(createView());
            getWindow().getAttributes().gravity = Gravity.TOP;
        }

        private View createView() {
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout buttonBar = new LinearLayout(context);
            {
                viewImageCheck = new CheckBox(context);
                viewImageCheck.setText(LocaleUtil.locale("サムネイル表示", "Display Thumbnail"));
                viewImageCheck.setTextColor(Color.BLACK);
                viewImageCheck.setChecked(preferences.getBoolean(CHECKBOX_KEY, false));
                viewImageCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundbutton, boolean flag) {
                        refreshFileList(new File(currentDirectory));
                        Editor editor = preferences.edit();
                        editor.putBoolean(CHECKBOX_KEY, viewImageCheck.isChecked());
                        editor.commit();
                    }
                });

                Button restoreButton = new Button(context);
                restoreButton.setText(LocaleUtil.locale("画像を解除", "Unbind Image"));
                restoreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedFile = getDefaultPath();
                        summeryView.setText(selectedFile.getAbsolutePath());
                        Editor editor = preferences.edit();
                        editor.putString(configKey, selectedFile.getAbsolutePath());
                        editor.commit();
                        updateIconImage(context);
                        cancel();
                    }
                });
                buttonBar.setOrientation(LinearLayout.HORIZONTAL);
                buttonBar.addView(viewImageCheck);
                buttonBar.addView(restoreButton);
            }
            adapter = new ListAdapter(context);
            itemView = new ListView(context);
            itemView.setAdapter(adapter);
            itemView.setOnItemClickListener(adapter);

            layout.addView(buttonBar, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            layout.addView(itemView);
            refreshFileList(selectedFile);

            return layout;
        }

        private void refreshFileList(File parentDirectory) {
            adapter.clear();

            if (parentDirectory == null) {
                parentDirectory = getDefaultPath();
                this.currentDirectory = getDefaultPath().getAbsolutePath();
            } else if (!parentDirectory.isDirectory()) {
                this.currentDirectory = parentDirectory.getParent();
                parentDirectory = parentDirectory.getParentFile();
            } else {
                this.currentDirectory = parentDirectory.getAbsolutePath();
            }
            setTitle(currentDirectory);
            try {
                File[] files = parentDirectory.listFiles(new ImageFileFilter());
                if (files == null) {
                    selectedFile = null;
                } else {
                    boolean hasParentDir = false;
                    Arrays.sort(files);
                    List<File> fileList = new ArrayList<File>(Arrays.asList(files));
                    if (!"/".equals(currentDirectory)) {
                        fileList.add(0, new File(currentDirectory).getParentFile());
                        hasParentDir = true;
                    }
                    for (int i = 0; i < fileList.size(); i++) {
                        File file = fileList.get(i);
                        if (i == 0 && hasParentDir) {
                            adapter.add(new ParentFileItem(parentDirectory));
                            adapter.add(new CurrentFolderFileItem(parentDirectory));
                        } else {
                            adapter.add(new FileItem(file));
                        }
                    }
                }
            } catch (SecurityException se) {
            } catch (Exception e) {
            }
            adapter.notifyDataSetChanged();
        }

        private class CurrentFolderFileItem extends ListAdapterItem {

            private final File dir;

            public CurrentFolderFileItem(File dir) {
                this.dir = dir;
            }

            @Override
            public View getView(Context context, View convertView, ViewGroup parent) {
                LinearLayout layout = new LinearLayout(context);
                TextView textView = new TextView(context);
                textView.setText(LocaleUtil.locale("このフォルダをスライドショー表示(スクリーンON毎に画像切替)", "Slideshow Current Folder (Image changes at Screen On)"));
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(18);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                if (viewImageCheck.isChecked()) {
                    ImageView imageView = new ImageView(context);
                    imageView.setBackgroundColor(Color.rgb(30, 30, 30));
                    layout.addView(imageView, new LayoutParams((int) GdiUtils.dp(context, 60), (int) GdiUtils.dp(context, 60)));
                }
                LayoutParams textLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                textLayout.leftMargin = (int) GdiUtils.dp(context, 5);
                layout.addView(textView, textLayout);
                return layout;
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view) {
                selectedFile = dir;
                summeryView.setText(dir.getAbsolutePath());
                Editor editor = preferences.edit();
                editor.putString(configKey, selectedFile.getAbsolutePath());
                editor.commit();
                updateIconImage(context);
                cancel();
            }
        }

        private class ParentFileItem extends ListAdapterItem {

            private final File dir;

            public ParentFileItem(File dir) {
                this.dir = dir;
            }

            @Override
            public View getView(Context context, View convertView, ViewGroup parent) {
                LinearLayout layout = new LinearLayout(context);
                TextView textView = new TextView(context);
                textView.setText("../");
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(18);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                if (viewImageCheck.isChecked()) {
                    ImageView imageView = new ImageView(context);
                    imageView.setBackgroundColor(Color.rgb(30, 30, 30));
                    layout.addView(imageView, new LayoutParams((int) GdiUtils.dp(context, 60), (int) GdiUtils.dp(context, 60)));
                }
                LayoutParams textLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, (int) GdiUtils.dp(context, 40));
                textLayout.leftMargin = (int) GdiUtils.dp(context, 5);
                layout.addView(textView, textLayout);
                return layout;
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view) {
                refreshFileList(dir.getParentFile());
            }
        }

        private class FileItem extends ListAdapterItem {
            private final File file;
            private Bitmap image = null;

            public FileItem(File file) {
                this.file = file;
            }

            @Override
            public View getView(Context context, View convertView, ViewGroup parent) {
                LinearLayout layout = new LinearLayout(context);
                TextView textView = new TextView(context);
                if (file.isDirectory()) {
                    textView.setText(file.getName() + "/");
                } else {
                    textView.setText(file.getName());
                }
                if (viewImageCheck.isChecked()) {
                    ImageView imageView = new ImageView(context);
                    if (!file.isDirectory()) {
                        ThumbnailManager thumbnailManager = ThumbnailManager.getInstance();
                        thumbnailManager.add(file, context, new ThumbnailHandler() {
                            @Override
                            public void finish(ThumbnailTask task) {
                                imageView.setImageBitmap(task.bitmap);
                            }
                        });
//
//                        if (image == null) {
//                            image = GdiUtils.loadResizedImage(file.getAbsolutePath(), (int) GdiUtils.dp(context, 60), (int) GdiUtils.dp(context, 60), false);
//                        }
//                        if (image != null) {
//                            imageView.setImageBitmap(image);
//                        }
                    }
                    imageView.setBackgroundColor(Color.rgb(30, 30, 30));
                    layout.addView(imageView, new LayoutParams((int) GdiUtils.dp(context, 60), (int) GdiUtils.dp(context, 60)));
                }
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(18);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                LayoutParams textLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, (int) GdiUtils.dp(context, 40));
                textLayout.leftMargin = (int) GdiUtils.dp(context, 5);
                layout.addView(textView, textLayout);
                return layout;
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view) {
                if (file.isDirectory()) {
                    refreshFileList(file);
                } else {
                    selectedFile = file;
                    summeryView.setText(file.getAbsolutePath());
                    Editor editor = preferences.edit();
                    editor.putString(configKey, selectedFile.getAbsolutePath());
                    updateIconImage(context);
                    editor.commit();
                    cancel();
                }
            }
        }
    }
}
