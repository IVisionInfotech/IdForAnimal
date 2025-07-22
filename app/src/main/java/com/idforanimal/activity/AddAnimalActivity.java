package com.idforanimal.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;

import com.idforanimal.R;
import com.idforanimal.bluetooth.Bluetooth;
import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.databinding.ActivityAddAnimalBinding;
import com.idforanimal.databinding.LayoutAddAnimalStepOneBinding;
import com.idforanimal.databinding.LayoutAddAnimalStepTwoBinding;
import com.idforanimal.databinding.ToolbarPrimaryBinding;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.Animal;
import com.idforanimal.model.CommonModel;
import com.idforanimal.model.Pond;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.utils.BroadCastManager;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;
import com.idforanimal.utils.MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAnimalActivity extends BaseActivity {

    private ActivityAddAnimalBinding binding;
    private LayoutAddAnimalStepOneBinding layoutBinding1;
    private LayoutAddAnimalStepTwoBinding layoutBinding2;
    private ToolbarPrimaryBinding toolbarPrimaryBinding;
    private String id = "", ownerId = "", taggingDate = "", validDate = "", rfidTagNo = "", visualTagNo = "",
            catchingLocation = "", animalTypeId = "", cattleTypeId = "", breedTypeId = "", colorId = "",
            tailId = "", hornId = "", visualSign = "", dob = "", age = "", girth = "", length = "",
            milperday = "", milktype = "", lactation = "", pregStatus = "", image = "", formDocument = "",
            remark = "", pondID = "", pondStatus = "0", fileImage = "", fileFormImage = "", tag = "";
    private final ArrayList<CommonModel> animalTypeList = new ArrayList<>();
    private final ArrayList<CommonModel> subTypeList = new ArrayList<>();
    private final ArrayList<CommonModel> breedTypeList = new ArrayList<>();
    private final ArrayList<CommonModel> colorList = new ArrayList<>();
    private final ArrayList<CommonModel> tailList = new ArrayList<>();
    private final ArrayList<CommonModel> hornList = new ArrayList<>();
    private final ArrayList<Pond> pondList = new ArrayList<>();
    private ArrayList<String> statusList = new ArrayList<>(Arrays.asList("NEUTERED", "NOT NEUTERED", "PREGNANT", "NON PREGNANT", "NECO"));
    private boolean isFocus;
    private LocalReceiver localReceiver;
    private StringBuilder scannedData = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAnimalBinding.inflate(getLayoutInflater());
        layoutBinding1 = LayoutAddAnimalStepOneBinding.bind(binding.layout1.getRoot());
        toolbarPrimaryBinding = ToolbarPrimaryBinding.bind(binding.toolbar.getRoot());
        layoutBinding2 = LayoutAddAnimalStepTwoBinding.bind(binding.layout2.getRoot());
        setContentView(binding.getRoot());

        context = AddAnimalActivity.this;
        activity = this;


        layoutBinding1.tvDate.setText(Common.getCurrentDate());
        taggingDate = Common.changeDateFormat(Common.getCurrentDate(), "dd-MM-yyyy", "yyyy-MM-dd");
        if (getIntent() != null) {
            if (getIntent().hasExtra("ownerId")) {
                ownerId = getIntent().getStringExtra("ownerId");
            }
            if (getIntent().hasExtra("RFID")) {
                rfidTagNo = getIntent().getStringExtra("RFID");
                binding.layout1.etRFID.setText(rfidTagNo);
            }
            if (getIntent().hasExtra("details")) {
                Animal model = (Animal) getIntent().getSerializableExtra("details");
                id = model.getAnimalId();
                ownerId = model.getOwnerId();
                getDetails();
                toolbarPrimaryBinding.tvTitle.setText("Update Animal");
                layoutBinding2.tvSubmit.setText("Update");
            } else {
                toolbarPrimaryBinding.tvTitle.setText("Add Animal");
                layoutBinding2.tvSubmit.setText("Add");
                init();
            }
        } else finish();

        try {
            IntentFilter filter = new IntentFilter("com.idforanimal.ACTION_VIEW_DETAILS");
            filter.addAction(Constant.refreshCount);
            localReceiver = new LocalReceiver();
            BroadCastManager.getInstance().registerReceiver(this, localReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        checkLocationPermissions(permission -> turnGPSOn(this, permission1 -> {
            checkLocationSettings();
            new Handler().postDelayed(() -> {
                layoutBinding2.etLocation.setText(address);
                layoutBinding2.tvDateTime.setText(Common.getCurrentDate());
                layoutBinding2.tvLocation.setText("latitude: " + String.valueOf(latitude) + "\n" + "longitude: " + String.valueOf(longitude));
            }, 500);
        }));
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
                        layoutBinding1.etRFID.setText(tag);
                        layoutBinding1.etVisualTag.setText(tag.substring(tag.length() - 4));
                    });
                    scannedData.setLength(0);
                }
            }
        }
        return super.dispatchKeyEvent(event);
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

        layoutBinding2.ivLocation.setOnClickListener(v -> checkLocationPermissions(permission -> turnGPSOn(this, permission1 -> {
            checkLocationSettings();
            new Handler().postDelayed(() -> layoutBinding2.etLocation.setText(address), 1000);
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

        layoutBinding1.tvValidDate.setOnClickListener(v -> Common.showMinDatePicker(context, dt -> {
            validDate = dt;
            layoutBinding1.tvValidDate.setText(Common.changeDateFormat(validDate));
        }));

        layoutBinding1.tvDate.setOnClickListener(v -> Common.showMinDatePicker(context, dt -> {
            taggingDate = dt;
            layoutBinding1.tvDate.setText(Common.changeDateFormat(taggingDate));
        }));

        layoutBinding2.tvDOB.setOnClickListener(v -> Common.showDatePicker(context, (date, year, monthOfYear, dayOfMonth) -> {
            dob = date;
            layoutBinding2.tvDOB.setText(Common.changeDateFormat(dob));
            age = Common.getAge(year, monthOfYear, dayOfMonth);
            layoutBinding2.etAge.setText(age);
        }));

        layoutBinding2.etAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && isFocus) {
                    if (!s.toString().equals("")) {
                        age = s.toString();
                        dob = Common.getDOBFromAge(Integer.parseInt(s.toString()));
                        layoutBinding2.tvDOB.setText(Common.changeDateFormat(dob));
                    }
                }
            }
        });
        layoutBinding2.llImage.setOnClickListener(v -> checkPermissions(getStoragePermissionList(), permission -> selectImage((file, bitmap) -> {
            layoutBinding2.tvLocation.setVisibility(View.VISIBLE);
            layoutBinding2.tvDateTime.setVisibility(View.VISIBLE);
            layoutBinding2.ivImage.setImageBitmap(bitmap);
            layoutBinding2.ivLocationImage.setImageBitmap(bitmap);
            layoutBinding2.llImage.setVisibility(View.GONE);
            layoutBinding2.rlImage.setVisibility(View.VISIBLE);
            image = file.getPath();
            fileImage = file.getName();
        })));
        layoutBinding2.rlImage.setOnClickListener(v ->
                checkPermissions(getStoragePermissionList(), permission -> selectImage((file, bitmap) -> {
                    layoutBinding2.tvLocation.setVisibility(View.VISIBLE);
                    layoutBinding2.tvDateTime.setVisibility(View.VISIBLE);
                    layoutBinding2.ivImage.setImageBitmap(bitmap);
                    layoutBinding2.ivLocationImage.setImageBitmap(bitmap);
                    layoutBinding2.llImage.setVisibility(View.GONE);
                    layoutBinding2.rlImage.setVisibility(View.VISIBLE);
                    image = file.getPath();
                    fileImage = file.getName();
                })));
        layoutBinding2.ivManualForm.setOnClickListener(v -> checkPermissions(getStoragePermissionList(), permission -> selectImage((file, bitmap) -> {
            layoutBinding2.ivManualForm.setImageBitmap(bitmap);
            formDocument = file.getPath();
            fileFormImage = file.getName();
        })));

        layoutBinding2.rdgMilkType.setOnCheckedChangeListener((group, checkedId) -> {
            if (layoutBinding2.rdoMilkTypeA1.isChecked()) {
                milktype = "A1";
            } else if (layoutBinding2.rdoMilkTypeA2.isChecked()) {
                milktype = "A2";
            }
        });


        layoutBinding2.rdgSourceOfIdentify.setOnCheckedChangeListener((group, checkedId) -> {
            if (layoutBinding2.rdOnField.isChecked()) {
                layoutBinding2.spinnerCattle.setVisibility(View.GONE);
                pondStatus = "0";
            } else if (layoutBinding2.rdCattlePond.isChecked()) {
                layoutBinding2.spinnerCattle.setVisibility(View.VISIBLE);
                pondStatus = "1";
            }
        });

        layoutBinding2.cvNext.setOnClickListener(v -> {
            layoutBinding2.cvNext.setEnabled(false);
            checkLocationPermissions(permission -> {
                turnGPSOn(this, permission1 -> validate());
                new Handler().postDelayed(() -> layoutBinding2.cvNext.setEnabled(true), 3000);
            });
        });
        layoutBinding2.etAge.setOnFocusChangeListener((view, b) -> isFocus = b);
    }

    private void bindView(String pages) {
        switch (pages) {
            case "1":
                binding.layout1.getRoot().setVisibility(View.VISIBLE);
                binding.layout2.getRoot().setVisibility(View.GONE);
                if (Common.getCustomerId().equals("5") || Common.getCustomerId().equals("6")) {
                    layoutBinding1.txtValidDate.setVisibility(View.GONE);
                    layoutBinding1.llValidDate.setVisibility(View.GONE);
                    layoutBinding1.etRFID.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String rfidText = s.toString();
                            if (rfidText.length() >= 4) {
                                String lastFourCharacters = rfidText.substring(rfidText.length() - 4);
                                layoutBinding1.etVisualTag.setText(lastFourCharacters);
                            } else {
                                layoutBinding1.etVisualTag.setText("");
                            }
                        }
                    });
                    layoutBinding1.etVisualTag.setEnabled(false);
                    layoutBinding1.etVisualTag.setFocusable(false);
                    layoutBinding1.etVisualTag.setFocusableInTouchMode(false);
                }
                if (animalTypeList.isEmpty()) getAnimalTypeList();
                break;
            case "2":
                binding.layout1.getRoot().setVisibility(View.GONE);
                binding.layout2.getRoot().setVisibility(View.VISIBLE);
                if (subTypeList.isEmpty()) getSubTypeList();
                if (breedTypeList.isEmpty()) getBreedTypeList();
                if (colorList.isEmpty()) getColorList();
                if (Common.getCustomerId().equals("5") || Common.getCustomerId().equals("6")) {
                    layoutBinding2.llTail.setVisibility(View.GONE);
                    layoutBinding2.llVisual.setVisibility(View.GONE);
                    layoutBinding2.llDOB.setVisibility(View.GONE);
                    layoutBinding2.llGrith.setVisibility(View.GONE);
                    layoutBinding2.llMilk.setVisibility(View.GONE);
                    layoutBinding2.llMilkType.setVisibility(View.GONE);
                    layoutBinding2.llIdentify.setVisibility(View.GONE);
                    layoutBinding2.ivManualForm.setVisibility(View.GONE);
                    layoutBinding2.tvManualForm.setVisibility(View.GONE);
                } else {
                    if (tailList.isEmpty()) getTailList();
                    if (hornList.isEmpty()) getHornList();
                    if (pondList.isEmpty()) getCattlePondList();
                }
                bindStatusData();
                break;
        }
    }

    private void bindData(Animal model) {
        if (model != null) {
            id = model.getAnimalId();
            ownerId = model.getOwnerId();
            taggingDate = model.getTaggingDate();
            layoutBinding1.tvDate.setText(Common.changeDateFormat(taggingDate));
            layoutBinding1.etRFID.setText(model.getRfidTagNo());
            layoutBinding1.etVisualTag.setText(model.getVisualTagNo());
            animalTypeId = model.getAnimalTypeId();
            init();

            layoutBinding2.etLactation.setText(model.getCatchingLocation());
            cattleTypeId = model.getCattleTypeId();
            breedTypeId = model.getBreedTypeId();
            colorId = model.getColorId();
            tailId = model.getTailId();
            hornId = model.getHornId();
            layoutBinding2.etSign.setText(model.getVisualSign());
            layoutBinding2.etAge.setText(model.getAge());
            dob = model.getDob();
            layoutBinding2.tvDOB.setText(Common.changeDateFormat(dob));
            if (!model.getValidDate().equals("0000-00-00")) {
                validDate = model.getValidDate();
                layoutBinding1.tvValidDate.setText(Common.changeDateFormat(validDate));
            }
            layoutBinding2.etGirth.setText(model.getGirth());
            layoutBinding2.etLegth.setText(model.getLength());
            layoutBinding2.etMilkPerDay.setText(model.getMilperday());
            layoutBinding2.etLactation.setText(model.getLactation());
            layoutBinding2.etRemarks.setText(model.getRemark());
            if (model.getMilktype() != null) {
                if (model.getMilktype().equals("A1")) {
                    layoutBinding2.rdoMilkTypeA1.setChecked(true);
                }
                if (model.getMilktype().equals("A2")) {
                    layoutBinding2.rdoMilkTypeA2.setChecked(true);
                }
            }
            if (model.getPregStatus() != null) {
                pregStatus = model.getPregStatus();
            }
            if (!TextUtils.isEmpty(model.getImage()))
                Common.loadImage(context, layoutBinding2.ivImage, model.getImage());
            if (!TextUtils.isEmpty(model.getFormDocument()))
                Common.loadImage(context, layoutBinding2.ivManualForm, model.getFormDocument());
        }
    }

    private void getDetails() {
        if (ConnectivityReceiver.isConnected(context)) {
            Common.showProgressDialog(context, context.getString(R.string.please_wait));

            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getDetails(Constant.animalDetailsUrl, id, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    Common.hideProgressDialog();

                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                if (apiResponse.getAnimalDetails() != null) {
                                    bindData(apiResponse.getAnimalDetails());
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

    private void validate() {
        rfidTagNo = layoutBinding1.etRFID.getText().toString().trim();
        visualTagNo = layoutBinding1.etVisualTag.getText().toString().trim();

        catchingLocation = layoutBinding2.etLocation.getText().toString().trim();
        visualSign = layoutBinding2.etSign.getText().toString().trim();
        age = layoutBinding2.etAge.getText().toString().trim();
        girth = layoutBinding2.etGirth.getText().toString().trim();
        length = layoutBinding2.etLegth.getText().toString().trim();
        milperday = layoutBinding2.etMilkPerDay.getText().toString().trim();
        lactation = layoutBinding2.etLactation.getText().toString().trim();
        remark = layoutBinding2.etRemarks.getText().toString().trim();

        if (taggingDate.isEmpty()) {
            layoutBinding1.tvDate.setError("Select tagging date");
            binding.llStep1.performClick();
        } else if (rfidTagNo.isEmpty()) {
            layoutBinding1.etRFID.setError("Enter RFID tag no");
            binding.llStep1.performClick();
        } else if (rfidTagNo.length() != 15) {
            layoutBinding1.etRFID.setError("Enter valid RFID tag no");
            binding.llStep1.performClick();
        } else if (visualTagNo.isEmpty()) {
            layoutBinding1.etVisualTag.setError("Enter visual tag no");
            binding.llStep1.performClick();
        } else if (animalTypeId.isEmpty()) {
            Common.showToast("Select animal type");
            if (animalTypeList.isEmpty()) getAnimalTypeList();
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

            MultipartBody.Part fileToUpload1 = null, fileToUpload2 = null;
            if (!image.isEmpty()) {
                layoutBinding2.rlImage.setDrawingCacheEnabled(true);
                layoutBinding2.rlImage.buildDrawingCache();
                Bitmap bm = layoutBinding2.rlImage.getDrawingCache();
                File file = Common.bitmapToFile(this, bm);
                image = file.getPath();
                fileImage = file.getName();
                fileToUpload1 = Common.imageToMultipart(image, fileImage, "fileImage");
            }
            if (!formDocument.isEmpty()) {
                fileToUpload2 = Common.imageToMultipart(formDocument, fileFormImage, "fileFormImage");
            }

            RequestBody id = Common.dataToRequestBody(this.id);
            RequestBody customerId = Common.dataToRequestBody(Common.getCustomerId());
            RequestBody empId = Common.dataToRequestBody(Common.getEmpId());
            RequestBody ownerId = Common.dataToRequestBody(this.ownerId);
            RequestBody taggingDate = Common.dataToRequestBody(this.taggingDate);
            RequestBody validDate = Common.dataToRequestBody(this.validDate);
            RequestBody rfidTagNo = Common.dataToRequestBody(this.rfidTagNo);
            RequestBody visualTagNo = Common.dataToRequestBody(this.visualTagNo);
            RequestBody catchingLocation = Common.dataToRequestBody(this.catchingLocation);
            RequestBody animalTypeId = Common.dataToRequestBody(this.animalTypeId);
            RequestBody cattleTypeId = Common.dataToRequestBody(this.cattleTypeId);
            RequestBody breedTypeId = Common.dataToRequestBody(this.breedTypeId);
            RequestBody colorId = Common.dataToRequestBody(this.colorId);
            RequestBody tailId = Common.dataToRequestBody(this.tailId);
            RequestBody hornId = Common.dataToRequestBody(this.hornId);
            RequestBody visualSign = Common.dataToRequestBody(this.visualSign);
            RequestBody dob = Common.dataToRequestBody(this.dob);
            RequestBody age = Common.dataToRequestBody(this.age);
            RequestBody girth = Common.dataToRequestBody(this.girth);
            RequestBody length = Common.dataToRequestBody(this.length);
            RequestBody milperday = Common.dataToRequestBody(this.milperday);
            RequestBody milktype = Common.dataToRequestBody(this.milktype);
            RequestBody lactation = Common.dataToRequestBody(this.lactation);
            RequestBody pregStatus = Common.dataToRequestBody(this.pregStatus);
            RequestBody remark = Common.dataToRequestBody(this.remark);
            RequestBody latitude = Common.dataToRequestBody(String.valueOf(this.latitude));
            RequestBody longitude = Common.dataToRequestBody(String.valueOf(this.longitude));
            RequestBody username = Common.dataToRequestBody(Common.getUsername());
            RequestBody password = Common.dataToRequestBody(Common.getPassword());
            RequestBody type = Common.dataToRequestBody(Common.getType());
            RequestBody pondId = Common.dataToRequestBody(pondID);
            RequestBody pondStatus = Common.dataToRequestBody(this.pondStatus);


            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.addAnimal(Constant.addEditAnimalUrl, id, customerId, empId, ownerId, taggingDate, validDate, rfidTagNo, visualTagNo, catchingLocation, animalTypeId, cattleTypeId, breedTypeId, colorId, tailId, hornId, visualSign, dob, age, girth, length, milperday, milktype, lactation, pregStatus, remark, pondStatus, pondId, fileToUpload1, fileToUpload2, latitude, longitude, username, password, type).enqueue(new Callback<>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    Common.hideProgressDialog();

                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                if (apiResponse.getAnimalDetails() != null) {
                                    Intent intent = new Intent();
                                    intent.putExtra("details1", apiResponse.getAnimalDetails());
                                    setResultOfActivity(intent, 1, true);
                                }
                            } else if (apiResponse.getStatus() == 3) {
                                layoutBinding1.etRFID.setError(apiResponse.getMessage());
                                binding.llStep1.performClick();
                            } else if (apiResponse.getStatus() == 4) {
                                layoutBinding1.etVisualTag.setError(apiResponse.getMessage());
                                binding.llStep1.performClick();
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

    private void getAnimalTypeList() {
        if (Common.getCustomerId().equals("5") || Common.getCustomerId().equals("6")) {
            animalTypeId = animalTypeId.isEmpty() ? "4" : animalTypeId;
            bindAnimalTypeData(Constant.getAnimalTypes());
        } else {
            Common.showProgressDialog(context, context.getString(R.string.please_wait));
            if (ConnectivityReceiver.isConnected(context)) {
                ApiInterface apiInterface = ApiUtils.getApiCalling();
                apiInterface.getList(Constant.animalTypeListUrl, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {

                    @Override
                    public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                        Common.hideProgressDialog();

                        try {
                            APIResponse apiResponse = response.body();
                            if (apiResponse != null) {
                                if (apiResponse.getStatus() == 99) {
                                    Common.logout(context, apiResponse.getMessage());
                                } else if (apiResponse.getStatus() == 1) {
                                    bindAnimalTypeData(apiResponse);
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

    private void bindAnimalTypeData(APIResponse apiResponse) {
        if (apiResponse.getAnimalTypeList() != null) {

            ArrayList<String> spinnerList = new ArrayList<>();
            animalTypeList.clear();

            if (apiResponse.getAnimalTypeList() != null) {
                animalTypeList.addAll(apiResponse.getAnimalTypeList());

                for (int i = 0; i < apiResponse.getAnimalTypeList().size(); i++) {
                    spinnerList.add(apiResponse.getAnimalTypeList().get(i).getAnimalName());
                }
            }

            layoutBinding1.spinnerAnimalType.setItem(spinnerList);

            layoutBinding1.spinnerAnimalType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    animalTypeId = animalTypeList.get(i).getAnimalTypeId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if (!animalTypeId.isEmpty()) {
                for (int i = 0; i < animalTypeList.size(); i++) {
                    if (animalTypeId.equals(animalTypeList.get(i).getAnimalTypeId())) {
                        layoutBinding1.spinnerAnimalType.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void getSubTypeList() {
        if (Common.getCustomerId().equals("5") || Common.getCustomerId().equals("6")) {
            bindSubTypeData(Constant.getSubTypes());
        } else {
            if (ConnectivityReceiver.isConnected(context)) {
                ApiInterface apiInterface = ApiUtils.getApiCalling();
                apiInterface.getList(Constant.subTypeListUrl, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                        try {
                            APIResponse apiResponse = response.body();
                            if (apiResponse != null) {
                                if (apiResponse.getStatus() == 99) {
                                    Common.logout(context, apiResponse.getMessage());
                                } else if (apiResponse.getStatus() == 1) {
                                    bindSubTypeData(apiResponse);
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
    }

    private void bindSubTypeData(APIResponse apiResponse) {
        if (apiResponse.getSubTypeList() != null) {

            ArrayList<String> spinnerList = new ArrayList<>();
            subTypeList.clear();

            if (apiResponse.getSubTypeList() != null) {
                subTypeList.addAll(apiResponse.getSubTypeList());

                for (int i = 0; i < apiResponse.getSubTypeList().size(); i++) {
                    spinnerList.add(apiResponse.getSubTypeList().get(i).getCattleName());
                }
            }

            layoutBinding2.spinnerSubType.setItem(spinnerList);

            layoutBinding2.spinnerSubType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    cattleTypeId = subTypeList.get(i).getCattleTypeId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if (!cattleTypeId.equals("")) {
                for (int i = 0; i < subTypeList.size(); i++) {
                    if (cattleTypeId.equals(subTypeList.get(i).getCattleTypeId())) {
                        layoutBinding2.spinnerSubType.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void getBreedTypeList() {
        if (Common.getCustomerId().equals("5") || Common.getCustomerId().equals("6")) {
            breedTypeId = breedTypeId.isEmpty() ? "11" : breedTypeId;
            bindBreedTypeData(Constant.getBreedTypes());
        } else {
            if (ConnectivityReceiver.isConnected(context)) {
                ApiInterface apiInterface = ApiUtils.getApiCalling();
                apiInterface.getList(Constant.breedTypeListUrl, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

                    @Override
                    public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                        try {
                            APIResponse apiResponse = response.body();
                            if (apiResponse != null) {
                                if (apiResponse.getStatus() == 99) {
                                    Common.logout(context, apiResponse.getMessage());
                                } else if (apiResponse.getStatus() == 1) {
                                    bindBreedTypeData(apiResponse);
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
    }

    private void bindBreedTypeData(APIResponse apiResponse) {
        if (apiResponse.getBreedTypeList() != null) {

            ArrayList<String> spinnerList = new ArrayList<>();
            breedTypeList.clear();

            if (apiResponse.getBreedTypeList() != null) {
                breedTypeList.addAll(apiResponse.getBreedTypeList());

                for (int i = 0; i < apiResponse.getBreedTypeList().size(); i++) {
                    spinnerList.add(apiResponse.getBreedTypeList().get(i).getBreedName());
                }
            }

            layoutBinding2.spinnerBreedType.setItem(spinnerList);

            layoutBinding2.spinnerBreedType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    breedTypeId = breedTypeList.get(i).getBreedTypeId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if (!breedTypeId.equals("")) {
                for (int i = 0; i < breedTypeList.size(); i++) {
                    if (breedTypeId.equals(breedTypeList.get(i).getBreedTypeId())) {
                        layoutBinding2.spinnerBreedType.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void getColorList() {
        if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getList(Constant.colorListUrl, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                bindColorData(apiResponse);
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

    private void bindColorData(APIResponse apiResponse) {
        if (apiResponse.getColorList() != null) {

            ArrayList<String> spinnerList = new ArrayList<>();
            colorList.clear();

            if (apiResponse.getColorList() != null) {
                colorList.addAll(apiResponse.getColorList());

                for (int i = 0; i < apiResponse.getColorList().size(); i++) {
                    spinnerList.add(apiResponse.getColorList().get(i).getColorName());
                }
            }

            layoutBinding2.spinnerColor.setItem(spinnerList);

            layoutBinding2.spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    colorId = colorList.get(i).getColorId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            colorId = colorId.isEmpty() ? "4" : colorId;
            for (int i = 0; i < colorList.size(); i++) {
                if (colorId.equals(colorList.get(i).getColorId())) {
                    layoutBinding2.spinnerColor.setSelection(i);
                    break;
                }
            }
        }
    }

    private void getTailList() {
        if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getList(Constant.tailListUrl, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                bindTailData(apiResponse);
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

    private void bindTailData(APIResponse apiResponse) {
        if (apiResponse.getTailList() != null) {

            ArrayList<String> spinnerList = new ArrayList<>();
            tailList.clear();

            if (apiResponse.getTailList() != null) {
                tailList.addAll(apiResponse.getTailList());

                for (int i = 0; i < apiResponse.getTailList().size(); i++) {
                    spinnerList.add(apiResponse.getTailList().get(i).getTailName());
                }
            }

            layoutBinding2.spinnerTail.setItem(spinnerList);

            layoutBinding2.spinnerTail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    tailId = tailList.get(i).getTailId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if (!tailId.equals("")) {
                for (int i = 0; i < tailList.size(); i++) {
                    if (tailId.equals(tailList.get(i).getTailId())) {
                        layoutBinding2.spinnerTail.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void getHornList() {
        if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getList(Constant.hornListUrl, Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else if (apiResponse.getStatus() == 1) {
                                bindHornData(apiResponse);
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

    private void bindHornData(APIResponse apiResponse) {
        if (apiResponse.getHornList() != null) {

            ArrayList<String> spinnerList = new ArrayList<>();
            hornList.clear();

            if (apiResponse.getHornList() != null) {
                hornList.addAll(apiResponse.getHornList());

                for (int i = 0; i < apiResponse.getHornList().size(); i++) {
                    spinnerList.add(apiResponse.getHornList().get(i).getHornName());
                }
            }

            layoutBinding2.spinnerHorn.setItem(spinnerList);

            layoutBinding2.spinnerHorn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    hornId = hornList.get(i).getHornId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if (!hornId.equals("")) {
                for (int i = 0; i < hornList.size(); i++) {
                    if (hornId.equals(hornList.get(i).getHornId())) {
                        layoutBinding2.spinnerHorn.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void bindStatusData() {
        layoutBinding2.spinnerStatus.setItem(statusList);

        layoutBinding2.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pregStatus = statusList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (!pregStatus.isEmpty()) {
            for (int i = 0; i < statusList.size(); i++) {
                if (pregStatus.equals(statusList.get(i))) {
                    layoutBinding2.spinnerStatus.setSelection(i);
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

        layoutBinding2.spinnerCattle.setItem(spinnerList);

        layoutBinding2.spinnerCattle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    layoutBinding2.spinnerCattle.setSelection(i);
                    break;
                }
            }
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
            if (intent.hasExtra(Constant.refreshCount)) {
                if (intent.getStringExtra(Constant.refreshCount).equals(Constant.refreshCount)) {
                    bindData(intent);
                }
            }
        }
    }

    private void bindData(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra("tag")) {
                tag = intent.getStringExtra("tag");
                layoutBinding1.etRFID.setText(tag);
                layoutBinding1.etVisualTag.setText(tag.substring(tag.length() - 4));
            }
        }
    }

}