package com.idforanimal.utils;

public interface ClickListener {

    default void onLoadListener() {

    }

    default void onItemSelected(int position) {

    }

    default void onItemSelected(int position, int itemId) {

    }

    default void onItemSelected(String str) {

    }

    default void onItemSelected(String str, String str2, String str3) {

    }
}