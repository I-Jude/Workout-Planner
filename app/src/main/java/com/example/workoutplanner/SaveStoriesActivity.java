package com.example.workoutplanner;

import static com.example.workoutplanner.R.id.statuText;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private String sdCardId = null;
    private TextView textViewStatus; // Changed from statusText to textViewStatus

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_stories);

        textViewStatus = findViewById(statuText);
        Button saveButton = findViewById(R.id.saveButton);

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
                });

        saveButton.setOnClickListener(v -> checkPermissionAndOpenPicker());

        // Find SD card ID when activity starts
        findSdCardId();
        Button backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaveStoriesActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void findSdCardId() {
        try {
            String[] projection = {"root_id"}; // Using the actual column name instead of constant
            Uri rootsUri = DocumentsContract.buildRootsUri("com.android.externalstorage.documents");

            try (Cursor cursor = getContentResolver().query(rootsUri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String documentId = cursor.getString(0);
                        Log.d(TAG, "Storage ID found: " + documentId);
                        if (!documentId.equals("primary")) {
                            sdCardId = documentId;
                            Log.d(TAG, "SD Card ID found: " + sdCardId);
                            break;
                        }
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding SD card", e);
        }
    }

    private void checkPermissionAndOpenPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                    updateStatus("Please grant storage permission");
                } catch (Exception e) {
                    Log.e(TAG, "Error requesting permission", e);
                    updateStatus("Error requesting permission");
                }
            } else {
                openFilePickerForSDCard();
            }
        } else {
            openFilePickerForSDCard();
        }
    }

    private void openFilePickerForSDCard() {
        try {
            Uri initialUri = null;
            if (sdCardId != null) {
                initialUri = DocumentsContract.buildRootUri(
                        "com.android.externalstorage.documents", sdCardId);
                Log.d(TAG, "Opening picker with SD card URI: " + initialUri);
            }
            openDocumentTreeLauncher.launch(initialUri);
        } catch (Exception e) {
            Log.e(TAG, "Error launching picker", e);
            updateStatus("Error opening file picker");
        }
    }

    private void handleSelectedDirectory(Uri uri) {
        try {
            // Take persistable permissions
            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
            getContentResolver().takePersistableUriPermission(uri, takeFlags);

            // Create a timestamped filename
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());
            String filename = "stories_" + timestamp + ".txt";

            // Get the document tree URI
            Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri,
                    DocumentsContract.getTreeDocumentId(uri));

            // Create the file
            Uri fileUri = DocumentsContract.createDocument(
                    getContentResolver(),
                    docUri,
                    "text/plain",
                    filename
            );

            if (fileUri != null) {
                saveContentToFile(fileUri);
            } else {
                updateStatus("Failed to create file");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling directory", e);
            updateStatus("Error: " + e.getMessage());
        }
    }

    private void saveContentToFile(Uri fileUri) {
        try {
            String content = "Story 1: John's Journey\n" +
                    "Story 2: Emma's Challenge\n" +
                    "Created on: " + new Date().toString() + "\n";

            try (OutputStream outputStream = getContentResolver().openOutputStream(fileUri, "w")) {
                if (outputStream != null) {
                    outputStream.write(content.getBytes());
                    outputStream.flush();
                    updateStatus("File saved successfully!\nLocation: " + fileUri.getPath());
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error saving file", e);
            updateStatus("Error saving file: " + e.getMessage());
        }
    }

    private void updateStatus(String message) {
        runOnUiThread(() -> {
            textViewStatus.setText(message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }
}