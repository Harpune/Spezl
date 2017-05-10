package com.dhbw.project.spezl.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhbw.project.spezl.model.User;
import com.dhbw.project.spezl.R;

import java.util.Calendar;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private List<User> userList;
    private Context context;

    class UserHolder extends RecyclerView.ViewHolder {
        TextView nameView, ageView;
        ImageView userImageView;

        UserHolder(View view) {
            super(view);
            nameView = (TextView) view.findViewById(R.id.text_name);
            ageView = (TextView) view.findViewById(R.id.text_age);
            userImageView = (ImageView) view.findViewById(R.id.userImage);
        }
    }

    public UserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_event_user_row, parent, false);
        return new UserHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UserHolder holder, int position) {
        // Get user from position.
        User user = userList.get(position);

        // Add values to views.
        holder.nameView.setText(user.getFirstName());

        // Display day of birth as number not as date.
        Calendar birthday = Calendar.getInstance();
        birthday.setTime(user.getAge());
        holder.ageView.setText(getAge(birthday) + " Jahre alt");

        // Change icons for sex.
        if(user.getSex() == null){
            holder.userImageView.setImageResource(R.drawable.ic_person);
        } else if (user.getSex()) {
            holder.userImageView.setImageResource(R.drawable.ic_male);
        } else {
            holder.userImageView.setImageResource(R.drawable.ic_female);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private String getAge(Calendar birthday) {
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birthday.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return "" + age;
    }
}
