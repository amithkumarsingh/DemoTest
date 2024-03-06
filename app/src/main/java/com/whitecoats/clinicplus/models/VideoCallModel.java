package com.whitecoats.clinicplus.models;

import java.util.List;

public class VideoCallModel {

    private String oToken, key;
    private int vjhId;
    private User user;
    private List<App> app;
    private int status;
    private String response;

    public String getoToken() {
        return oToken;
    }

    public List<App> getApp() {
        return app;
    }

    public int getVjhId() {
        return vjhId;
    }

    public String getKey() {
        return key;
    }

    public User getUser() {
        return user;
    }

    public String getResponse() {
        return response;
    }

    public int getStatus() {
        return status;
    }

    public void setApp(List<App> app) {
        this.app = app;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setoToken(String oToken) {
        this.oToken = oToken;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setVjhId(int vjhId) {
        this.vjhId = vjhId;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VideoCallModel{");
        sb.append("oToken='").append(oToken).append('\'');
        sb.append(", key='").append(key).append('\'');
        sb.append(", vjhId=").append(vjhId);
        sb.append(", user=").append(user);
        sb.append(", app=").append(app);
        sb.append(", status=").append(status);
        sb.append(", response='").append(response).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static class User {
        private int id;
        private String fname;

        public int getId() {
            return id;
        }

        public String getFname() {
            return fname;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", fname='" + fname + '\'' +
                    '}';
        }
    }

    public static class App {
        private Appointments appointments;

        public Appointments getAppointments() {
            return appointments;
        }

        public void setAppointments(Appointments appointments) {
            this.appointments = appointments;
        }

        @Override
        public String toString() {
            return "App{" +
                    "appointments=" + appointments +
                    '}';
        }
    }

    public static class Appointments {
        private String opentok_session_id;

        public String getOpentok_session_id() {
            return opentok_session_id;
        }

        public void setOpentok_session_id(String opentok_session_id) {
            this.opentok_session_id = opentok_session_id;
        }

        @Override
        public String toString() {
            return "Appointments{" +
                    "opentok_session_id='" + opentok_session_id + '\'' +
                    '}';
        }
    }

}
