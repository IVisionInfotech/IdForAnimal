package com.idforanimal.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.fragment.app.Fragment;

import com.idforanimal.model.User;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Context context) {
        realm = Realm.getDefaultInstance();
    }


    public static RealmController with(Activity activity) {
        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Context context) {
        if (instance == null) {
            instance = new RealmController(context);
        }
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    //Refresh the realm istance
    public void refresh() {
        realm.refresh();
    }

    public void clearAllUser() {
        realm.beginTransaction();
        realm.delete(User.class);
        realm.commitTransaction();
    }

    public RealmResults<User> getAllUser() {
        return realm.where(User.class).findAll();
    }

    public User getUser(String id) {
        return realm.where(User.class).equalTo("id", id).findFirst();
    }

    public User getUser() {
        return realm.where(User.class).findFirst();
    }
}