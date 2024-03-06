package com.whitecoats.clinicplus;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.whitecoats.adapter.RecordsListAdapter;
import com.whitecoats.model.RecordsModel;

public class CreateNoteActivity extends AppCompatActivity {

    private Button proceed, createProceed, step2Proceed;
    private ScrollView step1Layout, step2Layout;
    private Toolbar toolbar;
    private int previousStep = 0;
    private List<RecordsModel> recordsModelList;
    private RecyclerView recordList;
    private RecordsListAdapter recordsListAdapter;
    private RelativeLayout writtenNote, step3Layout, caseDateLayout, caseTimeLayout;
    private LinearLayout recordListLayout;
    private ImageView arrowIcon;
    private TextView caseDate, caseTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        toolbar =  findViewById(R.id.createNoteToolBar);
        toolbar.setTitle("Step One");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        proceed = findViewById(R.id.createNoteProceed);
        createProceed = findViewById(R.id.createNoteCreateProceed);
        step1Layout = findViewById(R.id.createNoteStep1Layout);
        step2Layout = findViewById(R.id.createNoteStep2Layout);
        recordList = findViewById(R.id.createNoteNoteRView);
        writtenNote = findViewById(R.id.createNoteNotesLayout);
        recordListLayout = findViewById(R.id.createNoteNoteListLayout);
        arrowIcon = findViewById(R.id.createNoteNotesArrow);
        step2Proceed = findViewById(R.id.createNoteStep2Proceed);
        step3Layout = findViewById(R.id.createNoteStep3Layout);
        caseDateLayout = findViewById(R.id.createNoteCaseDateLayout);
        caseTimeLayout = findViewById(R.id.createNoteCaseTimeLayout);
        caseDate = findViewById(R.id.createNoteCaseDate);
        caseTime = findViewById(R.id.createNoteCaseTime);
        recordsModelList = new ArrayList<>();
        recordsListAdapter = new RecordsListAdapter(recordsModelList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recordList.setLayoutManager(mLayoutManager);
        recordList.setItemAnimator(new DefaultItemAnimator());
        recordList.setAdapter(recordsListAdapter);

        //getting current date and setting it
        Calendar c = Calendar.getInstance();
        caseDate.setText(c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR));

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step2Layout.setVisibility(View.GONE);
                step1Layout.setVisibility(View.GONE);
                step3Layout.setVisibility(View.VISIBLE);
                toolbar.setTitle("Step Three");
                previousStep = 1;
            }
        });

        createProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step1Layout.setVisibility(View.GONE);
                step3Layout.setVisibility(View.GONE);
                step2Layout.setVisibility(View.VISIBLE);
                toolbar.setTitle("Step Two");
                previousStep = 1;
            }
        });

        step2Proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step2Layout.setVisibility(View.GONE);
                step1Layout.setVisibility(View.GONE);
                step3Layout.setVisibility(View.VISIBLE);
                toolbar.setTitle("Step Three");
                previousStep = 2;
            }
        });

        writtenNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recordListLayout.getVisibility() == View.VISIBLE) {
                    recordListLayout.setVisibility(View.GONE);
                    arrowIcon.setImageDrawable(getDrawable(R.drawable.ic_arrow_right));
                } else {
                    recordListLayout.setVisibility(View.VISIBLE);
                    arrowIcon.setImageDrawable(getDrawable(R.drawable.ic_arrow_down));
                }
            }
        });

        caseDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] date = caseDate.getText().toString().split("/");
                openDatePicker(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]));
            }
        });

        caseTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] time = caseTime.getText().toString().split(":");
                openTimePicker(Integer.parseInt(time[0]), Integer.parseInt(time[1]));
            }
        });

        dummyData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            if(previousStep == 2) {
                step2Layout.setVisibility(View.VISIBLE);
                step3Layout.setVisibility(View.GONE);
                step1Layout.setVisibility(View.GONE);
                toolbar.setTitle("Step Two");
                previousStep = 1;
            } else if(previousStep == 1) {
                step1Layout.setVisibility(View.VISIBLE);
                step2Layout.setVisibility(View.GONE);
                step3Layout.setVisibility(View.GONE);
                toolbar.setTitle("Step One");
                previousStep = 0;
            } else {
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(previousStep == 2) {
            step2Layout.setVisibility(View.VISIBLE);
            step3Layout.setVisibility(View.GONE);
            step1Layout.setVisibility(View.GONE);
            toolbar.setTitle("Step Two");
            previousStep = 1;
        } else if(previousStep == 1) {
            step1Layout.setVisibility(View.VISIBLE);
            step2Layout.setVisibility(View.GONE);
            step3Layout.setVisibility(View.GONE);
            toolbar.setTitle("Step One");
            previousStep = 0;
        } else {
            finish();
        }
    }


    private void dummyData() {

        RecordsModel temp = new RecordsModel();
        temp.setFileUrl("Hello");
        recordsModelList.add(temp);

        temp = new RecordsModel();
        temp.setFileUrl("Hello");
        recordsModelList.add(temp);

        temp = new RecordsModel();
        temp.setFileUrl("Hello");
        recordsModelList.add(temp);

        recordsListAdapter.notifyDataSetChanged();

    }

    private void openDatePicker(int day, int month, int year) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        caseDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void openTimePicker(int hour, int minute) {
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        caseTime.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

}
