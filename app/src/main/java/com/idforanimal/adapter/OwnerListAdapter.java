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
import com.idforanimal.databinding.RecyclerviewOwnerListItemBinding;
import com.idforanimal.model.AnimalOwner;
import com.idforanimal.utils.ClickListener;
import com.idforanimal.utils.Constant;
import com.idforanimal.utils.Session;

import java.util.ArrayList;

public class OwnerListAdapter extends RecyclerView.Adapter<OwnerListAdapter.MyViewHolder> {

    final int VIEW_TYPE_ITEM = 0;
    final int VIEW_TYPE_LOADING = 1;
    private Context context;
    private Session session;
    private String id = "";
    private ArrayList<AnimalOwner> list;
    private ArrayList<AnimalOwner> filterList;
    private ClickListener listener, listener2, listener3, listener4;
    private int type = 0; // 0 Vertical : 1 Horizontal

    public OwnerListAdapter(Context context, ArrayList<AnimalOwner> list, ClickListener listener) {
        this.context = context;
        this.session = new Session(context);
        this.list = list;
        this.filterList = list;
        this.listener = listener;
    }

    public OwnerListAdapter(Context context, String id, ArrayList<AnimalOwner> list, ClickListener listener) {
        this.context = context;
        this.session = new Session(context);
        this.id = id;
        this.list = list;
        this.filterList = list;
        this.listener = listener;
    }

    public OwnerListAdapter(Context context, String id, int type, ArrayList<AnimalOwner> list, ClickListener listener) {
        this.context = context;
        this.session = new Session(context);
        this.id = id;
        this.type = type;
        this.list = list;
        this.filterList = list;
        this.listener = listener;
    }

    public OwnerListAdapter(Context context, ArrayList<AnimalOwner> list, ClickListener listener, ClickListener listener2) {
        this.context = context;
        this.session = new Session(context);
        this.list = list;
        this.filterList = list;
        this.listener = listener;
        this.listener2 = listener2;
    }

    public OwnerListAdapter(Context context, ArrayList<AnimalOwner> list, ClickListener listener, ClickListener listener2, ClickListener listener3) {
        this.context = context;
        this.session = new Session(context);
        this.list = list;
        this.filterList = list;
        this.listener = listener;
        this.listener2 = listener2;
        this.listener3 = listener3;
    }

    public OwnerListAdapter(Context context, ArrayList<AnimalOwner> list, ClickListener listener, ClickListener listener2, ClickListener listener3, ClickListener listener4) {
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
            listHolder = new MyViewHolder(RecyclerviewOwnerListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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

        final AnimalOwner model = filterList.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            if (holder instanceof MyViewHolder) {

                final MyViewHolder mainHolder = holder;

                if (type == Constant.listTypeHorizontal) {
                    mainHolder.binding.cvMain.getLayoutParams().width = (int) context.getResources().getDimension(com.intuit.sdp.R.dimen._80sdp);
                }

                if (id.isEmpty()) {
                    mainHolder.binding.tvTitle.setText(model.getFirstName() + " " + model.getMiddleName() + " " + model.getLastName());
                } else {
                    mainHolder.binding.tvTitle.setText(model.getNgoName());
                }
                mainHolder.binding.tvZoneWard.setVisibility(View.GONE);
                if (model.getZoneName() != null && model.getWardName() != null) {
                    if (!model.getZoneName().isEmpty() && !model.getWardName().isEmpty()) {
                        mainHolder.binding.tvZoneWard.setText(model.getZoneName() + "  -  " + model.getWardName());
                        mainHolder.binding.tvZoneWard.setVisibility(View.VISIBLE);
                    }
                }
                mainHolder.binding.llContact.setVisibility(View.GONE);
                if (model.getContact() != null) {
                    if (!model.getContact().isEmpty()) {
                        mainHolder.binding.tvContact.setText(model.getContact());
                        mainHolder.binding.llContact.setVisibility(View.VISIBLE);
                    }
                }

                mainHolder.binding.cvMain.setOnClickListener(v -> listener.onItemSelected(position));
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

        private RecyclerviewOwnerListItemBinding binding;
        private ProgressbarBinding progressbarBinding;

        MyViewHolder(RecyclerviewOwnerListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        MyViewHolder(ProgressbarBinding progressbarBinding) {
            super(progressbarBinding.getRoot());
            this.progressbarBinding = progressbarBinding;
        }
    }
}
