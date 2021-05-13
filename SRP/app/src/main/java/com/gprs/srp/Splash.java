package com.gprs.srp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Splash extends AppCompatActivity {

    static int splash = 5000;
    Animation top, bottom;
    TextView state, appaname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("language", 0); // 0 - for private mode

        switch (pref.getString("lang", "")) {
            case "en":
                setAppLocale("en");
                break;
            case "hi":
                setAppLocale("hi");
                break;
            case "ta":
                setAppLocale("ta");
                break;
            case "te":
                setAppLocale("te");
                break;
            case "kn":
                setAppLocale("kn");
                break;
            case "ml":
                setAppLocale("ml");
                break;
            case "pa":
                setAppLocale("pa");
                break;
            case "gu":
                setAppLocale("gu");
                break;
            case "mr":
                setAppLocale("mr");
                break;
            case "or":
                setAppLocale("or");
                break;
            default:
                setAppLocale("en");
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);


        top = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        bottom = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);



        state = findViewById(R.id.statename);

        appaname = findViewById(R.id.appname);


        appaname.setAnimation(bottom);
        state.setAnimation(bottom);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, intro.class);


                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(appaname, "logo_image");
                pairs[1] = new Pair<View, String>(state, "logo_text");
                //wrap the call in API level 21 or higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Splash.this, pairs);
                    startActivity(intent, options.toBundle());
                }

                finish();
            }
        }, splash);
    }

    private void setAppLocale(String localeCode) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
    }


}
