package com.idforanimal.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idforanimal.databinding.ProgressbarBinding;
import com.idforanimal.databinding.RecyclerviewAnimalAuditListItemBinding;
import com.idforanimal.databinding.RecyclerviewAnimalCatchingListItemBinding;
import com.idforanimal.model.AuditViewModel;
import com.idforanimal.model.Catching;
import com.idforanimal.utils.ClickListener;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;
import com.idforanimal.utils.Session;

import java.util.ArrayList;

public class AnimalAuditListAdapter extends RecyclerView.Adapter<AnimalAuditListAdapter.MyViewHolder> {

    final int VIEW_TYPE_ITEM = 0;
    final int VIEW_TYPE_LOADING = 1;
    private Context context;
    private Session session;
    private String id = "";
    private ArrayList<AuditViewModel> list;
    private ArrayList<AuditViewModel> filterList;
    private ClickListener listener, listener2, listener3, listener4;
    private int type = 0; // 0 Vertical : 1 Horizontal

    public AnimalAuditListAdapter(Context context, ArrayList<AuditViewModel> list, ClickListener listener, ClickListener listener2, ClickListener listener3) {
        this.context = context;
        this.session = new Session(context);
        this.list = list;
        this.filterList = list;
        this.listener = listener;
        this.listener2 = listener2;
        this.listener3 = listener3;
    }

    public AnimalAuditListAdapter(Context context, ArrayList<AuditViewModel> list, ClickListener listener, ClickListener listener2, ClickListener listener3, ClickListener listener4) {
        this.context = context;
        this.session = new Session(context);
        this.list = list;
        this.filterList = list;
        this.listener = listener;
        this.listener2 = listener2;
        this.listener3 = listener3;
        this.listener4 = listener4;
    }

    public void addLoadingView() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                filterList.add(null);
                notifyItemInserted(filterList.size() - 1);
            }
        });
    }

    public void removeLoadingView() {
        if (filterList.size() > 0) {
            filterList.remove(filterList.size() - 1);
            notifyItemRemoved(filterList.size());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder listHolder = null;
        if (viewType == VIEW_TYPE_ITEM) {
            listHolder = new MyViewHolder(RecyclerviewAnimalAuditListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == VIEW_TYPE_LOADING) {
            listHolder = new MyViewHolder(ProgressbarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            return listHolder;
        }
        MyViewHolder finalListHolder = listHolder;
        listHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                listener.onItemSelected(finalListHolder.getBindingAdapterPosition());
            }
        });
        return listHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final AuditViewModel model = filterList.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            if (holder instanceof MyViewHolder) {

                final MyViewHolder mainHolder = holder;

                mainHolder.binding.tvInstitution.setText(model.getAuditByInstitution());
                mainHolder.binding.tvStartDate.setText(Common.changeDateFormat(model.getStartDate()));
                mainHolder.binding.tvEndDate.setText(Common.changeDateFormat(model.getEndDate()));
                mainHolder.binding.tvPerson.setText(model.getAuditByPerson());

                if (model.isVisibleDeleted()){
                    mainHolder.binding.ivDelete.setVisibility(View.GONE);
                    mainHolder.binding.ivExport.setVisibility(View.VISIBLE);
                }

                if (model.getStatus().equals("1")){
                    mainHolder.binding.ivDelete.setVisibility(View.GONE);
                    mainHolder.binding.ivEdit.setVisibility(View.GONE);
                    mainHolder.binding.ivExport.setVisibility(View.VISIBLE);
                }

                mainHolder.binding.cvMain.setOnClickListener(v -> listener.onItemSelected(position));
                mainHolder.binding.ivEdit.setOnClickListener(v -> listener2.onItemSelected(position));
                mainHolder.binding.ivDelete.setOnClickListener(v -> listener3.onItemSelected(position));
                mainHolder.binding.ivExport.setOnClickListener(v -> listener4.onItemSelected(position));
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

        private RecyclerviewAnimalAuditListItemBinding binding;
        private ProgressbarBinding progressbarBinding;

        MyViewHolder(RecyclerviewAnimalAuditListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        MyViewHolder(ProgressbarBinding progressbarBinding) {
            super(progressbarBinding.getRoot());
            this.progressbarBinding = progressbarBinding;
        }
    }
}
