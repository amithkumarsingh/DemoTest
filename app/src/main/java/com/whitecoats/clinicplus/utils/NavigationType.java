package com.whitecoats.clinicplus.utils;

public enum NavigationType {
    ServiceSetup(101),PaymentSetup(102),AddPatient(103),Appointments(104)
    ,BookedTrainings(105),UpcomingTrainings(106),SupportScreen(107);
    int navigationValue;

    private NavigationType(int mValue) {
        this.navigationValue = mValue;
    }

    public int getNavigationValue() {
        return navigationValue;
    }
}
