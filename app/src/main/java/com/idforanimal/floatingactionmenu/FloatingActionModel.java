package com.idforanimal.floatingactionmenu;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public class FloatingActionModel {
    @DrawableRes
    private int iconResId;
    @ColorRes
    private int iconBgColorResId;
    @StringRes
    private int labelResId;

    public FloatingActionModel(@DrawableRes int iconResId, @ColorRes int iconBgColorResId, @StringRes int labelResId) {
        this.iconResId = iconResId;
        this.iconBgColorResId = iconBgColorResId;
        this.labelResId = labelResId;
    }

    @DrawableRes
    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(@DrawableRes int iconResId) {
        this.iconResId = iconResId;
    }

    @ColorRes
    public int getIconBgColorResId() {
        return iconBgColorResId;
    }

    public void setIconBgColorResId(@ColorRes int iconBgColorResId) {
        this.iconBgColorResId = iconBgColorResId;
    }

    @StringRes
    public int getLabelResId() {
        return labelResId;
    }

    public void setLabelResId(@StringRes int labelResId) {
        this.labelResId = labelResId;
    }
}
