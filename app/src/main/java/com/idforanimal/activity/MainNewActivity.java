package com.idforanimal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.google.gson.Gson;
import com.idforanimal.activity.audit.AuditActivity;
import com.idforanimal.bluetooth.Bluetooth;
import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.R;
import com.idforanimal.databinding.ActivityMainNewBinding;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.CommonModel;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainNewActivity extends BaseActivity {

    private ActivityMainNewBinding binding;
    private StringBuilder scannedData = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = MainNewActivity.this;
        activity = MainNewActivity.this;

        init();
        getDetails();

        checkLocationPermissions(permission -> turnGPSOn(MainNewActivity.this, permission1 -> checkLocationSettings()));
    }

    private void init() {
        binding.tvName.setText("Hi " + Common.getUserName() + "!");
        binding.tvSubTitle.setText(Common.getGreeting());

        binding.btnScan.setOnClickListener(v -> checkPermissions(getBluetoothPermission(), permission -> {
            if (new Bluetooth(this).isOn()) {
                startBluetoothActivity();
            } else {
                enableBluetoothAndStartActivity();
            }
        }));

        binding.ivLogout.setOnClickListener(v -> {
            Common.confirmationDialog(context, getResources().getString(R.string.confirmation_logout), "NO", "Yes", () -> Common.logout(context, ""));
        });

        binding.btnSubmit.setOnClickListener(v -> {
            if (binding.etVisualTag.getText().toString().isEmpty()) {
                binding.etVisualTag.setError("Enter tag no");
            } else {
                String tag = binding.etVisualTag.getText().toString().trim();
                goToActivityForResult(new Intent(context, AnimalDetailsActivity.class)
                        .putExtra("tag", tag)
                        .putExtra("status", Constant.typeVisualTag), (data, resultCode) -> {
                        binding.etVisualTag.getText().clear();
                });
            }
        });

        binding.llAnimalOwner.setOnClickListener(v -> goToActivity(context, AnimalOwnerListActivity.class));
        binding.llAnimalAudit.setOnClickListener(v -> goToActivity(context, AuditActivity.class));
        binding.llAnimal.setOnClickListener(v -> goToActivity(context, AnimalListActivity.class));
        binding.llAnimalCatching.setOnClickListener(v -> goToActivity(context, AnimalCatchingListActivity.class));
        binding.llNgo.setOnClickListener(view -> goToActivityForResult(context, NgoListActivity.class, (data, resultCode) -> {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.hasExtra("details")) {
                    // Handle details if needed
                }
            }
        }));
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            int keyCode = event.getKeyCode();
            char pressedKey = (char) event.getUnicodeChar();

            if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT || keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT ||
                    keyCode == KeyEvent.KEYCODE_CTRL_LEFT || keyCode == KeyEvent.KEYCODE_CTRL_RIGHT ||
                    keyCode == KeyEvent.KEYCODE_ALT_LEFT || keyCode == KeyEvent.KEYCODE_ALT_RIGHT) {
                return super.dispatchKeyEvent(event);
            }

            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return true;
            }

            if (Character.isDigit(pressedKey)) {
                scannedData.append(pressedKey);

                if (scannedData.length() == 15) {
                    runOnUiThread(() -> binding.etVisualTag.setText(scannedData.toString()));
                    scannedData.setLength(0);
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getDetails();
        if (new Bluetooth(this).isOn()) {
            binding.tvDeviceName.setText(session.getDeviceName());
        } else {
            session.setDeviceName("");
            session.setDeviceAddress("");
            binding.tvDeviceName.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        gotoBack();
    }

    private void getDetails() {
        if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getDetails(Constant.homeListUrl, Common.getCustomerId(), "1", Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                CommonModel model = apiResponse.getCountDetails();
                                binding.tvAnimalOwner.setText(model.getAnimalOwnerCount());
                                binding.tvAnimal.setText(model.getAnimalCount());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } else {
            Common.noInternetDialog(this);
        }
    }
}
