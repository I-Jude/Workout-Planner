package com.example.workoutplanner;

public class SuccessStory {
    private final String id;
    private final String title;
    private final String description;
    private final byte[] image;

    public SuccessStory(String id, String title, String description, byte[] image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getImage() {
        return image;
    }
}
