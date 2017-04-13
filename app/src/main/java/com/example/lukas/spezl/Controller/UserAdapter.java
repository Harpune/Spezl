package com.example.lukas.spezl.Controller;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lukas.spezl.Model.Event;
import com.example.lukas.spezl.Model.User;
import com.example.lukas.spezl.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private List<User> userList;

    public class UserHolder extends RecyclerView.ViewHolder {
        public TextView nameView, townView, ageView;
        public ImageView userImageView;

        public UserHolder(View view) {
            super(view);
            nameView = (TextView) view.findViewById(R.id.text_name);
            townView = (TextView) view.findViewById(R.id.text_town);
            ageView = (TextView) view.findViewById(R.id.text_age);
            userImageView = (ImageView) view.findViewById(R.id.userImage);
        }
    }

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.acticity_event_user_row, parent, false);
        return new UserHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UserHolder holder, int position) {
        User user = userList.get(position);
        Log.d("onBindViewHolder", user.getUserId());
        holder.nameView.setText(user.getUsername());
        holder.townView.setText(user.getTown());
        holder.ageView.setText(String.valueOf(user.getAge()));

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
