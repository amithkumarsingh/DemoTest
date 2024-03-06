package com.whitecoats.clinicplus.utils;

import com.whitecoats.clinicplus.MyClinicGlobalClass;
import com.whitecoats.clinicplus.apis.ApiUrls;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApiClient {
    private static Retrofit retrofit;
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(MyClinicGlobalClass.productionURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
