package com.idforanimal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.R;
import com.idforanimal.adapter.ReleaseListAdapter;
import com.idforanimal.databinding.ActivityRecyclerviewListBinding;
import com.idforanimal.loadmore.RecyclerViewLoadMoreScroll;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.AnimalOwner;
import com.idforanimal.model.Release;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.utils.ClickListener;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReleaseAnimalListActivity extends BaseActivity {

    private ActivityRecyclerviewListBinding binding;
    private ReleaseListAdapter adapter;
    private ArrayList<Release> list = new ArrayList<>();
    private RecyclerViewLoadMoreScroll scrollListener;
    private int offset = 0, limit = 10;
    private String id = "", keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecyclerviewListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = ReleaseAnimalListActivity.this;

        setToolbar("Release Animal list");
        if (getIntent() != null) {
            if (getIntent().hasExtra("details")) {
                AnimalOwner model = (AnimalOwner) getIntent().getSerializableExtra("details");
                id = model.getOwnerId();
            }
        }

        adapter = new ReleaseListAdapter(context, list, new ClickListener() {
            @Override
            public void onItemSelected(int position) {
                goToActivityForResult(new Intent(context, AddAnimalReleaseActivity.class).putExtra("details", new Gson().toJson(list.get(position))), (data, resultCode) -> {
                    if (resultCode == Activity.RESULT_OK) {
                        if (data != null) {
                            if (data.hasExtra("details")) {
                                getList(false);
                            }
                        }
                    }
                });
            }
        }, new ClickListener() {
            @Override
            public void onItemSelected(int position) {
                deleteData(position);
            }
        });
        init();
        getList(false);
        checkLocationPermissions(permission -> turnGPSOn(this, permission1 -> checkLocationSettings()));
    }

    private void init() {
        binding.llSearch.setVisibility(View.GONE);
        binding.cvAdd.setOnClickListener(view -> goToActivityForResult(context, AddAnimalReleaseActivity.class, (data, resultCode) -> {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    if (data.hasExtra("details")) {
                        Release model = (Release) data.getSerializableExtra("details");
                        if (model != null) {
                           getList(false);
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
            apiInterface.getList(Constant.getAllReleaseList, id, String.valueOf(limit), String.valueOf(offset), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

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
        if (apiResponse.getReleaseArray() != null) {
            offset = offset + apiResponse.getReleaseArray().size();
            list.addAll(apiResponse.getReleaseArray());
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void deleteData(int position) {
        if (ConnectivityReceiver.isConnected(context)) {
            Common.showProgressDialog(context, context.getString(R.string.please_wait));

            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getDetails(Constant.removeReleaseAnimal, list.get(position).getReleaseId(), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    Common.hideProgressDialog();

                    try {
                        APIResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.getStatus() == 99) {
                                Common.logout(context, apiResponse.getMessage());
                            } else {
                                if (apiResponse.getStatus() == 1) {
                                    list.remove(position);
                                    if (adapter != null) adapter.notifyItemRemoved(position);
                                }
                                Common.showToast(apiResponse.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<APIResponse> call, Throwable t) {
                    t.printStackTrace();
                    Common.hideProgressDialog();
                }
            });
        } else {
            Common.noInternetDialog(this);
        }
    }
}
