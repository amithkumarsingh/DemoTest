package com.whitecoats.clinicplus.models;

public class TrainingScheduleModel {
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopicDate() {
        return topicDate;
    }

    public void setTopicDate(String topicDate) {
        this.topicDate = topicDate;
    }

    private String topic;
    private String topicDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTraingScheduleId() {
        return traingScheduleId;
    }

    public void setTraingScheduleId(int traingScheduleId) {
        this.traingScheduleId = traingScheduleId;
    }


    private int id;
    private int traingScheduleId;

    public String getTrainingDescription() {
        return trainingDescription;
    }

    public void setTrainingDescription(String trainingDescription) {
        this.trainingDescription = trainingDescription;
    }

    private String trainingDescription;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;

}
