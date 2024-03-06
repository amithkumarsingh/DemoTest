package com.whitecoats.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.whitecoats.model.CommunicationMessageListModel;
import com.whitecoats.clinicplus.ActivityMoreClickListener;
import com.whitecoats.clinicplus.R;

public class CommunicationLanguageListAdapter extends RecyclerView.Adapter<CommunicationLanguageListAdapter.MyViewHolder> {

    private List<CommunicationMessageListModel> communicationLanguageModelList;
    private Context mContext;
    private ArrayList<Integer> languageSelectArray;
    private int selectCount;
    public static List list;
    private ActivityMoreClickListener activityMoreListner;


    public CommunicationLanguageListAdapter(List<CommunicationMessageListModel> communicationLanguageModelList, Context mContext, ActivityMoreClickListener activityMoreListner) {
        this.communicationLanguageModelList = communicationLanguageModelList;
        this.mContext = mContext;
        languageSelectArray = new ArrayList<Integer>();
        selectCount = 0;
        list = new ArrayList();
        this.activityMoreListner = activityMoreListner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row_communication_language, viewGroup, false);
        return new CommunicationLanguageListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        final CommunicationMessageListModel communicationDetailsListModel = communicationLanguageModelList.get(i);

        myViewHolder.languageName.setText(communicationDetailsListModel.getLanguageName());
        myViewHolder.checkImage.setColorFilter(mContext.getResources().getColor(R.color.colorGrey2), PorterDuff.Mode.SRC_IN);
        if (communicationDetailsListModel.isSelected()) {
            myViewHolder.commLanguageCardViewLayout.setCardBackgroundColor(mContext.getResources().getColor(R.color.lightPurple));
            myViewHolder.checkImage.setColorFilter(mContext.getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN);
            myViewHolder.languageName.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
        } else {
            myViewHolder.commLanguageCardViewLayout.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
            myViewHolder.checkImage.setColorFilter(mContext.getResources().getColor(R.color.colorGrey2), PorterDuff.Mode.SRC_IN);
            myViewHolder.languageName.setTextColor(mContext.getResources().getColor(R.color.colorGrey2));
        }
        myViewHolder.commLanguageCardViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                communicationDetailsListModel.setSelected(!communicationDetailsListModel.isSelected());
                if (communicationDetailsListModel.isSelected()) {
                    myViewHolder.commLanguageCardViewLayout.setCardBackgroundColor(mContext.getResources().getColor(R.color.lightPurple));
                    myViewHolder.checkImage.setColorFilter(mContext.getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN);
                    myViewHolder.languageName.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                    selectCount++;
                    //Adding values to the ArrayList
                    list.add(i);
                } else {
                    myViewHolder.commLanguageCardViewLayout.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
                    myViewHolder.checkImage.setColorFilter(mContext.getResources().getColor(R.color.colorGrey2), PorterDuff.Mode.SRC_IN);
                    myViewHolder.languageName.setTextColor(mContext.getResources().getColor(R.color.colorGrey2));
                    selectCount--;
                    for (int j = 0; j < list.size(); j++) {

                        if (list.get(j).toString().equalsIgnoreCase(String.valueOf(i))) {
                            list.remove(j);
                        }
                    }
                }



                activityMoreListner.onItemClick(view, i, 0,"");


            }
        });


    }

    @Override
    public int getItemCount() {
        return communicationLanguageModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView languageName;
        private ImageView checkImage;
        final CardView commLanguageCardViewLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            languageName = itemView.findViewById(R.id.commLanguageListLanguageName);
            checkImage = itemView.findViewById(R.id.commLanguageCheckBox);
            commLanguageCardViewLayout = itemView.findViewById(R.id.commLanguageListCardViewLayout);
        }
    }
}
