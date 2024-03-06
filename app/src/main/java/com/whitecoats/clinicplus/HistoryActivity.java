package com.whitecoats.clinicplus;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.whitecoats.adapter.HistoryAdapter;
import com.whitecoats.model.HistoryModel;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView historyRecycleView;
    private List<HistoryModel> historyModelList;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //getting the toolbar
//        Toolbar toolbar =  findViewById(R.id.historyToolbar);
//        toolbar.setTitle("History");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
//        upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        historyRecycleView = findViewById(R.id.historyRecycleView);
        historyModelList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(historyModelList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        historyRecycleView.setLayoutManager(mLayoutManager);
        historyRecycleView.setItemAnimator(new DefaultItemAnimator());
        historyRecycleView.setAdapter(historyAdapter);

        dummyData();
    }

    private void dummyData() {
        HistoryModel temp = new HistoryModel();
        temp.setDetailText("Created Note");
        temp.setType("note");
        historyModelList.add(temp);

        temp = new HistoryModel();
        temp.setDetailText("Created Note");
        temp.setType("note");
        historyModelList.add(temp);

        temp = new HistoryModel();
        temp.setDetailText("Booked Appt for 12:30 on 2 Jan, 2019");
        temp.setType("book");
        historyModelList.add(temp);

        temp = new HistoryModel();
        temp.setDetailText("Created Note");
        temp.setType("note");
        historyModelList.add(temp);

        temp = new HistoryModel();
        temp.setDetailText("Patient Shared Record");
        temp.setType("record");
        historyModelList.add(temp);

        historyAdapter.notifyDataSetChanged();
    }
}
