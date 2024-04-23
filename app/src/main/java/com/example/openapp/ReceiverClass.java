package com.example.openapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverClass extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        notificationUtils.showFullScreenNotification("tittle", "nothing");
//
    }

}
