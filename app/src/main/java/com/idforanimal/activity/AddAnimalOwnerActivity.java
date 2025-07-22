package com.idforanimal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;

import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.R;
import com.idforanimal.databinding.ActivityAddAnimalOwnerBinding;
import com.idforanimal.databinding.LayoutAddAnimalOwnerStepOneBinding;
import com.idforanimal.databinding.LayoutAddAnimalOwnerStepThreeBinding;
import com.idforanimal.databinding.LayoutAddAnimalOwnerStepTwoBinding;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.AnimalOwner;
import com.idforanimal.model.CommonModel;
import com.idforanimal.model.Pond;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAnimalOwnerActivity extends BaseActivity {

    private ActivityAddAnimalOwnerBinding binding;
    private LayoutAddAnimalOwnerStepOneBinding layoutBinding1;
    private LayoutAddAnimalOwnerStepTwoBinding layoutBinding2;
    private LayoutAddAnimalOwnerStepThreeBinding layoutBinding3;
    private String id = "", firstName = "", middleName = "", lastName = "", date = "", validDate = "", houseNo = "", street = "", country = "", stateId = "", cityId = "", pinCode = "", aadharNo = "", electionNo = "", licenseNo = "", aadharImage1 = "", aadharImage2 = "", electionImage1 = "", electionImage2 = "", licenseImage1 = "", licenseImage2 = "", registerNo = "", gender = "", landmark = "", area = "", district = "", tenamentNo = "", zoneId = "", wardId = "", contact = "", contact2 = "", email = "", animalPlace = "", ownedBy = "", shed = "", storage = "", waterFacility = "", disposalFacility = "", image = "", remarks = "", formImage = "", pondID = "", pondStatus = "0";
    private String fileAadharImage1 = "", fileAadharImage2 = "", fileElectionImage1 = "", fileElectionImage2 = "", fileLicenseImage1 = "", fileLicenseImage2 = "", fileImage = "", fileFormImage = "";
    private ArrayList<CommonModel> stateList = new ArrayList<>();
    private ArrayList<CommonModel> cityList = new ArrayList<>();
    private ArrayList<CommonModel> zoneList = new ArrayList<>();
    private ArrayList<CommonModel> wardList = new ArrayList<>();
    private ArrayList<Pond> pondList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAnimalOwnerBinding.inflate(getLayoutInflater());
        layoutBinding1 = LayoutAddAnimalOwnerStepOneBinding.bind(binding.layout1.getRoot());
        layoutBinding2 = LayoutAddAnimalOwnerStepTwoBinding.bind(binding.layout2.getRoot());
        layoutBinding3 = LayoutAddAnimalOwnerStepThreeBinding.bind(binding.layout3.getRoot());
        setContentView(binding.getRoot());

        context = this;
        activity = this;
        if (getIntent() != null && getIntent().hasExtra("details")) {
            AnimalOwner model = (AnimalOwner) getIntent().getSerializableExtra("details");
            id = model.getOwnerId();
            getDetails();
            setToolbar("Update Animal Owner");
            layoutBinding3.tvSubmit.setText("Update");
        } else {
            setToolbar("Add Animal Owner");
            layoutBinding3.tvSubmit.setText("Add");
        }

        init();
        checkLocationPermissions(permission -> turnGPSOn(this, permission1 -> checkLocationSettings()));
    }

    private void init() {
        binding.llStep1.setOnClickListener(v -> {
            binding.tvStep1.setTextColor(getResources().getColor(R.color.darkBlack));
            binding.view1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            binding.tvStep2.setTextColor(getResources().getColor(R.color.textHintColor));
            binding.view2.setBackgroundColor(getResources().getColor(R.color.viewBackground));
            binding.tvStep3.setTextColor(getResources().getColor(R.color.textHintColor));
            binding.view3.setBackgroundColor(getResources().getColor(R.color.viewBackground));
            bindView("1");
        });
        binding.llStep2.setOnClickListener(v -> {
            binding.tvStep1.setTextColor(getResources().getColor(R.color.textHintColor));
            binding.view1.setBackgroundColor(getResources().getColor(R.color.viewBackground));
            binding.tvStep2.setTextColor(getResources().getColor(R.color.darkBlack));
            binding.view2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            binding.tvStep3.setTextColor(getResources().getColor(R.color.textHintColor));
            binding.view3.setBackgroundColor(getResources().getColor(R.color.viewBackground));
            bindView("2");
        });
        binding.llStep3.setOnClickListener(v -> {
            binding.tvStep1.setTextColor(getResources().getColor(R.color.textHintColor));
            binding.view1.setBackgroundColor(getResources().getColor(R.color.viewBackground));
            binding.tvStep2.setTextColor(getResources().getColor(R.color.textHintColor));
            binding.view2.setBackgroundColor(getResources().getColor(R.color.viewBackground));
            binding.tvStep3.setTextColor(getResources().getColor(R.color.darkBlack));
            binding.view3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            bindView("3");
        });
        binding.llStep1.performClick();
        layoutBinding1.cvNext.setOnClickListener(v -> binding.llStep2.performClick());
        layoutBinding2.cvPrevious.setOnClickListener(v -> binding.llStep1.performClick());
        layoutBinding2.cvNext.setOnClickListener(v -> binding.llStep3.performClick());
        layoutBinding3.cvPrevious.setOnClickListener(v -> binding.llStep2.performClick());

        layoutBinding1.tvDate.setOnClickListener(v -> Common.showDatePicker(context, dt -> {
            date = dt;
            layoutBinding1.tvDate.setText(Common.changeDateFormat(date));
        }));

        layoutBinding1.tvValidDate.setOnClickListener(v -> Common.showMinDatePicker(context, dt -> {
            validDate = dt;
            layoutBinding1.tvValidDate.setText(Common.changeDateFormat(validDate));
        }));

        layoutBinding2.ivAadharImage1.setOnClickListener(v ->
                checkPermissions(getStoragePermissionList(), permission -> selectImage((ImagePicker) (file, bitmap) -> {
                    layoutBinding2.ivAadharImage1.setImageBitmap(bitmap);
                    fileAadharImage1 = file.getName();
                    aadharImage1 = file.getPath();
                })));
        layoutBinding2.ivAadharImage2.setOnClickListener(v ->
                checkPermissions(getStoragePermissionList(), permission -> selectImage((file, bitmap) -> {
                    layoutBinding2.ivAadharImage2.setImageBitmap(bitmap);
                    aadharImage2 = file.getPath();
                    fileAadharImage2 = file.getName();
                })));
        layoutBinding2.ivElectionImage1.setOnClickListener(v ->
                checkPermissions(getStoragePermissionList(), permission -> selectImage((file, bitmap) -> {
                    layoutBinding2.ivElectionImage1.setImageBitmap(bitmap);
                    electionImage1 = file.getPath();
                    fileElectionImage1 = file.getName();
                })));
        layoutBinding2.ivElectionImage2.setOnClickListener(v ->
                checkPermissions(getStoragePermissionList(), permission -> selectImage((file, bitmap) -> {
                    layoutBinding2.ivElectionImage2.setImageBitmap(bitmap);
                    electionImage2 = file.getPath();
                    fileElectionImage2 = file.getName();
                })));
        layoutBinding2.ivDrivingImage1.setOnClickListener(v -> checkPermissions(getStoragePermissionList(), permission -> selectImage((file, bitmap) -> {
            licenseImage1 = file.getPath();
            layoutBinding2.ivDrivingImage1.setImageBitmap(bitmap);
            fileLicenseImage1 = file.getName();
        })));
        layoutBinding2.ivDrivingImage2.setOnClickListener(v -> checkPermissions(getStoragePermissionList(), permission -> selectImage((file, bitmap) -> {
            layoutBinding2.ivDrivingImage2.setImageBitmap(bitmap);
            licenseImage2 = file.getPath();
            fileLicenseImage2 = file.getName();
        })));
        layoutBinding3.ivImage.setOnClickListener(v ->
                checkPermissions(getStoragePermissionList(), permission -> selectImage((file, bitmap) -> {
                    layoutBinding3.ivImage.setImageBitmap(bitmap);
                    layoutBinding3.ivImage.setImageBitmap(bitmap);
                    image = file.getPath();
                    fileImage = file.getName();
                })));
        layoutBinding3.ivManualForm.setOnClickListener(v -> checkPermissions(getStoragePermissionList(), permission -> selectImage((file, bitmap) -> {
            layoutBinding3.ivManualForm.setImageBitmap(bitmap);
            formImage = file.getPath();
            fileFormImage = file.getName();
        })));

        layoutBinding3.rdgGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (layoutBinding3.rdoMale.isChecked()) {
                gender = "MALE";
            } else if (layoutBinding3.rdoFemale.isChecked()) {
                gender = "FEMALE";
            } else if (layoutBinding3.rdoOther.isChecked()) {
                gender = "OTHER";
            }
        });
        layoutBinding3.rdgAnimalPlace.setOnCheckedChangeListener((group, checkedId) -> {
            if (layoutBinding3.rdoAnimalPlaceYes.isChecked()) {
                animalPlace = "YES";
            } else if (layoutBinding3.rdoAnimalPlaceNo.isChecked()) {
                animalPlace = "No";
            }
        });
        layoutBinding3.rdgOwnedBy.setOnCheckedChangeListener((group, checkedId) -> {
            if (layoutBinding3.rdoOwnedBySelf.isChecked()) {
                ownedBy = "SELF";
            } else if (layoutBinding3.rdoOwnedByOther.isChecked()) {
                ownedBy = "OTHER";
            }
        });
        layoutBinding3.rdgShed.setOnCheckedChangeListener((group, checkedId) -> {
            if (layoutBinding3.rdoShedYes.isChecked()) {
                shed = "YES";
            } else if (layoutBinding3.rdoShedNo.isChecked()) {
                shed = "No";
            }
        });
        layoutBinding3.rdgStorage.setOnCheckedChangeListener((group, checkedId) -> {
            if (layoutBinding3.rdoStorageYes.isChecked()) {
                storage = "YES";
            } else if (layoutBinding3.rdoStorageNo.isChecked()) {
                storage = "No";
            }
        });
        layoutBinding3.rdgWaterFacility.setOnCheckedChangeListener((group, checkedId) -> {
            if (layoutBinding3.rdoWaterFacilityYes.isChecked()) {
                waterFacility = "YES";
            } else if (layoutBinding3.rdoWaterFacilityNo.isChecked()) {
                waterFacility = "No";
            }
        });
        layoutBinding3.rdgDisposalFacility.setOnCheckedChangeListener((group, checkedId) -> {
            if (layoutBinding3.rdoDisposalFacilityYes.isChecked()) {
                disposalFacility = "YES";
            } else if (layoutBinding3.rdoDisposalFacilityNo.isChecked()) {
                disposalFacility = "No";
            }
        });
        layoutBinding3.rdgSourceOfIdentify.setOnCheckedChangeListener((group, checkedId) -> {
            if (layoutBinding3.rdOnField.isChecked()) {
                layoutBinding3.spinnerCattle.setVisibility(View.GONE);
                pondStatus = "0";
            } else if (layoutBinding3.rdCattlePond.isChecked()) {
                layoutBinding3.spinnerCattle.setVisibility(View.VISIBLE);
                pondStatus = "1";
            }
        });

        layoutBinding3.cvNext.setOnClickListener(v -> checkLocationPermissions(permission -> turnGPSOn(this, permission1 -> validate())));
    }

    private void bindView(String pages) {
        switch (pages) {
            case "1":
                binding.layout1.getRoot().setVisibility(View.VISIBLE);
                binding.layout2.getRoot().setVisibility(View.GONE);
                binding.layout3.getRoot().setVisibility(View.GONE);
                if (stateList.isEmpty()) getStateList();
                break;
            case "2":
                binding.layout1.getRoot().setVisibility(View.GONE);
                binding.layout2.getRoot().setVisibility(View.VISIBLE);
                binding.layout3.getRoot().setVisibility(View.GONE);
                break;
            case "3":
                binding.layout1.getRoot().setVisibility(View.GONE);
                binding.layout2.getRoot().setVisibility(View.GONE);
                binding.layout3.getRoot().setVisibility(View.VISIBLE);
                if (zoneList.isEmpty()) getZoneList();
                if (pondList.isEmpty()) getCattlePondList();
                break;
        }
    }

    private void bindData(AnimalOwner model) {
        if (model != null) {
            layoutBinding1.etFirstName.setText(model.getFirstName());
            layoutBinding1.etMiddleName.setText(model.getMiddleName());
            layoutBinding1.etLastName.setText(model.getLastName());
            layoutBinding1.etHouseNo.setText(model.getHouseNo());
            layoutBinding1.etStreet.setText(model.getStreet());
            layoutBinding1.etCountry.setText(model.getCountryName());
            layoutBinding1.etPinCode.setText(model.getPincode());
            date = model.getRegistrationDate();
            layoutBinding1.tvDate.setText(Common.changeDateFormat(date));
            if (!model.getValidDate().equals("0000-00-00")) {
                validDate = model.getValidDate();
                layoutBinding1.tvValidDate.setText(Common.changeDateFormat(validDate));
            }

            stateId = model.getStateId();
            flag1 = 1;
            cityId = model.getCityId();
            flag2 = 1;

            layoutBinding2.etAadharNo.setText(model.getAadharNo());
            layoutBinding2.etElectionCardNo.setText(model.getElectionCardNo());
            layoutBinding2.etDrivingLicenseNo.setText(model.getDlNo());

            if (!TextUtils.isEmpty(model.getAadharFront()))
                Common.loadImage(context, layoutBinding2.ivAadharImage1, model.getAadharFront());
            if (!TextUtils.isEmpty(model.getAadharBack()))
                Common.loadImage(context, layoutBinding2.ivAadharImage2, model.getAadharBack());
            if (!TextUtils.isEmpty(model.getElectionFront()))
                Common.loadImage(context, layoutBinding2.ivElectionImage1, model.getElectionFront());
            if (!TextUtils.isEmpty(model.getElectionBack()))
                Common.loadImage(context, layoutBinding2.ivElectionImage2, model.getElectionBack());
            if (!TextUtils.isEmpty(model.getDlFront()))
                Common.loadImage(context, layoutBinding2.ivDrivingImage1, model.getDlFront());
            if (!TextUtils.isEmpty(model.getDlBack()))
                Common.loadImage(context, layoutBinding2.ivDrivingImage2, model.getDlBack());


            layoutBinding3.etRegistrationNo.setText(model.getRegistrationNo());
            layoutBinding3.etLandmark.setText(model.getLandmark());
            layoutBinding3.etArea.setText(model.getArea());
            layoutBinding3.etDistrict.setText(model.getDistrictName());
            layoutBinding3.etTenamentNo.setText(model.getTenementNo());
            layoutBinding3.etContact.setText(model.getContact());
            layoutBinding3.etAlternativeContact.setText(model.getContact1());
            layoutBinding3.etEmail.setText(model.getEmailId());
            layoutBinding3.etRemarks.setText(model.getRemarks());
            zoneId = model.getZoneId();
            flag3 = 1;
            wardId = model.getWardId();
            flag4 = 1;
            if (model.getGender() != null) {
                if (model.getGender().equals("MALE")) {
                    layoutBinding3.rdoMale.setChecked(true);
                }
                if (model.getGender().equals("FEMALE")) {
                    layoutBinding3.rdoFemale.setChecked(true);
                }
                if (model.getGender().equals("OTHER")) {
                    layoutBinding3.rdoOther.setChecked(true);
                }
            }
            if (model.getPondStatus() != null) {
                if (model.getPondStatus().equals("1")) {
                    layoutBinding3.rdCattlePond.setChecked(true);
                    pondID = model.getPondId();
                } else layoutBinding3.rdOnField.setChecked(true);
            } else layoutBinding3.rdOnField.setChecked(true);

            if (model.getPlaceArea() != null) {
                if (model.getPlaceArea().equals("YES")) {
                    layoutBinding3.rdoAnimalPlaceYes.setChecked(true);
                }
                if (model.getPlaceArea().equals("NO")) {
                    layoutBinding3.rdoAnimalPlaceNo.setChecked(true);
                }
            }
            if (model.getPlaceOwned() != null) {
                if (model.getPlaceOwned().equals("SELF")) {
                    layoutBinding3.rdoOwnedBySelf.setChecked(true);
                }
                if (model.getPlaceOwned().equals("OTHER")) {
                    layoutBinding3.rdoOwnedBySelf.setChecked(true);
                }
            }
            if (model.getShedAvailable() != null) {
                if (model.getShedAvailable().equals("YES")) {
                    layoutBinding3.rdoShedYes.setChecked(true);
                }
                if (model.getShedAvailable().equals("NO")) {
                    layoutBinding3.rdoShedNo.setChecked(true);
                }
            }
            if (model.getStorageAvailable() != null) {
                if (model.getStorageAvailable().equals("YES")) {
                    layoutBinding3.rdoStorageYes.setChecked(true);
                }
                if (model.getStorageAvailable().equals("NO")) {
                    layoutBinding3.rdoStorageNo.setChecked(true);
                }
            }
            if (model.getDrinkingWater() != null) {
                if (model.getDrinkingWater().equals("YES")) {
                    layoutBinding3.rdoWaterFacilityYes.setChecked(true);
                }
                if (model.getDrinkingWater().equals("NO")) {
                    layoutBinding3.rdoWaterFacilityNo.setChecked(true);
                }
            }
            if (model.getDisposalFacility() != null) {
                if (model.getDisposalFacility().equals("YES")) {
                    layoutBinding3.rdoDisposalFacilityYes.setChecked(true);
                }
                if (model.getDisposalFacility().equals("NO")) {
                    layoutBinding3.rdoDisposalFacilityNo.setChecked(true);
                }
            }
            if (TextUtils.isEmpty(model.getPhotoNo()))
                Common.loadImage(context, layoutBinding3.ivImage, model.getPhotoNo());
            if (model.getFormDocument() != null)
                Common.loadImage(context, layoutBinding3.ivManualForm, model.getFormDocument());
        }
    }

    private void getDetails() {
        if (ConnectivityReceiver.isConnected(context)) {
            Common.showProgressDialog(context, context.getString(R.string.please_wait));

            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getDetails(Constant.animalOwnerDetailsUrl, id, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

                @Override
                public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                    Common.hideProgressDialog();
                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                if (apiResponse.getAnimalOwnerDetails() != null) {
                                    bindData(apiResponse.getAnimalOwnerDetails());
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
                public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    Common.hideProgressDialog();
                }
            });
        } else {
            Common.noInternetDialog(this);
        }
    }

    private void validate() {

        firstName = layoutBinding1.etFirstName.getText().toString().trim();
        middleName = layoutBinding1.etMiddleName.getText().toString().trim();
        lastName = layoutBinding1.etLastName.getText().toString().trim();
        houseNo = layoutBinding1.etHouseNo.getText().toString().trim();
        street = layoutBinding1.etStreet.getText().toString().trim();
        country = layoutBinding1.etCountry.getText().toString().trim();
        pinCode = layoutBinding1.etPinCode.getText().toString().trim();

        aadharNo = layoutBinding2.etAadharNo.getText().toString().trim();
        electionNo = layoutBinding2.etElectionCardNo.getText().toString().trim();
        licenseNo = layoutBinding2.etDrivingLicenseNo.getText().toString().trim();

        registerNo = layoutBinding3.etRegistrationNo.getText().toString().trim();
        landmark = layoutBinding3.etLandmark.getText().toString().trim();
        area = layoutBinding3.etArea.getText().toString().trim();
        district = layoutBinding3.etDistrict.getText().toString().trim();
        tenamentNo = layoutBinding3.etTenamentNo.getText().toString().trim();
        contact = layoutBinding3.etContact.getText().toString().trim();
        contact2 = layoutBinding3.etAlternativeContact.getText().toString().trim();
        email = layoutBinding3.etEmail.getText().toString().trim();
        remarks = layoutBinding3.etRemarks.getText().toString().trim();

        if (firstName.equals("")) {
            layoutBinding1.etFirstName.setError("Enter first name");
            binding.llStep1.performClick();
        } else if (lastName.equals("")) {
            layoutBinding1.etLastName.setError("Enter last name");
            binding.llStep1.performClick();
        } else if (date.equals("")) {
            layoutBinding1.tvDate.setError("Select date");
            binding.llStep1.performClick();
        } else if (houseNo.equals("")) {
            layoutBinding1.etHouseNo.setError("Enter house no");
            binding.llStep1.performClick();
        } else if (street.equals("")) {
            layoutBinding1.etStreet.setError("Enter street");
            binding.llStep1.performClick();
        } else if (country.equals("")) {
            layoutBinding1.etCountry.setError("Enter country");
            binding.llStep1.performClick();
        } else if (stateId.equals("")) {
            Common.showToast("Select state");
            if (stateList.isEmpty()) getStateList();
            binding.llStep1.performClick();
        } else if (cityId.equals("")) {
            Common.showToast("Select city");
            if (cityList.isEmpty()) getCityList();
            binding.llStep1.performClick();
        } else if (pinCode.equals("")) {
            layoutBinding1.etPinCode.setError("Enter pin code");
            binding.llStep1.performClick();
        } else if (contact.equals("")) {
            layoutBinding3.etContact.setError("Enter contact number");
            binding.llStep3.performClick();
        } else if (contact.length() < 10 ) {
            layoutBinding3.etContact.setError("Please enter valid contact number");
            binding.llStep3.performClick();
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

            MultipartBody.Part fileToUpload1 = null, fileToUpload2 = null, fileToUpload3 = null, fileToUpload4 = null, fileToUpload5 = null, fileToUpload6 = null, fileToUpload7 = null, fileToUpload8 = null;
            if (!aadharImage1.isEmpty()) {
                fileToUpload1 = Common.imageToMultipart(aadharImage1, fileAadharImage1, "fileAadharImage1");
            }
            if (!aadharImage2.isEmpty()) {
                fileToUpload2 = Common.imageToMultipart(aadharImage2, fileAadharImage2, "fileAadharImage2");
            }
            if (!electionImage1.isEmpty()) {
                fileToUpload3 = Common.imageToMultipart(electionImage1, fileElectionImage1, "fileElectionImage1");
            }
            if (!electionImage2.isEmpty()) {
                fileToUpload4 = Common.imageToMultipart(electionImage2, fileElectionImage2, "fileElectionImage2");
            }
            if (!licenseImage1.isEmpty()) {
                fileToUpload5 = Common.imageToMultipart(licenseImage1, fileLicenseImage1, "fileLicenseImage1");
            }
            if (!licenseImage2.isEmpty()) {
                fileToUpload6 = Common.imageToMultipart(licenseImage2, fileLicenseImage2, "fileLicenseImage2");
            }
            if (!image.isEmpty()) {
                fileToUpload7 = Common.imageToMultipart(image, fileImage, "fileImage");
            }
            if (!formImage.isEmpty()) {
                fileToUpload8 = Common.imageToMultipart(formImage, fileFormImage, "fileFormImage");
            }

            RequestBody id = Common.dataToRequestBody(this.id);
            RequestBody customerId = Common.dataToRequestBody(Common.getCustomerId());
            RequestBody empId = Common.dataToRequestBody(Common.getEmpId());
            RequestBody registrationDate = Common.dataToRequestBody(this.date);
            RequestBody validDate = Common.dataToRequestBody(this.validDate);
            RequestBody registrationNo = Common.dataToRequestBody(this.registerNo);
            RequestBody firstName = Common.dataToRequestBody(this.firstName);
            RequestBody middleName = Common.dataToRequestBody(this.middleName);
            RequestBody lastName = Common.dataToRequestBody(this.lastName);
            RequestBody aadharNo = Common.dataToRequestBody(this.aadharNo);
            RequestBody electionCardNo = Common.dataToRequestBody(this.electionNo);
            RequestBody dlNo = Common.dataToRequestBody(this.licenseNo);
            RequestBody gender = Common.dataToRequestBody(this.gender);
            RequestBody houseNo = Common.dataToRequestBody(this.houseNo);
            RequestBody street = Common.dataToRequestBody(this.street);
            RequestBody landmark = Common.dataToRequestBody(this.landmark);
            RequestBody area = Common.dataToRequestBody(this.area);
            RequestBody countryName = Common.dataToRequestBody(this.country);
            RequestBody stateId = Common.dataToRequestBody(this.stateId);
            RequestBody districtName = Common.dataToRequestBody(this.district);
            RequestBody cityId = Common.dataToRequestBody(this.cityId);
            RequestBody pincode = Common.dataToRequestBody(this.pinCode);
            RequestBody tenementNo = Common.dataToRequestBody(this.tenamentNo);
            RequestBody zoneId = Common.dataToRequestBody(this.zoneId);
            RequestBody wardId = Common.dataToRequestBody(this.wardId);
            RequestBody contact = Common.dataToRequestBody(this.contact);
            RequestBody contact1 = Common.dataToRequestBody(this.contact2);
            RequestBody emailId = Common.dataToRequestBody(this.email);
            RequestBody placeArea = Common.dataToRequestBody(this.animalPlace);
            RequestBody placeOwned = Common.dataToRequestBody(this.ownedBy);
            RequestBody shedAvailable = Common.dataToRequestBody(this.shed);
            RequestBody storageAvailable = Common.dataToRequestBody(this.storage);
            RequestBody drinkingWater = Common.dataToRequestBody(this.waterFacility);
            RequestBody disposalFacility = Common.dataToRequestBody(this.disposalFacility);
            RequestBody remarks = Common.dataToRequestBody(this.remarks);
            RequestBody latitude = Common.dataToRequestBody(String.valueOf(this.latitude));
            RequestBody longitude = Common.dataToRequestBody(String.valueOf(this.longitude));
            RequestBody username = Common.dataToRequestBody(Common.getUsername());
            RequestBody password = Common.dataToRequestBody(Common.getPassword());
            RequestBody type = Common.dataToRequestBody(Common.getType());
            RequestBody pondId = Common.dataToRequestBody(pondID);
            RequestBody pondStatus = Common.dataToRequestBody(this.pondStatus);

            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.addAnimalOwner(Constant.addEditAnimalOwnerUrl, id, customerId, empId, validDate, registrationDate, registrationNo, firstName, middleName, lastName, aadharNo, fileToUpload1, fileToUpload2, electionCardNo, fileToUpload3, fileToUpload4, dlNo, fileToUpload5, fileToUpload6, gender, houseNo, street, landmark, area, countryName, stateId, districtName, cityId, pincode, tenementNo, zoneId, wardId, pondStatus, pondId, contact, contact1, emailId, placeArea, placeOwned, shedAvailable, storageAvailable, drinkingWater, disposalFacility, fileToUpload7, remarks, fileToUpload8, latitude, longitude, username, password, type).enqueue(new Callback<>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    Common.hideProgressDialog();

                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                if (apiResponse.getAnimalOwnerDetails() != null) {
                                    Intent intent = new Intent();
                                    intent.putExtra("details", apiResponse.getAnimalOwnerDetails());
                                    setResultOfActivity(intent, 1, true);
                                }
                                Common.showToast(apiResponse.getMessage());
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

    private void getStateList() {
        Common.showProgressDialog(context, context.getString(R.string.please_wait));
        if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getList(Constant.stateListUrl, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                    Common.hideProgressDialog();

                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                if (apiResponse.getStateList() != null) {
                                    bindStateData(apiResponse);
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

    private void bindStateData(APIResponse apiResponse) {
        ArrayList<String> spinnerList = new ArrayList<>();
        stateList.clear();

        if (apiResponse.getStateList() != null) {
            stateList.addAll(apiResponse.getStateList());

            for (int i = 0; i < apiResponse.getStateList().size(); i++) {
                spinnerList.add(apiResponse.getStateList().get(i).getName());
            }
        }

        layoutBinding1.spinnerState.setItem(spinnerList);

        layoutBinding1.spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stateId = stateList.get(i).getStateId();
                if (flag2 == 0) cityId = "";
                getCityList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (!stateId.equals("")) {
            for (int i = 0; i < stateList.size(); i++) {
                if (stateId.equals(stateList.get(i).getStateId())) {
                    layoutBinding1.spinnerState.setSelection(i);
                    break;
                }
            }
        }
    }

    private void getCityList() {
        Common.showProgressDialog(context, context.getString(R.string.please_wait));
        if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getList(Constant.cityListUrl, stateId, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                    Common.hideProgressDialog();

                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                if (apiResponse.getCityList() != null) {
                                    bindCityData(apiResponse);
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

    private void bindCityData(APIResponse apiResponse) {
        ArrayList<String> spinnerList = new ArrayList<>();
        cityList.clear();

        if (apiResponse.getCityList() != null) {
            cityList.addAll(apiResponse.getCityList());

            for (int i = 0; i < apiResponse.getCityList().size(); i++) {
                spinnerList.add(apiResponse.getCityList().get(i).getName());
            }
        }

        layoutBinding1.spinnerCity.setItem(spinnerList);

        layoutBinding1.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cityId = cityList.get(i).getCityId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (!cityId.equals("")) {
            for (int i = 0; i < cityList.size(); i++) {
                if (cityId.equals(cityList.get(i).getCityId())) {
                    layoutBinding1.spinnerCity.setSelection(i);
                    flag2 = 0;
                    break;
                }
            }
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

        layoutBinding3.spinnerZone.setItem(spinnerList);

        layoutBinding3.spinnerZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                zoneId = zoneList.get(i).getZoneId();
                if (flag4 == 0) wardId = "";
                getWardList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (!zoneId.equals("")) {
            for (int i = 0; i < zoneList.size(); i++) {
                if (zoneId.equals(zoneList.get(i).getZoneId())) {
                    layoutBinding3.spinnerZone.setSelection(i);
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

        layoutBinding3.spinnerWard.setItem(spinnerList);

        layoutBinding3.spinnerWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    layoutBinding3.spinnerWard.setSelection(i);
                    flag4 = 0;
                    break;
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

        layoutBinding3.spinnerCattle.setItem(spinnerList);

        layoutBinding3.spinnerCattle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pondID = pondList.get(i).getPondId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (!pondID.equals("")) {
            for (int i = 0; i < pondList.size(); i++) {
                if (pondID.equals(pondList.get(i).getPondId())) {
                    layoutBinding3.spinnerCattle.setSelection(i);
                    break;
                }
            }
        }
    }

}