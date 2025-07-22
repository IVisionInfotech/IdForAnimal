package com.idforanimal.retrofit;

import com.idforanimal.BuildConfig;

public class ApiUtils {

    public static ApiInterface getApiCalling() {
        return RetrofitBuilder.getClient(BuildConfig.Beta_Base_URL).create(ApiInterface.class);
    }
}