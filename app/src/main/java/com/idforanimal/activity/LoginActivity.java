package com.idforanimal.activity;

import android.os.Bundle;

import com.google.gson.JsonObject;
import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.R;
import com.idforanimal.databinding.ActivityLoginBinding;
import com.idforanimal.model.User;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.CommonParsing;
import com.idforanimal.utils.Constant;
import com.idforanimal.utils.RealmController;

import in.aabhasjindal.otptextview.OTPListener;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private String username = "", password = "", playerId = "", loginType = "";
    private Realm realm;
    private boolean login = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = LoginActivity.this;

        RealmController.with(context).refresh();
        realm = RealmController.with(this).getRealm();
        init();

        if (getIntent() != null) {
            if (getIntent().hasExtra("login")) {
                login = getIntent().getBooleanExtra("login", false);
            }
        }
        checkLocationPermissions(permission -> turnGPSOn(this, permission1 -> checkLocationSettings()));
    }

    private void init() {
        binding.otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                password = otp;
            }
        });
        binding.btnSubmit.setOnClickListener(view -> validate());
    }

    @Override
    public void onBackPressed() {
        gotoBack();
    }

    private void goToMain() {
        session.setLoginStatus(true);
        session.setLoginType(loginType);
        if (login) {
            setResultOfActivity(1, true);
        } else {
            goToActivity(context, MainNewActivity.class);
            finish();
        }
    }

    private void validate() {
        username = binding.etContact.getText().toString();
        if (username.isEmpty()) {
            binding.etContact.setError("Enter contact");
            binding.etContact.requestFocus();
        } else if (password.isEmpty()) {
            Common.showToast("Enter password");
        } else {
            login();
        }
    }

    private void login() {
        Common.showProgressDialog(context, getString(R.string.please_wait));
        if (ConnectivityReceiver.isConnected(context)) {
            ApiInterface apiInterface = ApiUtils.getApiCalling();

            apiInterface.login(Constant.loginUrl, username, password, playerId).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    try {
                        JsonObject jsonObject = response.body();
                        Common.showToast(jsonObject.get("message").getAsString());

                        if (jsonObject.get("status").getAsString().equals("1")) {
                            loginType = jsonObject.get("loginType").getAsString();
                            JsonObject result = jsonObject.getAsJsonObject("result");

                            User user = RealmController.with(context).getUser();
                            if (user != null) {
                                updateFlag = true;
                                realm.beginTransaction();
                            } else {
                                updateFlag = false;
                                user = new User();
                            }

                            user = CommonParsing.bindUserData(result, user);

                            if (!updateFlag) {
                                realm.beginTransaction();
                                realm.copyToRealm(user);
                            }
                            realm.commitTransaction();

                            session.setUsername(Common.encodeData(result.get("contact").getAsString()));
                            session.setPassword(Common.encodeData(result.get("password").getAsString()));

                            if (jsonObject.get("status").getAsString().equals("1")) {
                                session.setLoginStatus(true);
                                goToMain();
                            }
                        } else if (jsonObject.get("status").getAsString().equals("0")) {
                            binding.etContact.setError(jsonObject.get("message").getAsString());
                        } else {
                            Common.showSnackBar(binding.btnSubmit, jsonObject.get("message").getAsString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Common.hideProgressDialog();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                    Common.hideProgressDialog();
                }
            });
        } else {
            Common.noInternetDialog(this);
        }

    }
}
