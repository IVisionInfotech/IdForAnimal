package com.idforanimal.floatingactionmenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import androidx.annotation.ColorRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.idforanimal.R;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class FloatingActionMenu extends FrameLayout {

    private ArrayList<FloatingActionModel> mActions = new ArrayList<>();
    private ArrayList<ConstraintLayout> mRows = new ArrayList<>();
    private final LayoutInflater mInflater;
    private FloatingActionButton floatingActionButton;
    private String currentClickedTag = null;
    private boolean isOpen = false;
    private boolean dismissOnTapOutside = false;
    private final AtomicInteger mAnimationCount = new AtomicInteger();

    public FloatingActionMenu(Context context) {
        this(context, null);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setupMainFloatingActionButton();
    }

    private void setupMainFloatingActionButton() {
        floatingActionButton = new FloatingActionButton(getContext());
        floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.add));
        addView(floatingActionButton, getDefaultParam());
        floatingActionButton.setOnClickListener(v -> toggleOpenClose());
    }

    public FloatingActionMenu addSubFloatingActionButton(FloatingActionModel action, OnClickListener onClickListener, String tag) {
        mActions.add(action);
        ConstraintLayout row = (ConstraintLayout) mInflater.inflate(R.layout.floating_action_menu_row, this, false);
        row.setOnClickListener(v -> toggleOpenClose(new OnRowClickListener(v, onClickListener)));
        mRows.add(row);

        // Set the tag for the sub-button
        row.setTag(tag);

        return this;
    }

    public int getSize() {
        return mActions.size();
    }

    public FloatingActionMenu setMainButtonDrawableId(int resId) {
        if (floatingActionButton != null) {
            floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(), resId));
        }
        return this;
    }

    public void setColorFilter() {
        // Implement as needed
    }

    public FloatingActionMenu apply() {
        for (int index = 0; index < mRows.size(); index++) {
            ConstraintLayout it = mRows.get(index);
            ImageView imageView = it.findViewById(R.id.floating_action_menu_image);
            imageView.setImageResource(mActions.get(index).getIconResId());
            imageView.setColorFilter(Color.argb(255, 255, 255, 255));
            ViewCompat.setBackground(imageView, generateBackgroundShape(mActions.get(index).getIconBgColorResId()));
            TextView textView = it.findViewById(R.id.floating_action_menu_label);
            textView.setText(mActions.get(index).getLabelResId());
            if (getChildAt(index) != it) {
                addView(it, getDefaultParam());
            }
            it.setVisibility(View.GONE);
        }
        return this;
    }

    public FloatingActionMenu setDissmissOnTapOutside(boolean toDismiss) {
        dismissOnTapOutside = toDismiss;
        return this;
    }

    private Drawable generateBackgroundShape(@ColorRes int colorResId) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(ContextCompat.getColor(getContext(), colorResId));
        return drawable;
    }

    private float getAnimationHeight(int miniFabIndex) {
        float base = getResources().getDimension(R.dimen.fab_size_normal) - getResources().getDimension(R.dimen.space);
        float spacing = miniFabIndex * getResources().getDimension(R.dimen.fab_size_mini) + (miniFabIndex + 1) * getResources().getDimension(R.dimen.fab_menu_spacing);
        return -(base + spacing);
    }

    private FrameLayout.LayoutParams getDefaultParam() {
        int gravity = Gravity.BOTTOM | Gravity.END;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = getMargin();
        params.setMarginEnd(getMargin());
        params.gravity = gravity;
        return params;
    }

    private int getMargin() {
        return getResources().getDimensionPixelOffset(R.dimen.space);
    }

    private void toggleOpenClose() {
        toggleOpenClose(null);
    }

    private void toggleOpenClose(OnRowClickListener onRowClickListener) {
        isOpen = !isOpen;
        if (floatingActionButton != null) {
            floatingActionButton.animate().rotation(isOpen ? 135f : 0f);
        }
        setBackgroundColor(ContextCompat.getColor(getContext(), isOpen ? R.color.transparent : android.R.color.transparent));

        ConstraintLayout clickedLayout = null; // Declare a variable to store the clicked layout

        for (int index = 0; index < mRows.size(); index++) {
            ConstraintLayout it = mRows.get(index);
            if (isOpen) {
                showMenuRow(it, index);
            } else {
                hideMenuRow(it, onRowClickListener);
            }

            // Check if this layout was clicked and store it in the variable
            if (onRowClickListener != null && it == onRowClickListener.getView()) {
                clickedLayout = it;
            }
        }

        // Update currentClickedTag with the tag of the clicked layout
        if (clickedLayout != null) {
            currentClickedTag = clickedLayout.getTag().toString();
        } else {
            currentClickedTag = null;
        }
    }

    public String getCurrentClickedTag() {
        return currentClickedTag;
    }

    private void showMenuRow(ConstraintLayout layout, int index) {
        layout.setVisibility(View.VISIBLE);
        layout.animate().alpha(1f).translationY(getAnimationHeight(index));
        if (dismissOnTapOutside) {
            setOnClickListener(v -> toggleOpenClose());
        } else {
            setClickable(true);
        }
    }

    private void hideMenuRow(ConstraintLayout layout, OnRowClickListener onRowClickListener) {
        mAnimationCount.incrementAndGet();
        setOnClickListener(null);
        setClickable(false);

        layout.animate().translationY(0f).alpha(0f).withEndAction(() -> {
            layout.setVisibility(View.GONE);
            if (mAnimationCount.decrementAndGet() == 0) {
                if (onRowClickListener != null) {
                    onRowClickListener.performClick();
                }
            }
        });
    }

    private static class OnRowClickListener {
        private final View view;
        private final OnClickListener onClickListener;

        public OnRowClickListener(View view, OnClickListener onClickListener) {
            this.view = view;
            this.onClickListener = onClickListener;
        }

        public void performClick() {
            if (onClickListener != null) {
                onClickListener.onClick(view);
            }
        }

        public View getView() {
            return view;
        }
    }
}
