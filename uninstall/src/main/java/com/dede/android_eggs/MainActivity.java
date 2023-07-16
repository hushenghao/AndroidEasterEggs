package com.dede.android_eggs;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String REASON_URL = "https://github.com/hushenghao/AndroidEasterEggs/blob/master/uninstall/README.md";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.tv_text);
        SpannableStringBuilder message = new SpannableStringBuilder("This App is designed to solve:\n")
                .append("Easter Eggs uninstall failed on some devices!\n",
                        new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                .append("Uninstall this app to uninstall Easter Eggs.");
        textView.setText(message);

        TextView tvReason = findViewById(R.id.tv_reason);
        tvReason.setPaintFlags(tvReason.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public void reason(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(REASON_URL))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void uninstall(View v) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:" + getPackageName()))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}