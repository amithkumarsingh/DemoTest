package com.whitecoats.model;

import java.util.ArrayList;

public class SettingScheduleModel {

    String daysName;
    String slotOneStart;
    String slotOneEnd;
    String slotTwoStart;
    String slotTwoEnd;


    public String getSlotOneStart() {
        return slotOneStart;
    }

    public void setSlotOneStart(String slotOneStart) {
        this.slotOneStart = slotOneStart;
    }

    public String getSlotOneEnd() {
        return slotOneEnd;
    }

    public void setSlotOneEnd(String slotOneEnd) {
        this.slotOneEnd = slotOneEnd;
    }

    public String getSlotTwoStart() {
        return slotTwoStart;
    }

    public void setSlotTwoStart(String slotTwoStart) {
        this.slotTwoStart = slotTwoStart;
    }

    public String getSlotTwoEnd() {
        return slotTwoEnd;
    }

    public void setSlotTwoEnd(String slotTwoEnd) {
        this.slotTwoEnd = slotTwoEnd;
    }

    ArrayList<String> mondayList = new ArrayList<String>();
    ;

    public ArrayList<String> getMondayList() {
        return mondayList;
    }

    public void setMondayList(ArrayList<String> mondayList) {
        this.mondayList = mondayList;
    }

    public String getDaysName() {
        return daysName;
    }

    public void setDaysName(String daysName) {
        this.daysName = daysName;
    }


}
