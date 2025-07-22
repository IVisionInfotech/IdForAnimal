package com.idforanimal.activity;

import static com.idforanimal.utils.Common.loadImageAndSetOnClickListener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.R;
import com.idforanimal.databinding.ActivityAnimalOwnerDetailsBinding;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.AnimalOwner;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimalOwnerDetailsActivity extends BaseActivity {

    private ActivityAnimalOwnerDetailsBinding binding;
    private String id = "";
    private Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimalOwnerDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = AnimalOwnerDetailsActivity.this;

        setToolbar("Animal Owner Details");

        bindData(getIntent());
        checkLocationPermissions(permission -> turnGPSOn(this, permission1 -> checkLocationSettings()));
    }

    private void bindData(Intent intent) {
        if (intent != null) {
            if (getIntent().hasExtra("details")) {
                AnimalOwner model = (AnimalOwner) getIntent().getSerializableExtra("details");
                id = model.getOwnerId();
                getDetails();

                binding.rlViewAnimal.setOnClickListener(v -> goToActivityForResult(new Intent(context, AnimalListActivity.class).putExtra("details", model), (data, resultCode) -> {
                    if (resultCode == Activity.RESULT_OK) {
                        flag = 1;
                        getDetails();
                    }
                }));

                binding.rlReleaseAnimal.setOnClickListener(v -> goToActivityForResult(new Intent(context, ReleaseAnimalListActivity.class).putExtra("details", model), (data, resultCode) -> {
                    if (resultCode == Activity.RESULT_OK) {
                        flag = 1;
                        getDetails();
                    }
                }));
            }
        }
    }

    private void getDetails() {
         if (ConnectivityReceiver.isConnected(context)) {
            Common.showProgressDialog(context, getString(R.string.please_wait));

            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getDetails(Constant.animalOwnerDetailsUrl, id, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {

                @Override
                public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                    Common.hideProgressDialog();
                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            Common.showToast(apiResponse.getMessage());
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                bindData(apiResponse);
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

    private void bindData(APIResponse apiResponse) {
        if (apiResponse.getAnimalOwnerDetails() != null) {

            AnimalOwner model = apiResponse.getAnimalOwnerDetails();

            if (flag == 1) {
                intent.putExtra("details", model);
                setResultOfActivity(intent, 1, false);
                flag = 0;
            }

            binding.tvName.setText(model.getFirstName() + " " + model.getMiddleName() + " " + model.getLastName());
            binding.tvZoneName.setText(model.getZoneName());
            binding.tvWardName.setText(model.getWardName());
            binding.tvLiveAnimal.setText(model.getLiveAnimalCount());
            binding.tvDeathAnimal.setText(model.getDeathAnimalCount());

            binding.tvContact.setText(model.getContact());
            binding.tvContact2.setText(model.getContact1());
            binding.tvEmail.setText(model.getEmailId());
            binding.tvRegistrationNo.setText(model.getRegistrationNo());
            binding.tvRegisterDate.setText(Common.changeDateFormat(model.getRegistrationDate()));
            binding.tvAadharNo.setText(model.getAadharNo());
            binding.tvLicenseNo.setText(model.getDlNo());
            binding.tvGender.setText(model.getGender());
            binding.tvHouseNo.setText(model.getHouseNo());
            binding.tvStreet.setText(model.getStreet());
            binding.tvLandmark.setText(model.getLandmark());
            binding.tvArea.setText(model.getArea());
            binding.tvStateCountry.setText(model.getStateName() + " - " + model.getCountryName());
            binding.tvCityDistrict.setText(model.getCityName() + " - " + model.getDistrictName());
            binding.tvPinCode.setText(model.getPincode());
            binding.tvTenementNo.setText(model.getTenementNo());
            binding.tvPlaceArea.setText(model.getPlaceArea());
            binding.tvOwnedBy.setText(model.getPlaceOwned());
            binding.tvShed.setText(model.getShedAvailable());
            binding.tvStorageAvailable.setText(model.getStorageAvailable());
            binding.tvWaterFacility.setText(model.getDrinkingWater());
            binding.tvDisposalFacility.setText(model.getDisposalFacility());
            binding.tvRemarks.setText(model.getRemarks());

            loadImageAndSetOnClickListener(context, binding.ivAadharFront, model.getAadharFront());
            loadImageAndSetOnClickListener(context, binding.ivAadharBack, model.getAadharBack());
            loadImageAndSetOnClickListener(context, binding.ivElectionFront, model.getElectionFront());
            loadImageAndSetOnClickListener(context, binding.ivElectionBack, model.getElectionBack());
            loadImageAndSetOnClickListener(context, binding.ivLicenseFront, model.getDlFront());
            loadImageAndSetOnClickListener(context, binding.ivLicenseBack, model.getDlBack());
            loadImageAndSetOnClickListener(context, binding.ivPhotoNo, model.getPhotoNo());
            loadImageAndSetOnClickListener(context, binding.ivManualForm, model.getFormDocument());
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
}