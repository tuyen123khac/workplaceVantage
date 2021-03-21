package com.tuyenvo.tmavantage.activities;

import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tuyenvo.tmavantage.adapters.TabPagerAdapter;
import com.tuyenvo.tmavantage.R;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setAdapter(new TabPagerAdapter(this));
        final TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0: {
                        //tab.setText("Profile");
                        tab.setIcon(R.drawable.ic_profile);
                        BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                        badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_a700));
                        badgeDrawable.setVisible(false);
                        break;
                    }
                    case 1: {
                        //tab.setText("Contacts");
                        tab.setIcon(R.drawable.ic_contact);
                        BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                        badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_a700));
                        badgeDrawable.setVisible(false);
                        break;
                    }
                    case 2:{
                        //tab.setText("Messages");
                        tab.setIcon(R.drawable.ic_message);
                        BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                        badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_a700));
                        badgeDrawable.setVisible(false);
                        break;
                    }
                    default: {
                        //tab.setText("History");
                        tab.setIcon(R.drawable.ic_history);
                        BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                        badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_a700));
                        badgeDrawable.setVisible(false);
                        break;
                    }
                }

            }
        });
        tabLayoutMediator.attach();
    }
}