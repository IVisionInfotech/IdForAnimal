package com.idforanimal.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idforanimal.databinding.OfflineListItemBinding;
import com.idforanimal.databinding.ProgressbarBinding;
import com.idforanimal.databinding.RecyclerviewAnimalListItemBinding;
import com.idforanimal.databinding.SpinnerListItemBinding;
import com.idforanimal.model.Animal;
import com.idforanimal.model.OfflineAnimalData;
import com.idforanimal.utils.ClickListener;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;
import com.idforanimal.utils.Session;

import java.util.ArrayList;
import java.util.List;

public class OfflineAnimalListAdapter extends RecyclerView.Adapter<OfflineAnimalListAdapter.MyViewHolder> {

    final int VIEW_TYPE_ITEM = 0;
    final int VIEW_TYPE_LOADING = 1;
    private Context context;
    private Session session;
    private String id = "";
    private boolean isShow = true;
    private ArrayList<OfflineAnimalData> list;
    private ArrayList<OfflineAnimalData> filterList;
    private ClickListener listener, listener2, listener3, listener4;
    private int type = 0; // 0 Vertical : 1 Horizontal

    public OfflineAnimalListAdapter(Context context, ArrayList<OfflineAnimalData> list, ClickListener listener) {
        this.context = context;
        this.session = new Session(context);
        this.list = list;
        this.filterList = list;
        this.listener = listener;
    }


    public void addLoadingView() {
        new Handler().post(() -> {
            filterList.add(null);
            notifyItemInserted(filterList.size() - 1);
        });
    }

    public void removeLoadingView() {
        if (!filterList.isEmpty()) {
            filterList.remove(filterList.size() - 1);
            notifyItemRemoved(filterList.size());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder listHolder = null;
        if (viewType == VIEW_TYPE_ITEM) {
            listHolder = new MyViewHolder(OfflineListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == VIEW_TYPE_LOADING) {
            listHolder = new MyViewHolder(ProgressbarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            return listHolder;
        }
        return listHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final OfflineAnimalData model = filterList.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            if (holder instanceof MyViewHolder) {
                final MyViewHolder mainHolder = holder;
                int no = position + 1;
                mainHolder.binding.tvSrNo.setText(String.valueOf(no));
                mainHolder.binding.tvText.setText(model.getMicroChipNo());

                mainHolder.binding.ivClose.setOnClickListener(v -> {
                    if (listener != null){
                        listener.onItemSelected(position);
                    }
                });

            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return filterList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return (null != filterList ? filterList.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private OfflineListItemBinding binding;
        private ProgressbarBinding progressbarBinding;

        MyViewHolder(OfflineListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        MyViewHolder(ProgressbarBinding progressbarBinding) {
            super(progressbarBinding.getRoot());
            this.progressbarBinding = progressbarBinding;
        }
    }

}
