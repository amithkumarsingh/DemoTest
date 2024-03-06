package com.whitecoats.clinicplus.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.models.MedicineModel;
import com.whitecoats.model.PatientPListModel;

import java.util.ArrayList;
import java.util.List;

public class MedicineSearchAdapter extends ArrayAdapter<MedicineModel> {
    private Context context;
    private int resourceid;
    private List<MedicineModel> medicineModelList,suggestions,tempPatients;
    public MedicineSearchAdapter(@NonNull Context context, int resource, @NonNull List<MedicineModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resourceid = resource;
        this.medicineModelList = objects;
        this.suggestions = new ArrayList<>();
        this.tempPatients = new ArrayList<>(objects);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                view = inflater.inflate(resourceid, parent, false);
            }
            MedicineModel medicineModel = getItem(position);
            TextView name = (TextView) view.findViewById(R.id.patient_suggestion_name);
            name.setText(medicineModel.getMedicineName()+"("+medicineModel.getMedicineCompany()+")");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
    @Nullable
    @Override
    public MedicineModel getItem(int position) {
        return medicineModelList.get(position);
    }
    @Override
    public int getCount() {
        return medicineModelList.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @NonNull
    @Override
    public Filter getFilter() {
        return patientFilter;
    }
    private Filter patientFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            MedicineModel medicineModel = (MedicineModel) resultValue;
            return medicineModel.getMedicineName();
        }
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                suggestions.clear();
                for (MedicineModel medicineModel: tempPatients) {
                    if (medicineModel.getMedicineName().toLowerCase().startsWith(charSequence.toString().toLowerCase()) || medicineModel.getMedicineCompany().startsWith(charSequence.toString())) {
                        suggestions.add(medicineModel);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            ArrayList<MedicineModel> tempValues = (ArrayList<MedicineModel>) filterResults.values;
            if (filterResults != null && filterResults.count > 0) {
                clear();
                for (MedicineModel medicineModel : tempValues) {
                    add(medicineModel);
                }
                notifyDataSetChanged();
            } else {
                clear();
                notifyDataSetChanged();
            }
        }
    };
}
