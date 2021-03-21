package com.tuyenvo.tmavantage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuyenvo.tmavantage.models.OnBoardingItem;
import com.tuyenvo.tmavantage.R;

import java.util.List;

public class OnBoardingAdapter extends RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder> {

    List<OnBoardingItem> onBoardingItemList;

    public OnBoardingAdapter(List<OnBoardingItem> onBoardingItemList) {
        this.onBoardingItemList = onBoardingItemList;
    }

    @NonNull
    @Override
    public OnBoardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnBoardingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_onboard, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OnBoardingViewHolder holder, int position) {
        holder.setOnBoardingData(onBoardingItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return onBoardingItemList.size();
    }

    class OnBoardingViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView title, description;

        public OnBoardingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageOnBoarding);
            title = itemView.findViewById(R.id.textTitle);
            description = itemView.findViewById(R.id.textDescription);
        }

        void setOnBoardingData(OnBoardingItem onBoardingItem) {
            imageView.setImageResource(onBoardingItem.getImage());
            title.setText(onBoardingItem.getTitle());
            description.setText(onBoardingItem.getDescription());
        }
    }
}
