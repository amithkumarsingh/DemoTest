package com.whitecoats.clinicplus.models;

import com.whitecoats.clinicplus.constants.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class PatientModel {

    //<<<<<<< HEAD
    private PatientDetails patientDetails;
    private int status;
    private String response;
    //=======
    private String name;
    private String email;
    private String phone;
    private String blood_group;
    private String age;
    private String height;

    private String age_type;
    private int id, gender;
    private  String generalid;

    public String getGeneralID() {
        return generalid;
    }

    public void setGeneralID(String generalid) {
        this.generalid = generalid;
    }

    public PatientModel() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setEmail(String email) {
        this.email = email;
    }
//>>>>>>> new-architecture

    public PatientDetails getPatientDetails() {
        return patientDetails;
    }

    //<<<<<<< HEAD
    public int getStatus() {
        return status;
    }

    public String getResponse() {
        return response;
    }

    //=======
    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
//>>>>>>> new-architecture
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPatientDetails(PatientDetails patientDetails) {
        this.patientDetails = patientDetails;
    }

    //<<<<<<< HEAD
    public void setResponse(String response) {
        this.response = response;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    //=======
    public int getGender() {
        return gender;
    }

    public String getAge() {
        return age;
//>>>>>>> new-architecture
    }

    public String getAge_type() {
        return age_type;
    }

    public void setAge_type(String age_type) {
        this.age_type = age_type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PatientModel{");
        sb.append("patientDetails=").append(patientDetails);
        sb.append(", status=").append(status);
        sb.append(", response='").append(response).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static class PatientDetails {
        private int gender, id, has_profile, active;
        private String fname, email, phone, blood_group, dob, height, general_id, age,age_type;

        //<<<<<<< HEAD
        public void setFname(String fname) {
            this.fname = fname;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public void setAge_type(String age_type) {
            this.age_type = age_type;
        }


        public void setEmail(String email) {
            this.email = email;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public void setBlood_group(String blood_group) {
            this.blood_group = blood_group;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public void setGeneral_id(String general_id) {
            this.general_id = general_id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setActive(int active) {
            this.active = active;
        }

        public void setHas_profile(int has_profile) {
            this.has_profile = has_profile;
        }

        public String getFname() {
            return fname;
        }

        public String getBlood_group() {
            return blood_group;
        }

        public int getGender() {
            return gender;
        }

        public String getHeight() {
            return height;
        }

        public String getDob() {
            return dob;
        }

        public String getGeneral_id() {
            return general_id;
        }

        public String getAge() {
            return age;
        }

        public String getAge_type() {
            return age_type;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public int getId() {
            return id;
        }

        public int getActive() {
            return active;
        }

        public int getHas_profile() {
            return has_profile;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("PatientDetails{");
            sb.append("gender=").append(gender);
            sb.append(", id=").append(id);
            sb.append(", has_profile=").append(has_profile);
            sb.append(", active=").append(active);
            sb.append(", fname='").append(fname).append('\'');
            sb.append(", email='").append(email).append('\'');
            sb.append(", phone='").append(phone).append('\'');
            sb.append(", blood_group='").append(blood_group).append('\'');
            sb.append(", dob='").append(dob).append('\'');
            sb.append(", height='").append(height).append('\'');
            sb.append(", general_id='").append(general_id).append('\'');
            sb.append(", age='").append(age).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    //=======
    public String getBlood_group() {
        return blood_group;
    }

    public String getHeight() {
        return height;
//>>>>>>> new-architecture
    }
}
