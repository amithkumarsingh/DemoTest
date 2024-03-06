package com.whitecoats.clinicplus;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import com.whitecoats.adapter.CommunicationLanguageListAdapter;
import com.whitecoats.model.CommunicationMessageListModel;

/**
 * Created by vaibhav on 07-02-2018.
 */

public class CommunicationLanguageBottomSheet extends BottomSheetDialogFragment {
    private CreateVideoArticleActivity appointmentActivity;
    private RecyclerView patientListRView;
    private List<CommunicationMessageListModel> commLanguageModelList;
    private CommunicationLanguageListAdapter commLanguageListAdapter;
    private RelativeLayout cancel, done, searchLayout, addPatientLayout;
    private ImageView cancelIcon;
    private Context mContext;
    private FloatingActionButton fab;
    private FloatingActionButton addIcon;
    private int intValue;

    public CommunicationLanguageBottomSheet() { }

    public void setupConfig(CreateVideoArticleActivity appointmentActivity) {
        this.appointmentActivity = appointmentActivity;
        this.mContext = appointmentActivity;
        this.commLanguageModelList = new ArrayList<>();
        dummyData();
        //commLanguageListAdapter = new CommunicationLanguageListAdapter(commLanguageModelList, mContext);

        commLanguageListAdapter = new CommunicationLanguageListAdapter(commLanguageModelList, mContext, new ActivityMoreClickListener() {
            @Override
            public void onItemClick(View v, int position, int tt,String loadmore) {

            }
        });
    }
    //Bottom Sheet Callback
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(final Dialog dialog, int style) {
//        super.setupDialog(dialog, style);
        //Get the content View
        final View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet_comm_language_list, null);
        dialog.setContentView(contentView);
        //Set the coordinator layout behavior
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        cancelIcon = contentView.findViewById(R.id.bottomSheetCancelIcon);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        RecyclerView recList = contentView.findViewById(R.id.bottomSheetCardList);

        //Set callback
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        recList.setHasFixedSize(true);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        recList.setAdapter(commLanguageListAdapter);
        cancelIcon.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN);
        cancelIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
    }


    private void dummyData() {
        CommunicationMessageListModel temp = new CommunicationMessageListModel();
        temp.setLanguageName("Hindi");
        commLanguageModelList.add(temp);

        temp = new CommunicationMessageListModel();
        temp.setLanguageName("English");
        commLanguageModelList.add(temp);

        temp = new CommunicationMessageListModel();
        temp.setLanguageName("Marathi");
        commLanguageModelList.add(temp);
        // commDetailsListAdapter.notifyDataSetChanged();
    }

}