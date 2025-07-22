package com.idforanimal.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.idforanimal.R;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;
import com.idforanimal.utils.OnSyncPageChangeListener;

import java.util.ArrayList;
import java.util.List;

public class ImagePreviewActivity extends BaseActivity {

    private ViewPager photoPreviewPager, photoViewPager;
    private ArrayList<String> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        context = ImagePreviewActivity.this;

        Constant._hasLoadedOnce = false;

        setToolbar("");

        init();

        if (getIntent() != null) {
            if (getIntent().hasExtra("list")) {
                imageList = getIntent().getStringArrayListExtra("list");
                setViewPager();
            }
        }
    }

    private void init() {

//        ivBack.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        photoPreviewPager = findViewById(R.id.photoPreviewPager);
        photoViewPager = findViewById(R.id.photoViewPager);
    }

    private void setViewPager() {

        final PhotoPreviewAdapter photoPreviewAdapter = new PhotoPreviewAdapter(getSupportFragmentManager(), imageList);
        final PhotoViewAdapter photoViewAdapter = new PhotoViewAdapter(getSupportFragmentManager(), imageList);

        photoPreviewPager.setAdapter(photoPreviewAdapter);
        photoPreviewPager.addOnPageChangeListener(new OnSyncPageChangeListener(photoViewPager, photoPreviewPager));

        photoViewPager.setAdapter(photoViewAdapter);
        photoViewPager.addOnPageChangeListener(new OnSyncPageChangeListener(photoPreviewPager, photoViewPager));
    }

    public class PhotoPreviewAdapter extends FragmentPagerAdapter {

        private static final int DEFAULT_SIDE_PREVIEW_COUNT = 3;
        private final int sidePreviewCount;
        private final List<String> imageList;

        public PhotoPreviewAdapter(FragmentManager fm, List<String> photoInfos) {
            this(fm, DEFAULT_SIDE_PREVIEW_COUNT, photoInfos);
        }

        public PhotoPreviewAdapter(FragmentManager fm, int sidePreviewCount, List<String> photoInfos) {
            super(fm);
            this.sidePreviewCount = sidePreviewCount;
            this.imageList = photoInfos;
        }

        public int getSidePreviewCount() {
            return sidePreviewCount;
        }

        @Override
        public Fragment getItem(int position) {
            if (isDummy(position)) {
                return DummyPreviewFragment.newInstance();
            } else {
                return PhotoPreviewFragment.newInstance(imageList.get(getRealPosition(position)));
            }
        }

        private boolean isDummy(int position) {
            return position < sidePreviewCount || position > imageList.size() - 1 + sidePreviewCount;
        }

        private int getRealPosition(int position) {
            return position - sidePreviewCount;
        }

        @Override
        public int getCount() {
            return imageList.size() + (sidePreviewCount * 2);
        }

        @Override
        public float getPageWidth(int position) {
            return 1.0f / getElementsPerPage();
        }

        private int getElementsPerPage() {
            return (sidePreviewCount * 2) + 1;
        }
    }

    public class PhotoViewAdapter extends FragmentPagerAdapter {

        private final List<String> imageList;

        public PhotoViewAdapter(FragmentManager fm, List<String> photoInfos) {
            super(fm);
            this.imageList = photoInfos;
        }

        @Override
        public Fragment getItem(int position) {
            return PhotoViewFragment.newInstance(imageList.get(position));
        }

        @Override
        public int getCount() {
            return imageList.size();
        }
    }

    public static class PhotoPreviewFragment extends Fragment {

        private String image;
        private ImageView ivImage;

        public static PhotoPreviewFragment newInstance(@NonNull String photoInfo) {
            final PhotoPreviewFragment fragment = new PhotoPreviewFragment();
            final Bundle args = new Bundle();
            args.putString(Common.ARG_PARAM1, photoInfo);
            fragment.setArguments(args);
            return fragment;
        }

        public PhotoPreviewFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            image = getArguments().getString(Common.ARG_PARAM1);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_photo_preview, container, false);

            ivImage = view.findViewById(R.id.ivImage);

            return view;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            Common.loadImage(getActivity(), ivImage, image);
        }
    }

    public static class PhotoViewFragment extends Fragment {

        private ImageView ivImage;
        private String image;

        public static PhotoViewFragment newInstance(@NonNull String image) {
            final PhotoViewFragment fragment = new PhotoViewFragment();
            final Bundle args = new Bundle();
            args.putSerializable(Common.ARG_PARAM1, image);
            fragment.setArguments(args);
            return fragment;
        }

        public PhotoViewFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            image = getArguments().getString(Common.ARG_PARAM1);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_photo_view, container, false);

            ivImage = view.findViewById(R.id.ivImage);

            return view;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            Common.loadImage(getActivity(), ivImage, image);
        }
    }

    public static class DummyPreviewFragment extends Fragment {

        public static DummyPreviewFragment newInstance() {
            return new DummyPreviewFragment();
        }

        public DummyPreviewFragment() {
        }

    }
}