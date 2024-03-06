package com.whitecoats.clinicplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.models.VoiceEMRFullDictationModel;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VoiceEMRFullDictationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    public List<VoiceEMRFullDictationModel> voiceEMRFullDictationModels;

    public VoiceEMRFullDictationListAdapter(Context context, List<VoiceEMRFullDictationModel> voiceEMRFullDictationModels) {
        this.context = context;
        this.voiceEMRFullDictationModels = voiceEMRFullDictationModels;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_full_dictation, parent, false);
        context = parent.getContext();
        return new VoiceEMRFullDictationListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder itemViewHolder = (VoiceEMRFullDictationListAdapter.MyViewHolder) holder;
        itemViewHolder.fullDictationText.setText( voiceEMRFullDictationModels.get(position).getFullDictationText());
    }

    @Override
    public int getItemCount() {
        return voiceEMRFullDictationModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fullDictationText;

        public MyViewHolder(View view) {
            super(view);
            fullDictationText = view.findViewById(R.id.fullDictationText);
        }
    }
}
