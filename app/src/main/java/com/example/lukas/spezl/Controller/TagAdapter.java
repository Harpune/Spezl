package com.example.lukas.spezl.Controller;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.lukas.spezl.Model.Event;
import com.example.lukas.spezl.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagHolder> {

    private List<String> tagList;

    public class TagHolder extends RecyclerView.ViewHolder {
        public TextView tagView;
        public ImageButton deleteButton;

        public TagHolder(View view) {
            super(view);

            this.tagView = (TextView) view.findViewById(R.id.text_tag);
            this.deleteButton = (ImageButton) view.findViewById(R.id.imageButton_delete_tag);
        }
    }

    public TagAdapter(List<String> tagList) {
        this.tagList = tagList;
    }

    @Override
    public TagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_create_tag_row, parent, false);
        return new TagHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TagHolder holder, int position) {
        String tag = tagList.get(position);
        Log.d("onBindViewHolder", tag);
        holder.tagView.setText(tag);

        //Set OnClickListener on Delete-ImageButton to delete chosen tag.
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagList.remove(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }
}
