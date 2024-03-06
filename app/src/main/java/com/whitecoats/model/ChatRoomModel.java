package com.whitecoats.model;

public class ChatRoomModel {

    String chatMsg, chatTime, chatReadStatus;
    int type;


    public String getChatMsg() {
        return chatMsg;
    }

    public String getChatReadStatus() {
        return chatReadStatus;
    }

    public String getChatTime() {
        return chatTime;
    }

    public int getType() {
        return type;
    }

    public void setChatMsg(String chatMsg) {
        this.chatMsg = chatMsg;
    }

    public void setChatReadStatus(String chatReadStatus) {
        this.chatReadStatus = chatReadStatus;
    }

    public void setChatTime(String chatTime) {
        this.chatTime = chatTime;
    }

    public void setType(int type) {
        this.type = type;
    }
}
