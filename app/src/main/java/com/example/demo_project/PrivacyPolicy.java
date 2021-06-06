package com.example.demo_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class PrivacyPolicy extends AppCompatActivity {
WebView webview;
public String fileName="privacy.html";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        changeStatusBarColor();
        getSupportActionBar().setTitle("Privacy Policy");
        webview = (WebView) findViewById(R.id.privacyid);
        // displaying content in WebView from html file that stored in assets folder
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("file:///android_asset/" + fileName);

    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.colorDarkAccent));
        }
    }
}
