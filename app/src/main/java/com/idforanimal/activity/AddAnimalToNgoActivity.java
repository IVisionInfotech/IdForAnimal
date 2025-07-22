package com.idforanimal.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.idforanimal.bluetooth.Bluetooth;
import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.R;
import com.idforanimal.adapter.AnimalListAdapter;
import com.idforanimal.databinding.ActivityAddAnimalBinding;
import com.idforanimal.databinding.DialogShowanimaldetailBinding;
import com.idforanimal.databinding.LayoutCatchAnimalStepTwoBinding;
import com.idforanimal.databinding.LayoutNgoAnimalStepOneBinding;
import com.idforanimal.databinding.ToolbarPrimaryBinding;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.Animal;
import com.idforanimal.model.AnimalOwner;
import com.idforanimal.utils.BroadCastManager;
import com.idforanimal.utils.ClickListener;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;
import com.idforanimal.utils.MyApplication;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAnimalToNgoActivity extends BaseActivity {

    private ActivityAddAnimalBinding binding;
    private LayoutNgoAnimalStepOneBinding layoutBinding1;
    private ToolbarPrimaryBinding toolbarPrimaryBinding;
    private LayoutCatchAnimalStepTwoBinding layoutBinding2;
    private String id = "", keyword = "", date = "", status = "", remark = "", ngoID = "", vehicleRegNo = "", applicationDate = "", driverName = "",
            driverContact = "", alternativeContact = "", cleanerName = "", applicationNo = "", deatil = "";
    private StringBuilder animalTypeId = new StringBuilder("");
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
        layoutBinding1 = LayoutNgoAnimalStepOneBinding.bind(binding.layout6.getRoot());
        layoutBinding2 = LayoutCatchAnimalStepTwoBinding.bind(binding.layout4.getRoot());
        toolbarPrimaryBinding = ToolbarPrimaryBinding.bind(binding.toolbar.getRoot());
        setContentView(binding.getRoot());
        context = AddAnimalToNgoActivity.this;
        activity = this;
        if (getIntent() != null) {
            if (getIntent().hasExtra("ownerDetails")) {
                AnimalOwner model = (AnimalOwner) getIntent().getSerializableExtra("ownerDetails");
                if (model != null)
                    bindData(model);
            }
        }
        layoutBinding2.tvSubmit.setText("Add");

        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constant.refreshCount);
            localReceiver = new LocalReceiver();
            BroadCastManager.getInstance().registerReceiver(this, localReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbarPrimaryBinding.tvTitle.setText("Add Transfer Animal to NGO");
        init();
        checkLocationPermissions( permission -> turnGPSOn(this, permission1 -> checkLocationSettings()));
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

    private void bindData(AnimalOwner model) {
//        layoutBinding1.tvDate.setText(Common.changeDateFormat(model.getReleaseDate()));
//        layoutBinding1.etFirNo.setText(model.getFirNo());
//        layoutBinding1.etChargeSheetNo.setText(model.getChargeSheetNo());
//        layoutBinding1.etAffidavitNo.setText(model.getAffidavitNo());
//        layoutBinding1.etPaymentReceiptNo.setText(model.getPaymentReceiptNo());
//        layoutBinding1.etRemarks.setText(model.getRemark());
//        if (model.getAnimalList() != null && !model.getAnimalList().isEmpty()) {
//            animalList.addAll(model.getAnimalList());
//        }

        ngoID = model.getNgoId();
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
        layoutBinding2.ivClear.setOnClickListener(v -> {
            layoutBinding2.etSearch.getText().clear();
        });

        layoutBinding1.tvDate.setOnClickListener(v -> Common.showDatePicker(context, dt -> {
            date = dt;
            layoutBinding1.tvDate.setText(Common.changeDateFormat(date));
        }));

        layoutBinding1.tvApplicationDate.setOnClickListener(v -> Common.showDatePicker(context, dt -> {
            applicationDate = dt;
            layoutBinding1.tvApplicationDate.setText(Common.changeDateFormat(date));
        }));

        layoutBinding2.cvNext.setOnClickListener(v -> checkLocationPermissions( permission -> turnGPSOn(this, permission1 -> validate())));
    }

    private void bindView(String pages) {
        switch (pages) {
            case "1":
                binding.layout6.getRoot().setVisibility(View.VISIBLE);
                binding.layout4.getRoot().setVisibility(View.GONE);
                break;
            case "2":
                binding.layout6.getRoot().setVisibility(View.GONE);
                binding.layout4.getRoot().setVisibility(View.VISIBLE);
                if (adapter == null) bindRecyclerView();
                if (animalList != null && !animalList.isEmpty())
                    layoutBinding2.cvNext.setVisibility(View.VISIBLE);
                break;
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
                        } else Common.showToast(apiResponse.getMessage());
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
            if (animal != null && animal.getAnimalId() != null) {
                if (!animalTypeId.toString().contains(Objects.requireNonNull(animal).getAnimalId())) {
                    animalList.add(animal);
                    if (layoutBinding2.cvNext.getVisibility() == View.GONE)
                        layoutBinding2.cvNext.setVisibility(View.VISIBLE);
                    if (layoutBinding2.recyclerView.getVisibility() == View.GONE) {
                        layoutBinding2.recyclerView.setVisibility(View.VISIBLE);
                        layoutBinding2.ivNotFound.setVisibility(View.GONE);
                    }
                    if (adapter != null) adapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    Common.showToast("Already Added");
                }
            } else {
                if (animalTypeId != null && !animalTypeId.toString().contains(null)) {
                    animalList.add(animal);
                    if (layoutBinding2.cvNext.getVisibility() == View.GONE)
                        layoutBinding2.cvNext.setVisibility(View.VISIBLE);
                    if (layoutBinding2.recyclerView.getVisibility() == View.GONE) {
                        layoutBinding2.recyclerView.setVisibility(View.VISIBLE);
                        layoutBinding2.ivNotFound.setVisibility(View.GONE);
                    }
                    if (adapter != null) adapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    Common.showToast("Already Added");
                }
            }
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
                        if (animalList.size() == 0) {
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
        vehicleRegNo = layoutBinding1.etVehicleNo.getText().toString().trim();
        driverName = layoutBinding1.etDriverName.getText().toString().trim();
        driverContact = layoutBinding1.etDriverContact.getText().toString().trim();
        alternativeContact = layoutBinding1.etAlternativeContact.getText().toString().trim();
        cleanerName = layoutBinding1.etCleanerName.getText().toString().trim();
        applicationNo = layoutBinding1.etApplicationNo.getText().toString().trim();
        deatil = layoutBinding1.etDetails.getText().toString().trim();
        remark = layoutBinding1.etRemarks.getText().toString().trim();
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


        if (date.equals("")) {
            layoutBinding1.tvDate.setError("Select tagging date");
            binding.llStep1.performClick();
        } else if (vehicleRegNo.equals("")) {
            layoutBinding1.etVehicleNo.setError("Enter vehicle no");
            binding.llStep1.performClick();
        } else if (latitude == 0.0 && longitude == 0.0) {
            checkLocationPermissions( permission -> turnGPSOn(this, permission1 -> checkLocationSettings()));
            Common.showToast("Location not found");
        } else {
            addData();
        }
    }

    private void addData() {
         if (ConnectivityReceiver.isConnected(context)) {
        Common.showProgressDialog(context, context.getString(R.string.please_wait));
        ApiInterface apiInterface = ApiUtils.getApiCalling();
        apiInterface.addEditNgoTransfer(Constant.addEditNgoTransfer, id, Common.getCustomerId(), ngoID, date, vehicleRegNo, driverName, driverContact, alternativeContact, cleanerName, deatil, applicationNo, applicationDate, String.valueOf(animalTypeId), remark, String.valueOf(latitude), String.valueOf(longitude), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                Common.hideProgressDialog();
                try {
                    APIResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.getStatus() == 99) {
                            Common.logout(context, apiResponse.getMessage());
                        } else if (apiResponse.getStatus() == 1) {
                            Common.showToast(apiResponse.getMessage());
                            setResultOfActivity(new Intent(), 1, true);
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