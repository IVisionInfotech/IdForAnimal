package com.idforanimal.activity.audit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.idforanimal.R;
import com.idforanimal.activity.BaseActivity;
import com.idforanimal.activity.OwnerListActivity;
import com.idforanimal.adapter.AuditAnimalListAdapter;
import com.idforanimal.bluetooth.Bluetooth;
import com.idforanimal.databinding.ActivityRecyclerviewListBinding;
import com.idforanimal.databinding.DialogAnimalauditBinding;
import com.idforanimal.databinding.DialogAnimaldeathBinding;
import com.idforanimal.databinding.DialogTreatmentBinding;
import com.idforanimal.databinding.ToolbarPrimaryBinding;
import com.idforanimal.loadmore.RecyclerViewLoadMoreScroll;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.Animal;
import com.idforanimal.model.AnimalOwner;
import com.idforanimal.model.CommonModel;
import com.idforanimal.model.Pond;
import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.utils.ClickListener;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuditAnimalListActivity extends BaseActivity {

    private ActivityRecyclerviewListBinding binding;
    private ToolbarPrimaryBinding toolbarPrimaryBinding;
    private AuditAnimalListAdapter adapter;
    private final ArrayList<Animal> list = new ArrayList<>();
    private RecyclerViewLoadMoreScroll scrollListener;
    private int offset = 0, limit = 10;
    private String id = "", keyword = "";
    private BottomSheetDialog bottomSheetDialog;
    private DialogAnimaldeathBinding dialogAnimaldeathBinding;
    private DialogAnimalauditBinding dialogAnimalauditBinding;
    private String filterType = "0";
    private String auditId = "0";
    private String[] auditOptions = {"None", "Count as audit", "Count as audit and death"};
    String auditStatus = "0";
    String missingStatus = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecyclerviewListBinding.inflate(getLayoutInflater());
        toolbarPrimaryBinding = ToolbarPrimaryBinding.bind(binding.toolbar.getRoot());
        setContentView(binding.getRoot());

        context = AuditAnimalListActivity.this;
        activity = this;

        filterType = getIntent().getStringExtra("id");
        auditId = getIntent().getStringExtra("title");
        toolbarPrimaryBinding.tvTitle.setText("Animal");

        init();
        getList(false);
        checkLocationPermissions(permission -> turnGPSOn(this, permission1 -> checkLocationSettings()));
    }

    private void init() {
        toolbarPrimaryBinding.ivBack.setOnClickListener(v -> finish());
        toolbarPrimaryBinding.ivBluetooth.setVisibility(View.GONE);
        binding.cvAdd.setVisibility(View.GONE);


        adapter = new AuditAnimalListAdapter(context, list, new ClickListener() {
            @Override
            public void onItemSelected(int position, int itemId) {
                if (!TextUtils.isEmpty(list.get(position).getOldOwnerId())) {
                    id = list.get(position).getAnimalId();
                }
                Animal animal = list.get(position);
                if (filterType.equals("2") || filterType.equals("3")) {
                    openDialog(animal);
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

    private void openDialog(Animal animal) {
        dialogAnimalauditBinding = DialogAnimalauditBinding.inflate(getLayoutInflater());
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        bottomSheetDialog.setContentView(dialogAnimalauditBinding.getRoot());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, auditOptions);
        dialogAnimalauditBinding.spnAudit.setAdapter(adapter);
        dialogAnimalauditBinding.spnAudit.setSelection(0);
        dialogAnimalauditBinding.spnAudit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                missingStatus = String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (filterType.equals("2")) {
            dialogAnimalauditBinding.spnAudit.setVisibility(View.VISIBLE);
            dialogAnimalauditBinding.tvSubTitle.setVisibility(View.VISIBLE);
        } else if (filterType.equals("3")) {
            dialogAnimalauditBinding.spnAudit.setVisibility(View.GONE);
            dialogAnimalauditBinding.tvSubTitle.setVisibility(View.GONE);
        }

        dialogAnimalauditBinding.btnSubmit.setOnClickListener(v -> {
            if (filterType.equals("2")) {
                auditStatus = "2";
            } else {
                auditStatus = "3";
                missingStatus = "0";
            }
            String remark = dialogAnimalauditBinding.etRemark.getText().toString().trim();

            checkLocationPermissions(permission ->
                    turnGPSOn(AuditAnimalListActivity.this, permission1 -> getDetails(auditStatus, missingStatus, remark, animal)));
        });
        bottomSheetDialog.show();
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
            apiInterface.getList(Constant.auditAnimalListUrl, Common.getCustomerId(), id, keyword, filterType, auditId, String.valueOf(limit), String.valueOf(offset), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {
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
                    turnGPSOn(AuditAnimalListActivity.this, permission1 -> callApiDeath(deathDate, disposedDate, deathCertificateNo, disposedReceipt)));
        });
        bottomSheetDialog.show();
    }

    private void getDetails(String auditStatus, String missingStatus, String remarks, Animal animal) {
        Common.hideProgressDialog();
        if (ConnectivityReceiver.isConnected(context)) {
            Common.showProgressDialog(context, getString(R.string.please_wait));
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getAuditDetails(Constant.getAnimalAuditDetails, animal.getRfidTagNo(), auditId, auditStatus, missingStatus, remarks, Common.getEmpId(), Common.getCustomerId(),String.valueOf(latitude), String.valueOf(longitude), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    try {
                        Common.hideProgressDialog();
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            }
                            bottomSheetDialog.dismiss();
                            if (missingStatus.equals("2")) {
                                showDeathDialog();
                            } else {
                                getList(false);
                            }

                            Common.showToast(apiResponse.getMessage());
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

    private void callApiSubmitRemark(String auditStatus, String remarks, Animal animal) {
        if (ConnectivityReceiver.isConnected(context)) {
            Common.showProgressDialog(context, context.getString(R.string.please_wait));
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.addAnimalAuditRemark(Constant.auditAnimalRemarkUrl, animal.getAuditAnimalId(), auditId, animal.getAnimalId(), animal.getRfidTagNo(), "1", auditStatus, remarks, Common.getCustomerId(), Common.getEmpId(),String.valueOf(latitude), String.valueOf(longitude), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {

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

}