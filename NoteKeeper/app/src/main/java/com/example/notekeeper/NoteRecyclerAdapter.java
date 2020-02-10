package com.example.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {
    
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final List<NoteInfo> notes;

    public NoteRecyclerAdapter(Context context, List<NoteInfo> notes)
    {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.notes = notes;
    }
   
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View Itemview = layoutInflater.inflate(R.layout.item_notes_list,viewGroup,false);
        return new ViewHolder(Itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        NoteInfo note = notes.get(position);
        viewHolder.textCourse.setText(note.getCourse().getTitle());
        viewHolder.textTitle.setText(note.getText());
        viewHolder.currentPosition = position;
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView textCourse;
        public final TextView textTitle;
        public int currentPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourse = (TextView) itemView.findViewById(R.id.text_course);
            textTitle = (TextView) itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent noteIntent = new Intent(context,MainActivity.class);
                    noteIntent.putExtra(MainActivity.NOTE_POSITION,currentPosition);
                    context.startActivity(noteIntent);
                }
            });

        }
    }
}
