package com.example.lukas.spezl.Controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lukas.spezl.Model.Event;
import com.example.lukas.spezl.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {

    private List<Event> eventList;

    public class EventHolder extends RecyclerView.ViewHolder {
        public TextView nameView, beschreibungView, datumView;

        public EventHolder(View view){
            super(view);

            this.nameView = (TextView) view.findViewById(R.id.eventName);
            this.beschreibungView = (TextView) view.findViewById(R.id.eventDescription);
            this.datumView = (TextView) view.findViewById(R.id.eventDate);
        }
    }

    public EventAdapter(List<Event> moviesList) {
        this.eventList = moviesList;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_main_row, parent, false);
        return new EventHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        Event event = eventList.get(position);

        holder.nameView.setText(event.getName());
        holder.beschreibungView.setText(event.getDescription());
        holder.datumView.setText(event.getDate().toString());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
