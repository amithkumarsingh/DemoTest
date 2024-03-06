package com.whitecoats.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.model.PatientRecordsModel;
import com.whitecoats.clinicplus.PatientRecordListener;
import com.whitecoats.clinicplus.R;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class PatientRecordsAdapter extends RecyclerView.Adapter<PatientRecordsAdapter.MyViewHolder> {

    private List<PatientRecordsModel> patientRecordsModels;
    private Activity activity;
    private PatientRecordListener recordListener;
    private SharedPreferences appPreference;

    public PatientRecordsAdapter(List<PatientRecordsModel> patientRecordsModels, Activity activity, PatientRecordListener recordListener) {
        this.patientRecordsModels = patientRecordsModels;
        this.activity = activity;
        this.recordListener = recordListener;
        appPreference = activity.getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_records_category, viewGroup, false);
        return new PatientRecordsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        final PatientRecordsModel recordsModel = patientRecordsModels.get(i);

        //show data of shared records
        if (recordsModel.getType() == 2) { //2 is for shared record data
            myViewHolder.recordLayout.setVisibility(View.VISIBLE);
            myViewHolder.recordEpisLayout.setVisibility(View.GONE);

            myViewHolder.recordCategoryName.setText(recordsModel.getRecordName());
            myViewHolder.recordCount.setText(recordsModel.getRecordCount() + "");

            myViewHolder.recordLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    recordListener.onItemClick(view, recordsModel.getRecordId() + "_" + recordsModel.getRecordName(), "TO_DETAILS",recordsModel.getRecordIdArray().toString());
                    recordListener.onItemClick(view, recordsModel.getRecordId() + "_" + recordsModel.getRecordName(), "TO_DETAILS", "");

                }
            });

//            myViewHolder.recordLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    myViewHolder.recordLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    showGuide(3, i, myViewHolder);
//                }
//            });
        } else {
            myViewHolder.recordLayout.setVisibility(View.GONE);
            myViewHolder.recordEpisLayout.setVisibility(View.VISIBLE);

            myViewHolder.recordEpisName.setText(recordsModel.getEpisodeName());

            myViewHolder.recordEpisLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recordListener.onItemClick(view, recordsModel.getEpisodeId() + "", "TO_EPISODE_DETAILS", "");
                }
            });

            myViewHolder.recordEpisShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recordListener.onItemClick(view, recordsModel.getEpisodeId() + "", "TO_SHARE_DATA", "");
                }
            });

            myViewHolder.recordEpisLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    myViewHolder.recordEpisLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    showGuide(1, i, myViewHolder);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return patientRecordsModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        //shared record
        private TextView recordCategoryName, recordCount;
        private RelativeLayout recordLayout;

        //record episodes
        private RelativeLayout recordEpisLayout;
        private TextView recordEpisName;
        private ImageButton recordEpisShare;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //shared data
            recordCategoryName = itemView.findViewById(R.id.recordCategoryNameTv);
            recordCount = itemView.findViewById(R.id.recordCategoryCountv);
            recordLayout = itemView.findViewById(R.id.recordCategoryLayout);

            //record Episode
            recordEpisLayout = itemView.findViewById(R.id.recordEpisodesLayout);
            recordEpisName = itemView.findViewById(R.id.recordEpisodesNameTv);
            recordEpisShare = itemView.findViewById(R.id.recordEpisodesShareBtn);
        }
    }

    private void showGuide(int section, final int position, final MyViewHolder myViewHolder) {
        if (position == 0) {
            switch (section) {
                case 1:
                    if (!appPreference.getBoolean("RecordsCase", false)) {
//                        new GuideView.Builder(activity)
//                                .setTitle("Patient's Cases")
//                                .setContentText("Tap to get more information on every case")
//                                .setTargetView(myViewHolder.recordEpisLayout)
//                                .setDismissType(DismissType.outside)
//                                .setGuideListener(new GuideListener() {
//                                    @Override
//                                    public void onDismiss(View view) {
//                                        showGuide(2, position, myViewHolder);
//                                        SharedPreferences.Editor editor = appPreference.edit();
//                                        editor.putBoolean("RecordsCase", true);
//                                        editor.commit();
//                                    }
//                                })
//                                .build()
//                                .show();

                        new MaterialIntroView.Builder(activity)
                                .enableDotAnimation(true)
                                .enableIcon(false)
                                .dismissOnTouch(true)
                                .setFocusGravity(FocusGravity.CENTER)
                                .setFocusType(Focus.NORMAL)
                                .setDelayMillis(50)
                                .enableFadeAnimation(true)
                                .setInfoText("Tap to get more information on every case")
                                .setShape(ShapeType.RECTANGLE)
                                .setTarget(myViewHolder.recordEpisLayout)
                                .setUsageId("intro_record_epiLayout") //THIS SHOULD BE UNIQUE ID
                                .setListener(new MaterialIntroListener() {
                                    @Override
                                    public void onUserClicked(String materialIntroViewId) {
                                        showGuide(2, position, myViewHolder);
                                        SharedPreferences.Editor editor = appPreference.edit();
                                        editor.putBoolean("RecordsCase", true);
                                        editor.commit();
                                    }
                                })
                                .show();


                    }
                    break;

                case 2:
//                    new GuideView.Builder(activity)
//                            .setTitle("Share With Patient")
//                            .setContentText("Share your cases with patient on the go")
//                            .setTargetView(myViewHolder.recordEpisShare)
//                            .setDismissType(DismissType.outside)
//                            .setGuideListener(new GuideListener() {
//                                @Override
//                                public void onDismiss(View view) {
//
//                                }
//                            })
//                            .build()
//                            .show();

                            new MaterialIntroView.Builder(activity)
                            .enableDotAnimation(true)
                            .enableIcon(false)
                            .dismissOnTouch(true)
                            .setFocusGravity(FocusGravity.CENTER)
                            .setFocusType(Focus.NORMAL)
                            .setDelayMillis(50)
                            .enableFadeAnimation(true)
                            .setInfoText("Share your cases with patient on the go")
                            .setShape(ShapeType.CIRCLE)
                            .setTarget(myViewHolder.recordEpisShare)
                            .setUsageId("intro_recordEpisShare") //THIS SHOULD BE UNIQUE ID
                            .setListener(new MaterialIntroListener() {
                                @Override
                                public void onUserClicked(String materialIntroViewId) {

                                }
                            })
                            .show();


                    break;

                case 3:
                    if (!appPreference.getBoolean("SharedRecords", false)) {
//                        new GuideView.Builder(activity)
//                                .setTitle("Shared By Patient")
//                                .setContentText("See the records shared by patients")
//                                .setTargetView(myViewHolder.recordEpisShare)
//                                .setDismissType(DismissType.outside)
//                                .setGuideListener(new GuideListener() {
//                                    @Override
//                                    public void onDismiss(View view) {
//                                        SharedPreferences.Editor editor = appPreference.edit();
//                                        editor.putBoolean("SharedRecords", true);
//                                        editor.commit();
//                                    }
//                                })
//                                .build()
//                                .show();

                                new MaterialIntroView.Builder(activity)
                                .enableDotAnimation(true)
                                .enableIcon(false)
                                .dismissOnTouch(true)
                                .setFocusGravity(FocusGravity.CENTER)
                                .setFocusType(Focus.NORMAL)
                                .setDelayMillis(50)
                                .enableFadeAnimation(true)
                                .setInfoText("See the records shared by patients")
                                .setShape(ShapeType.CIRCLE)
                                .setTarget(myViewHolder.recordEpisShare)
                                .setUsageId("intro_recordEpisShared") //THIS SHOULD BE UNIQUE ID
                                .setListener(new MaterialIntroListener() {
                                    @Override
                                    public void onUserClicked(String materialIntroViewId) {
                                        SharedPreferences.Editor editor = appPreference.edit();
                                        editor.putBoolean("SharedRecords", true);
                                        editor.commit();
                                    }
                                })
                                .show();



                    }
                    break;
            }
        }
    }
}
