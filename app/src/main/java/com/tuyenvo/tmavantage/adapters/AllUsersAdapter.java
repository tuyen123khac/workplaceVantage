package com.tuyenvo.tmavantage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tuyenvo.tmavantage.fragments.ContactFragment;
import com.tuyenvo.tmavantage.models.User;
import com.tuyenvo.tmavantage.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.AllUsersViewHolder> implements Filterable {
    Fragment fragment;
    List<User> userArrayList;
    List<User> userArrayListFull;

    public AllUsersAdapter(Fragment fragment, List<User> userArrayList) {
        this.fragment = fragment;
        this.userArrayList = userArrayList;
    }

    public void setUserArrayListFull() {
        this.userArrayListFull = new ArrayList<>(this.userArrayList);
    }

    @NonNull
    @Override
    public AllUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.all_user,
                parent,
                false
        );

        AllUsersViewHolder allUsersViewHolder = new AllUsersViewHolder(view);
        return  allUsersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AllUsersViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.textViewName.setText(user.getUsername());
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class AllUsersViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        Button button;
        public AllUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.itemName);
            button = itemView.findViewById(R.id.callButton);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = userArrayList.get(getAdapterPosition());
                    //((ContactFragment)fragment).callUser(user);
                }
            });
        }
    }

    @Override
    public Filter getFilter() {
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filterList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filterList.addAll(userArrayListFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(User user : userArrayListFull){
                    if(user.getUsername().toLowerCase().contains(filterPattern)){
                        filterList.add(user);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filterList;

            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            userArrayList.clear();
            userArrayList.addAll((Collection<? extends User>) results.values);
            notifyDataSetChanged();
        }
    };
}
