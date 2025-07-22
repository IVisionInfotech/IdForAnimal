package com.idforanimal.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.idforanimal.model.OfflineAnimalData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Session {

    private SharedPreferences prefs;
    private final ArrayList<String> list = new ArrayList<>(); // Local list storage


    public Session(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getLoginType() {
        return prefs.getString("loginType", "");
    }

    public void setLoginType(String status) {
        prefs.edit().putString("loginType", status).apply();
    }

    public Boolean getLoginStatus() {
        return prefs.getBoolean("loginStatus", false);
    }

    public void setLoginStatus(Boolean status) {
        prefs.edit().putBoolean("loginStatus", status).apply();
    }

    public String getDeviceName() {
        return prefs.getString("deviceName", "");
    }

    public void setDeviceName(String deviceName) {
        prefs.edit().putString("deviceName", deviceName).apply();
    }

    public String getDeviceAddress() {
        return prefs.getString("deviceAddress", "");
    }

    public void setDeviceAddress(String deviceAddress) {
        prefs.edit().putString("deviceAddress", deviceAddress).apply();
    }

    public String getUsername() {
        return prefs.getString("username", "");
    }

    public void setUsername(String username) {
        prefs.edit().putString("username", username).apply();
    }

    public String getPassword() {
        return prefs.getString("password", "");
    }

    public void setPassword(String password) {
        prefs.edit().putString("password", password).apply();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        prefs.edit().putBoolean("isFirstTimeLaunch", isFirstTime).apply();
    }

    public boolean isFirstTimeLaunch() {
        return prefs.getBoolean("isFirstTimeLaunch", true);
    }


    public void saveListToPreferences(List<OfflineAnimalData> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefs.edit().putString("microchipList", json).apply();
    }

    public List<OfflineAnimalData> getStringList() {
        Gson gson = new Gson();
        String json = prefs.getString("microchipList", ""); // Retrieve JSON
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<OfflineAnimalData>>() {}.getType();
        return gson.fromJson(json, type);
    }
    public void addStringToList(OfflineAnimalData value) {
        List<OfflineAnimalData> list = getStringList();
        if (!list.contains(value)) {
            list.add(value);
            saveListToPreferences(list);
        }
    }

    public void removeStringFromList(OfflineAnimalData value) {
        List<OfflineAnimalData> list = getStringList();
        for (OfflineAnimalData model:list) {
            if (model.getMicroChipNo().equals(value.getMicroChipNo())){
                list.remove(model);
                saveListToPreferences(list);
                break;
            }
        }
    }

    public void clearList() {
        prefs.edit().remove("microchipList").apply();
    }

}