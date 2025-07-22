package com.idforanimal.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;

import androidx.recyclerview.widget.RecyclerView;

import com.idforanimal.bluetooth.Bluetooth;
import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.R;
import com.idforanimal.adapter.AnimalListAdapter;
import com.idforanimal.databinding.ActivityAddAnimalBinding;
import com.idforanimal.databinding.DialogShowanimaldetailBinding;
import com.idforanimal.databinding.LayoutCatchAnimalStepOneBinding;
import com.idforanimal.databinding.LayoutCatchAnimalStepTwoBinding;
import com.idforanimal.databinding.ToolbarPrimaryBinding;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.Animal;
import com.idforanimal.model.Catching;
import com.idforanimal.model.CommonModel;
import com.idforanimal.model.Pond;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.utils.BroadCastManager;
import com.idforanimal.utils.ClickListener;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;
import com.idforanimal.utils.MyApplication;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAnimalCatchingActivity extends BaseActivity {

    private ActivityAddAnimalBinding binding;
    private ToolbarPrimaryBinding toolbarPrimaryBinding;
    private LayoutCatchAnimalStepOneBinding layoutBinding1;
    private LayoutCatchAnimalStepTwoBinding layoutBinding2;
    private String id = "", keyword = "", catchingDate = "", zoneId = "", wardId = "", pondID = "",
            catchingTime = "", landmark = "", area = "", remark = "", status = "";
    private StringBuilder animalTypeId;
    private ArrayList<CommonModel> zoneList = new ArrayList<>();
    private ArrayList<CommonModel> wardList = new ArrayList<>();
    private ArrayList<Pond> cattlePondList = new ArrayList<>();
    private ArrayList<Animal> animalList = new ArrayList<>();
    private DialogShowanimaldetailBinding showAnimalDetailBinding;
    private Dialog dialog;
    private AnimalListAdapter adapter;
    private LocalReceiver localReceiver;
    private StringBuilder scannedData = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAnimalBinding.inflate(getLayoutInflater());
        layoutBinding1 = LayoutCatchAnimalStepOneBinding.bind(binding.layout3.getRoot());
        layoutBinding2 = LayoutCatchAnimalStepTwoBinding.bind(binding.layout4.getRoot());
        toolbarPrimaryBinding = ToolbarPrimaryBinding.bind(binding.toolbar.getRoot());
        setContentView(binding.getRoot());
        context = AddAnimalCatchingActivity.this;
        activity = this;
        Catching model = null;
        if (getIntent() != null && getIntent().hasExtra("details")) {
            model = (Catching) getIntent().getSerializableExtra("details");
        }

        if (model != null) {
            bindData(model);
            toolbarPrimaryBinding.tvTitle.setText("Update Animal Catching");
            layoutBinding2.tvSubmit.setText("Update");
        } else {
            toolbarPrimaryBinding.tvTitle.setText("Add Animal Catching");
            layoutBinding2.tvSubmit.setText("Add");
        }

        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constant.refreshCount);
            localReceiver = new LocalReceiver();
            BroadCastManager.getInstance().registerReceiver(this, localReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkLocationPermissions(view -> turnGPSOn(this, view1 -> {
            checkLocationSettings();
            new Handler().postDelayed(() -> layoutBinding1.etArea.setText(address), 1000);
        }));
        init();
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
                    runOnUiThread(() -> layoutBinding2.etSearch.setText(scannedData));
                    scannedData.setLength(0);
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void bindData(Catching model) {
        layoutBinding1.tvDate.setText(Common.changeDateFormat(model.getCatchedDate()));
        layoutBinding1.tvTime.setText(Common.changeDateFormat(model.getCatchedTime(), "hh:mm:ss", "HH:mm"));
        layoutBinding1.etArea.setText(model.getArea());
        layoutBinding1.etLandmark.setText(model.getLandmark());
        layoutBinding1.etRemarks.setText(model.getRemark());
        pondID = model.getPondId();
        zoneId = model.getZoneId();
        wardId = model.getWardId();
        id = model.getCatchingId();
        if (model.getAnimalList() != null && !model.getAnimalList().isEmpty()) {
            animalList.addAll(model.getAnimalList());
        }

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

        layoutBinding1.ivLocation.setOnClickListener(v -> checkLocationPermissions(permission -> turnGPSOn(this, permission1 -> {
            checkLocationSettings();
            new Handler().postDelayed(() -> layoutBinding1.etArea.setText(address), 1000);
        })));

        binding.llStep1.setOnClickListener(v -> {
            binding.tvStep1.setTextColor(getResources().getColor(R.color.darkBlack));
            binding.view1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            binding.tvStep2.setTextColor(getResources().getColor(R.color.textHintColor));
            binding.view2.setBackgroundColor(getResources().getColor(R.color.viewBackground));
            bindView("1");
        });
        binding.llStep2.setOnClickListener(v -> {
            binding.tvStep1.setTextColor(getResources().getColor(R.color.textHintColor));
            binding.view1.setBackgroundColor(getResources().getColor(R.color.viewBackground));
            binding.tvStep2.setTextColor(getResources().getColor(R.color.darkBlack));
            binding.view2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            bindView("2");
        });
        binding.llStep1.performClick();
        layoutBinding1.cvNext.setOnClickListener(v -> binding.llStep2.performClick());
        layoutBinding2.cvPrevious.setOnClickListener(v -> binding.llStep1.performClick());
        layoutBinding2.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    keyword = s.toString();
                    if (!s.toString().isEmpty()) {
                        layoutBinding2.ivSearch.setVisibility(View.VISIBLE);
                        layoutBinding2.ivClear.setVisibility(View.VISIBLE);
                    } else {
                        layoutBinding2.ivSearch.setVisibility(View.GONE);
                        layoutBinding2.ivClear.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        layoutBinding2.ivSearch.setOnClickListener(v -> {
            if (keyword.isEmpty()) {
                layoutBinding2.etSearch.setError("Enter keyword");
            } else {
                getBriefDetails();
            }
        });
        layoutBinding2.ivClear.setOnClickListener(v -> layoutBinding2.etSearch.getText().clear());

        layoutBinding1.tvDate.setOnClickListener(v -> Common.showDatePicker(context, dt -> {
            catchingDate = dt;
            layoutBinding1.tvDate.setText(Common.changeDateFormat(catchingDate));
        }));

        layoutBinding1.tvTime.setOnClickListener(v -> Common.showTimePicker(context, time -> {
            catchingTime = time;
            layoutBinding1.tvTime.setText(time);
        }));

        layoutBinding2.cvNext.setOnClickListener(v -> checkLocationPermissions(permission -> turnGPSOn(this, permission1 -> validate())));
    }

    private void bindView(String pages) {
        switch (pages) {
            case "1":
                binding.layout3.getRoot().setVisibility(View.VISIBLE);
                binding.layout4.getRoot().setVisibility(View.GONE);
                if (zoneList.isEmpty()) getZoneList();
                if (cattlePondList.isEmpty()) getCattlePondList();
                break;
            case "2":
                binding.layout3.getRoot().setVisibility(View.GONE);
                binding.layout4.getRoot().setVisibility(View.VISIBLE);
                if (adapter == null) bindRecyclerView();
                if (animalList != null && !animalList.isEmpty())
                    layoutBinding2.cvNext.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void getZoneList() {
        Common.showProgressDialog(context, context.getString(R.string.please_wait));
        if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getList(Constant.zoneListUrl, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                    Common.hideProgressDialog();

                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                if (apiResponse.getZoneList() != null) {
                                    bindZoneData(apiResponse);
                                }
                            } else {
                                Common.showToast(apiResponse.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t) {
                    t.printStackTrace();
                    Common.hideProgressDialog();
                }
            });
        } else {
            Common.noInternetDialog(this);
        }
    }

    private void bindZoneData(APIResponse apiResponse) {
        ArrayList<String> spinnerList = new ArrayList<>();
        zoneList.clear();

        if (apiResponse.getZoneList() != null) {
            zoneList.addAll(apiResponse.getZoneList());

            for (int i = 0; i < apiResponse.getZoneList().size(); i++) {
                spinnerList.add(apiResponse.getZoneList().get(i).getZoneName());
            }
        }

        layoutBinding1.spinnerZone.setItem(spinnerList);

        layoutBinding1.spinnerZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                zoneId = zoneList.get(i).getZoneId();
                getWardList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (!zoneId.equals("")) {
            for (int i = 0; i < zoneList.size(); i++) {
                if (zoneId.equals(zoneList.get(i).getZoneId())) {
                    layoutBinding1.spinnerZone.setSelection(i);
                    break;
                }
            }
        }
    }

    private void getWardList() {
        Common.showProgressDialog(context, context.getString(R.string.please_wait));
        if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getList(Constant.wardListUrl, zoneId, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                    Common.hideProgressDialog();

                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                if (apiResponse.getWardList() != null) {
                                    bindWardData(apiResponse);
                                }
                            } else {
                                Common.showToast(apiResponse.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t) {
                    t.printStackTrace();
                    Common.hideProgressDialog();
                }
            });
        } else {
            Common.noInternetDialog(this);
        }
    }

    private void bindWardData(APIResponse apiResponse) {
        ArrayList<String> spinnerList = new ArrayList<>();
        wardList.clear();

        if (apiResponse.getWardList() != null) {
            wardList.addAll(apiResponse.getWardList());

            for (int i = 0; i < apiResponse.getWardList().size(); i++) {
                spinnerList.add(apiResponse.getWardList().get(i).getWardName());
            }
        }

        layoutBinding1.spinnerWard.setItem(spinnerList);

        layoutBinding1.spinnerWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                wardId = wardList.get(i).getWardId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (!wardId.equals("")) {
            for (int i = 0; i < wardList.size(); i++) {
                if (wardId.equals(wardList.get(i).getWardId())) {
                    layoutBinding1.spinnerWard.setSelection(i);
                    break;
                }
            }
        }
    }

    private void getCattlePondList() {
        if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getList(Constant.CattlePondList, Common.getCustomerId(), "1", Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                if (apiResponse.getPondArray() != null)
                                    bindCattlePondData(apiResponse);
                            } else {
                                Common.showToast(apiResponse.getMessage());
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

    private void bindCattlePondData(APIResponse apiResponse) {
        ArrayList<String> spinnerList = new ArrayList<>();
        cattlePondList.clear();

        if (apiResponse.getPondArray() != null) {
            cattlePondList.addAll(apiResponse.getPondArray());
            for (int i = 0; i < apiResponse.getPondArray().size(); i++) {
                spinnerList.add(apiResponse.getPondArray().get(i).getPondName());
            }
        }

        layoutBinding1.spinnerCattlePond.setItem(spinnerList);

        layoutBinding1.spinnerCattlePond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pondID = cattlePondList.get(i).getPondId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (!pondID.equals("")) {
            for (int i = 0; i < cattlePondList.size(); i++) {
                if (pondID.equals(cattlePondList.get(i).getPondId())) {
                    layoutBinding1.spinnerCattlePond.setSelection(i);
                    break;
                }
            }
        }
    }

    private void getBriefDetails() {
        if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getDetails(Constant.getAnimalBriefDetails, keyword, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                openDialog(apiResponse.getAnimalDetails());
                            } else {
                                Common.confirmationDialog(context, "Microchip No." + keyword + " not found in the database. Would you like to add this animal?", "No", "Yes", () -> getOwnerIDApi());
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

    public void getOwnerIDApi() {
        if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getDetails(Constant.getOwnerIDApi, Common.getCustomerId(), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                goToActivityForResult(new Intent(context, AddAnimalActivity.class).putExtra("ownerId", apiResponse.getOwnerId()), (data, resultCode) -> {
                                    if (resultCode == Activity.RESULT_OK) {
                                        if (data != null) {
                                            if (data.hasExtra("details")) {
                                                Animal model = (Animal) data.getSerializableExtra("details");
                                                if (model != null) {
                                                    keyword = model.getRfidTagNo();
                                                    getBriefDetails();
                                                }
                                            }
                                        }
                                    }
                                });
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


    public void openDialog(Animal animal) {
        showAnimalDetailBinding = DialogShowanimaldetailBinding.inflate(getLayoutInflater());
        dialog = new Dialog(context, R.style.Theme_MyDialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(showAnimalDetailBinding.getRoot());

        if (animal != null) {
            showAnimalDetailBinding.txtRFID.setText(animal.getRfidTagNo());
            showAnimalDetailBinding.txtVisualNo.setText(animal.getVisualTagNo());
            StringBuilder stringBuilder = new StringBuilder();
            if (!animal.getAnimalName().isEmpty()) stringBuilder.append(animal.getAnimalName());

            if (!animal.getCattleName().isEmpty()) {
                if (stringBuilder.length() > 0) stringBuilder.append("-");
                stringBuilder.append(animal.getCattleName());
            }
            showAnimalDetailBinding.tvAnimalType.setText(stringBuilder.toString());

            showAnimalDetailBinding.tvOwnerName.setText(animal.getOwnerName());
            showAnimalDetailBinding.tvOwnerContact.setText(animal.getOwnerContact());

            showAnimalDetailBinding.tvLastCatchingDetail.setText(Common.getLastDetail(animal.getCatchingLocation(), animal.getTotalCatching(), "CATCHING"));
            showAnimalDetailBinding.tvLastTreatmentDetail.setText(Common.getLastDetail(animal.getTreatmentData(), animal.getTotalTreatment(), "TREATMENT"));
            showAnimalDetailBinding.tvLastVaccinationDetail.setText(Common.getLastDetail(animal.getVaccinationData(), animal.getTotalVaccination(), "VACCINATION"));
        }
        showAnimalDetailBinding.btnDismiss.setOnClickListener(v -> dialog.dismiss());

        showAnimalDetailBinding.btnAdd.setOnClickListener(v -> {
            if (!animalList.isEmpty()) {
                for (int i = 0; i < animalList.size(); i++) {
                    if (animalList.get(i).getAnimalId().equals(animal.getAnimalId())) {
                        Common.showToast("Already Added");
                        return;
                    }
                }
            }
            animalList.add(animal);
            if (layoutBinding2.cvNext.getVisibility() == View.GONE)
                layoutBinding2.cvNext.setVisibility(View.VISIBLE);
            if (layoutBinding2.recyclerView.getVisibility() == View.GONE) {
                layoutBinding2.recyclerView.setVisibility(View.VISIBLE);
                layoutBinding2.ivNotFound.setVisibility(View.GONE);
            }
            if (adapter != null) adapter.notifyDataSetChanged();
            dialog.dismiss();
        });
        dialog.show();
    }

    private void bindRecyclerView() {
        adapter = new AnimalListAdapter(context, false, animalList, new ClickListener() {
            @Override
            public void onItemSelected(int position) {
                Common.confirmationDialog(context, getString(R.string.confirmation_remove), "No", "Yes", new Runnable() {
                    @Override
                    public void run() {
                        animalList.remove(position);
                        if (adapter != null) adapter.notifyItemRemoved(position);
                        if (animalList.isEmpty()) {
                            layoutBinding2.ivNotFound.setVisibility(View.VISIBLE);
                            layoutBinding2.recyclerView.setVisibility(View.GONE);
                            layoutBinding2.cvNext.setVisibility(View.GONE);
                        } else layoutBinding2.cvNext.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        layoutBinding2.recyclerView.setAdapter(adapter);
        Common.bindLoadMoreRecyclerView(layoutBinding2.recyclerView, 1, RecyclerView.VERTICAL, null);
    }

    private void validate() {
        landmark = layoutBinding1.etLandmark.getText().toString().trim();
        area = layoutBinding1.etArea.getText().toString().trim();
        remark = layoutBinding1.etRemarks.getText().toString().trim();
        catchingDate = layoutBinding1.tvDate.getText().toString().trim();
        catchingTime = layoutBinding1.tvTime.getText().toString().trim();

        if (!animalList.isEmpty()) {
            animalTypeId = new StringBuilder("");
            for (int i = 0; i < animalList.size(); i++) {
                if (animalList.get(i).getAnimalId() != null) {
                    if (i < animalList.size() - 1) {
                        animalTypeId = animalTypeId.append(animalList.get(i).getAnimalId()).append(",");
                    } else animalTypeId = animalTypeId.append(animalList.get(i).getAnimalId());
                } else animalTypeId = animalTypeId.append(",");
            }
        }
        if (catchingDate.equals("")) {
            layoutBinding1.tvDate.setError("Select date");
            binding.llStep1.performClick();
        } else if (catchingTime.equals("")) {
            layoutBinding1.tvTime.setError("Select time");
            binding.llStep1.performClick();
        } else if (pondID.equals("")) {
            Common.showToast("Select Cattle Pond");
            if (cattlePondList.isEmpty()) getCattlePondList();
            binding.llStep1.performClick();
        } else if (zoneId.equals("")) {
            Common.showToast("Select Zone");
            if (zoneList.isEmpty()) getZoneList();
            binding.llStep1.performClick();
        } else if (wardId.equals("")) {
            Common.showToast("Select Zone");
            if (wardList.isEmpty()) getWardList();
            binding.llStep1.performClick();
        } else if (latitude == 0.0 && longitude == 0.0) {
            checkLocationPermissions(permission -> turnGPSOn(this, permission1 -> checkLocationSettings()));
            Common.showToast("Location not found");
        } else {
            addData();
        }
    }

    private void addData() {
        if (ConnectivityReceiver.isConnected(context)) {
            Common.showProgressDialog(context, context.getString(R.string.please_wait));

            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.addEditCatching(Constant.addEditCatching, id, String.valueOf(animalTypeId), remark, area, landmark, wardId, zoneId, pondID, catchingTime, catchingDate, String.valueOf(latitude), String.valueOf(longitude), Common.getCustomerId(), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    Common.hideProgressDialog();

                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                if (apiResponse.getAnimalCatchingDetails() != null) {
                                    Intent intent = new Intent();
                                    intent.putExtra("details", apiResponse.getAnimalCatchingDetails());
                                    setResultOfActivity(intent, 1, true);
                                }
                            }
                            Common.showToast(apiResponse.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t) {
                    t.printStackTrace();
                    Common.hideProgressDialog();
                }
            });
        } else {
            Common.noInternetDialog(this);
        }
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
        MyApplication.activityResumed();
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

    private void bindData(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra("status")) {
                status = intent.getStringExtra("status");
            }
            if (intent.hasExtra("tag")) {
                keyword = intent.getStringExtra("tag");
                getBriefDetails();
            }
        }
    }
}