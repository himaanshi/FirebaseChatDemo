package com.paxcel.firebasechatdemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private MainActivity mainActivity;
    private ArrayList<User> userArrayList;

    RecyclerViewAdapter(MainActivity mainActivity, ArrayList<User> userArrayList) {
        this.mainActivity = mainActivity;
        this.userArrayList = userArrayList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_user_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.setText(holder.tvPhoneNumber, userArrayList.get(position).getPhoneNumber());
        holder.setText(holder.tvId, userArrayList.get(position).getuId());
        holder.layout_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.startActivity(new Intent(mainActivity, Chat.class)
                        .putExtra("userId", userArrayList.get(position).getuId())
                        .putExtra("number", userArrayList.get(position).getPhoneNumber()));

                //mainActivity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPhoneNumber, tvId;
        CardView layout_card;

        ViewHolder(View itemView) {
            super(itemView);

            tvId = itemView.findViewById(R.id.tv_id);
            tvPhoneNumber = itemView.findViewById(R.id.tv_phone_number);
            layout_card = itemView.findViewById(R.id.layout_card);

        }

        void setText(TextView textView, String text) {
            textView.setText(text);
        }

        void onClick() {
            layout_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.startActivity(new Intent(mainActivity, ChatActivity.class));

                }
            });
        }
    }
}
