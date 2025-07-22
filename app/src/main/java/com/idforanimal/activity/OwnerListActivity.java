package com.idforanimal.activity;

import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.idforanimal.loadmore.RecyclerViewLoadMoreScroll;
import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.R;
import com.idforanimal.adapter.OwnerListAdapter;
import com.idforanimal.databinding.ActivityRecyclerviewListBinding;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.AnimalOwner;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.utils.ClickListener;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerListActivity extends BaseActivity {

    private ActivityRecyclerviewListBinding binding;
    private OwnerListAdapter adapter;
    private ArrayList<AnimalOwner> list = new ArrayList<>();
    private RecyclerViewLoadMoreScroll scrollListener;
    private int offset = 0, limit = 10;
    private String id = "", keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecyclerviewListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = OwnerListActivity.this;

        setToolbar("Owner list");

        adapter = new OwnerListAdapter(context, list, new ClickListener() {
            @Override
            public void onItemSelected(int position) {
                goToActivityForResult(new Intent(context, AddAnimalActivity.class).putExtra("ownerId", list.get(position).getOwnerId()), (data, resultCode) -> {
                    if (resultCode == Activity.RESULT_OK) {
                        if (data.hasExtra("details1")) {
                            setResultOfActivity(new Intent(), 1, true);
                        }
                        int count = 0;
                        if (list.get(position).getLiveAnimalCount() != null && (!list.get(position).getLiveAnimalCount().isEmpty())) {
                                count = Integer.parseInt(list.get(position).getLiveAnimalCount());
                        }
                        count = count + 1;
                        list.get(position).setLiveAnimalCount(String.valueOf(count));
                        if (adapter != null) adapter.notifyItemChanged(position);
                    }
                });
            }
        });

        init();

        getList(false);
        checkLocationPermissions( permission -> turnGPSOn(this, permission1 -> checkLocationSettings()));
    }

    private void init() {
        binding.cvAdd.setVisibility(View.GONE);
        binding.cvAdd.setOnClickListener(view -> goToActivityForResult(context, AddAnimalOwnerActivity.class, (data, resultCode) -> {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    if (data.hasExtra("details")) {
                        AnimalOwner model = (AnimalOwner) data.getSerializableExtra("details");
                        if (model != null) {
                            list.add(0, model);
                            if (adapter != null) adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }));

        binding.recyclerView.setAdapter(adapter);
        scrollListener = Common.bindLoadMoreRecyclerView(binding.recyclerView, 1, RecyclerView.VERTICAL, new ClickListener() {
            @Override
            public void onLoadListener() {
                getList(true);
            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    keyword = s.toString();
                    if (!s.toString().isEmpty()) {
                        binding.ivSearch.setVisibility(View.VISIBLE);
                        binding.ivClear.setVisibility(View.VISIBLE);
                    } else {
                        binding.ivSearch.setVisibility(View.GONE);
                        binding.ivClear.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.ivSearch.setOnClickListener(v -> {
            if (keyword.isEmpty()) {
                binding.etSearch.setError("Enter keyword");
            } else {
                getList(false);
            }
        });
        binding.ivClear.setOnClickListener(v -> {
            binding.etSearch.getText().clear();
            getList(false);
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            getList(false);
        });
    }

    private void getList(boolean loadMore) {
         if (ConnectivityReceiver.isConnected(context)) {
        if (!loadMore) {
            Common.showProgressDialog(context, context.getString(R.string.please_wait));
            offset = 0;
            limit = 10;
            list.clear();
        } else {
            adapter.addLoadingView();
        }

        ApiInterface apiInterface = ApiUtils.getApiCalling();
        apiInterface.getList(Constant.animalOwnerListUrl, Common.getCustomerId(), keyword, String.valueOf(limit), String.valueOf(offset), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<>() {

            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                if (loadMore) {
                    adapter.removeLoadingView();
                } else {
                    Common.hideProgressDialog();
                }

                try {
                    APIResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.getStatus() == 99) {
                            Common.logout(context, apiResponse.getMessage());
                        } else if (apiResponse.getStatus() == 1) {
                            bindListData(apiResponse);
                            scrollListener.setLoaded();
                            binding.recyclerView.setVisibility(View.VISIBLE);
                            binding.ivNotFound.setVisibility(View.GONE);
                        } else {
                            if (loadMore) {
                                Common.showToast(apiResponse.getMessage());
                            } else {
                                list.clear();
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                                binding.ivNotFound.setVisibility(View.VISIBLE);
                                binding.recyclerView.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                t.printStackTrace();
                if (loadMore) {
                    adapter.removeLoadingView();
                } else {
                    Common.hideProgressDialog();
                }
            }
        });
        } else {
            Common.noInternetDialog(this);
        }
    }

    private void bindListData(APIResponse apiResponse) {
        if (apiResponse.getAnimalOwnerList() != null) {
            offset = offset + apiResponse.getAnimalOwnerList().size();
            list.addAll(apiResponse.getAnimalOwnerList());
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }

}