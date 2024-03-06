package com.whitecoats.clinicplus.utils

import android.util.Log
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.apis.ApiUrls
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {
        fun getInstance(): Retrofit {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MyClinicGlobalClass.productionURL)
                .client(makeOkHttpclient())
                .build()
        }

        private fun makeOkHttpclient(): OkHttpClient {
            return OkHttpClient().newBuilder()
                .addInterceptor(HeaderInterceptor())
                .build()
        }
    }

    class HeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response = chain.run {
            Log.d("authToken ","authToken "+ApiUrls.loginToken)
            proceed(
                request()
                    .newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("App-Origin", ApiUrls.appOrigin)
                    .addHeader("Authorization", "Bearer " + ApiUrls.loginToken)
                    .build()
            )
        }
    }
}