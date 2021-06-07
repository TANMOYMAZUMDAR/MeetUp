package com.example.demo_project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.Element;
import mehdi.sakout.aboutpage.AboutPage;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        changeStatusBarColor();

        Element adsElement = new Element();
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("Meet Up is a fast,simple,secure and reliable messaging  with strangers with common interest anywhere anytime in the world." +
                                                 "You get to know more about your Interest." +
                        "Set emotions to the messages you send to express it more."+
                "Group chat feature to chat with all users and get to know about others with differnet Interest and get to grow yourself.")
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("CONNECT WITH US!")
                .addEmail("tmazumdar0@gmail.com")
                .addWebsite("www.exposysdata.com/")
                .addYoutube("UCCdSuhhzWqmj9h9uyEl-JSA")   //Enter your youtube link here (replace with my channel link)
                .addPlayStore("com.example.demo_project")   //Replace all this with your package name
                .addInstagram("exposysdatalabs")    //Your instagram id
                .addItem(createCopyright())
                .create();
        setContentView(aboutPage);
    }
    private Element createCopyright()
    {
        Element copyright = new Element();
        @SuppressLint("DefaultLocale") final String copyrightString = String.format("Copyright %d TANMOY", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
         copyright.setIconDrawable(R.mipmap.ic_launcher);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this,copyrightString, Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;
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
