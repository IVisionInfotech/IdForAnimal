package com.idforanimal.activity;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.idforanimal.bluetooth.BluetoothActivity;
import com.idforanimal.permission.PermissionHandler;
import com.idforanimal.permission.PermissionsHandler;
import com.idforanimal.R;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Session;
import com.idforanimal.utils.TopExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

import ir.smartdevelopers.smartfilebrowser.customClasses.SFBFileFilter;
import ir.smartdevelopers.smartfilebrowser.customClasses.SmartFilePicker;


public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "okhttp";
    public static int splashTimeOut = 1000;
    private static long back;
    public Context context;
    public Activity activity;
    public Session session;
    protected ImageView ivBack;
    protected TextView tvTitle;
    public int flag = 0, flag1 = 0, flag2 = 0, flag3 = 0, flag4 = 0;
    public boolean updateFlag = false;
    private OnActivityResultLauncher resultLauncher;
    public Double latitude = 0.0;
    public Double longitude = 0.0;
    public String address = "";
    private static final int REQUESTCODE_TURNON_GPS = 11;
    public Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        session = new Session(context);

        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler("/mnt/sdcard/", context));
    }

    protected void setToolbar(String title) {

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(view -> finish());

        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(title);
    }

    protected void gotoBack() {
        if (back + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(home);
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
        } else {
            Toast.makeText(this, "Press once again to exit...", Toast.LENGTH_SHORT).show();
        }
        back = System.currentTimeMillis();
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void showSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public interface OnActivityResultLauncher {
        void onActivityResultData(Intent data, int resultCode);
    }

    public interface GetStoragePermission {
        void getStoragePermission(boolean permission);
    }

    public interface AskLocationService {
        void addOnSuccessListener(boolean permission);
    }

    public interface ImagePicker {
        void onPick(File file, Bitmap bitmap);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Intent data = result.getData();
            if (resultLauncher != null)
                resultLauncher.onActivityResultData(data, result.getResultCode());
        }
    });

    public void goToActivity(Intent intent) {
        startActivity(intent);
    }

    public void goToActivity(Context context, Class aClass) {
        startActivity(new Intent(context, aClass));
    }

    public void goToActivity(Context context, Class aClass, String id) {
        Intent intent = new Intent(context, aClass);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void goToActivity(Context context, Class aClass, String id, String title) {
        Intent intent = new Intent(context, aClass);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    public void goToActivityForResult(Intent intent, OnActivityResultLauncher resultLauncher) {
        activityResultLauncher.launch(intent);
        this.resultLauncher = resultLauncher;
    }

    public void goToActivityForResult(Context context, Class aClass, OnActivityResultLauncher resultLauncher) {
        Intent intent = new Intent(context, aClass);
        activityResultLauncher.launch(intent);
        this.resultLauncher = resultLauncher;
    }

    public void goToActivityForResult(Context context, Class aClass, String data, OnActivityResultLauncher resultLauncher) {
        Intent intent = new Intent(context, aClass);
        intent.putExtra("id", data);
        activityResultLauncher.launch(intent);
        this.resultLauncher = resultLauncher;
    }

    public void goToActivityForResult(Context context, Class aClass, String data,String title, OnActivityResultLauncher resultLauncher) {
        Intent intent = new Intent(context, aClass);
        intent.putExtra("id", data);
        intent.putExtra("title", title);
        activityResultLauncher.launch(intent);
        this.resultLauncher = resultLauncher;
    }

    public void setResultOfActivity(int resultCode, boolean finishStatus) {
        Intent intent = new Intent();
        setResult(resultCode, intent);
        intent.putExtra("result", resultCode);
        setResult(Activity.RESULT_OK, intent);
        if (finishStatus) finish();
    }

    public void setResultOfActivity(Intent intent, int resultCode, boolean finishStatus) {
        setResult(resultCode, intent);
        intent.putExtra("result", resultCode);
        setResult(Activity.RESULT_OK, intent);
        if (finishStatus) finish();
    }

    public void setResultOfActivity(int resultCode, int position) {
        Intent intent = new Intent();
        setResult(resultCode, intent);
        intent.putExtra("result", resultCode);
        intent.putExtra("position", position);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void setCancelResultOfActivity(int resultCode) {
        Intent intent = new Intent();
        setResult(resultCode, intent);
        intent.putExtra("result", resultCode);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    public String[] getStoragePermissionList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return new String[]{Manifest.permission.CAMERA};
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        } else {
            return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        }
    }


    public String[] getBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};
            } else {
                return new String[]{Manifest.permission.BLUETOOTH};
            }
        } else {
            return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
    }

    public void checkPermissions(String[] permissions, GetStoragePermission getStoragePermission) {

        PermissionsHandler.requestPermission(context, permissions, null, null, new PermissionHandler() {
            @Override
            public void onPermissionGranted() {
                getStoragePermission.getStoragePermission(true);
            }

            @Override
            public void onPermissionDenied(Context context, ArrayList<String> deniedPermissions) {
                Toast.makeText(context, "Permissions denied.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onPermissionNeverAskAgain(Context context, ArrayList<String> blockedList) {
                return super.onPermissionNeverAskAgain(context, blockedList);
            }

            @Override
            public void onPermissionDeniedOnce(Context context, ArrayList<String> justBlockedList, ArrayList<String> deniedPermissions) {
                super.onPermissionDeniedOnce(context, justBlockedList, deniedPermissions);
            }
        });
    }

    public void checkLocationPermissions(GetStoragePermission getStoragePermission) {
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Common.confirmationDialog(context, getResources().getString(R.string.location_permission_dialog_message), "Deny", "Allow", new Runnable() {
                @Override
                public void run() {
                    PermissionsHandler.requestPermission(context, permissions, null, null, new PermissionHandler() {
                        @Override
                        public void onPermissionGranted() {
                            getStoragePermission.getStoragePermission(true);
                        }

                        @Override
                        public void onPermissionDenied(Context context, ArrayList<String> deniedPermissions) {
                            Toast.makeText(context, "Permissions denied.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public boolean onPermissionNeverAskAgain(Context context, ArrayList<String> blockedList) {
                            return super.onPermissionNeverAskAgain(context, blockedList);
                        }

                        @Override
                        public void onPermissionDeniedOnce(Context context, ArrayList<String> justBlockedList, ArrayList<String> deniedPermissions) {
                            super.onPermissionDeniedOnce(context, justBlockedList, deniedPermissions);
                        }
                    });
                }
            });
        } else {
            getStoragePermission.getStoragePermission(true);
        }
    }

    protected void startBluetoothActivity() {
        goToActivityForResult(context, BluetoothActivity.class, (data, resultCode) -> {
            if (resultCode == Activity.RESULT_OK) {
                // Handle the result if needed
            }
        });
    }

    protected void enableBluetoothAndStartActivity() {
        enableBluetooth(resultCode -> {
            if (resultCode == Activity.RESULT_OK) {
                startBluetoothActivity();
            }
        });
    }

    private void enableBluetooth(Consumer<Integer> resultCallback) {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        goToActivityForResult(enableIntent, (data, resultCode) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                resultCallback.accept(resultCode);
            }
        });

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothReceiver, filter);
    }

    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Objects.equals(action, BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                switch (state) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        // Bluetooth is turning on
                        break;
                    case BluetoothAdapter.STATE_ON:
                        // Bluetooth is on
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        // Bluetooth is turning off
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        // Bluetooth is off
                        Common.showToast("Please enable bluetooth");
                        break;
                }
            }
        }
    };

    public boolean turnGPSOn(Activity activity, AskLocationService askLocationService) {
        LocationRequest locationRequest = LocationRequest.create();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, locationSettingsResponse -> askLocationService.addOnSuccessListener(true));

        task.addOnFailureListener(activity, e -> {
            askLocationService.addOnSuccessListener(false);
            if (e instanceof ResolvableApiException) {
                ResolvableApiException resolvable = (ResolvableApiException) e;
                try {
                    resolvable.startResolutionForResult(activity, REQUESTCODE_TURNON_GPS);
                } catch (IntentSender.SendIntentException e1) {
                    e1.printStackTrace();
                }
            }
        });
        return false;
    }

    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                address = Common.getAddress(context, latitude, longitude);
            }
        });
    }

    public void checkLocationSettings() {
        if (Common.isLocationEnabled(context)) {
            getCurrentLocation();
        } else {
            if (Common.displayLocationSettingsRequest(context, this)) {
                getCurrentLocation();
            }
        }
    }

    public void selectImage(ImagePicker imagePicker) {
        Bundle extra = new Bundle();
        extra.putInt("my_number", 1);
        Intent intent = new SmartFilePicker.IntentBuilder()
                .showCamera(true)
                .canSelectMultipleInGallery(false)
                .showGalleryTab(true)
                .showPickFromSystemGalleyMenu(false)
                .setExtra(extra)
                .showAudioTab(false)
                .showFilesTab(false)
                .showPDFTab(false)
                .setFileFilter(new SFBFileFilter.Builder().isFile(true).isFolder(true).build())
//                .canSelectMultipleInFiles(false)
                .build(this);
        goToActivityForResult(intent, (data, resultCode) -> {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri[] uris = SmartFilePicker.getResultUris(data);
                    if (uris != null) {
                        for (Uri uri : uris) {
                            String filePath = Common.getRealPathFromURI(context, uri);
                            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                            File file = Common.bitmapToFile(this, bitmap);
                            imagePicker.onPick(file, bitmap);
                        }
                    }
                }
            }
        });
    }

    private Bitmap handleImageOrientation(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        Bitmap rotatedBitmap = bitmap; // Default to original bitmap

        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;
                default:
                    break; // No rotation needed
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check if the image is in landscape orientation (width > height)
        if (rotatedBitmap.getWidth() > rotatedBitmap.getHeight()) {
            // Rotate the image by 90 degrees to show it in portrait
            rotatedBitmap = rotateImage(rotatedBitmap, 90);
        }

        return rotatedBitmap;
    }

    // Method to rotate the bitmap
    private Bitmap rotateImage(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public void restartApp() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity(); // Close all activities
            System.exit(0); // Kill the process
        }
    }

}
