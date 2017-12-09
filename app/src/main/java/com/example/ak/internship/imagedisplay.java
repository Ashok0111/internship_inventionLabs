package com.example.ak.internship;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ak on 08-12-2017.
 */

public class imagedisplay extends Activity {

    SliderPagerAdapter sliderPagerAdapter;
    ArrayList<String> slider_image_list;
    int page_position = 0;
    Button sa;
    private ViewPager vp_slider;
    private LinearLayout ll_dots;
    private TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.imagedisplay);
        Typeface fs2 = Typeface.createFromAsset(getAssets(), "cc.ttf");
        sa = (Button) findViewById(R.id.sa);
        sa.setTypeface(fs2);
// method for initialisation
        init();

// method for adding indicators
        addBottomDots(0);


    }

    public void goback(View v) {
        Intent i = new Intent(imagedisplay.this, MainActivity.class);
        startActivity(i);
        Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(75);
    }

    private void init() {


        vp_slider = (ViewPager) findViewById(R.id.vp_slider);
        ll_dots = (LinearLayout) findViewById(R.id.ll_dots);

        slider_image_list = new ArrayList<>();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("urls", 0); // 0 - for private mode
        SharedPreferences pref1 = getApplicationContext().getSharedPreferences("tags", 0);
        SharedPreferences pref2 = getApplicationContext().getSharedPreferences("len", 0);// 0 - for private mode

        int le = pref2.getInt("length", 0);
        if (le != 0) {
            for (int k = 0; k < le; k++) {

                slider_image_list.add(pref.getString("url" + k, null));

            }
        } else {
            Intent i = new Intent(imagedisplay.this, MainActivity.class);
            startActivity(i);
        }


        sliderPagerAdapter = new SliderPagerAdapter(imagedisplay.this, slider_image_list);
        vp_slider.setAdapter(sliderPagerAdapter);

        vp_slider.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[slider_image_list.size()];

        ll_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#000000"));
            ll_dots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(Color.parseColor("#FFFFFF"));
    }
}
