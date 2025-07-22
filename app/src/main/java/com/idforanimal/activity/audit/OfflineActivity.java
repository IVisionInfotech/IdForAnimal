package com.idforanimal.activity.audit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.idforanimal.R;
import com.idforanimal.activity.BaseActivity;
import com.idforanimal.adapter.OfflineAnimalListAdapter;
import com.idforanimal.bluetooth.Bluetooth;
import com.idforanimal.databinding.ActivityRecyclerviewListBinding;
import com.idforanimal.databinding.ToolbarPrimaryBinding;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.OfflineAnimalData;
import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.utils.BroadCastManager;
import com.idforanimal.utils.ClickListener;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;
import com.idforanimal.utils.MyApplication;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfflineActivity extends BaseActivity {

    private ActivityRecyclerviewListBinding binding;
    private ToolbarPrimaryBinding toolbarPrimaryBinding;
    private ArrayList<OfflineAnimalData> list = new ArrayList<>();
    private OfflineAnimalListAdapter adapter;
    private LocalReceiver localReceiver;
    private String tag = "", auditId = "";
    private StringBuilder scannedData = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecyclerviewListBinding.inflate(getLayoutInflater());
        toolbarPrimaryBinding = ToolbarPrimaryBinding.bind(binding.toolbar.getRoot());
        setContentView(binding.getRoot());
        context = OfflineActivity.this;
        activity = this;

        init();
        if (getIntent().getStringExtra("title") != null){
            auditId = getIntent().getStringExtra("title");
        }
        checkLocationPermissions(permission -> turnGPSOn(this, permission1 -> {
            checkLocationSettings();
        }));
    }

    private void init() {
        try {
            IntentFilter filter = new IntentFilter("com.idforanimal.ACTION_VIEW_DETAILS");
            filter.addAction(Constant.refreshCount);
            localReceiver = new LocalReceiver();
            BroadCastManager.getInstance().registerReceiver(this, localReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbarPrimaryBinding.ivBluetooth.setOnClickListener(v -> {
            if (new Bluetooth(this).isOn()) {
                startBluetoothActivity();
            } else {
                enableBluetoothAndStartActivity();
            }
        });

        toolbarPrimaryBinding.ivBluetooth.setVisibility(View.VISIBLE);
        toolbarPrimaryBinding.ivBack.setOnClickListener(v -> finish());
        toolbarPrimaryBinding.tvTitle.setText("Offline Animals");
        binding.tvAdd.setText("Sync Data");
        binding.cvTotal.setVisibility(View.VISIBLE);
        binding.tvTotal.setText("Total : " + session.getStringList().size());
        binding.cvAdd.setVisibility(View.VISIBLE);
        binding.cvAdd.setOnClickListener(v -> {
            if (ConnectivityReceiver.isConnected(context)) {
                if (auditId == null || auditId.equals("")) {
                    Common.confirmationDialog(activity, "You need to go online first.", "No", "Yes", new Runnable() {
                        @Override
                        public void run() {
                            restartApp();
                        }
                    });
                } else {
                    for (OfflineAnimalData s : session.getStringList()) {
                        getDetails(s);
                    }
                }
            }else{
                Common.showToast("No internet connection");
            }
        });

        binding.cvExport.setOnClickListener(v -> {
            if (!session.getStringList().isEmpty()) {
                Common.generateExcelFile(context, session.getStringList());
            } else {
                Common.showToast("No data found");
            }
        });

        binding.cvClear.setOnClickListener(v -> {
            if (!session.getStringList().isEmpty()) {
                Common.confirmationDialog(activity, "Are you sure you want to clear data?", "No", "Yes", new Runnable() {
                    @Override
                    public void run() {
                        session.clearList();
                        getList();
                    }
                });
            } else {
                Common.showToast("No data to clear");
            }
        });
        binding.cardSearch.setVisibility(View.GONE);

        adapter = new OfflineAnimalListAdapter(context, list, new ClickListener() {
            @Override
            public void onItemSelected(int position) {
                Common.confirmationDialog(activity, "Are you sure you want to delete this microchip " + list.get(position).getMicroChipNo() + " ?", "No", "Yes", new Runnable() {
                    @Override
                    public void run() {
                        session.removeStringFromList(list.get(position));
                        getList();
                    }
                });

            }
        });

        Common.bindLoadMoreRecyclerView(binding.recyclerView, 1, RecyclerView.VERTICAL, new ClickListener() {
            @Override
            public void onLoadListener() {

            }
        });

        binding.recyclerView.setAdapter(adapter);

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            getList();
        });

        getList();
    }


    @Override
    protected void onDestroy() {
        BroadCastManager.getInstance().unregisterReceiver(this, localReceiver);
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
        if (new Bluetooth(this).isOn() && !session.getDeviceName().isEmpty()) {
            toolbarPrimaryBinding.ivBluetooth.setImageDrawable(getResources().getDrawable(R.drawable.bluetooth_on));
        } else {
            session.setDeviceName("");
            session.setDeviceAddress("");
            toolbarPrimaryBinding.ivBluetooth.setImageDrawable(getResources().getDrawable(R.drawable.bluetooth_off));
        }
        MyApplication.activityResumed();
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.hasExtra(Constant.refreshCount)) {
                String refreshValue = intent.getStringExtra(Constant.refreshCount);
                if (Constant.refreshCount.equals(refreshValue)) {
                    bindData(intent);
                }
            }
        }
    }

    private void bindData(Intent intent) {
        if (intent != null && intent.hasExtra("tag")) {
            tag = intent.getStringExtra("tag");
            if (tag != null && !tag.isEmpty()) {
                OfflineAnimalData data = new OfflineAnimalData();
                data.setMicroChipNo(tag);
                data.setLatitude(String.valueOf(latitude));
                data.setLongitude(String.valueOf(longitude));
                session.addStringToList(data);
                getList();
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
                        OfflineAnimalData data = new OfflineAnimalData();
                        data.setMicroChipNo(tag);
                        data.setLatitude(String.valueOf(latitude));
                        data.setLongitude(String.valueOf(longitude));
                        session.addStringToList(data);
                        getList();
                    });
                    scannedData.setLength(0);
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void getList() {
        list.clear();
        list.addAll(session.getStringList());

        binding.llOfflineDetail.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
        binding.tvTotal.setText("Total : " + (session.getStringList().isEmpty() ? "0" : session.getStringList().size()));
        if (list.isEmpty()) {
            binding.ivNotFound.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        } else {
            binding.ivNotFound.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    private void getDetails(OfflineAnimalData data) {
        Common.hideProgressDialog();
        if (ConnectivityReceiver.isConnected(context)) {
            Common.showProgressDialog(context, getString(R.string.please_wait));
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getAuditDetails(Constant.getAnimalAuditDetails, data.getMicroChipNo(), auditId,"1", Common.getEmpId(), Common.getCustomerId(), data.getLatitude(), data.getLongitude(), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    try {
                        Common.hideProgressDialog();
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1 || apiResponse.getStatus() == 2) {
                                session.removeStringFromList(data);
                            }
                            binding.tvTotal.setText("Total : " + session.getStringList().size());
                            getList();
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
}
