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

import com.idforanimal.R;
import com.idforanimal.databinding.ProgressbarBinding;
import com.idforanimal.databinding.RecyclerviewAnimalListItemBinding;
import com.idforanimal.model.Animal;
import com.idforanimal.utils.ClickListener;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;
import com.idforanimal.utils.Session;

import java.util.ArrayList;

public class AuditAnimalListAdapter extends RecyclerView.Adapter<AuditAnimalListAdapter.MyViewHolder> {

    final int VIEW_TYPE_ITEM = 0;
    final int VIEW_TYPE_LOADING = 1;
    private Context context;
    private Session session;
    private String id = "";
    private boolean isShow = true;
    private ArrayList<Animal> list;
    private ArrayList<Animal> filterList;
    private ClickListener listener, listener2, listener3, listener4;
    private int type = 0; // 0 Vertical : 1 Horizontal

    public AuditAnimalListAdapter(Context context, ArrayList<Animal> list, ClickListener listener) {
        this.context = context;
        this.session = new Session(context);
        this.list = list;
        this.filterList = list;
        this.listener = listener;
    }

    public AuditAnimalListAdapter(Context context, String id, ArrayList<Animal> list, ClickListener listener) {
        this.context = context;
        this.session = new Session(context);
        this.id = id;
        this.list = list;
        this.filterList = list;
        this.listener = listener;
    }

    public AuditAnimalListAdapter(Context context, boolean isShow, ArrayList<Animal> list, ClickListener listener) {
        this.context = context;
        this.session = new Session(context);
        this.isShow = isShow;
        this.list = list;
        this.filterList = list;
        this.listener4 = listener;
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
            listHolder = new MyViewHolder(RecyclerviewAnimalListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == VIEW_TYPE_LOADING) {
            listHolder = new MyViewHolder(ProgressbarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            return listHolder;
        }
        return listHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final Animal model = filterList.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            if (holder instanceof MyViewHolder) {

                final MyViewHolder mainHolder = holder;

                if (type == Constant.listTypeHorizontal) {
                    mainHolder.binding.cvMain.getLayoutParams().width = (int) context.getResources().getDimension(com.intuit.sdp.R.dimen._80sdp);
                }

                mainHolder.binding.tvRFID.setText(
                        !TextUtils.isEmpty(model.getRfidTagNo()) ? model.getRfidTagNo() : "-"
                );
                mainHolder.binding.tvVisualTag.setText(
                        !TextUtils.isEmpty(model.getVisualTagNo()) ? model.getVisualTagNo() : "-"
                );

                StringBuilder stringBuilder = new StringBuilder();
                if (!TextUtils.isEmpty(model.getAnimalName())) {
                    stringBuilder.append(model.getAnimalName());
                }
                if (!TextUtils.isEmpty(model.getCattleName())) {
                    if (stringBuilder.length() > 0) stringBuilder.append("-");
                    stringBuilder.append(model.getCattleName());
                }
                mainHolder.binding.tvAnimalType.setText(
                        stringBuilder.length() > 0 ? stringBuilder.toString() : "-"
                );

                mainHolder.binding.ivMore.setVisibility(isShow ? View.VISIBLE : View.GONE);
                mainHolder.binding.ivDelete.setVisibility(isShow ? View.GONE : View.VISIBLE);
                mainHolder.binding.llTaggingDate.setVisibility(isShow ? View.VISIBLE : View.GONE);
                mainHolder.binding.tvTitle.setVisibility(isShow ? View.VISIBLE : View.GONE);
                mainHolder.binding.llCatching.setVisibility(isShow ? View.GONE : View.VISIBLE);
                mainHolder.binding.llTreatment.setVisibility(isShow ? View.GONE : View.VISIBLE);
                mainHolder.binding.llVaccination.setVisibility(isShow ? View.GONE : View.VISIBLE);

                if (!TextUtils.isEmpty(model.getValidDate()) && !"0000-00-00".equals(model.getValidDate())) {
                    mainHolder.binding.llValidDate.setVisibility(View.VISIBLE);
                    mainHolder.binding.tvValidDate.setText(Common.changeDateFormat(model.getValidDate()));
                } else {
                    mainHolder.binding.llValidDate.setVisibility(View.GONE);
                }

                if (!isShow) {
                    mainHolder.binding.ivCall.setVisibility(View.GONE);
                    mainHolder.binding.tvCatching.setText(
                            Common.getLastDetail(model.getCatchingLocation(), model.getTotalCatching(), "CATCHING")
                    );
                    mainHolder.binding.tvTreatment.setText(
                            Common.getLastDetail(model.getTreatmentData(), model.getTotalTreatment(), "TREATMENT")
                    );
                    mainHolder.binding.tvVaccination.setText(
                            Common.getLastDetail(model.getVaccinationData(), model.getTotalVaccination(), "VACCINATION")
                    );
                } else {
                    mainHolder.binding.ivCall.setVisibility(View.GONE);
                    mainHolder.binding.tvTitle.setText(
                            Common.buildText(
                                    !TextUtils.isEmpty(model.getName()) ? model.getName() : "-",
                                    !TextUtils.isEmpty(model.getContact()) ? model.getContact() : "-"
                            )
                    );
                    mainHolder.binding.tvTaggingDate.setText(
                            !TextUtils.isEmpty(model.getTaggingDate()) ? Common.changeDateFormat(model.getTaggingDate()) : "-"
                    );

                    mainHolder.binding.ivCall.setOnClickListener(v -> {
                        if (!TextUtils.isEmpty(model.getContact())) {
                            Intent phone_intent = new Intent(Intent.ACTION_DIAL);
                            phone_intent.setData(Uri.parse("tel:" + model.getContact()));
                            context.startActivity(phone_intent);
                        }
                    });
                }
                mainHolder.binding.ivMore.setVisibility(View.GONE);
                mainHolder.binding.cvMain.setOnClickListener(v -> {
                    if (listener != null) listener.onItemSelected(position, 0);
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

        private RecyclerviewAnimalListItemBinding binding;
        private ProgressbarBinding progressbarBinding;

        MyViewHolder(RecyclerviewAnimalListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        MyViewHolder(ProgressbarBinding progressbarBinding) {
            super(progressbarBinding.getRoot());
            this.progressbarBinding = progressbarBinding;
        }
    }

}
