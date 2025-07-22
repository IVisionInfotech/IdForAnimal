package com.idforanimal.activity.audit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.idforanimal.R;
import com.idforanimal.activity.BaseActivity;
import com.idforanimal.adapter.AnimalAuditListAdapter;
import com.idforanimal.databinding.ActivityRecyclerviewListBinding;
import com.idforanimal.model.APIResponse;
import com.idforanimal.model.AuditViewModel;
import com.idforanimal.retrofit.ApiInterface;
import com.idforanimal.retrofit.ApiUtils;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.utils.ClickListener;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuditActivity extends BaseActivity {

    private ActivityRecyclerviewListBinding binding;
    private AnimalAuditListAdapter adapter;
    private final ArrayList<AuditViewModel> list = new ArrayList<>();
    private int offset = 0, limit = 10;
    private String id = "", keyword = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecyclerviewListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        activity = this;

        setToolbar("Audit");
        init();
        getList(false);
    }

    private void init() {
        binding.cardSearch.setVisibility(View.GONE);
        binding.cvAdd.setOnClickListener(v -> goToActivityForResult(new Intent(context, AddAuditActivity.class), (data, resultCode) -> {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    getList(false);
                }
            }
        }));

        adapter = new AnimalAuditListAdapter(context, list, new ClickListener() {
            @Override
            public void onItemSelected(int position) {
                goToActivityForResult(new Intent(context, ViewAuditAnimalActivity.class).putExtra("id", new Gson().toJson(list.get(position))), (data, resultCode) -> {
                    getList(false);
                });
            }
        }, new ClickListener() {
            @Override
            public void onItemSelected(int position) {
                AuditViewModel model = list.get(position);
                goToActivityForResult(new Intent(context, AddAuditActivity.class).putExtra("id", new Gson().toJson(model)), (data, resultCode) -> {
                    if (resultCode == Activity.RESULT_OK) {
                        if (data != null) {
                            getList(false);
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
        }, new ClickListener() {
            @Override
            public void onItemSelected(int position) {
                getExportData(list.get(position).getAuditId());
            }
        });

        Common.bindLoadMoreRecyclerView(binding.recyclerView, 1, RecyclerView.VERTICAL, new ClickListener() {
        });
        binding.recyclerView.setAdapter(adapter);

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            getList(false);
        });
    }

    private void getExportData(String auditId) {
        String fileUrl = "https://idforanimal.com/API/V2/export_audit_animal.php?customerId=" + Common.getCustomerId() + "&auditId=" + auditId;
        String fileName = "audit_animal_data.csv";

        downloadCSV(fileUrl, fileName);
    }

    private void downloadCSV(String fileUrl, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setTitle("Downloading CSV");
        request.setDescription("Fetching data...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Common.showToast("Download started...");
        } else {
            Common.showToast("Download Manager not available");
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
            apiInterface.getList(Constant.auditList, Common.getCustomerId(), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {
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
        if (apiResponse.getAuditList() != null) {
            list.addAll(apiResponse.getAuditList());
            checkData();
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void checkData() {
        boolean isOpen = false;
        for (AuditViewModel model : list) {
            if (model.getStatus().equals("0")) {
                isOpen = true;
                break;
            }
        }

        if (isOpen) {
            binding.cvAdd.setVisibility(View.GONE);
        } else {
            binding.cvAdd.setVisibility(View.VISIBLE);
        }
    }

    private void deleteData(int position) {
        if (ConnectivityReceiver.isConnected(context)) {
            Common.showProgressDialog(context, context.getString(R.string.please_wait));
            ApiInterface apiInterface = ApiUtils.getApiCalling();
            apiInterface.getDetails(Constant.removeAudit, list.get(position).getAuditId(), Common.getUsername(), Common.getPassword(), Common.getType()).enqueue(new Callback<APIResponse>() {

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
                                checkData();
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
}