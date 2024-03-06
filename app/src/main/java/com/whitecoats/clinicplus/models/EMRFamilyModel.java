package com.whitecoats.clinicplus.models;

import java.util.List;

public class EMRFamilyModel {

    private String relative_name;

    public String getRelative_age_str() {
        return relative_age_str;
    }

    public void setRelative_age_str(String relative_age_str) {
        this.relative_age_str = relative_age_str;
    }

    private String relative_age_str;
    private int id, patient_profile_id,relative_age;


    private Relation relation;
    private List<Problems> problems;

    public String getAge_type() {
        return age_type;
    }

    public void setAge_type(String age_type) {
        this.age_type = age_type;
    }

    private String age_type;

    public void setId(int id) {
        this.id = id;
    }

    public void setPatient_profile_id(int patient_profile_id) {
        this.patient_profile_id = patient_profile_id;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public void setRelative_age(int relative_age) {
        this.relative_age = relative_age;
    }

    public void setRelative_name(String relative_name) {
        this.relative_name = relative_name;
    }

    public void setProblems(List<Problems> problems) {
        this.problems = problems;
    }

    public int getId() {
        return id;
    }

    public int getPatient_profile_id() {
        return patient_profile_id;
    }

    public int getRelative_age() {
        return relative_age;
    }

    public String getRelative_name() {
        return relative_name;
    }

    public Relation getRelation() {
        return relation;
    }

    public List<Problems> getProblems() {
        return problems;
    }

    @Override
    public String toString() {
        return "EMRFamilyModel{" +
                "relative_name='" + relative_name + '\'' +
                ", id=" + id +
                ", patient_profile_id=" + patient_profile_id +
                ", relative_age=" + relative_age +
                ", relative_age_str=" + relative_age_str +
                ", age_type=" + age_type +
                ", relation=" + relation +
                ", problems=" + problems +
                '}';
    }

    public static class Relation {
        private int id;
        private String relation_name;

        public void setId(int id) {
            this.id = id;
        }

        public void setRelation_name(String relation_name) {
            this.relation_name = relation_name;
        }

        public int getId() {
            return id;
        }

        public String getRelation_name() {
            return relation_name;
        }

        @Override
        public String toString() {
            return "Relation{" +
                    "id=" + id +
                    ", relation_name='" + relation_name + '\'' +
                    '}';
        }
    }

    public static class Problems {
        private int id;
        private Problem problem;

        public void setId(int id) {
            this.id = id;
        }

        public void setProblem(Problem problem) {
            this.problem = problem;
        }

        public int getId() {
            return id;
        }

        public Problem getProblem() {
            return problem;
        }

        @Override
        public String toString() {
            return "Problems{" +
                    "id=" + id +
                    ", problem=" + problem +
                    '}';
        }

        public static class Problem {
            private int id;
            private String condition;

            public void setId(int id) {
                this.id = id;
            }

            public void setCondition(String condition) {
                this.condition = condition;
            }

            public int getId() {
                return id;
            }

            public String getCondition() {
                return condition;
            }

            @Override
            public String toString() {
                return "Problem{" +
                        "id=" + id +
                        ", condition='" + condition + '\'' +
                        '}';
            }
        }
    }
}