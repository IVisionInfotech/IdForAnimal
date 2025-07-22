package com.idforanimal.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.idforanimal.bluetooth.Bluetooth;
import com.idforanimal.loadmore.RecyclerViewLoadMoreScroll;
import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.R;
import com.idforanimal.adapter.AnimalListAdapter;
import com.idforanimal.databinding.ActivityRecyclerviewListBinding;
import com.idforanimal.databinding.DialogAnimaldeathBinding;
import com.idforanimal.databinding.DialogTreatmentBinding;
import com.idforanimal.databinding.ToolbarPrimaryBinding;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.Animal;
import com.idforanimal.model.AnimalOwner;
import com.idforanimal.model.CommonModel;
import com.idforanimal.model.Pond;
import com.idforanimal.service.ConnectivityReceiver;
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

public class AnimalListActivity extends BaseActivity {

    private ActivityRecyclerviewListBinding binding;
    private ToolbarPrimaryBinding toolbarPrimaryBinding;
    private AnimalListAdapter adapter;
    private final ArrayList<Animal> list = new ArrayList<>();
    private RecyclerViewLoadMoreScroll scrollListener;
    private int offset = 0, limit = 10;
    private String oldOwnerId = "", newOwnerId = "", rfidTagNo = "", visualTagNo = "", historyType = "", date = "",
            pondStatus = "0", pondId = "", other = "", operationId = "", remark = "", ownerId = "", id = "", keyword = "";
    private LocalReceiver localReceiver;
    private final ArrayList<Pond> pondList = new ArrayList<>();
    private final ArrayList<CommonModel> operationList = new ArrayList<>();
    private final ArrayList<AnimalOwner> ownerArrayList = new ArrayList<>();
    private DialogTreatmentBinding bottomBinding;
    private BottomSheetDialog bottomSheetDialog;
    private DialogAnimaldeathBinding dialogAnimaldeathBinding;
    private StringBuilder scannedData = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecyclerviewListBinding.inflate(getLayoutInflater());
        toolbarPrimaryBinding = ToolbarPrimaryBinding.bind(binding.toolbar.getRoot());
        setContentView(binding.getRoot());

        context = AnimalListActivity.this;
        activity = this;

        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constant.refreshCount);
            localReceiver = new LocalReceiver();
            BroadCastManager.getInstance().registerReceiver(this, localReceiver, filter);//Registered broadcast recipient
        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbarPrimaryBinding.tvTitle.setText("Animal");
        if (getIntent() != null) {
            if (getIntent().hasExtra("details")) {
                AnimalOwner model = (AnimalOwner) getIntent().getSerializableExtra("details");
                id = model.getOwnerId();
            }
        }
        init();
        getList(false);
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

        binding.cvAdd.setOnClickListener(view -> goToActivityForResult(context, OwnerListActivity.class, (data, resultCode) -> {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    getList(false);
                }
            }
        }));

        adapter = new AnimalListAdapter(context, list, new ClickListener() {
            @Override
            public void onItemSelected(int position, int itemId) {
                if (!TextUtils.isEmpty(list.get(position).getOldOwnerId())) {
                    oldOwnerId = list.get(position).getOldOwnerId();
                    ownerId = list.get(position).getOwnerId();
                    rfidTagNo = list.get(position).getRfidTagNo();
                    visualTagNo = list.get(position).getVisualTagNo();
                    id = list.get(position).getAnimalId();
                }

                if (itemId == R.id.mAddAnimalDeath) {
                    showDeathDialog();
                } else if (itemId == R.id.mAnimalTransfer) {
                    showVaccinationDialog(3);
                } else if (itemId == R.id.mAnimalRetagging) {
                    showVaccinationDialog(2);
                } else if (itemId == R.id.mAnimalVaccination) {
                    showVaccinationDialog(1);
                } else if (itemId == R.id.mAnimalTreatment) {
                    showVaccinationDialog(0);
                } else if (itemId == R.id.mEdit) {
                    goToActivityForResult(new Intent(context, AddAnimalActivity.class).putExtra("details", list.get(position)), (data, resultCode) -> {
                        if (resultCode == Activity.RESULT_OK) {
                            if (data != null) {
                                if (data.hasExtra("details1")) {
                                    id = "";
                                    getList(false);
                                }
                            }
                        }
                    });
                } else if (itemId == R.id.mDelete) {
                    Common.confirmationDialog(context, getString(R.string.confirmation_remove), "No", "Yes", () -> deleteData(position));
                } else {
                    goToActivityForResult(new Intent(context, AnimalDetailsActivity.class).putExtra("details", list.get(position)), (data, resultCode) -> {
                        if (resultCode == Activity.RESULT_OK) {
                            if (data != null) {

                            }
                        }
                    });
                }

            }
        });

        binding.recyclerView.setAdapter(adapter);
        scrollListener = Common.bindLoadMoreRecyclerView(binding.recyclerView, 1, RecyclerView.VERTICAL, new ClickListener() {
            @Override
            public void onLoadListener() {
                getList(true);
            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    keyword = s.toString();
                    if (!s.toString().isEmpty()) {
                        binding.ivSearch.setVisibility(View.VISIBLE);
                        binding.ivClear.setVisibility(View.VISIBLE);
                    } else {
                        binding.ivSearch.setVisibility(View.GONE);
                        binding.ivClear.setVisibility(View.GONE);
                        getList(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.ivSearch.setOnClickListener(v -> {
            if (keyword.isEmpty()) {
                binding.etSearch.setError("Enter keyword");
            } else {
                getList(false);
            }
        });
        binding.ivClear.setOnClickListener(v -> binding.etSearch.getText().clear());

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            getList(false);
        });
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Received the process after broadcast
            if (intent.hasExtra(Constant.refreshCount)) {
                if (intent.getStringExtra(Constant.refreshCount).equals(Constant.refreshCount)) {
                    if (intent.hasExtra("tag")) {
                        String tag = intent.getStringExtra("tag");
                        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                            bottomBinding.etRFID.setText(tag);
                        }
                    }
                }
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
                        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                            bottomBinding.etRFID.setText(scannedData);
                        }
                    });
                    scannedData.setLength(0);
                }
            }
        }
        return super.dispatchKeyEvent(event);
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
            apiInterface.getList(Constant.animalListUrl, Common.getCustomerId(), id, keyword, String.valueOf(limit), String.valueOf(offset), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

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
                                binding.ivNotFound.setVisibility(View.GONE);
                            } else {
                                if (loadMore) {
                                    Common.showToast(apiResponse.getMessage());
                                } else {
                                    list.clear();
                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
                                    }
                                    binding.ivNotFound.setVisibility(View.VISIBLE);
                                    binding.recyclerView.setVisibility(View.GONE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t) {
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

    private void bindListData(APIResponse apiResponse) {
        if (apiResponse.getAnimalList() != null) {
            offset = offset + apiResponse.getAnimalList().size();
            list.addAll(apiResponse.getAnimalList());
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void deleteData(int position) {
        if (ConnectivityReceiver.isConnected(context)) {
            Common.showProgressDialog(context, context.getString(R.string.please_wait));

            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getDetails(Constant.removeAnimalUrl, list.get(position).getAnimalId(), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    Common.hideProgressDialog();

                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                list.remove(position);
                                if (adapter != null) adapter.notifyItemRemoved(position);
                                if (list.size() == 0) {
                                    binding.ivNotFound.setVisibility(View.VISIBLE);
                                    binding.recyclerView.setVisibility(View.GONE);
                                }
                                setResultOfActivity(1, false);
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
                    turnGPSOn(AnimalListActivity.this, permission1 -> callApiDeath(deathDate, disposedDate, deathCertificateNo, disposedReceipt)));
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
                                getList(false);
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
        bottomBinding.btnSubmit.setOnClickListener(v -> checkLocationPermissions(permission -> turnGPSOn(AnimalListActivity.this, permission1 -> validate(finalMessage))));

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
                    checkLocationPermissions(permission -> turnGPSOn(AnimalListActivity.this, permission1 -> addData())));
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

        if (apiResponse.getOperationList() != null) {
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
}