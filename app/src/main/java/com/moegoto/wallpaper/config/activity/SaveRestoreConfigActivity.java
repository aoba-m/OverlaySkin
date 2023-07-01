package com.moegoto.wallpaper.config.activity;

import java.io.File;
import java.io.IOException;

import android.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.moegoto.wallpaper.Constants;
import com.moegoto.wallpaper.OverlaySkinService;
import com.moegoto.wallpaper.component.AbstractConfig;
import com.moegoto.wallpaper.component.ConfigSeparator;
import com.moegoto.wallpaper.component.TextInputDialog;
import com.moegoto.wallpaper.preference.ConfigrationIO;
import com.moegoto.wallpaper.util.Alerter;
import com.moegoto.wallpaper.util.ListAdapter;
import com.moegoto.wallpaper.util.LocaleUtil;

public class SaveRestoreConfigActivity extends BaseListActivity {

    private Context ctx;
    private ListAdapter adapter;

    public SaveRestoreConfigActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        adapter = new ListAdapter(ctx);
        resetList();
        getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(adapter);
        getListView().setOnItemLongClickListener(adapter);
    }

    private void resetList() {
        adapter.clear();
        adapter.add(new ConfigSeparator(LocaleUtil.locale("設定を保存", "Save Settings")));
        adapter.add(new SaveAsListItem(LocaleUtil.locale("名前を付けて保存", "Save as")));
        adapter.add(new ConfigSeparator(LocaleUtil.locale("設定を読み込み", "Load Settings")));

        File sdcardDir = createSaveDir();
        if (!sdcardDir.exists()) {
            // ディレクトリが用意できない
            Alerter.showOkMessage(getApplicationContext(), "", LocaleUtil.locale("SDカードが存在しないため保存は行えません。", "SD Card is not found. Cannot Save."));
            return;
        }

        // ファイルを検索
        File[] files = sdcardDir.listFiles();
        for (File file : files) {
            adapter.add(new ReadFileListItem(file));
        }
        adapter.notifyDataSetChanged();
    }

    private class SaveAsListItem extends AbstractConfig {

        public SaveAsListItem(String title) {
            super(title, R.drawable.ic_menu_save);
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, View view) {

            // SDカードが存在しない場合はエラー
            String status = Environment.getExternalStorageState();
            if (!status.equals(Environment.MEDIA_MOUNTED)) {
                Alerter.showOkMessage(parent.getContext(), "", LocaleUtil.locale("SDカードが存在しないため保存は行えません。", "SD Card is not found. Cannot Save."));
                return;
            }

            final File sdcardDir = createSaveDir();
            if (!sdcardDir.exists()) {
                // ディレクトリが用意できない
                Alerter.showOkMessage(parent.getContext(), "", LocaleUtil.locale("SDカードが存在しないため保存は行えません。", "SD Card is not found. Cannot Save."));
            } else {
                // 保存確認ダイアログ
                final TextInputDialog dialog = new TextInputDialog(parent.getContext(),
                        LocaleUtil.locale("名前を入力してください", "Please Input Save Name"),
                        LocaleUtil.locale("次の文字は使用できません。\n「<>:*?\"/\\|」", "Can't use following characters.\n「<>:*?\"/\\|」")) {
                    @Override
                    public void onOkClick(CharSequence inputedString) {
                        // 同じ名前が存在するか確認
                        final File file = new File(sdcardDir, inputedString + ".xml");
                        if (file.exists()) {
                            Builder alertDialog = new Builder(parent.getContext());
                            alertDialog.setTitle(LocaleUtil.locale("同名の設定が保存します", "Existing same name file."));
                            alertDialog.setItems(new String[] { LocaleUtil.locale("上書き", "Overwrite"), LocaleUtil.locale("取消", "Cancel") },
                                    new OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {
                                                saveToFile(parent.getContext(), file);
                                                cancel();
                                                dialog.cancel();
                                            } else {
                                                cancel();
                                                dialog.cancel();
                                            }
                                        }
                                    });
                            alertDialog.show();
                        } else {
                            saveToFile(parent.getContext(), file);
                            cancel();
                        }
                    }
                };
                dialog.show();
            }
        }

    }

    private class ReadFileListItem extends AbstractConfig {

        private final File file;
        private ProgressDialog progress;

        public ReadFileListItem(File file) {
            super(file.getName(), R.drawable.ic_menu_revert);
            this.file = file;
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, View view) {
            Builder alertDialog = new Builder(parent.getContext());
            alertDialog.setItems(new String[] { LocaleUtil.locale("読込", "Restore"), LocaleUtil.locale("取消", "Cancel") }, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {

                        progress = new ProgressDialog(parent.getContext());
                        progress.setTitle("Please wait");
                        progress.setMessage("Restoring data...");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setCancelable(false);
                        progress.show();

                        final Handler handler = new ReadFromFileHandler(parent.getContext(), file, progress);

                        (new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    Log.e("Runnable", "InterruptedException");
                                }

                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("", "");
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        })).start();
                    }
                }
            });
            alertDialog.show();
        }

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, View view) {
            Builder alertDialog = new Builder(parent.getContext());
            alertDialog.setItems(new String[] { LocaleUtil.locale("上書き", "Overwrite"), LocaleUtil.locale("削除", "Delete"), LocaleUtil.locale("取消", "Cancel") },
                    new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                // 上書き
                                saveToFile(parent.getContext(), file);
                            }
                            if (which == 1) {
                                // 削除
                                file.delete();
                                Alerter.showOkMessage(parent.getContext(), "", LocaleUtil.locale("削除が完了しました。", "Delete complete."));
                                resetList();
                            }
                        }
                    });
            alertDialog.show();
            return true;
        }
    }

    private File createSaveDir() {
        // 保存用ディレクトリを作る
        final File sdcardDir = new File("/sdcard/data/overlaySkin/");
        if (!sdcardDir.exists()) {
            Log.w(Constants.TAG, "Create dir. " + sdcardDir.getAbsolutePath());
            sdcardDir.mkdirs();
        }
        return sdcardDir;
    }

    private void saveToFile(Context context, File file) {
        SharedPreferences preferences = ctx.getSharedPreferences(OverlaySkinService.SHARED_PREFS_NAME, 0);
        // XML形式で保存
        ConfigrationIO io = new ConfigrationIO();
        try {
            io.exportConfig(file, preferences, ctx);
            Alerter.showOkMessage(context, "", LocaleUtil.locale("保存が完了しました。", "Save complete."));
        } catch (IOException e) {
            Log.e(Constants.TAG, e.getMessage(), e);
            Alerter.showOkMessage(context, "", LocaleUtil.locale("保存処理に失敗しました。", "Save failed."));
        }
        resetList();
    }

    private class ReadFromFileHandler extends Handler {

        private Context context;
        private File file;
        private final ProgressDialog progress;

        private ReadFromFileHandler(Context context, File file, ProgressDialog progress) {
            this.context = context;
            this.file = file;
            this.progress = progress;
        }

        @Override
        public void handleMessage(Message msg) {
            SharedPreferences preferences = ctx.getSharedPreferences(OverlaySkinService.SHARED_PREFS_NAME, 0);
            ConfigrationIO io = new ConfigrationIO();
            try {
                io.importConfig(file, preferences, ctx);
                Alerter.showOkMessage(context, "", LocaleUtil.locale("読込が完了しました。", "Restore complete."));
            } catch (IOException e) {
                Log.e(Constants.TAG, e.getMessage(), e);
                Alerter.showOkMessage(context, "", LocaleUtil.locale("読込処理に失敗しました。", "Restore failed."));
            } finally {
                progress.dismiss();
            }
        }
    }
}
