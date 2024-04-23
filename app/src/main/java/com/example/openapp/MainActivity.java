package com.example.openapp;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Button button;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = firebaseDatabase.getReference("value");
    String token;
    String lastFourDigits;
    private MediaPlayer mediaPlayer;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.open_app);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    11);
        } else {
            // Permission has already been granted
            // Proceed with your logic
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();
                        lastFourDigits = token.substring(Math.max(0, token.length() - 4));
                        myRef.child(lastFourDigits).setValue(new ModelData(token, lastFourDigits));
                        // Log and toast
                        Log.d("TAG", "onComplete: " + token);
                    }
                });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ModelData value = dataSnapshot.getValue(ModelData.class);
                            if (!value.getuId().equals(lastFourDigits)) {
                                sendNotification(value.getToken());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private void sendNotification(String userID) {
        JSONObject json = new JSONObject();
        try {
            json.put("to", userID); // Send to a specific device using its registration token
            json.put("priority", "high"); // Set priority to high for immediate delivery
            JSONObject data = new JSONObject();
            data.put("title", "Notification Title");
            data.put("message", "Notification Message");
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(mediaType, json.toString());

        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .header("Authorization", "key=AAAAWKQVUiA:APA91bEdQPxDEucCov2O4Au0_QuIkbi748QvnORDYmGj93m55uqATeCRqjkDvbiYa0ino7EzatTHxJslFkSAX9NVrfkyB_c8vm_NC6us5QKHWHkl1zYh9e0tpPyRWX8C8WSdM2m_Li7g") // Replace YOUR_SERVER_KEY with your actual FCM server key
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("onResponse: " + response.code());
                System.out.println("onResponse: " + response.body().string());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                // Proceed with your logic
            } else {
                // Permission is denied
                // You can show a message to the user indicating why the permission is required
            }
        }
    }

}