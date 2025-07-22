package com.idforanimal.utils;

import com.google.gson.JsonObject;
import com.idforanimal.model.User;

public class CommonParsing {

    private static String TAG = "CommonParsing";

    public static User bindUserData(JsonObject object, User model) {

        if (object.has("userId")) model.setId(Integer.parseInt(object.get("userId").getAsString()));
        if (object.has("customerId")) model.setCustomerId(object.get("customerId").getAsString());
        if (object.has("name")) model.setName(object.get("name").getAsString());
        if (object.has("empNo")) model.setEmpNo(object.get("empNo").getAsString());
        if (object.has("designationId"))
            model.setDesignationId(object.get("designationId").getAsString());
        if (object.has("designationName"))
            model.setDesignationName(object.get("designationName").getAsString());
        if (object.has("departmentId"))
            model.setDepartmentId(object.get("departmentId").getAsString());
        if (object.has("departmentName"))
            model.setDepartmentName(object.get("departmentName").getAsString());
        if (object.has("contact")) model.setContact(object.get("contact").getAsString());
        if (object.has("password")) model.setPassword(object.get("password").getAsString());
        if (object.has("contact2")) model.setContact2(object.get("contact2").getAsString());
        if (object.has("image")) model.setImage(object.get("image").getAsString());
        if (object.has("remark")) model.setRemark(object.get("remark").getAsString());
        if (object.has("emailId")) model.setEmailId(object.get("emailId").getAsString());
        if (object.has("countryName"))
            model.setCountryName(object.get("countryName").getAsString());
        if (object.has("stateId")) model.setStateId(object.get("stateId").getAsString());
        if (object.has("StateName")) model.setStateName(object.get("StateName").getAsString());
        if (object.has("cityId")) model.setCityId(object.get("cityId").getAsString());
        if (object.has("cityName")) model.setCityName(object.get("cityName").getAsString());
        if (object.has("districtName"))
            model.setDistrictName(object.get("districtName").getAsString());
        if (object.has("address")) model.setAddress(object.get("address").getAsString());
        if (object.has("pincode")) model.setPincode(object.get("pincode").getAsString());

        return model;
    }
}