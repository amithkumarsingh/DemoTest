package com.whitecoats.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.whitecoats.model.HomeCommunicationListModel;
import com.whitecoats.clinicplus.R;

public class HomeCommunicationListAdapter extends RecyclerView.Adapter<HomeCommunicationListAdapter.MyViewHolder> {

    private List<HomeCommunicationListModel> homeCommunicationListModelList;

    public HomeCommunicationListAdapter(List<HomeCommunicationListModel> communicationListModelList) {
        this.homeCommunicationListModelList = communicationListModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row_home_communication, viewGroup, false);

        return new HomeCommunicationListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        HomeCommunicationListModel communicationListModel = homeCommunicationListModelList.get(i);

        myViewHolder.title.setText(communicationListModel.getTitle());
        myViewHolder.description.setText(communicationListModel.getDescription());

        if(communicationListModel.getContentType().equalsIgnoreCase("video"))
        {
            myViewHolder.typeIcon.setImageResource(R.drawable.ic_video);
        } else if(communicationListModel.getContentType().equalsIgnoreCase("video"))
        {
            myViewHolder.typeIcon.setImageResource(R.drawable.ic_notes);

        }




    }

    @Override
    public int getItemCount() {
        return homeCommunicationListModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title, description, more;
        public ImageView typeIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.homeCommListTitle);
            description = itemView.findViewById(R.id.homeCommListDesp);
            more = itemView.findViewById(R.id.homeCommListMore);
            typeIcon = itemView.findViewById(R.id.homeCommListIcon);

        }
    }
}
