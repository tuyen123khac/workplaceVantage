package com.tuyenvo.tmavantage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.sinch.android.rtc.internal.client.ConnectivityListener;
import com.tuyenvo.tmavantage.R;
import com.tuyenvo.tmavantage.databinding.ItemContainerMessageBinding;
import com.tuyenvo.tmavantage.listener.ConversationItemListener;
import com.tuyenvo.tmavantage.models.ConversationItem;

import java.util.List;

public class ConversationItemAdapter extends RecyclerView.Adapter<ConversationItemAdapter.MessengerUserViewHolder> {
    private List<ConversationItem> conversationItems;
    private LayoutInflater layoutInflater;
    private ConversationItemListener conversationItemListener;

    public ConversationItemAdapter(List<ConversationItem> conversationItems, ConversationItemListener conversationItemListener) {
        this.conversationItems = conversationItems;
        this.conversationItemListener = conversationItemListener;
    }

    @NonNull
    @Override
    public MessengerUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemContainerMessageBinding messageBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.item_container_message, parent, false
        );
        return new MessengerUserViewHolder(messageBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessengerUserViewHolder holder, int position) {
        holder.bindConversationInfo(conversationItems.get(position));
    }

    @Override
    public int getItemCount() {
        return conversationItems.size();
    }

    class MessengerUserViewHolder extends RecyclerView.ViewHolder{
        private ItemContainerMessageBinding itemContainerMessageBinding;

        public MessengerUserViewHolder(ItemContainerMessageBinding itemContainerMessageBinding){
            super(itemContainerMessageBinding.getRoot());
            this.itemContainerMessageBinding = itemContainerMessageBinding;
        }

        public void bindConversationInfo(ConversationItem conversationItem){
            itemContainerMessageBinding.setConversationItem(conversationItem);
            itemContainerMessageBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    conversationItemListener.onConversationClicked(conversationItem);
                }
            });
        }
    }
}
