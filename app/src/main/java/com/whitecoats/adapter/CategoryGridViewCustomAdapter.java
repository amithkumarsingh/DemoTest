package com.whitecoats.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.whitecoats.model.AppointmentApptListModel;
import com.whitecoats.clinicplus.CategoryGridViewClickListener;
import com.whitecoats.clinicplus.PatientProfileActivity;
import com.whitecoats.clinicplus.R;

public class CategoryGridViewCustomAdapter extends BaseAdapter {

   // List<String> result;
    Context context;
    int [] imageId;
    public static List<AppointmentApptListModel> result;
    public CategoryGridViewClickListener catGridViewClickListner;


    private static LayoutInflater inflater=null;
    public CategoryGridViewCustomAdapter(Activity activity, List<AppointmentApptListModel> result,CategoryGridViewClickListener catGridViewClickListner) {
        // TODO Auto-generated constructor stub
        this.result=result;
        context=activity;
       // imageId=prgmImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.catGridViewClickListner = catGridViewClickListner;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.list_row_category_item, null);
        holder.tv=(TextView) rowView.findViewById(R.id.categogryName);
        holder.img=(ImageView) rowView.findViewById(R.id.categoryCancel);

        holder.tv.setText(result.get(position).getCategoryName());
       // holder.img.setImageResource(imageId[position]);

//        rowView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Toast.makeText(context, "You Clicked "+result.get(position), Toast.LENGTH_LONG).show();
//            }
//        });

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(context, "You Clicked "+result.get(position), Toast.LENGTH_LONG).show();
               // removeItem(position);
                catGridViewClickListner.onItemClick(view,position);


            }
        });

        return rowView;
    }

    public void removeItem(int position){
        result.remove(position);
        //Imageid.remove(position);
        notifyDataSetChanged();

        StringBuilder sb = new StringBuilder();
        for(int i=0;i<result.size();i++)
        {
            // JSONObject categoryObject = appointmentAssignCategory.getJSONObject(k);
            String str = result.get(i).getCategoryName();
            sb.append(str);
            sb.append(",");

        }
        if(sb.length()>0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        if(AppointmentApptListAdapter.flagAppointmentListAdp == 1)
        {
           // AppointmentApptListAdapter.MyViewHolder.apptListCatAssignText.setText(sb.toString());
          //  AppointmentApptListAdapter.MyViewHolder.apptListCatAssignText;
        }
        else {
            PatientProfileActivity.patientProCat.setText("Category: " + sb.toString());
        }

    }




}