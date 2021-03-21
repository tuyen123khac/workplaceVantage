package com.tuyenvo.tmavantage.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tuyenvo.tmavantage.R;
import com.tuyenvo.tmavantage.adapters.ConversationItemAdapter;
import com.tuyenvo.tmavantage.databinding.ActivityMessengerBinding;
import com.tuyenvo.tmavantage.listener.ConversationItemListener;
import com.tuyenvo.tmavantage.models.ConversationItem;
import com.tuyenvo.tmavantage.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class MessengerActivity extends AppCompatActivity implements ConversationItemListener {
    ActivityMessengerBinding activityMessengerBinding;
    ConversationItemAdapter conversationItemAdapter;
    List<ConversationItem> conversationItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMessengerBinding = DataBindingUtil.setContentView(this, R.layout.activity_messenger);

        doInitialization();
    }

    private void doInitialization() {
        conversationItems = new ArrayList<>();
        ConversationItem user1 = new ConversationItem(R.drawable.avatar, "Doan chat 1", "Noi dung doan chat 1");
        ConversationItem user2 = new ConversationItem(R.drawable.avatar, "Doan chat 2", "Noi dung doan chat 2");
        conversationItems.add(user1);
        conversationItems.add(user2);

        conversationItemAdapter = new ConversationItemAdapter(conversationItems, this);
        activityMessengerBinding.messengerRecyclerView.setHasFixedSize(true);
        activityMessengerBinding.messengerRecyclerView.setAdapter(conversationItemAdapter);
    }


    @Override
    public void onConversationClicked(ConversationItem conversationItem) {
        Intent intent = new Intent(MessengerActivity.this, ConversationDetailActivity.class);
        startActivity(intent);
    }
}