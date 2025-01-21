package com.example.workoutplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveStoriesActivity extends AppCompatActivity {
    private static final String TAG = "SaveStoriesActivity";
    private ActivityResultLauncher<Uri> openDocumentTreeLauncher;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private String sdCardId = null;
    private TextView textViewStatus;
    private ImageView imagePreview;
    private Uri selectedImageUri;

    private void checkPermissionAndOpenPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API 30) and above
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                    updateStatus("Please grant all files access permission");
                    return;
                } catch (Exception e) {
                    Log.e(TAG, "Error requesting permission", e);
                    updateStatus("Error requesting permission: " + e.getMessage());
                    return;
                }
            }
        }

        // If we have SD card ID, try to create a specific URI for it
        if (sdCardId != null) {
            Uri sdCardUri = Uri.parse("content://com.android.externalstorage.documents/tree/" + sdCardId);
            openDocumentTreeLauncher.launch(sdCardUri);
        } else {
            // If no SD card ID, just launch the picker normally
            openDocumentTreeLauncher.launch(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_stories);

        textViewStatus = findViewById(R.id.statuText);
        Button saveButton = findViewById(R.id.saveButton);
        Button selectImageButton = findViewById(R.id.selectImageButton);
        imagePreview = findViewById(R.id.imagePreview);

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        imagePreview.setImageURI(uri);
                        imagePreview.setVisibility(View.VISIBLE);
                    }
                }
        );

        // Initialize document tree launcher
        openDocumentTreeLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocumentTree(),
                uri -> {
                    if (uri != null) {
                        Log.d(TAG, "Selected URI: " + uri);
                        handleSelectedDirectory(uri);
                    } else {
                        updateStatus("No folder selected");
                    }
                }
        );

        selectImageButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                checkPermissionAndOpenPicker();
            } else {
                updateStatus("Please select an image first");
            }
        });

        findSdCardId();

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SaveStoriesActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @SuppressLint("NewApi")
    private void findSdCardId() {
        try {
            Uri rootUri = MediaStore.getExternalVolumeNames(this).stream()
                    .filter(volume -> !MediaStore.VOLUME_EXTERNAL_PRIMARY.equals(volume))
                    .findFirst()
                    .map(volume -> MediaStore.Files.getContentUri(volume))
                    .orElse(null);

            if (rootUri != null) {
                // Extract the volume name from the URI
                String path = rootUri.getPath();
                if (path != null) {
                    String[] segments = path.split("/");
                    if (segments.length > 0) {
                        sdCardId = segments[segments.length - 1] + ":";
                        Log.d(TAG, "Found SD card ID: " + sdCardId);
                    }
                }
            } else {
                Log.d(TAG, "No external SD card found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding SD card", e);
        }
    }

    private void openImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    // [Previous findSdCardId and checkPermissionAndOpenPicker methods remain the same]

    private void handleSelectedDirectory(Uri uri) {
        try {
            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
            getContentResolver().takePersistableUriPermission(uri, takeFlags);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());
            String filename = "image_" + timestamp + ".jpg";

            Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri,
                    DocumentsContract.getTreeDocumentId(uri));

            Uri fileUri = DocumentsContract.createDocument(
                    getContentResolver(),
                    docUri,
                    "image/jpeg",
                    filename
            );

            if (fileUri != null) {
                saveImageToFile(fileUri);
            } else {
                updateStatus("Failed to create file");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling directory", e);
            updateStatus("Error: " + e.getMessage());
        }
    }

    private void saveImageToFile(Uri destinationUri) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(destinationUri, "w");
            if (outputStream != null) {
                // Copy the selected image to the destination
                getContentResolver().openInputStream(selectedImageUri).transferTo(outputStream);
                outputStream.flush();
                outputStream.close();
                updateStatus("Image saved successfully!\nLocation: " + destinationUri.getPath());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error saving image", e);
            updateStatus("Error saving image: " + e.getMessage());
        }
    }

    private void updateStatus(String message) {
        runOnUiThread(() -> {
            textViewStatus.setText(message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }
}