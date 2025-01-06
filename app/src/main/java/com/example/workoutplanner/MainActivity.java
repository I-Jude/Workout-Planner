package com.example.workoutplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView plannerButton = findViewById(R.id.myPlanButton);
        plannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.successStoriesRecyclerView);

        List<SuccessStory> successStories = new ArrayList<>();
        successStories.add(new SuccessStory("John's Transformation", "Lost 20 kg in 3 months.", R.drawable.sample_img));
        successStories.add(new SuccessStory("Emma's Journey", "Ran her first marathon.", R.drawable.sample_img));
        successStories.add(new SuccessStory("Mark's Growth", "Gained 10 kg of muscle.", R.drawable.sample_img));

        SuccessStoriesAdapter adapter = new SuccessStoriesAdapter(successStories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }
}