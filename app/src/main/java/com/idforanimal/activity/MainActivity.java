package com.idforanimal.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import com.idforanimal.activity.audit.OfflineActivity;
import com.idforanimal.databinding.ActivitySplashBinding;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.utils.Common;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;

        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;
        activity = this;

        session.setDeviceName("");
        session.setDeviceAddress("");
        if (ConnectivityReceiver.isConnected(context)) {
            processIntentData(getIntent());
        } else {
            Common.confirmationDialog(activity, "No Internet Connection", "Exit", "Go to Offline Screen", () -> {
                goToActivity(context, OfflineActivity.class);
                finish();
            }, () -> onBackPressed());
        }
    }

    private void processIntentData(Intent intent) {
        String data = Common.handleIntent(intent);
        final Handler handler = new Handler();
        handler.postDelayed(() -> navigateToNextScreen(data), 2000);
    }

    private void navigateToNextScreen(String data) {
        if (session.getLoginStatus()) {
            Intent nextIntent = new Intent(context, MainNewActivity.class);
            if (!data.isEmpty()) {
                String[] parts = data.split(", ");
                nextIntent.putExtra("id", parts[0]);
                nextIntent.putExtra("type", parts[1]);
            }
            goToActivity(nextIntent);
        } else {
            goToActivity(new Intent(context, LoginActivity.class));
        }

        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }
}
