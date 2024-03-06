package com.whitecoats.model;

public class CaseChannelModel {

    private int caseId;
    private int patientId;
    private int episodeId, ownerId;
    private int status, taskCount, recordsCount, messageCounts;
    private String caseChannelName;
    private String createdAt;
    private String ownerName, patientName;

    //task variable
    private String taskName, taskAssignedTo, taskDueOn;
    private int taskStatus, taskId;

    //post discussion
    private String postMessage, postSenderName, postDate;
    private int postSenderId;

    public int getCaseId() {
        return caseId;
    }

    public String getCaseChannelName() {
        return caseChannelName;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    public int getPatientId() {
        return patientId;
    }

    public int getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getMessageCounts() {
        return messageCounts;
    }

    public int getRecordsCount() {
        return recordsCount;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getPostMessage() {
        return postMessage;
    }

    public String getPostDate() {
        return postDate;
    }

    public int getPostSenderId() {
        return postSenderId;
    }

    public String getPostSenderName() {
        return postSenderName;
    }

    public String getTaskAssignedTo() {
        return taskAssignedTo;
    }

    public String getTaskDueOn() {
        return taskDueOn;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setCaseChannelName(String caseChannelName) {
        this.caseChannelName = caseChannelName;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessageCounts(int messageCounts) {
        this.messageCounts = messageCounts;
    }

    public void setRecordsCount(int recordsCount) {
        this.recordsCount = recordsCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public void setPostMessage(String postMessage) {
        this.postMessage = postMessage;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public void setPostSenderId(int postSenderId) {
        this.postSenderId = postSenderId;
    }

    public void setPostSenderName(String postSenderName) {
        this.postSenderName = postSenderName;
    }

    public void setTaskAssignedTo(String taskAssignedTo) {
        this.taskAssignedTo = taskAssignedTo;
    }

    public void setTaskDueOn(String taskDueOn) {
        this.taskDueOn = taskDueOn;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
