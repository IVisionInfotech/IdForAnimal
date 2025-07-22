package com.idforanimal.dialog;


import static com.idforanimal.utils.Common.loadImageAndSetOnClickListener;
import static com.idforanimal.utils.Common.setVisibilityAndText;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.R;
import com.idforanimal.databinding.FragmentAnimalDetailBinding;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.Animal;
import com.idforanimal.model.AnimalOwner;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentAnimalDetail extends DialogFragment {

    private Context context;
    private Activity activity;
    private FragmentAnimalDetailBinding binding;
    private String ownerId = "", status = "", tag = "";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @Override
    public int getTheme() {
        return R.style.FullScreenDialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnimalDetailBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            Bundle mArgs = getArguments();
            status = mArgs.getString("status");
            tag = mArgs.getString("tag");
            ownerId = mArgs.getString("ownerId");
        }
        initView();
        return binding.getRoot();
    }

    private void initView() {
        binding.ivBack.setOnClickListener(v -> dismiss());
        getDetail();
        getOwnerDetails();
    }

    private void getDetail() {
         if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getDetails(Constant.scanAnimalDetailsUrl, Common.getCustomerId(), "", tag, status, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                    Common.hideProgressDialog();
                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                Animal model = apiResponse.getAnimal();
                                bindData(model);
                            } else if (apiResponse.getStatus() == 0) {
                                if (Objects.equals(status, "1")) {
                                    binding.tvRFID.setText(tag);
                                } else if (Objects.equals(status, "2")) {
                                    binding.txtVisualNo.setText(tag);
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
                    Common.hideProgressDialog();
                }
            });
        } else {
            Common.noInternetDialog(requireActivity());
        }
    }

    private void getOwnerDetails() {
         if (ConnectivityReceiver.isConnected(context)) {
            Common.showProgressDialog(context, getString(R.string.please_wait));

            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getDetails(Constant.animalOwnerDetailsUrl, ownerId, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {

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
            Common.noInternetDialog(requireActivity());
        }
    }


    private void bindData(Animal model) {
        if (model != null) {

            if (!model.getImage().equals("")) {
                Common.loadImage(binding.ivImage, R.drawable.animal_bg, model.getImage());
                binding.ivImage.setOnClickListener(v -> {
                    Common.openImagePreview(context, model.getImage());
                });
            }

            if (!TextUtils.isEmpty(model.getRfidTagNo()) || Objects.equals(status, "1")) {
                binding.tvRFID.setText(!model.getRfidTagNo().isEmpty() ? model.getRfidTagNo() : tag);
                binding.tvRFID.setVisibility(View.VISIBLE);
            } else binding.tvRFID.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(model.getVisualTagNo()) || Objects.equals(status, "2")) {
                binding.txtVisualNo.setVisibility(View.VISIBLE);
                binding.txtVisualNo.setText(!model.getVisualTagNo().isEmpty() ? model.getVisualTagNo() : tag);
            } else binding.txtVisualNo.setVisibility(View.GONE);

            setVisibilityAndText(binding.llType, binding.tvType, Common.buildText(model.getAnimalName(), model.getCattleName()));
            setVisibilityAndText(binding.llBreed, binding.tvBreed, Common.buildText(model.getBreedName(), model.getColorName()));
            setVisibilityAndText(binding.llTail, binding.tvTail, Common.buildText(model.getTailName(), model.getHornName()));
            setVisibilityAndText(binding.llAge, binding.tvDOB, Common.buildText(Common.changeDateFormat(model.getDob()), model.getAge()), " Month");
            setVisibilityAndText(binding.llGrith, binding.tvGirth, Common.buildText(model.getGirth(), model.getLength()));
            setVisibilityAndText(binding.llMilk, binding.tvMilk, Common.buildText(model.getMilperday(), model.getMilktype()));
            setVisibilityAndText(binding.llStatus, binding.tvStatus, model.getPregStatus());
            setVisibilityAndText(binding.llVisualSign, binding.tvSign, model.getVisualSign());
            setVisibilityAndText(binding.llLactation, binding.tvLactation, model.getLactation());
            setVisibilityAndText(binding.llRemark, binding.tvRemarks, model.getRemark());
            setVisibilityAndText(binding.llTagging, binding.tvTaggingDate, Common.changeDateFormat(model.getTaggingDate()));
            setVisibilityAndText(binding.llValidDate, binding.tvValidDate, Common.changeDateFormat(model.getValidDate()));
            setVisibilityAndText(binding.llOwnerValidDate, binding.tvOwnerValidDate, Common.changeDateFormat(model.getOwnerValidDate()));
        }
    }

    private void bindData(APIResponse apiResponse) {
        if (apiResponse.getAnimalOwnerDetails() != null) {
            AnimalOwner model = apiResponse.getAnimalOwnerDetails();
            binding.tvName.setText(model.getFirstName() + " " + model.getMiddleName() + " " + model.getLastName());
            binding.tvZoneName.setText(model.getZoneName());
            binding.tvWardName.setText(model.getWardName());

            setVisibilityAndText(binding.llContact, binding.tvContact, model.getContact());
            setVisibilityAndText(binding.llContact2, binding.tvContact2, model.getContact1());
            setVisibilityAndText(binding.llEmail, binding.tvEmail, model.getEmailId());
            setVisibilityAndText(binding.llRegister, binding.tvRegistrationNo, model.getRegistrationNo());
            setVisibilityAndText(binding.llRegisterDate, binding.tvRegisterDate, Common.changeDateFormat(model.getRegistrationDate()));
            setVisibilityAndText(binding.llAadhar, binding.tvAadharNo, model.getAadharNo());
            setVisibilityAndText(binding.llLicense, binding.tvLicenseNo, model.getDlNo());
            setVisibilityAndText(binding.llElection, binding.tvElectionNo, model.getElectionCardNo());
            setVisibilityAndText(binding.llGender, binding.tvGender, model.getGender());
            setVisibilityAndText(binding.llHouse, binding.tvHouseNo, model.getHouseNo());
            setVisibilityAndText(binding.llStreet, binding.tvStreet, model.getStreet());
            setVisibilityAndText(binding.llLandmark, binding.tvLandmark, model.getLandmark());
            setVisibilityAndText(binding.llArea, binding.tvArea, model.getArea());
            setVisibilityAndText(binding.llState, binding.tvStateCountry, Common.buildText(model.getStateName(), model.getCountryName()));
            setVisibilityAndText(binding.llCity, binding.tvCityDistrict, Common.buildText(model.getCityName(), model.getDistrictName()));
            setVisibilityAndText(binding.llPinCode, binding.tvPinCode, model.getPincode());
            setVisibilityAndText(binding.llTenement, binding.tvTenementNo, model.getTenementNo());
            setVisibilityAndText(binding.llAnimalPlace, binding.tvPlaceArea, model.getPlaceArea());
            setVisibilityAndText(binding.llPlaceOwned, binding.tvOwnedBy, model.getPlaceOwned());
            setVisibilityAndText(binding.llShed, binding.tvShed, model.getShedAvailable());
            setVisibilityAndText(binding.llStorage, binding.tvStorageAvailable, model.getStorageAvailable());
            setVisibilityAndText(binding.llDrinking, binding.tvWaterFacility, model.getDrinkingWater());
            setVisibilityAndText(binding.llDisposal, binding.tvDisposalFacility, model.getDisposalFacility());
            setVisibilityAndText(binding.llOwnerRemarks, binding.tvOwenerRemarks, model.getRemarks());


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

}
