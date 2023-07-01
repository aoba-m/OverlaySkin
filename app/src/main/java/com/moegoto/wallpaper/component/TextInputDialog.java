package com.moegoto.wallpaper.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moegoto.wallpaper.util.LocaleUtil;

public abstract class TextInputDialog extends Dialog {

    private Context context;
    private EditText editText;
    private final String title;
    private final String helpText;

    public TextInputDialog(Context context, String title, String helpText) {
        super(context);
        this.context = context;
        this.title = title;
        this.helpText = helpText;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(createView());
    }

    private View createView() {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        setTitle(title);

        TextView textView = new TextView(context);
        textView.setTextSize(18);
        textView.setTextColor(Color.WHITE);
        textView.setText(helpText);
        layout.addView(textView);

        editText = new EditText(context);
        layout.addView(editText, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        layout.addView(new TextView(context));

        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.VERTICAL);
        buttonLayout.setGravity(Gravity.CENTER);

        Button okButton = new Button(context);
        okButton.setText(LocaleUtil.locale("決定", "OK"));
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOkClick(editText.getText());
            }
        });
        buttonLayout.addView(okButton);
        layout.addView(buttonLayout);
        return layout;
    }

    public abstract void onOkClick(CharSequence inputedString);
}
