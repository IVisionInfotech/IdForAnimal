package com.idforanimal.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.idforanimal.bluetooth.Bluetooth;
import com.idforanimal.floatingactionmenu.FloatingActionModel;
import com.idforanimal.loadmore.RecyclerViewLoadMoreScroll;
import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.R;
import com.idforanimal.adapter.AnimalHistoryListAdapter;
import com.idforanimal.databinding.ActivityAnimalDetailsBinding;
import com.idforanimal.databinding.DialogAnimaldeathBinding;
import com.idforanimal.databinding.DialogTreatmentBinding;
import com.idforanimal.databinding.ToolbarPrimaryBinding;
import com.idforanimal.dialog.FragmentAnimalDetail;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.Animal;
import com.idforanimal.model.AnimalOwner;
import com.idforanimal.model.CommonModel;
import com.idforanimal.model.Pond;
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

public class AnimalDetailsActivity extends BaseActivity {

    private ActivityAnimalDetailsBinding binding;
    private ToolbarPrimaryBinding toolbarPrimaryBinding;
    private ArrayList<Animal> list = new ArrayList<>();
    private String id = "", status = "", tag = "", ownerId = "";
    private AnimalHistoryListAdapter adapter;
    private String oldOwnerId = "", newOwnerId = "", rfidTagNo = "", visualTagNo = "", historyType = "", date = "",
            pondStatus = "0", pondId = "", other = "", operationId = "", remark = "";
    private LocalReceiver localReceiver;
    private ArrayList<Pond> pondList = new ArrayList<>();
    private ArrayList<CommonModel> operationList = new ArrayList<>();
    private ArrayList<AnimalOwner> ownerArrayList = new ArrayList<>();
    private DialogTreatmentBinding bottomBinding;
    private DialogAnimaldeathBinding dialogAnimaldeathBinding;
    private BottomSheetDialog bottomSheetDialog;
    private Animal model;
    private int offset = 0, limit = 10;
    private RecyclerViewLoadMoreScroll scrollListener;
    private StringBuilder scannedData = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimalDetailsBinding.inflate(getLayoutInflater());
        toolbarPrimaryBinding = ToolbarPrimaryBinding.bind(binding.toolbar.getRoot());
        setContentView(binding.getRoot());

        context = AnimalDetailsActivity.this;
        activity = this;
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constant.refreshCount);
            localReceiver = new LocalReceiver();
            BroadCastManager.getInstance().registerReceiver(this, localReceiver, filter);//Registered broadcast recipient
        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbarPrimaryBinding.tvTitle.setText("Animal Details");
        init();
        bindData(getIntent());
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

        View.OnClickListener onClick = v -> {
            switch (binding.mainFloatingActionMenu.getCurrentClickedTag()) {
                case "0":
                    showVaccinationDialog(0);
                    break;
                case "1":
                    showVaccinationDialog(1);
                    break;
                case "2":
                    showVaccinationDialog(2);
                    break;
                case "3":
                    showVaccinationDialog(3);
                    break;
                case "4":
                    showDeathDialog();
                    break;
            }
        };

        binding.mainFloatingActionMenu
                .setMainButtonDrawableId(R.drawable.add)
                .addSubFloatingActionButton(new FloatingActionModel(R.drawable.tretment, R.color.blue, R.string.animal_treatment), onClick, "0")
                .addSubFloatingActionButton(new FloatingActionModel(R.drawable.vaccinated, R.color.blue, R.string.animal_vaccination), onClick, "1")
                .addSubFloatingActionButton(new FloatingActionModel(R.drawable.tag, R.color.green, R.string.animal_retagging), onClick, "2")
                .addSubFloatingActionButton(new FloatingActionModel(R.drawable.truck, R.color.green, R.string.animal_transfer), onClick, "3")
                .addSubFloatingActionButton(new FloatingActionModel(R.drawable.death, R.color.red, R.string.animal_death), onClick, "4")
                .apply();


        adapter = new AnimalHistoryListAdapter(context, list, null);
        binding.recyclerView.setAdapter(adapter);
        scrollListener = Common.bindLoadMoreRecyclerView(binding.recyclerView, 1, RecyclerView.VERTICAL, new ClickListener() {
            @Override
            public void onLoadListener() {
                getList(true);
            }
        });
    }


    private void getList(boolean loadMore) {
        if (ConnectivityReceiver.isConnected(context)) {
            if (!loadMore) {
                Common.showProgressDialog(context, context.getString(R.string.please_wait));
                offset = 0;
                limit = 10;
                list.clear();
            } else {
                adapter.addLoadingView();
            }

            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getList(Constant.getAnimalHistory, id, String.valueOf(limit), String.valueOf(offset), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                    if (loadMore) {
                        adapter.removeLoadingView();
                    } else {
                        Common.hideProgressDialog();
                    }
                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                bindListData(apiResponse);
                                scrollListener.setLoaded();
                                binding.recyclerView.setVisibility(View.VISIBLE);
                            } else {
                                if (loadMore) {
                                    Common.showToast(apiResponse.getMessage());
                                } else {
                                    list.clear();
                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
                                    }
                                    binding.recyclerView.setVisibility(View.GONE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    if (loadMore) {
                        adapter.removeLoadingView();
                    } else {
                        Common.hideProgressDialog();
                    }
                }
            });
        } else {
            Common.noInternetDialog(this);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void bindListData(APIResponse apiResponse) {
        if (apiResponse.getAnimalHistory() != null) {
            offset = offset + apiResponse.getAnimalHistory().size();
            list.addAll(apiResponse.getAnimalHistory());
            if (!TextUtils.isEmpty(list.get(0).getOldOwnerId())) {
                oldOwnerId = list.get(0).getOldOwnerId();
            }
        }
        if (adapter != null) adapter.notifyDataSetChanged();
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

    private void bindData(Intent intent) {
        if (intent != null) {
            if (getIntent().hasExtra("details")) {
                model = (Animal) getIntent().getSerializableExtra("details");
                if (model != null) {
                    id = model.getAnimalId();
                    tag = model.getRfidTagNo();
                }
                getDetails();
            }
            if (intent.hasExtra("status")) {
                status = intent.getStringExtra("status");
            }
            if (intent.hasExtra("tag")) {
                tag = intent.getStringExtra("tag");
                if (status.equals(Constant.typeVisualTag)) {
                    binding.txtVisualNo.setText(tag);
                } else if (status.equals(Constant.typeRFIDTag)) {
                    binding.tvRFID.setText(tag);
                }
                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    bottomBinding.etRFID.setText(tag);
                } else
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
                        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                            bottomBinding.etRFID.setText(tag);
                        } else
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
            apiInterface.getDetails(Constant.getAnimalBriefDetails, tag, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    try {
                        Common.hideProgressDialog();
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                bindData(apiResponse.getAnimalDetails());
                                getList(false);
                            } else {
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
            if (!model.getCustomerId().equals("") && model.getCustomerId().equals(Common.getCustomerId())) {
                binding.mainFloatingActionMenu.setVisibility(View.VISIBLE);
            } else binding.mainFloatingActionMenu.setVisibility(View.GONE);
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
        }

        rfidTagNo = model.getRfidTagNo();
        visualTagNo = model.getVisualTagNo();
        id = model.getAnimalId();
        ownerId = model.getOwnerId();

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

    private void showDeathDialog() {
        dialogAnimaldeathBinding = DialogAnimaldeathBinding.inflate(getLayoutInflater());
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        bottomSheetDialog.setContentView(dialogAnimaldeathBinding.getRoot());
        dialogAnimaldeathBinding.etDeathDate.setText(Common.getCurrentDate());

        dialogAnimaldeathBinding.etDeathDate.setOnClickListener(v -> Common.showDatePicker(context, date1 -> {
            dialogAnimaldeathBinding.etDeathDate.setText(Common.changeDateFormat(date1));
        }));

        dialogAnimaldeathBinding.etDisposedDate.setOnClickListener(v -> Common.showDatePicker(context, date1 -> {
            dialogAnimaldeathBinding.etDisposedDate.setText(Common.changeDateFormat(date1));
        }));


        dialogAnimaldeathBinding.btnSubmit.setOnClickListener(v -> {
            String disposedReceipt = dialogAnimaldeathBinding.etDisposed.getText().toString();
            String deathCertificateNo = dialogAnimaldeathBinding.etDeathCertificateNo.getText().toString();
            String deathDate = dialogAnimaldeathBinding.etDeathDate.getText().toString();
            String disposedDate = dialogAnimaldeathBinding.etDisposedDate.getText().toString();

            checkLocationPermissions(permission ->
                    turnGPSOn(AnimalDetailsActivity.this, permission1 -> callApiDeath(deathDate, disposedDate, deathCertificateNo, disposedReceipt)));
        });
        bottomSheetDialog.show();
    }

    private void callApiDeath(String deathDate, String disposedDate, String deathCertificateNo, String disposedReceipt) {
        if (ConnectivityReceiver.isConnected(context)) {
            Common.showProgressDialog(context, context.getString(R.string.please_wait));
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.editAnimalDeath(Constant.editAnimalDeath, id, deathDate, disposedDate, deathCertificateNo, disposedReceipt, String.valueOf(latitude), String.valueOf(longitude), Common.getCustomerId(), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {

                @Override
                public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                    Common.hideProgressDialog();
                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                bottomSheetDialog.dismiss();
                                getDetails();
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

    private void showVaccinationDialog(int type) {
        String message = getResources().getString(R.string.confirmation_message);
        bottomBinding = DialogTreatmentBinding.inflate(getLayoutInflater());
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        bottomSheetDialog.setContentView(bottomBinding.getRoot());
        bottomBinding.etDate.setText(Common.getCurrentDate());
        checkLocationPermissions(view -> turnGPSOn(this, view1 -> {
            checkLocationSettings();
            new Handler().postDelayed(() -> bottomBinding.etRemark.setText(address), 1000);
        }));

        bottomBinding.etDate.setOnClickListener(v -> Common.showDatePicker(context, date1 -> {
            bottomBinding.etDate.setText(Common.changeDateFormat(date1));
        }));

        bottomBinding.rdgSourceOfIdentify.setOnCheckedChangeListener((group, checkedId) -> {
            if (bottomBinding.rdOnField.isChecked()) {
                bottomBinding.spinnerCattle.setVisibility(View.GONE);
                pondStatus = "0";
            } else if (bottomBinding.rdCattlePond.isChecked()) {
                bottomBinding.spinnerCattle.setVisibility(View.VISIBLE);
                pondStatus = "1";
            }
        });

        if (type == 0) {
            message = message + " " + "add Treatment & Operation";
            bottomBinding.tvTitle.setText("Animal Treatment & Operation");
            bottomBinding.tvSpinnerTitle.setText("Animal Treatment & Operation");
            bottomBinding.llTreatment.setVisibility(View.VISIBLE);
            bottomBinding.llRetagging.setVisibility(View.GONE);
            bottomBinding.llIdentify.setVisibility(View.VISIBLE);
            getOperationList(type);
            getCattlePondList();
            historyType = Constant.TREATMENT;
        } else if (type == 1) {
            message = message + " " + "add Vaccination";
            bottomBinding.tvTitle.setText("Animal Vaccination");
            bottomBinding.tvSpinnerTitle.setText("Animal Vaccination");
            bottomBinding.llTreatment.setVisibility(View.VISIBLE);
            bottomBinding.llRetagging.setVisibility(View.GONE);
            bottomBinding.llIdentify.setVisibility(View.VISIBLE);
            getOperationList(type);
            getCattlePondList();
            historyType = Constant.VACCINATION;
        } else if (type == 2) {
            message = message + " " + "Animal Retagging";
            bottomBinding.tvTitle.setText("Animal Retagging");
            bottomBinding.llTreatment.setVisibility(View.GONE);
            bottomBinding.llRetagging.setVisibility(View.VISIBLE);
            bottomBinding.etRFID.setText(rfidTagNo);
            bottomBinding.etVisualTagNo.setText(visualTagNo);
            historyType = Constant.RETAGGING;
        } else if (type == 3) {
            message = message + " " + "Animal Transfer";
            bottomBinding.tvTitle.setText("Animal Transfer");
            bottomBinding.tvSpinnerTitle.setText("Owner Name");
            bottomBinding.llTreatment.setVisibility(View.VISIBLE);
            bottomBinding.llRetagging.setVisibility(View.GONE);
            bottomBinding.llIdentify.setVisibility(View.GONE);
            historyType = Constant.TRANSFER;
            getOwnerList();
        }

        String finalMessage = message;
        bottomBinding.btnSubmit.setOnClickListener(v -> checkLocationPermissions(permission -> turnGPSOn(AnimalDetailsActivity.this, permission1 -> validate(finalMessage))));

        bottomSheetDialog.show();
    }

    private void validate(String finalMessage) {
        date = Common.changeDateFormat(bottomBinding.etDate.getText().toString(), "dd-MM-yyyy", "yyyy-MM-dd");
        remark = bottomBinding.etRemark.getText().toString().trim();
        other = bottomBinding.etOther.getText().toString().trim();

        if (historyType.equals(Constant.RETAGGING)) {
            visualTagNo = bottomBinding.etVisualTagNo.getText().toString().trim();
            rfidTagNo = bottomBinding.etRFID.getText().toString().trim();
        }
        if (historyType.equals(Constant.TRANSFER)) {
            oldOwnerId = ownerId;
        } else {
            newOwnerId = ownerId;
        }

        if (pondStatus.equals("1") && pondId.equals("")) {
            Common.showToast("Select pond");
            if (pondList.isEmpty()) getCattlePondList();
        } else if (operationId.equals("0") && other.equals("")) {
            bottomBinding.etOther.setError("Enter Other detail");
        } else if (historyType.equals(Constant.RETAGGING) && rfidTagNo.equals("") || visualTagNo.equals("")) {
            if (rfidTagNo.equals("")) {
                bottomBinding.etRFID.setError("");
            } else bottomBinding.etVisualTagNo.setError("");
        } else if (historyType.equals(Constant.TRANSFER) && newOwnerId.equals("")) {
            Common.showToast("Select owner");
        } else {
            Common.confirmationDialog(context, finalMessage, "No", "Yes", () ->
                    checkLocationPermissions(permission -> turnGPSOn(AnimalDetailsActivity.this, permission1 -> addData())));
        }
    }

    private void addData() {
        if (ConnectivityReceiver.isConnected(context)) {
            Common.showProgressDialog(context, context.getString(R.string.please_wait));
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.addAnimalHistory(Constant.addTreatment, id, oldOwnerId, newOwnerId, rfidTagNo, visualTagNo, historyType, date, pondStatus, pondId, other, operationId, remark, String.valueOf(latitude), String.valueOf(longitude), Common.getCustomerId(), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {

                @Override
                public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                    Common.hideProgressDialog();
                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                bottomSheetDialog.dismiss();
                                getDetails();
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

    private void getCattlePondList() {
        if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getList(Constant.CattlePondList, Common.getCustomerId(), "1", Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
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
                    Common.hideProgressDialog();
                    t.printStackTrace();
                }
            });
        } else {
            Common.noInternetDialog(this);
        }
    }

    private void bindCattlePondData(APIResponse apiResponse) {
        ArrayList<String> spinnerList = new ArrayList<>();
        pondList.clear();

        if (apiResponse.getPondArray() != null) {
            pondList.addAll(apiResponse.getPondArray());

            for (int i = 0; i < apiResponse.getPondArray().size(); i++) {
                spinnerList.add(apiResponse.getPondArray().get(i).getPondName());
            }
        }

        bottomBinding.spinnerCattle.setItem(spinnerList);

        bottomBinding.spinnerCattle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pondId = pondList.get(i).getPondId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (!pondId.equals("")) {
            for (int i = 0; i < pondList.size(); i++) {
                if (pondId.equals(pondList.get(i).getPondId())) {
                    bottomBinding.spinnerCattle.setSelection(i);
                    break;
                }
            }
        }
    }

    private void getOperationList(int type) {
        if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getList(Constant.getOperationList, String.valueOf(type), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                bindOperationData(apiResponse);
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
                    Common.hideProgressDialog();
                    t.printStackTrace();
                }
            });
        } else {
            Common.noInternetDialog(this);
        }
    }

    private void bindOperationData(APIResponse apiResponse) {
        ArrayList<String> operationList1 = new ArrayList<>();
        operationList.clear();

        if (apiResponse.getOperationList() != null && apiResponse.getOperationList().size() > 0) {
            operationList.addAll(apiResponse.getOperationList());
            CommonModel model1 = new CommonModel();
            model1.setId("0");
            model1.setName("other");
            operationList.add(model1);

            for (int i = 0; i < operationList.size(); i++) {
                operationList1.add(operationList.get(i).getName());
            }
        }

        bottomBinding.spinnerType.setItem(operationList1);

        bottomBinding.spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                operationId = operationList.get(i).getId();
                if (Objects.equals(operationId, "0")) {
                    bottomBinding.etOther.clearComposingText();
                    bottomBinding.llOther.setVisibility(View.VISIBLE);
                } else bottomBinding.llOther.setVisibility(View.GONE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void getOwnerList() {
        if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getList(Constant.animalOwnerListUrl, Common.getCustomerId(), "", "", "", Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                bindOwnerData(apiResponse);
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
                    Common.hideProgressDialog();
                    t.printStackTrace();
                }
            });
        } else {
            Common.noInternetDialog(this);
        }
    }

    private void bindOwnerData(APIResponse apiResponse) {
        ArrayList<String> ownerList = new ArrayList<>();
        ownerArrayList.clear();

        if (apiResponse.getAnimalOwnerList() != null) {
            ownerArrayList.addAll(apiResponse.getAnimalOwnerList());

            for (int i = 0; i < ownerArrayList.size(); i++) {
                ownerList.add(ownerArrayList.get(i).getFirstName() + " " + ownerArrayList.get(i).getMiddleName() + " " + ownerArrayList.get(i).getLastName());
            }
        }

        bottomBinding.spinnerType.setItem(ownerList);

        bottomBinding.spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newOwnerId = ownerArrayList.get(i).getOwnerId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (!newOwnerId.equals("")) {
            for (int i = 0; i < ownerArrayList.size(); i++) {
                if (newOwnerId.equals(ownerArrayList.get(i).getOwnerId())) {
                    bottomBinding.spinnerCattle.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (model != null) {
            Intent intent = new Intent();
            intent.putExtra("details", model);
            setResultOfActivity(intent, 1, true);
        }
        super.onBackPressed();
    }
}