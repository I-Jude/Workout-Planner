package com.example.workoutplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Find all CardViews and set click listeners
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Find all category CardViews (you'll need to add IDs to them in the layout)
        CardView cardioCard = findViewById(R.id.cardioCard);
        CardView strengthCard = findViewById(R.id.strengthCard);
        CardView flexibilityCard = findViewById(R.id.flexibilityCard);
        CardView hiitCard = findViewById(R.id.hiitCard);

        View.OnClickListener categoryClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = "";
                if (v == cardioCard) category = "Cardio";
                else if (v == strengthCard) category = "Strength";
                else if (v == flexibilityCard) category = "Flexibility";
                else if (v == hiitCard) category = "HIIT";

                Toast.makeText(HomeActivity.this,
                        category + " workouts coming soon!",
                        Toast.LENGTH_SHORT).show();
            }
        };

        // Set the click listener for each card
        cardioCard.setOnClickListener(categoryClickListener);
        strengthCard.setOnClickListener(categoryClickListener);
        flexibilityCard.setOnClickListener(categoryClickListener);
        hiitCard.setOnClickListener(categoryClickListener);
    }
}