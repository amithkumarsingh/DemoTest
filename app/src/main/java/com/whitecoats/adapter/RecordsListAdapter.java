package com.whitecoats.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.whitecoats.model.RecordsModel;
import com.whitecoats.clinicplus.R;

public class RecordsListAdapter extends RecyclerView.Adapter<RecordsListAdapter.MyViewHolder>{

    private List<RecordsModel> recordsModelList;

    public RecordsListAdapter(List<RecordsModel> recordsModelList) {
        this.recordsModelList = recordsModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row_records_list, viewGroup, false);

        return new RecordsListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        RecordsModel recordsModel = recordsModelList.get(i);

    }

    @Override
    public int getItemCount() {
        return recordsModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        //written note varaible
        private TextView fileView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fileView = itemView.findViewById(R.id.recordNotesView);
        }
    }
}
