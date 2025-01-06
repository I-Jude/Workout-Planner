package com.example.workoutplanner;

public class SuccessStory {
    private final String title;
    private final String description;
    private final int imageResId;

    public SuccessStory(String title, String description, int imageResId) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }
}
