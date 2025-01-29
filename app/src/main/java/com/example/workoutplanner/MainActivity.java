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
        plannerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        CardView addStoryButton = findViewById(R.id.addStoryButton);
        addStoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UploadStoryActivity.class);
            startActivity(intent);
        });

        RecyclerView recyclerView = findViewById(R.id.successStoriesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Fetch stories from the database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<SuccessStory> successStories = databaseHelper.getAllStories();

        // Set adapter with fetched stories
        SuccessStoriesAdapter adapter = new SuccessStoriesAdapter(successStories);
        recyclerView.setAdapter(adapter);

        CardView saveStoriesButton = findViewById(R.id.saveStoriesButton);
        saveStoriesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SaveStoriesActivity.class);
            startActivity(intent);
        });


    }
}
