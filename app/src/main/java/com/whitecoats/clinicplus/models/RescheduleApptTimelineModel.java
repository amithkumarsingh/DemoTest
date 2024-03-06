package com.whitecoats.clinicplus.models;

public class RescheduleApptTimelineModel  {
    public String getAppt_time() {
        return appt_time;
    }

    public void setAppt_time(String appt_time) {
        this.appt_time = appt_time;
    }

    public String getBooked_by() {
        return booked_by;
    }

    public void setBooked_by(String booked_by) {
        this.booked_by = booked_by;
    }

    String appt_time;
    String booked_by;
}
