package com.example.openapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CallReceive extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    private boolean isVibrating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_receive);
        Window window = this.getWindow();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// Cancel the notification by its ID
        int notificationId = 0; // Replace 1 with the ID of the notification you want to cancel
        notificationManager.cancel(notificationId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT); // Set transparent color
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(ContextCompat.getColor(CallReceive.this, com.google.android.material.R.color.material_dynamic_primary99));
//        }
        Button button = findViewById(R.id.stopMusic);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//        MediaPlayer mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer = MediaPlayer.create(this, R.raw.toon);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.VIBRATE}, 11);
                return;
            }
        }
        startVibration();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                finish();
                Toast.makeText(CallReceive.this, "Thank You", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startVibration() {
        if (!isVibrating) {
            isVibrating = true;
            long[] pattern = {0, 1000}; // Vibrate for 1 second, then pause for 1 second (in milliseconds)
            // -1 means "do not repeat", 0 means "repeat indefinitely" starting at the beginning of the pattern
            int repeat = 0;
            vibrate(pattern, repeat);
        }
    }

    private void vibrate(long[] pattern, int repeat) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            // Check for compatibility
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, repeat));
            } else {
                // For older devices, use deprecated method
                vibrator.vibrate(pattern, repeat);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop vibration when activity is destroyed
        stopVibration();
    }

    private void stopVibration() {
        if (isVibrating) {
            isVibrating = false;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.cancel(); // Cancel the vibration
            }
        }
    }

}