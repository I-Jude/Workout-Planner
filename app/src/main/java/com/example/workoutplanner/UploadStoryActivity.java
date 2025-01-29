package com.example.workoutplanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class UploadStoryActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etTitle, etDescription;
    private ImageView ivPreview;
    private Button btnSelectImage, btnUploadStory;

    private byte[] selectedImageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_story);

        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        ivPreview = findViewById(R.id.iv_preview);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnUploadStory = findViewById(R.id.btn_upload_story);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open image picker
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        btnUploadStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString().trim();
                String description = etDescription.getText().toString().trim();

                if (title.isEmpty() || description.isEmpty() || selectedImageBytes == null) {
                    Toast.makeText(UploadStoryActivity.this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
                } else {
                    // Save story to the database
                    DatabaseHelper db = new DatabaseHelper(UploadStoryActivity.this);
                    db.addStory(title,description, selectedImageBytes);

                    Toast.makeText(UploadStoryActivity.this, "Story uploaded successfully", Toast.LENGTH_SHORT).show();

                    // Clear the form
                    etTitle.setText("");
                    etDescription.setText("");
                    ivPreview.setImageResource(android.R.color.transparent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ivPreview.setImageBitmap(bitmap);

                // Convert the selected image to byte[]
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                selectedImageBytes = outputStream.toByteArray();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
