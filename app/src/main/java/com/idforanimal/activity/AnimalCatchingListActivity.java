package com.idforanimal.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idforanimal.loadmore.RecyclerViewLoadMoreScroll;
import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.R;
import com.idforanimal.adapter.AnimalCatchingListAdapter;
import com.idforanimal.databinding.ActivityRecyclerviewListBinding;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.Catching;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.utils.ClickListener;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimalCatchingListActivity extends BaseActivity {

    private ActivityRecyclerviewListBinding binding;
    private AnimalCatchingListAdapter adapter;
    private final ArrayList<Catching> list = new ArrayList<>();
    private RecyclerViewLoadMoreScroll scrollListener;
    private int offset = 0, limit = 10;
    private String id = "", keyword = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecyclerviewListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = AnimalCatchingListActivity.this;
        init();
        setToolbar("Animal Catching");
        getList(false);
        checkLocationPermissions( permission -> turnGPSOn(this, permission1 -> checkLocationSettings()));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void init() {
        adapter = new AnimalCatchingListAdapter(context, list, new ClickListener() {
            @Override
            public void onItemSelected(int position) {

            }
        }, new ClickListener() {
            @Override
            public void onItemSelected(int position) {
                goToActivityForResult(new Intent(context, AddAnimalCatchingActivity.class).putExtra("details", list.get(position)), (data, resultCode) -> {
                    if (resultCode == Activity.RESULT_OK) {
                        if (data != null) {
                            if (data.hasExtra("details")) {
                                Catching model = (Catching) data.getSerializableExtra("details");
                                if (model != null) {
                                    list.set(position,model);
                                    if (adapter != null) adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
            }
        }, new ClickListener() {
            @Override
            public void onItemSelected(int position) {
                Common.confirmationDialog(context, getString(R.string.confirmation_remove), "No", "Yes", new Runnable() {
                    @Override
                    public void run() {
                        deleteData(position);
                    }
                });
            }
        });

        binding.recyclerView.setAdapter(adapter);
        scrollListener = Common.bindLoadMoreRecyclerView(binding.recyclerView, 1, RecyclerView.VERTICAL, new ClickListener() {
            @Override
            public void onLoadListener() {
                getList(true);
            }
        });

        binding.cvAdd.setOnClickListener(view -> goToActivityForResult(context, AddAnimalCatchingActivity.class, (data, resultCode) -> {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    if (data.hasExtra("details")) {
                        Catching model = (Catching) data.getSerializableExtra("details");
                        if (model != null) {
                            list.add(0, model);
                            if (adapter != null) adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }));

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
                        getList(false);
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
        binding.ivClear.setOnClickListener(v -> binding.etSearch.getText().clear());

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            getList(false);
        });
    }

    private void deleteData(int position) {
         if (ConnectivityReceiver.isConnected(context)) {
        Common.showProgressDialog(context, context.getString(R.string.please_wait));
        ApiInterface apiInterface = ApiUtils.getApiCalling();
        apiInterface.getDetails(Constant.removeCatching, list.get(position).getCatchingId(), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                Common.hideProgressDialog();
                try {
                    APIResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.getStatus() == 99) {
                            Common.logout(context, apiResponse.getMessage());
                        } else if (apiResponse.getStatus() == 1) {
                            list.remove(position);
                            if (adapter != null) adapter.notifyItemRemoved(position);
                        } else {
                            Common.showToast(apiResponse.getMessage());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
                Common.hideProgressDialog();
            }
        });
        } else {
            Common.noInternetDialog(this);
        }
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
        apiInterface.getList(Constant.CatchingList, Common.getCustomerId(),keyword, String.valueOf(limit), String.valueOf(offset), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
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
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
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

    @SuppressLint("NotifyDataSetChanged")
    private void bindListData(APIResponse apiResponse) {
        if (apiResponse.getCatchingList() != null) {
            offset = offset + apiResponse.getCatchingList().size();
            list.addAll(apiResponse.getCatchingList());
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }


}