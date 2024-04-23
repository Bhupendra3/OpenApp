package com.example.openapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationUtils {

    private Context mContext;

    public NotificationUtils(Context context) {
        mContext = context;
    }

    public void showFullScreenNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        //
        Intent intent = new Intent(mContext, CallReceive.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        //
        Intent openApp = new Intent(mContext, CallReceive.class);
        openApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(openApp);
        //
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "channel_id").setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(title).setContentText(message).setPriority(NotificationCompat.PRIORITY_HIGH).setFullScreenIntent(pendingIntent, true).setAutoCancel(true);
        notificationManager.notify(0, builder.build());
    }

}

