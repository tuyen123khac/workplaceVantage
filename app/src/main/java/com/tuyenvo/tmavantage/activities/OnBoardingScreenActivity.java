package com.tuyenvo.tmavantage.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.tuyenvo.tmavantage.adapters.OnBoardingAdapter;
import com.tuyenvo.tmavantage.models.OnBoardingItem;
import com.tuyenvo.tmavantage.R;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingScreenActivity extends AppCompatActivity {
    private OnBoardingAdapter onBoardingAdapter;
    private LinearLayout linearOnBoardingIndicator;
    private MaterialButton onBoardingAction, onBoardingSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_screen);

        linearOnBoardingIndicator = findViewById(R.id.onBoardingLayoutIndicator);
        onBoardingAction = findViewById(R.id.onBoardActionButton);
        onBoardingSkip = findViewById(R.id.onBoardSkipButton);
        final ViewPager2 onBoardingViewPager = findViewById(R.id.onBoardingViewPager);

        setUpOnBoardingItem();
        onBoardingViewPager.setAdapter(onBoardingAdapter);
        setupOnBoardingIndicators();
        setCurrentOnBoardingIndicators(0);
        onBoardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnBoardingIndicators(position);
            }
        });
        onBoardingAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBoardingViewPager.getCurrentItem() + 1 < onBoardingAdapter.getItemCount()) {
                    onBoardingViewPager.setCurrentItem(onBoardingViewPager.getCurrentItem() + 1);
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        onBoardingSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setUpOnBoardingItem() {

        List<OnBoardingItem> onBoardingItem = new ArrayList<>();

        OnBoardingItem connectEveryOne = new OnBoardingItem();
        connectEveryOne.setImage(R.drawable.connect_every_one);
        connectEveryOne.setTitle("Connect Everyone");
        connectEveryOne.setDescription("Bring people together every time and every where");

        OnBoardingItem collaborationWork = new OnBoardingItem();
        collaborationWork.setImage(R.drawable.work_together);
        collaborationWork.setTitle("Collaboration work");
        collaborationWork.setDescription("Work closely through conference server.");

        OnBoardingItem oasisConnect = new OnBoardingItem();
        oasisConnect.setImage(R.drawable.oasis);
        oasisConnect.setTitle("Light weight data");
        oasisConnect.setDescription("Don't worry if you are in an oasis or desert island. The sharks will deliver your data");

        onBoardingItem.add(connectEveryOne);
        onBoardingItem.add(collaborationWork);
        onBoardingItem.add(oasisConnect);

        onBoardingAdapter = new OnBoardingAdapter(onBoardingItem);

    }

    private void setupOnBoardingIndicators() {
        ImageView[] indicators = new ImageView[onBoardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            linearOnBoardingIndicator.addView(indicators[i]);
        }
    }

    public void setCurrentOnBoardingIndicators(int index) {
        int childCount = linearOnBoardingIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) linearOnBoardingIndicator.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive));
            }
        }

        if (index == (onBoardingAdapter.getItemCount() - 1)) {
            onBoardingAction.setText("Start");
        } else {
            onBoardingAction.setText("Next");
        }
    }
}