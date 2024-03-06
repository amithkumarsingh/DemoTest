package com.whitecoats.clinicplus.trainingschedule;

public class upcomingTrainingScheduleListModel {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public int getTrainingScheduleId() {
        return trainingScheduleId;
    }

    public void setTrainingScheduleId(int trainingScheduleId) {
        this.trainingScheduleId = trainingScheduleId;
    }

    private int trainingScheduleId;
    private String trainingTitle;
    private String trainingDescription;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;

    public String getStartTrainingDate() {
        return startTrainingDate;
    }

    public void setStartTrainingDate(String startTrainingDate) {
        this.startTrainingDate = startTrainingDate;
    }

    private String startTrainingDate;

    public String getStartTrainingSlot() {
        return startTrainingSlot;
    }

    public void setStartTrainingSlot(String startTrainingSlot) {
        this.startTrainingSlot = startTrainingSlot;
    }

    public String getEndTrainingSlot() {
        return endTrainingSlot;
    }

    public void setEndTrainingSlot(String endTrainingSlot) {
        this.endTrainingSlot = endTrainingSlot;
    }

    private String startTrainingSlot;
    private String endTrainingSlot;

    public String getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(String trainingDate) {
        this.trainingDate = trainingDate;
    }

    private String trainingDate;

    public int getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(int groupNo) {
        this.groupNo = groupNo;
    }

    private int groupNo;

    public String getTrainingTitle() {
        return trainingTitle;
    }

    public void setTrainingTitle(String trainingTitle) {
        this.trainingTitle = trainingTitle;
    }

    public String getTrainingDescription() {
        return trainingDescription;
    }

    public void setTrainingDescription(String trainingDescription) {
        this.trainingDescription = trainingDescription;
    }




}
