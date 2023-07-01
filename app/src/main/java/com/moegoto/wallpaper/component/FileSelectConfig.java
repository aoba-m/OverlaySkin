package com.moegoto.wallpaper.component;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.moegoto.wallpaper.OverlaySkinService;
import com.moegoto.wallpaper.util.GdiUtils;
import com.moegoto.wallpaper.util.ListAdapterItem;

public class FileSelectConfig extends ListAdapterItem {

    private final String title;
    private final String configKey;
    private String defaultValue;
    private File selectedFile;
    private TextView summeryView;
    private TextView titleView;
    private SharedPreferences preferences;

    public FileSelectConfig(String title, String configKey, String defaultValue) {
        super();
        this.title = title;
        this.configKey = configKey;
        this.defaultValue = defaultValue;
    }

    @Override
    public View getView(Context context, View convertView, ViewGroup parent) {
        preferences = context.getSharedPreferences(OverlaySkinService.SHARED_PREFS_NAME, 0);
        selectedFile = new File(preferences.getString(configKey, defaultValue));

        LinearLayout layout = new LinearLayout(context);

        layout.setOrientation(LinearLayout.VERTICAL);
        titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(18);
        titleView.setGravity(Gravity.BOTTOM);

        summeryView = new TextView(context);
        summeryView.setText(selectedFile == null ? "" : selectedFile.getAbsolutePath());
        summeryView.setTextColor(Color.BLACK);
        summeryView.setTextSize(16);
        summeryView.setGravity(Gravity.TOP);
        summeryView.setSingleLine(true);

        layout.addView(titleView, new LayoutParams(LayoutParams.FILL_PARENT, GdiUtils.realDp(context, 30)));
        layout.addView(summeryView, new LayoutParams(LayoutParams.FILL_PARENT, GdiUtils.realDp(context, 30)));

        return layout;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view) {
        FileSelectPreferenceActivity fileListDialog = new FileSelectPreferenceActivity(parent.getContext());
        fileListDialog.show(selectedFile);
    }

    public class FileSelectPreferenceActivity extends Activity implements DialogInterface.OnClickListener {

        private final Context context;
        private List<File> fileList;
        private int selectCount = -1;
        private String currentDirectory;

        public FileSelectPreferenceActivity(Context context) {
            super();
            this.context = context;
        }

        public String getSelectedFileName() {
            if (selectCount < 0) {
                return "";
            }
            return currentDirectory + "/" + fileList.get(selectCount).getName();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            selectCount = which;
            if (fileList != null) {
                File file = fileList.get(which);
                if (file.isDirectory()) {
                    show(file);
                } else {
                    selectedFile = file;
                    summeryView.setText(file.getAbsolutePath());
                    Editor editor = preferences.edit();
                    editor.putString(configKey, selectedFile.getAbsolutePath());
                    editor.commit();
                }
            }
        }

        public void show(File path) {
            if (path == null) {
                path = new File("/");
                this.currentDirectory = "/";
            } else if (!path.isDirectory()) {
                this.currentDirectory = path.getParent();
                path = path.getParentFile();
            } else {
                this.currentDirectory = path.getAbsolutePath();
            }
            String title = currentDirectory;
            try {
                File[] files = path.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return (file.isDirectory() || file.getAbsolutePath().endsWith(".jpg") || file.getAbsolutePath().endsWith(".png") || file.getAbsolutePath()
                                .endsWith(".gif")) && file.canRead();
                    }
                });
                if (files == null) {
                    selectedFile = null;
                } else {
                    boolean hasParentDir = false;
                    Arrays.sort(files);
                    fileList = new ArrayList<File>(Arrays.asList(files));
                    if (!"/".equals(currentDirectory)) {
                        fileList.add(0, new File(currentDirectory).getParentFile());
                        hasParentDir = true;
                    }
                    String[] list = new String[fileList.size()];
                    for (int i = 0; i < fileList.size(); i++) {
                        File file = fileList.get(i);
                        if (i == 0 && hasParentDir) {
                            list[i] = "../";
                        } else if (file.isDirectory()) {
                            list[i] = file.getName() + "/";
                        } else {
                            list[i] = file.getName();
                        }
                    }
                    new AlertDialog.Builder(context).setTitle(title).setItems(list, this).show();
                }
            } catch (SecurityException se) {
            } catch (Exception e) {
            }
        }
    }
}
