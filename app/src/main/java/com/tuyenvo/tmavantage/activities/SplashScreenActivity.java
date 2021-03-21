package com.tuyenvo.tmavantage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.airbnb.lottie.LottieAnimationView;
import com.tuyenvo.tmavantage.R;

public class SplashScreenActivity extends AppCompatActivity {

    Animation layoutAnimation;
    LottieAnimationView ltSupport;
    int fallDownDelay = 400;
    long splashScreenDelay = 2000;
    /*TextView textView;
    CharSequence charSequence;
    int index;
    long delay = 200;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ltSupport = findViewById(R.id.ltSupport);
        layoutAnimation = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.fall_down);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ltSupport.setVisibility(View.VISIBLE);
                ltSupport.setAnimation(layoutAnimation);
            }
        }, fallDownDelay);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                Intent intent = new Intent(SplashScreenActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        }, splashScreenDelay);

        //animateText("Vantage");
    }

    /*Runnable runnable = new Runnable() {
        @Override
        public void run() {
            textView.setText(charSequence.subSequence(0, index++));

            if (index <= charSequence.length()){
                handler.postDelayed(runnable, delay);
            }
        }
    };

    public void animateText(CharSequence cs){
        charSequence = cs;
        index = 0;
        textView.setText("");
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,delay);
    }*/
}