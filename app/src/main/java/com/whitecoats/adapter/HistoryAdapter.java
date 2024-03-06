package com.whitecoats.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.whitecoats.model.HistoryModel;
import com.whitecoats.clinicplus.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private List<HistoryModel> historyModelList;
    private Activity activity;

    public HistoryAdapter(List<HistoryModel> historyModelList, Activity activity) {
        this.historyModelList = historyModelList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row_history, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHoler, int i) {

        HistoryModel historyModel = historyModelList.get(i);

            myViewHoler.detailText.setText(historyModel.getDetailText());

            if(historyModel.getType().equals("note")) {
                myViewHoler.typeColor.setBackgroundColor(activity.getResources().getColor(R.color.colorSuccess));
                myViewHoler.actionText.setText(activity.getResources().getString(R.string.view));
                myViewHoler.actionText.setTextColor(activity.getResources().getColor(R.color.colorSuccess));
            } else if(historyModel.getType().equals("book")) {
                myViewHoler.typeColor.setBackgroundColor(activity.getResources().getColor(R.color.colorPurple));
                myViewHoler.actionText.setText(activity.getResources().getString(R.string.view));
                myViewHoler.actionText.setTextColor(activity.getResources().getColor(R.color.colorPurple));
            } else if(historyModel.getType().equals("record")) {
                myViewHoler.typeColor.setBackgroundColor(activity.getResources().getColor(R.color.colorPink));
                myViewHoler.actionText.setText(activity.getResources().getString(R.string.view));
                myViewHoler.actionText.setTextColor(activity.getResources().getColor(R.color.colorPink));
            }
    }

    @Override
    public int getItemCount() {
        return historyModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView detailText, actionText;
        public View typeColor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            detailText = itemView.findViewById(R.id.historyDetailsText);
            actionText = itemView.findViewById(R.id.historyActionBtn);
            typeColor = itemView.findViewById(R.id.historyTypeColor);
        }
    }
}
