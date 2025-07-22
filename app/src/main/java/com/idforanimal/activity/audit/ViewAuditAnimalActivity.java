package com.idforanimal.activity.audit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.idforanimal.R;
import com.idforanimal.activity.BaseActivity;
import com.idforanimal.bluetooth.Bluetooth;
import com.idforanimal.databinding.ActivityViewAuditAnimalBinding;
import com.idforanimal.databinding.ToolbarPrimaryBinding;
import com.idforanimal.dialog.FragmentAnimalDetail;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.Animal;
import com.idforanimal.model.AuditViewModel;
import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.utils.BroadCastManager;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;
import com.idforanimal.utils.MyApplication;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAuditAnimalActivity extends BaseActivity {

    private ActivityViewAuditAnimalBinding binding;
    private ToolbarPrimaryBinding toolbarPrimaryBinding;
    private String status = "", tag = "", ownerId = "";
    private LocalReceiver localReceiver;
    private AuditViewModel auditViewModel;
    private StringBuilder scannedData = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewAuditAnimalBinding.inflate(getLayoutInflater());
        toolbarPrimaryBinding = ToolbarPrimaryBinding.bind(binding.toolbar.getRoot());
        setContentView(binding.getRoot());

        context = ViewAuditAnimalActivity.this;
        activity = this;

        auditViewModel = new Gson().fromJson(getIntent().getStringExtra("id"), AuditViewModel.class);

        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constant.refreshCount);
            localReceiver = new LocalReceiver();
            BroadCastManager.getInstance().registerReceiver(this, localReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbarPrimaryBinding.tvTitle.setText("Audit Details");
        init();
        bindData(getIntent());
        getDetails();
        checkLocationPermissions(permission -> turnGPSOn(this, permission1 -> checkLocationSettings()));
    }

    private void init() {
        toolbarPrimaryBinding.ivBack.setOnClickListener(v -> finish());
        toolbarPrimaryBinding.ivBluetooth.setVisibility(View.VISIBLE);
        toolbarPrimaryBinding.ivBluetooth.setOnClickListener(v -> {
            if (new Bluetooth(this).isOn()) {
                startBluetoothActivity();
            } else {
                enableBluetoothAndStartActivity();
            }
        });
        binding.tvViewMore.setOnClickListener(v -> displayBottomSheet());

        binding.llOffline.setOnClickListener(v -> goToActivityForResult(context, OfflineActivity.class, "0", auditViewModel.getAuditId(), (data, resultCode) -> {
            getDetails();
        }));
        binding.llTotalCount.setOnClickListener(v -> goToActivityForResult(context, AuditAnimalListActivity.class, "0", auditViewModel.getAuditId(), (data, resultCode) -> {
            getDetails();
        }));
        binding.llAuditCount.setOnClickListener(v -> goToActivityForResult(context, AuditAnimalListActivity.class, "1", auditViewModel.getAuditId(), (data, resultCode) -> {
            getDetails();
        }));
        binding.llPendingCount.setOnClickListener(v -> goToActivityForResult(context, AuditAnimalListActivity.class, "2", auditViewModel.getAuditId(), (data, resultCode) -> {
            getDetails();
        }));
        binding.llExtraCount.setOnClickListener(v -> goToActivityForResult(context, AuditAnimalListActivity.class, "3", auditViewModel.getAuditId(), (data, resultCode) -> {
            getDetails();
        }));

    }

    @Override
    protected void onDestroy() {
        BroadCastManager.getInstance().unregisterReceiver(this, localReceiver);//Logout broadcast recipient
        super.onDestroy();
        MyApplication.activityPaused();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (new Bluetooth(this).isOn() && !session.getDeviceName().equals("")) {
            toolbarPrimaryBinding.ivBluetooth.setImageDrawable(getResources().getDrawable(R.drawable.bluetooth_on));
        } else {
            session.setDeviceName("");
            session.setDeviceAddress("");
            toolbarPrimaryBinding.ivBluetooth.setImageDrawable(getResources().getDrawable(R.drawable.bluetooth_off));
        }
        if (session.getStringList().isEmpty()) {
            binding.llOffline.setVisibility(View.GONE);
        } else {
            binding.llOffline.setVisibility(View.VISIBLE);
        }
        MyApplication.activityResumed();
    }

    private void bindData(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra("tag")) {
                tag = intent.getStringExtra("tag");
                binding.tvRFID.setText(tag);
                getDetails();
            }
        }
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
                    runOnUiThread(() -> {
                        tag = String.valueOf(scannedData);
                        binding.tvRFID.setText(tag);
                        getDetails();
                    });
                    scannedData.setLength(0);
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void getDetails() {
        Common.hideProgressDialog();
        if (ConnectivityReceiver.isConnected(context)) {
            Common.showProgressDialog(context, getString(R.string.please_wait));
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getAuditDetails(Constant.getAnimalAuditDetails, tag, auditViewModel.getAuditId(),"1", Common.getEmpId(), Common.getCustomerId(), String.valueOf(latitude), String.valueOf(longitude), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    try {
                        Common.hideProgressDialog();
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                binding.tvTotal.setText(apiResponse.getTotalAnimalCount());
                                binding.tvAudit.setText(apiResponse.getTotalAuditCount());
                                binding.tvPending.setText(apiResponse.getTotalPendingCount());
                                binding.tvExtra.setText(apiResponse.getTotalExtraCount());
                                bindData(apiResponse.getAnimalDetails());
                            } else {
                                binding.tvTotal.setText(apiResponse.getTotalAnimalCount());
                                binding.tvAudit.setText(apiResponse.getTotalAuditCount());
                                binding.tvPending.setText(apiResponse.getTotalPendingCount());
                                binding.tvExtra.setText(apiResponse.getTotalExtraCount());
                                clearData();
                                Common.showToast(apiResponse.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        Common.hideProgressDialog();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t) {
                    Common.hideProgressDialog();
                    t.printStackTrace();
                }
            });
        } else {
            Common.noInternetDialog(this);
        }
    }

    private void bindData(Animal model) {
        if (model != null) {
            if (!model.getImage().equals("")) {
                Common.loadImage(binding.ivImage, R.drawable.animal_bg, model.getImage());
                binding.ivImage.setOnClickListener(v -> Common.openImagePreview(context, model.getImage()));
            }

            if (model.getValidDate() != null && !model.getValidDate().equals("0000-00-00")) {
                binding.llValidDate.setVisibility(View.VISIBLE);
                binding.tvValidDate.setText(Common.changeDateFormat(model.getValidDate()));
            } else {
                binding.llValidDate.setVisibility(View.GONE);
            }

            if (model.getOwnerValidDate() != null && !model.getOwnerValidDate().equals("0000-00-00")) {
                binding.llOwnerValid.setVisibility(View.VISIBLE);
                binding.tvOwnerValidDate.setText(Common.changeDateFormat(model.getOwnerValidDate()));
            } else {
                binding.llOwnerValid.setVisibility(View.GONE);
            }

            binding.tvRFID.setText(model.getRfidTagNo());
            binding.txtVisualNo.setText(model.getVisualTagNo());
            StringBuilder stringBuilder = new StringBuilder();
            if (!model.getAnimalName().isEmpty()) stringBuilder.append(model.getAnimalName());

            if (!model.getCattleName().isEmpty()) {
                if (stringBuilder.length() > 0) stringBuilder.append("-");
                stringBuilder.append(model.getCattleName());
            }
            binding.tvAnimalType.setText(stringBuilder.toString());

            binding.tvOwnerName.setText(model.getOwnerName());
            binding.tvOwnerContact.setText(model.getOwnerContact());
            if (model.getDeathStatus().equals("1")) {
                binding.tvDeathAnimal.setText("Death");
                binding.tvDeathAnimal.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
            } else {
                binding.tvDeathAnimal.setText("Alive");
                binding.tvDeathAnimal.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.green));
            }
            binding.tvLastCatchingDetail.setText(Common.getLastDetail(model.getCatchingLocation(), model.getTotalCatching(), "CATCHING"));
            binding.tvLastTreatmentDetail.setText(Common.getLastDetail(model.getTreatmentData(), model.getTotalTreatment(), "TREATMENT"));
            binding.tvLastVaccinationDetail.setText(Common.getLastDetail(model.getVaccinationData(), model.getTotalVaccination(), "VACCINATION"));
            ownerId = model.getOwnerId();
        } else {
            clearData();
        }
    }

    private void clearData() {
        binding.ivImage.setImageResource(R.drawable.animal_bg);
        binding.ivImage.setOnClickListener(null);

        binding.llValidDate.setVisibility(View.GONE);
        binding.llOwnerValid.setVisibility(View.GONE);

        binding.tvValidDate.setText("");
        binding.tvOwnerValidDate.setText("");
        binding.txtVisualNo.setText("");
        binding.tvAnimalType.setText("");
        binding.tvOwnerName.setText("");
        binding.tvOwnerContact.setText("");

        binding.tvDeathAnimal.setText("Alive");
        binding.tvDeathAnimal.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.green));

        binding.tvLastCatchingDetail.setText("");
        binding.tvLastTreatmentDetail.setText("");
        binding.tvLastVaccinationDetail.setText("");

        ownerId = "";
    }


    private void displayBottomSheet() {
        Bundle args = new Bundle();
        args.putString("tag", tag);
        args.putString("status", status);
        args.putString("ownerId", ownerId);
        DialogFragment newFragment = new FragmentAnimalDetail();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "TAG");
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Received the process after broadcast
            if (intent.hasExtra(Constant.refreshCount)) {
                if (intent.getStringExtra(Constant.refreshCount).equals(Constant.refreshCount)) {
                    bindData(intent);
                }
            }
        }
    }

}