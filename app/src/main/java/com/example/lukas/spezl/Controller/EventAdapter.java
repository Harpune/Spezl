package com.example.lukas.spezl.Controller;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lukas.spezl.Model.Event;
import com.example.lukas.spezl.R;
import com.example.lukas.spezl.View.EventActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {

    private final String TAG_EVENT_ID = "TAG_EVENT_ID";
    private final String TAG_OWNER_ID = "TAG_OWNER_ID";


    private List<Event> eventList;

    public class EventHolder extends RecyclerView.ViewHolder {
        public TextView nameView, descriptionView, datumView, townView, participantView;

        public RelativeLayout rootLayout;

        public EventHolder(View view){
            super(view);

            this.nameView = (TextView) view.findViewById(R.id.eventName);
            this.descriptionView = (TextView) view.findViewById(R.id.eventDescription);
            this.datumView = (TextView) view.findViewById(R.id.eventDate);
            this.townView = (TextView) view.findViewById(R.id.eventTown);
            this.participantView = (TextView) view.findViewById(R.id.eventParticipant);

            this.rootLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);
            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(rootLayout.getContext(), EventActivity.class);
                    intent.putExtra(TAG_EVENT_ID, eventList.get(getAdapterPosition()).getuId());
                    intent.putExtra(TAG_OWNER_ID, eventList.get(getAdapterPosition()).getOwnerId());
                    rootLayout.getContext().startActivity(intent);
                }
            });
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.YYYY HH:mm", Locale.GERMAN);
        int participants = 0;
        if(event.getParticipantIds() != null){
            participants = event.getParticipantIds().size();
        }


        holder.nameView.setText(event.getName());
        holder.descriptionView.setText(event.getDescription());
        holder.datumView.setText(simpleDateFormat.format(event.getDate()));
        holder.townView.setText(event.getTown());
        holder.participantView.setText(String.valueOf(participants) + "/" + event.getMaxParticipants().intValue());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
