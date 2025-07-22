package com.idforanimal.activity.audit;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.idforanimal.R;
import com.idforanimal.activity.AddAnimalToNgoActivity;
import com.idforanimal.activity.BaseActivity;
import com.idforanimal.databinding.ActivityAddAuditBinding;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.AnimalOwner;
import com.idforanimal.model.AuditViewModel;
import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAuditActivity extends BaseActivity {

    private ActivityAddAuditBinding binding;
    private AuditViewModel model;
    private String id, startDate, endDate, auditbyInstitution, auditbyPerson, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAuditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = AddAuditActivity.this;
        activity = this;

        setToolbar("Add Audit");
        if (getIntent() != null) {
            if (getIntent().hasExtra("id")) {
                model = new Gson().fromJson(getIntent().getStringExtra("id"), AuditViewModel.class);
                if (model != null) {
                    setToolbar("Update Audit");
                    id = model.getAuditId();
                    if (model.isVisibleDeleted()) {
                        binding.tvStartDate.setEnabled(false);
                        binding.tvEndDate.setEnabled(false);
                        binding.etAuditInstitutionName.setEnabled(false);
                        binding.etAuditPersonName.setEnabled(false);
                        binding.rdOpen.setEnabled(false);
                    } else {
                        binding.tvStartDate.setEnabled(true);
                        binding.tvEndDate.setEnabled(true);
                        binding.etAuditInstitutionName.setEnabled(true);
                        binding.etAuditPersonName.setEnabled(true);
                        binding.rdOpen.setEnabled(true);
                    }
                    binding.tvStartDate.setText(Common.changeDateFormat(model.getStartDate()));
                    startDate = model.getStartDate();
                    binding.tvEndDate.setText(Common.changeDateFormat(model.getEndDate()));
                    binding.etAuditInstitutionName.setText(model.getAuditByInstitution());
                    binding.etAuditPersonName.setText(model.getAuditByPerson());
                    if (model.getStatus().equals("0")) {
                        binding.rdOpen.setChecked(true);
                    } else {
                        binding.rdClose.setChecked(true);
                    }
                }

            }
        }
        init();
    }

    private void init() {
        if (model == null) {
            binding.tvStartDate.setText(Common.getCurrentDate());
            startDate = Common.changeDateFormat(Common.getCurrentDate(), "dd-MM-yyyy", "yyyy-MM-dd");
        }
        binding.tvStartDate.setOnClickListener(v -> Common.showMinDatePicker(context, dt -> {
            startDate = dt;
            binding.tvStartDate.setText(Common.changeDateFormat(dt));
        }));

        binding.tvEndDate.setOnClickListener(v -> Common.showMinDatePicker(context, startDate, dt -> {
            endDate = dt;
            binding.tvEndDate.setText(Common.changeDateFormat(dt));
        }));

        binding.cvNext.setOnClickListener(v -> validate());
    }

    private void validate() {
        startDate = binding.tvStartDate.getText().toString().trim();
        endDate = binding.tvEndDate.getText().toString().trim();
        auditbyInstitution = binding.etAuditInstitutionName.getText().toString().trim();
        auditbyPerson = binding.etAuditPersonName.getText().toString().trim();
        if (binding.rdOpen.isChecked()) {
            status = "0";
        } else {
            status = "1";
        }

        if (startDate.isEmpty()) {
            binding.tvStartDate.setError("Please select start date");
        } else if (endDate.isEmpty()) {
            binding.tvEndDate.setError("Please select end date");
        } else if (auditbyInstitution.isEmpty()) {
            binding.etAuditInstitutionName.setError("Please enter audit institution name");
        } else if (auditbyPerson.isEmpty()) {
            binding.etAuditPersonName.setError("Please enter audit person name");
        } else {
            if (status.equals("1")) {
                if (Common.getType().equals(Constant.loginTypeCustomer) ){
                    Common.confirmationDialog(activity, "Warning", " You will not be able to make any changes after closing the Audit Process. \n\nAre you sure you want to close the Audit Process?", "No", "Yes", this::addData);
                }else{
                    Common.showToast("You can't close the Audit Process.");
                }
            } else {
                addData();
            }
        }

    }

    private void addData() {
        if (ConnectivityReceiver.isConnected(context)) {
            Common.showProgressDialog(context, context.getString(R.string.please_wait));
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.addEditAudit(Constant.addEditAudit, id, startDate, endDate, auditbyInstitution, auditbyPerson, status, Common.getCustomerId(), Common.getEmpId(), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {
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
}