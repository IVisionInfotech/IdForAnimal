package com.idforanimal.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class BroadCastManager {

    private static BroadCastManager broadCastManager = new BroadCastManager();

    public static BroadCastManager getInstance() {
        return broadCastManager;
    }

    public void registerReceiver(Activity activity, BroadcastReceiver receiver, IntentFilter filter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.registerReceiver(activity, receiver, filter, ContextCompat.RECEIVER_EXPORTED);
        } else {
            ContextCompat.registerReceiver(activity, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
        }
    }

    public void unregisterReceiver(Activity activity, BroadcastReceiver receiver) {
        activity.unregisterReceiver(receiver);
    }

    public void sendBroadCast(Activity activity, Intent intent) {
        activity.sendBroadcast(intent);
    }

    public void sendBroadCast(Context context, Intent intent) {
        context.sendBroadcast(intent);
    }
}
