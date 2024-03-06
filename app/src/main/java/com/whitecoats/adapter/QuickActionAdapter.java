package com.whitecoats.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;

import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.model.QuickActionViewItem;
import com.whitecoats.clinicplus.ActivityMoreClickListener;
import com.whitecoats.clinicplus.R;
import com.zoho.salesiqembed.ZohoSalesIQ;


public class QuickActionAdapter extends RecyclerView.Adapter<QuickActionItemHolder> {

    private List<QuickActionViewItem> carItemList;
    private Context mContext;
    private QuickActionViewItem carItem;
    private RelativeLayout quickLinkLayout;
    private ActivityMoreClickListener activityMoreListner;


    public QuickActionAdapter(List<QuickActionViewItem> carItemList, Context mContext, ActivityMoreClickListener activityMoreListner) {
        this.carItemList = carItemList;
        this.mContext = mContext;
        this.activityMoreListner = activityMoreListner;

    }

    @Override
    public QuickActionItemHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View carItemView = layoutInflater.inflate(R.layout.activity_quickaction_view_item, parent, false);
        final TextView carTitleView = (TextView) carItemView.findViewById(R.id.card_view_image_title);
        quickLinkLayout = (RelativeLayout) carItemView.findViewById(R.id.quickLinkLayout);
        QuickActionItemHolder ret = new QuickActionItemHolder(carItemView);
        return ret;
    }

    @Override
    public void onBindViewHolder(QuickActionItemHolder holder, final int position) {
        if (carItemList != null) {
            carItem = carItemList.get(position);

            if (carItem != null) {
                holder.getCarTitleText().setText(carItem.getQuickActionName());
                int icon;
                if (carItem.getQuickActionIcon() != null) {
                    icon = getId(carItem.getQuickActionIcon(), R.drawable.class);
                } else {
                    icon = getId("ic_home", R.drawable.class);
                }
                holder.getCarImageView().setImageResource(icon);

                if (carItem.getQuickActionHiddenForDoctorOnly() == 1 && ApiUrls.isDoctorOnly == 1) {
                    holder.getCarImageView().setColorFilter(mContext.getResources().getColor(R.color.colorGrey1), PorterDuff.Mode.SRC_IN);
                    holder.getCarTitleText().setTextColor(mContext.getResources().getColor(R.color.colorGrey1));

                }


            }


            quickLinkLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ZohoSalesIQ.Tracking.setCustomAction("HomeTab - Quick Action - " + carItemList.get(position).getQuickActionName());
                    if (carItemList.get(position).getQuickActionHiddenForDoctorOnly() == 1 && ApiUrls.isDoctorOnly == 1) {
                        Snackbar snackbar = Snackbar.make(quickLinkLayout, "Service not available", Snackbar.LENGTH_LONG);
                        snackbar.show();

                    } else {

                        activityMoreListner.onItemClick(v, position, carItemList.get(position).getQuickActionId(),"");
                    }

                }
            });


        }
    }

    @Override
    public int getItemCount() {
        int ret = 0;
        if (carItemList != null) {
            ret = carItemList.size();
        }
        return ret;
    }


    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e);
        }
    }

}
