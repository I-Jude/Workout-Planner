package com.example.workoutplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SuccessStoriesAdapter extends RecyclerView.Adapter<SuccessStoriesAdapter.ViewHolder> {

    private final List<SuccessStory> successStories;

    public SuccessStoriesAdapter(List<SuccessStory> successStories) {
        this.successStories = successStories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.success_story_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SuccessStory story = successStories.get(position);
        holder.title.setText(story.getTitle());
        holder.description.setText(story.getDescription());
        holder.image.setImageResource(story.getImageResId());
    }

    @Override
    public int getItemCount() {
        return successStories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView image;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            image = itemView.findViewById(R.id.image);
            cardView = (CardView) itemView;
        }
    }
}
